package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.memorandums.draft.*;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.assessment.ResearchEvidenceAssessment;
import com.demo.legalfirm.model.research.facts.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.*;
import java.util.*;

/**
 * Verifies both seeded cases produce saved PDFs and completed database rows with mocked AI stages.
 *
 * @file MemorandumPreparationIntegrationTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@SpringBootTest(
        properties = {
            "spring.ai.openai.api-key=integration-test-placeholder",
            "memorandum.output-directory=target/test-completed-memorandums"
        })
@Transactional
@Sql(
        statements =
                "UPDATE research_memorandums SET research_memorandum_status='requested',"
                    + " filename=NULL WHERE id IN (1,2)")
class MemorandumPreparationIntegrationTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumPreparationIntegrationTest.class);
    @Autowired MemorandumProcessingService processing;
    @Autowired JdbcTemplate jdbc;
    @MockitoBean ResearchPlanningService planner;
    @MockitoBean CourtListenerResearchService researcher;
    @MockitoBean ResearchEvidenceAssessmentService assessor;
    @MockitoBean MemorandumDraftingService drafter;

    @Test
    @WithUserDetails("riley.morgan@example.com")
    void preparesBothPdfsAndDoesNotSelectCompletedCasesAgain() throws Exception {
        LOGGER.trace("preparesBothPdfsAndDoesNotSelectCompletedCasesAgain entered");
        when(planner.createPlan(any())).thenReturn(new ResearchPlan(List.of()));
        when(researcher.research(any())).thenReturn(List.of());
        when(assessor.assess(any(), any())).thenReturn(new ResearchEvidenceAssessment(List.of()));
        when(drafter.draft(any(), any(), any(), any()))
                .thenAnswer(
                        invocation -> {
                            CaseInput input = invocation.getArgument(0);
                            var details = input.getDetails();
                            return new MemorandumDraft(
                                    details.getMemorandumId(),
                                    details.getIntake().getIntakeId(),
                                    "Preliminary legal research memorandum",
                                    "Synthetic draft for pipeline testing.",
                                    List.of(
                                            new ResearchFact(
                                                    "Supplied intake material requires lawyer"
                                                        + " review.",
                                                    FactClassification.DOCUMENTED,
                                                    details.getIntake().getSourceId(),
                                                    null)),
                                    List.of(
                                            new MemorandumIssueDraft(
                                                    "T1",
                                                    "Issue requiring review",
                                                    "No legal conclusion has been verified.",
                                                    List.of(),
                                                    List.of("Review official sources."))),
                                    List.of(),
                                    List.of("Review the intake and supporting records."),
                                    List.of(
                                            "Test draft; text-only evidence and unreviewed"
                                                + " sources."));
                        });
        Path directory = Path.of("target/test-completed-memorandums").toAbsolutePath().normalize();
        try {
            var response = processing.prepareForCurrentStaff();
            assertThat(response.getStatus()).isEqualTo("completed");
            assertThat(response.getMemorandums()).hasSize(2);
            var filenames =
                    jdbc.queryForList(
                            "SELECT filename FROM research_memorandums WHERE"
                                + " research_memorandum_status='completed' ORDER BY id",
                            String.class);
            assertThat(filenames).hasSize(2);
            List<String> texts = new ArrayList<>();
            for (String filename : filenames) {
                Path file = directory.resolve(filename).normalize();
                assertThat(file.getParent()).isEqualTo(directory);
                assertThat(file).exists();
                try (var document = Loader.loadPDF(file.toFile())) {
                    texts.add(new PDFTextStripper().getText(document));
                }
            }
            assertThat(texts.get(0)).contains("Elena Rivera", "Intake: I-204", "Maya Chen");
            assertThat(texts.get(1)).contains("Mateo Santos", "Intake: I-205", "Doug Smith");
            assertThat(processing.prepareForCurrentStaff().getStatus()).isEqualTo("no_work");
            verify(drafter, times(2)).draft(any(), any(), any(), any());
            LOGGER.trace("preparesBothPdfsAndDoesNotSelectCompletedCasesAgain completed");
        } finally {
            for (String filename :
                    jdbc.queryForList(
                            "SELECT filename FROM research_memorandums WHERE id IN (1,2) AND"
                                + " filename IS NOT NULL",
                            String.class)) {
                Path file = directory.resolve(filename).normalize();
                if (file.getParent().equals(directory)) Files.deleteIfExists(file);
            }
        }
    }
}
