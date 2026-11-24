package com.demo.legalfirm.model.research.assessment;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Contains relevance assessments for every planned research question.
 *
 * @file ResearchEvidenceAssessment.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ResearchEvidenceAssessment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchEvidenceAssessment.class);
    private List<ResearchQuestionAssessment> questionAssessments;

    public ResearchEvidenceAssessment() {
        LOGGER.trace("ResearchEvidenceAssessment entered");
        LOGGER.trace("ResearchEvidenceAssessment completed");
    }

    public ResearchEvidenceAssessment(List<ResearchQuestionAssessment> questionAssessments) {
        LOGGER.trace("ResearchEvidenceAssessment constructor entered");
        LOGGER.trace("questionAssessments={}", LogSummary.describe(questionAssessments));
        this.questionAssessments = questionAssessments;
        LOGGER.trace("ResearchEvidenceAssessment completed");
    }

    public List<ResearchQuestionAssessment> getQuestionAssessments() {
        LOGGER.trace("getQuestionAssessments entered");
        LOGGER.trace(
                "getQuestionAssessments returning {}", LogSummary.describe(questionAssessments));
        return questionAssessments;
    }

    public void setQuestionAssessments(List<ResearchQuestionAssessment> questionAssessments) {
        LOGGER.trace(
                "setQuestionAssessments(value={}) entered",
                LogSummary.describe(questionAssessments));
        this.questionAssessments = questionAssessments;
        LOGGER.trace("setQuestionAssessments completed");
    }
}
