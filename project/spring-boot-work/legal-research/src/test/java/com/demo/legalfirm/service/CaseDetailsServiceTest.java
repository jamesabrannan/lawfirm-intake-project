package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.repository.CaseDetailsRepository;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Verifies that the service returns requested case details without changing repository results.
 *
 * @file CaseDetailsServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class CaseDetailsServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDetailsServiceTest.class);

    private final CaseDetailsRepository repository = mock(CaseDetailsRepository.class);
    private final CaseDetailsService service = new CaseDetailsService(repository);

    @Test
    void passesThroughAllRequestedCasesForLaterAssembly() {
        LOGGER.trace("passesThroughAllRequestedCasesForLaterAssembly() entered");
        try {
            CaseDetails first = new CaseDetails();
            first.setMemorandumId(1);
            CaseDetails second = new CaseDetails();
            second.setMemorandumId(2);
            List<CaseDetails> cases = List.of(first, second);
            when(repository.findRequestedForStaff("riley.morgan@example.com")).thenReturn(cases);

            assertThat(service.findRequestedForStaff("riley.morgan@example.com")).isSameAs(cases);
            verify(repository).findRequestedForStaff("riley.morgan@example.com");
            verifyNoMoreInteractions(repository);
            LOGGER.trace("passesThroughAllRequestedCasesForLaterAssembly completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "passesThroughAllRequestedCasesForLaterAssembly exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Test
    void returnsAnEmptyCollectionWhenNothingIsRequested() {
        LOGGER.trace("returnsAnEmptyCollectionWhenNothingIsRequested() entered");
        try {
            when(repository.findRequestedForStaff("riley.morgan@example.com"))
                    .thenReturn(List.of());

            assertThat(service.findRequestedForStaff("riley.morgan@example.com")).isEmpty();
            verify(repository).findRequestedForStaff("riley.morgan@example.com");
            verifyNoMoreInteractions(repository);
            LOGGER.trace("returnsAnEmptyCollectionWhenNothingIsRequested completed");
        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "returnsAnEmptyCollectionWhenNothingIsRequested exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
