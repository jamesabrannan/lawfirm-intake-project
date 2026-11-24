package com.demo.legalfirm.model.research.assessment;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Records relevance of a retrieved CourtListener cluster without asserting its holding.
 *
 * @file ResearchResultAssessment.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class ResearchResultAssessment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchResultAssessment.class);
    private Long clusterId;
    private ResearchResultClassification classification;
    private String reason;

    public ResearchResultAssessment() {
        LOGGER.trace("ResearchResultAssessment entered");
        LOGGER.trace("ResearchResultAssessment completed");
    }

    public ResearchResultAssessment(
            Long clusterId, ResearchResultClassification classification, String reason) {
        LOGGER.trace("ResearchResultAssessment constructor entered");
        LOGGER.trace("clusterId={}", LogSummary.describe(clusterId));
        this.clusterId = clusterId;
        LOGGER.trace("classification={}", LogSummary.describe(classification));
        this.classification = classification;
        LOGGER.trace("reason={}", LogSummary.describe(reason));
        this.reason = reason;
        LOGGER.trace("ResearchResultAssessment completed");
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

    public ResearchResultClassification getClassification() {
        LOGGER.trace("getClassification entered");
        LOGGER.trace("getClassification returning {}", LogSummary.describe(classification));
        return classification;
    }

    public void setClassification(ResearchResultClassification classification) {
        LOGGER.trace("setClassification(value={}) entered", LogSummary.describe(classification));
        this.classification = classification;
        LOGGER.trace("setClassification completed");
    }

    public String getReason() {
        LOGGER.trace("getReason entered");
        LOGGER.trace("getReason returning {}", LogSummary.describe(reason));
        return reason;
    }

    public void setReason(String reason) {
        LOGGER.trace("setReason(value={}) entered", LogSummary.describe(reason));
        this.reason = reason;
        LOGGER.trace("setReason completed");
    }
}
