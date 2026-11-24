package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.ExternalHttpLog;
import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.research.courtlistener.CourtListenerSearchResponse;
import com.demo.legalfirm.model.research.courtlistener.CourtListenerSearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches CourtListener opinions and returns a bounded set of preliminary case-law leads.
 *
 * @file CourtListenerSearchService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class CourtListenerSearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourtListenerSearchService.class);
    private static final String BASE_URL = "https://www.courtlistener.com";
    private final RestClient restClient;
    // Environment-backed secret holder. Never log this value or the Authorization header.
    private final String apiKey;
    private final int maxResults;
    private final long requestIntervalNanos;
    private long lastRequestStarted;
    private boolean requestStarted;

    @Autowired
    public CourtListenerSearchService(
            @Value("${courtlistener.api-key:}") String apiKey,
            @Value("${courtlistener.max-results:5}") int maxResults,
            @Value("${courtlistener.request-interval:13s}") Duration requestInterval) {
        this(defaultClientBuilder(), apiKey, maxResults, requestInterval);
        LOGGER.trace(
                "CourtListenerSearchService configured; token=[REDACTED], maxResults={}",
                maxResults);
    }

    // Builder injection lets tests bind a mock server without external requests or real
    // credentials.
    CourtListenerSearchService(RestClient.Builder builder, String apiKey, int maxResults) {
        this(builder, apiKey, maxResults, Duration.ZERO);
        LOGGER.trace("CourtListenerSearchService test constructor completed");
    }

    CourtListenerSearchService(
            RestClient.Builder builder, String apiKey, int maxResults, Duration requestInterval) {
        LOGGER.trace(
                "CourtListenerSearchService(builder={}, token=[REDACTED], maxResults={}) entered",
                LogSummary.describe(builder),
                maxResults);
        Assert.notNull(builder, "RestClient builder must not be null");
        Assert.isTrue(
                maxResults >= 1 && maxResults <= 10, "Demo result limit must be between 1 and 10");
        this.restClient =
                builder.baseUrl(BASE_URL)
                        .requestInterceptor(
                                (request, body, execution) -> {
                                    // Do not inspect body or consume response content for transport
                                    // logging.
                                    long started = System.nanoTime();
                                    String id =
                                            ExternalHttpLog.request(
                                                    "CourtListener",
                                                    request.getMethod().name(),
                                                    request.getURI(),
                                                    request.getHeaders().asMultiValueMap());
                                    try {
                                        var response = execution.execute(request, body);
                                        ExternalHttpLog.response(
                                                id,
                                                response.getStatusCode().value(),
                                                response.getHeaders().asMultiValueMap(),
                                                started);
                                        return response;
                                    } catch (java.io.IOException | RuntimeException failure) {
                                        ExternalHttpLog.failure(id, failure, started);
                                        throw failure;
                                    }
                                })
                        .build();
        this.apiKey = apiKey;
        this.maxResults = maxResults;
        Assert.notNull(requestInterval, "Request interval is required");
        Assert.isTrue(!requestInterval.isNegative(), "Request interval must not be negative");
        this.requestIntervalNanos = requestInterval.toNanos();
        LOGGER.trace("Request interval milliseconds={}", requestInterval.toMillis());
        LOGGER.trace("CourtListenerSearchService completed");
    }

    /**
     * Uses keyword opinion search (type=o), with highlighted snippets. One call makes one HTTP
     * request: pagination and automatic retries are deliberately omitted to limit demo quota
     * consumption. Only the configured number of first-page results are returned. HTTP errors,
     * including 429 throttling, propagate.
     *
     * <p>These are search-based research leads, not verified holdings or full opinions. Supply
     * research terms, not client names or full intake narratives.
     *
     * @param query a focused CourtListener query generated for a research topic
     * @return case metadata, source links, and snippets; an empty list for no matches
     */
    public synchronized List<CourtListenerSearchResult> search(String query) {
        LOGGER.debug("search(query={}) entered", LogSummary.describe(query));
        try {
            Assert.hasText(query, "Search query must not be blank");
            Assert.hasText(apiKey, "Set COURTLISTENER_API_KEY before searching CourtListener");
            awaitRequestSlot();
            LOGGER.info("Searching CourtListener opinions; resultLimit={}", maxResults);
            CourtListenerSearchResponse response =
                    restClient
                            .get()
                            .uri("/api/rest/v4/search/?type=o&highlight=on&q={query}", query)
                            .header("Authorization", "Token " + apiKey)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(CourtListenerSearchResponse.class);
            if (response == null || response.getResults() == null) {
                throw new IllegalStateException("CourtListener returned no search results payload");
            }

            List<CourtListenerSearchResult> results = new ArrayList<>();
            for (CourtListenerSearchResult result : response.getResults()) {
                if (results.size() == maxResults) {
                    break;
                }
                // CourtListener's absolute_url commonly contains a site-relative opinion path.
                if (result.getAbsoluteUrl() != null && !result.getAbsoluteUrl().isBlank()) {
                    result.setAbsoluteUrl(
                            URI.create(BASE_URL).resolve(result.getAbsoluteUrl()).toString());
                }
                results.add(result);
            }
            LOGGER.info(
                    "CourtListener search completed; matches={}, returned={}",
                    response.getCount(),
                    results.size());
            LOGGER.debug("search returning {}", LogSummary.describe(results));
            return results;
        } catch (RuntimeException failure) {
            // HTTP exception messages may include queries or response bodies; log only the type.
            LOGGER.warn("CourtListener search failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    // One application-wide client serializes calls across all plans and incoming requests.
    // This protects the minute rate for this JVM, not shared-account hourly/daily quotas.
    private void awaitRequestSlot() {
        LOGGER.trace("awaitRequestSlot entered");
        try {
            if (requestStarted) {
                long remaining = requestIntervalNanos - (System.nanoTime() - lastRequestStarted);
                while (remaining > 0) {
                    LOGGER.debug(
                            "Pacing CourtListener request; waitMillis={}", remaining / 1_000_000);
                    java.util.concurrent.TimeUnit.NANOSECONDS.sleep(remaining);
                    remaining = requestIntervalNanos - (System.nanoTime() - lastRequestStarted);
                }
            }
            lastRequestStarted = System.nanoTime();
            requestStarted = true;
            LOGGER.trace("awaitRequestSlot completed");
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            LOGGER.warn("CourtListener pacing interrupted");
            throw new IllegalStateException("CourtListener research interrupted", failure);
        }
    }

    private static RestClient.Builder defaultClientBuilder() {
        LOGGER.trace("defaultClientBuilder() entered");
        // Use Java's built-in HTTP client through Spring's adapter, avoiding an
        // additional HTTP-client dependency. Explicit connection (10s) and read
        // (30s) timeouts prevent a stalled CourtListener request from holding up
        // the synchronous demo indefinitely. This factory is a configuration
        // choice, not a requirement of CourtListener or RestClient.
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(client);
        factory.setReadTimeout(Duration.ofSeconds(30));
        RestClient.Builder builder = RestClient.builder().requestFactory(factory);
        LOGGER.trace("defaultClientBuilder returning {}", LogSummary.describe(builder));
        return builder;
    }
}
