package com.demo.legalfirm.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Produces concise logging summaries without revealing string contents or calling
 * application-object toString methods.
 *
 * @file LogSummary.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
public final class LogSummary {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogSummary.class);

    private LogSummary() {
        LOGGER.trace("LogSummary() entered");
        LOGGER.trace("LogSummary() completed");
    }

    /**
     * Strings are always redacted, including identifiers supplied as strings. Collections expose
     * size only; arbitrary objects expose their type only. Never traverse application getters or
     * log exception messages here.
     */
    public static String describe(Object value) {
        LOGGER.trace("describe(value=redacted) entered");
        String summary;
        if (value == null) {
            summary = "null";
        } else if (value instanceof CharSequence text) {
            summary = "redacted(length=" + text.length() + ")";
        } else if (value instanceof Collection<?> items) {
            summary = "collection(size=" + items.size() + ")";
        } else if (value instanceof Map<?, ?> entries) {
            summary = "map(size=" + entries.size() + ")";
        } else if (value.getClass().isArray()) {
            summary = "array(length=" + Array.getLength(value) + ")";
        } else if (value instanceof Integer
                || value instanceof Long
                || value instanceof Boolean
                || value instanceof Short
                || value instanceof Byte) {
            summary = value.toString();
        } else if (value instanceof Enum<?> choice) {
            summary = choice.name();
        } else {
            summary = value.getClass().getSimpleName();
        }
        LOGGER.trace("describe returning {}", summary);
        return summary;
    }
}
