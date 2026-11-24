package com.demo.legalfirm.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

/**
 * Verifies saved PDF bytes, unique filenames, cleanup and filesystem failure behavior.
 *
 * @file MemorandumOutputServiceTest.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
class MemorandumOutputServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemorandumOutputServiceTest.class);
    @TempDir Path directory;

    @Test
    void savesUniqueFilesAndRemovesOnlyNamedOutput() throws IOException {
        LOGGER.trace("savesUniqueFilesAndRemovesOnlyNamedOutput entered");
        var service = new MemorandumOutputService(directory.toString());
        byte[] bytes = "%PDF-test".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String first = service.save(2L, bytes), second = service.save(2L, bytes);
        assertThat(first).isNotEqualTo(second);
        assertThat(Files.readAllBytes(directory.resolve(first))).isEqualTo(bytes);
        service.remove(first);
        assertThat(directory.resolve(first)).doesNotExist();
        assertThat(directory.resolve(second)).exists();
        try (var files = Files.list(directory)) {
            assertThat(files.map(path -> path.getFileName().toString()).toList())
                    .containsExactly(second);
        }
        LOGGER.trace("savesUniqueFilesAndRemovesOnlyNamedOutput completed");
    }

    @Test
    void refusesExternalCleanupAndInvalidBytes() {
        LOGGER.trace("refusesExternalCleanupAndInvalidBytes entered");
        var service = new MemorandumOutputService(directory.toString());
        assertThatThrownBy(() -> service.remove("../outside.pdf"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.save(2L, new byte[0]))
                .isInstanceOf(IllegalArgumentException.class);
        LOGGER.trace("refusesExternalCleanupAndInvalidBytes completed");
    }

    @Test
    void reportsUnwritableOutputLocation() throws IOException {
        LOGGER.trace("reportsUnwritableOutputLocation entered");
        Path file = directory.resolve("not-a-directory");
        Files.writeString(file, "test");
        var service = new MemorandumOutputService(file.toString());
        assertThatThrownBy(() -> service.save(2L, new byte[] {1})).isInstanceOf(IOException.class);
        LOGGER.trace("reportsUnwritableOutputLocation completed");
    }
}
