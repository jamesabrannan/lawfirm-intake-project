package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

/**
 * Verifies both seeded Riley cases assemble real classpath PDFs into serializable AI input.
 *
 * @file CaseInputServiceIntegrationTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
// Tests require two requested cases independently of the selected live demo case.
@org.springframework.test.context.jdbc.Sql(statements =
        "UPDATE research_memorandums SET research_memorandum_status = 'requested' WHERE id IN (1, 2)")
@SpringBootTest(properties = "spring.ai.openai.api-key=integration-test-placeholder")
@org.springframework.transaction.annotation.Transactional
class CaseInputServiceIntegrationTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CaseInputServiceIntegrationTest.class);

    @Autowired MemorandumWorkService workService;

    @Autowired CaseInputService caseInputService;

    @Test
    @WithUserDetails("riley.morgan@example.com")
    void assemblesBothCasesWithTheirOwnClerksAndRealExtractedPages() throws IOException {
        LOGGER.trace("assemblesBothCasesWithTheirOwnClerksAndRealExtractedPages entered");
        List<CaseDetails> cases = workService.findRequestedForCurrentStaff();
        assertThat(cases).extracting(CaseDetails::getMemorandumId).containsExactly(1L, 2L);

        CaseInput elena = caseInputService.assemble(cases.get(0));
        CaseInput mateo = caseInputService.assemble(cases.get(1));

        assertThat(elena.getDetails().getPreparer().getStaffId()).isEqualTo("S-001");
        assertThat(mateo.getDetails().getPreparer().getStaffId()).isEqualTo("S-002");
        assertThat(elena.getDocuments()).hasSize(3);
        assertThat(mateo.getDocuments()).hasSize(4);
        assertThat(elena.getMissingDocumentFilenames()).isEmpty();
        assertThat(mateo.getMissingDocumentFilenames()).isEmpty();
        for (CaseInput input : List.of(elena, mateo)) {
            String intakeId = input.getDetails().getIntake().getIntakeId();
            assertThat(input.getDocuments())
                    .extracting(DocumentInput::getFilename)
                    .allMatch(name -> name.startsWith(intakeId + "-"));
            assertThat(input.getDocuments())
                    .allSatisfy(
                            document -> {
                                assertThat(document.getPages()).isNotEmpty();
                                assertThat(document.getPages().get(0).getPageNumber()).isEqualTo(1);
                                assertThat(document.getPages())
                                        .anySatisfy(
                                                page -> assertThat(page.getText()).isNotBlank());
                            });
            JsonMapper mapper = JsonMapper.builder().build();
            String json = mapper.writeValueAsString(input);
            assertThat(mapper.readValue(json, CaseInput.class))
                    .usingRecursiveComparison()
                    .isEqualTo(input);
            assertThat(mapper.readTree(json).get("documents").isArray()).isTrue();
        }
        LOGGER.trace("assemblesBothCasesWithTheirOwnClerksAndRealExtractedPages completed");
    }
}
