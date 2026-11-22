package com.demo.legalfirm.model.caseinputs.details;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds client identity and contact details used in case preparation, excluding SSNs.
 *
 * @file ClientInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ClientInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientInput.class);

    private String contactId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;

    public ClientInput() {
        LOGGER.trace("ClientInput() entered");
        try {

            LOGGER.trace("ClientInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ClientInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public ClientInput(
            String contactId,
            String firstName,
            String lastName,
            String phone,
            String email,
            String streetAddress,
            String city,
            String state,
            String postalCode) {
        LOGGER.trace(
                "ClientInput(contactId={}, firstName={}, lastName={}, phone={}, email={},"
                        + " streetAddress={}, city={}, state={}, postalCode={}) entered",
                LogSummary.describe(contactId),
                LogSummary.describe(firstName),
                LogSummary.describe(lastName),
                LogSummary.describe(phone),
                LogSummary.describe(email),
                LogSummary.describe(streetAddress),
                LogSummary.describe(city),
                LogSummary.describe(state),
                LogSummary.describe(postalCode));
        try {
            this.contactId = contactId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.email = email;
            this.streetAddress = streetAddress;
            this.city = city;
            this.state = state;
            this.postalCode = postalCode;
            LOGGER.trace("ClientInput completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ClientInput exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getContactId() {
        LOGGER.trace("getContactId() entered");
        try {

            var loggedResult = contactId;
            LOGGER.trace("getContactId returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getContactId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setContactId(String contactId) {
        LOGGER.trace("setContactId(contactId={}) entered", LogSummary.describe(contactId));
        try {
            this.contactId = contactId;
            LOGGER.trace("setContactId completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setContactId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getFirstName() {
        LOGGER.trace("getFirstName() entered");
        try {

            var loggedResult = firstName;
            LOGGER.trace("getFirstName returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getFirstName exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setFirstName(String firstName) {
        LOGGER.trace("setFirstName(firstName={}) entered", LogSummary.describe(firstName));
        try {
            this.firstName = firstName;
            LOGGER.trace("setFirstName completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setFirstName exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getLastName() {
        LOGGER.trace("getLastName() entered");
        try {

            var loggedResult = lastName;
            LOGGER.trace("getLastName returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getLastName exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setLastName(String lastName) {
        LOGGER.trace("setLastName(lastName={}) entered", LogSummary.describe(lastName));
        try {
            this.lastName = lastName;
            LOGGER.trace("setLastName completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setLastName exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getPhone() {
        LOGGER.trace("getPhone() entered");
        try {

            var loggedResult = phone;
            LOGGER.trace("getPhone returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getPhone exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setPhone(String phone) {
        LOGGER.trace("setPhone(phone={}) entered", LogSummary.describe(phone));
        try {
            this.phone = phone;
            LOGGER.trace("setPhone completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setPhone exited exceptionally; type={}",
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

    public String getStreetAddress() {
        LOGGER.trace("getStreetAddress() entered");
        try {

            var loggedResult = streetAddress;
            LOGGER.trace("getStreetAddress returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getStreetAddress exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setStreetAddress(String streetAddress) {
        LOGGER.trace(
                "setStreetAddress(streetAddress={}) entered", LogSummary.describe(streetAddress));
        try {
            this.streetAddress = streetAddress;
            LOGGER.trace("setStreetAddress completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setStreetAddress exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getCity() {
        LOGGER.trace("getCity() entered");
        try {

            var loggedResult = city;
            LOGGER.trace("getCity returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getCity exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setCity(String city) {
        LOGGER.trace("setCity(city={}) entered", LogSummary.describe(city));
        try {
            this.city = city;
            LOGGER.trace("setCity completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setCity exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getState() {
        LOGGER.trace("getState() entered");
        try {

            var loggedResult = state;
            LOGGER.trace("getState returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getState exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setState(String state) {
        LOGGER.trace("setState(state={}) entered", LogSummary.describe(state));
        try {
            this.state = state;
            LOGGER.trace("setState completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setState exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getPostalCode() {
        LOGGER.trace("getPostalCode() entered");
        try {

            var loggedResult = postalCode;
            LOGGER.trace("getPostalCode returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getPostalCode exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setPostalCode(String postalCode) {
        LOGGER.trace("setPostalCode(postalCode={}) entered", LogSummary.describe(postalCode));
        try {
            this.postalCode = postalCode;
            LOGGER.trace("setPostalCode completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setPostalCode exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
