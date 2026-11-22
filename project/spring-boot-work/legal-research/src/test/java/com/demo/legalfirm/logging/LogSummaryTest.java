package com.demo.legalfirm.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import com.demo.legalfirm.model.caseinputs.documents.DocumentPageInput;
import com.demo.legalfirm.model.research.facts.FactClassification;
import com.demo.legalfirm.model.research.facts.ResearchFact;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Verifies safe value summaries and redaction in actual model method log entries.
 *
 * @file LogSummaryTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class LogSummaryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogSummaryTest.class);

    @Test
    void summariesDoNotExposeTextCollectionContentsOrArbitraryObjectStrings() {
        LOGGER.trace(
                "summariesDoNotExposeTextCollectionContentsOrArbitraryObjectStrings() entered");
        try {
            String secret = "sensitive-client-text\nFORGED-LOG-ENTRY";
            Object object = mock(Object.class);
            when(object.toString()).thenThrow(new AssertionError("toString must not be used"));
            assertThat(LogSummary.describe(secret))
                    .isEqualTo("redacted(length=" + secret.length() + ")");
            assertThat(LogSummary.describe(List.of(secret))).isEqualTo("collection(size=1)");
            assertThat(LogSummary.describe(new String[] {secret})).isEqualTo("array(length=1)");
            assertThat(LogSummary.describe(object)).doesNotContain(secret, "FORGED-LOG-ENTRY");
            assertThat(LogSummary.describe(null)).isEqualTo("null");
            assertThat(LogSummary.describe(42)).isEqualTo("42");
            LOGGER.trace(
                    "summariesDoNotExposeTextCollectionContentsOrArbitraryObjectStrings completed");
        } catch (Exception failure) {
            LOGGER.trace(
                    "Summary verification failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    @Test
    void modelEntryAndReturnLogsRedactNarrativesAndExtractedPdfText() {
        LOGGER.trace("modelEntryAndReturnLogsRedactNarrativesAndExtractedPdfText() entered");
        ch.qos.logback.classic.Logger factLogger =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ResearchFact.class);
        ch.qos.logback.classic.Logger pageLogger =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DocumentPageInput.class);
        Level previousFactLevel = factLogger.getLevel();
        Level previousPageLevel = pageLogger.getLevel();
        ListAppender<ILoggingEvent> capture = new ListAppender<>();
        capture.start();
        factLogger.setLevel(Level.TRACE);
        pageLogger.setLevel(Level.TRACE);
        factLogger.addAppender(capture);
        pageLogger.addAppender(capture);
        try {
            String secret = "PRIVATE_MEDICAL_NARRATIVE\nFORGED-ENTRY";
            ResearchFact fact =
                    new ResearchFact(secret, FactClassification.CLIENT_REPORTED, "INTAKE", null);
            fact.getStatement();
            fact.setStatement(secret);
            DocumentPageInput page = new DocumentPageInput(1, secret);
            page.getText();
            page.setText(secret);

            List<String> messages =
                    capture.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
            assertThat(messages).anyMatch(message -> message.contains("ResearchFact("));
            assertThat(messages)
                    .anyMatch(message -> message.contains("getStatement returning redacted"));
            assertThat(messages)
                    .anyMatch(message -> message.contains("getText returning redacted"));
            assertThat(messages)
                    .allSatisfy(
                            message ->
                                    assertThat(message)
                                            .doesNotContain(
                                                    "PRIVATE_MEDICAL_NARRATIVE", "FORGED-ENTRY"));
            LOGGER.trace("modelEntryAndReturnLogsRedactNarrativesAndExtractedPdfText completed");
        } catch (Exception failure) {
            LOGGER.trace(
                    "Log redaction verification failed; type={}",
                    failure.getClass().getSimpleName());
            throw failure;
        } finally {
            factLogger.detachAppender(capture);
            pageLogger.detachAppender(capture);
            factLogger.setLevel(previousFactLevel);
            pageLogger.setLevel(previousPageLevel);
            capture.stop();
        }
    }
}
