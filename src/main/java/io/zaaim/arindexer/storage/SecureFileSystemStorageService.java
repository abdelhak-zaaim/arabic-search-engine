package io.zaaim.arindexer.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecureFileSystemStorageService implements StorageService {

    private final Path baseDir;

    public SecureFileSystemStorageService(Path baseDir) {
        this.baseDir = baseDir.normalize().toAbsolutePath();
        init();
    }

    private void init() {
        try {
            if (Files.notExists(baseDir)) {
                Files.createDirectories(baseDir);
                // Optionally set POSIX permissions if supported
                try {
                    Files.setPosixFilePermissions(baseDir,
                            PosixFilePermissions.fromString("rwx------"));
                } catch (UnsupportedOperationException ignored) { }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }

    @Override
    public String readDocument(String docId) throws IOException {
        Path p = resolveSafe(docId);
        if (!Files.exists(p)) {
            throw new NoSuchFileException(docId);
        }
        return Files.readString(p);
    }

    @Override
    public void storeDocument(String docId, String content) throws IOException {
        Path p = resolveSafe(docId);
        Files.writeString(p, content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public List<String> listDocuments() throws IOException {
        try (Stream<Path> s = Files.list(baseDir)) {
            return s.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Path getBaseDir() {
        return baseDir;
    }

    private Path resolveSafe(String docId) {
        if (docId.contains("..") || docId.contains("/") || docId.contains("\\"))
            throw new IllegalArgumentException("Invalid document id");
        return baseDir.resolve(docId).normalize();
    }
}
