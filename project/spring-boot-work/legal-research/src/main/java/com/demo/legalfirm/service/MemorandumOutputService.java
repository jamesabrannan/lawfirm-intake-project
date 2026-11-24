package com.demo.legalfirm.service;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Saves generated PDFs to a configured filesystem directory before database completion.
 *
 * @file MemorandumOutputService.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Service
public class MemorandumOutputService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumOutputService.class);
    private final Path directory;

    public MemorandumOutputService(
            @Value("${memorandum.output-directory:output/memorandums}") String directory) {
        LOGGER.trace(
                "MemorandumOutputService(directory={}) entered", LogSummary.describe(directory));
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        LOGGER.trace("MemorandumOutputService completed");
    }

    public String save(long memorandumId, byte[] pdf) throws IOException {
        LOGGER.debug(
                "save(memorandumId={}, pdf={}) entered", memorandumId, LogSummary.describe(pdf));
        Path temporary = null;
        try {
            if (memorandumId < 1 || pdf == null || pdf.length == 0)
                throw new IllegalArgumentException("Memorandum ID and PDF bytes are required");
            Files.createDirectories(directory);
            String filename = "memorandum-" + memorandumId + "-" + UUID.randomUUID() + ".pdf";
            temporary = Files.createTempFile(directory, ".memorandum-", ".tmp");
            Files.write(temporary, pdf);
            Path target = directory.resolve(filename);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unavailable) {
                Files.move(temporary, target);
            }
            LOGGER.info(
                    "Memorandum PDF saved; memorandumId={}, filename={}", memorandumId, filename);
            LOGGER.debug("save returning filename={}", filename);
            return filename;
        } catch (IOException | RuntimeException failure) {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException cleanup) {
                    failure.addSuppressed(cleanup);
                }
            }
            LOGGER.warn("Memorandum PDF save failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }

    /** Compensates for a failed database update; never deletes a caller-supplied external path. */
    public void remove(String filename) throws IOException {
        LOGGER.debug("remove(filename={}) entered", LogSummary.describe(filename));
        try {
            if (filename == null || !filename.matches("memorandum-[0-9]+-[a-f0-9-]+\\.pdf")) {
                throw new IllegalArgumentException(
                        "Only generated memorandum filenames may be removed");
            }
            Path target = directory.resolve(filename).normalize();
            if (!target.startsWith(directory) || !target.getParent().equals(directory))
                throw new IllegalArgumentException("Invalid output location");
            Files.deleteIfExists(target);
            LOGGER.info("Removed unrecorded memorandum PDF; filename={}", filename);
            LOGGER.debug("remove completed");
        } catch (IOException | RuntimeException failure) {
            LOGGER.warn(
                    "Memorandum PDF cleanup failed; type={}", failure.getClass().getSimpleName());
            throw failure;
        }
    }
}
