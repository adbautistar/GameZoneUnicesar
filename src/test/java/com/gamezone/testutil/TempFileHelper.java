package com.gamezone.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Provides helper methods for isolating file-based persistence tests from
 * the real {@code data/} files, by writing to and cleaning up temporary
 * files instead.
 */
public final class TempFileHelper {

    private TempFileHelper() {
    }

    /**
     * Creates a temporary CSV file with the given lines already written to it.
     *
     * @param prefix the prefix to use for the temporary file's name
     * @param lines  the lines to write to the file, one per line
     * @return the path to the created temporary file
     * @throws IOException if the file cannot be created or written to
     */
    public static Path createTempCsv(String prefix, List<String> lines) throws IOException {
        Path path = Files.createTempFile(prefix, ".csv");
        Files.write(path, lines);
        return path;
    }

    /**
     * Deletes the file at the given path if it exists, silently ignoring
     * any failure to delete it.
     *
     * @param path the path to delete
     */
    public static void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Best-effort cleanup: a leftover temp file does not fail the test.
        }
    }
}
