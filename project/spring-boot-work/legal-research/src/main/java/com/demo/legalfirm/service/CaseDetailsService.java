package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.repository.CaseDetailsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides requested case details to the later case-input assembly stage.
 *
 * @file CaseDetailsService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class CaseDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDetailsService.class);

    private final CaseDetailsRepository repository;

    public CaseDetailsService(CaseDetailsRepository repository) {
        LOGGER.trace("CaseDetailsService(repository={}) entered", LogSummary.describe(repository));
        try {
            this.repository = repository;
            LOGGER.trace("CaseDetailsService completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "CaseDetailsService exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    /** Retrieves case details only; document extraction and AI analysis are separate stages. */
    public List<CaseDetails> findRequestedForStaff(String staffEmail) {
        LOGGER.debug(
                "findRequestedForStaff(staffEmail={}) entered", LogSummary.describe(staffEmail));
        try {

            var loggedResult = repository.findRequestedForStaff(staffEmail);
            LOGGER.debug("findRequestedForStaff returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "findRequestedForStaff exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
