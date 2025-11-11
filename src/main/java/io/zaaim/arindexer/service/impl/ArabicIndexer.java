package io.zaaim.arindexer.service.impl;

import io.zaaim.arindexer.model.Index;
import io.zaaim.arindexer.service.Indexer;
import io.zaaim.arindexer.util.TextProcessor;
import io.zaaim.arindexer.util.TfIdfCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class ArabicIndexer implements Indexer {

    @Override
    public Index createIndex(Path indexPath) throws IOException {
        if (!Files.exists(indexPath) || !Files.isDirectory(indexPath)) {
            throw new IOException("Index path does not exist or is not a directory: " + indexPath);
        }

        Map<String, Map<String, Integer>> invertedIndex = Files.walk(indexPath)
                .filter(Files::isRegularFile)
                .parallel()
                .filter(path -> path.toString().endsWith(".txt"))
                .map(path -> {
                    try {
                        String content = Files.readString(path);
                        String fileName = indexPath.relativize(path).toString();
                        Map<String, Integer> termFrequency = TextProcessor.processDocument(content);
                        return Map.entry(fileName, termFrequency);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to process file: " + path, e);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Index(TfIdfCalculator.calculateTfIdf(invertedIndex));
    }
}
