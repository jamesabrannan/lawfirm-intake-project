package com.demo.legalfirm;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Spring Boot legal research application.
 *
 * @file LegalResearchApplication.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@SpringBootApplication
public class LegalResearchApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegalResearchApplication.class);

    public static void main(String[] args) {
        LOGGER.debug("main(args={}) entered", LogSummary.describe(args));
        try {
            SpringApplication.run(LegalResearchApplication.class, args);
            LOGGER.info("Legal research application started");
            LOGGER.debug("main completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "main exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
