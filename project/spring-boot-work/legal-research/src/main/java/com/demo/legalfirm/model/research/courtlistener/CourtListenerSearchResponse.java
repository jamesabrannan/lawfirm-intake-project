package com.demo.legalfirm.model.research.courtlistener;

import com.demo.legalfirm.logging.LogSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Maps one CourtListener search page, including pagination metadata.
 *
 * @file CourtListenerSearchResponse.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourtListenerSearchResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourtListenerSearchResponse.class);
    private long count;
    private String next;
    private String previous;
    private List<CourtListenerSearchResult> results;

    public CourtListenerSearchResponse() {
        LOGGER.trace("CourtListenerSearchResponse() entered");
        LOGGER.trace("CourtListenerSearchResponse completed");
    }

    public CourtListenerSearchResponse(
            long count, String next, String previous, List<CourtListenerSearchResult> results) {
        LOGGER.trace(
                "CourtListenerSearchResponse constructor entered; fields are summarized or"
                    + " redacted");
        LOGGER.trace("count={}", LogSummary.describe(count));
        this.count = count;
        LOGGER.trace("next={}", LogSummary.describe(next));
        this.next = next;
        LOGGER.trace("previous={}", LogSummary.describe(previous));
        this.previous = previous;
        LOGGER.trace("results={}", LogSummary.describe(results));
        this.results = results;
        LOGGER.trace("CourtListenerSearchResponse completed");
    }

    public long getCount() {
        LOGGER.trace("getCount() entered");
        LOGGER.trace("getCount returning {}", LogSummary.describe(count));
        return count;
    }

    public void setCount(long count) {
        LOGGER.trace("setCount(count={}) entered", LogSummary.describe(count));
        this.count = count;
        LOGGER.trace("setCount completed");
    }

    public String getNext() {
        LOGGER.trace("getNext() entered");
        LOGGER.trace("getNext returning {}", LogSummary.describe(next));
        return next;
    }

    public void setNext(String next) {
        LOGGER.trace("setNext(next={}) entered", LogSummary.describe(next));
        this.next = next;
        LOGGER.trace("setNext completed");
    }

    public String getPrevious() {
        LOGGER.trace("getPrevious() entered");
        LOGGER.trace("getPrevious returning {}", LogSummary.describe(previous));
        return previous;
    }

    public void setPrevious(String previous) {
        LOGGER.trace("setPrevious(previous={}) entered", LogSummary.describe(previous));
        this.previous = previous;
        LOGGER.trace("setPrevious completed");
    }

    public List<CourtListenerSearchResult> getResults() {
        LOGGER.trace("getResults() entered");
        LOGGER.trace("getResults returning {}", LogSummary.describe(results));
        return results;
    }

    public void setResults(List<CourtListenerSearchResult> results) {
        LOGGER.trace("setResults(results={}) entered", LogSummary.describe(results));
        this.results = results;
        LOGGER.trace("setResults completed");
    }
}
