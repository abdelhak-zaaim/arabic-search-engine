package io.zaaim.arindexer.service.impl;

import io.zaaim.arindexer.model.Index;
import io.zaaim.arindexer.service.Search;
import io.zaaim.arindexer.util.Constants;
import io.zaaim.arindexer.util.TextProcessor;
import io.zaaim.arindexer.util.TfIdfCalculator;

import java.nio.file.Path;
import java.util.*;

public class SearchImpl implements Search {

    @Override
    public Map<String, Float> search(String query, String indexRelativePath, int limit) {
        try {

            Path indexDir = Constants.INDEXES_DIR;
            String[] files = indexDir.toFile().list();
            if (files == null || files.length == 0) {
                throw new RuntimeException("No index files found in directory: " + indexDir.toString());
            }

            String indexPath = Arrays.stream(files).filter(str -> str.startsWith(indexRelativePath) && (str.endsWith(".ser") || str.endsWith(".xml")))
                    .sorted((str1, str2) -> {
                        Long l1 = indexDir.resolve(str1).toFile().lastModified();
                        Long l2 = indexDir.resolve(str2).toFile().lastModified();
                        return l2.compareTo(l1);
                    }).findFirst().orElseThrow(() -> new RuntimeException("Index file not found: " + indexRelativePath));

            Index index;
            if (indexPath.endsWith(".ser")) {
                index = Index.loadIndexFromFile(indexDir.resolve(indexPath));
            } else {
                index = Index.loadIndexFromXml(indexDir.resolve(indexPath));
            }


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
