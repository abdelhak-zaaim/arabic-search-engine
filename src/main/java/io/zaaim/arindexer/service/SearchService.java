package io.zaaim.arindexer.service;

import io.zaaim.arindexer.model.Document;
import io.zaaim.arindexer.model.SearchResult;
import io.zaaim.arindexer.util.IndexMaps;
import io.zaaim.arindexer.util.TFIDFVector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for searching documents using TF-IDF similarity
 */
public class SearchService {
    private final IndexService indexService;

    public SearchService(IndexService indexService) {
        this.indexService = indexService;
    }

    /**
     * Search in a specific index
     */
    public List<SearchResult> search(String query, String indexName, int limit) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        IndexMaps indexMaps = indexService.getIndexMaps(indexName);
        Map<String, TFIDFVector> vectors = indexService.getTFIDFVectors(indexName);

        if (indexMaps == null || vectors == null || vectors.isEmpty()) {
            return new ArrayList<>();
        }

        // Build query vector
        TFIDFVector queryVector = buildQueryVector(query, indexName);

        if (queryVector.getMagnitude() == 0) {
            return new ArrayList<>();
        }

        // Calculate similarity scores
        Map<String, Double> scores = new HashMap<>();
        for (String docId : vectors.keySet()) {
            TFIDFVector docVector = vectors.get(docId);
            double similarity = queryVector.cosineSimilarity(docVector);
            if (similarity > 0) {
                scores.put(docId, similarity);
            }
        }

        // Sort by score descending and build results
        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(entry -> {
                    String docId = entry.getKey();
                    double score = entry.getValue();
                    Document doc = indexService.getDocument(docId, indexName);

                    return new SearchResult(
                            docId,
                            doc.getTitle() != null ? doc.getTitle() : "",
                            truncateContent(doc.getContent(), 200),
                            score,
                            indexName
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Search with filter
     */
    public List<SearchResult> searchWithFilter(String query, String indexName,
                                                SearchFilter filter, int limit) {
        List<SearchResult> results = search(query, indexName, limit * 2);

        if (filter == null) {
            return results.stream().limit(limit).collect(Collectors.toList());
        }

        return results.stream()
                .filter(filter::matches)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Build TF-IDF vector for query
     */
    private TFIDFVector buildQueryVector(String query, String indexName) {
        IndexMaps indexMaps = indexService.getIndexMaps(indexName);

        // Tokenize query using the same stemming as during indexing
        Set<String> queryTokens = indexMaps != null ?
                indexMaps.getAllTerms().stream()
                        .filter(term -> query.toLowerCase().contains(term))
                        .collect(Collectors.toSet()) : new HashSet<>();

        Map<String, Double> termWeights = new HashMap<>();

        if (!queryTokens.isEmpty()) {
            for (String term : queryTokens) {
                int tf = 1;
                double idf = indexMaps.getIDF(term);
                double tfidf = tf * idf;
                termWeights.put(term, tfidf);
            }
        }

        return new TFIDFVector("query", termWeights);
    }

    /**
     * Get related documents
     */
    public List<SearchResult> getRelatedDocuments(String docId, String indexName, int limit) {
        Map<String, TFIDFVector> vectors = indexService.getTFIDFVectors(indexName);

        if (vectors == null || !vectors.containsKey(docId)) {
            return new ArrayList<>();
        }

        TFIDFVector queryVector = vectors.get(docId);

        Map<String, Double> scores = new HashMap<>();
        for (String otherDocId : vectors.keySet()) {
            if (!otherDocId.equals(docId)) {
                TFIDFVector otherVector = vectors.get(otherDocId);
                double similarity = queryVector.cosineSimilarity(otherVector);
                if (similarity > 0) {
                    scores.put(otherDocId, similarity);
                }
            }
        }

        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(entry -> {
                    String relatedDocId = entry.getKey();
                    double score = entry.getValue();
                    Document doc = indexService.getDocument(relatedDocId, indexName);

                    return new SearchResult(
                            relatedDocId,
                            doc.getTitle() != null ? doc.getTitle() : "",
                            truncateContent(doc.getContent(), 200),
                            score,
                            indexName
                    );
                })
                .collect(Collectors.toList());
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        return content.length() > maxLength ?
                content.substring(0, maxLength) + "..." :
                content;
    }

    /**
     * Filter interface for custom search filtering
     */
    public interface SearchFilter {
        boolean matches(SearchResult result);
    }
