package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.repository.MemorandumCompletionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Prepares and saves one preliminary memorandum, then records successful completion.
 *
 * @file MemorandumPreparationService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class MemorandumPreparationService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumPreparationService.class);
    private final CaseInputService caseInputService;
    private final ResearchPlanningService researchPlanningService;
    private final CourtListenerResearchService courtListenerResearchService;
    private final ResearchEvidenceAssessmentService evidenceAssessmentService;
    private final MemorandumDraftingService memorandumDraftingService;
    private final MemorandumPdfService pdfService;
    private final MemorandumOutputService outputService;
    private final MemorandumCompletionRepository completionRepository;

    public MemorandumPreparationService(
            CaseInputService caseInputService,
            ResearchPlanningService researchPlanningService,
            CourtListenerResearchService courtListenerResearchService,
            ResearchEvidenceAssessmentService evidenceAssessmentService,
            MemorandumDraftingService memorandumDraftingService,
            MemorandumPdfService pdfService,
            MemorandumOutputService outputService,
            MemorandumCompletionRepository completionRepository) {
        LOGGER.trace(
                "MemorandumPreparationService(caseInputService={}) entered",
                LogSummary.describe(caseInputService));
        this.caseInputService = caseInputService;
        this.researchPlanningService = researchPlanningService;
        this.courtListenerResearchService = courtListenerResearchService;
        this.evidenceAssessmentService = evidenceAssessmentService;
        this.memorandumDraftingService = memorandumDraftingService;
        this.pdfService = pdfService;
        this.outputService = outputService;
        this.completionRepository = completionRepository;
        LOGGER.trace(
                "PDF dependencies: renderer={}, output={}, repository={}",
                LogSummary.describe(pdfService),
                LogSummary.describe(outputService),
                LogSummary.describe(completionRepository));
        LOGGER.trace("Drafting dependency={}", LogSummary.describe(memorandumDraftingService));
        LOGGER.trace("Assessment dependency={}", LogSummary.describe(evidenceAssessmentService));
        LOGGER.trace(
                "CourtListener dependency={}", LogSummary.describe(courtListenerResearchService));
        LOGGER.trace("Planning dependency={}", LogSummary.describe(researchPlanningService));
        LOGGER.trace("MemorandumPreparationService completed");
    }

    /**
     * Assembles CaseInput, researches, assesses, drafts, saves the PDF, and records completion. It
     * must return only after those steps have succeeded. No role checks or work-selection rules
     * belong in this step.
     */
    public void prepare(CaseDetails details) {
        LOGGER.debug("prepare(details={}) entered", LogSummary.describe(details));
        try {
            CaseInput input = caseInputService.assemble(details);
            LOGGER.info(
                    "Case input ready for AI; memorandumId={}, documents={}, missingFiles={}",
                    details.getMemorandumId(),
                    input.getDocuments().size(),
                    input.getMissingDocumentFilenames().size());
            ResearchPlan plan = researchPlanningService.createPlan(input);
            LOGGER.info("Research plan ready; topics={}", plan.getTopics().size());
            var researchResults = courtListenerResearchService.research(plan);
            LOGGER.info(
                    "CourtListener evidence ready; topicQueryResults={}", researchResults.size());
            var assessment = evidenceAssessmentService.assess(plan, researchResults);
            LOGGER.info(
                    "Research evidence assessed; questions={}",
                    assessment.getQuestionAssessments().size());
            var draft = memorandumDraftingService.draft(input, plan, researchResults, assessment);
            LOGGER.info("Memorandum draft ready; memorandumId={}", details.getMemorandumId());
            LOGGER.debug(
                    "Draft ready for future PDF generation; draft={}", LogSummary.describe(draft));
            byte[] pdf = pdfService.render(draft, details);
            String filename = outputService.save(details.getMemorandumId(), pdf);
            // The database is updated only after the PDF has been saved. This demo uses
            // local files, not a distributed transaction. A failed update triggers cleanup.
            try {
                completionRepository.markCompleted(
                        details.getMemorandumId(), details.getIntake().getIntakeId(), filename);
            } catch (RuntimeException failure) {
                try {
                    outputService.remove(filename);
                } catch (IOException | RuntimeException cleanup) {
                    failure.addSuppressed(cleanup);
                }
                throw failure;
            }
            LOGGER.info(
                    "Memorandum preparation completed; memorandumId={}, filename={}",
                    details.getMemorandumId(),
                    filename);
            LOGGER.debug("prepare completed");
        } catch (IOException failure) {
            LOGGER.trace(
                    "prepare failed during file processing; type={}",
                    failure.getClass().getSimpleName());
            throw new UncheckedIOException("Unable to process memorandum files", failure);
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "prepare exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
