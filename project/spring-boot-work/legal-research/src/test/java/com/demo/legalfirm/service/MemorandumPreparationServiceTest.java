package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.details.*;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.assessment.ResearchEvidenceAssessment;
import com.demo.legalfirm.repository.MemorandumCompletionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Verifies save-before-completion ordering and failure compensation for memorandum preparation.
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
    private final CaseInputService assembler = mock(CaseInputService.class);
    private final ResearchPlanningService planner = mock(ResearchPlanningService.class);
    private final CourtListenerResearchService researcher =
            mock(CourtListenerResearchService.class);
    private final ResearchEvidenceAssessmentService assessor =
            mock(ResearchEvidenceAssessmentService.class);
    private final MemorandumDraftingService drafter = mock(MemorandumDraftingService.class);
    private final MemorandumPdfService pdf = mock(MemorandumPdfService.class);
    private final MemorandumOutputService output = mock(MemorandumOutputService.class);
    private final MemorandumCompletionRepository repository =
            mock(MemorandumCompletionRepository.class);
    private final MemorandumPreparationService service =
            new MemorandumPreparationService(
                    assembler, planner, researcher, assessor, drafter, pdf, output, repository);
    private final CaseDetails details = new CaseDetails();

    @BeforeEach
    void setUp() throws IOException {
        LOGGER.trace("setUp entered");
        details.setMemorandumId(2L);
        var intake = new IntakeInput();
        intake.setIntakeId("I-test");
        details.setIntake(intake);
        when(assembler.assemble(details)).thenReturn(new CaseInput(details, List.of(), List.of()));
        when(planner.createPlan(any())).thenReturn(new ResearchPlan(List.of()));
        when(assessor.assess(any(), any())).thenReturn(new ResearchEvidenceAssessment(List.of()));
        when(pdf.render(any(), eq(details))).thenReturn(new byte[] {1});
        when(output.save(eq(2L), any())).thenReturn("memorandum-2-test.pdf");
        LOGGER.trace("setUp completed");
    }

    @Test
    void savesPdfBeforeRecordingCompletion() throws IOException {
        LOGGER.trace("savesPdfBeforeRecordingCompletion entered");
        service.prepare(details);
        var order =
                inOrder(assembler, planner, researcher, assessor, drafter, pdf, output, repository);
        order.verify(assembler).assemble(details);
        order.verify(planner).createPlan(any());
        order.verify(researcher).research(any());
        order.verify(assessor).assess(any(), any());
        order.verify(drafter).draft(any(), any(), any(), any());
        order.verify(pdf).render(any(), eq(details));
        order.verify(output).save(eq(2L), any());
        order.verify(repository).markCompleted(2L, "I-test", "memorandum-2-test.pdf");
        verify(output, never()).remove(any());
        LOGGER.trace("savesPdfBeforeRecordingCompletion completed");
    }

    @Test
    void assemblyFailureDoesNotProceed() throws IOException {
        LOGGER.trace("assemblyFailureDoesNotProceed entered");
        IOException failure = new IOException("Unreadable PDF");
        when(assembler.assemble(details)).thenThrow(failure);
        assertThatThrownBy(() -> service.prepare(details))
                .isInstanceOf(UncheckedIOException.class)
                .hasCause(failure);
        verifyNoInteractions(planner, researcher, assessor, drafter, pdf, output, repository);
        LOGGER.trace("assemblyFailureDoesNotProceed completed");
    }

    @Test
    void renderingFailureDoesNotSaveOrComplete() throws IOException {
        LOGGER.trace("renderingFailureDoesNotSaveOrComplete entered");
        when(pdf.render(any(), eq(details))).thenThrow(new IOException("Rendering failed"));
        assertThatThrownBy(() -> service.prepare(details)).isInstanceOf(UncheckedIOException.class);
        verifyNoInteractions(output, repository);
        LOGGER.trace("renderingFailureDoesNotSaveOrComplete completed");
    }

    @Test
    void saveFailureDoesNotComplete() throws IOException {
        LOGGER.trace("saveFailureDoesNotComplete entered");
        when(output.save(eq(2L), any())).thenThrow(new IOException("No disk space"));
        assertThatThrownBy(() -> service.prepare(details)).isInstanceOf(UncheckedIOException.class);
        verifyNoInteractions(repository);
        LOGGER.trace("saveFailureDoesNotComplete completed");
    }

    @Test
    void completionFailureRemovesUnrecordedPdf() throws IOException {
        LOGGER.trace("completionFailureRemovesUnrecordedPdf entered");
        var failure = new IllegalStateException("No longer requested");
        doThrow(failure).when(repository).markCompleted(anyLong(), any(), any());
        assertThatThrownBy(() -> service.prepare(details)).isSameAs(failure);
        verify(output).remove("memorandum-2-test.pdf");
        LOGGER.trace("completionFailureRemovesUnrecordedPdf completed");
    }

    @Test
    void cleanupFailurePreservesOriginalException() throws IOException {
        LOGGER.trace("cleanupFailurePreservesOriginalException entered");
        var failure = new IllegalStateException("Update failed");
        var cleanup = new IOException("Cleanup failed");
        doThrow(failure).when(repository).markCompleted(anyLong(), any(), any());
        doThrow(cleanup).when(output).remove(any());
        assertThatThrownBy(() -> service.prepare(details)).isSameAs(failure);
        assertThat(failure.getSuppressed()).containsExactly(cleanup);
        LOGGER.trace("cleanupFailurePreservesOriginalException completed");
    }
}
