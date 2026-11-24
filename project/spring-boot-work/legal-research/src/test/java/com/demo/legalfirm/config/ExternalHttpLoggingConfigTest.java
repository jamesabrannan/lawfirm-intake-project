package com.demo.legalfirm.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.logging.ExternalHttpLog;

import okhttp3.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.http.okhttp.SpringAiOpenAiHttpClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Verifies external HTTP metadata logging excludes secrets and payloads and preserves responses.
 *
 * @file ExternalHttpLoggingConfigTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class ExternalHttpLoggingConfigTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ExternalHttpLoggingConfigTest.class);

    @Test
    void recordsStatusHeadersAndTimingWithoutPayloadOrCredentials() throws IOException {
        LOGGER.trace("recordsStatusHeadersAndTimingWithoutPayloadOrCredentials entered");
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ExternalHttpLog.class);
        var appender =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        var builder = SpringAiOpenAiHttpClient.builder();
        new ExternalHttpLoggingConfig().openAiHttpLoggingCustomizer().customize(builder);
        try (var client = builder.build()) {
            var interceptors = client.getOkHttpClient().interceptors();
            var interceptor = interceptors.get(interceptors.size() - 1);
            var request =
                    new Request.Builder()
                            .url("https://api.openai.com/v1/chat/completions?secret=QUERY_SECRET")
                            .header("Authorization", "Bearer TOKEN_SECRET")
                            .header("X-Stainless-Retry-Count", "1")
                            .post(
                                    RequestBody.create(
                                            "REQUEST_BODY_SECRET",
                                            MediaType.get("application/json")))
                            .build();
            var response =
                    new Response.Builder()
                            .request(request)
                            .protocol(Protocol.HTTP_1_1)
                            .code(429)
                            .message("Too Many Requests")
                            .header("Retry-After", "13")
                            .header("X-Request-Id", "request-123")
                            .header("Set-Cookie", "COOKIE_SECRET")
                            .body(
                                    ResponseBody.create(
                                            "RESPONSE_BODY_SECRET",
                                            MediaType.get("application/json")))
                            .build();
            Interceptor.Chain chain = mock(Interceptor.Chain.class);
            when(chain.request()).thenReturn(request);
            when(chain.proceed(request)).thenReturn(response);
            assertThat(interceptor.intercept(chain)).isSameAs(response);
            assertThat(response.body().string()).isEqualTo("RESPONSE_BODY_SECRET");
            String text =
                    appender.list.stream()
                            .map(event -> event.getFormattedMessage())
                            .collect(java.util.stream.Collectors.joining("\n"));
            assertThat(text)
                    .contains(
                            "External HTTP request",
                            "method=POST",
                            "https://api.openai.com/v1/chat/completions",
                            "x-stainless-retry-count=1",
                            "status=429",
                            "retry-after=13",
                            "x-request-id=request-123",
                            "elapsedToHeadersMs=");
            assertThat(text)
                    .doesNotContain(
                            "QUERY_SECRET",
                            "TOKEN_SECRET",
                            "COOKIE_SECRET",
                            "REQUEST_BODY_SECRET",
                            "RESPONSE_BODY_SECRET");
            LOGGER.trace("recordsStatusHeadersAndTimingWithoutPayloadOrCredentials completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void recordsNetworkFailuresWithoutLoggingExceptionMessages() throws IOException {
        LOGGER.trace("recordsNetworkFailuresWithoutLoggingExceptionMessages entered");
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ExternalHttpLog.class);
        var appender =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        var builder = SpringAiOpenAiHttpClient.builder();
        new ExternalHttpLoggingConfig().openAiHttpLoggingCustomizer().customize(builder);
        try (var client = builder.build()) {
            var interceptors = client.getOkHttpClient().interceptors();
            var request =
                    new Request.Builder().url("https://api.openai.com/v1/chat/completions").build();
            Interceptor.Chain chain = mock(Interceptor.Chain.class);
            when(chain.request()).thenReturn(request);
            IOException failure = new java.net.SocketTimeoutException("SECRET_EXCEPTION_MESSAGE");
            when(chain.proceed(request)).thenThrow(failure);
            assertThatThrownBy(() -> interceptors.get(interceptors.size() - 1).intercept(chain))
                    .isSameAs(failure);
            String text =
                    appender.list.stream()
                            .map(event -> event.getFormattedMessage())
                            .collect(java.util.stream.Collectors.joining("\n"));
            assertThat(text)
                    .contains("External HTTP failure", "SocketTimeoutException", "elapsedMs=")
                    .doesNotContain("SECRET_EXCEPTION_MESSAGE");
            LOGGER.trace("recordsNetworkFailuresWithoutLoggingExceptionMessages completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void redactsUrlAndUnknownHeadersAndSanitizesControlCharacters() {
        LOGGER.trace("redactsUrlAndUnknownHeadersAndSanitizesControlCharacters entered");
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ExternalHttpLog.class);
        var appender =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        try {
            ExternalHttpLog.request(
                    "CourtListener",
                    "GET",
                    URI.create(
                            "https://USER_SECRET:PASS_SECRET@example.com/search/?q=QUERY_SECRET#FRAGMENT_SECRET"),
                    Map.of(
                            "X-Unknown",
                            List.of("HEADER_SECRET"),
                            "Authorization",
                            List.of("TOKEN_SECRET"),
                            "X-Request-Id",
                            List.of("id\r\nFORGED")));
            String text =
                    appender.list.stream()
                            .map(event -> event.getFormattedMessage())
                            .collect(java.util.stream.Collectors.joining("\n"));
            assertThat(text)
                    .contains("https://example.com/search/", "id  FORGED")
                    .doesNotContain(
                            "USER_SECRET",
                            "PASS_SECRET",
                            "QUERY_SECRET",
                            "FRAGMENT_SECRET",
                            "HEADER_SECRET",
                            "TOKEN_SECRET",
                            "id\r\nFORGED");
            LOGGER.trace("redactsUrlAndUnknownHeadersAndSanitizesControlCharacters completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
}
