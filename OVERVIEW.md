<!--
File: OVERVIEW.md
Author: James Brannan
Date: 2026-11-23
Description: Plain-language overview of the three law firm workflow demonstrations.
Copyright (c) 2026 James Brannan.
Licensed under the MIT License. See LICENSE in the repository root for the full terms.
SPDX-License-Identifier: MIT
-->

# Law Firm Workflow Demonstrations: Overview

These three demonstrations explore how software and AI could reduce the time staff spend assembling intake information, preparing preliminary research, and checking draft memorandums. They use fictional cases and documents. The goal is to give staff organized material to review, while keeping legal judgment and final approval with the attorney.

The short recordings show each demonstration executing and producing its results. You do not need to install or run the software to view them. GitHub may offer the videos as downloads.

## 1. Organizing an intake

[Watch the intake demonstration](n8n-run.mp4)

The workflow brings together client information, intake records, staff assignments, and supporting documents. It compares the documents listed as received with the intake checklist and determines whether a request for missing material is needed.

AI prepares an intake brief from the supplied information and document text, then a separate AI step checks the draft. A brief that passes that check is saved as a PDF. The workflow also prepares applicable missing-document requests and a handoff message for the paralegal. Messages are saved as drafts on disk; no emails are sent.

The recording shows the workflow running and the resulting files. Its purpose is to demonstrate how routine intake preparation can be organized before staff review.

## 2. Preparing a preliminary research memorandum

[Watch the research memorandum demonstration](spring-run.mp4)

An authenticated staff member starts preparation of their assigned research work. The application gathers the intake information and reads the supporting documents. AI identifies research questions, and the application searches CourtListener for potentially relevant cases. It then assesses those search results, drafts a preliminary memorandum, and saves it as a PDF.

The recording shows the request being made, processing completing, and the generated memorandum. This demonstrates a possible starting point for research and drafting, rather than completed legal research. Case leads, citations, factual statements, and conclusions still require verification.

## 3. Reviewing a memorandum against its source material

[Watch the memorandum review demonstration](jupyter-notebook-run.mp4)

This demonstration brings each memorandum together with its associated intake information and supporting documents. AI compares them and produces a separate written review for each memorandum, highlighting possible discrepancies, unsupported statements, missing evidence, and recommended follow-up. The reviews are displayed and saved as PDFs.

The recording shows the review process executing and its results. This is an additional consistency check for a staff member to consider; it does not independently verify the law or replace attorney review.

## How to interpret the demonstrations

The three examples represent related stages of work, but they run separately using prepared sample records. They do not yet form one connected case-management system. AI checking another AI response can still miss errors or introduce new ones.

My background is software engineering, and I do not have professional legal domain expertise. The scenarios and workflow assumptions would need refinement with attorneys and paralegals before practical use. Real client information would also require appropriate privacy, security, and operational controls.

For architecture, workflow diagrams, data relationships, and implementation decisions, see the [design document](DESIGN.md). For setup instructions, see the [README](README.md).
