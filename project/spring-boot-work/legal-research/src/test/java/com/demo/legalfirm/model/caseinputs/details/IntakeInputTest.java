package com.demo.legalfirm.model.caseinputs.details;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Verifies JSON conversion of intake data and document checklists.
 *
 * @file IntakeInputTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class IntakeInputTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntakeInputTest.class);

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void preservesDatabaseFieldsThroughJsonRoundTrip() {
        LOGGER.trace("preservesDatabaseFieldsThroughJsonRoundTrip() entered");
        try {
            IntakeInput input =
                    new IntakeInput(
                            "INTAKE:I-205",
                            "I-205",
                            "Client reports confusion; cause is unverified.",
                            List.of("receipt.pdf", "citation.pdf"),
                            List.of("Receipt", "Citation", "Hearing notice"));

            String json = mapper.writeValueAsString(input);
            IntakeInput restored = mapper.readValue(json, IntakeInput.class);

            assertThat(restored).usingRecursiveComparison().isEqualTo(input);
            assertThat(mapper.readTree(json).get("sourceId").asString()).isEqualTo("INTAKE:I-205");
            assertThat(restored.getDocumentFilenames()).hasSize(2);
            assertThat(restored.getExpectedDocumentTypes()).hasSize(3);
            LOGGER.trace("preservesDatabaseFieldsThroughJsonRoundTrip completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "preservesDatabaseFieldsThroughJsonRoundTrip exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
