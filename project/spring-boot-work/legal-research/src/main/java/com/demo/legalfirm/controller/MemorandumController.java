package com.demo.legalfirm.controller;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.memorandums.MemorandumPreparationResponse;
import com.demo.legalfirm.service.MemorandumProcessingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Starts synchronous memorandum preparation for the currently authenticated staff member.
 *
 * <p>Demo design: memorandum preparation runs synchronously for simplicity. The preparation
 * endpoint waits for the service to finish processing the selected memorandums before returning
 * a JSON completion summary. It does not queue background work or return the generated PDFs; PDF
 * retrieval belongs to a separate service. A successful response must describe completed work
 * rather than report asynchronous acceptance.
 *
 * <p>A memorandum is reported created only after its PDF is saved and the database completion
 * update succeeds. Earlier successful memorandums remain completed if a later preparation fails.
 *
 * @file MemorandumController.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@RestController
@RequestMapping("/api/memorandums")
public class MemorandumController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumController.class);

    private final MemorandumProcessingService processingService;

    public MemorandumController(MemorandumProcessingService processingService) {
        LOGGER.trace(
                "MemorandumController(processingService={}) entered",
                LogSummary.describe(processingService));
        try {
            this.processingService = processingService;
            LOGGER.trace("MemorandumController completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumController exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    /** Starts processing and returns a summary after synchronous completion. */
    @PostMapping("/prepare")
    public MemorandumPreparationResponse prepare() {
        LOGGER.info("Received synchronous memorandum preparation request");
        LOGGER.debug("prepare() entered");
        try {

            var loggedResult = processingService.prepareForCurrentStaff();
            LOGGER.debug("prepare returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "prepare exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
