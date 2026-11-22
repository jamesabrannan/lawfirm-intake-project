package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentPageInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentType;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts the existing text layer from exactly one caller-supplied PDF.
 *
 * @file PdfDocumentService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class PdfDocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfDocumentService.class);

    /**
     * Reads one PDF and retains its physical, one-based page numbers, including pages with no
     * extractable text. The caller supplies the resource, source ID and document type; this service
     * does not query SQLite, construct filenames, prepend intake IDs, or discover/iterate over
     * document files.
     *
     * <p>For this demo, PDFs are assumed to be in resources/docs and packaged inside the
     * application JAR. A real application would store them in a filesystem. Reading the supplied
     * Resource as a stream supports both a ClassPathResource and a FileSystemResource without
     * changing extraction.
     *
     * <p>Demo simplification: all relevant PDF information is assumed to exist in an extractable
     * text layer. Embedded images and visual-only content are not analyzed; scanned PDFs requiring
     * OCR are outside the demo's scope.
     *
     * @param pdf the single PDF resource resolved by the caller
     * @param sourceId the caller-assigned reference used to trace AI findings
     * @param documentType whether this is an intake brief or supporting document
     * @return the supplied document identity and text extracted page by page
     * @throws IOException if the resource cannot be read or parsed as a PDF
     */
    public DocumentInput extract(Resource pdf, String sourceId, DocumentType documentType)
            throws IOException {
        LOGGER.debug(
                "extract(pdf={}, sourceId={}, documentType={}) entered",
                LogSummary.describe(pdf),
                LogSummary.describe(sourceId),
                LogSummary.describe(documentType));
        try {
            Assert.notNull(pdf, "PDF resource must not be null");
            Assert.hasText(sourceId, "Source ID must not be blank");
            Assert.notNull(documentType, "Document type must not be null");

            LOGGER.info("Starting PDF extraction; documentType={}", documentType);
            List<DocumentPageInput> pages = new ArrayList<>();
            try (InputStream input = pdf.getInputStream();
                    PDDocument document = Loader.loadPDF(input.readAllBytes())) {
                LOGGER.debug("Loaded PDF; pageCount={}", document.getNumberOfPages());
                PDFTextStripper stripper = new PDFTextStripper();
                for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
                    stripper.setStartPage(pageNumber);
                    stripper.setEndPage(pageNumber);
                    String pageText = stripper.getText(document);
                    pages.add(new DocumentPageInput(pageNumber, pageText));
                    LOGGER.debug(
                            "Extracted PDF page; pageNumber={}, textLength={}, blank={}",
                            pageNumber,
                            pageText.length(),
                            pageText.isBlank());
                }
            }

            LOGGER.info("Completed PDF extraction; pageCount={}", pages.size());
            var loggedResult = new DocumentInput(sourceId, pdf.getFilename(), documentType, pages);
            LOGGER.debug("extract returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.error(
                    "PDF extraction failed; type={}", loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
