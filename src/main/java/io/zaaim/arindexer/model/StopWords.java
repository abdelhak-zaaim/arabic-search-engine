package io.zaaim.arindexer.model;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWords {
    private static volatile Set<String> stopWords;

    public Set<String> getStopWords() {
        if (stopWords == null) {
            synchronized (StopWords.class) {
                if (stopWords == null) {
                    stopWords = loadStopWords();
                }
            }
        }
        return stopWords;
    }

    private Set<String> loadStopWords() {
        try (var stream = getClass().getClassLoader()
                .getResourceAsStream("stopwords.txt")) {
            if (stream == null) {
                throw new IllegalStateException("stopwords.txt not found in classpath");
            }
            List<String> words = new String(stream.readAllBytes())
                    .lines()
                    .toList();
            return Collections.unmodifiableSet(new HashSet<>(words));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load stop words", e);
        }
    }
}
