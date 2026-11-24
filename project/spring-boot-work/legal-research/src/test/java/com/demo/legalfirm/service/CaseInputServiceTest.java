package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.details.IntakeInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentType;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * Verifies case-input assembly, document naming, missing files, and extraction failures.
 *
 * @file CaseInputServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class CaseInputServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseInputServiceTest.class);
    private final PdfDocumentService extractor = mock(PdfDocumentService.class);
    private final CaseInputService service =
            new CaseInputService(extractor, new DefaultResourceLoader());

    @Test
    void assemblesBriefAndListedSupportingPdfWithoutRenamingSupportingFilename()
            throws IOException {
        LOGGER.trace(
                "assemblesBriefAndListedSupportingPdfWithoutRenamingSupportingFilename entered");
        stubExtraction();
        CaseDetails details = details("I-204", List.of("I-205-uscis-receipt-notice.pdf"));

        CaseInput input = service.assemble(details);

        assertThat(input.getDetails()).isSameAs(details);
        assertThat(input.getDocuments())
                .extracting(DocumentInput::getFilename)
                .containsExactly("I-204-intake-brief.pdf", "I-205-uscis-receipt-notice.pdf");
        assertThat(input.getDocuments())
                .extracting(DocumentInput::getSourceId)
                .containsExactly(
                        "PDF:I-204-intake-brief.pdf", "PDF:I-205-uscis-receipt-notice.pdf");
        assertThat(input.getDocuments())
                .extracting(DocumentInput::getDocumentType)
                .containsExactly(DocumentType.INTAKE_BRIEF, DocumentType.SUPPORTING_DOCUMENT);
        assertThat(input.getMissingDocumentFilenames()).isEmpty();
        LOGGER.trace(
                "assemblesBriefAndListedSupportingPdfWithoutRenamingSupportingFilename completed");
    }

    @Test
    void deduplicatesFilenamesAndPreservesBriefClassification() throws IOException {
        LOGGER.trace("deduplicatesFilenamesAndPreservesBriefClassification entered");
        stubExtraction();

        CaseInput input =
                service.assemble(
                        details(
                                "I-204",
                                List.of(
                                        "I-204-intake-brief.pdf",
                                        "I-204-notice-to-appear.pdf",
                                        "I-204-notice-to-appear.pdf")));

        assertThat(input.getDocuments())
                .extracting(DocumentInput::getFilename)
                .containsExactly("I-204-intake-brief.pdf", "I-204-notice-to-appear.pdf");
        assertThat(input.getDocuments().get(0).getDocumentType())
                .isEqualTo(DocumentType.INTAKE_BRIEF);
        verify(extractor, times(2))
                .extract(any(Resource.class), anyString(), any(DocumentType.class));
        LOGGER.trace("deduplicatesFilenamesAndPreservesBriefClassification completed");
    }

    @Test
    void recordsMissingBriefAndSupportingFileWithoutTurningChecklistIntoFilenames()
            throws IOException {
        LOGGER.trace(
                "recordsMissingBriefAndSupportingFileWithoutTurningChecklistIntoFilenames entered");
        CaseDetails details = details("MISSING", List.of("unavailable.pdf"));
        details.getIntake().setExpectedDocumentTypes(List.of("not-a-local-filename"));

        CaseInput input = service.assemble(details);

        assertThat(input.getDocuments()).isEmpty();
        assertThat(input.getMissingDocumentFilenames())
                .containsExactly("MISSING-intake-brief.pdf", "unavailable.pdf");
        verifyNoInteractions(extractor);
        LOGGER.trace(
                "recordsMissingBriefAndSupportingFileWithoutTurningChecklistIntoFilenames"
                    + " completed");
    }

    @Test
    void extractsBriefEvenWhenNoSupportingDocumentsAreListed() throws IOException {
        LOGGER.trace("extractsBriefEvenWhenNoSupportingDocumentsAreListed entered");
        stubExtraction();

        CaseInput input = service.assemble(details("I-204", List.of()));

        assertThat(input.getDocuments())
                .extracting(DocumentInput::getFilename)
                .containsExactly("I-204-intake-brief.pdf");
        LOGGER.trace("extractsBriefEvenWhenNoSupportingDocumentsAreListed completed");
    }

    @Test
    void existingPdfExtractionFailureIsNotReportedAsAMissingFile() throws IOException {
        LOGGER.trace("existingPdfExtractionFailureIsNotReportedAsAMissingFile entered");
        IOException failure = new IOException("Extraction failed");
        when(extractor.extract(any(Resource.class), anyString(), any(DocumentType.class)))
                .thenThrow(failure);

        assertThatThrownBy(() -> service.assemble(details("I-204", List.of()))).isSameAs(failure);
        LOGGER.trace("existingPdfExtractionFailureIsNotReportedAsAMissingFile completed");
    }

    private void stubExtraction() throws IOException {
        LOGGER.trace("stubExtraction entered");
        // Keep the source ID supplied by the assembler rather than inventing one in extraction.
        when(extractor.extract(any(Resource.class), anyString(), any(DocumentType.class)))
                .thenAnswer(
                        call ->
                                new DocumentInput(
                                        call.getArgument(1),
                                        ((Resource) call.getArgument(0)).getFilename(),
                                        call.getArgument(2),
                                        List.of()));
        LOGGER.trace("stubExtraction completed");
    }

    private CaseDetails details(String intakeId, List<String> filenames) {
        LOGGER.trace(
                "details(intakeId={}, filenames={}) entered",
                LogSummary.describe(intakeId),
                LogSummary.describe(filenames));
        CaseDetails result = new CaseDetails();
        result.setMemorandumId(1);
        result.setIntake(
                new IntakeInput(
                        "INTAKE:" + intakeId, intakeId, "Test summary", filenames, List.of()));
        LOGGER.trace("details returning {}", LogSummary.describe(result));
        return result;
    }
}
