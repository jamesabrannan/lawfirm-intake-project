package com.demo.legalfirm.model.memorandums.draft;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.research.facts.ResearchFact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Structures a preliminary lawyer-review draft; it does not represent a saved memorandum PDF.
 *
 * @file MemorandumDraft.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class MemorandumDraft {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumDraft.class);
    private Long memorandumId;
    private String intakeId;
    private String title;
    private String summary;
    private List<ResearchFact> factualBackground;
    private List<MemorandumIssueDraft> issues;
    private List<String> missingInformation;
    private List<String> lawyerFollowUp;
    private List<String> limitations;

    public MemorandumDraft() {
        LOGGER.trace("MemorandumDraft entered");
        LOGGER.trace("MemorandumDraft completed");
    }

    public MemorandumDraft(
            Long memorandumId,
            String intakeId,
            String title,
            String summary,
            List<ResearchFact> factualBackground,
            List<MemorandumIssueDraft> issues,
            List<String> missingInformation,
            List<String> lawyerFollowUp,
            List<String> limitations) {
        LOGGER.trace("MemorandumDraft constructor entered");
        LOGGER.trace("memorandumId={}", LogSummary.describe(memorandumId));
        this.memorandumId = memorandumId;
        LOGGER.trace("intakeId={}", LogSummary.describe(intakeId));
        this.intakeId = intakeId;
        LOGGER.trace("title={}", LogSummary.describe(title));
        this.title = title;
        LOGGER.trace("summary={}", LogSummary.describe(summary));
        this.summary = summary;
        LOGGER.trace("factualBackground={}", LogSummary.describe(factualBackground));
        this.factualBackground = factualBackground;
        LOGGER.trace("issues={}", LogSummary.describe(issues));
        this.issues = issues;
        LOGGER.trace("missingInformation={}", LogSummary.describe(missingInformation));
        this.missingInformation = missingInformation;
        LOGGER.trace("lawyerFollowUp={}", LogSummary.describe(lawyerFollowUp));
        this.lawyerFollowUp = lawyerFollowUp;
        LOGGER.trace("limitations={}", LogSummary.describe(limitations));
        this.limitations = limitations;
        LOGGER.trace("MemorandumDraft constructor completed");
    }

    public Long getMemorandumId() {
        LOGGER.trace("getMemorandumId entered");
        LOGGER.trace("getMemorandumId returning {}", LogSummary.describe(memorandumId));
        return memorandumId;
    }

    public void setMemorandumId(Long memorandumId) {
        LOGGER.trace("setMemorandumId(value={}) entered", LogSummary.describe(memorandumId));
        this.memorandumId = memorandumId;
        LOGGER.trace("setMemorandumId completed");
    }

    public String getIntakeId() {
        LOGGER.trace("getIntakeId entered");
        LOGGER.trace("getIntakeId returning {}", LogSummary.describe(intakeId));
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        LOGGER.trace("setIntakeId(value={}) entered", LogSummary.describe(intakeId));
        this.intakeId = intakeId;
        LOGGER.trace("setIntakeId completed");
    }

    public String getTitle() {
        LOGGER.trace("getTitle entered");
        LOGGER.trace("getTitle returning {}", LogSummary.describe(title));
        return title;
    }

    public void setTitle(String title) {
        LOGGER.trace("setTitle(value={}) entered", LogSummary.describe(title));
        this.title = title;
        LOGGER.trace("setTitle completed");
    }

    public String getSummary() {
        LOGGER.trace("getSummary entered");
        LOGGER.trace("getSummary returning {}", LogSummary.describe(summary));
        return summary;
    }

    public void setSummary(String summary) {
        LOGGER.trace("setSummary(value={}) entered", LogSummary.describe(summary));
        this.summary = summary;
        LOGGER.trace("setSummary completed");
    }

    public List<ResearchFact> getFactualBackground() {
        LOGGER.trace("getFactualBackground entered");
        LOGGER.trace("getFactualBackground returning {}", LogSummary.describe(factualBackground));
        return factualBackground;
    }

    public void setFactualBackground(List<ResearchFact> factualBackground) {
        LOGGER.trace(
                "setFactualBackground(value={}) entered", LogSummary.describe(factualBackground));
        this.factualBackground = factualBackground;
        LOGGER.trace("setFactualBackground completed");
    }

    public List<MemorandumIssueDraft> getIssues() {
        LOGGER.trace("getIssues entered");
        LOGGER.trace("getIssues returning {}", LogSummary.describe(issues));
        return issues;
    }

    public void setIssues(List<MemorandumIssueDraft> issues) {
        LOGGER.trace("setIssues(value={}) entered", LogSummary.describe(issues));
        this.issues = issues;
        LOGGER.trace("setIssues completed");
    }

    public List<String> getMissingInformation() {
        LOGGER.trace("getMissingInformation entered");
        LOGGER.trace("getMissingInformation returning {}", LogSummary.describe(missingInformation));
        return missingInformation;
    }

    public void setMissingInformation(List<String> missingInformation) {
        LOGGER.trace(
                "setMissingInformation(value={}) entered", LogSummary.describe(missingInformation));
        this.missingInformation = missingInformation;
        LOGGER.trace("setMissingInformation completed");
    }

    public List<String> getLawyerFollowUp() {
        LOGGER.trace("getLawyerFollowUp entered");
        LOGGER.trace("getLawyerFollowUp returning {}", LogSummary.describe(lawyerFollowUp));
        return lawyerFollowUp;
    }

    public void setLawyerFollowUp(List<String> lawyerFollowUp) {
        LOGGER.trace("setLawyerFollowUp(value={}) entered", LogSummary.describe(lawyerFollowUp));
        this.lawyerFollowUp = lawyerFollowUp;
        LOGGER.trace("setLawyerFollowUp completed");
    }

    public List<String> getLimitations() {
        LOGGER.trace("getLimitations entered");
        LOGGER.trace("getLimitations returning {}", LogSummary.describe(limitations));
        return limitations;
    }

    public void setLimitations(List<String> limitations) {
        LOGGER.trace("setLimitations(value={}) entered", LogSummary.describe(limitations));
        this.limitations = limitations;
        LOGGER.trace("setLimitations completed");
    }
}
