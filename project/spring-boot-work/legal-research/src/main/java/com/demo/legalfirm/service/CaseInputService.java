package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.details.IntakeInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Assembles one case's existing details and extracted local PDF pages into CaseInput.
 *
 * @file CaseInputService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class CaseInputService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseInputService.class);
    private final PdfDocumentService pdfDocumentService;
    private final ResourceLoader resourceLoader;

    public CaseInputService(PdfDocumentService pdfDocumentService, ResourceLoader resourceLoader) {
        LOGGER.trace(
                "CaseInputService(pdfDocumentService={}, resourceLoader={}) entered",
                LogSummary.describe(pdfDocumentService),
                LogSummary.describe(resourceLoader));
        this.pdfDocumentService = pdfDocumentService;
        this.resourceLoader = resourceLoader;
        LOGGER.trace("CaseInputService completed");
    }

    /**
     * Reuses the supplied database details; no database query or AI call occurs here. Supporting
     * filenames are used as listed on the intake. Only the intake brief follows the existing
     * {intakeId}-intake-brief.pdf naming convention.
     *
     * <p>Demo: PDFs live under resources/docs inside the JAR. A real application would resolve them
     * from filesystem storage. Extraction assumes a text layer; OCR and embedded-image
     * interpretation are outside this demo.
     *
     * <p>Missing files are reported by filename as limitations, not as checklist document requests.
     * An existing but unreadable PDF propagates an extraction failure rather than being silently
     * treated as missing.
     *
     * @param details the case details already selected for this memorandum
     * @return the supplied details, extracted documents, and locally missing filenames
     * @throws IOException if an existing PDF cannot be read or extracted
     */
    public CaseInput assemble(CaseDetails details) throws IOException {
        LOGGER.debug("assemble(details={}) entered", LogSummary.describe(details));
        try {
            Assert.notNull(details, "Case details must not be null");
            IntakeInput intake = details.getIntake();
            Assert.notNull(intake, "Intake must not be null");
            Assert.hasText(intake.getIntakeId(), "Intake ID must not be blank");
            Assert.notNull(intake.getDocumentFilenames(), "Document filenames must not be null");
            LOGGER.info("Assembling case input; memorandumId={}", details.getMemorandumId());

            String briefFilename = intake.getIntakeId() + "-intake-brief.pdf";
            Set<String> filenames = new LinkedHashSet<>();
            filenames.add(briefFilename);
            filenames.addAll(intake.getDocumentFilenames());
            List<DocumentInput> documents = new ArrayList<>();
            List<String> missing = new ArrayList<>();
            for (String filename : filenames) {
                Assert.hasText(filename, "Document filename must not be blank");
                Assert.isTrue(
                        !filename.contains("/") && !filename.contains("\\"),
                        "Document filename must be a filename without a directory");
                Resource resource = resourceLoader.getResource("classpath:docs/" + filename);
                if (!resource.exists()) {
                    missing.add(filename);
                    LOGGER.warn(
                            "Local PDF is missing; memorandumId={}, filename={}",
                            details.getMemorandumId(),
                            LogSummary.describe(filename));
                    continue;
                }
                DocumentType type =
                        filename.equals(briefFilename)
                                ? DocumentType.INTAKE_BRIEF
                                : DocumentType.SUPPORTING_DOCUMENT;
                documents.add(pdfDocumentService.extract(resource, "PDF:" + filename, type));
            }
            CaseInput result = new CaseInput(details, documents, missing);
            LOGGER.info(
                    "Assembled case input; memorandumId={}, documents={}, missingFiles={}",
                    details.getMemorandumId(),
                    documents.size(),
                    missing.size());
            LOGGER.debug("assemble returning {}", LogSummary.describe(result));
            return result;
        } catch (IOException | RuntimeException failure) {
            LOGGER.trace(
                    "assemble exited exceptionally; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
