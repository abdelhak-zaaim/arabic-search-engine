package io.zaaim.arindexer.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import safar.basic.morphology.stemmer.impl.ISRIStemmer;

public class CustomArabicAnalyzer extends Analyzer {

    private final ISRIStemmer stemmer;

    public CustomArabicAnalyzer(ISRIStemmer stemmer) {
        this.stemmer = stemmer;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new StandardTokenizer();  // or WhitespaceTokenizer
        TokenStream filter = new CustomArabicStemFilter(tokenizer, stemmer);
        return new TokenStreamComponents(tokenizer, filter);
    }
}

