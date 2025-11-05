package io.zaaim.arindexer.util;

import java.util.*;

/**
 * Represents a TF-IDF vector with term frequencies and IDF scores
 */
public class TFIDFVector {
    private final Map<String, Double> termWeights;
    private final String documentId;
    private final double magnitude;

    public TFIDFVector(String documentId, Map<String, Double> termWeights) {
        this.documentId = documentId;
        this.termWeights = new HashMap<>(termWeights);
        this.magnitude = calculateMagnitude();
    }

    private double calculateMagnitude() {
        double sum = 0;
        for (double weight : termWeights.values()) {
            sum += weight * weight;
        }
        return Math.sqrt(sum);
    }

    public String getDocumentId() {
        return documentId;
    }

    public Map<String, Double> getTermWeights() {
        return Collections.unmodifiableMap(termWeights);
    }

    public double getMagnitude() {
        return magnitude;
    }

    /**
     * Calculate cosine similarity with another vector
     */
    public double cosineSimilarity(TFIDFVector other) {
        if (this.magnitude == 0 || other.magnitude == 0) {
            return 0;
        }

        double dotProduct = 0;
        for (String term : this.termWeights.keySet()) {
            if (other.termWeights.containsKey(term)) {
                dotProduct += this.termWeights.get(term) * other.termWeights.get(term);
            }
        }

        return dotProduct / (this.magnitude * other.magnitude);
    }

    @Override
    public String toString() {
        return "TFIDFVector{" +
                "documentId='" + documentId + '\'' +
                ", terms=" + termWeights.size() +
                ", magnitude=" + magnitude +
                '}';
    }
}

