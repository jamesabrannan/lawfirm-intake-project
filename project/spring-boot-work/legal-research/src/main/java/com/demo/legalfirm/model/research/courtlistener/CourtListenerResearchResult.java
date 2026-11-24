package com.demo.legalfirm.model.research.courtlistener;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Associates a research topic and executed query with preliminary case-law search leads.
 *
 * @file CourtListenerResearchResult.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class CourtListenerResearchResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourtListenerResearchResult.class);
    private String topicId;
    private String query;
    private List<CourtListenerSearchResult> results;

    public CourtListenerResearchResult() {
        LOGGER.trace("CourtListenerResearchResult entered");
        LOGGER.trace("CourtListenerResearchResult completed");
    }

    public CourtListenerResearchResult(
            String topicId, String query, List<CourtListenerSearchResult> results) {
        LOGGER.trace(
                "CourtListenerResearchResult(topicId={}, query={}, results={}) entered",
                LogSummary.describe(topicId),
                LogSummary.describe(query),
                LogSummary.describe(results));
        this.topicId = topicId;
        this.query = query;
        this.results = results;
        LOGGER.trace("CourtListenerResearchResult completed");
    }

    public String getTopicId() {
        LOGGER.trace("getTopicId entered");
        LOGGER.trace("getTopicId returning {}", LogSummary.describe(topicId));
        return topicId;
    }

    public void setTopicId(String topicId) {
        LOGGER.trace("setTopicId(value={}) entered", LogSummary.describe(topicId));
        this.topicId = topicId;
        LOGGER.trace("setTopicId completed");
    }

    public String getQuery() {
        LOGGER.trace("getQuery entered");
        LOGGER.trace("getQuery returning {}", LogSummary.describe(query));
        return query;
    }

    public void setQuery(String query) {
        LOGGER.trace("setQuery(value={}) entered", LogSummary.describe(query));
        this.query = query;
        LOGGER.trace("setQuery completed");
    }

    public List<CourtListenerSearchResult> getResults() {
        LOGGER.trace("getResults entered");
        LOGGER.trace("getResults returning {}", LogSummary.describe(results));
        return results;
    }

    public void setResults(List<CourtListenerSearchResult> results) {
        LOGGER.trace("setResults(value={}) entered", LogSummary.describe(results));
        this.results = results;
        LOGGER.trace("setResults completed");
    }
}
