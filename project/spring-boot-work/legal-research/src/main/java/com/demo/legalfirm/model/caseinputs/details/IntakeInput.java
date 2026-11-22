package com.demo.legalfirm.model.caseinputs.details;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Relevant intake values loaded from the database; sourceId identifies these values as a source.
 *
 * @file IntakeInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class IntakeInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntakeInput.class);

    private String sourceId;
    private String intakeId;
    private String summary;
    private List<String> documentFilenames;
    private List<String> expectedDocumentTypes;

    public IntakeInput() {
        LOGGER.trace("IntakeInput() entered");
        try {

            LOGGER.trace("IntakeInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "IntakeInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public IntakeInput(
            String sourceId,
            String intakeId,
            String summary,
            List<String> documentFilenames,
            List<String> expectedDocumentTypes) {
        LOGGER.trace(
                "IntakeInput(sourceId={}, intakeId={}, summary={}, documentFilenames={},"
                        + " expectedDocumentTypes={}) entered",
                LogSummary.describe(sourceId),
                LogSummary.describe(intakeId),
                LogSummary.describe(summary),
                LogSummary.describe(documentFilenames),
                LogSummary.describe(expectedDocumentTypes));
        try {
            this.sourceId = sourceId;
            this.intakeId = intakeId;
            this.summary = summary;
            this.documentFilenames = documentFilenames;
            this.expectedDocumentTypes = expectedDocumentTypes;
            LOGGER.trace("IntakeInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "IntakeInput exited exceptionally; type={}",
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

    public String getIntakeId() {
        LOGGER.trace("getIntakeId() entered");
        try {

            var loggedResult = intakeId;
            LOGGER.trace("getIntakeId returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getIntakeId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setIntakeId(String intakeId) {
        LOGGER.trace("setIntakeId(intakeId={}) entered", LogSummary.describe(intakeId));
        try {
            this.intakeId = intakeId;
            LOGGER.trace("setIntakeId completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setIntakeId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getSummary() {
        LOGGER.trace("getSummary() entered");
        try {

            var loggedResult = summary;
            LOGGER.trace("getSummary returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getSummary exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setSummary(String summary) {
        LOGGER.trace("setSummary(summary={}) entered", LogSummary.describe(summary));
        try {
            this.summary = summary;
            LOGGER.trace("setSummary completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setSummary exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getDocumentFilenames() {
        LOGGER.trace("getDocumentFilenames() entered");
        try {

            var loggedResult = documentFilenames;
            LOGGER.trace("getDocumentFilenames returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getDocumentFilenames exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setDocumentFilenames(List<String> documentFilenames) {
        LOGGER.trace(
                "setDocumentFilenames(documentFilenames={}) entered",
                LogSummary.describe(documentFilenames));
        try {
            this.documentFilenames = documentFilenames;
            LOGGER.trace("setDocumentFilenames completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setDocumentFilenames exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getExpectedDocumentTypes() {
        LOGGER.trace("getExpectedDocumentTypes() entered");
        try {

            var loggedResult = expectedDocumentTypes;
            LOGGER.trace(
                    "getExpectedDocumentTypes returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getExpectedDocumentTypes exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setExpectedDocumentTypes(List<String> expectedDocumentTypes) {
        LOGGER.trace(
                "setExpectedDocumentTypes(expectedDocumentTypes={}) entered",
                LogSummary.describe(expectedDocumentTypes));
        try {
            this.expectedDocumentTypes = expectedDocumentTypes;
            LOGGER.trace("setExpectedDocumentTypes completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setExpectedDocumentTypes exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
