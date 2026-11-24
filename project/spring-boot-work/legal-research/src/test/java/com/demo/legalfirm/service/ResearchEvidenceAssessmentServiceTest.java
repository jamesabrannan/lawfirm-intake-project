package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.model.research.*;
import com.demo.legalfirm.model.research.assessment.*;
import com.demo.legalfirm.model.research.courtlistener.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.prompt.*;
import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.*;

/**
 * Tests evidence relevance mapping, provenance checks and demo JSON logging without live AI calls.
 *
 * @file ResearchEvidenceAssessmentServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class ResearchEvidenceAssessmentServiceTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResearchEvidenceAssessmentServiceTest.class);

    @Test
    void mapsAssessmentsForEveryQuestionAndLogsJson() throws IOException {
        LOGGER.trace("mapsAssessmentsForEveryQuestionAndLogsJson entered");
        ChatModel model = mock(ChatModel.class);
        var response = response();
        var service = service(model, response);
        var logger =
                (ch.qos.logback.classic.Logger)
                        LoggerFactory.getLogger(ResearchEvidenceAssessmentService.class);
        var originalLevel = logger.getLevel();
        var appender =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        logger.addAppender(appender);
        try {
            var assessment = service.assess(plan(), results());
            assertThat(assessment.getQuestionAssessments()).hasSize(3);
            assertThat(
                            assessment
                                    .getQuestionAssessments()
                                    .get(0)
                                    .getResultAssessments()
                                    .get(0)
                                    .getClassification())
                    .isEqualTo(ResearchResultClassification.POTENTIAL_LEAD);
            assertThat(assessment.getQuestionAssessments().get(1).getUsefulLeadsFound()).isFalse();
            assertThat(assessment.getQuestionAssessments().get(2).getResultAssessments()).isEmpty();
            var mapper = JsonMapper.builder().build();
            assertThat(mapper.readTree(mapper.writeValueAsString(assessment)))
                    .isEqualTo(mapper.readTree(mapper.writeValueAsString(response)));
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .contains(
                                                    "Research evidence assessment JSON:",
                                                    "POTENTIAL_LEAD",
                                                    "questionAssessments"));
            ArgumentCaptor<Prompt> prompt = ArgumentCaptor.forClass(Prompt.class);
            verify(model).call(prompt.capture());
            assertThat(prompt.getValue().getContents())
                    .contains("retrievedResults", "researchPlan", "ZERO-BASED");
            String rawResponse = mapper.writeValueAsString(response);
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .isEqualTo(
                                                    "Research evidence assessment JSON:\n"
                                                            + rawResponse));
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .contains(
                                                    "OpenAI research evidence assessment prompt:",
                                                    "SYSTEM:",
                                                    "USER:",
                                                    "retrievedResults",
                                                    "researchPlan",
                                                    "ZERO-BASED",
                                                    "questionAssessments"));
            LOGGER.trace("mapsAssessmentsForEveryQuestionAndLogsJson completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            logger.setLevel(originalLevel);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "inventedCase",
                "wrongTopicCase",
                "invalidQuestion",
                "duplicateQuestion",
                "missingQuestion",
                "supportingEvidence",
                "missingReason",
                "missingGaps",
                "inconsistentUsefulFlag",
                "missingCandidate"
            })
    void rejectsInvalidAssessments(String scenario) throws IOException {
        LOGGER.trace("rejectsInvalidAssessments entered; scenarioLength={}", scenario.length());
        var response = response();
        var first = response.getQuestionAssessments().get(0);
        switch (scenario) {
            case "inventedCase" -> first.getResultAssessments().get(0).setClusterId(999L);
            case "wrongTopicCase" ->
                    response.getQuestionAssessments()
                            .get(2)
                            .setResultAssessments(first.getResultAssessments());
            case "invalidQuestion" -> first.setResearchQuestionIndex(22);
            case "duplicateQuestion" -> response.getQuestionAssessments().set(1, first);
            case "missingQuestion" -> response.getQuestionAssessments().remove(1);
            case "supportingEvidence" ->
                    first.getResultAssessments()
                            .get(0)
                            .setClassification(ResearchResultClassification.SUPPORTING_EVIDENCE);
            case "missingReason" -> first.getResultAssessments().get(0).setReason(" ");
            case "missingGaps" -> first.setResearchGaps(List.of());
            case "inconsistentUsefulFlag" -> first.setUsefulLeadsFound(false);
            case "missingCandidate" -> first.setResultAssessments(List.of());
            default -> throw new IllegalArgumentException();
        }
        assertThatThrownBy(() -> service(mock(ChatModel.class), response).assess(plan(), results()))
                .isInstanceOf(IllegalStateException.class);
        LOGGER.trace("rejectsInvalidAssessments completed");
    }

    @Test
    void invalidInputDoesNotCallModel() throws IOException {
        LOGGER.trace("invalidInputDoesNotCallModel entered");
        ChatModel model = mock(ChatModel.class);
        var service = service(model, response());
        assertThatThrownBy(() -> service.assess(null, results()))
                .isInstanceOf(IllegalArgumentException.class);
        verify(model, never()).call(any(Prompt.class));
        LOGGER.trace("invalidInputDoesNotCallModel completed");
    }

    @Test
    void noRetrievedCasesStillAssessesEveryQuestion() throws IOException {
        LOGGER.trace("noRetrievedCasesStillAssessesEveryQuestion entered");
        var response = response();
        for (var question : response.getQuestionAssessments()) {
            question.setResultAssessments(List.of());
            question.setUsefulLeadsFound(false);
            question.setResearchGaps(
                    List.of(
                            "No useful authority was identified in the retrieved results for this"
                                    + " question."));
        }
        var actual = service(mock(ChatModel.class), response).assess(plan(), List.of());
        assertThat(actual.getQuestionAssessments())
                .hasSize(3)
                .allSatisfy(
                        question -> {
                            assertThat(question.getUsefulLeadsFound()).isFalse();
                            assertThat(question.getResearchGaps()).isNotEmpty();
                        });
        LOGGER.trace("noRetrievedCasesStillAssessesEveryQuestion completed");
    }

    private ResearchEvidenceAssessmentService service(
            ChatModel model, ResearchEvidenceAssessment response) throws IOException {
        LOGGER.trace("service entered; responseType={}", response.getClass().getSimpleName());
        JsonMapper mapper = JsonMapper.builder().build();
        when(model.getOptions()).thenReturn(ChatOptions.builder().build());
        when(model.call(any(Prompt.class)))
                .thenReturn(
                        new ChatResponse(
                                List.of(
                                        new Generation(
                                                new AssistantMessage(
                                                        mapper.writeValueAsString(response))))));
        var service =
                new ResearchEvidenceAssessmentService(
                        ChatClient.builder(model),
                        mapper,
                        new ClassPathResource("prompts/research-evidence-assessment.txt"));
        LOGGER.trace("service returning ResearchEvidenceAssessmentService");
        return service;
    }

    private ResearchPlan plan() {
        LOGGER.trace("plan entered");
        var first = new ResearchTopic();
        first.setId("T1");
        first.setResearchQuestions(List.of("First question?", "Second question?"));
        var second = new ResearchTopic();
        second.setId("T2");
        second.setResearchQuestions(List.of("Third question?"));
        var plan = new ResearchPlan(List.of(first, second));
        LOGGER.trace("plan returning ResearchPlan");
        return plan;
    }

    private List<CourtListenerResearchResult> results() {
        LOGGER.trace("results entered");
        var result = new CourtListenerSearchResult();
        result.setClusterId(7L);
        var results =
                List.of(
                        new CourtListenerResearchResult("T1", "first query", List.of(result)),
                        new CourtListenerResearchResult("T1", "second query", List.of(result)),
                        new CourtListenerResearchResult("T2", "third query", List.of()));
        LOGGER.trace("results returning size={}", results.size());
        return results;
    }

    private ResearchEvidenceAssessment response() {
        LOGGER.trace("response entered");
        var response =
                new ResearchEvidenceAssessment(
                        new ArrayList<>(
                                List.of(
                                        new ResearchQuestionAssessment(
                                                "T1",
                                                0,
                                                true,
                                                List.of(
                                                        new ResearchResultAssessment(
                                                                7L,
                                                                ResearchResultClassification
                                                                        .POTENTIAL_LEAD,
                                                                "Potential relevance only; full"
                                                                    + " opinion needs review.")),
                                                List.of("No full opinion was reviewed.")),
                                        new ResearchQuestionAssessment(
                                                "T1",
                                                1,
                                                false,
                                                List.of(
                                                        new ResearchResultAssessment(
                                                                7L,
                                                                ResearchResultClassification
                                                                        .IRRELEVANT,
                                                                "Snippet does not address the"
                                                                        + " question.")),
                                                List.of(
                                                        "No useful authority was identified in the"
                                                                + " retrieved results for this"
                                                                + " question.")),
                                        new ResearchQuestionAssessment(
                                                "T2",
                                                0,
                                                false,
                                                List.of(),
                                                List.of(
                                                        "No useful authority was identified in the"
                                                                + " retrieved results for this"
                                                                + " question.")))));
        LOGGER.trace("response returning ResearchEvidenceAssessment");
        return response;
    }
}
