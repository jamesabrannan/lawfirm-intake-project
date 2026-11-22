package com.demo.legalfirm.model.research;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.research.facts.ResearchFact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * One proposed research topic, its factual basis, and questions to investigate.
 *
 * @file ResearchTopic.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ResearchTopic {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchTopic.class);

    private String id;
    private String title;
    private List<ResearchFact> facts;
    private List<String> missingInformation;
    private List<String> researchQuestions;
    private List<String> searchQueries;
    private List<String> relatedTopicIds;

    public ResearchTopic() {
        LOGGER.trace("ResearchTopic() entered");
        try {

            LOGGER.trace("ResearchTopic completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ResearchTopic exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public ResearchTopic(
            String id,
            String title,
            List<ResearchFact> facts,
            List<String> missingInformation,
            List<String> researchQuestions,
            List<String> searchQueries,
            List<String> relatedTopicIds) {
        LOGGER.trace(
                "ResearchTopic(id={}, title={}, facts={}, missingInformation={},"
                        + " researchQuestions={}, searchQueries={}, relatedTopicIds={}) entered",
                LogSummary.describe(id),
                LogSummary.describe(title),
                LogSummary.describe(facts),
                LogSummary.describe(missingInformation),
                LogSummary.describe(researchQuestions),
                LogSummary.describe(searchQueries),
                LogSummary.describe(relatedTopicIds));
        try {
            this.id = id;
            this.title = title;
            this.facts = facts;
            this.missingInformation = missingInformation;
            this.researchQuestions = researchQuestions;
            this.searchQueries = searchQueries;
            this.relatedTopicIds = relatedTopicIds;
            LOGGER.trace("ResearchTopic completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ResearchTopic exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getId() {
        LOGGER.trace("getId() entered");
        try {

            var loggedResult = id;
            LOGGER.trace("getId returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setId(String id) {
        LOGGER.trace("setId(id={}) entered", LogSummary.describe(id));
        try {
            this.id = id;
            LOGGER.trace("setId completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setId exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public String getTitle() {
        LOGGER.trace("getTitle() entered");
        try {

            var loggedResult = title;
            LOGGER.trace("getTitle returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getTitle exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setTitle(String title) {
        LOGGER.trace("setTitle(title={}) entered", LogSummary.describe(title));
        try {
            this.title = title;
            LOGGER.trace("setTitle completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setTitle exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<ResearchFact> getFacts() {
        LOGGER.trace("getFacts() entered");
        try {

            var loggedResult = facts;
            LOGGER.trace("getFacts returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getFacts exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setFacts(List<ResearchFact> facts) {
        LOGGER.trace("setFacts(facts={}) entered", LogSummary.describe(facts));
        try {
            this.facts = facts;
            LOGGER.trace("setFacts completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setFacts exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getMissingInformation() {
        LOGGER.trace("getMissingInformation() entered");
        try {

            var loggedResult = missingInformation;
            LOGGER.trace("getMissingInformation returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getMissingInformation exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setMissingInformation(List<String> missingInformation) {
        LOGGER.trace(
                "setMissingInformation(missingInformation={}) entered",
                LogSummary.describe(missingInformation));
        try {
            this.missingInformation = missingInformation;
            LOGGER.trace("setMissingInformation completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setMissingInformation exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getResearchQuestions() {
        LOGGER.trace("getResearchQuestions() entered");
        try {

            var loggedResult = researchQuestions;
            LOGGER.trace("getResearchQuestions returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getResearchQuestions exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setResearchQuestions(List<String> researchQuestions) {
        LOGGER.trace(
                "setResearchQuestions(researchQuestions={}) entered",
                LogSummary.describe(researchQuestions));
        try {
            this.researchQuestions = researchQuestions;
            LOGGER.trace("setResearchQuestions completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setResearchQuestions exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getSearchQueries() {
        LOGGER.trace("getSearchQueries() entered");
        try {

            var loggedResult = searchQueries;
            LOGGER.trace("getSearchQueries returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getSearchQueries exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setSearchQueries(List<String> searchQueries) {
        LOGGER.trace(
                "setSearchQueries(searchQueries={}) entered", LogSummary.describe(searchQueries));
        try {
            this.searchQueries = searchQueries;
            LOGGER.trace("setSearchQueries completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setSearchQueries exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<String> getRelatedTopicIds() {
        LOGGER.trace("getRelatedTopicIds() entered");
        try {

            var loggedResult = relatedTopicIds;
            LOGGER.trace("getRelatedTopicIds returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getRelatedTopicIds exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setRelatedTopicIds(List<String> relatedTopicIds) {
        LOGGER.trace(
                "setRelatedTopicIds(relatedTopicIds={}) entered",
                LogSummary.describe(relatedTopicIds));
        try {
            this.relatedTopicIds = relatedTopicIds;
            LOGGER.trace("setRelatedTopicIds completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setRelatedTopicIds exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
