package com.demo.legalfirm.model.research;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.converter.BeanOutputConverter;

/**
 * Verifies conversion of AI JSON responses into research plans.
 *
 * @file ResearchPlanTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class ResearchPlanTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchPlanTest.class);

    private final BeanOutputConverter<ResearchPlan> converter =
            new BeanOutputConverter<>(ResearchPlan.class);

    @Test
    void convertsMultipleTopicsFromOneModelResponse() {
        LOGGER.trace("convertsMultipleTopicsFromOneModelResponse() entered");
        try {
            ResearchPlan plan =
                    converter.convert(
                            """
                            {
                              "topics":[
                                {
                                  "id":"T1","title":"Immigration",
                                  "facts":[],"missingInformation":["Eligibility category"],
                                  "researchQuestions":["What is the relevant category?"],
                                  "searchQueries":["USCIS eligibility categories"],
                                  "relatedTopicIds":["T2"]
                                },
                                {
                                  "id":"T2","title":"Traffic matter",
                                  "facts":[],"missingInformation":[],
                                  "researchQuestions":["What are the alleged offense elements?"],
                                  "searchQueries":["Maryland reckless driving elements"],
                                  "relatedTopicIds":["T1"]
                                }
                              ]
                            }
                            """);

            assertThat(plan.getTopics())
                    .extracting(ResearchTopic::getId)
                    .containsExactly("T1", "T2");
            assertThat(plan.getTopics().get(0).getRelatedTopicIds()).containsExactly("T2");
            assertThat(plan.getTopics().get(1).getMissingInformation()).isEmpty();
            LOGGER.trace("convertsMultipleTopicsFromOneModelResponse completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "convertsMultipleTopicsFromOneModelResponse exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void convertsAnEmptyTopicCollection() {
        LOGGER.trace("convertsAnEmptyTopicCollection() entered");
        try {
            ResearchPlan plan =
                    converter.convert(
                            """
                            {"topics":[]}
                            """);

            assertThat(plan.getTopics()).isEmpty();
            LOGGER.trace("convertsAnEmptyTopicCollection completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "convertsAnEmptyTopicCollection exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
