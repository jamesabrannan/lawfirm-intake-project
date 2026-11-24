package com.demo.legalfirm.model.research.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines relevance categories; snippet-only assessment must never return supporting evidence.
 *
 * @file ResearchResultClassification.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public enum ResearchResultClassification {
    IRRELEVANT,
    POTENTIAL_LEAD,
    SUPPORTING_EVIDENCE;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResearchResultClassification.class);

    static {
        LOGGER.trace("ResearchResultClassification initialized; values={}", values().length);
    }
}
