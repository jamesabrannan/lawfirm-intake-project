package com.demo.legalfirm.model.research;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The complete topic-planning response returned by the AI call.
 *
 * @file ResearchPlan.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ResearchPlan {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchPlan.class);

    private List<ResearchTopic> topics;

    public ResearchPlan() {
        LOGGER.trace("ResearchPlan() entered");
        try {

            LOGGER.trace("ResearchPlan completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ResearchPlan exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public ResearchPlan(List<ResearchTopic> topics) {
        LOGGER.trace("ResearchPlan(topics={}) entered", LogSummary.describe(topics));
        try {
            this.topics = topics;
            LOGGER.trace("ResearchPlan completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "ResearchPlan exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public List<ResearchTopic> getTopics() {
        LOGGER.trace("getTopics() entered");
        try {

            var loggedResult = topics;
            LOGGER.trace("getTopics returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "getTopics exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    public void setTopics(List<ResearchTopic> topics) {
        LOGGER.trace("setTopics(topics={}) entered", LogSummary.describe(topics));
        try {
            this.topics = topics;
            LOGGER.trace("setTopics completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "setTopics exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
