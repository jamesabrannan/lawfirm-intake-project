package com.demo.legalfirm.repository;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.caseinputs.details.ClientInput;
import com.demo.legalfirm.model.caseinputs.details.IntakeInput;
import com.demo.legalfirm.model.caseinputs.details.StaffInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Retrieves case details for requested memorandums through JDBC.
 *
 * @file CaseDetailsRepository.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Repository
public class CaseDetailsRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDetailsRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public CaseDetailsRepository(JdbcTemplate jdbcTemplate) {
        LOGGER.trace(
                "CaseDetailsRepository(jdbcTemplate={}) entered",
                LogSummary.describe(jdbcTemplate));
        try {
            this.jdbcTemplate = jdbcTemplate;
            LOGGER.trace("CaseDetailsRepository completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "CaseDetailsRepository exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    /**
     * Uses the intake's assigned paralegal as requestor and law clerk as preparer. These roles are
     * a demo convention, not a history of who submitted a request.
     *
     * <p>Spring Boot initializes the demo's in-memory SQLite database from schema.sql and data.sql.
     * This repository uses that datasource and does not open a separate database. Passwords and
     * SSNs are not selected. PDF processing is separate.
     *
     * @param staffEmail identity supplied by the work-selection layer; restricts results to this
     *     staff member's requestor assignments
     */
    public List<CaseDetails> findRequestedForStaff(String staffEmail) {
        LOGGER.debug(
                "findRequestedForStaff(staffEmail={}) entered", LogSummary.describe(staffEmail));
        try {

            LOGGER.info("Loading requested case details for staff (identity redacted)");
            var loggedResult =
                    jdbcTemplate.query(
                            """
SELECT m.id AS memorandum_id, m.research_memorandum_status,
       i.intake_id, i.summary, i.document_filenames, i.expected_document_types,
       c.contact_id, c.first_name, c.last_name, c.phone, c.email AS client_email,
       c.street_address, c.city, c.state, c.postal_code,
       requestor.staff_id AS requestor_id,
       requestor.staff_name AS requestor_name,
       requestor.email AS requestor_email, requestor.role AS requestor_role,
       preparer.staff_id AS preparer_id,
       preparer.staff_name AS preparer_name,
       preparer.email AS preparer_email, preparer.role AS preparer_role
FROM research_memorandums m
JOIN active_intakes i ON i.intake_id = m.intake_id
JOIN clients c ON c.contact_id = i.contact_id
JOIN users requestor ON requestor.staff_id = i.assigned_paralegal_id
JOIN users preparer ON preparer.staff_id = i.assigned_lawclerk_id
WHERE m.research_memorandum_status = ?
  AND requestor.email = ?
ORDER BY m.id
""",
                            (rs, rowNum) -> mapCaseDetails(rs),
                            "requested",
                            staffEmail);
            LOGGER.info("Loaded {} requested case details", loggedResult.size());
            LOGGER.debug("findRequestedForStaff returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "findRequestedForStaff exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    private CaseDetails mapCaseDetails(ResultSet rs) throws SQLException {
        LOGGER.debug("mapCaseDetails(rs={}) entered", LogSummary.describe(rs));
        try {
            String intakeId = rs.getString("intake_id");
            IntakeInput intake =
                    new IntakeInput(
                            "INTAKE:" + intakeId,
                            intakeId,
                            rs.getString("summary"),
                            splitList(rs.getString("document_filenames")),
                            splitList(rs.getString("expected_document_types")));
            ClientInput client =
                    new ClientInput(
                            rs.getString("contact_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("phone"),
                            rs.getString("client_email"),
                            rs.getString("street_address"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("postal_code"));
            StaffInput requestor =
                    new StaffInput(
                            rs.getString("requestor_id"), rs.getString("requestor_name"),
                            rs.getString("requestor_email"), rs.getString("requestor_role"));
            StaffInput preparer =
                    new StaffInput(
                            rs.getString("preparer_id"), rs.getString("preparer_name"),
                            rs.getString("preparer_email"), rs.getString("preparer_role"));

            LOGGER.debug("Mapped case details; memorandumId={}", rs.getLong("memorandum_id"));
            var loggedResult =
                    new CaseDetails(
                            rs.getLong("memorandum_id"),
                            rs.getString("research_memorandum_status"),
                            client,
                            requestor,
                            preparer,
                            intake);
            LOGGER.debug("mapCaseDetails returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "mapCaseDetails exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    private List<String> splitList(String value) {
        LOGGER.debug("splitList(value={}) entered", LogSummary.describe(value));
        try {

            var loggedResult =
                    Arrays.stream(value.split("\\|"))
                            .map(String::trim)
                            .filter(item -> !item.isEmpty())
                            .toList();
            LOGGER.debug("splitList returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "splitList exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
