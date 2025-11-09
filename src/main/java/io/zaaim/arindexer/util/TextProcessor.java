package io.zaaim.arindexer.util;

import io.zaaim.arindexer.model.StopWords;
import io.zaaim.arindexer.stemmer.ArabicStemmerKhoja;
import io.zaaim.arindexer.stemmer.Stemmer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextProcessor {

    private static final Stemmer DEFAULT_STEMMER = new ArabicStemmerKhoja();
    private static Set<String> cachedStopWords;

    public static Map<String, Integer> processDocument(String document) throws IOException {
        Map<String, Integer> termFrequency = new HashMap<>();
        String[] tokens = Tokenizer.tokenize(document);
        tokens = removeStopWords(tokens);

        for (String token : tokens) {
            String stemmed = DEFAULT_STEMMER.stem(token);
            termFrequency.put(stemmed, termFrequency.getOrDefault(stemmed, 0) + 1);
        }
        return termFrequency;
    }

    public static String[] removeStopWords(String[] tokens) throws IOException {
        if (cachedStopWords == null) {
            cachedStopWords = new StopWords().getStopWords();
        }
        return Arrays.stream(tokens)
                .filter(token -> !cachedStopWords.contains(token))
                .toArray(String[]::new);
    }
}