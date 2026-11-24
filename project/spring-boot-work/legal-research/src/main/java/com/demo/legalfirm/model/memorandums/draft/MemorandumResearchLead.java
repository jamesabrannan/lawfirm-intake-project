package com.demo.legalfirm.model.memorandums.draft;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Links a potential case-law lead to assessed questions; source metadata is filled by Java.
 *
 * @file MemorandumResearchLead.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class MemorandumResearchLead {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumResearchLead.class);
    private Long clusterId;
    private List<Integer> researchQuestionIndexes;
    private String relevance;
    private String limitation;
    private String caseName;
    private List<String> citations;
    private String court;
    private String sourceUrl;

    public MemorandumResearchLead() {
        LOGGER.trace("MemorandumResearchLead entered");
        LOGGER.trace("MemorandumResearchLead completed");
    }

    public MemorandumResearchLead(
            Long clusterId,
            List<Integer> researchQuestionIndexes,
            String relevance,
            String limitation,
            String caseName,
            List<String> citations,
            String court,
            String sourceUrl) {
        LOGGER.trace("MemorandumResearchLead constructor entered");
        LOGGER.trace("clusterId={}", LogSummary.describe(clusterId));
        this.clusterId = clusterId;
        LOGGER.trace("researchQuestionIndexes={}", LogSummary.describe(researchQuestionIndexes));
        this.researchQuestionIndexes = researchQuestionIndexes;
        LOGGER.trace("relevance={}", LogSummary.describe(relevance));
        this.relevance = relevance;
        LOGGER.trace("limitation={}", LogSummary.describe(limitation));
        this.limitation = limitation;
        LOGGER.trace("caseName={}", LogSummary.describe(caseName));
        this.caseName = caseName;
        LOGGER.trace("citations={}", LogSummary.describe(citations));
        this.citations = citations;
        LOGGER.trace("court={}", LogSummary.describe(court));
        this.court = court;
        LOGGER.trace("sourceUrl={}", LogSummary.describe(sourceUrl));
        this.sourceUrl = sourceUrl;
        LOGGER.trace("MemorandumResearchLead constructor completed");
    }

    public Long getClusterId() {
        LOGGER.trace("getClusterId entered");
        LOGGER.trace("getClusterId returning {}", LogSummary.describe(clusterId));
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        LOGGER.trace("setClusterId(value={}) entered", LogSummary.describe(clusterId));
        this.clusterId = clusterId;
        LOGGER.trace("setClusterId completed");
    }

    public List<Integer> getResearchQuestionIndexes() {
        LOGGER.trace("getResearchQuestionIndexes entered");
        LOGGER.trace(
                "getResearchQuestionIndexes returning {}",
                LogSummary.describe(researchQuestionIndexes));
        return researchQuestionIndexes;
    }

    public void setResearchQuestionIndexes(List<Integer> researchQuestionIndexes) {
        LOGGER.trace(
                "setResearchQuestionIndexes(value={}) entered",
                LogSummary.describe(researchQuestionIndexes));
        this.researchQuestionIndexes = researchQuestionIndexes;
        LOGGER.trace("setResearchQuestionIndexes completed");
    }

    public String getRelevance() {
        LOGGER.trace("getRelevance entered");
        LOGGER.trace("getRelevance returning {}", LogSummary.describe(relevance));
        return relevance;
    }

    public void setRelevance(String relevance) {
        LOGGER.trace("setRelevance(value={}) entered", LogSummary.describe(relevance));
        this.relevance = relevance;
        LOGGER.trace("setRelevance completed");
    }

    public String getLimitation() {
        LOGGER.trace("getLimitation entered");
        LOGGER.trace("getLimitation returning {}", LogSummary.describe(limitation));
        return limitation;
    }

    public void setLimitation(String limitation) {
        LOGGER.trace("setLimitation(value={}) entered", LogSummary.describe(limitation));
        this.limitation = limitation;
        LOGGER.trace("setLimitation completed");
    }

    public String getCaseName() {
        LOGGER.trace("getCaseName entered");
        LOGGER.trace("getCaseName returning {}", LogSummary.describe(caseName));
        return caseName;
    }

    public void setCaseName(String caseName) {
        LOGGER.trace("setCaseName(value={}) entered", LogSummary.describe(caseName));
        this.caseName = caseName;
        LOGGER.trace("setCaseName completed");
    }

    public List<String> getCitations() {
        LOGGER.trace("getCitations entered");
        LOGGER.trace("getCitations returning {}", LogSummary.describe(citations));
        return citations;
    }

    public void setCitations(List<String> citations) {
        LOGGER.trace("setCitations(value={}) entered", LogSummary.describe(citations));
        this.citations = citations;
        LOGGER.trace("setCitations completed");
    }

    public String getCourt() {
        LOGGER.trace("getCourt entered");
        LOGGER.trace("getCourt returning {}", LogSummary.describe(court));
        return court;
    }

    public void setCourt(String court) {
        LOGGER.trace("setCourt(value={}) entered", LogSummary.describe(court));
        this.court = court;
        LOGGER.trace("setCourt completed");
    }

    public String getSourceUrl() {
        LOGGER.trace("getSourceUrl entered");
        LOGGER.trace("getSourceUrl returning {}", LogSummary.describe(sourceUrl));
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        LOGGER.trace("setSourceUrl(value={}) entered", LogSummary.describe(sourceUrl));
        this.sourceUrl = sourceUrl;
        LOGGER.trace("setSourceUrl completed");
    }
}
