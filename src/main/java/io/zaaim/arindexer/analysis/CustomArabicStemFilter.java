package io.zaaim.arindexer.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import safar.basic.morphology.stemmer.impl.ISRIStemmer;

import java.io.IOException;

public final class CustomArabicStemFilter extends TokenFilter {

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final ISRIStemmer stemmer; // your custom stemmer

    public CustomArabicStemFilter(TokenStream input, ISRIStemmer stemmer) {
        super(input);
        this.stemmer = stemmer;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        // get the current token
        String token = termAttr.toString();

        // stem it using your custom stemmer
        String stemmed = stemmer.stem(token);

        // replace the token in the stream
        termAttr.setEmpty();
        termAttr.append(stemmed);

        return true;
    }
}
