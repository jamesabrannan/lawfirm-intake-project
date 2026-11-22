package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentPageInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentType;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies PDF extraction, source identity, page numbering, and input failures.
 *
 * @file PdfDocumentServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class PdfDocumentServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfDocumentServiceTest.class);

    private final PdfDocumentService service = new PdfDocumentService();

    @TempDir Path directory;

    @Test
    void extractsOneStreamResourceAndPreservesBlankPageAndDocumentIdentity() throws IOException {
        LOGGER.trace("extractsOneStreamResourceAndPreservesBlankPageAndDocumentIdentity() entered");
        try {
            ByteArrayResource resource =
                    new ByteArrayResource(createPdf("First page.", "", "Third page.")) {
                        @Override
                        public String getFilename() {
                            LOGGER.trace("getFilename() entered for supplied test resource");
                            LOGGER.trace(
                                    "getFilename returning {}",
                                    LogSummary.describe("caller-selected.pdf"));
                            return "caller-selected.pdf";
                        }
                    };

            DocumentInput result =
                    service.extract(resource, "DOC-7", DocumentType.SUPPORTING_DOCUMENT);

            assertThat(result.getSourceId()).isEqualTo("DOC-7");
            assertThat(result.getFilename()).isEqualTo("caller-selected.pdf");
            assertThat(result.getDocumentType()).isEqualTo(DocumentType.SUPPORTING_DOCUMENT);
            assertThat(result.getPages())
                    .extracting(DocumentPageInput::getPageNumber)
                    .containsExactly(1, 2, 3);
            assertThat(result.getPages().get(0).getText())
                    .contains("First page.")
                    .doesNotContain("Third page.");
            assertThat(result.getPages().get(1).getText()).isBlank();
            assertThat(result.getPages().get(2).getText())
                    .contains("Third page.")
                    .doesNotContain("First page.");
            LOGGER.trace(
                    "extractsOneStreamResourceAndPreservesBlankPageAndDocumentIdentity completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "extractsOneStreamResourceAndPreservesBlankPageAndDocumentIdentity exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void readsSuppliedFilesystemResource() throws IOException {
        LOGGER.trace("readsSuppliedFilesystemResource() entered");
        try {
            Path pdf = directory.resolve("chosen-name.pdf");
            Files.write(pdf, createPdf("Filesystem content."));

            DocumentInput result =
                    service.extract(new FileSystemResource(pdf), "FILE", DocumentType.INTAKE_BRIEF);

            assertThat(result.getFilename()).isEqualTo("chosen-name.pdf");
            assertThat(result.getPages().get(0).getText()).contains("Filesystem content.");
            // The service must release its input resources after extraction.
            Files.delete(pdf);
            LOGGER.trace("readsSuppliedFilesystemResource completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "readsSuppliedFilesystemResource exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void readsCallerSelectedDemoClasspathPdf() throws IOException {
        LOGGER.trace("readsCallerSelectedDemoClasspathPdf() entered");
        try {
            DocumentInput result =
                    service.extract(
                            new ClassPathResource("docs/I-205-intake-brief.pdf"),
                            "BRIEF",
                            DocumentType.INTAKE_BRIEF);

            assertThat(result.getFilename()).isEqualTo("I-205-intake-brief.pdf");
            assertThat(result.getPages()).isNotEmpty();
            assertThat(result.getPages().get(0).getPageNumber()).isEqualTo(1);
            assertThat(result.getPages())
                    .anySatisfy(page -> assertThat(page.getText()).isNotBlank());
            LOGGER.trace("readsCallerSelectedDemoClasspathPdf completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "readsCallerSelectedDemoClasspathPdf exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void reportsUnreadableOrInvalidInputRatherThanReturningAnEmptyDocument() {
        LOGGER.trace("reportsUnreadableOrInvalidInputRatherThanReturningAnEmptyDocument() entered");
        try {
            assertThatThrownBy(
                            () ->
                                    service.extract(
                                            new FileSystemResource(
                                                    directory.resolve("missing.pdf")),
                                            "MISSING",
                                            DocumentType.SUPPORTING_DOCUMENT))
                    .isInstanceOf(IOException.class);
            assertThatThrownBy(
                            () ->
                                    service.extract(
                                            new ByteArrayResource(
                                                    "Not a PDF".getBytes(StandardCharsets.UTF_8)),
                                            "INVALID",
                                            DocumentType.SUPPORTING_DOCUMENT))
                    .isInstanceOf(IOException.class);
            LOGGER.trace(
                    "reportsUnreadableOrInvalidInputRatherThanReturningAnEmptyDocument completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "reportsUnreadableOrInvalidInputRatherThanReturningAnEmptyDocument exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    private byte[] createPdf(String... pageTexts) throws IOException {
        LOGGER.trace("createPdf(pageTexts={}) entered", LogSummary.describe(pageTexts));
        try {
            try (PDDocument document = new PDDocument();
                    ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                for (String text : pageTexts) {
                    PDPage page = new PDPage();
                    document.addPage(page);
                    if (!text.isEmpty()) {
                        try (PDPageContentStream content =
                                new PDPageContentStream(document, page)) {
                            content.beginText();
                            content.setFont(
                                    new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                            content.newLineAtOffset(50, 700);
                            content.showText(text);
                            content.endText();
                        }
                    }
                }
                document.save(output);

                var loggedResult = output.toByteArray();
                LOGGER.trace("createPdf returning {}", LogSummary.describe(loggedResult));
                return loggedResult;
            }
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "createPdf exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
