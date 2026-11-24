package com.demo.legalfirm.config;

import com.demo.legalfirm.logging.ExternalHttpLog;
import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds safe transport metadata logging to every Spring AI OpenAI HTTP attempt, including retries.
 *
 * @file ExternalHttpLoggingConfig.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Configuration
public class ExternalHttpLoggingConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalHttpLoggingConfig.class);

    @Bean
    public OpenAiHttpClientBuilderCustomizer openAiHttpLoggingCustomizer() {
        LOGGER.debug("openAiHttpLoggingCustomizer entered");
        OpenAiHttpClientBuilderCustomizer customizer =
                builder -> {
                    LOGGER.trace("customize(builder={}) entered", LogSummary.describe(builder));
                    builder.interceptor(
                            chain -> {
                                var request = chain.request();
                                long started = System.nanoTime();
                                String id =
                                        ExternalHttpLog.request(
                                                "OpenAI",
                                                request.method(),
                                                request.url().uri(),
                                                request.headers().toMultimap());
                                try {
                                    var response = chain.proceed(request);
                                    ExternalHttpLog.response(
                                            id,
                                            response.code(),
                                            response.headers().toMultimap(),
                                            started);
                                    return response;
                                } catch (java.io.IOException | RuntimeException failure) {
                                    ExternalHttpLog.failure(id, failure, started);
                                    throw failure;
                                }
                            });
                    LOGGER.trace("customize completed");
                };
        LOGGER.debug("openAiHttpLoggingCustomizer returning customizer");
        return customizer;
    }
}
