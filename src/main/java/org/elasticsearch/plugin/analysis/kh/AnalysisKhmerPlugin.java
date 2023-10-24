package org.elasticsearch.plugin.analysis.kh;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class AnalysisKhmerPlugin extends Plugin implements AnalysisPlugin {
    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("kh_tokenizer", KhmerlangTokenizerFactory::new);
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return singletonMap("kh_analyzer", KhmerlangAnalyzerProvider::new);
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("kh_stop", KhmerlangStopTokenFilterFactory::new);
        extra.put("kh_number", KhmerlangNumberFilterFactory::new);
        extra.put("kh_synonym", KhmerlangSynonymGraphFilterFactory::new);
        extra.put("kh_synonym_graph", KhmerlangSynonymGraphFilterFactory::new);
        return extra;
    }
}
