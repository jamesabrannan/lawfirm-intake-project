package com.demo.legalfirm.model.caseinputs.documents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Distinguishes a generated intake brief from its supporting source documents.
 *
 * @file DocumentType.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public enum DocumentType {
    INTAKE_BRIEF,
    SUPPORTING_DOCUMENT;
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentType.class);

    static {
        LOGGER.trace("DocumentType initialized with 2 constants");
    }
}
