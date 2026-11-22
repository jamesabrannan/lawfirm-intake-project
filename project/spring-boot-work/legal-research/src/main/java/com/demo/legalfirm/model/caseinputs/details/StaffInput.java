package com.demo.legalfirm.model.caseinputs.details;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds assigned staff identity and contact details, excluding credentials.
 *
 * @file StaffInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class StaffInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaffInput.class);

    private String staffId;
    private String staffName;
    private String email;
    private String role;

    public StaffInput() {
        LOGGER.trace("StaffInput() entered");
        try {

            LOGGER.trace("StaffInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "StaffInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public StaffInput(String staffId, String staffName, String email, String role) {
        LOGGER.trace(
                "StaffInput(staffId={}, staffName={}, email={}, role={}) entered",
                LogSummary.describe(staffId),
                LogSummary.describe(staffName),
                LogSummary.describe(email),
                LogSummary.describe(role));
        try {
            this.staffId = staffId;
            this.staffName = staffName;
            this.email = email;
            this.role = role;
            LOGGER.trace("StaffInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "StaffInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getStaffId() {
        LOGGER.trace("getStaffId() entered");
        try {

            var loggedResult = staffId;
            LOGGER.trace("getStaffId returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getStaffId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setStaffId(String staffId) {
        LOGGER.trace("setStaffId(staffId={}) entered", LogSummary.describe(staffId));
        try {
            this.staffId = staffId;
            LOGGER.trace("setStaffId completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setStaffId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getStaffName() {
        LOGGER.trace("getStaffName() entered");
        try {

            var loggedResult = staffName;
            LOGGER.trace("getStaffName returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getStaffName exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setStaffName(String staffName) {
        LOGGER.trace("setStaffName(staffName={}) entered", LogSummary.describe(staffName));
        try {
            this.staffName = staffName;
            LOGGER.trace("setStaffName completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setStaffName exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getEmail() {
        LOGGER.trace("getEmail() entered");
        try {

            var loggedResult = email;
            LOGGER.trace("getEmail returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getEmail exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setEmail(String email) {
        LOGGER.trace("setEmail(email={}) entered", LogSummary.describe(email));
        try {
            this.email = email;
            LOGGER.trace("setEmail completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setEmail exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getRole() {
        LOGGER.trace("getRole() entered");
        try {

            var loggedResult = role;
            LOGGER.trace("getRole returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getRole exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setRole(String role) {
        LOGGER.trace("setRole(role={}) entered", LogSummary.describe(role));
        try {
            this.role = role;
            LOGGER.trace("setRole completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setRole exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
