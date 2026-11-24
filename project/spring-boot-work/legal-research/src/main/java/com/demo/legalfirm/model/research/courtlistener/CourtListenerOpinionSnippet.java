package com.demo.legalfirm.model.research.courtlistener;

import com.demo.legalfirm.logging.LogSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds a search-result opinion snippet, not the full opinion text.
 *
 * @file CourtListenerOpinionSnippet.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourtListenerOpinionSnippet {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourtListenerOpinionSnippet.class);
    private Long id;
    private String type;
    private String snippet;

    public CourtListenerOpinionSnippet() {
        LOGGER.trace("CourtListenerOpinionSnippet() entered");
        LOGGER.trace("CourtListenerOpinionSnippet completed");
    }

    public CourtListenerOpinionSnippet(Long id, String type, String snippet) {
        LOGGER.trace(
                "CourtListenerOpinionSnippet constructor entered; fields are summarized or"
                    + " redacted");
        LOGGER.trace("id={}", LogSummary.describe(id));
        this.id = id;
        LOGGER.trace("type={}", LogSummary.describe(type));
        this.type = type;
        LOGGER.trace("snippet={}", LogSummary.describe(snippet));
        this.snippet = snippet;
        LOGGER.trace("CourtListenerOpinionSnippet completed");
    }

    public Long getId() {
        LOGGER.trace("getId() entered");
        LOGGER.trace("getId returning {}", LogSummary.describe(id));
        return id;
    }

    public void setId(Long id) {
        LOGGER.trace("setId(id={}) entered", LogSummary.describe(id));
        this.id = id;
        LOGGER.trace("setId completed");
    }

    public String getType() {
        LOGGER.trace("getType() entered");
        LOGGER.trace("getType returning {}", LogSummary.describe(type));
        return type;
    }

    public void setType(String type) {
        LOGGER.trace("setType(type={}) entered", LogSummary.describe(type));
        this.type = type;
        LOGGER.trace("setType completed");
    }

    public String getSnippet() {
        LOGGER.trace("getSnippet() entered");
        LOGGER.trace("getSnippet returning {}", LogSummary.describe(snippet));
        return snippet;
    }

    public void setSnippet(String snippet) {
        LOGGER.trace("setSnippet(snippet={}) entered", LogSummary.describe(snippet));
        this.snippet = snippet;
        LOGGER.trace("setSnippet completed");
    }
}
