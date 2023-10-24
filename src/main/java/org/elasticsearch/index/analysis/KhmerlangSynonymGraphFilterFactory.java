package org.elasticsearch.index.analysis;


import com.google.common.base.Charsets;
import com.khmerlang.utils.FileResourcesUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kh.KhmerlangAnalyzer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.elasticsearch.analysis.KhmerlangConfig;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

import java.io.InputStreamReader;
import java.io.Reader;

public class KhmerlangSynonymGraphFilterFactory extends AbstractTokenFilterFactory {
    private final String location;
    private final boolean expand;
    private final boolean lenient;
    protected SynonymMap synonymMap;
    protected final Environment environment;
    private KhmerlangAnalyzer analyzer;

    public KhmerlangSynonymGraphFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.environment = environment;
        this.location = settings.get("synonyms_path", KhmerlangConfig.DEFAULT_SYNONYM_PATH);
        this.expand = settings.getAsBoolean("expand", true);
        this.lenient = settings.getAsBoolean("lenient", false);
        analyzer = new KhmerlangAnalyzer(new KhmerlangConfig(settings));

        try {
            Reader rulesReader = new InputStreamReader(FileResourcesUtils.getFileFromResourceAsStream(location), Charsets.UTF_8);
            SynonymMap.Builder parser = null;
            parser = new SolrSynonymParser(true, expand, analyzer);
            ((SolrSynonymParser) parser).parse(rulesReader);
            synonymMap = parser.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
//        throw new IllegalStateException("Call getChainAwareTokenFilterFactory to specialize this factory for an analysis chain first");
        return synonymMap.fst == null ? tokenStream : new SynonymGraphFilter(tokenStream, synonymMap, true);
    }

}
