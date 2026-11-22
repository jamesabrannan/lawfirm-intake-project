package com.demo.legalfirm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;

import com.demo.legalfirm.logging.LogSummary;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Checks the complete path from an incoming request, through the security checks, to the database
 * and synchronous memorandum preparation entry point.
 *
 * <p>MockMvc sends requests inside the test process, so no browser or running web server is needed.
 * The application's actual security rules and database lookups still run; the tests do not pretend
 * that a user has already logged in.
 *
 * @file SecurityIntegrationTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
// This placeholder lets the AI configuration start. These tests make no AI calls.
@SpringBootTest(properties = "spring.ai.openai.api-key=integration-test-placeholder")
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityIntegrationTest.class);

    private static final String ENDPOINT = "/api/memorandums/prepare";

    // Use the application's actual in-memory datasource configuration. Spring Boot
    // initializes it from schema.sql and data.sql and closes it with the context.

    // Spring supplies the request tool with the application's security filters enabled.
    @Autowired MockMvc mockMvc;

    // This database access tool lets one test add a user with a disallowed role.
    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired HikariDataSource dataSource;

    @Test
    void applicationUsesSeededInMemoryDatabaseAcrossJdbcCalls() {
        LOGGER.trace("applicationUsesSeededInMemoryDatabaseAcrossJdbcCalls() entered");
        try {
            assertThat(dataSource.getJdbcUrl()).isEqualTo("jdbc:sqlite::memory:");
            assertThat(dataSource.getMaximumPoolSize()).isEqualTo(1);
            assertThat(dataSource.getMinimumIdle()).isEqualTo(1);
            assertThat(dataSource.getMaxLifetime()).isZero();
            assertThat(dataSource.getIdleTimeout()).isZero();
            assertThat(
                            jdbcTemplate.queryForObject(
                                    "SELECT file FROM pragma_database_list WHERE name = 'main'",
                                    String.class))
                    .isEmpty();
            assertThat(jdbcTemplate.queryForObject("PRAGMA foreign_keys", Integer.class))
                    .isEqualTo(1);
            assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class))
                    .isEqualTo(4);
            assertThat(
                            jdbcTemplate.queryForObject(
                                    "SELECT COUNT(*) FROM active_intakes", Integer.class))
                    .isEqualTo(2);
            // Each JdbcTemplate call returns its connection to the pool. A subsequent
            // call must still see the same initialized database, not a new empty one.
            assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class))
                    .isEqualTo(4);
            LOGGER.trace("applicationUsesSeededInMemoryDatabaseAcrossJdbcCalls completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "applicationUsesSeededInMemoryDatabaseAcrossJdbcCalls exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    // A request without a login must get 401 (authentication required).
    // The response must also tell the client that it accepts HTTP Basic credentials.
    @Test
    void missingCredentialsAreRejected() throws Exception {
        LOGGER.trace("missingCredentialsAreRejected() entered");
        try {
            mockMvc.perform(post(ENDPOINT).with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(header().string("WWW-Authenticate", startsWith("Basic ")));
            LOGGER.trace("missingCredentialsAreRejected completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "missingCredentialsAreRejected exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    // An existing email address is insufficient: the password must also match
    // the value read from the users table. A wrong password must get 401.
    @Test
    void wrongPasswordIsRejected() throws Exception {
        LOGGER.trace("wrongPasswordIsRejected() entered");
        try {
            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .with(httpBasic("jordan.ellis@example.com", "wrong")))
                    .andExpect(status().isUnauthorized());
            LOGGER.trace("wrongPasswordIsRejected completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "wrongPasswordIsRejected exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    // Knowing the shared demo password must not allow an unknown email to log in.
    @Test
    void unknownUserIsRejected() throws Exception {
        LOGGER.trace("unknownUserIsRejected() entered");
        try {
            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .with(httpBasic("unknown@example.com", "password")))
                    .andExpect(status().isUnauthorized());
            LOGGER.trace("unknownUserIsRejected completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "unknownUserIsRejected exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    // Check two memorandum preparers and two intake preparers with their real JDBC credentials.
    // httpBasic adds an email/password header to the request. Spring Security must
    // check it against the users table before allowing access.
    @ParameterizedTest
    @ValueSource(
            strings = {
                "jordan.ellis@example.com", "riley.morgan@example.com",
                "maya.chen@example.com", "doug_smith@example.com"
            })
    void seededUsersAuthenticateThroughJdbc(String email) throws Exception {
        LOGGER.trace(
                "seededUsersAuthenticateThroughJdbc(email={}) entered", LogSummary.describe(email));
        try {
            // Only memorandum preparers may access this endpoint. Riley has requested
            // work; Jordan has none. Intake preparers authenticate but receive 403.
            if (email.equals("riley.morgan@example.com")) {
                // Authentication succeeds and reaches the explicitly unimplemented pipeline.
                mockMvc.perform(post(ENDPOINT).with(csrf()).with(httpBasic(email, "password")))
                        .andExpect(status().isNotImplemented());
            } else if (email.equals("jordan.ellis@example.com")) {
                mockMvc.perform(post(ENDPOINT).with(csrf()).with(httpBasic(email, "password")))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("no_work"))
                        .andExpect(jsonPath("$.memorandums.length()").value(0));
            } else {
                mockMvc.perform(post(ENDPOINT).with(csrf()).with(httpBasic(email, "password")))
                        .andExpect(status().isForbidden());
            }
            LOGGER.trace("seededUsersAuthenticateThroughJdbc completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "seededUsersAuthenticateThroughJdbc exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    @Transactional
    void unfinishedPreparationDoesNotReportCreationOrChangeStatus() throws Exception {
        LOGGER.trace("unfinishedPreparationDoesNotReportCreationOrChangeStatus() entered");
        try {
            // Give both authorized staff requested work for this failure scenario only.
            jdbcTemplate.update(
                    "UPDATE active_intakes SET assigned_paralegal_id = 'S-003' WHERE intake_id ="
                            + " 'I-204'");

            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .with(httpBasic("jordan.ellis@example.com", "password")))
                    .andExpect(status().isNotImplemented());
            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .with(httpBasic("riley.morgan@example.com", "password")))
                    .andExpect(status().isNotImplemented());

            assertThat(
                            jdbcTemplate.queryForObject(
                                    """
SELECT COUNT(*) FROM research_memorandums
WHERE research_memorandum_status = 'requested' AND filename IS NULL
""",
                                    Integer.class))
                    .isEqualTo(2);
            LOGGER.trace("unfinishedPreparationDoesNotReportCreationOrChangeStatus completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "unfinishedPreparationDoesNotReportCreationOrChangeStatus exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void assignmentListingEndpointHasBeenRemoved() throws Exception {
        LOGGER.trace("assignmentListingEndpointHasBeenRemoved() entered");
        try {
            mockMvc.perform(
                            get("/api/paralegal-assignments")
                                    .with(httpBasic("riley.morgan@example.com", "password")))
                    .andExpect(status().isNotFound());
            LOGGER.trace("assignmentListingEndpointHasBeenRemoved completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "assignmentListingEndpointHasBeenRemoved exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    // A correct login establishes who someone is; their role determines access.
    // This user has valid credentials but neither allowed role, so must get
    // 403 (identity accepted, access refused), rather than 401.
    @Test
    void authenticatedUserWithUnapprovedRoleIsForbidden() throws Exception {
        LOGGER.trace("authenticatedUserWithUnapprovedRoleIsForbidden() entered");
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO users (staff_id, staff_name, email, password, role)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    "TEST-001",
                    "Test User",
                    "test@example.com",
                    "password",
                    "other");
            try {
                mockMvc.perform(
                                post(ENDPOINT)
                                        .with(csrf())
                                        .with(httpBasic("test@example.com", "password")))
                        .andExpect(status().isForbidden());
            } finally {
                // Remove the extra user even if the check fails, keeping tests independent.
                jdbcTemplate.update("DELETE FROM users WHERE staff_id = ?", "TEST-001");
            }
            LOGGER.trace("authenticatedUserWithUnapprovedRoleIsForbidden completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "authenticatedUserWithUnapprovedRoleIsForbidden exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    // Stateless authentication means the server does not keep a logged-in session.
    // Every request must supply credentials, even after an earlier request succeeds.
    @Test
    void authenticationDoesNotCreateASession() throws Exception {
        LOGGER.trace("authenticationDoesNotCreateASession() entered");
        try {
            MvcResult result =
                    mockMvc.perform(
                                    post(ENDPOINT)
                                            .with(csrf())
                                            .with(
                                                    httpBasic(
                                                            "jordan.ellis@example.com",
                                                            "password")))
                            .andExpect(status().isOk())
                            .andReturn();
            // false means "look for an existing session without creating one."
            // A null result confirms that the successful request created no session.
            assertThat(result.getRequest().getSession(false)).isNull();
            mockMvc.perform(post(ENDPOINT).with(csrf())).andExpect(status().isUnauthorized());
            LOGGER.trace("authenticationDoesNotCreateASession completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "authenticationDoesNotCreateASession exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://localhost", "http://localhost:3000", "https://localhost:8443"})
    void allowsApprovedGuiOriginPreflight(String origin) throws Exception {
        LOGGER.trace(
                "allowsApprovedGuiOriginPreflight(origin={}) entered", LogSummary.describe(origin));
        try {
            mockMvc.perform(
                            options("http://localhost:8080" + ENDPOINT)
                                    .header("Origin", origin)
                                    .header("Access-Control-Request-Method", "POST")
                                    .header(
                                            "Access-Control-Request-Headers",
                                            "authorization,content-type,x-csrf-token"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", origin))
                    .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
            LOGGER.trace("allowsApprovedGuiOriginPreflight completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "allowsApprovedGuiOriginPreflight exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "http://localhost.example.com:3000", "null"})
    void rejectsUnapprovedGuiOrigins(String origin) throws Exception {
        LOGGER.trace("rejectsUnapprovedGuiOrigins(origin={}) entered", LogSummary.describe(origin));
        try {
            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .with(httpBasic("jordan.ellis@example.com", "password"))
                                    .header("Origin", origin))
                    .andExpect(status().isForbidden())
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
            mockMvc.perform(
                            options(ENDPOINT)
                                    .header("Origin", origin)
                                    .header("Access-Control-Request-Method", "POST"))
                    .andExpect(status().isForbidden());
            LOGGER.trace("rejectsUnapprovedGuiOrigins completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "rejectsUnapprovedGuiOrigins exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void approvedGuiOriginStillRequiresAuthenticationAndPermission() throws Exception {
        LOGGER.trace("approvedGuiOriginStillRequiresAuthenticationAndPermission() entered");
        try {
            mockMvc.perform(post(ENDPOINT).with(csrf()).header("Origin", "http://localhost:3000"))
                    .andExpect(status().isUnauthorized());
            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .header("Origin", "http://localhost:3000")
                                    .with(httpBasic("doug_smith@example.com", "password")))
                    .andExpect(status().isForbidden());
            mockMvc.perform(
                            post(ENDPOINT)
                                    .with(csrf())
                                    .header("Origin", "http://localhost:3000")
                                    .with(httpBasic("jordan.ellis@example.com", "password")))
                    .andExpect(status().isOk())
                    .andExpect(
                            header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                    .andExpect(jsonPath("$.status").value("no_work"));
            LOGGER.trace("approvedGuiOriginStillRequiresAuthenticationAndPermission completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "approvedGuiOriginStillRequiresAuthenticationAndPermission exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void loggingWritesToStandardOutAndTheConfiguredRollingFile() throws Exception {
        LOGGER.trace("loggingWritesToStandardOutAndTheConfiguredRollingFile() entered");
        try {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
            ConsoleAppender<?> console = (ConsoleAppender<?>) root.getAppender("CONSOLE");
            RollingFileAppender<?> file = (RollingFileAppender<?>) root.getAppender("FILE");
            assertThat(console).isNotNull();
            assertThat(console.isStarted()).isTrue();
            assertThat(console.getTarget()).isEqualTo("System.out");
            assertThat(file).isNotNull();
            assertThat(file.isStarted()).isTrue();
            assertThat(file.getRollingPolicy()).isNotNull();
            assertThat(file.getFile().replace('\\', '/')).endsWith("logs/researchmemorandum.log");
            LOGGER.info("Logging verification marker: console-and-file");
            assertThat(Files.readString(Path.of(file.getFile())))
                    .contains("Logging verification marker: console-and-file");
            LOGGER.trace("loggingWritesToStandardOutAndTheConfiguredRollingFile completed");
        } catch (Exception failure) {
            LOGGER.trace(
                    "Logging destination verification failed; type={}",
                    failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
