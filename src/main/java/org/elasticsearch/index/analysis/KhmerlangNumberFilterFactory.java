package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kh.KhmerNumberFilter;
import org.elasticsearch.analysis.KhmerlangConfig;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class KhmerlangNumberFilterFactory extends AbstractTokenFilterFactory {
    private final KhmerlangConfig config;
    public KhmerlangNumberFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        config = new KhmerlangConfig(settings);
    }


    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new KhmerNumberFilter(tokenStream);
    }
}
