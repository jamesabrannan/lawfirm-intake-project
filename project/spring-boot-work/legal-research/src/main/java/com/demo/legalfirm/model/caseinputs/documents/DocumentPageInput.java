package com.demo.legalfirm.model.caseinputs.documents;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text extracted from a PDF page, using a one-based page number.
 *
 * @file DocumentPageInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class DocumentPageInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentPageInput.class);

    private int pageNumber;
    private String text;

    public DocumentPageInput() {
        LOGGER.trace("DocumentPageInput() entered");
        try {

            LOGGER.trace("DocumentPageInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "DocumentPageInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public DocumentPageInput(int pageNumber, String text) {
        LOGGER.trace(
                "DocumentPageInput(pageNumber={}, text={}) entered",
                LogSummary.describe(pageNumber),
                LogSummary.describe(text));
        try {
            this.pageNumber = pageNumber;
            this.text = text;
            LOGGER.trace("DocumentPageInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "DocumentPageInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public int getPageNumber() {
        LOGGER.trace("getPageNumber() entered");
        try {

            var loggedResult = pageNumber;
            LOGGER.trace("getPageNumber returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getPageNumber exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setPageNumber(int pageNumber) {
        LOGGER.trace("setPageNumber(pageNumber={}) entered", LogSummary.describe(pageNumber));
        try {
            this.pageNumber = pageNumber;
            LOGGER.trace("setPageNumber completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setPageNumber exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getText() {
        LOGGER.trace("getText() entered");
        try {

            var loggedResult = text;
            LOGGER.trace("getText returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getText exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setText(String text) {
        LOGGER.trace("setText(text={}) entered", LogSummary.describe(text));
        try {
            this.text = text;
            LOGGER.trace("setText completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setText exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
