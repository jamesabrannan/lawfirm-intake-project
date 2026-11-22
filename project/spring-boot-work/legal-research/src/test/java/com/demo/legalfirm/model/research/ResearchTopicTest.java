package com.demo.legalfirm.model.research;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.legalfirm.model.research.facts.FactClassification;
import com.demo.legalfirm.model.research.facts.ResearchFact;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.converter.BeanOutputConverter;

/**
 * Verifies conversion of AI JSON topics and their nested facts.
 *
 * @file ResearchTopicTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class ResearchTopicTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchTopicTest.class);

    @Test
    void convertsTopicWithNestedFactsAndResearchInstructions() {
        LOGGER.trace("convertsTopicWithNestedFactsAndResearchInstructions() entered");
        try {
            ResearchTopic topic =
                    new BeanOutputConverter<>(ResearchTopic.class)
                            .convert(
                                    """
                                    {
                                      "id":"T1",
                                      "title":"Employment authorization",
                                      "facts":[{
                                        "statement":"A receipt records an application.",
                                        "classification":"DOCUMENTED",
                                        "sourceId":"DOC1",
                                        "pageNumber":1
                                      }],
                                      "missingInformation":["Eligibility category"],
                                      "researchQuestions":["What does the receipt establish?"],
                                      "searchQueries":["USCIS employment authorization receipt"],
                                      "relatedTopicIds":["T2"]
                                    }
                                    """);

            assertThat(topic.getId()).isEqualTo("T1");
            assertThat(topic.getTitle()).isEqualTo("Employment authorization");
            assertThat(topic.getFacts())
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(
                            new ResearchFact(
                                    "A receipt records an application.",
                                    FactClassification.DOCUMENTED,
                                    "DOC1",
                                    1));
            assertThat(topic.getMissingInformation()).containsExactly("Eligibility category");
            assertThat(topic.getResearchQuestions())
                    .containsExactly("What does the receipt establish?");
            assertThat(topic.getSearchQueries())
                    .containsExactly("USCIS employment authorization receipt");
            assertThat(topic.getRelatedTopicIds()).containsExactly("T2");
            LOGGER.trace("convertsTopicWithNestedFactsAndResearchInstructions completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "convertsTopicWithNestedFactsAndResearchInstructions exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
