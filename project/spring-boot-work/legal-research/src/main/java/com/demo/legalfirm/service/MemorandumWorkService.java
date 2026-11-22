package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Selects requested memorandum work for the currently authenticated staff member.
 *
 * @file MemorandumWorkService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class MemorandumWorkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumWorkService.class);

    private final CaseDetailsService caseDetailsService;

    public MemorandumWorkService(CaseDetailsService caseDetailsService) {
        LOGGER.trace(
                "MemorandumWorkService(caseDetailsService={}) entered",
                LogSummary.describe(caseDetailsService));
        try {
            this.caseDetailsService = caseDetailsService;
            LOGGER.trace("MemorandumWorkService completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumWorkService exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    /**
     * Selects work using the authenticated identity, never caller-supplied credentials. Method
     * authorization requires MEMORANDUM_PREPARER; unauthorized callers are denied. The law clerk
     * remains preparer metadata.
     *
     * <p>Permission-dependent work eligibility is confined here. Case-detail retrieval, PDF
     * extraction and the later preparation workflow do not inspect security roles.
     */
    @PreAuthorize("isAuthenticated() and hasAuthority('MEMORANDUM_PREPARER')")
    public List<CaseDetails> findRequestedForCurrentStaff() {
        LOGGER.debug("findRequestedForCurrentStaff() entered");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            var loggedResult = caseDetailsService.findRequestedForStaff(authentication.getName());
            LOGGER.debug(
                    "findRequestedForCurrentStaff returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "findRequestedForCurrentStaff exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
