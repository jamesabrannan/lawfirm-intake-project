package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.memorandums.MemorandumPreparationResponse;
import com.demo.legalfirm.model.memorandums.MemorandumPreparationResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Selects current-user work and synchronously invokes preparation for each memorandum.
 *
 * @file MemorandumProcessingService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class MemorandumProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumProcessingService.class);

    private final MemorandumWorkService workService;
    private final MemorandumPreparationService preparationService;

    public MemorandumProcessingService(
            MemorandumWorkService workService, MemorandumPreparationService preparationService) {
        LOGGER.trace(
                "MemorandumProcessingService(workService={}, preparationService={}) entered",
                LogSummary.describe(workService),
                LogSummary.describe(preparationService));
        try {
            this.workService = workService;
            this.preparationService = preparationService;
            LOGGER.trace("MemorandumProcessingService completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumProcessingService exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    /**
     * Demo simplification: process on the request thread, with no background queue. Return the
     * summary only after every selected memorandum has been prepared. An exception stops the loop
     * and propagates rather than reporting false success.
     */
    @PreAuthorize("isAuthenticated() and hasAuthority('MEMORANDUM_PREPARER')")
    public MemorandumPreparationResponse prepareForCurrentStaff() {
        LOGGER.debug("prepareForCurrentStaff() entered");
        try {
            List<CaseDetails> cases = workService.findRequestedForCurrentStaff();
            LOGGER.info("Selected {} requested memorandums for current staff", cases.size());
            List<MemorandumPreparationResult> results = new ArrayList<>();
            for (CaseDetails details : cases) {
                LOGGER.info(
                        "Starting memorandum preparation; memorandumId={}",
                        details.getMemorandumId());
                preparationService.prepare(details);
                LOGGER.info(
                        "Completed memorandum preparation; memorandumId={}",
                        details.getMemorandumId());
                results.add(
                        new MemorandumPreparationResult(
                                details.getMemorandumId(),
                                details.getIntake().getIntakeId(),
                                "created"));
            }

            LOGGER.info("Preparation request completed; createdCount={}", results.size());
            var loggedResult =
                    new MemorandumPreparationResponse(
                            results.isEmpty() ? "no_work" : "completed", results);
            LOGGER.debug("prepareForCurrentStaff returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            if (loggingFailure instanceof ResponseStatusException statusFailure
                    && statusFailure.getStatusCode().value() == 501) {
                LOGGER.warn("Preparation request stopped: preparation pipeline is not implemented");
            } else {
                LOGGER.error(
                        "Preparation request failed; type={}",
                        loggingFailure.getClass().getSimpleName());
            }
            throw loggingFailure;
        }
    }
}
