package io.zaaim.arindexer.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TfIdfCalculator {

    public static Map<String, Map<String, Float>> calculateTfIdf(Map<String, Map<String, Integer>> invertedIndex) {
        int totalDocuments = invertedIndex.size();

        // Calculate document frequency for each term
        Map<String, Integer> documentFrequency = invertedIndex.values().parallelStream()
                .flatMap(docTerms -> docTerms.keySet().stream())
                .collect(Collectors.toConcurrentMap(
                        term -> term,
                        term -> 1,
                        Integer::sum
                ));

        // Calculate TF-IDF for each document
        return invertedIndex.entrySet().parallelStream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateDocumentTfIdf(entry.getValue(), totalDocuments, documentFrequency)
                ));
    }

    public static Map<String, Float> calculateQueryTfIdf(Map<String, Integer> queryTermFreq,
                                                          Map<String, Integer> documentFrequency,
                                                          int totalDocuments) {
        int totalTermsInQuery = queryTermFreq.values().stream().mapToInt(Integer::intValue).sum();

        return queryTermFreq.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String term = entry.getKey();
                            int termCount = entry.getValue();
                            float tf = (float) termCount / totalTermsInQuery;
                            int df = documentFrequency.getOrDefault(term, 0);
                            float idf = df > 0 ? (float) Math.log((double) totalDocuments / df) : 0;
                            return tf * idf;
                        }
                ));
    }

    private static Map<String, Float> calculateDocumentTfIdf(Map<String, Integer> termFrequencies,
                                                              int totalDocuments,
                                                              Map<String, Integer> documentFrequency) {
        int totalTermsInDoc = termFrequencies.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        return termFrequencies.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        termEntry -> {
                            String term = termEntry.getKey();
                            int termCount = termEntry.getValue();
                            float tf = (float) termCount / totalTermsInDoc;
                            float idf = (float) Math.log((double) totalDocuments / documentFrequency.get(term));
                            return tf * idf;
                        }
                ));
    }

    public static float calculateCosineSimilarity(Map<String, Float> vectorA, Map<String, Float> vectorB) {
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (Map.Entry<String, Float> entry : vectorA.entrySet()) {
            String term = entry.getKey();
            float valueA = entry.getValue();
            float valueB = vectorB.getOrDefault(term, 0.0f);

            dotProduct += valueA * valueB;
            normA += valueA * valueA;
        }

        for (float value : vectorB.values()) {
            normB += value * value;
        }

        normA = (float) Math.sqrt(normA);
        normB = (float) Math.sqrt(normB);

        if (normA == 0.0f || normB == 0.0f) {
            return 0.0f;
        }

        return dotProduct / (normA * normB);
    }
}