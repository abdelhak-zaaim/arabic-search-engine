package io.zaaim.arindexer.commun.model;

import java.util.HashMap;
import java.util.Map;

public class Index {

    private Map<String, Map<String, Float>> index = new HashMap<>();
    private int documentCount = 0;

    public Map<String, Map<String, Float>> getIndex() {
        return index;
    }

    public Map<String, Map<String, Integer>> getInvertedIndex() {
        Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();

        for (Map.Entry<String, Map<String, Float>> docEntry : index.entrySet()) {
            String docId = docEntry.getKey();
            Map<String, Float> termWeights = docEntry.getValue();

            for (Map.Entry<String, Float> termEntry : termWeights.entrySet()) {
                String term = termEntry.getKey();

                invertedIndex
                    .computeIfAbsent(term, k -> new HashMap<>())
                    .put(docId, 1); // Using 1 to indicate presence
            }
        }

        return invertedIndex;
    }
}
