package io.zaaim.arindexer.util;

import java.util.*;

/**
 * Manages term-to-document mappings and inverted index
 */
public class IndexMaps {
    // Map: term -> set of document IDs containing this term
    private final Map<String, Set<String>> invertedIndex;

    // Map: term -> document ID -> term frequency
    private final Map<String, Map<String, Integer>> termDocumentFrequency;

    // Map: term -> IDF value
    private final Map<String, Double> idfValues;

    // Map: document ID -> list of terms
    private final Map<String, Set<String>> documentTerms;

    private int totalDocuments;

    public IndexMaps() {
        this.invertedIndex = new HashMap<>();
        this.termDocumentFrequency = new HashMap<>();
        this.idfValues = new HashMap<>();
        this.documentTerms = new HashMap<>();
        this.totalDocuments = 0;
    }

    public void addDocument(String docId, Set<String> terms) {
        totalDocuments++;
        documentTerms.put(docId, new HashSet<>(terms));

        for (String term : terms) {
            // Update inverted index
            invertedIndex.computeIfAbsent(term, k -> new HashSet<>()).add(docId);

            // Update term-document frequency
            termDocumentFrequency.computeIfAbsent(term, k -> new HashMap<>())
                    .merge(docId, 1, Integer::sum);
        }

        // Recalculate IDF values
        recalculateIDF();
    }

    public void removeDocument(String docId) {
        if (!documentTerms.containsKey(docId)) {
            return;
        }

        Set<String> terms = documentTerms.get(docId);
        for (String term : terms) {
            // Remove from inverted index
            Set<String> docs = invertedIndex.get(term);
            if (docs != null) {
                docs.remove(docId);
                if (docs.isEmpty()) {
                    invertedIndex.remove(term);
                    termDocumentFrequency.remove(term);
                    idfValues.remove(term);
                }
            }

            // Remove from term-document frequency
            Map<String, Integer> docFreq = termDocumentFrequency.get(term);
            if (docFreq != null) {
                docFreq.remove(docId);
            }
        }

        documentTerms.remove(docId);
        totalDocuments--;
        recalculateIDF();
    }

    private void recalculateIDF() {
        idfValues.clear();
        for (String term : invertedIndex.keySet()) {
            int docsWithTerm = invertedIndex.get(term).size();
            double idf = Math.log10((double) totalDocuments / docsWithTerm);
            idfValues.put(term, idf);
        }
    }

    public int getTermFrequency(String term, String docId) {
        Map<String, Integer> termFreqs = termDocumentFrequency.get(term);
        if (termFreqs == null) {
            return 0;
        }
        return termFreqs.getOrDefault(docId, 0);
    }

    public double getIDF(String term) {
        return idfValues.getOrDefault(term, 0.0);
    }

    public Set<String> getDocumentsWithTerm(String term) {
        Set<String> docs = invertedIndex.get(term);
        return docs != null ? Collections.unmodifiableSet(docs) : Collections.emptySet();
    }

    public Set<String> getTermsInDocument(String docId) {
        Set<String> terms = documentTerms.get(docId);
        return terms != null ? Collections.unmodifiableSet(terms) : Collections.emptySet();
    }

    public Set<String> getAllTerms() {
        return Collections.unmodifiableSet(invertedIndex.keySet());
    }

    public int getTotalDocuments() {
        return totalDocuments;
    }

    public int getDocumentFrequency(String term) {
        Set<String> docs = invertedIndex.get(term);
        return docs != null ? docs.size() : 0;
    }

    public void clear() {
        invertedIndex.clear();
        termDocumentFrequency.clear();
        idfValues.clear();
        documentTerms.clear();
        totalDocuments = 0;
    }

    @Override
    public String toString() {
        return "IndexMaps{" +
                "totalDocuments=" + totalDocuments +
                ", uniqueTerms=" + invertedIndex.size() +
                '}';
    }
}

