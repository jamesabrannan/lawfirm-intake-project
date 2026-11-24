package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.assessment.*;
import com.demo.legalfirm.model.research.courtlistener.CourtListenerResearchResult;

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
 * Assesses retrieved snippets against each research question and validates all references.
 *
 * @file ResearchEvidenceAssessmentService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class ResearchEvidenceAssessmentService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResearchEvidenceAssessmentService.class);
    private final ChatClient chatClient;
    private final ObjectMapper mapper;
    private final String instructions;

    public ResearchEvidenceAssessmentService(
            ChatClient.Builder builder,
            ObjectMapper mapper,
            @Value("classpath:prompts/research-evidence-assessment.txt") Resource prompt)
            throws IOException {
        LOGGER.trace(
                "ResearchEvidenceAssessmentService(builder={}, mapper={}, prompt={}) entered",
                LogSummary.describe(builder),
                LogSummary.describe(mapper),
                LogSummary.describe(prompt));
        try {
            this.chatClient = builder.build();
            this.mapper = mapper;
            this.instructions = prompt.getContentAsString(StandardCharsets.UTF_8);
            LOGGER.trace("ResearchEvidenceAssessmentService completed");
        } catch (IOException | RuntimeException failure) {
            LOGGER.trace(
                    "ResearchEvidenceAssessmentService failed; type={}",
                    failure.getClass().getSimpleName());
            throw failure;
        }
    }

    public ResearchEvidenceAssessment assess(
            ResearchPlan plan, List<CourtListenerResearchResult> results) {
        LOGGER.debug(
                "assess(plan={}, results={}) entered",
                LogSummary.describe(plan),
                LogSummary.describe(results));
        try {
            Map<String, Set<Long>> candidates = candidates(plan, results);
            ResearchEvidenceAssessment assessment;
            if (plan.getTopics().isEmpty()) {
                assessment = new ResearchEvidenceAssessment(List.of());
            } else {
                LOGGER.info(
                        "Starting AI evidence assessment; topics={}, topicQueryResults={}",
                        plan.getTopics().size(),
                        results.size());
                // Only the plan and bounded search results are sent; local PDFs are not resent.
                String input =
                        mapper.writeValueAsString(
                                Map.of("researchPlan", plan, "retrievedResults", results));
                BeanOutputConverter<ResearchEvidenceAssessment> converter =
                        new BeanOutputConverter<>(ResearchEvidenceAssessment.class);
                String systemPrompt = instructions + "\n\n" + converter.getFormat();
                LOGGER.debug(
                        "OpenAI research evidence assessment prompt:\nSYSTEM:\n{}\nUSER:\n{}",
                        systemPrompt,
                        input);
                String responseJson =
                        chatClient.prompt().system(systemPrompt).user(input).call().content();
                LOGGER.debug("Research evidence assessment JSON:\n{}", responseJson);
                assessment = converter.convert(responseJson);
            }
            validateAssessment(assessment, plan, candidates);
            LOGGER.info(
                    "Evidence assessment completed; questions={}",
                    assessment.getQuestionAssessments().size());
            LOGGER.debug("assess returning {}", LogSummary.describe(assessment));
            return assessment;
        } catch (RuntimeException failure) {
            LOGGER.warn("Evidence assessment failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    private Map<String, Set<Long>> candidates(
            ResearchPlan plan, List<CourtListenerResearchResult> results) {
        LOGGER.trace(
                "candidates(plan={}, results={}) entered",
                LogSummary.describe(plan),
                LogSummary.describe(results));
        try {
            if (plan == null || plan.getTopics() == null || results == null) {
                throw new IllegalArgumentException("Plan and retrieved results are required");
            }
            Map<String, Set<Long>> candidates = new LinkedHashMap<>();
            for (var topic : plan.getTopics()) {
                if (topic == null
                        || topic.getId() == null
                        || topic.getId().isBlank()
                        || topic.getResearchQuestions() == null
                        || topic.getResearchQuestions().isEmpty()
                        || topic.getResearchQuestions().stream()
                                .anyMatch(q -> q == null || q.isBlank())
                        || candidates.putIfAbsent(topic.getId(), new HashSet<>()) != null) {
                    throw new IllegalArgumentException(
                            "Topics require unique IDs and nonblank research questions");
                }
            }
            for (var group : results) {
                if (group == null
                        || !candidates.containsKey(group.getTopicId())
                        || group.getResults() == null) {
                    throw new IllegalArgumentException(
                            "Search results must reference planned topics");
                }
                for (var result : group.getResults()) {
                    if (result == null || result.getClusterId() == null) {
                        throw new IllegalArgumentException("Retrieved cases require cluster IDs");
                    }
                    candidates.get(group.getTopicId()).add(result.getClusterId());
                }
            }
            LOGGER.trace("candidates returning topics={}", candidates.size());
            return candidates;
        } catch (RuntimeException failure) {
            LOGGER.trace("candidates failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    private void validateAssessment(
            ResearchEvidenceAssessment assessment,
            ResearchPlan plan,
            Map<String, Set<Long>> candidates) {
        LOGGER.trace(
                "validateAssessment(assessment={}, plan={}, candidates={}) entered",
                LogSummary.describe(assessment),
                LogSummary.describe(plan),
                LogSummary.describe(candidates));
        try {
            if (assessment == null || assessment.getQuestionAssessments() == null) {
                throw new IllegalStateException("AI must return question assessments");
            }
            Map<String, Integer> expected = new HashMap<>();
            for (var topic : plan.getTopics()) {
                expected.put(topic.getId(), topic.getResearchQuestions().size());
            }
            Map<String, Set<Integer>> seen = new HashMap<>();
            for (var question : assessment.getQuestionAssessments()) {
                if (question == null
                        || !expected.containsKey(question.getTopicId())
                        || question.getResearchQuestionIndex() == null
                        || question.getResearchQuestionIndex() < 0
                        || question.getResearchQuestionIndex()
                                >= expected.get(question.getTopicId())
                        || !seen.computeIfAbsent(question.getTopicId(), key -> new HashSet<>())
                                .add(question.getResearchQuestionIndex())
                        || question.getUsefulLeadsFound() == null
                        || question.getResultAssessments() == null
                        || question.getResearchGaps() == null
                        || question.getResearchGaps().stream()
                                .anyMatch(g -> g == null || g.isBlank())) {
                    throw new IllegalStateException(
                            "Invalid, duplicate or incomplete question assessment");
                }
                Set<Long> assessed = new HashSet<>();
                boolean useful = false;
                for (var result : question.getResultAssessments()) {
                    if (result == null
                            || !candidates
                                    .get(question.getTopicId())
                                    .contains(result.getClusterId())
                            || !assessed.add(result.getClusterId())
                            || result.getClassification() == null
                            || result.getReason() == null
                            || result.getReason().isBlank()) {
                        throw new IllegalStateException(
                                "Case assessments require retrieved IDs, classifications and"
                                        + " reasons");
                    }
                    if (result.getClassification()
                            == ResearchResultClassification.SUPPORTING_EVIDENCE) {
                        throw new IllegalStateException(
                                "Snippet-only results cannot be supporting evidence");
                    }
                    useful |=
                            result.getClassification()
                                    == ResearchResultClassification.POTENTIAL_LEAD;
                }
                if (!assessed.equals(candidates.get(question.getTopicId()))
                        || useful != question.getUsefulLeadsFound()
                        || question.getResearchGaps().isEmpty()) {
                    throw new IllegalStateException(
                            "Assess every candidate and disclose snippet-only research gaps");
                }
            }
            for (var entry : expected.entrySet()) {
                if (seen.getOrDefault(entry.getKey(), Set.of()).size() != entry.getValue()) {
                    throw new IllegalStateException(
                            "Every planned research question must be assessed");
                }
            }
            LOGGER.trace(
                    "validateAssessment completed; questions={}",
                    assessment.getQuestionAssessments().size());
        } catch (RuntimeException failure) {
            LOGGER.trace("validateAssessment failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
