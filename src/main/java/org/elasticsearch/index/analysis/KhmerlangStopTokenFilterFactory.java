package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kh.KhmerlangAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class KhmerlangStopTokenFilterFactory extends AbstractTokenFilterFactory  {
    private final CharArraySet stopWords;
    public KhmerlangStopTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        stopWords = Analysis.parseStopWords(env, settings, KhmerlangAnalyzer.getDefaultStopSet());
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new StopFilter(tokenStream, stopWords);
    }
}
