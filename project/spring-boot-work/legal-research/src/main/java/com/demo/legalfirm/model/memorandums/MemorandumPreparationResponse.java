package com.demo.legalfirm.model.memorandums;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Summarizes a synchronous preparation request without returning case details or PDFs.
 *
 * @file MemorandumPreparationResponse.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class MemorandumPreparationResponse {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumPreparationResponse.class);

    private String status;
    private List<MemorandumPreparationResult> memorandums;

    public MemorandumPreparationResponse() {
        LOGGER.trace("MemorandumPreparationResponse() entered");
        try {

            LOGGER.trace("MemorandumPreparationResponse completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumPreparationResponse exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public MemorandumPreparationResponse(
            String status, List<MemorandumPreparationResult> memorandums) {
        LOGGER.trace(
                "MemorandumPreparationResponse(status={}, memorandums={}) entered",
                LogSummary.describe(status),
                LogSummary.describe(memorandums));
        try {
            this.status = status;
            this.memorandums = memorandums;
            LOGGER.trace("MemorandumPreparationResponse completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumPreparationResponse exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getStatus() {
        LOGGER.trace("getStatus() entered");
        try {

            var loggedResult = status;
            LOGGER.trace("getStatus returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getStatus exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setStatus(String status) {
        LOGGER.trace("setStatus(status={}) entered", LogSummary.describe(status));
        try {
            this.status = status;
            LOGGER.trace("setStatus completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setStatus exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<MemorandumPreparationResult> getMemorandums() {
        LOGGER.trace("getMemorandums() entered");
        try {

            var loggedResult = memorandums;
            LOGGER.trace("getMemorandums returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getMemorandums exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setMemorandums(List<MemorandumPreparationResult> memorandums) {
        LOGGER.trace("setMemorandums(memorandums={}) entered", LogSummary.describe(memorandums));
        try {
            this.memorandums = memorandums;
            LOGGER.trace("setMemorandums completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setMemorandums exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
