package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.details.*;
import com.demo.legalfirm.model.caseinputs.documents.*;
import com.demo.legalfirm.model.research.*;
import com.demo.legalfirm.model.research.facts.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

/**
 * Tests structured research planning and rejection of unsupported source references without live
 * AI.
 *
 * @file ResearchPlanningServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class ResearchPlanningServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchPlanningServiceTest.class);

    @Test
    void convertsStructuredResponseAndSendsCaseJson() throws IOException {
        LOGGER.trace("convertsStructuredResponseAndSendsCaseJson entered");
        ChatModel model = mock(ChatModel.class);
        ResearchPlan plan = plan();
        ResearchPlanningService service = service(model, plan);
        CaseInput input = input();
        ResearchPlan actual = service.createPlan(input);
        assertThat(actual.getTopics()).hasSize(1);
        assertThat(actual.getTopics().get(0).getFacts()).hasSize(2);
        ArgumentCaptor<Prompt> prompt = ArgumentCaptor.forClass(Prompt.class);
        verify(model).call(prompt.capture());
        assertThat(prompt.getValue().getContents())
                .contains("INTAKE:test", "PDF:test.pdf", "researchQuestions");
        LOGGER.trace("convertsStructuredResponseAndSendsCaseJson completed");
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknownSource", "relatedTopic", "emptyTopics", "classification"})
    void rejectsUnsupportedPlans(String scenario) throws IOException {
        LOGGER.trace("rejectsUnsupportedPlans entered; scenarioLength={}", scenario.length());
        ResearchPlan plan = plan();
        ResearchTopic topic = plan.getTopics().get(0);
        ResearchFact fact = topic.getFacts().get(0);
        switch (scenario) {
            case "unknownSource" -> fact.setSourceId("unknown");
            case "relatedTopic" -> topic.setRelatedTopicIds(List.of("missing"));
            case "emptyTopics" -> plan.setTopics(List.of());
            case "classification" -> fact.setClassification(null);
            default -> throw new IllegalArgumentException();
        }
        assertThatThrownBy(() -> service(mock(ChatModel.class), plan).createPlan(input()))
                .isInstanceOf(IllegalStateException.class);
        LOGGER.trace("rejectsUnsupportedPlans completed");
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrongPage", "missingPage", "intakePage"})
    void invalidPageReferencesWarnAndPreserveThePlan(String scenario) throws IOException {
        LOGGER.trace(
                "invalidPageReferencesWarnAndPreserveThePlan entered; scenarioLength={}",
                scenario.length());
        ResearchPlan response = plan();
        ResearchFact fact =
                response.getTopics().get(0).getFacts().get("intakePage".equals(scenario) ? 1 : 0);
        fact.setPageNumber("missingPage".equals(scenario) ? null : 99);
        ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger)
                        LoggerFactory.getLogger(ResearchPlanningService.class);
        ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender =
                new ch.qos.logback.core.read.ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            ResearchPlan actual = service(mock(ChatModel.class), response).createPlan(input());
            assertThat(actual.getTopics()).hasSize(1);
            assertThat(
                            actual.getTopics()
                                    .get(0)
                                    .getFacts()
                                    .get("intakePage".equals(scenario) ? 1 : 0)
                                    .getPageNumber())
                    .isEqualTo(fact.getPageNumber());
            assertThat(appender.list)
                    .anySatisfy(
                            event -> {
                                assertThat(event.getLevel())
                                        .isEqualTo(ch.qos.logback.classic.Level.WARN);
                                assertThat(event.getFormattedMessage())
                                        .contains("unverified page reference");
                            });
            LOGGER.trace("invalidPageReferencesWarnAndPreserveThePlan completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void logsPromptAndRawResponseEvenWhenValidationRejectsIt() throws IOException {
        LOGGER.trace("logsPromptAndRawResponseEvenWhenValidationRejectsIt entered");
        ChatModel model = mock(ChatModel.class);
        ResearchPlan response = plan();
        response.getTopics().get(0).getFacts().get(0).setSourceId("unknown");
        ResearchPlanningService service = service(model, response);
        var logger =
                (ch.qos.logback.classic.Logger)
                        LoggerFactory.getLogger(ResearchPlanningService.class);
        var originalLevel = logger.getLevel();
        var appender =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        logger.addAppender(appender);
        try {
            assertThatThrownBy(() -> service.createPlan(input()))
                    .isInstanceOf(IllegalStateException.class);
            String responseJson = JsonMapper.builder().build().writeValueAsString(response);
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .isEqualTo("Research plan JSON:\n" + responseJson));
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .contains(
                                                    "OpenAI research planning prompt:",
                                                    "SYSTEM:",
                                                    "USER:",
                                                    "INTAKE:test",
                                                    "PDF:test.pdf",
                                                    "researchQuestions"));
            LOGGER.trace("logsPromptAndRawResponseEvenWhenValidationRejectsIt completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            logger.setLevel(originalLevel);
        }
    }

    @Test
    void invalidInputDoesNotCallModel() throws IOException {
        LOGGER.trace("invalidInputDoesNotCallModel entered");
        ChatModel model = mock(ChatModel.class);
        ResearchPlanningService service = service(model, plan());
        assertThatThrownBy(() -> service.createPlan(null))
                .isInstanceOf(IllegalArgumentException.class);
        verify(model, never()).call(any(Prompt.class));
        LOGGER.trace("invalidInputDoesNotCallModel completed");
    }

    @Test
    void malformedJsonFailsRatherThanReturningAnEmptyPlan() throws IOException {
        LOGGER.trace("malformedJsonFailsRatherThanReturningAnEmptyPlan entered");
        ChatModel model = mock(ChatModel.class);
        ResearchPlanningService service = service(model, plan());
        when(model.call(any(Prompt.class)))
                .thenReturn(
                        new ChatResponse(
                                List.of(new Generation(new AssistantMessage("invalid JSON")))));
        assertThatThrownBy(() -> service.createPlan(input())).isInstanceOf(RuntimeException.class);
        LOGGER.trace("malformedJsonFailsRatherThanReturningAnEmptyPlan completed");
    }

    private ResearchPlanningService service(ChatModel model, ResearchPlan response)
            throws IOException {
        LOGGER.trace("service entered; responseType={}", response.getClass().getSimpleName());
        when(model.getOptions())
                .thenReturn(org.springframework.ai.chat.prompt.ChatOptions.builder().build());
        JsonMapper mapper = JsonMapper.builder().build();
        when(model.call(any(Prompt.class)))
                .thenReturn(
                        new ChatResponse(
                                List.of(
                                        new Generation(
                                                new AssistantMessage(
                                                        mapper.writeValueAsString(response))))));
        ResearchPlanningService service =
                new ResearchPlanningService(
                        ChatClient.builder(model),
                        mapper,
                        new ClassPathResource("prompts/research-planning.txt"));
        LOGGER.trace("service returning ResearchPlanningService");
        return service;
    }

    private CaseInput input() {
        LOGGER.trace("input entered");
        IntakeInput intake = new IntakeInput();
        intake.setSourceId("INTAKE:test");
        CaseDetails details = new CaseDetails();
        details.setIntake(intake);
        CaseInput input =
                new CaseInput(
                        details,
                        List.of(
                                new DocumentInput(
                                        "PDF:test.pdf",
                                        "test.pdf",
                                        DocumentType.SUPPORTING_DOCUMENT,
                                        List.of(new DocumentPageInput(1, "Supplied information")))),
                        List.of());
        LOGGER.trace("input returning CaseInput");
        return input;
    }

    private ResearchPlan plan() {
        LOGGER.trace("plan entered");
        ResearchPlan plan =
                new ResearchPlan(
                        List.of(
                                new ResearchTopic(
                                        "T1",
                                        "Issue to investigate",
                                        List.of(
                                                new ResearchFact(
                                                        "Recorded information",
                                                        FactClassification.DOCUMENTED,
                                                        "PDF:test.pdf",
                                                        1),
                                                new ResearchFact(
                                                        "Client statement",
                                                        FactClassification.CLIENT_REPORTED,
                                                        "INTAKE:test",
                                                        null)),
                                        List.of("Supporting evidence"),
                                        List.of("What law applies?"),
                                        List.of("applicable case law"),
                                        List.of())));
        LOGGER.trace("plan returning ResearchPlan");
        return plan;
    }
}
