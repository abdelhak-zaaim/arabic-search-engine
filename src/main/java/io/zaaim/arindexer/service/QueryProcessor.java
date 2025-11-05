package io.zaaim.arindexer.service;

import io.zaaim.arindexer.util.ArabicTokenizer;
import safar.basic.morphology.stemmer.impl.ISRIStemmer;

import java.util.*;

/**
 * Processes queries by tokenizing and stemming
 */
public class QueryProcessor {
    private final ISRIStemmer stemmer;

    public QueryProcessor(ISRIStemmer stemmer) {
        this.stemmer = stemmer;
    }

    /**
     * Process query: tokenize and stem
     */
    public Set<String> processQuery(String query) {
        if (query == null || query.isBlank()) {
            return new HashSet<>();
        }

        Set<String> tokens = ArabicTokenizer.tokenize(query);
        Set<String> stemmedTokens = new HashSet<>();

        for (String token : tokens) {
            try {
                Object result = stemmer.stem(token);
                String stemmed = result.toString();
                stemmedTokens.add(stemmed);
            } catch (Exception e) {
                stemmedTokens.add(token);
            }
        }

        return stemmedTokens;
    }

    /**
     * Process query preserving order
     */
    public List<String> processQueryToList(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        List<String> tokens = ArabicTokenizer.tokenizeToList(query);
        List<String> stemmedTokens = new ArrayList<>();

        for (String token : tokens) {
            try {
                Object result = stemmer.stem(token);
                String stemmed = result.toString();
                stemmedTokens.add(stemmed);
            } catch (Exception e) {
                stemmedTokens.add(token);
            }
        }

        return stemmedTokens;
    }
}
