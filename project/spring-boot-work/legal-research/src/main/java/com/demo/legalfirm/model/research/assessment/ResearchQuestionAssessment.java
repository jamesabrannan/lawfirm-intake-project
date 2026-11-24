package com.demo.legalfirm.model.research.assessment;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Records candidate-case relevance and research gaps for one zero-based question index.
 *
 * @file ResearchQuestionAssessment.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ResearchQuestionAssessment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchQuestionAssessment.class);
    private String topicId;
    private Integer researchQuestionIndex;
    private Boolean usefulLeadsFound;
    private List<ResearchResultAssessment> resultAssessments;
    private List<String> researchGaps;

    public ResearchQuestionAssessment() {
        LOGGER.trace("ResearchQuestionAssessment entered");
        LOGGER.trace("ResearchQuestionAssessment completed");
    }

    public ResearchQuestionAssessment(
            String topicId,
            Integer researchQuestionIndex,
            Boolean usefulLeadsFound,
            List<ResearchResultAssessment> resultAssessments,
            List<String> researchGaps) {
        LOGGER.trace("ResearchQuestionAssessment constructor entered");
        LOGGER.trace("topicId={}", LogSummary.describe(topicId));
        this.topicId = topicId;
        LOGGER.trace("researchQuestionIndex={}", LogSummary.describe(researchQuestionIndex));
        this.researchQuestionIndex = researchQuestionIndex;
        LOGGER.trace("usefulLeadsFound={}", LogSummary.describe(usefulLeadsFound));
        this.usefulLeadsFound = usefulLeadsFound;
        LOGGER.trace("resultAssessments={}", LogSummary.describe(resultAssessments));
        this.resultAssessments = resultAssessments;
        LOGGER.trace("researchGaps={}", LogSummary.describe(researchGaps));
        this.researchGaps = researchGaps;
        LOGGER.trace("ResearchQuestionAssessment completed");
    }

    public String getTopicId() {
        LOGGER.trace("getTopicId entered");
        LOGGER.trace("getTopicId returning {}", LogSummary.describe(topicId));
        return topicId;
    }

    public void setTopicId(String topicId) {
        LOGGER.trace("setTopicId(value={}) entered", LogSummary.describe(topicId));
        this.topicId = topicId;
        LOGGER.trace("setTopicId completed");
    }

    public Integer getResearchQuestionIndex() {
        LOGGER.trace("getResearchQuestionIndex entered");
        LOGGER.trace(
                "getResearchQuestionIndex returning {}",
                LogSummary.describe(researchQuestionIndex));
        return researchQuestionIndex;
    }

    public void setResearchQuestionIndex(Integer researchQuestionIndex) {
        LOGGER.trace(
                "setResearchQuestionIndex(value={}) entered",
                LogSummary.describe(researchQuestionIndex));
        this.researchQuestionIndex = researchQuestionIndex;
        LOGGER.trace("setResearchQuestionIndex completed");
    }

    public Boolean getUsefulLeadsFound() {
        LOGGER.trace("getUsefulLeadsFound entered");
        LOGGER.trace("getUsefulLeadsFound returning {}", LogSummary.describe(usefulLeadsFound));
        return usefulLeadsFound;
    }

    public void setUsefulLeadsFound(Boolean usefulLeadsFound) {
        LOGGER.trace(
                "setUsefulLeadsFound(value={}) entered", LogSummary.describe(usefulLeadsFound));
        this.usefulLeadsFound = usefulLeadsFound;
        LOGGER.trace("setUsefulLeadsFound completed");
    }

    public List<ResearchResultAssessment> getResultAssessments() {
        LOGGER.trace("getResultAssessments entered");
        LOGGER.trace("getResultAssessments returning {}", LogSummary.describe(resultAssessments));
        return resultAssessments;
    }

    public void setResultAssessments(List<ResearchResultAssessment> resultAssessments) {
        LOGGER.trace(
                "setResultAssessments(value={}) entered", LogSummary.describe(resultAssessments));
        this.resultAssessments = resultAssessments;
        LOGGER.trace("setResultAssessments completed");
    }

    public List<String> getResearchGaps() {
        LOGGER.trace("getResearchGaps entered");
        LOGGER.trace("getResearchGaps returning {}", LogSummary.describe(researchGaps));
        return researchGaps;
    }

    public void setResearchGaps(List<String> researchGaps) {
        LOGGER.trace("setResearchGaps(value={}) entered", LogSummary.describe(researchGaps));
        this.researchGaps = researchGaps;
        LOGGER.trace("setResearchGaps completed");
    }
}
