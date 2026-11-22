package com.demo.legalfirm.model.memorandums;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Identifies one memorandum whose synchronous preparation completed.
 *
 * @file MemorandumPreparationResult.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class MemorandumPreparationResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumPreparationResult.class);

    private long memorandumId;
    private String intakeId;
    private String status;

    public MemorandumPreparationResult() {
        LOGGER.trace("MemorandumPreparationResult() entered");
        try {

            LOGGER.trace("MemorandumPreparationResult completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumPreparationResult exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public MemorandumPreparationResult(long memorandumId, String intakeId, String status) {
        LOGGER.trace(
                "MemorandumPreparationResult(memorandumId={}, intakeId={}, status={}) entered",
                LogSummary.describe(memorandumId),
                LogSummary.describe(intakeId),
                LogSummary.describe(status));
        try {
            this.memorandumId = memorandumId;
            this.intakeId = intakeId;
            this.status = status;
            LOGGER.trace("MemorandumPreparationResult completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "MemorandumPreparationResult exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public long getMemorandumId() {
        LOGGER.trace("getMemorandumId() entered");
        try {

            var loggedResult = memorandumId;
            LOGGER.trace("getMemorandumId returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getMemorandumId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setMemorandumId(long memorandumId) {
        LOGGER.trace("setMemorandumId(memorandumId={}) entered", LogSummary.describe(memorandumId));
        try {
            this.memorandumId = memorandumId;
            LOGGER.trace("setMemorandumId completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setMemorandumId exited exceptionally; type={}",
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
}
