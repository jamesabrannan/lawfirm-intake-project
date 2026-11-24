package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.caseinputs.documents.DocumentInput;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.ResearchTopic;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Produces a source-grounded research plan from assembled local case information.
 *
 * @file ResearchPlanningService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class ResearchPlanningService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchPlanningService.class);
    private final ChatClient chatClient;
    private final ObjectMapper mapper;
    private final String instructions;

    public ResearchPlanningService(
            ChatClient.Builder builder,
            ObjectMapper mapper,
            @Value("classpath:prompts/research-planning.txt") Resource prompt)
            throws IOException {
        LOGGER.trace(
                "ResearchPlanningService(builder={}, mapper={}, prompt={}) entered",
                LogSummary.describe(builder),
                LogSummary.describe(mapper),
                LogSummary.describe(prompt));
        try {
            this.chatClient = builder.build();
            this.mapper = mapper;
            this.instructions = prompt.getContentAsString(StandardCharsets.UTF_8);
            LOGGER.trace("ResearchPlanningService completed");
        } catch (IOException | RuntimeException failure) {
            LOGGER.trace(
                    "ResearchPlanningService failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    public ResearchPlan createPlan(CaseInput input) {
        LOGGER.debug("createPlan(input={}) entered", LogSummary.describe(input));
        try {
            Map<String, Set<Integer>> sources = sourcePages(input);
            LOGGER.info(
                    "Starting AI research planning; memorandumId={}, sources={}",
                    input.getDetails().getMemorandumId(),
                    sources.size());
            BeanOutputConverter<ResearchPlan> converter =
                    new BeanOutputConverter<>(ResearchPlan.class);
            String systemPrompt = instructions + "\n\n" + converter.getFormat();
            String userPrompt = mapper.writeValueAsString(input);
            // Explicit demo logging: prompt text only, without credentials or transport headers.
            LOGGER.debug(
                    "OpenAI research planning prompt:\nSYSTEM:\n{}\nUSER:\n{}",
                    systemPrompt,
                    userPrompt);
            String responseJson =
                    chatClient.prompt().system(systemPrompt).user(userPrompt).call().content();
            // Capture exactly what was returned, even if conversion or validation later fails.
            LOGGER.debug("Research plan JSON:\n{}", responseJson);
            ResearchPlan plan = converter.convert(responseJson);
            validatePlan(plan, sources);
            LOGGER.info(
                    "Research planning completed; memorandumId={}, topics={}",
                    input.getDetails().getMemorandumId(),
                    plan.getTopics().size());
            LOGGER.debug("createPlan returning {}", LogSummary.describe(plan));
            return plan;
        } catch (RuntimeException failure) {
            // Provider exceptions can contain prompts or response bodies. Log only the type.
            LOGGER.warn("Research planning failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    private Map<String, Set<Integer>> sourcePages(CaseInput input) {
        LOGGER.trace("sourcePages(input={}) entered", LogSummary.describe(input));
        try {
            if (input == null
                    || input.getDetails() == null
                    || input.getDetails().getIntake() == null
                    || input.getDocuments() == null) {
                throw new IllegalArgumentException("Assembled case input is required");
            }
            Map<String, Set<Integer>> sources = new HashMap<>();
            String intakeSource = input.getDetails().getIntake().getSourceId();
            if (intakeSource == null || intakeSource.isBlank()) {
                throw new IllegalArgumentException("Intake source ID is required");
            }
            sources.put(intakeSource, null);
            for (DocumentInput document : input.getDocuments()) {
                if (document == null
                        || document.getSourceId() == null
                        || document.getSourceId().isBlank()
                        || document.getPages() == null
                        || sources.containsKey(document.getSourceId())) {
                    throw new IllegalArgumentException(
                            "Documents require unique source IDs and pages");
                }
                Set<Integer> pages = new HashSet<>();
                for (var page : document.getPages()) {
                    if (page == null
                            || page.getPageNumber() < 1
                            || !pages.add(page.getPageNumber())) {
                        throw new IllegalArgumentException(
                                "Document page numbers must be positive and unique");
                    }
                }
                sources.put(document.getSourceId(), pages);
            }
            LOGGER.trace("sourcePages returning sourceCount={}", sources.size());
            return sources;
        } catch (RuntimeException failure) {
            LOGGER.trace("sourcePages failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    // Structural provenance checks cannot establish legal correctness or factual entailment.
    private void validatePlan(ResearchPlan plan, Map<String, Set<Integer>> sources) {
        LOGGER.trace(
                "validatePlan(plan={}, sources={}) entered",
                LogSummary.describe(plan),
                LogSummary.describe(sources));
        try {
            if (plan == null || plan.getTopics() == null || plan.getTopics().isEmpty()) {
                throw new IllegalStateException("AI must return at least one research topic");
            }
            Set<String> ids = new HashSet<>();
            for (ResearchTopic topic : plan.getTopics()) {
                if (topic == null
                        || topic.getId() == null
                        || topic.getId().isBlank()
                        || !ids.add(topic.getId())
                        || topic.getTitle() == null
                        || topic.getTitle().isBlank()
                        || topic.getFacts() == null
                        || topic.getFacts().isEmpty()) {
                    throw new IllegalStateException(
                            "Research topics require unique IDs, titles and facts");
                }
                validateStrings(topic.getMissingInformation());
                validateStrings(topic.getResearchQuestions());
                validateStrings(topic.getSearchQueries());
                validateStrings(topic.getRelatedTopicIds());
                if (topic.getResearchQuestions().isEmpty()) {
                    throw new IllegalStateException("Research topics require research questions");
                }
                for (ResearchFact fact : topic.getFacts()) {
                    if (fact == null
                            || fact.getStatement() == null
                            || fact.getStatement().isBlank()
                            || fact.getClassification() == null
                            || !sources.containsKey(fact.getSourceId())) {
                        throw new IllegalStateException(
                                "Research fact requires a statement, classification and known"
                                        + " source");
                    }
                    Set<Integer> pages = sources.get(fact.getSourceId());
                    if (pages == null
                            ? fact.getPageNumber() != null
                            : fact.getPageNumber() == null
                                    || !pages.contains(fact.getPageNumber())) {
                        // Demo: preserve the supplied reference for review without blocking
                        // planning.
                        // A page warning does not establish that the source supports the statement.
                        LOGGER.warn(
                                "Research fact has an unverified page reference; source={},"
                                    + " page={}; continuing for demo",
                                LogSummary.describe(fact.getSourceId()),
                                fact.getPageNumber());
                    }
                }
            }
            for (ResearchTopic topic : plan.getTopics()) {
                if (!ids.containsAll(topic.getRelatedTopicIds())) {
                    throw new IllegalStateException(
                            "Research topic references an unknown related topic");
                }
            }
            LOGGER.trace("validatePlan completed; topics={}", ids.size());
        } catch (RuntimeException failure) {
            LOGGER.trace("validatePlan failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    private void validateStrings(List<String> values) {
        LOGGER.trace("validateStrings(values={}) entered", LogSummary.describe(values));
        try {
            if (values == null
                    || values.stream().anyMatch(value -> value == null || value.isBlank())) {
                throw new IllegalStateException(
                        "Research topic lists must be present and contain nonblank values");
            }
            LOGGER.trace("validateStrings completed");
        } catch (RuntimeException failure) {
            LOGGER.trace("validateStrings failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
