package com.demo.legalfirm.model.memorandums.draft;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.CaseInput;
import com.demo.legalfirm.model.research.ResearchPlan;
import com.demo.legalfirm.model.research.assessment.ResearchEvidenceAssessment;
import com.demo.legalfirm.model.research.courtlistener.CourtListenerResearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Carries local case context and only potentially relevant case-law candidates for drafting.
 *
 * @file MemorandumDraftInput.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public class MemorandumDraftInput {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumDraftInput.class);
    private CaseInput caseInput;
    private ResearchPlan researchPlan;
    private List<CourtListenerResearchResult> researchLeads;
    private ResearchEvidenceAssessment evidenceAssessment;

    public MemorandumDraftInput() {
        LOGGER.trace("MemorandumDraftInput entered");
        LOGGER.trace("MemorandumDraftInput completed");
    }

    public MemorandumDraftInput(
            CaseInput caseInput,
            ResearchPlan researchPlan,
            List<CourtListenerResearchResult> researchLeads,
            ResearchEvidenceAssessment evidenceAssessment) {
        LOGGER.trace("MemorandumDraftInput constructor entered");
        LOGGER.trace("caseInput={}", LogSummary.describe(caseInput));
        this.caseInput = caseInput;
        LOGGER.trace("researchPlan={}", LogSummary.describe(researchPlan));
        this.researchPlan = researchPlan;
        LOGGER.trace("researchLeads={}", LogSummary.describe(researchLeads));
        this.researchLeads = researchLeads;
        LOGGER.trace("evidenceAssessment={}", LogSummary.describe(evidenceAssessment));
        this.evidenceAssessment = evidenceAssessment;
        LOGGER.trace("MemorandumDraftInput constructor completed");
    }

    public CaseInput getCaseInput() {
        LOGGER.trace("getCaseInput entered");
        LOGGER.trace("getCaseInput returning {}", LogSummary.describe(caseInput));
        return caseInput;
    }

    public void setCaseInput(CaseInput caseInput) {
        LOGGER.trace("setCaseInput(value={}) entered", LogSummary.describe(caseInput));
        this.caseInput = caseInput;
        LOGGER.trace("setCaseInput completed");
    }

    public ResearchPlan getResearchPlan() {
        LOGGER.trace("getResearchPlan entered");
        LOGGER.trace("getResearchPlan returning {}", LogSummary.describe(researchPlan));
        return researchPlan;
    }

    public void setResearchPlan(ResearchPlan researchPlan) {
        LOGGER.trace("setResearchPlan(value={}) entered", LogSummary.describe(researchPlan));
        this.researchPlan = researchPlan;
        LOGGER.trace("setResearchPlan completed");
    }

    public List<CourtListenerResearchResult> getResearchLeads() {
        LOGGER.trace("getResearchLeads entered");
        LOGGER.trace("getResearchLeads returning {}", LogSummary.describe(researchLeads));
        return researchLeads;
    }

    public void setResearchLeads(List<CourtListenerResearchResult> researchLeads) {
        LOGGER.trace("setResearchLeads(value={}) entered", LogSummary.describe(researchLeads));
        this.researchLeads = researchLeads;
        LOGGER.trace("setResearchLeads completed");
    }

    public ResearchEvidenceAssessment getEvidenceAssessment() {
        LOGGER.trace("getEvidenceAssessment entered");
        LOGGER.trace("getEvidenceAssessment returning {}", LogSummary.describe(evidenceAssessment));
        return evidenceAssessment;
    }

    public void setEvidenceAssessment(ResearchEvidenceAssessment evidenceAssessment) {
        LOGGER.trace(
                "setEvidenceAssessment(value={}) entered", LogSummary.describe(evidenceAssessment));
        this.evidenceAssessment = evidenceAssessment;
        LOGGER.trace("setEvidenceAssessment completed");
    }
}
