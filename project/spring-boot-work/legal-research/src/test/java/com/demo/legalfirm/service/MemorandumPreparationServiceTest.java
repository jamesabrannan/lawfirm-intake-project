package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.demo.legalfirm.model.caseinputs.details.CaseDetails;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Verifies that the unfinished preparation pipeline explicitly reports not implemented.
 *
 * @file MemorandumPreparationServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class MemorandumPreparationServiceTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumPreparationServiceTest.class);

    @Test
    void placeholderCannotReportSuccessfulCreation() {
        LOGGER.trace("placeholderCannotReportSuccessfulCreation() entered");
        try {
            assertThatThrownBy(() -> new MemorandumPreparationService().prepare(new CaseDetails()))
                    .isInstanceOfSatisfying(
                            ResponseStatusException.class,
                            exception ->
                                    assertThat(exception.getStatusCode())
                                            .isEqualTo(HttpStatus.NOT_IMPLEMENTED));
            LOGGER.trace("placeholderCannotReportSuccessfulCreation completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "placeholderCannotReportSuccessfulCreation exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
