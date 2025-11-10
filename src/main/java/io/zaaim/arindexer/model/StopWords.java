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
    private static WeakReference<Set<String>> stopWordsRef;

    public Set<String> getStopWords() {
        if (stopWordsRef == null || stopWordsRef.get() == null) {
            Path stopWordsPath = Path.of("src/main/resources/stopwords.txt");
            try {
                List<String> stopWords = Files.readAllLines(stopWordsPath);
                stopWordsRef = new WeakReference<>(Collections.unmodifiableSet(new HashSet<>(stopWords)));
                return stopWordsRef.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return stopWordsRef.get();
    }

}
