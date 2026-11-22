package com.demo.legalfirm.model.caseinputs.documents;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.json.JsonMapper;

/**
 * Verifies JSON conversion of PDF page numbers and extracted text.
 *
 * @file DocumentPageInputTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class DocumentPageInputTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentPageInputTest.class);

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void preservesPageNumberAndExtractedTextIncludingJsonSpecialCharacters() {
        LOGGER.trace("preservesPageNumberAndExtractedTextIncludingJsonSpecialCharacters() entered");
        try {
            DocumentPageInput input =
                    new DocumentPageInput(
                            2,
                            "Client said \"confused\".\n"
                                    + "Path: C:\\documents\\citation.pdf\n"
                                    + "Nombre: Jos�");

            String json = mapper.writeValueAsString(input);
            DocumentPageInput restored = mapper.readValue(json, DocumentPageInput.class);

            assertThat(restored).usingRecursiveComparison().isEqualTo(input);
            assertThat(mapper.readTree(json).get("pageNumber").asInt()).isEqualTo(2);
            assertThat(restored.getText()).isEqualTo(input.getText());
            LOGGER.trace(
                    "preservesPageNumberAndExtractedTextIncludingJsonSpecialCharacters completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "preservesPageNumberAndExtractedTextIncludingJsonSpecialCharacters exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
