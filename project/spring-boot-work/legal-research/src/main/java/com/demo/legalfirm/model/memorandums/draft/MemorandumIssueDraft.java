package com.demo.legalfirm.model.memorandums.draft;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Discusses one planned topic using assessed leads and explicit unresolved research gaps.
 *
 * @file MemorandumIssueDraft.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class MemorandumIssueDraft {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumIssueDraft.class);
    private String topicId;
    private String title;
    private String preliminaryDiscussion;
    private List<MemorandumResearchLead> researchLeads;
    private List<String> researchGaps;

    public MemorandumIssueDraft() {
        LOGGER.trace("MemorandumIssueDraft entered");
        LOGGER.trace("MemorandumIssueDraft completed");
    }

    public MemorandumIssueDraft(
            String topicId,
            String title,
            String preliminaryDiscussion,
            List<MemorandumResearchLead> researchLeads,
            List<String> researchGaps) {
        LOGGER.trace("MemorandumIssueDraft constructor entered");
        LOGGER.trace("topicId={}", LogSummary.describe(topicId));
        this.topicId = topicId;
        LOGGER.trace("title={}", LogSummary.describe(title));
        this.title = title;
        LOGGER.trace("preliminaryDiscussion={}", LogSummary.describe(preliminaryDiscussion));
        this.preliminaryDiscussion = preliminaryDiscussion;
        LOGGER.trace("researchLeads={}", LogSummary.describe(researchLeads));
        this.researchLeads = researchLeads;
        LOGGER.trace("researchGaps={}", LogSummary.describe(researchGaps));
        this.researchGaps = researchGaps;
        LOGGER.trace("MemorandumIssueDraft constructor completed");
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

    public String getPreliminaryDiscussion() {
        LOGGER.trace("getPreliminaryDiscussion entered");
        LOGGER.trace(
                "getPreliminaryDiscussion returning {}",
                LogSummary.describe(preliminaryDiscussion));
        return preliminaryDiscussion;
    }

    public void setPreliminaryDiscussion(String preliminaryDiscussion) {
        LOGGER.trace(
                "setPreliminaryDiscussion(value={}) entered",
                LogSummary.describe(preliminaryDiscussion));
        this.preliminaryDiscussion = preliminaryDiscussion;
        LOGGER.trace("setPreliminaryDiscussion completed");
    }

    public List<MemorandumResearchLead> getResearchLeads() {
        LOGGER.trace("getResearchLeads entered");
        LOGGER.trace("getResearchLeads returning {}", LogSummary.describe(researchLeads));
        return researchLeads;
    }

    public void setResearchLeads(List<MemorandumResearchLead> researchLeads) {
        LOGGER.trace("setResearchLeads(value={}) entered", LogSummary.describe(researchLeads));
        this.researchLeads = researchLeads;
        LOGGER.trace("setResearchLeads completed");
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
