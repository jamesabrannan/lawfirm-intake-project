package com.demo.legalfirm.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * Checks conditional completion updates against an isolated in-memory SQLite database.
 *
 * @file MemorandumCompletionRepositoryTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class MemorandumCompletionRepositoryTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumCompletionRepositoryTest.class);
    private SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbc;
    private MemorandumCompletionRepository repository;

    @BeforeEach
    void setUp() {
        LOGGER.trace("setUp entered");
        dataSource = new SingleConnectionDataSource("jdbc:sqlite::memory:", true);
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute(
                "CREATE TABLE research_memorandums (id INTEGER PRIMARY KEY, intake_id TEXT,"
                    + " research_memorandum_status TEXT, filename TEXT)");
        jdbc.update("INSERT INTO research_memorandums VALUES (2, 'I-test', 'requested', NULL)");
        repository = new MemorandumCompletionRepository(jdbc);
        LOGGER.trace("setUp completed");
    }

    @AfterEach
    void close() {
        LOGGER.trace("close entered");
        dataSource.destroy();
        LOGGER.trace("close completed");
    }

    @Test
    void storesFilenameAndStopsSelectingCompletedWork() {
        LOGGER.trace("storesFilenameAndStopsSelectingCompletedWork entered");
        repository.markCompleted(2L, "I-test", "memorandum-2-test.pdf");
        assertThat(
                        jdbc.queryForObject(
                                "SELECT filename FROM research_memorandums WHERE id=2",
                                String.class))
                .isEqualTo("memorandum-2-test.pdf");
        assertThat(
                        jdbc.queryForObject(
                                "SELECT count(*) FROM research_memorandums WHERE"
                                    + " research_memorandum_status='requested'",
                                Integer.class))
                .isZero();
        assertThatThrownBy(() -> repository.markCompleted(2L, "I-test", "other.pdf"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(
                        jdbc.queryForObject(
                                "SELECT filename FROM research_memorandums WHERE id=2",
                                String.class))
                .isEqualTo("memorandum-2-test.pdf");
        LOGGER.trace("storesFilenameAndStopsSelectingCompletedWork completed");
    }

    @Test
    void wrongIntakeCannotCompleteMemorandum() {
        LOGGER.trace("wrongIntakeCannotCompleteMemorandum entered");
        assertThatThrownBy(() -> repository.markCompleted(2L, "wrong", "memorandum.pdf"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(
                        jdbc.queryForObject(
                                "SELECT research_memorandum_status FROM research_memorandums WHERE"
                                    + " id=2",
                                String.class))
                .isEqualTo("requested");
        LOGGER.trace("wrongIntakeCannotCompleteMemorandum completed");
    }
}
