package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;
import com.demo.legalfirm.model.caseinputs.details.CaseDetails;
import com.demo.legalfirm.model.memorandums.draft.MemorandumDraft;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Renders a validated, enriched preliminary memorandum with wrapping, pagination and source links.
 *
 * @file MemorandumPdfService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class MemorandumPdfService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumPdfService.class);

    public MemorandumPdfService() {
        LOGGER.trace("MemorandumPdfService entered");
        LOGGER.trace("MemorandumPdfService completed");
    }

    public byte[] render(MemorandumDraft draft, CaseDetails details) throws IOException {
        LOGGER.debug(
                "render(draft={}, details={}) entered",
                LogSummary.describe(draft),
                LogSummary.describe(details));
        try {
            if (draft == null
                    || details == null
                    || details.getClient() == null
                    || details.getIntake() == null
                    || !Objects.equals(draft.getMemorandumId(), details.getMemorandumId())
                    || !Objects.equals(draft.getIntakeId(), details.getIntake().getIntakeId())
                    || draft.getFactualBackground() == null
                    || draft.getIssues() == null
                    || draft.getMissingInformation() == null
                    || draft.getLawyerFollowUp() == null
                    || draft.getLimitations() == null) {
                throw new IllegalArgumentException(
                        "Validated draft and matching case details are required");
            }
            try (PDDocument document = new PDDocument();
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                var info = document.getDocumentInformation();
                info.setTitle(draft.getTitle());
                info.setAuthor("Legal Research Demo");
                info.setSubject(
                        "Preliminary memorandum for lawyer review; fictional demonstration data.");
                info.setCreationDate(Calendar.getInstance());
                info.setCustomMetadataValue(
                        "License",
                        "MIT License; Copyright (c) 2026 James Brannan; SPDX-License-Identifier:"
                                + " MIT");
                try (Layout layout = new Layout(document)) {
                    layout.title(draft.getTitle());
                    layout.text(
                            "Client: "
                                    + details.getClient().getFirstName()
                                    + " "
                                    + details.getClient().getLastName());
                    layout.text(
                            "Intake: "
                                    + draft.getIntakeId()
                                    + "    Memorandum: "
                                    + draft.getMemorandumId()
                                    + "    Generated: "
                                    + LocalDate.now());
                    layout.text(
                            "Requesting staff: "
                                    + (details.getRequestor() == null
                                            ? "Not supplied"
                                            : details.getRequestor().getStaffName()));
                    layout.text(
                            "Intake preparer: "
                                    + (details.getPreparer() == null
                                            ? "Not supplied"
                                            : details.getPreparer().getStaffName()));
                    layout.notice(
                            "DEMO - Fictional case materials. Preliminary automated research for"
                                    + " lawyer review, not final legal advice.");
                    layout.heading("Summary");
                    layout.text(draft.getSummary());
                    layout.heading("Factual background");
                    for (var fact : draft.getFactualBackground()) {
                        layout.text("[" + fact.getClassification() + "] " + fact.getStatement());
                        layout.caption(
                                "Source: "
                                        + fact.getSourceId()
                                        + (fact.getPageNumber() == null
                                                ? ""
                                                : ", page " + fact.getPageNumber()));
                    }
                    int number = 1;
                    for (var issue : draft.getIssues()) {
                        layout.heading(number++ + ". " + issue.getTitle());
                        layout.text(issue.getPreliminaryDiscussion());
                        if (!issue.getResearchLeads().isEmpty()) {
                            layout.keepLeadTogether(issue.getResearchLeads().get(0), 30);
                            layout.subheading(
                                    "Potential case-law leads - full-opinion review required");
                            for (var lead : issue.getResearchLeads()) {
                                layout.keepLeadTogether(lead, 0);
                                layout.subheading(
                                        lead.getCaseName() == null
                                                ? "CourtListener case " + lead.getClusterId()
                                                : lead.getCaseName());
                                if (lead.getCitations() != null && !lead.getCitations().isEmpty())
                                    layout.text(String.join("; ", lead.getCitations()));
                                if (lead.getCourt() != null) layout.caption(lead.getCourt());
                                layout.text(lead.getRelevance());
                                layout.text("Limitations: " + lead.getLimitation());
                                if (lead.getSourceUrl() != null) layout.link(lead.getSourceUrl());
                            }
                        }
                        layout.subheading("Research gaps");
                        layout.items(issue.getResearchGaps());
                    }
                    layout.heading("Missing information");
                    if (draft.getMissingInformation().isEmpty())
                        layout.text("No additional missing information identified in this draft.");
                    else layout.items(draft.getMissingInformation());
                    layout.heading("Lawyer follow-up");
                    layout.items(draft.getLawyerFollowUp());
                    layout.heading("Limitations");
                    layout.items(draft.getLimitations());
                }
                // Append footers after pagination is known; no body content is overwritten.
                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    try (var stream =
                            new PDPageContentStream(
                                    document,
                                    document.getPage(i),
                                    PDPageContentStream.AppendMode.APPEND,
                                    true,
                                    true)) {
                        stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                        stream.beginText();
                        stream.newLineAtOffset(54, 30);
                        stream.showText("PRELIMINARY - LAWYER REVIEW REQUIRED");
                        stream.endText();
                        stream.beginText();
                        stream.newLineAtOffset(475, 30);
                        stream.showText("Page " + (i + 1) + " of " + document.getNumberOfPages());
                        stream.endText();
                    }
                }
                document.save(bytes);
                LOGGER.info(
                        "Memorandum PDF rendered; memorandumId={}, pages={}, bytes={}",
                        details.getMemorandumId(),
                        document.getNumberOfPages(),
                        bytes.size());
                LOGGER.debug("render returning byteCount={}", bytes.size());
                return bytes.toByteArray();
            }
        } catch (IOException | RuntimeException failure) {
            LOGGER.warn(
                    "Memorandum PDF rendering failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    /**
     * Maintains page layout for one document. Standard PDF fonts support this English-language demo
     * without platform font paths. Unsupported glyphs are visibly replaced with '?' and counted in
     * warnings; a multilingual deployment should embed appropriately licensed fonts.
     *
     * @file MemorandumPdfService.java
     * @author James Brannan
     * @date 2026-11-22
     * @version 0.0.1-SNAPSHOT
     * @since 0.0.1-SNAPSHOT
     * @copyright Copyright (c) 2026 James Brannan.
     * @license MIT License. See LICENSE in the project root. SPDX-License-Identifier: MIT
     */
    private static final class Layout implements AutoCloseable {
        private static final Logger LOGGER = LoggerFactory.getLogger(Layout.class);
        private static final float LEFT = 54, WIDTH = 504, TOP = 720, BOTTOM = 58;
        private final PDDocument document;
        private final PDFont regular = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        private final PDFont bold = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
        private PDPage page;
        private PDPageContentStream stream;
        private float y;

        private Layout(PDDocument document) throws IOException {
            LOGGER.trace("Layout(document={}) entered", LogSummary.describe(document));
            this.document = document;
            newPage();
            LOGGER.trace("Layout completed");
        }

        private void newPage() throws IOException {
            LOGGER.trace("newPage entered");
            if (stream != null) stream.close();
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            stream.setFont(bold, 9);
            stream.beginText();
            stream.newLineAtOffset(LEFT, 756);
            stream.showText("LEGAL RESEARCH MEMORANDUM / PRELIMINARY");
            stream.endText();
            stream.moveTo(LEFT, 746);
            stream.lineTo(558, 746);
            stream.stroke();
            y = TOP;
            LOGGER.trace("newPage completed; pages={}", document.getNumberOfPages());
        }

        private void keepLeadTogether(
                com.demo.legalfirm.model.memorandums.draft.MemorandumResearchLead lead, float headingAllowance)
                throws IOException {
            LOGGER.trace("keepLeadTogether(lead={}, headingAllowance={}) entered", LogSummary.describe(lead), headingAllowance);
            String name =
                    lead.getCaseName() == null
                            ? "CourtListener case " + lead.getClusterId()
                            : lead.getCaseName();
            float height = headingAllowance + 5 + height(name, bold, 11, 15, 5);
            if (lead.getCitations() != null && !lead.getCitations().isEmpty())
                height += height(String.join("; ", lead.getCitations()), regular, 11, 14, 5);
            if (lead.getCourt() != null) height += height(lead.getCourt(), regular, 9, 11, 5);
            height += height(lead.getRelevance(), regular, 11, 14, 5);
            height += height("Limitations: " + lead.getLimitation(), regular, 11, 14, 5);
            if (lead.getSourceUrl() != null)
                height += height(lead.getSourceUrl(), regular, 9, 12, 8);
            if (height < TOP - BOTTOM && y - height < BOTTOM) newPage();
            LOGGER.trace("keepLeadTogether completed; estimatedHeight={}", height);
        }

        private float height(String value, PDFont font, float size, float leading, float after)
                throws IOException {
            LOGGER.trace("height(value={}, size={}) entered", LogSummary.describe(value), size);
            float height =
                    wrap(printable(value == null ? "" : value, font), font, size).size() * leading
                            + after;
            LOGGER.trace("height returning {}", height);
            return height;
        }

        private void title(String value) throws IOException {
            LOGGER.trace("title(value={}) entered", LogSummary.describe(value));
            paragraph(value, bold, 19, 24, 12, false);
            LOGGER.trace("title completed");
        }

        private void heading(String value) throws IOException {
            LOGGER.trace("heading(value={}) entered", LogSummary.describe(value));
            if (y - 110 < BOTTOM) newPage();
            y -= 12;
            paragraph(value, bold, 14, 18, 8, false);
            LOGGER.trace("heading completed");
        }

        private void subheading(String value) throws IOException {
            LOGGER.trace("subheading(value={}) entered", LogSummary.describe(value));
            if (y - 84 < BOTTOM) newPage();
            y -= 5;
            paragraph(value, bold, 11, 15, 5, false);
            LOGGER.trace("subheading completed");
        }

        private void text(String value) throws IOException {
            LOGGER.trace("text(value={}) entered", LogSummary.describe(value));
            paragraph(value, regular, 11, 14, 5, false);
            LOGGER.trace("text completed");
        }

        private void caption(String value) throws IOException {
            LOGGER.trace("caption(value={}) entered", LogSummary.describe(value));
            paragraph(value, regular, 9, 11, 5, false);
            LOGGER.trace("caption completed");
        }

        private void notice(String value) throws IOException {
            LOGGER.trace("notice(value={}) entered", LogSummary.describe(value));
            y -= 9;
            paragraph(value, bold, 10, 14, 7, false);
            LOGGER.trace("notice completed");
        }

        private void items(List<String> values) throws IOException {
            LOGGER.trace("items(values={}) entered", LogSummary.describe(values));
            for (String value : values) text("- " + value);
            LOGGER.trace("items completed");
        }

        private void link(String url) throws IOException {
            LOGGER.trace("link(url={}) entered", LogSummary.describe(url));
            // Only ordinary HTTP(S) source links can become actionable PDF annotations.
            java.net.URI uri = java.net.URI.create(url);
            boolean allowed =
                    ("https".equalsIgnoreCase(uri.getScheme())
                                    || "http".equalsIgnoreCase(uri.getScheme()))
                            && uri.getHost() != null
                            && uri.getUserInfo() == null;
            paragraph(url, regular, 9, 12, 8, allowed);
            LOGGER.trace("link completed; clickable={}", allowed);
        }

        private void paragraph(
                String value,
                PDFont font,
                float size,
                float leading,
                float after,
                boolean hyperlink)
                throws IOException {
            LOGGER.trace(
                    "paragraph(value={}, size={}, hyperlink={}) entered",
                    LogSummary.describe(value),
                    size,
                    hyperlink);
            String text = printable(value == null ? "" : value, font);
            List<String> lines = wrap(text, font, size);
            // Keep short paragraphs together and avoid a one-line start at the page foot.
            float reserve = Math.min(lines.size(), 3) * leading;
            if (y - reserve < BOTTOM) newPage();
            for (String line : lines) {
                if (y - leading < BOTTOM) newPage();
                stream.setFont(font, size);
                stream.setNonStrokingColor(
                        hyperlink ? 0.12f : 0f, hyperlink ? 0.24f : 0f, hyperlink ? 0.48f : 0f);
                stream.beginText();
                stream.newLineAtOffset(LEFT, y);
                stream.showText(line);
                stream.endText();
                if (hyperlink) {
                    var annotation = new PDAnnotationLink();
                    annotation.setRectangle(
                            new PDRectangle(
                                    LEFT, y - 2, font.getStringWidth(line) * size / 1000, leading));
                    var action = new PDActionURI();
                    action.setURI(value);
                    annotation.setAction(action);
                    var border = new org.apache.pdfbox.cos.COSArray();
                    border.add(org.apache.pdfbox.cos.COSInteger.ZERO);
                    border.add(org.apache.pdfbox.cos.COSInteger.ZERO);
                    border.add(org.apache.pdfbox.cos.COSInteger.ZERO);
                    annotation.setBorder(border);
                    page.getAnnotations().add(annotation);
                }
                y -= leading;
            }
            y -= after;
            LOGGER.trace("paragraph completed; lines={}", lines.size());
        }

        private List<String> wrap(String text, PDFont font, float size) throws IOException {
            LOGGER.trace("wrap(text={}, size={}) entered", LogSummary.describe(text), size);
            List<String> lines = new ArrayList<>();
            String line = "";
            for (String word : text.split("\\s+")) {
                String proposed = line.isEmpty() ? word : line + " " + word;
                if (font.getStringWidth(proposed) * size / 1000 <= WIDTH) {
                    line = proposed;
                    continue;
                }
                if (!line.isEmpty()) {
                    lines.add(line);
                    line = "";
                }
                // Long URLs and identifiers need character-level wrapping.
                for (int i = 0; i < word.length(); i++) {
                    String candidate = line + word.charAt(i);
                    if (!line.isEmpty() && font.getStringWidth(candidate) * size / 1000 > WIDTH) {
                        lines.add(line);
                        line = "";
                    }
                    line += word.charAt(i);
                }
            }
            if (!line.isEmpty()) lines.add(line);
            LOGGER.trace("wrap returning lines={}", lines.size());
            return lines;
        }

        private String printable(String text, PDFont font) throws IOException {
            LOGGER.trace("printable(text={}) entered", LogSummary.describe(text));
            String normalized =
                    text.replace('\u2011', '-').replace('\u2013', '-').replace('\u2014', '-');
            StringBuilder result = new StringBuilder();
            int replaced = 0;
            for (int offset = 0; offset < normalized.length(); ) {
                int point = normalized.codePointAt(offset);
                offset += Character.charCount(point);
                if (Character.isWhitespace(point)) {
                    result.append(' ');
                    continue;
                }
                String character = new String(Character.toChars(point));
                try {
                    font.encode(character);
                    result.append(character);
                } catch (IllegalArgumentException unsupported) {
                    result.append('?');
                    replaced++;
                }
            }
            if (replaced > 0)
                LOGGER.warn("PDF text contains unsupported glyphs; replacedCount={}", replaced);
            LOGGER.trace("printable returning length={}", result.length());
            return result.toString();
        }

        @Override
        public void close() throws IOException {
            LOGGER.trace("Layout.close entered");
            if (stream != null) stream.close();
            LOGGER.trace("Layout.close completed");
        }
    }
}
