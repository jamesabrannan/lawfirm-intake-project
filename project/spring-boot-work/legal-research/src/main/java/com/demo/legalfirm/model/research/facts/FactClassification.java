package com.demo.legalfirm.model.research.facts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describes what kind of support a factual statement has in the supplied material.
 *
 * @file FactClassification.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public enum FactClassification {
    DOCUMENTED,
    ALLEGATION,
    CLIENT_REPORTED;
    private static final Logger LOGGER = LoggerFactory.getLogger(FactClassification.class);

    static {
        LOGGER.trace("FactClassification initialized with 3 constants");
    }
}
