package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;

import com.demo.legalfirm.model.caseinputs.details.*;
import com.demo.legalfirm.model.memorandums.draft.*;
import com.demo.legalfirm.model.research.facts.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.json.JsonMapper;

import java.nio.file.*;
import java.util.*;

/**
 * Verifies readable paginated PDF content and links without external research calls.
 *
 * @file MemorandumPdfServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class MemorandumPdfServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumPdfServiceTest.class);

    @Test
    void rendersSectionsUnicodeLongLinksAndPageNumbers() throws Exception {
        LOGGER.trace("rendersSectionsUnicodeLongLinksAndPageNumbers entered");
        var lead =
                new MemorandumResearchLead(
                        7L,
                        List.of(0),
                        "Potential lead; requires review.",
                        "Snippet only.",
                        "Example v. Example",
                        List.of("100 Example 1"),
                        "Example Court",
                        "https://www.courtlistener.com/opinion/7/"
                                + "long-source-name-".repeat(15));
        var gaps = new ArrayList<String>();
        for (int i = 0; i < 30; i++)
            gaps.add(
                    "Research gap "
                            + i
                            + ": obtain and review the full opinion and relevant official sources. "
                            + "Do not infer a holding or a confirmed medical explanation from preliminary materials. "
                                    .repeat(3));
        var issue =
                new MemorandumIssueDraft(
                        "T1",
                        "Issue for lawyer review",
                        "The client disputes the allegation.",
                        List.of(lead),
                        gaps);
        var draft =
                new MemorandumDraft(
                        2L,
                        "I-test",
                        "Preliminary Legal Research Memorandum",
                        "Summary for lawyer review.",
                        List.of(
                                new ResearchFact(
                                        "Client reports symptoms; caf\u00e9 and \u00a7 21-901.1 remain source"
                                            + " text.",
                                        FactClassification.CLIENT_REPORTED,
                                        "INTAKE:I-test",
                                        null)),
                        List.of(issue),
                        List.of("Medical records not supplied."),
                        List.of("Review supporting records."),
                        List.of("Text-only extraction; snippets are unverified."));
        byte[] bytes = new MemorandumPdfService().render(draft, details());
        try (var document = Loader.loadPDF(bytes)) {
            String text = new PDFTextStripper().getText(document);
            assertThat(document.getNumberOfPages()).isGreaterThan(2);
            assertThat(text)
                    .contains(
                            "Summary",
                            "Factual background",
                            "CLIENT_REPORTED",
                            "caf\u00e9",
                            "\u00a7 21-901.1",
                            "Example v. Example",
                            "Research gaps",
                            "Missing information",
                            "Lawyer follow-up",
                            "Limitations",
                            "Research gap 29");
            assertThat(text)
                    .contains(
                            "Page 1 of " + document.getNumberOfPages(),
                            "Page "
                                    + document.getNumberOfPages()
                                    + " of "
                                    + document.getNumberOfPages());
            boolean found = false;
            for (var page : document.getPages()) {
                assertThat(page.getMediaBox().getWidth()).isEqualTo(612f);
                for (var annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink link) {
                        assertThat(((PDActionURI) link.getAction()).getURI())
                                .isEqualTo(lead.getSourceUrl());
                        assertThat(link.getRectangle().getUpperRightX()).isLessThanOrEqualTo(559f);
                        assertThat(link.getRectangle().getLowerLeftY()).isGreaterThan(50f);
                        found = true;
                    }
                }
            }
            assertThat(found).isTrue();
        }
        Path output = Path.of("target/pdf-preview/layout-test.pdf");
        Files.createDirectories(output.getParent());
        Files.write(output, bytes);
        LOGGER.trace("rendersSectionsUnicodeLongLinksAndPageNumbers completed");
    }

    @Test
    void rejectsMismatchedCaseIdentity() {
        LOGGER.trace("rejectsMismatchedCaseIdentity entered");
        assertThatThrownBy(
                        () -> new MemorandumPdfService().render(new MemorandumDraft(), details()))
                .isInstanceOf(IllegalArgumentException.class);
        LOGGER.trace("rejectsMismatchedCaseIdentity completed");
    }

    @Test
    void rendersOptionalCapturedDraftForVisualReview() throws Exception {
        LOGGER.trace("rendersOptionalCapturedDraftForVisualReview entered");
        String fixture = System.getProperty("memorandum.preview.input");
        if (fixture != null) {
            var mapper = JsonMapper.builder().build();
            var input = mapper.readTree(Files.readString(Path.of(fixture)));
            var draft = mapper.treeToValue(input.get("draft"), MemorandumDraft.class);
            var details = mapper.treeToValue(input.get("details"), CaseDetails.class);
            byte[] bytes = new MemorandumPdfService().render(draft, details);
            Files.write(Path.of("target/pdf-preview/mateo-preliminary-memorandum.pdf"), bytes);
            try (var document = Loader.loadPDF(bytes)) {
                assertThat(new PDFTextStripper().getText(document))
                        .contains("Mateo Santos", "Lawyer follow-up");
            }
        }
        LOGGER.trace("rendersOptionalCapturedDraftForVisualReview completed");
    }

    private CaseDetails details() {
        LOGGER.trace("details entered");
        var details = new CaseDetails();
        details.setMemorandumId(2L);
        var intake = new IntakeInput();
        intake.setIntakeId("I-test");
        details.setIntake(intake);
        var client = new ClientInput();
        client.setFirstName("Mateo");
        client.setLastName("Santos");
        details.setClient(client);
        LOGGER.trace("details returning CaseDetails");
        return details;
    }
}
