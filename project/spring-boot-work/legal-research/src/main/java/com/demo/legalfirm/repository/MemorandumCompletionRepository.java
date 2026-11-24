package com.demo.legalfirm.repository;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Records a saved PDF and completes only the matching still-requested memorandum.
 *
 * @file MemorandumCompletionRepository.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Repository
public class MemorandumCompletionRepository {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumCompletionRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public MemorandumCompletionRepository(JdbcTemplate jdbcTemplate) {
        LOGGER.trace(
                "MemorandumCompletionRepository(jdbcTemplate={}) entered",
                LogSummary.describe(jdbcTemplate));
        this.jdbcTemplate = jdbcTemplate;
        LOGGER.trace("MemorandumCompletionRepository completed");
    }

    public void markCompleted(long memorandumId, String intakeId, String filename) {
        LOGGER.debug(
                "markCompleted(memorandumId={}, intakeId={}, filename={}) entered",
                memorandumId,
                LogSummary.describe(intakeId),
                LogSummary.describe(filename));
        try {
            if (intakeId == null || intakeId.isBlank() || filename == null || filename.isBlank()) {
                throw new IllegalArgumentException("Intake ID and saved filename are required");
            }
            int changed =
                    jdbcTemplate.update(
                            """
UPDATE research_memorandums SET research_memorandum_status = 'completed', filename = ?
WHERE id = ? AND intake_id = ? AND research_memorandum_status = 'requested'
""",
                            filename,
                            memorandumId,
                            intakeId);
            if (changed != 1)
                throw new IllegalStateException(
                        "Memorandum is no longer requested or does not match the intake");
            LOGGER.info("Memorandum marked completed; memorandumId={}", memorandumId);
            LOGGER.debug("markCompleted completed");
        } catch (RuntimeException failure) {
            LOGGER.warn(
                    "Memorandum completion update failed; type={}",
                    failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
