package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.courtlistener.CourtListenerResearchResult;
import com.demo.legalfirm.model.research.courtlistener.CourtListenerSearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes planned case-law queries and preserves their topic and query provenance.
 *
 * @file CourtListenerResearchService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class CourtListenerResearchService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CourtListenerResearchService.class);
    private final CourtListenerSearchService searchService;
    private final int maxQueriesPerTopic;
    private final tools.jackson.databind.ObjectMapper jsonMapper = tools.jackson.databind.json.JsonMapper.builder().build();

    public CourtListenerResearchService(CourtListenerSearchService searchService,
            @Value("${courtlistener.max-queries-per-topic:2}") int maxQueriesPerTopic) {
        LOGGER.trace(
                "CourtListenerResearchService(searchService={}) entered",
                LogSummary.describe(searchService));
        Assert.isTrue(maxQueriesPerTopic > 0, "Maximum queries per topic must be positive");
        this.searchService = searchService;
        this.maxQueriesPerTopic = maxQueriesPerTopic;
        LOGGER.trace("Maximum queries per topic={}", maxQueriesPerTopic);
        LOGGER.trace("CourtListenerResearchService completed");
    }

    /**
     * Runs sequentially for this demo. Duplicate queries within this plan share one HTTP call, but
     * retain separate topic associations. Empty query lists need no API key or request. Search
     * failures propagate; an unavailable source is never presented as zero matches. Results are
     * snippets and metadata, not reviewed full opinions or verified legal authority.
     */
    public List<CourtListenerResearchResult> research(ResearchPlan plan) {
        LOGGER.debug("research(plan={}) entered", LogSummary.describe(plan));
        try {
            Assert.notNull(plan, "Research plan is required");
            Assert.notNull(plan.getTopics(), "Research topics are required");
            List<CourtListenerResearchResult> evidence = new ArrayList<>();
            Map<String, List<CourtListenerSearchResult>> searches = new LinkedHashMap<>();
            for (var topic : plan.getTopics()) {
                Assert.notNull(topic, "Research topic is required");
                Assert.hasText(topic.getId(), "Topic ID is required");
                Assert.notNull(topic.getSearchQueries(), "Search queries are required");
                LOGGER.info(
                        "Researching topic; topic={}, plannedQueries={}",
                        LogSummary.describe(topic.getId()),
                        topic.getSearchQueries().size());
                int queryLimit = Math.min(maxQueriesPerTopic, topic.getSearchQueries().size());
                LOGGER.info("Selecting topic queries; selected={}, skipped={}",
                        queryLimit, topic.getSearchQueries().size() - queryLimit);
                for (String proposedQuery : topic.getSearchQueries().subList(0, queryLimit)) {
                    Assert.hasText(proposedQuery, "Search query must not be blank");
                    String query = proposedQuery.trim();
                    if (!searches.containsKey(query)) {
                        searches.put(query, searchService.search(query));
                    }
                    evidence.add(
                            new CourtListenerResearchResult(
                                    topic.getId(), query, searches.get(query)));
                }
            }
            LOGGER.info(
                    "CourtListener research completed; uniqueQueries={}, topicQueryResults={}",
                    searches.size(),
                    evidence.size());
            // Explicit demo inspection: includes generated queries and public case-law snippets.
            // This serializes the mapped, bounded results, not the raw paginated API response.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CourtListener research results JSON:\n{}",
                        jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(evidence));
            }
            LOGGER.debug("research returning {}", LogSummary.describe(evidence));
            return evidence;
        } catch (RuntimeException failure) {
            LOGGER.warn(
                    "CourtListener research failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
