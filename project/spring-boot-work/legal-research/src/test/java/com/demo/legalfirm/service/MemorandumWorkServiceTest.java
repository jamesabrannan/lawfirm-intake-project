package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

/**
 * Verifies current-user identity and method authorization through Spring service proxies.
 *
 * @file MemorandumWorkServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@SpringBootTest(properties = "spring.ai.openai.api-key=integration-test-placeholder")
class MemorandumWorkServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumWorkServiceTest.class);

    @MockitoBean CaseDetailsService caseDetailsService;

    @MockitoBean MemorandumPreparationService preparationService;

    @Autowired MemorandumWorkService service;

    @Autowired MemorandumProcessingService processingService;

    @AfterEach
    void clearSecurityContext() {
        LOGGER.trace("clearSecurityContext() entered");
        try {
            SecurityContextHolder.clearContext();
            LOGGER.trace("clearSecurityContext completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "clearSecurityContext exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void usesAuthenticatedEmailAndReturnsAllSelectedCases() {
        LOGGER.trace("usesAuthenticatedEmailAndReturnsAllSelectedCases() entered");
        try {
            List<CaseDetails> cases = List.of(new CaseDetails(), new CaseDetails());
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            UsernamePasswordAuthenticationToken.authenticated(
                                    "riley.morgan@example.com",
                                    null,
                                    AuthorityUtils.createAuthorityList("MEMORANDUM_PREPARER")));
            when(caseDetailsService.findRequestedForStaff("riley.morgan@example.com"))
                    .thenReturn(cases);

            assertThat(service.findRequestedForCurrentStaff()).isSameAs(cases);
            verify(caseDetailsService).findRequestedForStaff("riley.morgan@example.com");
            verifyNoMoreInteractions(caseDetailsService);
            LOGGER.trace("usesAuthenticatedEmailAndReturnsAllSelectedCases completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "usesAuthenticatedEmailAndReturnsAllSelectedCases exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"INTAKE_PREPARER", "other"})
    void otherPermissionsCannotInvokeWorkSelection(String authority) {
        LOGGER.trace(
                "otherPermissionsCannotInvokeWorkSelection(authority={}) entered",
                LogSummary.describe(authority));
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            UsernamePasswordAuthenticationToken.authenticated(
                                    "staff@example.com",
                                    null,
                                    AuthorityUtils.createAuthorityList(authority)));

            assertThatThrownBy(service::findRequestedForCurrentStaff)
                    .isInstanceOf(AccessDeniedException.class);
            verifyNoInteractions(caseDetailsService);
            LOGGER.trace("otherPermissionsCannotInvokeWorkSelection completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "otherPermissionsCannotInvokeWorkSelection exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void rejectsMissingAuthentication() {
        LOGGER.trace("rejectsMissingAuthentication() entered");
        try {
            SecurityContextHolder.clearContext();

            assertThatThrownBy(service::findRequestedForCurrentStaff)
                    .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
            verifyNoInteractions(caseDetailsService);
            LOGGER.trace("rejectsMissingAuthentication completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "rejectsMissingAuthentication exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void rejectsUnauthenticatedIdentity() {
        LOGGER.trace("rejectsUnauthenticatedIdentity() entered");
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            UsernamePasswordAuthenticationToken.unauthenticated(
                                    "riley.morgan@example.com", null));

            assertThatThrownBy(service::findRequestedForCurrentStaff)
                    .isInstanceOfAny(
                            AuthenticationCredentialsNotFoundException.class,
                            AccessDeniedException.class);
            verifyNoInteractions(caseDetailsService);
            LOGGER.trace("rejectsUnauthenticatedIdentity completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "rejectsUnauthenticatedIdentity exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void rejectsAnonymousAuthenticationEvenWithMemorandumPermission() {
        LOGGER.trace("rejectsAnonymousAuthenticationEvenWithMemorandumPermission() entered");
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            new AnonymousAuthenticationToken(
                                    "test",
                                    "anonymous",
                                    AuthorityUtils.createAuthorityList("MEMORANDUM_PREPARER")));

            assertThatThrownBy(service::findRequestedForCurrentStaff)
                    .isInstanceOf(AccessDeniedException.class);
            verifyNoInteractions(caseDetailsService);
            assertThatThrownBy(processingService::prepareForCurrentStaff)
                    .isInstanceOf(AccessDeniedException.class);
            verifyNoInteractions(preparationService);
            LOGGER.trace("rejectsAnonymousAuthenticationEvenWithMemorandumPermission completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "rejectsAnonymousAuthenticationEvenWithMemorandumPermission exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void authorizedStaffCanInvokeProcessingDirectlyWithoutAController() {
        LOGGER.trace("authorizedStaffCanInvokeProcessingDirectlyWithoutAController() entered");
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            UsernamePasswordAuthenticationToken.authenticated(
                                    "riley.morgan@example.com",
                                    null,
                                    AuthorityUtils.createAuthorityList("MEMORANDUM_PREPARER")));
            when(caseDetailsService.findRequestedForStaff("riley.morgan@example.com"))
                    .thenReturn(List.of());

            assertThat(processingService.prepareForCurrentStaff().getStatus()).isEqualTo("no_work");
            verify(caseDetailsService).findRequestedForStaff("riley.morgan@example.com");
            verifyNoInteractions(preparationService);
            LOGGER.trace("authorizedStaffCanInvokeProcessingDirectlyWithoutAController completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "authorizedStaffCanInvokeProcessingDirectlyWithoutAController exited"
                            + " exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"INTAKE_PREPARER", "other"})
    void otherPermissionsCannotInvokeProcessingDirectly(String authority) {
        LOGGER.trace(
                "otherPermissionsCannotInvokeProcessingDirectly(authority={}) entered",
                LogSummary.describe(authority));
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            UsernamePasswordAuthenticationToken.authenticated(
                                    "staff@example.com",
                                    null,
                                    AuthorityUtils.createAuthorityList(authority)));

            assertThatThrownBy(processingService::prepareForCurrentStaff)
                    .isInstanceOf(AccessDeniedException.class);
            verifyNoInteractions(caseDetailsService, preparationService);
            LOGGER.trace("otherPermissionsCannotInvokeProcessingDirectly completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "otherPermissionsCannotInvokeProcessingDirectly exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void unauthenticatedCallerCannotInvokeProcessingDirectly() {
        LOGGER.trace("unauthenticatedCallerCannotInvokeProcessingDirectly() entered");
        try {
            SecurityContextHolder.clearContext();

            assertThatThrownBy(processingService::prepareForCurrentStaff)
                    .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
            verifyNoInteractions(caseDetailsService, preparationService);
            LOGGER.trace("unauthenticatedCallerCannotInvokeProcessingDirectly completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "unauthenticatedCallerCannotInvokeProcessingDirectly exited exceptionally;"
                            + " type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
