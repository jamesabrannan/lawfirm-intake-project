package com.demo.legalfirm.model.caseinputs;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.details.ClientInput;
import com.demo.legalfirm.model.caseinputs.details.IntakeInput;
import com.demo.legalfirm.model.caseinputs.details.StaffInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentPageInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentType;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Verifies JSON conversion of assembled case inputs and nested documents.
 *
 * @file CaseInputTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class CaseInputTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseInputTest.class);

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void preservesCompleteCaseWithMultipleDocumentsAndMissingFiles() {
        LOGGER.trace("preservesCompleteCaseWithMultipleDocumentsAndMissingFiles() entered");
        try {
            IntakeInput intake =
                    new IntakeInput(
                            "INTAKE:I-205",
                            "I-205",
                            "Client account.",
                            List.of("brief.pdf", "citation.pdf", "receipt.pdf"),
                            List.of("Citation", "Receipt"));
            CaseInput input =
                    new CaseInput(
                            createDetails(intake),
                            List.of(
                                    new DocumentInput(
                                            "BRIEF",
                                            "brief.pdf",
                                            DocumentType.INTAKE_BRIEF,
                                            List.of(new DocumentPageInput(1, "Brief text."))),
                                    new DocumentInput(
                                            "DOC1",
                                            "citation.pdf",
                                            DocumentType.SUPPORTING_DOCUMENT,
                                            List.of(new DocumentPageInput(1, "Citation text.")))),
                            List.of("receipt.pdf"));

            String json = mapper.writeValueAsString(input);
            CaseInput restored = mapper.readValue(json, CaseInput.class);

            assertThat(restored).usingRecursiveComparison().isEqualTo(input);
            assertThat(restored.getDetails().getClient().getContactId()).isEqualTo("C-102");
            assertThat(restored.getDetails().getIntake().getIntakeId()).isEqualTo("I-205");
            assertThat(mapper.readTree(json).get("details").isObject()).isTrue();
            assertThat(restored.getDocuments())
                    .extracting(DocumentInput::getSourceId)
                    .containsExactly("BRIEF", "DOC1");
            assertThat(restored.getMissingDocumentFilenames()).containsExactly("receipt.pdf");
            assertThat(mapper.readTree(json).get("documents").isArray()).isTrue();
            LOGGER.trace("preservesCompleteCaseWithMultipleDocumentsAndMissingFiles completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "preservesCompleteCaseWithMultipleDocumentsAndMissingFiles exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void preservesEmptyDocumentCollections() {
        LOGGER.trace("preservesEmptyDocumentCollections() entered");
        try {
            CaseInput input =
                    new CaseInput(
                            createDetails(
                                    new IntakeInput(
                                            "INTAKE:I-205",
                                            "I-205",
                                            "No files supplied.",
                                            List.of(),
                                            List.of())),
                            List.of(),
                            List.of());

            CaseInput restored =
                    mapper.readValue(mapper.writeValueAsString(input), CaseInput.class);

            assertThat(restored).usingRecursiveComparison().isEqualTo(input);
            assertThat(restored.getDocuments()).isEmpty();
            assertThat(restored.getMissingDocumentFilenames()).isEmpty();
            LOGGER.trace("preservesEmptyDocumentCollections completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "preservesEmptyDocumentCollections exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    private CaseDetails createDetails(IntakeInput intake) {
        LOGGER.trace("createDetails(intake={}) entered", LogSummary.describe(intake));
        try {

            var loggedResult =
                    new CaseDetails(
                            2,
                            "requested",
                            new ClientInput(
                                    "C-102",
                                    "Mateo",
                                    "Santos",
                                    "202-555-0102",
                                    "mateo.santos@example.com",
                                    "200 Sample Lane",
                                    "Example City",
                                    "MD",
                                    "00000"),
                            new StaffInput(
                                    "S-004",
                                    "Riley Morgan",
                                    "riley.morgan@example.com",
                                    "MEMORANDUM_PREPARER"),
                            new StaffInput(
                                    "S-002",
                                    "Doug Smith",
                                    "doug_smith@example.com",
                                    "INTAKE_PREPARER"),
                            intake);
            LOGGER.trace("createDetails returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "createDetails exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
