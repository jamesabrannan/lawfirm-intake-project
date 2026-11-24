package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.model.research.*;
import com.demo.legalfirm.model.research.courtlistener.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Tests topic-query provenance, duplicate-query reuse, empty searches and failure propagation.
 *
 * @file CourtListenerResearchServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class CourtListenerResearchServiceTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CourtListenerResearchServiceTest.class);

    @Test
    void linksResultsToTopicsAndReusesDuplicateQueries() {
        LOGGER.trace("linksResultsToTopicsAndReusesDuplicateQueries entered");
        CourtListenerSearchService search = mock(CourtListenerSearchService.class);
        CourtListenerSearchResult result = new CourtListenerSearchResult();
        result.setClusterId(123L);
        when(search.search("shared query")).thenReturn(List.of(result));
        when(search.search("no matches")).thenReturn(List.of());
        ResearchPlan plan =
                new ResearchPlan(
                        List.of(
                                topic("T1", List.of(" shared query ", "no matches")),
                                topic("T2", List.of("shared query")),
                                topic("T3", List.of())));
        var evidence = new CourtListenerResearchService(search, 2).research(plan);
        assertThat(evidence).hasSize(3);
        assertThat(evidence.get(0).getTopicId()).isEqualTo("T1");
        assertThat(evidence.get(0).getQuery()).isEqualTo("shared query");
        assertThat(evidence.get(0).getResults().get(0).getClusterId()).isEqualTo(123L);
        assertThat(evidence.get(1).getResults()).isEmpty();
        assertThat(evidence.get(2).getTopicId()).isEqualTo("T2");
        assertThat(evidence.get(2).getResults()).containsExactly(result);
        verify(search, times(1)).search("shared query");
        verify(search, times(1)).search("no matches");
        verifyNoMoreInteractions(search);
        LOGGER.trace("linksResultsToTopicsAndReusesDuplicateQueries completed");
    }

    @Test
    void emptyQueriesDoNotCallCourtListener() {
        LOGGER.trace("emptyQueriesDoNotCallCourtListener entered");
        CourtListenerSearchService search = mock(CourtListenerSearchService.class);
        assertThat(
                        new CourtListenerResearchService(search, 2)
                                .research(new ResearchPlan(List.of(topic("T1", List.of())))))
                .isEmpty();
        verifyNoInteractions(search);
        LOGGER.trace("emptyQueriesDoNotCallCourtListener completed");
    }

    @Test
    void failureStopsResearchRatherThanReportingNoMatches() {
        LOGGER.trace("failureStopsResearchRatherThanReportingNoMatches entered");
        CourtListenerSearchService search = mock(CourtListenerSearchService.class);
        RuntimeException failure = new IllegalStateException("Unavailable");
        when(search.search("first")).thenThrow(failure);
        assertThatThrownBy(
                        () ->
                                new CourtListenerResearchService(search, 2)
                                        .research(
                                                new ResearchPlan(
                                                        List.of(
                                                                topic(
                                                                        "T1",
                                                                        List.of(
                                                                                "first",
                                                                                "second"))))))
                .isSameAs(failure);
        verify(search, never()).search("second");
        LOGGER.trace("failureStopsResearchRatherThanReportingNoMatches completed");
    }

    @Test
    void appliesConfiguredLimitWithoutChangingThePlan() {
        LOGGER.trace("appliesConfiguredLimitWithoutChangingThePlan entered");
        CourtListenerSearchService search = mock(CourtListenerSearchService.class);
        ResearchPlan plan = new ResearchPlan(List.of(topic("T1", List.of("first", "second", "third"))));
        var evidence = new CourtListenerResearchService(search, 2).research(plan);
        assertThat(evidence).extracting(CourtListenerResearchResult::getQuery).containsExactly("first", "second");
        verify(search, never()).search("third");
        assertThat(plan.getTopics().get(0).getSearchQueries()).hasSize(3);
        reset(search);
        assertThat(new CourtListenerResearchService(search, 1).research(plan)).hasSize(1);
        verify(search).search("first");
        verifyNoMoreInteractions(search);
        LOGGER.trace("appliesConfiguredLimitWithoutChangingThePlan completed");
    }

    private ResearchTopic topic(String id, List<String> queries) {
        LOGGER.trace("topic entered; idLength={}, queries={}", id.length(), queries.size());
        ResearchTopic topic = new ResearchTopic();
        topic.setId(id);
        topic.setSearchQueries(queries);
        LOGGER.trace("topic returning ResearchTopic");
        return topic;
    }
}
