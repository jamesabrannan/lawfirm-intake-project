package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.details.IntakeInput;
import com.demo.legalfirm.model.memorandums.MemorandumPreparationResponse;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Verifies synchronous iteration, completion summaries, no-work handling, and preparation failures.
 *
 * @file MemorandumProcessingServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class MemorandumProcessingServiceTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumProcessingServiceTest.class);

    private final MemorandumWorkService workService = mock(MemorandumWorkService.class);
    private final MemorandumPreparationService preparationService =
            mock(MemorandumPreparationService.class);
    private final MemorandumProcessingService service =
            new MemorandumProcessingService(workService, preparationService);

    @Test
    void preparesEverySelectedMemorandumBeforeReturningAnIdentifierOnlySummary() {
        LOGGER.trace(
                "preparesEverySelectedMemorandumBeforeReturningAnIdentifierOnlySummary() entered");
        try {
            CaseDetails first = details(1, "I-204");
            CaseDetails second = details(2, "I-205");
            when(workService.findRequestedForCurrentStaff()).thenReturn(List.of(first, second));

            MemorandumPreparationResponse response = service.prepareForCurrentStaff();

            InOrder order = inOrder(workService, preparationService);
            order.verify(workService).findRequestedForCurrentStaff();
            order.verify(preparationService).prepare(first);
            order.verify(preparationService).prepare(second);
            assertThat(response.getStatus()).isEqualTo("completed");
            assertThat(response.getMemorandums())
                    .extracting(result -> result.getMemorandumId())
                    .containsExactly(1L, 2L);
            assertThat(response.getMemorandums())
                    .extracting(result -> result.getStatus())
                    .containsOnly("created");
            String json = JsonMapper.builder().build().writeValueAsString(response);
            assertThat(json)
                    .contains("\"memorandumId\"", "\"intakeId\"", "\"created\"")
                    .doesNotContain("\"client\"", "\"requestor\"", "\"summary\"", "\"documents\"");
            LOGGER.trace(
                    "preparesEverySelectedMemorandumBeforeReturningAnIdentifierOnlySummary"
                            + " completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "preparesEverySelectedMemorandumBeforeReturningAnIdentifierOnlySummary exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void noWorkDoesNotInvokePreparation() {
        LOGGER.trace("noWorkDoesNotInvokePreparation() entered");
        try {
            when(workService.findRequestedForCurrentStaff()).thenReturn(List.of());

            MemorandumPreparationResponse response = service.prepareForCurrentStaff();

            assertThat(response.getStatus()).isEqualTo("no_work");
            assertThat(response.getMemorandums()).isEmpty();
            verifyNoInteractions(preparationService);
            LOGGER.trace("noWorkDoesNotInvokePreparation completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "noWorkDoesNotInvokePreparation exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void stopsAndPropagatesFailureInsteadOfReturningSuccess() {
        LOGGER.trace("stopsAndPropagatesFailureInsteadOfReturningSuccess() entered");
        try {
            CaseDetails first = details(1, "I-204");
            CaseDetails second = details(2, "I-205");
            when(workService.findRequestedForCurrentStaff()).thenReturn(List.of(first, second));
            RuntimeException failure = new IllegalStateException("Preparation failed");
            doThrow(failure).when(preparationService).prepare(first);

            assertThatThrownBy(service::prepareForCurrentStaff).isSameAs(failure);
            verify(preparationService).prepare(first);
            verify(preparationService, never()).prepare(second);
            LOGGER.trace("stopsAndPropagatesFailureInsteadOfReturningSuccess completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "stopsAndPropagatesFailureInsteadOfReturningSuccess exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    private CaseDetails details(long id, String intakeId) {
        LOGGER.trace(
                "details(id={}, intakeId={}) entered",
                LogSummary.describe(id),
                LogSummary.describe(intakeId));
        try {
            CaseDetails details = new CaseDetails();
            details.setMemorandumId(id);
            IntakeInput intake = new IntakeInput();
            intake.setIntakeId(intakeId);
            details.setIntake(intake);

            var loggedResult = details;
            LOGGER.trace("details returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "details exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
