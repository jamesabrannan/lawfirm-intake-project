package com.demo.legalfirm.model.caseinputs;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The locally assembled material supplied to the fact-extraction call.
 *
 * @file CaseInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class CaseInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseInput.class);

    private CaseDetails details;
    private List<DocumentInput> documents;
    private List<String> missingDocumentFilenames;

    public CaseInput() {
        LOGGER.trace("CaseInput() entered");
        try {

            LOGGER.trace("CaseInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "CaseInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public CaseInput(
            CaseDetails details,
            List<DocumentInput> documents,
            List<String> missingDocumentFilenames) {
        LOGGER.trace(
                "CaseInput(details={}, documents={}, missingDocumentFilenames={}) entered",
                LogSummary.describe(details),
                LogSummary.describe(documents),
                LogSummary.describe(missingDocumentFilenames));
        try {
            this.details = details;
            this.documents = documents;
            this.missingDocumentFilenames = missingDocumentFilenames;
            LOGGER.trace("CaseInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "CaseInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public CaseDetails getDetails() {
        LOGGER.trace("getDetails() entered");
        try {

            var loggedResult = details;
            LOGGER.trace("getDetails returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getDetails exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setDetails(CaseDetails details) {
        LOGGER.trace("setDetails(details={}) entered", LogSummary.describe(details));
        try {
            this.details = details;
            LOGGER.trace("setDetails completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setDetails exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<DocumentInput> getDocuments() {
        LOGGER.trace("getDocuments() entered");
        try {

            var loggedResult = documents;
            LOGGER.trace("getDocuments returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getDocuments exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setDocuments(List<DocumentInput> documents) {
        LOGGER.trace("setDocuments(documents={}) entered", LogSummary.describe(documents));
        try {
            this.documents = documents;
            LOGGER.trace("setDocuments completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setDocuments exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getMissingDocumentFilenames() {
        LOGGER.trace("getMissingDocumentFilenames() entered");
        try {

            var loggedResult = missingDocumentFilenames;
            LOGGER.trace(
                    "getMissingDocumentFilenames returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getMissingDocumentFilenames exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setMissingDocumentFilenames(List<String> missingDocumentFilenames) {
        LOGGER.trace(
                "setMissingDocumentFilenames(missingDocumentFilenames={}) entered",
                LogSummary.describe(missingDocumentFilenames));
        try {
            this.missingDocumentFilenames = missingDocumentFilenames;
            LOGGER.trace("setMissingDocumentFilenames completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setMissingDocumentFilenames exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
