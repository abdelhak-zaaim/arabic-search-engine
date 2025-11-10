package io.zaaim.arindexer.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface StorageService {
    String readDocument(String docId) throws IOException;
    void storeDocument(String docId, String content) throws IOException;
    List<String> listDocuments() throws IOException;
    Path getBaseDir();
}