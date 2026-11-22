package com.demo.legalfirm.model.caseinputs.details;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groups memorandum details, intake data, client, requestor, and preparer.
 *
 * @file CaseDetails.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class CaseDetails {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDetails.class);

    private long memorandumId;
    private String memorandumStatus;
    private ClientInput client;
    private StaffInput requestor;
    private StaffInput preparer;
    private IntakeInput intake;

    public CaseDetails() {
        LOGGER.trace("CaseDetails() entered");
        try {

            LOGGER.trace("CaseDetails completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "CaseDetails exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public CaseDetails(
            long memorandumId,
            String memorandumStatus,
            ClientInput client,
            StaffInput requestor,
            StaffInput preparer,
            IntakeInput intake) {
        LOGGER.trace(
                "CaseDetails(memorandumId={}, memorandumStatus={}, client={}, requestor={},"
                        + " preparer={}, intake={}) entered",
                LogSummary.describe(memorandumId),
                LogSummary.describe(memorandumStatus),
                LogSummary.describe(client),
                LogSummary.describe(requestor),
                LogSummary.describe(preparer),
                LogSummary.describe(intake));
        try {
            this.memorandumId = memorandumId;
            this.memorandumStatus = memorandumStatus;
            this.client = client;
            this.requestor = requestor;
            this.preparer = preparer;
            this.intake = intake;
            LOGGER.trace("CaseDetails completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "CaseDetails exited exceptionally; type={}",
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

    public String getMemorandumStatus() {
        LOGGER.trace("getMemorandumStatus() entered");
        try {

            var loggedResult = memorandumStatus;
            LOGGER.trace("getMemorandumStatus returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getMemorandumStatus exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setMemorandumStatus(String memorandumStatus) {
        LOGGER.trace(
                "setMemorandumStatus(memorandumStatus={}) entered",
                LogSummary.describe(memorandumStatus));
        try {
            this.memorandumStatus = memorandumStatus;
            LOGGER.trace("setMemorandumStatus completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setMemorandumStatus exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public ClientInput getClient() {
        LOGGER.trace("getClient() entered");
        try {

            var loggedResult = client;
            LOGGER.trace("getClient returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getClient exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setClient(ClientInput client) {
        LOGGER.trace("setClient(client={}) entered", LogSummary.describe(client));
        try {
            this.client = client;
            LOGGER.trace("setClient completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setClient exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public StaffInput getRequestor() {
        LOGGER.trace("getRequestor() entered");
        try {

            var loggedResult = requestor;
            LOGGER.trace("getRequestor returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getRequestor exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setRequestor(StaffInput requestor) {
        LOGGER.trace("setRequestor(requestor={}) entered", LogSummary.describe(requestor));
        try {
            this.requestor = requestor;
            LOGGER.trace("setRequestor completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setRequestor exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public StaffInput getPreparer() {
        LOGGER.trace("getPreparer() entered");
        try {

            var loggedResult = preparer;
            LOGGER.trace("getPreparer returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getPreparer exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setPreparer(StaffInput preparer) {
        LOGGER.trace("setPreparer(preparer={}) entered", LogSummary.describe(preparer));
        try {
            this.preparer = preparer;
            LOGGER.trace("setPreparer completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setPreparer exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public IntakeInput getIntake() {
        LOGGER.trace("getIntake() entered");
        try {

            var loggedResult = intake;
            LOGGER.trace("getIntake returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getIntake exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setIntake(IntakeInput intake) {
        LOGGER.trace("setIntake(intake={}) entered", LogSummary.describe(intake));
        try {
            this.intake = intake;
            LOGGER.trace("setIntake completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setIntake exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
