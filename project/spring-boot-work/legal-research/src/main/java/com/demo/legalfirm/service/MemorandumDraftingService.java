package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.memorandums.draft.*;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.assessment.*;
import com.demo.legalfirm.model.research.courtlistener.*;
import com.demo.legalfirm.model.research.facts.ResearchFact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Drafts a preliminary memorandum using supplied facts, assessed leads and unresolved gaps.
 *
 * @file MemorandumDraftingService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class MemorandumDraftingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumDraftingService.class);
    private final ChatClient chatClient;
    private final ObjectMapper mapper;
    private final String instructions;

    public MemorandumDraftingService(
            ChatClient.Builder builder,
            ObjectMapper mapper,
            @Value("classpath:prompts/memorandum-drafting.txt") Resource prompt)
            throws IOException {
        LOGGER.trace(
                "MemorandumDraftingService(builder={}, mapper={}, prompt={}) entered",
                LogSummary.describe(builder),
                LogSummary.describe(mapper),
                LogSummary.describe(prompt));
        try {
            this.chatClient = builder.build();
            this.mapper = mapper;
            this.instructions = prompt.getContentAsString(StandardCharsets.UTF_8);
            LOGGER.trace("MemorandumDraftingService completed");
        } catch (IOException | RuntimeException failure) {
            LOGGER.trace(
                    "MemorandumDraftingService failed; type={}",
                    failure.getClass().getSimpleName());
            throw failure;
        }
    }

    /** Returns a draft only. No files are saved and no database completion state is changed. */
    public MemorandumDraft draft(
            CaseInput input,
            ResearchPlan plan,
            List<CourtListenerResearchResult> results,
            ResearchEvidenceAssessment assessment) {
        LOGGER.debug(
                "draft(input={}, plan={}, results={}, assessment={}) entered",
                LogSummary.describe(input),
                LogSummary.describe(plan),
                LogSummary.describe(results),
                LogSummary.describe(assessment));
        try {
            MemorandumDraftInput context = draftingInput(input, plan, results, assessment);
            LOGGER.info(
                    "Starting AI memorandum drafting; memorandumId={}, topics={},"
                        + " retainedQueryGroups={}",
                    input.getDetails().getMemorandumId(),
                    plan.getTopics().size(),
                    context.getResearchLeads().size());
            BeanOutputConverter<MemorandumDraft> converter =
                    new BeanOutputConverter<>(MemorandumDraft.class);
            String systemPrompt = instructions + "\n\n" + converter.getFormat();
            String userPrompt = mapper.writeValueAsString(context);
            LOGGER.debug(
                    "OpenAI memorandum drafting prompt:\nSYSTEM:\n{}\nUSER:\n{}",
                    systemPrompt,
                    userPrompt);
            String responseJson =
                    chatClient.prompt().system(systemPrompt).user(userPrompt).call().content();
            LOGGER.debug("Memorandum draft JSON:\n{}", responseJson);
            MemorandumDraft draft = converter.convert(responseJson);
            validateAndEnrich(draft, context);
            LOGGER.info(
                    "Memorandum draft completed; memorandumId={}, issues={}",
                    draft.getMemorandumId(),
                    draft.getIssues().size());
            LOGGER.debug("draft returning {}", LogSummary.describe(draft));
            return draft;
        } catch (RuntimeException failure) {
            LOGGER.warn("Memorandum drafting failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    // Remove rejected material from the drafting input without mutating upstream results.
    private MemorandumDraftInput draftingInput(
            CaseInput input,
            ResearchPlan plan,
            List<CourtListenerResearchResult> results,
            ResearchEvidenceAssessment assessment) {
        LOGGER.trace(
                "draftingInput(input={}, plan={}, results={}, assessment={}) entered",
                LogSummary.describe(input),
                LogSummary.describe(plan),
                LogSummary.describe(results),
                LogSummary.describe(assessment));
        try {
            if (input == null
                    || input.getDetails() == null
                    || input.getDetails().getIntake() == null
                    || input.getDetails().getIntake().getIntakeId() == null
                    || plan == null
                    || plan.getTopics() == null
                    || plan.getTopics().isEmpty()
                    || results == null
                    || assessment == null
                    || assessment.getQuestionAssessments() == null) {
                throw new IllegalArgumentException(
                        "Case input, nonempty plan, results and assessment are required");
            }
            Map<String, Integer> questionCounts = new LinkedHashMap<>();
            Map<String, Set<Long>> available = new HashMap<>();
            for (var topic : plan.getTopics()) {
                if (topic == null
                        || topic.getId() == null
                        || topic.getId().isBlank()
                        || topic.getFacts() == null
                        || topic.getResearchQuestions() == null
                        || topic.getResearchQuestions().isEmpty()
                        || questionCounts.putIfAbsent(
                                        topic.getId(), topic.getResearchQuestions().size())
                                != null) {
                    throw new IllegalArgumentException(
                            "Plan topics must be unique and contain facts and questions");
                }
                available.put(topic.getId(), new HashSet<>());
            }
            for (var group : results) {
                if (group == null
                        || !available.containsKey(group.getTopicId())
                        || group.getResults() == null) {
                    throw new IllegalArgumentException(
                            "Retrieved groups must belong to planned topics");
                }
                for (var result : group.getResults()) {
                    if (result == null || result.getClusterId() == null) {
                        throw new IllegalArgumentException("Retrieved cases require cluster IDs");
                    }
                    available.get(group.getTopicId()).add(result.getClusterId());
                }
            }
            Map<String, Set<Integer>> seen = new HashMap<>();
            Map<String, Set<Long>> retainedIds = new HashMap<>();
            List<ResearchQuestionAssessment> filteredAssessments = new ArrayList<>();
            for (var question : assessment.getQuestionAssessments()) {
                if (question == null
                        || !questionCounts.containsKey(question.getTopicId())
                        || question.getResearchQuestionIndex() == null
                        || question.getResearchQuestionIndex() < 0
                        || question.getResearchQuestionIndex()
                                >= questionCounts.get(question.getTopicId())
                        || !seen.computeIfAbsent(question.getTopicId(), key -> new HashSet<>())
                                .add(question.getResearchQuestionIndex())
                        || question.getResultAssessments() == null
                        || question.getResearchGaps() == null
                        || question.getResearchGaps().isEmpty()) {
                    throw new IllegalArgumentException(
                            "A complete assessment of each planned question is required");
                }
                List<ResearchResultAssessment> leads = new ArrayList<>();
                for (var result : question.getResultAssessments()) {
                    if (result == null
                            || result.getClassification() == null
                            || !available.get(question.getTopicId()).contains(result.getClusterId())
                            || result.getClassification()
                                    == ResearchResultClassification.SUPPORTING_EVIDENCE) {
                        throw new IllegalArgumentException(
                                "Only assessed retrieved snippets may be used in drafting");
                    }
                    if (result.getClassification() == ResearchResultClassification.POTENTIAL_LEAD) {
                        leads.add(result);
                        retainedIds
                                .computeIfAbsent(question.getTopicId(), key -> new HashSet<>())
                                .add(result.getClusterId());
                    }
                }
                filteredAssessments.add(
                        new ResearchQuestionAssessment(
                                question.getTopicId(),
                                question.getResearchQuestionIndex(),
                                !leads.isEmpty(),
                                leads,
                                question.getResearchGaps()));
            }
            for (var topic : questionCounts.entrySet()) {
                if (seen.getOrDefault(topic.getKey(), Set.of()).size() != topic.getValue()) {
                    throw new IllegalArgumentException(
                            "Every planned question must have an assessment");
                }
            }
            List<CourtListenerResearchResult> filteredResults = new ArrayList<>();
            for (var group : results) {
                var kept =
                        group.getResults().stream()
                                .filter(
                                        result ->
                                                retainedIds
                                                        .getOrDefault(group.getTopicId(), Set.of())
                                                        .contains(result.getClusterId()))
                                .toList();
                if (!kept.isEmpty()) {
                    filteredResults.add(
                            new CourtListenerResearchResult(
                                    group.getTopicId(), group.getQuery(), kept));
                }
            }
            MemorandumDraftInput context =
                    new MemorandumDraftInput(
                            input,
                            plan,
                            filteredResults,
                            new ResearchEvidenceAssessment(filteredAssessments));
            LOGGER.trace(
                    "draftingInput returning topics={}, retainedQueryGroups={}",
                    questionCounts.size(),
                    filteredResults.size());
            return context;
        } catch (RuntimeException failure) {
            LOGGER.trace("draftingInput failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    // These checks enforce identity/provenance, not semantic truth or legal correctness.
    private void validateAndEnrich(MemorandumDraft draft, MemorandumDraftInput context) {
        LOGGER.trace(
                "validateAndEnrich(draft={}, context={}) entered",
                LogSummary.describe(draft),
                LogSummary.describe(context));
        try {
            var details = context.getCaseInput().getDetails();
            if (draft == null
                    || !Objects.equals(draft.getMemorandumId(), details.getMemorandumId())
                    || !Objects.equals(draft.getIntakeId(), details.getIntake().getIntakeId())
                    || draft.getTitle() == null
                    || draft.getTitle().isBlank()
                    || draft.getSummary() == null
                    || draft.getSummary().isBlank()
                    || draft.getFactualBackground() == null
                    || draft.getFactualBackground().isEmpty()
                    || draft.getIssues() == null) {
                throw new IllegalStateException(
                        "Draft requires matching case identity, title, summary, facts and issues");
            }
            validateTextList(draft.getMissingInformation(), false);
            validateTextList(draft.getLawyerFollowUp(), true);
            validateTextList(draft.getLimitations(), true);
            List<ResearchFact> originalFacts =
                    context.getResearchPlan().getTopics().stream()
                            .flatMap(topic -> topic.getFacts().stream())
                            .toList();
            for (ResearchFact fact : draft.getFactualBackground()) {
                if (fact == null
                        || originalFacts.stream()
                                .noneMatch(
                                        original ->
                                                original != null
                                                        && Objects.equals(
                                                                original.getStatement(),
                                                                fact.getStatement())
                                                        && Objects.equals(
                                                                original.getClassification(),
                                                                fact.getClassification())
                                                        && Objects.equals(
                                                                original.getSourceId(),
                                                                fact.getSourceId())
                                                        && Objects.equals(
                                                                original.getPageNumber(),
                                                                fact.getPageNumber()))) {
                    throw new IllegalStateException(
                            "Draft facts must preserve planned statements, classifications and"
                                + " source references");
                }
            }
            Set<String> topics = new HashSet<>();
            for (var topic : context.getResearchPlan().getTopics()) topics.add(topic.getId());
            Set<String> covered = new HashSet<>();
            for (var issue : draft.getIssues()) {
                if (issue == null
                        || !topics.contains(issue.getTopicId())
                        || !covered.add(issue.getTopicId())
                        || issue.getTitle() == null
                        || issue.getTitle().isBlank()
                        || issue.getPreliminaryDiscussion() == null
                        || issue.getPreliminaryDiscussion().isBlank()
                        || issue.getResearchLeads() == null) {
                    throw new IllegalStateException(
                            "Draft must contain one complete issue per planned topic");
                }
                validateTextList(issue.getResearchGaps(), true);
                Set<Long> used = new HashSet<>();
                for (var lead : issue.getResearchLeads()) {
                    if (lead == null
                            || !used.add(lead.getClusterId())
                            || lead.getResearchQuestionIndexes() == null
                            || lead.getResearchQuestionIndexes().isEmpty()
                            || new HashSet<>(lead.getResearchQuestionIndexes()).size()
                                    != lead.getResearchQuestionIndexes().size()
                            || lead.getRelevance() == null
                            || lead.getRelevance().isBlank()
                            || lead.getLimitation() == null
                            || lead.getLimitation().isBlank()) {
                        throw new IllegalStateException(
                                "Research leads require unique cases, questions, relevance and"
                                    + " limitations");
                    }
                    for (Integer index : lead.getResearchQuestionIndexes()) {
                        boolean allowed =
                                context.getEvidenceAssessment().getQuestionAssessments().stream()
                                        .anyMatch(
                                                question ->
                                                        question.getTopicId()
                                                                        .equals(issue.getTopicId())
                                                                && Objects.equals(
                                                                        question
                                                                                .getResearchQuestionIndex(),
                                                                        index)
                                                                && question
                                                                        .getResultAssessments()
                                                                        .stream()
                                                                        .anyMatch(
                                                                                result ->
                                                                                        Objects
                                                                                                .equals(
                                                                                                        result
                                                                                                                .getClusterId(),
                                                                                                        lead
                                                                                                                .getClusterId())));
                        if (!allowed)
                            throw new IllegalStateException(
                                    "Draft lead is not a potential lead for the cited topic and"
                                        + " question");
                    }
                    var source =
                            context.getResearchLeads().stream()
                                    .filter(group -> group.getTopicId().equals(issue.getTopicId()))
                                    .flatMap(group -> group.getResults().stream())
                                    .filter(
                                            result ->
                                                    Objects.equals(
                                                            result.getClusterId(),
                                                            lead.getClusterId()))
                                    .findFirst()
                                    .orElseThrow(
                                            () ->
                                                    new IllegalStateException(
                                                            "Draft case was not retrieved"));
                    // Source metadata is authoritative for identity, not the model's generated
                    // text.
                    lead.setCaseName(plainText(source.getCaseName()));
                    List<String> citations = new ArrayList<>();
                    if (source.getCitation() != null)
                        for (String citation : source.getCitation())
                            citations.add(plainText(citation));
                    lead.setCitations(citations);
                    lead.setCourt(plainText(source.getCourt()));
                    lead.setSourceUrl(source.getAbsoluteUrl());
                }
            }
            if (!covered.equals(topics))
                throw new IllegalStateException("Draft omitted a planned research topic");
            LOGGER.trace("validateAndEnrich completed; issues={}", covered.size());
        } catch (RuntimeException failure) {
            LOGGER.trace("validateAndEnrich failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    private void validateTextList(List<String> values, boolean requiredNonempty) {
        LOGGER.trace(
                "validateTextList(values={}, requiredNonempty={}) entered",
                LogSummary.describe(values),
                requiredNonempty);
        try {
            if (values == null
                    || (requiredNonempty && values.isEmpty())
                    || values.stream().anyMatch(value -> value == null || value.isBlank())) {
                throw new IllegalStateException(
                        "Draft lists must contain nonblank text and required follow-up or"
                            + " limitations");
            }
            LOGGER.trace("validateTextList completed");
        } catch (RuntimeException failure) {
            LOGGER.trace("validateTextList failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    private String plainText(String text) {
        LOGGER.trace("plainText(text={}) entered", LogSummary.describe(text));
        String result =
                text == null
                        ? null
                        : org.springframework.web.util.HtmlUtils.htmlUnescape(
                                text.replaceAll("<[^>]*>", ""));
        LOGGER.trace("plainText returning {}", LogSummary.describe(result));
        return result;
    }
}
