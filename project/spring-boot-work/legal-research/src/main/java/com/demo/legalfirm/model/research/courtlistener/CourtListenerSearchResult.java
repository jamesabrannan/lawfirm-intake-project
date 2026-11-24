package com.demo.legalfirm.model.research.courtlistener;

import com.demo.legalfirm.logging.LogSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Holds case metadata and opinion snippets returned by CourtListener search.
 *
 * @file CourtListenerSearchResult.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourtListenerSearchResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourtListenerSearchResult.class);

    @JsonProperty("cluster_id")
    private Long clusterId;

    private String caseName;
    private List<String> citation;
    private String court;

    @JsonProperty("court_id")
    private String courtId;

    private String dateFiled;

    @JsonProperty("absolute_url")
    private String absoluteUrl;

    private String status;
    private List<CourtListenerOpinionSnippet> opinions;

    public CourtListenerSearchResult() {
        LOGGER.trace("CourtListenerSearchResult() entered");
        LOGGER.trace("CourtListenerSearchResult completed");
    }

    public CourtListenerSearchResult(
            Long clusterId,
            String caseName,
            List<String> citation,
            String court,
            String courtId,
            String dateFiled,
            String absoluteUrl,
            String status,
            List<CourtListenerOpinionSnippet> opinions) {
        LOGGER.trace(
                "CourtListenerSearchResult constructor entered; fields are summarized or redacted");
        LOGGER.trace("clusterId={}", LogSummary.describe(clusterId));
        this.clusterId = clusterId;
        LOGGER.trace("caseName={}", LogSummary.describe(caseName));
        this.caseName = caseName;
        LOGGER.trace("citation={}", LogSummary.describe(citation));
        this.citation = citation;
        LOGGER.trace("court={}", LogSummary.describe(court));
        this.court = court;
        LOGGER.trace("courtId={}", LogSummary.describe(courtId));
        this.courtId = courtId;
        LOGGER.trace("dateFiled={}", LogSummary.describe(dateFiled));
        this.dateFiled = dateFiled;
        LOGGER.trace("absoluteUrl={}", LogSummary.describe(absoluteUrl));
        this.absoluteUrl = absoluteUrl;
        LOGGER.trace("status={}", LogSummary.describe(status));
        this.status = status;
        LOGGER.trace("opinions={}", LogSummary.describe(opinions));
        this.opinions = opinions;
        LOGGER.trace("CourtListenerSearchResult completed");
    }

    public Long getClusterId() {
        LOGGER.trace("getClusterId() entered");
        LOGGER.trace("getClusterId returning {}", LogSummary.describe(clusterId));
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        LOGGER.trace("setClusterId(clusterId={}) entered", LogSummary.describe(clusterId));
        this.clusterId = clusterId;
        LOGGER.trace("setClusterId completed");
    }

    public String getCaseName() {
        LOGGER.trace("getCaseName() entered");
        LOGGER.trace("getCaseName returning {}", LogSummary.describe(caseName));
        return caseName;
    }

    public void setCaseName(String caseName) {
        LOGGER.trace("setCaseName(caseName={}) entered", LogSummary.describe(caseName));
        this.caseName = caseName;
        LOGGER.trace("setCaseName completed");
    }

    public List<String> getCitation() {
        LOGGER.trace("getCitation() entered");
        LOGGER.trace("getCitation returning {}", LogSummary.describe(citation));
        return citation;
    }

    public void setCitation(List<String> citation) {
        LOGGER.trace("setCitation(citation={}) entered", LogSummary.describe(citation));
        this.citation = citation;
        LOGGER.trace("setCitation completed");
    }

    public String getCourt() {
        LOGGER.trace("getCourt() entered");
        LOGGER.trace("getCourt returning {}", LogSummary.describe(court));
        return court;
    }

    public void setCourt(String court) {
        LOGGER.trace("setCourt(court={}) entered", LogSummary.describe(court));
        this.court = court;
        LOGGER.trace("setCourt completed");
    }

    public String getCourtId() {
        LOGGER.trace("getCourtId() entered");
        LOGGER.trace("getCourtId returning {}", LogSummary.describe(courtId));
        return courtId;
    }

    public void setCourtId(String courtId) {
        LOGGER.trace("setCourtId(courtId={}) entered", LogSummary.describe(courtId));
        this.courtId = courtId;
        LOGGER.trace("setCourtId completed");
    }

    public String getDateFiled() {
        LOGGER.trace("getDateFiled() entered");
        LOGGER.trace("getDateFiled returning {}", LogSummary.describe(dateFiled));
        return dateFiled;
    }

    public void setDateFiled(String dateFiled) {
        LOGGER.trace("setDateFiled(dateFiled={}) entered", LogSummary.describe(dateFiled));
        this.dateFiled = dateFiled;
        LOGGER.trace("setDateFiled completed");
    }

    public String getAbsoluteUrl() {
        LOGGER.trace("getAbsoluteUrl() entered");
        LOGGER.trace("getAbsoluteUrl returning {}", LogSummary.describe(absoluteUrl));
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl) {
        LOGGER.trace("setAbsoluteUrl(absoluteUrl={}) entered", LogSummary.describe(absoluteUrl));
        this.absoluteUrl = absoluteUrl;
        LOGGER.trace("setAbsoluteUrl completed");
    }

    public String getStatus() {
        LOGGER.trace("getStatus() entered");
        LOGGER.trace("getStatus returning {}", LogSummary.describe(status));
        return status;
    }

    public void setStatus(String status) {
        LOGGER.trace("setStatus(status={}) entered", LogSummary.describe(status));
        this.status = status;
        LOGGER.trace("setStatus completed");
    }

    public List<CourtListenerOpinionSnippet> getOpinions() {
        LOGGER.trace("getOpinions() entered");
        LOGGER.trace("getOpinions returning {}", LogSummary.describe(opinions));
        return opinions;
    }

    public void setOpinions(List<CourtListenerOpinionSnippet> opinions) {
        LOGGER.trace("setOpinions(opinions={}) entered", LogSummary.describe(opinions));
        this.opinions = opinions;
        LOGGER.trace("setOpinions completed");
    }
}
