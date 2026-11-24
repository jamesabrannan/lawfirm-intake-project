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

The short recordings show each demonstration executing and producing its results. The videos are too large for GitHub’s inline playback. Open a video link, select **Download raw file** (the downward-arrow icon in the upper-right corner of the file viewer), and open the downloaded MP4 in your video player. You do not need to install or run the demo software to view them.

[!NOTE]
If a video does not play on GitHub, open its link and select **Download raw file** (the downward-arrow icon in the upper-right corner of the file viewer, above the preview area). Open the downloaded MP4 in your video player.

## 1. Organizing an intake

[Watch the intake demonstration](n8n-run.mp4)

The workflow brings together client information, intake records, staff assignments, and supporting documents. It checks the documents listed as received against the intake checklist. When documents are missing and a request has not already been sent, AI drafts an email asking the client to provide them. A separate AI step reviews the email before it is saved.

AI also prepares an intake brief from the supplied information and document text. A separate AI step checks the brief, and an approved brief is saved as a PDF. When a staff handoff is needed, the workflow prepares an email notifying the assigned paralegal that the brief is ready for review.

For this demonstration, emails are saved as text files rather than sent. The recording shows the workflow running and the resulting intake briefs and email files.

## 2. Preparing a preliminary research memorandum

[Watch the research memorandum demonstration](spring-run.mp4)

The application brings together intake information, an intake brief, and supporting documents for research work assigned to a staff member. After the staff member signs in and requests preparation, AI uses those materials to identify research questions and plan searches. The application then searches CourtListener, a legal research service, for potentially relevant court decisions.

AI assesses the returned case summaries and excerpts, identifies gaps in the research, and drafts a preliminary memorandum using the intake materials and research results. The application saves the memorandum as a PDF and marks the assignment as completed.

The recording shows the staff member’s request, the application processing the assigned work, and the resulting memorandum. The document provides a starting point for attorney review; its case references, factual statements, and legal conclusions still require verification.

## 3. Reviewing a memorandum against its source material

[Watch the memorandum review demonstration](jupyter-notebook-run.mp4)

The review workflow brings together each preliminary memorandum with its associated intake information and supporting documents. A staff member runs the review in a Jupyter notebook, an interactive workspace that displays the processing steps and results.

AI compares each memorandum against those materials and prepares a separate written review. It highlights possible factual discrepancies, unsupported statements, conflicting accounts, missing evidence, and recommended corrections or follow-up. The reviews appear in the notebook and are saved as PDFs.

The recording shows the review process and the resulting reports. This provides an additional check before attorney review; it does not independently verify legal authorities or establish that a memorandum is accurate or complete.

## How to interpret the demonstrations

The three examples represent related stages of work, but they run separately using prepared sample records. They do not yet form one connected case-management system. AI checking another AI response can still miss errors or introduce new ones.

> [!NOTE]
> My background is software engineering, and I do not have professional legal domain expertise. The scenarios and workflow assumptions would need refinement with attorneys and paralegals before practical use. Real client information would also require appropriate privacy, security, and operational controls.

For architecture, workflow diagrams, data relationships, and implementation decisions, see the [design document](DESIGN.md). For setup instructions, see the [README](README.md).
