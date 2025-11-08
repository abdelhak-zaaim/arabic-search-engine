package io.zaaim.arindexer.index.service.impl;

import io.zaaim.arindexer.commun.model.Index;
import io.zaaim.arindexer.commun.model.StopWords;
import io.zaaim.arindexer.index.service.Indexer;
import io.zaaim.arindexer.steemer.ArabicStemmerKhoja;
import io.zaaim.arindexer.util.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ArabicIndexer implements Indexer {

    @Override
    public Index createIndex(Path indexPath) throws IOException {
        if (!Files.exists(indexPath) || !Files.isDirectory(indexPath)) {
            throw new IOException("Index path does not exist or is not a directory: " + indexPath);
        }

        Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();

        ArabicStemmerKhoja stemmer = new ArabicStemmerKhoja();

        // get all text files from the directory
        Files.walk(indexPath)
                .filter(Files::isRegularFile)
                .parallel()
                .filter(path -> path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        Map<String, Integer> stemmerCache = new HashMap<>();
                        String content = Files.readString(path);
                        String fileName = indexPath.relativize(path).toString();
                        String[] tokens = Tokenizer.tokenize(content);

                        // remove stop words
                        tokens = removeStops(tokens);

                        for (String token : tokens) {
                            String stemmed = stemmer.stem(token);
                            // if stemmed already in cache, increment count, else add to cache
                            stemmerCache.put(stemmed, stemmerCache.getOrDefault(stemmed, 0) + 1);
                        }
                        invertedIndex.put(fileName, stemmerCache);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        return calculateTfIdf(invertedIndex);
    }

    private Index calculateTfIdf(Map<String, Map<String, Integer>> invertedIndex) {
        int totalDocuments = invertedIndex.size();
        Index tfIdfIndex = new Index();
        // Calculate document frequency for each term
        Map<String, Integer> documentFrequency = new HashMap<>();
        for (Map<String, Integer> docTerms : invertedIndex.values()) {
            for (String term : docTerms.keySet()) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }


        // Calculate TF-IDF for each term in each document
        for (Map.Entry<String, Map<String, Integer>> entry : invertedIndex.entrySet()) {
            String document = entry.getKey();
            Map<String, Integer> termFrequencies = entry.getValue();

            int totalTermsInDoc = termFrequencies.values().stream().mapToInt(Integer::intValue).sum();
            Map<String, Float> tfIdfScores = new HashMap<>();

            for (Map.Entry<String, Integer> termEntry : termFrequencies.entrySet()) {
                String term = termEntry.getKey();
                int termCount = termEntry.getValue();

                float tf = (float) termCount / totalTermsInDoc;
                float idf = (float) Math.log((double) totalDocuments / documentFrequency.get(term));
                float tfIdf = tf * idf;

                tfIdfScores.put(term, tfIdf);
            }

            tfIdfIndex.addEntry(document, tfIdfScores);
        }

        return tfIdfIndex;
    }

    private String[] removeStops(String[] tokens) throws IOException {
        Set<String> stopWords = new StopWords().getStopWords();

        return Arrays.stream(tokens).filter(token -> !stopWords.contains(token)).toArray(String[]::new);
    }

}
