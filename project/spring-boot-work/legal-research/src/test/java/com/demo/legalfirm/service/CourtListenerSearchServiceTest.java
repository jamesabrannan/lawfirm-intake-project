package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import com.demo.legalfirm.model.research.courtlistener.CourtListenerSearchResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import tools.jackson.databind.json.JsonMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Verifies search requests, response mapping, result limits, and safe failure logging without live
 * API calls.
 *
 * @file CourtListenerSearchServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class CourtListenerSearchServiceTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CourtListenerSearchServiceTest.class);
    private static final String TOKEN = "test-token-not-a-real-secret";
    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private CourtListenerSearchService service;

    @Test
    void logsHttpMetadataForThrottlingWithoutQueryTokenOrBody() {
        LOGGER.trace("logsHttpMetadataForThrottlingWithoutQueryTokenOrBody entered");
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(com.demo.legalfirm.logging.ExternalHttpLog.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            server.expect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "58").header("X-Request-Id", "cl-request-123")
                    .header("Set-Cookie", "COOKIE_SECRET").contentType(MediaType.APPLICATION_JSON)
                    .body("{\"detail\":\"BODY_SECRET\"}"));
            assertThatThrownBy(() -> service.search("QUERY_SECRET")).isInstanceOf(RestClientResponseException.class);
            String text = appender.list.stream().map(ILoggingEvent::getFormattedMessage)
                    .collect(java.util.stream.Collectors.joining("\n"));
            assertThat(text).contains("provider=CourtListener", "method=GET", "status=429",
                    "retry-after=58", "x-request-id=cl-request-123", "elapsedToHeadersMs=")
                    .doesNotContain(TOKEN, "QUERY_SECRET", "BODY_SECRET", "COOKIE_SECRET");
            server.verify();
            LOGGER.trace("logsHttpMetadataForThrottlingWithoutQueryTokenOrBody completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @BeforeEach
    void setUp() {
        LOGGER.trace("setUp entered");
        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        service = new CourtListenerSearchService(builder, TOKEN, 2);
        LOGGER.trace("setUp completed");
    }

    @Test
    void searchesOpinionsWithEncodedQueryTokenAndHighlightsAndMapsBoundedResults() {
        LOGGER.trace(
                "searchesOpinionsWithEncodedQueryTokenAndHighlightsAndMapsBoundedResults entered");
        String query = "hypoglycemia & driving court:md";
        server.expect(
                        request -> {
                            assertThat(request.getURI().getScheme()).isEqualTo("https");
                            assertThat(request.getURI().getHost())
                                    .isEqualTo("www.courtlistener.com");
                            assertThat(request.getURI().getPath())
                                    .isEqualTo("/api/rest/v4/search/");
                            String rawQuery = request.getURI().getRawQuery();
                            assertThat(rawQuery).startsWith("type=o&highlight=on&q=");
                            assertThat(
                                            URLDecoder.decode(
                                                    rawQuery.substring(rawQuery.indexOf("q=") + 2),
                                                    StandardCharsets.UTF_8))
                                    .isEqualTo(query);
                            assertThat(rawQuery).contains("%26");
                        })
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Token " + TOKEN))
                .andExpect(header("Accept", "application/json"))
                .andRespond(
                        withSuccess(
                                """
{
  "count":3,
  "next":"https://unused.example/next-page",
  "previous":null,
  "results":[
    {
      "cluster_id":100,"caseName":"Example v. Sample",
      "citation":["123 Example 456"],"court":"Example Court","court_id":"md",
      "dateFiled":"2020-01-02","absolute_url":"/opinion/100/example/",
      "status":"Published","ignored_provider_field":"ignored",
      "opinions":[{"id":101,"type":"lead-opinion",
        "snippet":"Reported <mark>impairment</mark> discussed.","ignored_field":true}]
    },
    {
      "cluster_id":200,"caseName":"Second case","citation":[],
      "absolute_url":"/opinion/200/second/","opinions":[]
    },
    {"cluster_id":300,"caseName":"Outside limit","opinions":[]}
  ]
}
""",
                                MediaType.APPLICATION_JSON));

        List<CourtListenerSearchResult> results = service.search(query);

        assertThat(results).hasSize(2);
        CourtListenerSearchResult first = results.get(0);
        assertThat(first.getClusterId()).isEqualTo(100);
        assertThat(first.getCaseName()).isEqualTo("Example v. Sample");
        assertThat(first.getCitation()).containsExactly("123 Example 456");
        assertThat(first.getCourt()).isEqualTo("Example Court");
        assertThat(first.getCourtId()).isEqualTo("md");
        assertThat(first.getDateFiled()).isEqualTo("2020-01-02");
        assertThat(first.getAbsoluteUrl())
                .isEqualTo("https://www.courtlistener.com/opinion/100/example/");
        assertThat(first.getStatus()).isEqualTo("Published");
        assertThat(first.getOpinions().get(0).getId()).isEqualTo(101);
        assertThat(first.getOpinions().get(0).getType()).isEqualTo("lead-opinion");
        assertThat(first.getOpinions().get(0).getSnippet()).contains("<mark>impairment</mark>");
        JsonMapper mapper = JsonMapper.builder().build();
        assertThat(
                        mapper.readValue(
                                mapper.writeValueAsString(first), CourtListenerSearchResult.class))
                .usingRecursiveComparison()
                .isEqualTo(first);
        // No attempt is made to follow the provider's next-page URL.
        server.verify();
        LOGGER.trace(
                "searchesOpinionsWithEncodedQueryTokenAndHighlightsAndMapsBoundedResults"
                    + " completed");
    }

    @Test
    void noMatchesReturnAnEmptyCollection() {
        LOGGER.trace("noMatchesReturnAnEmptyCollection entered");
        server.expect(anything())
                .andRespond(
                        withSuccess(
                                "{\"count\":0,\"next\":null,\"previous\":null,\"results\":[]}",
                                MediaType.APPLICATION_JSON));

        assertThat(service.search("no matching case")).isEmpty();
        server.verify();
        LOGGER.trace("noMatchesReturnAnEmptyCollection completed");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 429, 500})
    void preservesHttpFailuresWithoutRetryingOrLoggingSecrets(int status) {
        LOGGER.trace(
                "preservesHttpFailuresWithoutRetryingOrLoggingSecrets(status={}) entered", status);
        ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger)
                        LoggerFactory.getLogger(CourtListenerSearchService.class);
        Level oldLevel = logger.getLevel();
        ListAppender<ILoggingEvent> capture = new ListAppender<>();
        capture.start();
        logger.setLevel(Level.TRACE);
        logger.addAppender(capture);
        try {
            CourtListenerSearchService client = new CourtListenerSearchService(builder, TOKEN, 2);
            server.expect(anything())
                    .andRespond(
                            withStatus(HttpStatus.valueOf(status))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body("{\"detail\":\"response-body-must-not-be-logged\"}")
                                    .header("Retry-After", "60"));

            assertThatThrownBy(() -> client.search("private-query-must-not-be-logged"))
                    .isInstanceOfSatisfying(
                            RestClientResponseException.class,
                            exception -> {
                                assertThat(exception.getStatusCode().value()).isEqualTo(status);
                                assertThat(exception.getResponseHeaders().getFirst("Retry-After"))
                                        .isEqualTo("60");
                            });
            server.verify();
            assertThat(capture.list)
                    .isNotEmpty()
                    .allSatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .doesNotContain(
                                                    TOKEN,
                                                    "private-query-must-not-be-logged",
                                                    "response-body-must-not-be-logged"));
            LOGGER.trace("preservesHttpFailuresWithoutRetryingOrLoggingSecrets completed");
        } finally {
            logger.detachAppender(capture);
            logger.setLevel(oldLevel);
            capture.stop();
        }
    }

    @Test
    void missingEnvironmentTokenFailsBeforeAnyHttpCall() {
        LOGGER.trace("missingEnvironmentTokenFailsBeforeAnyHttpCall entered");
        CourtListenerSearchService unconfigured = new CourtListenerSearchService(builder, "", 2);

        assertThatThrownBy(() -> unconfigured.search("driving"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("COURTLISTENER_API_KEY");
        server.verify();
        LOGGER.trace("missingEnvironmentTokenFailsBeforeAnyHttpCall completed");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rejectsBlankQueriesBeforeAnyHttpCall(String query) {
        LOGGER.trace("rejectsBlankQueriesBeforeAnyHttpCall entered; query redacted");
        assertThatThrownBy(() -> service.search(query))
                .isInstanceOf(IllegalArgumentException.class);
        server.verify();
        LOGGER.trace("rejectsBlankQueriesBeforeAnyHttpCall completed");
    }

    @Test
    void malformedSuccessResponseIsNotReportedAsNoMatches() {
        LOGGER.trace("malformedSuccessResponseIsNotReportedAsNoMatches entered");
        server.expect(anything())
                .andRespond(withSuccess("{\"count\":1}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.search("driving"))
                .isInstanceOf(IllegalStateException.class);
        server.verify();
        LOGGER.trace("malformedSuccessResponseIsNotReportedAsNoMatches completed");
    }
}
