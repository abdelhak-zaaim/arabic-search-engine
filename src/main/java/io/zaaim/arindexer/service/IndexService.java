package io.zaaim.arindexer.service;

import io.zaaim.arindexer.model.Document;
import io.zaaim.arindexer.util.ArabicTokenizer;
import io.zaaim.arindexer.util.IndexMaps;
import io.zaaim.arindexer.util.TFIDFVector;
import safar.basic.morphology.stemmer.impl.ISRIStemmer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for indexing documents with TF-IDF vectorization
 * Supports multiple indexes with different names
 */
public class IndexService {
    private final ISRIStemmer stemmer;

    // Map: indexName -> IndexMaps
    private final Map<String, IndexMaps> indexes;

    // Map: indexName -> (docId -> Document)
    private final Map<String, Map<String, Document>> documents;

    // Map: indexName -> (docId -> TFIDFVector)
    private final Map<String, Map<String, TFIDFVector>> tfidfVectors;

    public IndexService(ISRIStemmer stemmer) {
        this.stemmer = stemmer;
        this.indexes = new ConcurrentHashMap<>();
        this.documents = new ConcurrentHashMap<>();
        this.tfidfVectors = new ConcurrentHashMap<>();
    }

    /**
     * Create or get an index by name
     */
    private void ensureIndexExists(String indexName) {
        indexes.computeIfAbsent(indexName, _ -> new IndexMaps());
        documents.computeIfAbsent(indexName, _ -> new ConcurrentHashMap<>());
        tfidfVectors.computeIfAbsent(indexName, _ -> new ConcurrentHashMap<>());
    }

    /**
     * Index a document
     */
    public void indexDocument(Document doc) {
        if (doc == null || doc.getId() == null) {
            throw new IllegalArgumentException("Document and ID cannot be null");
        }

        String indexName = doc.getIndexName() != null ? doc.getIndexName() : "default";
        ensureIndexExists(indexName);

        // Combine title and content for indexing
        String fullText = (doc.getTitle() != null ? doc.getTitle() + " " : "") +
                         (doc.getContent() != null ? doc.getContent() : "");

        // Remove old version if exists
        if (documents.get(indexName).containsKey(doc.getId())) {
            removeDocument(doc.getId(), indexName);
        }

        // Tokenize and stem
        Set<String> tokens = tokenizeAndStem(fullText);

        // Store document
        documents.get(indexName).put(doc.getId(), doc);

        // Add to index maps
        indexes.get(indexName).addDocument(doc.getId(), tokens);

        // Build TF-IDF vector
        TFIDFVector vector = buildTFIDFVector(doc.getId(), tokens, indexName);
        tfidfVectors.get(indexName).put(doc.getId(), vector);
    }

    /**
     * Remove a document from index
     */
    public void removeDocument(String docId, String indexName) {
        if (!indexes.containsKey(indexName)) {
            return;
        }

        indexes.get(indexName).removeDocument(docId);
        documents.get(indexName).remove(docId);
        tfidfVectors.get(indexName).remove(docId);
    }

    /**
     * Build TF-IDF vector for a document
     */
    private TFIDFVector buildTFIDFVector(String docId, Set<String> terms, String indexName) {
        IndexMaps indexMaps = indexes.get(indexName);
        Map<String, Double> termWeights = new HashMap<>();

        for (String term : terms) {
            int tf = indexMaps.getTermFrequency(term, docId);
            double idf = indexMaps.getIDF(term);
            double tfidf = tf * idf;
            termWeights.put(term, tfidf);
        }

        return new TFIDFVector(docId, termWeights);
    }

    /**
     * Tokenize and stem text
     */
    private Set<String> tokenizeAndStem(String text) {
        Set<String> tokens = ArabicTokenizer.tokenize(text);
        Set<String> stemmedTokens = new HashSet<>();

        for (String token : tokens) {
            try {
                Object result = stemmer.stem(token);
                String stemmed = result.toString();
                stemmedTokens.add(stemmed);
            } catch (Exception e) {
                // If stemming fails, use original token
                stemmedTokens.add(token);
            }
        }

        return stemmedTokens;
    }

    /**
     * Get all indexes
     */
    public List<String> getAllIndexNames() {
        return new ArrayList<>(indexes.keySet());
    }

    /**
     * Get index info
     */
    public Map<String, Object> getIndexInfo(String indexName) {
        Map<String, Object> info = new HashMap<>();

        if (indexes.containsKey(indexName)) {
            IndexMaps indexMaps = indexes.get(indexName);
            info.put("indexName", indexName);
            info.put("totalDocuments", indexMaps.getTotalDocuments());
            info.put("uniqueTerms", indexMaps.getAllTerms().size());
        }

        return info;
    }

    /**
     * Delete entire index
     */
    public void deleteIndex(String indexName) {
        indexes.remove(indexName);
        documents.remove(indexName);
        tfidfVectors.remove(indexName);
    }

    /**
     * Get document count for an index
     */
    public int getDocumentCount(String indexName) {
        IndexMaps indexMaps = indexes.get(indexName);
        return indexMaps != null ? indexMaps.getTotalDocuments() : 0;
    }

    /**
     * Get all documents in index
     */
    public List<Document> getAllDocuments(String indexName) {
        Map<String, Document> docs = documents.get(indexName);
        return docs != null ? new ArrayList<>(docs.values()) : new ArrayList<>();
    }

    /**
     * Get specific document
     */
    public Document getDocument(String docId, String indexName) {
        Map<String, Document> docs = documents.get(indexName);
        return docs != null ? docs.get(docId) : null;
    }

    /**
     * Get index maps for advanced operations
     */
    protected IndexMaps getIndexMaps(String indexName) {
        return indexes.get(indexName);
    }

    /**
     * Get TFIDF vectors for index
     */
    protected Map<String, TFIDFVector> getTFIDFVectors(String indexName) {
        return tfidfVectors.get(indexName);
    }

    /**
     * Rebuild index (recalculate TFIDF for all documents)
     */
    public void rebuildIndex(String indexName) {
        if (!indexes.containsKey(indexName)) {
            return;
        }

        Map<String, Document> docs = documents.get(indexName);
        Map<String, TFIDFVector> vectors = tfidfVectors.get(indexName);
        IndexMaps indexMaps = indexes.get(indexName);

        vectors.clear();
        for (String docId : docs.keySet()) {
            Set<String> terms = indexMaps.getTermsInDocument(docId);
            TFIDFVector vector = buildTFIDFVector(docId, terms, indexName);
            vectors.put(docId, vector);
        }
    }
}
