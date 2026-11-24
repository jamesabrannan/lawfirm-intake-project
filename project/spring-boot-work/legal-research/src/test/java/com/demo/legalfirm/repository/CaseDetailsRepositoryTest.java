package com.demo.legalfirm.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.details.ClientInput;
import com.demo.legalfirm.model.caseinputs.details.StaffInput;
import com.demo.legalfirm.service.MemorandumWorkService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Verifies requested case-detail joins against script-initialized in-memory SQLite.
 *
 * @file CaseDetailsRepositoryTest.java
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
@Transactional
class CaseDetailsRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDetailsRepositoryTest.class);

    @Autowired CaseDetailsRepository repository;

    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired MemorandumWorkService memorandumWorkService;

    @Test
    void retrievesRequestedCaseWithCompleteClientStaffAndIntakeDetails() {
        LOGGER.trace("retrievesRequestedCaseWithCompleteClientStaffAndIntakeDetails() entered");
        try {
            List<CaseDetails> cases = repository.findRequestedForStaff("riley.morgan@example.com");

            // Both seeded requests belong to Riley; inspect Mateo by ID rather than list position.
            assertThat(cases).extracting(CaseDetails::getMemorandumId).containsExactly(1L, 2L);
            CaseDetails details =
                    cases.stream()
                            .filter(item -> item.getMemorandumId() == 2)
                            .findFirst()
                            .orElseThrow();
            assertThat(details.getMemorandumId()).isEqualTo(2);
            assertThat(details.getMemorandumStatus()).isEqualTo("requested");
            assertThat(details.getClient())
                    .usingRecursiveComparison()
                    .isEqualTo(
                            new ClientInput(
                                    "C-102",
                                    "Mateo",
                                    "Santos",
                                    "202-555-0102",
                                    "mateo.santos@example.com",
                                    "200 Sample Lane",
                                    "Example City",
                                    "MD",
                                    "00000"));
            assertThat(details.getRequestor())
                    .usingRecursiveComparison()
                    .isEqualTo(
                            new StaffInput(
                                    "S-004",
                                    "Riley Morgan",
                                    "riley.morgan@example.com",
                                    "MEMORANDUM_PREPARER"));
            assertThat(details.getPreparer())
                    .usingRecursiveComparison()
                    .isEqualTo(
                            new StaffInput(
                                    "S-002",
                                    "Doug Smith",
                                    "doug_smith@example.com",
                                    "INTAKE_PREPARER"));
            assertThat(details.getIntake().getIntakeId()).isEqualTo("I-205");
            assertThat(details.getIntake().getSourceId()).isEqualTo("INTAKE:I-205");
            assertThat(details.getIntake().getSummary())
                    .contains(
                            "Client reports that he has diabetes",
                            "Those records have not been provided.");
            assertThat(details.getIntake().getDocumentFilenames())
                    .containsExactly(
                            "I-205-uscis-receipt-notice.pdf",
                            "I-205-maryland-uniform-traffic-citation.pdf",
                            "I-205-maryland-district-court-trial-notice.pdf");
            assertThat(details.getIntake().getExpectedDocumentTypes())
                    .containsExactly(
                            "notice-to-appear",
                            "notice-of-hearing",
                            "uscis-receipt-notice",
                            "maryland-uniform-traffic-citation",
                            "maryland-district-court-trial-notice");
            assertThat(JsonMapper.builder().build().writeValueAsString(details))
                    .doesNotContain("\"password\"", "\"ssn\"", "DEMO-SSN");
            assertThat(
                            jdbcTemplate.queryForObject(
                                    "SELECT file FROM pragma_database_list WHERE name = 'main'",
                                    String.class))
                    .isEmpty();
            LOGGER.trace("retrievesRequestedCaseWithCompleteClientStaffAndIntakeDetails completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "retrievesRequestedCaseWithCompleteClientStaffAndIntakeDetails exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void excludesCompletedAndUnrequestedMemorandums() {
        LOGGER.trace("excludesCompletedAndUnrequestedMemorandums() entered");
        try {
            jdbcTemplate.update(
                    "UPDATE research_memorandums SET research_memorandum_status = NULL WHERE"
                            + " intake_id = 'I-204'");
            jdbcTemplate.update(
                    """
                    UPDATE research_memorandums SET research_memorandum_status = 'completed'
                    WHERE intake_id = 'I-205'
                    """);

            assertThat(repository.findRequestedForStaff("riley.morgan@example.com")).isEmpty();
            LOGGER.trace("excludesCompletedAndUnrequestedMemorandums completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "excludesCompletedAndUnrequestedMemorandums exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void returnsMultipleRequestedCasesInMemorandumOrder() {
        LOGGER.trace("returnsMultipleRequestedCasesInMemorandumOrder() entered");
        try {
            // Exercise the actual demo fixtures: two requested matters assigned to Riley.
            List<CaseDetails> cases = repository.findRequestedForStaff("riley.morgan@example.com");

            assertThat(cases).extracting(CaseDetails::getMemorandumId).containsExactly(1L, 2L);
            assertThat(cases)
                    .extracting(details -> details.getClient().getContactId())
                    .containsExactly("C-101", "C-102");
            assertThat(cases.get(0).getRequestor().getStaffId()).isEqualTo("S-004");
            assertThat(cases.get(0).getPreparer().getStaffId()).isEqualTo("S-001");
            LOGGER.trace("returnsMultipleRequestedCasesInMemorandumOrder completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "returnsMultipleRequestedCasesInMemorandumOrder exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void usesUpdatedIntakeAssignmentsForWorkSelection() {
        LOGGER.trace("usesUpdatedIntakeAssignmentsForWorkSelection() entered");
        try {
            jdbcTemplate.update(
                    """
                    UPDATE active_intakes SET assigned_paralegal_id = 'S-003',
                                              assigned_lawclerk_id = 'S-001'
                    WHERE intake_id = 'I-205'
                    """);

            assertThat(repository.findRequestedForStaff("riley.morgan@example.com"))
                    .extracting(CaseDetails::getMemorandumId)
                    .containsExactly(1L);
            CaseDetails details =
                    repository.findRequestedForStaff("jordan.ellis@example.com").get(0);

            assertThat(details.getRequestor().getStaffId()).isEqualTo("S-003");
            assertThat(details.getPreparer().getStaffId()).isEqualTo("S-001");
            assertThat(details.getClient().getContactId()).isEqualTo("C-102");
            LOGGER.trace("usesUpdatedIntakeAssignmentsForWorkSelection completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "usesUpdatedIntakeAssignmentsForWorkSelection exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void separatesRequestedWorkByAssignedStaff() {
        LOGGER.trace("separatesRequestedWorkByAssignedStaff() entered");
        try {
            // This isolation scenario deliberately assigns one of Riley's cases to Jordan.
            jdbcTemplate.update(
                    "UPDATE active_intakes SET assigned_paralegal_id = 'S-003' WHERE intake_id ="
                            + " 'I-204'");

            assertThat(repository.findRequestedForStaff("riley.morgan@example.com"))
                    .extracting(CaseDetails::getMemorandumId)
                    .containsExactly(2L);
            assertThat(repository.findRequestedForStaff("jordan.ellis@example.com"))
                    .extracting(CaseDetails::getMemorandumId)
                    .containsExactly(1L);
            assertThat(repository.findRequestedForStaff("doug_smith@example.com")).isEmpty();
            assertThat(repository.findRequestedForStaff("unknown@example.com")).isEmpty();
            assertThat(repository.findRequestedForStaff("' OR 1=1 --")).isEmpty();
            LOGGER.trace("separatesRequestedWorkByAssignedStaff completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "separatesRequestedWorkByAssignedStaff exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    @WithUserDetails("riley.morgan@example.com")
    void selectsCurrentStaffWorkUsingDatabaseAuthenticatedIdentity() {
        LOGGER.trace("selectsCurrentStaffWorkUsingDatabaseAuthenticatedIdentity() entered");
        try {
            assertThat(memorandumWorkService.findRequestedForCurrentStaff())
                    .extracting(CaseDetails::getMemorandumId)
                    .containsExactly(1L, 2L);
            LOGGER.trace("selectsCurrentStaffWorkUsingDatabaseAuthenticatedIdentity completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "selectsCurrentStaffWorkUsingDatabaseAuthenticatedIdentity exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    @WithUserDetails("jordan.ellis@example.com")
    void currentStaffWithNoRequestedWorkReceivesNothing() {
        LOGGER.trace("currentStaffWithNoRequestedWorkReceivesNothing() entered");
        try {
            assertThat(memorandumWorkService.findRequestedForCurrentStaff()).isEmpty();
            LOGGER.trace("currentStaffWithNoRequestedWorkReceivesNothing completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "currentStaffWithNoRequestedWorkReceivesNothing exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    @WithUserDetails("doug_smith@example.com")
    void assignedPreparerDoesNotReceiveParalegalMemorandumWork() {
        LOGGER.trace("assignedPreparerDoesNotReceiveParalegalMemorandumWork() entered");
        try {
            assertThatThrownBy(memorandumWorkService::findRequestedForCurrentStaff)
                    .isInstanceOf(AccessDeniedException.class);
            LOGGER.trace("assignedPreparerDoesNotReceiveParalegalMemorandumWork completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "assignedPreparerDoesNotReceiveParalegalMemorandumWork exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " |  | "})
    void returnsEmptyListsForEmptyOrBlankDelimitedFields(String value) {
        LOGGER.trace(
                "returnsEmptyListsForEmptyOrBlankDelimitedFields(value={}) entered",
                LogSummary.describe(value));
        try {
            jdbcTemplate.update(
                    """
                    UPDATE active_intakes SET document_filenames = ?, expected_document_types = ?
                    WHERE intake_id = 'I-205'
                    """,
                    value,
                    value);

            CaseDetails details = requestedCase(2);

            assertThat(details.getIntake().getDocumentFilenames()).isEmpty();
            assertThat(details.getIntake().getExpectedDocumentTypes()).isEmpty();
            LOGGER.trace("returnsEmptyListsForEmptyOrBlankDelimitedFields completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "returnsEmptyListsForEmptyOrBlankDelimitedFields exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void trimsDelimitedItemsWithoutConstructingDocumentNames() {
        LOGGER.trace("trimsDelimitedItemsWithoutConstructingDocumentNames() entered");
        try {
            jdbcTemplate.update(
                    """
                    UPDATE active_intakes SET document_filenames = ?, expected_document_types = ?
                    WHERE intake_id = 'I-205'
                    """,
                    " custom-name.pdf | | another.pdf |",
                    " citation | receipt | ");

            CaseDetails details = requestedCase(2);

            assertThat(details.getIntake().getDocumentFilenames())
                    .containsExactly("custom-name.pdf", "another.pdf");
            assertThat(details.getIntake().getExpectedDocumentTypes())
                    .containsExactly("citation", "receipt");
            LOGGER.trace("trimsDelimitedItemsWithoutConstructingDocumentNames completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "trimsDelimitedItemsWithoutConstructingDocumentNames exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    private CaseDetails requestedCase(long memorandumId) {
        LOGGER.trace("requestedCase(memorandumId={}) entered", LogSummary.describe(memorandumId));
        try {

            var loggedResult =
                    repository.findRequestedForStaff("riley.morgan@example.com").stream()
                            .filter(details -> details.getMemorandumId() == memorandumId)
                            .findFirst()
                            .orElseThrow();
            LOGGER.trace("requestedCase returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "requestedCase exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
