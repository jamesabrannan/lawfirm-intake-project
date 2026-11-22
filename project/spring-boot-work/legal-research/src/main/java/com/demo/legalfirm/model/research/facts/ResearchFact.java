package com.demo.legalfirm.model.research.facts;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A source-linked statement identified during case analysis. pageNumber is null for sources without
 * pages, such as database intake notes.
 *
 * @file ResearchFact.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ResearchFact {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchFact.class);

    private String statement;
    private FactClassification classification;
    private String sourceId;
    private Integer pageNumber;

    public ResearchFact() {
        LOGGER.trace("ResearchFact() entered");
        try {

            LOGGER.trace("ResearchFact completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ResearchFact exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public ResearchFact(
            String statement,
            FactClassification classification,
            String sourceId,
            Integer pageNumber) {
        LOGGER.trace(
                "ResearchFact(statement={}, classification={}, sourceId={}, pageNumber={}) entered",
                LogSummary.describe(statement),
                LogSummary.describe(classification),
                LogSummary.describe(sourceId),
                LogSummary.describe(pageNumber));
        try {
            this.statement = statement;
            this.classification = classification;
            this.sourceId = sourceId;
            this.pageNumber = pageNumber;
            LOGGER.trace("ResearchFact completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ResearchFact exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getStatement() {
        LOGGER.trace("getStatement() entered");
        try {

            var loggedResult = statement;
            LOGGER.trace("getStatement returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getStatement exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setStatement(String statement) {
        LOGGER.trace("setStatement(statement={}) entered", LogSummary.describe(statement));
        try {
            this.statement = statement;
            LOGGER.trace("setStatement completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setStatement exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public FactClassification getClassification() {
        LOGGER.trace("getClassification() entered");
        try {

            var loggedResult = classification;
            LOGGER.trace("getClassification returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getClassification exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setClassification(FactClassification classification) {
        LOGGER.trace(
                "setClassification(classification={}) entered",
                LogSummary.describe(classification));
        try {
            this.classification = classification;
            LOGGER.trace("setClassification completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setClassification exited exceptionally; type={}",
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

    public Integer getPageNumber() {
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

    public void setPageNumber(Integer pageNumber) {
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
}
