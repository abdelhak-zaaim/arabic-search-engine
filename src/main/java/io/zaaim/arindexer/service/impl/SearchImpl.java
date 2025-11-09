package io.zaaim.arindexer.service.impl;

import io.zaaim.arindexer.model.Index;
import io.zaaim.arindexer.service.Search;
import io.zaaim.arindexer.util.Constants;
import io.zaaim.arindexer.util.TextProcessor;
import io.zaaim.arindexer.util.TfIdfCalculator;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchImpl implements Search {

    @Override
    public Map<String, Float> search(String query, String[] filters, int limit) {
        try {
            // Load the index
            Index index = Index.loadIndexFromXml(Constants.INDEXES_DIR.resolve("index-1762615442274.ser"));
            Map<String, Map<String, Float>> indexMap = index.getIndexMap();

            if (indexMap == null || indexMap.isEmpty()) {
                return Collections.emptyMap();
            }

            // Process the query to get term frequencies
            Map<String, Integer> queryTermFreq = TextProcessor.processDocument(query);

            // Calculate document frequency for IDF calculation
            Map<String, Integer> documentFrequency = new HashMap<>();
            for (Map<String, Float> docTerms : indexMap.values()) {
                for (String term : docTerms.keySet()) {
                    documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
                }
            }

            // Calculate TF-IDF for the query
            Map<String, Float> queryTfIdf = TfIdfCalculator.calculateQueryTfIdf(
                    queryTermFreq,
                    documentFrequency,
                    indexMap.size()
            );

            // Calculate cosine similarity with each document
            Map<String, Float> similarities = new HashMap<>();
            for (Map.Entry<String, Map<String, Float>> docEntry : indexMap.entrySet()) {
                String docName = docEntry.getKey();

                // Apply filters if provided
//                if (filters != null && filters.length > 0) {
//                    String[] docParts = docName.split("/");
//                    if (docParts.length < filters.length) continue;
//                    boolean matches = false;
//                    for (int i = 0; i < filters.length; i++) {
//                        if (!docParts[i].toLowerCase().trim().equals(filters[i].toLowerCase().trim())) {
//                            matches = false;
//                            break;
//                        }
//                        matches = true;
//                    }
//                    if (!matches) continue;
//                }

                Map<String, Float> docVector = docEntry.getValue();
                float similarity = TfIdfCalculator.calculateCosineSimilarity(queryTfIdf, docVector);

                if (similarity > 0) {
                    similarities.put(docName, similarity);
                }
            }

            // Return top K most similar documents
            return similarities.entrySet().stream()
                    .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                    .limit(limit)
                    .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);

        } catch (Exception e) {
            throw new RuntimeException("Failed to perform search", e);
        }
    }


}
