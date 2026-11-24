package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.legalfirm.model.caseinputs.*;
import com.demo.legalfirm.model.caseinputs.details.*;
import com.demo.legalfirm.model.memorandums.draft.*;
import com.demo.legalfirm.model.research.*;
import com.demo.legalfirm.model.research.assessment.*;
import com.demo.legalfirm.model.research.courtlistener.*;
import com.demo.legalfirm.model.research.facts.*;

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
 * Tests preliminary drafting, exclusion of rejected evidence, references and request/response
 * logging.
 *
 * @file MemorandumDraftingServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class MemorandumDraftingServiceTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MemorandumDraftingServiceTest.class);

    @Test
    void draftsOnlyWithAssessedLeadsAndLogsPromptAndActualResponse() throws IOException {
        LOGGER.trace("draftsOnlyWithAssessedLeadsAndLogsPromptAndActualResponse entered");
        ChatModel model = mock(ChatModel.class);
        MemorandumDraft response = response();
        var service = service(model, response);
        var logger =
                (ch.qos.logback.classic.Logger)
                        LoggerFactory.getLogger(MemorandumDraftingService.class);
        var originalLevel = logger.getLevel();
        var appender =
                new ch.qos.logback.core.read.ListAppender<
                        ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        logger.addAppender(appender);
        try {
            var assessment = assessment();
            var results = results();
            var draft = service.draft(input(), plan(), results, assessment);
            assertThat(draft.getMemorandumId()).isEqualTo(2L);
            assertThat(draft.getIssues()).hasSize(1);
            var lead = draft.getIssues().get(0).getResearchLeads().get(0);
            assertThat(lead.getCaseName()).isEqualTo("Relevant case");
            assertThat(lead.getCitations()).containsExactly("100 Example 1");
            assertThat(lead.getCourt()).isEqualTo("Example Court");
            assertThat(lead.getSourceUrl())
                    .isEqualTo("https://www.courtlistener.com/opinion/7/example/");
            ArgumentCaptor<Prompt> prompt = ArgumentCaptor.forClass(Prompt.class);
            verify(model).call(prompt.capture());
            String promptText = prompt.getValue().getContents();
            assertThat(promptText)
                    .contains(
                            "caseInput",
                            "researchPlan",
                            "evidenceAssessment",
                            "Potential reason",
                            "No useful authority",
                            "POTENTIAL_LEAD",
                            "ZERO-BASED");
            assertThat(promptText)
                    .doesNotContain(
                            "REJECTED_CASE_NAME",
                            "REJECTED_SNIPPET",
                            "Rejected reason",
                            "IRRELEVANT");
            // Filtering does not erase upstream audit records.
            assertThat(results.get(0).getResults()).hasSize(2);
            assertThat(assessment.getQuestionAssessments().get(0).getResultAssessments())
                    .hasSize(2);
            String raw = JsonMapper.builder().build().writeValueAsString(response);
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .isEqualTo("Memorandum draft JSON:\n" + raw));
            assertThat(appender.list)
                    .anySatisfy(
                            event ->
                                    assertThat(event.getFormattedMessage())
                                            .contains(
                                                    "OpenAI memorandum drafting prompt:",
                                                    "SYSTEM:",
                                                    "USER:",
                                                    "caseInput",
                                                    "factualBackground"));
            LOGGER.trace("draftsOnlyWithAssessedLeadsAndLogsPromptAndActualResponse completed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            logger.setLevel(originalLevel);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "wrongMemo",
                "wrongIntake",
                "changedFact",
                "changedClassification",
                "changedSource",
                "changedPage",
                "missingTopic",
                "unknownTopic",
                "inventedCase",
                "rejectedCase",
                "irrelevantQuestion",
                "missingGaps",
                "missingLimitations",
                "missingFollowUp"
            })
    void rejectsInvalidDrafts(String scenario) throws IOException {
        LOGGER.trace("rejectsInvalidDrafts entered; scenarioLength={}", scenario.length());
        MemorandumDraft response = response();
        var fact = response.getFactualBackground().get(0);
        var issue = response.getIssues().get(0);
        var lead = issue.getResearchLeads().get(0);
        switch (scenario) {
            case "wrongMemo" -> response.setMemorandumId(99L);
            case "wrongIntake" -> response.setIntakeId("other");
            case "changedFact" -> fact.setStatement("Invented fact");
            case "changedClassification" -> fact.setClassification(FactClassification.DOCUMENTED);
            case "changedSource" -> fact.setSourceId("unknown");
            case "changedPage" -> fact.setPageNumber(99);
            case "missingTopic" -> response.setIssues(List.of());
            case "unknownTopic" -> issue.setTopicId("other");
            case "inventedCase" -> lead.setClusterId(999L);
            case "rejectedCase" -> lead.setClusterId(9L);
            case "irrelevantQuestion" -> lead.setResearchQuestionIndexes(List.of(1));
            case "missingGaps" -> issue.setResearchGaps(List.of());
            case "missingLimitations" -> response.setLimitations(List.of());
            case "missingFollowUp" -> response.setLawyerFollowUp(List.of());
            default -> throw new IllegalArgumentException();
        }
        assertThatThrownBy(
                        () ->
                                service(mock(ChatModel.class), response)
                                        .draft(input(), plan(), results(), assessment()))
                .isInstanceOf(IllegalStateException.class);
        LOGGER.trace("rejectsInvalidDrafts completed");
    }

    @Test
    void draftsWithExplicitGapsWhenNoUsefulCasesWereRetrieved() throws IOException {
        LOGGER.trace("draftsWithExplicitGapsWhenNoUsefulCasesWereRetrieved entered");
        var response = response();
        response.getIssues().get(0).setResearchLeads(List.of());
        var assessment = assessment();
        for (var question : assessment.getQuestionAssessments()) {
            question.setResultAssessments(List.of());
            question.setUsefulLeadsFound(false);
        }
        var draft =
                service(mock(ChatModel.class), response)
                        .draft(input(), plan(), List.of(), assessment);
        assertThat(draft.getIssues().get(0).getResearchLeads()).isEmpty();
        assertThat(draft.getIssues().get(0).getResearchGaps())
                .contains("No useful authority for the second question.");
        LOGGER.trace("draftsWithExplicitGapsWhenNoUsefulCasesWereRetrieved completed");
    }

    @Test
    void rejectsMissingAssessmentBeforeCallingOpenAi() throws IOException {
        LOGGER.trace("rejectsMissingAssessmentBeforeCallingOpenAi entered");
        ChatModel model = mock(ChatModel.class);
        var service = service(model, response());
        assertThatThrownBy(
                        () ->
                                service.draft(
                                        input(),
                                        plan(),
                                        results(),
                                        new ResearchEvidenceAssessment(List.of())))
                .isInstanceOf(IllegalArgumentException.class);
        verify(model, never()).call(any(Prompt.class));
        LOGGER.trace("rejectsMissingAssessmentBeforeCallingOpenAi completed");
    }

    @Test
    void invalidJsonOrTransportFailureDoesNotReturnADraft() throws IOException {
        LOGGER.trace("invalidJsonOrTransportFailureDoesNotReturnADraft entered");
        ChatModel model = mock(ChatModel.class);
        var service = service(model, response());
        when(model.call(any(Prompt.class)))
                .thenReturn(
                        new ChatResponse(
                                List.of(new Generation(new AssistantMessage("invalid JSON")))));
        assertThatThrownBy(() -> service.draft(input(), plan(), results(), assessment()))
                .isInstanceOf(RuntimeException.class);
        RuntimeException failure = new IllegalStateException("Transport failed");
        when(model.call(any(Prompt.class))).thenThrow(failure);
        assertThatThrownBy(() -> service.draft(input(), plan(), results(), assessment()))
                .isSameAs(failure);
        LOGGER.trace("invalidJsonOrTransportFailureDoesNotReturnADraft completed");
    }

    private MemorandumDraftingService service(ChatModel model, MemorandumDraft response)
            throws IOException {
        LOGGER.trace("service entered; responseType={}", response.getClass().getSimpleName());
        var mapper = JsonMapper.builder().build();
        when(model.getOptions()).thenReturn(ChatOptions.builder().build());
        when(model.call(any(Prompt.class)))
                .thenReturn(
                        new ChatResponse(
                                List.of(
                                        new Generation(
                                                new AssistantMessage(
                                                        mapper.writeValueAsString(response))))));
        var service =
                new MemorandumDraftingService(
                        ChatClient.builder(model),
                        mapper,
                        new ClassPathResource("prompts/memorandum-drafting.txt"));
        LOGGER.trace("service returning MemorandumDraftingService");
        return service;
    }

    private CaseInput input() {
        LOGGER.trace("input entered");
        var intake = new IntakeInput();
        intake.setIntakeId("I-test");
        intake.setSourceId("INTAKE:I-test");
        var details = new CaseDetails();
        details.setMemorandumId(2L);
        details.setIntake(intake);
        var input = new CaseInput(details, List.of(), List.of());
        LOGGER.trace("input returning CaseInput");
        return input;
    }

    private ResearchFact fact() {
        LOGGER.trace("fact entered");
        var fact =
                new ResearchFact(
                        "Client reports symptoms.",
                        FactClassification.CLIENT_REPORTED,
                        "INTAKE:I-test",
                        null);
        LOGGER.trace("fact returning ResearchFact");
        return fact;
    }

    private ResearchPlan plan() {
        LOGGER.trace("plan entered");
        var topic =
                new ResearchTopic(
                        "T1",
                        "Preliminary issue",
                        List.of(fact()),
                        List.of("Records unavailable"),
                        List.of("First question?", "Second question?"),
                        List.of("query"),
                        List.of());
        var plan = new ResearchPlan(List.of(topic));
        LOGGER.trace("plan returning ResearchPlan");
        return plan;
    }

    private List<CourtListenerResearchResult> results() {
        LOGGER.trace("results entered");
        var relevant = new CourtListenerSearchResult();
        relevant.setClusterId(7L);
        relevant.setCaseName("<mark>Relevant</mark> case");
        relevant.setCitation(List.of("100 <mark>Example</mark> 1"));
        relevant.setCourt("Example Court");
        relevant.setAbsoluteUrl("https://www.courtlistener.com/opinion/7/example/");
        var rejected = new CourtListenerSearchResult();
        rejected.setClusterId(9L);
        rejected.setCaseName("REJECTED_CASE_NAME");
        rejected.setOpinions(
                List.of(
                        new CourtListenerOpinionSnippet(
                                9L, "combined-opinion", "REJECTED_SNIPPET")));
        var results =
                List.of(
                        new CourtListenerResearchResult(
                                "T1", "query", List.of(relevant, rejected)));
        LOGGER.trace("results returning size={}", results.size());
        return results;
    }

    private ResearchEvidenceAssessment assessment() {
        LOGGER.trace("assessment entered");
        var first =
                new ResearchQuestionAssessment(
                        "T1",
                        0,
                        true,
                        List.of(
                                new ResearchResultAssessment(
                                        7L,
                                        ResearchResultClassification.POTENTIAL_LEAD,
                                        "Potential reason"),
                                new ResearchResultAssessment(
                                        9L,
                                        ResearchResultClassification.IRRELEVANT,
                                        "Rejected reason")),
                        List.of("Full opinion not reviewed."));
        var second =
                new ResearchQuestionAssessment(
                        "T1",
                        1,
                        false,
                        List.of(
                                new ResearchResultAssessment(
                                        7L,
                                        ResearchResultClassification.IRRELEVANT,
                                        "Rejected reason"),
                                new ResearchResultAssessment(
                                        9L,
                                        ResearchResultClassification.IRRELEVANT,
                                        "Rejected reason")),
                        List.of("No useful authority for the second question."));
        var assessment = new ResearchEvidenceAssessment(List.of(first, second));
        LOGGER.trace("assessment returning ResearchEvidenceAssessment");
        return assessment;
    }

    private MemorandumDraft response() {
        LOGGER.trace("response entered");
        var lead =
                new MemorandumResearchLead(
                        7L,
                        List.of(0),
                        "Unverified lead for the first question.",
                        "Full opinion requires review.",
                        null,
                        null,
                        null,
                        null);
        var issue =
                new MemorandumIssueDraft(
                        "T1",
                        "Preliminary issue",
                        "The reported symptoms require evidence and legal research.",
                        List.of(lead),
                        List.of(
                                "Full opinion not reviewed.",
                                "No useful authority for the second question."));
        var response =
                new MemorandumDraft(
                        2L,
                        "I-test",
                        "Preliminary legal research memorandum",
                        "For lawyer review.",
                        List.of(fact()),
                        List.of(issue),
                        List.of("Obtain records."),
                        List.of("Review full opinions and official sources."),
                        List.of(
                                "Preliminary only; PDF extraction was text-only and opinions were"
                                    + " not reviewed."));
        LOGGER.trace("response returning MemorandumDraft");
        return response;
    }
}
