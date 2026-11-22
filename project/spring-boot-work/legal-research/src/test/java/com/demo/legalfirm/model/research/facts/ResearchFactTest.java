package com.demo.legalfirm.model.research.facts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.converter.BeanOutputConverter;

/**
 * Verifies AI JSON fact conversion and classification handling.
 *
 * @file ResearchFactTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class ResearchFactTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchFactTest.class);

    private final BeanOutputConverter<ResearchFact> converter =
            new BeanOutputConverter<>(ResearchFact.class);

    @Test
    void convertsDocumentFactWithPageReference() {
        LOGGER.trace("convertsDocumentFactWithPageReference() entered");
        try {
            ResearchFact fact =
                    converter.convert(
                            """
                            {"statement":"A charge is alleged in the citation.",
                             "classification":"ALLEGATION","sourceId":"DOC1","pageNumber":1}
                            """);

            assertThat(fact)
                    .usingRecursiveComparison()
                    .isEqualTo(
                            new ResearchFact(
                                    "A charge is alleged in the citation.",
                                    FactClassification.ALLEGATION,
                                    "DOC1",
                                    1));
            LOGGER.trace("convertsDocumentFactWithPageReference completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "convertsDocumentFactWithPageReference exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void convertsClientStatementWithoutPageReference() {
        LOGGER.trace("convertsClientStatementWithoutPageReference() entered");
        try {
            ResearchFact fact =
                    converter.convert(
                            """
{"statement":"Client reports sudden confusion.",
 "classification":"CLIENT_REPORTED","sourceId":"INTAKE","pageNumber":null}
""");

            assertThat(fact.getClassification()).isEqualTo(FactClassification.CLIENT_REPORTED);
            assertThat(fact.getPageNumber()).isNull();
            LOGGER.trace("convertsClientStatementWithoutPageReference completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "convertsClientStatementWithoutPageReference exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void rejectsUnrecognizedClassification() {
        LOGGER.trace("rejectsUnrecognizedClassification() entered");
        try {
            assertThatThrownBy(
                            () ->
                                    converter.convert(
                                            """
{"statement":"Unsupported classification.",
 "classification":"PROVEN","sourceId":"DOC1","pageNumber":1}
"""))
                    .isInstanceOf(RuntimeException.class);
            LOGGER.trace("rejectsUnrecognizedClassification completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "rejectsUnrecognizedClassification exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
