package io.zaaim.arindexer.storage;

import java.nio.file.Path;

public interface IndexStorageService {
    Path baseDir();

    Path resolveIndex(String id, String extension);

    boolean exists(String id, IndexFormat format);
}