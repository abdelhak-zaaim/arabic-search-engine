package io.zaaim.arindexer.service;

import io.zaaim.arindexer.storage.StorageService;

import java.util.Map;

public class DocumentIndexService {

    // Placeholder: plug in real indexing (Lucene, custom, etc.)
    public Map<String, Float> search(String query, String index, int limit) {
        // Implement lookup against index
        return Map.of(); // stub
    }

    public DocumentIndexService(StorageService storageService) {
        this.storage = storageService;
    }

    private final StorageService storage;

    public String fetchContent(String docId) {
        try {
            return storage.readDocument(docId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read " + docId, e);
        }
    }
}