package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.kh.KhmerlangTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.analysis.KhmerlangConfig;

public class KhmerlangTokenizerFactory extends AbstractTokenizerFactory  {
    private final KhmerlangConfig config;

    public KhmerlangTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, settings, name);
        config = new KhmerlangConfig(settings);
    }

    @Override
    public org.apache.lucene.analysis.Tokenizer create() {
        return new KhmerlangTokenizer(config);
    }
}
