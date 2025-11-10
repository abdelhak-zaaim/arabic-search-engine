package io.zaaim.arindexer.storage;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;

public class FileIndexStorageService implements IndexStorageService {

    private final Path baseDir;

    public FileIndexStorageService(Path baseDir) {
        this.baseDir = Objects.requireNonNull(baseDir).toAbsolutePath().normalize();
        init();
    }

    private void init() {
        try {
            if (Files.notExists(baseDir)) {
                Files.createDirectories(baseDir);
                try {
                    Files.setPosixFilePermissions(baseDir, PosixFilePermissions.fromString("rwx------"));
                } catch (UnsupportedOperationException ignored) { }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize index storage at " + baseDir, e);
        }
    }

    @Override
    public Path baseDir() {
        return baseDir;
    }

    @Override
    public Path resolveIndex(String id, String extension) {
        String safeId = validateId(id);
        return baseDir.resolve(safeId + extension).normalize();
    }

    @Override
    public boolean exists(String id, IndexFormat format) {
        return Files.exists(resolveIndex(id, format.extension()));
    }

    private String validateId(String id) {
        if (id == null || !id.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("Invalid index id");
        }
        return id;
    }
}