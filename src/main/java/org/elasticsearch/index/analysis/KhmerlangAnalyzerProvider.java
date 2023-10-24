package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.kh.KhmerlangAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.analysis.KhmerlangConfig;

public class KhmerlangAnalyzerProvider extends AbstractIndexAnalyzerProvider<KhmerlangAnalyzer> {
    private final KhmerlangAnalyzer analyzer;

    public KhmerlangAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        final CharArraySet stopWords = Analysis.parseStopWords(env, settings, KhmerlangAnalyzer.getDefaultStopSet());
        analyzer = new KhmerlangAnalyzer(new KhmerlangConfig(settings), stopWords);
    }

    @Override
    public KhmerlangAnalyzer get() {
        return analyzer;
    }
}
