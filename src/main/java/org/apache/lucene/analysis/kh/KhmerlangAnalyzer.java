package org.apache.lucene.analysis.kh;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.elasticsearch.analysis.KhmerlangConfig;

import java.io.IOException;

public class KhmerlangAnalyzer extends StopwordAnalyzerBase {
    public final static String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
    private static final String STOPWORDS_COMMENT = "#";
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        static {
            try {
                DEFAULT_STOP_SET = loadStopwordSet(false, KhmerlangAnalyzer.class, DEFAULT_STOPWORDS_FILE, STOPWORDS_COMMENT);
            } catch (IOException ex) {
                // default set should always be present as it is part of the
                // distribution (JAR)
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }

    private final KhmerlangConfig config;

    public KhmerlangAnalyzer(KhmerlangConfig config) {
        this(config, getDefaultStopSet());
    }

    public KhmerlangAnalyzer(KhmerlangConfig config, CharArraySet stopWords) {
        super(stopWords);
        this.config = config;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new KhmerlangTokenizer(config);
        TokenStream streamResult = source;
        if(config.lowercase) {
            streamResult = new LowerCaseFilter(streamResult);
        }

        if(config.enableStopwords) {
            streamResult = new StopFilter(streamResult, stopwords);
        }

        // should in last of all?
        if(config.khmerNumber) {
            streamResult = new KhmerNumberFilter(streamResult);
        }

        return new TokenStreamComponents(source, streamResult);
    }
}
