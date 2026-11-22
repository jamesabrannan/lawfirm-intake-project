package com.demo.legalfirm.model.caseinputs.documents;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Text extracted from one intake brief or supporting PDF, preserving its source identity.
 *
 * @file DocumentInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class DocumentInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentInput.class);

    private String sourceId;
    private String filename;
    private DocumentType documentType;
    private List<DocumentPageInput> pages;

    public DocumentInput() {
        LOGGER.trace("DocumentInput() entered");
        try {

            LOGGER.trace("DocumentInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "DocumentInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public DocumentInput(
            String sourceId,
            String filename,
            DocumentType documentType,
            List<DocumentPageInput> pages) {
        LOGGER.trace(
                "DocumentInput(sourceId={}, filename={}, documentType={}, pages={}) entered",
                LogSummary.describe(sourceId),
                LogSummary.describe(filename),
                LogSummary.describe(documentType),
                LogSummary.describe(pages));
        try {
            this.sourceId = sourceId;
            this.filename = filename;
            this.documentType = documentType;
            this.pages = pages;
            LOGGER.trace("DocumentInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "DocumentInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getSourceId() {
        LOGGER.trace("getSourceId() entered");
        try {

            var loggedResult = sourceId;
            LOGGER.trace("getSourceId returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getSourceId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setSourceId(String sourceId) {
        LOGGER.trace("setSourceId(sourceId={}) entered", LogSummary.describe(sourceId));
        try {
            this.sourceId = sourceId;
            LOGGER.trace("setSourceId completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setSourceId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getFilename() {
        LOGGER.trace("getFilename() entered");
        try {

            var loggedResult = filename;
            LOGGER.trace("getFilename returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getFilename exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setFilename(String filename) {
        LOGGER.trace("setFilename(filename={}) entered", LogSummary.describe(filename));
        try {
            this.filename = filename;
            LOGGER.trace("setFilename completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setFilename exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public DocumentType getDocumentType() {
        LOGGER.trace("getDocumentType() entered");
        try {

            var loggedResult = documentType;
            LOGGER.trace("getDocumentType returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getDocumentType exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setDocumentType(DocumentType documentType) {
        LOGGER.trace("setDocumentType(documentType={}) entered", LogSummary.describe(documentType));
        try {
            this.documentType = documentType;
            LOGGER.trace("setDocumentType completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setDocumentType exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<DocumentPageInput> getPages() {
        LOGGER.trace("getPages() entered");
        try {

            var loggedResult = pages;
            LOGGER.trace("getPages returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getPages exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setPages(List<DocumentPageInput> pages) {
        LOGGER.trace("setPages(pages={}) entered", LogSummary.describe(pages));
        try {
            this.pages = pages;
            LOGGER.trace("setPages completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setPages exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
