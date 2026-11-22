package com.demo.legalfirm.model.caseinputs.documents;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.legalfirm.logging.LogSummary;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Verifies JSON conversion of document identity, type, and page order.
 *
 * @file DocumentInputTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class DocumentInputTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentInputTest.class);

    private final JsonMapper mapper = JsonMapper.builder().build();

    @ParameterizedTest
    @EnumSource(DocumentType.class)
    void preservesDocumentTypeSourceAndPageOrder(DocumentType type) {
        LOGGER.trace(
                "preservesDocumentTypeSourceAndPageOrder(type={}) entered",
                LogSummary.describe(type));
        try {
            DocumentInput input =
                    new DocumentInput(
                            "DOC1",
                            "source.pdf",
                            type,
                            List.of(
                                    new DocumentPageInput(1, "First page."),
                                    new DocumentPageInput(2, "Second page.")));

            String json = mapper.writeValueAsString(input);
            DocumentInput restored = mapper.readValue(json, DocumentInput.class);

            assertThat(restored).usingRecursiveComparison().isEqualTo(input);
            assertThat(mapper.readTree(json).get("documentType").asString()).isEqualTo(type.name());
            assertThat(restored.getPages())
                    .extracting(DocumentPageInput::getPageNumber)
                    .containsExactly(1, 2);
            LOGGER.trace("preservesDocumentTypeSourceAndPageOrder completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "preservesDocumentTypeSourceAndPageOrder exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
