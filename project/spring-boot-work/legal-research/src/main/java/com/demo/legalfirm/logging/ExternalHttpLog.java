package com.demo.legalfirm.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Logs correlated external HTTP metadata without payloads, query values or secret headers.
 *
 * @file ExternalHttpLog.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public final class ExternalHttpLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalHttpLog.class);
    private static final Set<String> ALLOWED_HEADERS =
            Set.of(
                    "accept",
                    "content-type",
                    "content-length",
                    "date",
                    "user-agent",
                    "retry-after",
                    "retry-after-ms",
                    "x-request-id",
                    "request-id",
                    "cf-ray",
                    "openai-version",
                    "openai-model",
                    "openai-processing-ms",
                    "x-stainless-retry-count",
                    "x-stainless-read-timeout",
                    "x-stainless-timeout",
                    "x-ratelimit-limit-requests",
                    "x-ratelimit-limit-tokens",
                    "x-ratelimit-remaining-requests",
                    "x-ratelimit-remaining-tokens",
                    "x-ratelimit-reset-requests",
                    "x-ratelimit-reset-tokens",
                    "ratelimit-limit",
                    "ratelimit-remaining",
                    "ratelimit-reset");

    private ExternalHttpLog() {
        LOGGER.trace("ExternalHttpLog constructor entered");
        LOGGER.trace("ExternalHttpLog constructor completed");
    }

    public static String request(
            String provider, String method, URI uri, Map<String, List<String>> headers) {
        LOGGER.trace("request entered; headers={}", headers.size());
        String id = UUID.randomUUID().toString();
        // Never include raw URI.toString(): credentials and queries may be embedded in it.
        String destination =
                uri.getScheme()
                        + "://"
                        + uri.getHost()
                        + (uri.getPort() < 0 ? "" : ":" + uri.getPort())
                        + uri.getRawPath();
        LOGGER.info(
                "External HTTP request; callId={}, provider={}, method={}, destination={},"
                    + " headers={}",
                id,
                safe(provider),
                safe(method),
                safe(destination),
                safeHeaders(headers));
        LOGGER.trace("request returning callId={}", id);
        return id;
    }

    public static void response(
            String id, int status, Map<String, List<String>> headers, long startedNanos) {
        LOGGER.trace(
                "response entered; callId={}, status={}, headers={}", id, status, headers.size());
        LOGGER.info(
                "External HTTP response; callId={}, status={}, elapsedToHeadersMs={}, headers={}",
                id,
                status,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos),
                safeHeaders(headers));
        LOGGER.trace("response completed; callId={}", id);
    }

    public static void failure(String id, Exception failure, long startedNanos) {
        LOGGER.trace("failure entered; callId={}, type={}", id, failure.getClass().getSimpleName());
        LOGGER.warn(
                "External HTTP failure; callId={}, elapsedMs={}, type={}",
                id,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos),
                failure.getClass().getSimpleName());
        LOGGER.trace("failure completed; callId={}", id);
    }

    private static Map<String, String> safeHeaders(Map<String, List<String>> headers) {
        LOGGER.trace("safeHeaders entered; headers={}", headers.size());
        Map<String, String> selected = new TreeMap<>();
        for (var entry : headers.entrySet()) {
            String name = entry.getKey().toLowerCase(Locale.ROOT);
            if (ALLOWED_HEADERS.contains(name)) {
                selected.put(name, safe(String.join(", ", entry.getValue())));
            }
        }
        LOGGER.trace("safeHeaders returning count={}", selected.size());
        return selected;
    }

    private static String safe(String value) {
        LOGGER.trace("safe entered; length={}", value == null ? 0 : value.length());
        String result = value == null ? "" : value.replaceAll("[\\p{Cntrl}]", " ");
        if (result.length() > 512) {
            result = result.substring(0, 512) + "[truncated]";
        }
        LOGGER.trace("safe returning length={}", result.length());
        return result;
    }
}
