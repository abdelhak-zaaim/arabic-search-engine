package io.zaaim.arindexer.index.service.impl;

import io.zaaim.arindexer.model.Index;
import io.zaaim.arindexer.model.StopWords;
import io.zaaim.arindexer.index.service.Indexer;
import io.zaaim.arindexer.stemmer.ArabicStemmerKhoja;
import io.zaaim.arindexer.stemmer.Stemmer;
import io.zaaim.arindexer.util.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ArabicIndexer implements Indexer {

    @Override
    public Index createIndex(Path indexPath) throws IOException {
        if (!Files.exists(indexPath) || !Files.isDirectory(indexPath)) {
            throw new IOException("Index path does not exist or is not a directory: " + indexPath);
        }

        Stemmer stemmer = new ArabicStemmerKhoja();

        // get all text files from the directory
        Map<String, Map<String, Integer>> invertedIndex = Files.walk(indexPath)
                .filter(Files::isRegularFile)
                .parallel()
                .filter(path -> path.toString().endsWith(".txt"))
                .map(path -> {
                    try {
                        Map<String, Integer> stemmerCache = new HashMap<>();
                        String content = Files.readString(path);
                        String fileName = indexPath.relativize(path).toString();
                        String[] tokens = Tokenizer.tokenize(content);
                        tokens = removeStops(tokens);

                        for (String token : tokens) {
                            String stemmed = stemmer.stem(token);
                            stemmerCache.put(stemmed, stemmerCache.getOrDefault(stemmed, 0) + 1);
                        }
                        return Map.entry(fileName, stemmerCache);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to process file: " + path, e);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return calculateTfIdf(invertedIndex);
    }

    private Index calculateTfIdf(Map<String, Map<String, Integer>> invertedIndex) {
        int totalDocuments = invertedIndex.size();
        Index tfIdfIndex = new Index();

        // Calculate document frequency for each term
        Map<String, Integer> documentFrequency = invertedIndex.values().parallelStream()
                .flatMap(docTerms -> docTerms.keySet().stream())
                .collect(Collectors.toConcurrentMap(
                        term -> term,
                        term -> 1,
                        Integer::sum
                ));

        // Calculate TF-IDF for each document
        Map<String, Map<String, Float>> tfIdfMap = invertedIndex.entrySet().parallelStream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Map<String, Integer> termFrequencies = entry.getValue();
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
                ));

        // Populate the Index object
        tfIdfMap.forEach(tfIdfIndex::addEntry);

        return tfIdfIndex;
    }

    private String[] removeStops(String[] tokens) throws IOException {
        Set<String> stopWords = new StopWords().getStopWords();

        return Arrays.stream(tokens).filter(token -> !stopWords.contains(token)).toArray(String[]::new);
    }

}
