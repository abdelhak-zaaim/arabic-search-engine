package io.zaaim.arindexer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zaaim.arindexer.model.Document;
import io.zaaim.arindexer.model.SearchResponse;
import io.zaaim.arindexer.model.SearchResult;
import io.zaaim.arindexer.service.IndexService;
import io.zaaim.arindexer.service.SearchService;

import java.util.*;

/**
 * API helper for search engine operations
 * Used by Main to handle HTTP requests
 */
public class SearchController {
    private final IndexService indexService;
    private final SearchService searchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchController(IndexService indexService, SearchService searchService) {
        this.indexService = indexService;
        this.searchService = searchService;
    }

    /**
     * Add/Index a document
     */
    public String addDocument(String body) {
        try {
            Document doc = objectMapper.readValue(body, Document.class);
            indexService.indexDocument(doc);

            return objectMapper.writeValueAsString(
                    Map.of("status", "success", "message", "Document indexed", "docId", doc.getId())
            );
        } catch (Exception e) {
            return createError("Error indexing document: " + e.getMessage());
        }
    }

    /**
     * Search documents
     */
    public String search(String query, String indexName, int limit) {
        try {
            long startTime = System.currentTimeMillis();
            List<SearchResult> results = searchService.search(query, indexName, limit);
            long executionTime = System.currentTimeMillis() - startTime;

            SearchResponse response = new SearchResponse(
                    query,
                    results,
                    results.size(),
                    executionTime
            );

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return createError("Error searching: " + e.getMessage());
        }
    }

    /**
     * Get related documents
     */
    public String getRelated(String docId, String indexName, int limit) {
        try {
            List<SearchResult> results = searchService.getRelatedDocuments(docId, indexName, limit);

            return objectMapper.writeValueAsString(
                    Map.of("results", results, "count", results.size())
            );
        } catch (Exception e) {
            return createError("Error getting related documents: " + e.getMessage());
        }
    }

    /**
     * List all indexes
     */
    public String listIndexes() {
        try {
            List<String> indexes = indexService.getAllIndexNames();
            Map<String, Object> indexInfo = new HashMap<>();

            for (String indexName : indexes) {
                indexInfo.put(indexName, indexService.getIndexInfo(indexName));
            }

            return objectMapper.writeValueAsString(Map.of("indexes", indexInfo));
        } catch (Exception e) {
            return createError("Error listing indexes: " + e.getMessage());
        }
    }

    /**
     * Get index info
     */
    public String getIndexInfo(String indexName) {
        try {
            Map<String, Object> info = indexService.getIndexInfo(indexName);

            if (info.isEmpty()) {
                return objectMapper.writeValueAsString(
                        Map.of("status", "error", "message", "Index not found")
                );
            }

            return objectMapper.writeValueAsString(info);
        } catch (Exception e) {
            return createError("Error getting index info: " + e.getMessage());
        }
    }

    /**
     * Get all documents in index
     */
    public String getDocuments(String indexName) {
        try {
            List<Document> docs = indexService.getAllDocuments(indexName);

            return objectMapper.writeValueAsString(
                    Map.of("indexName", indexName, "documents", docs, "count", docs.size())
            );
        } catch (Exception e) {
            return createError("Error getting documents: " + e.getMessage());
        }
    }

    /**
     * Get specific document
     */
    public String getDocument(String indexName, String docId) {
        try {
            Document doc = indexService.getDocument(docId, indexName);

            if (doc == null) {
                return objectMapper.writeValueAsString(
                        Map.of("status", "error", "message", "Document not found")
                );
            }

            return objectMapper.writeValueAsString(doc);
        } catch (Exception e) {
            return createError("Error getting document: " + e.getMessage());
        }
    }

    /**
     * Delete document
     */
    public String deleteDocument(String indexName, String docId) {
        try {
            indexService.removeDocument(docId, indexName);

            return objectMapper.writeValueAsString(
                    Map.of("status", "success", "message", "Document deleted")
            );
        } catch (Exception e) {
            return createError("Error deleting document: " + e.getMessage());
        }
    }

    /**
     * Delete entire index
     */
    public String deleteIndex(String indexName) {
        try {
            indexService.deleteIndex(indexName);

            return objectMapper.writeValueAsString(
                    Map.of("status", "success", "message", "Index deleted")
            );
        } catch (Exception e) {
            return createError("Error deleting index: " + e.getMessage());
        }
    }

    /**
     * Rebuild index
     */
    public String rebuildIndex(String indexName) {
        try {
            indexService.rebuildIndex(indexName);

            return objectMapper.writeValueAsString(
                    Map.of("status", "success", "message", "Index rebuilt")
            );
        } catch (Exception e) {
            return createError("Error rebuilding index: " + e.getMessage());
        }
    }

    private String createError(String message) {
        try {
            return objectMapper.writeValueAsString(
                    Map.of("status", "error", "message", message)
            );
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"" + message + "\"}";
        }
    }
}

