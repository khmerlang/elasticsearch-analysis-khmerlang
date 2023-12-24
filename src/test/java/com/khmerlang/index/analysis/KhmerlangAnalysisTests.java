package com.khmerlang.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.kh.KhmerlangAnalyzer;
import org.apache.lucene.analysis.kh.KhmerlangTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetadata;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.plugin.analysis.kh.AnalysisKhmerPlugin;
import org.elasticsearch.test.ESSingleNodeTestCase;

import java.io.IOException;
import java.io.StringReader;

import static org.apache.lucene.analysis.BaseTokenStreamTestCase.assertTokenStreamContents;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class KhmerlangAnalysisTests extends ESSingleNodeTestCase {
    public void testKhmerlangAnalysis() throws IOException {
        TestAnalysis analysis = createTestAnalysis(Settings.EMPTY);
        assertNotNull(analysis);

        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);
        assertThat(analyzer.analyzer(), instanceOf(KhmerlangAnalyzer.class));

        TokenizerFactory tokenizerFactory = analysis.tokenizer.get("kh_tokenizer");
        assertNotNull(tokenizerFactory);
        assertThat(tokenizerFactory, instanceOf(KhmerlangTokenizerFactory.class));
    }

    public void testKhmerlangAnalyzer() throws IOException {
        TestAnalysis analysis = createTestAnalysis(Settings.EMPTY);
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);

        TokenStream ts = analyzer.analyzer().tokenStream("test", "ខ្ញុំស្រលាញ់កម្ពុជា។");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        for (String expected : new String[]{"ខ្ញុំ", "ស្រលាញ់", "កម្ពុជា"}) {
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term.toString(), equalTo(expected));
        }
        assertThat(ts.incrementToken(), equalTo(false));
    }

    public void testKhmerlangAnalyzerCorrectCharacter() throws IOException {
        TestAnalysis analysis = createTestAnalysis(Settings.EMPTY);
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);

        TokenStream ts = analyzer.analyzer().tokenStream("test", "ស្រ្តី ប្តី");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        for (String expected : new String[]{"ស្ត្រី", "ប្ដី"}) {
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term.toString(), equalTo(expected));
        }
        assertThat(ts.incrementToken(), equalTo(false));
    }

    public void testCustomKhmerAnalyzer() throws IOException {
        Settings settings = Settings.builder()
                .put("index.analysis.analyzer.my_analyzer.tokenizer", "kh_tokenizer")
                .build();
        TestAnalysis analysis = createTestAnalysis(settings);

        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("my_analyzer");
        assertNotNull(analyzer);
        assertThat(analyzer.analyzer(), instanceOf(CustomAnalyzer.class));
        assertThat(analyzer.analyzer().tokenStream(null, new StringReader("")), instanceOf(KhmerlangTokenizer.class));
    }

    public void testKhmerAnalyzerWithCustomTokenizer() throws IOException {
        Settings settings = Settings.builder()
                .put("index.analysis.analyzer.kh_analyzer.tokenizer", "my_tokenizer")
                .put("index.analysis.tokenizer.my_tokenizer.type", "kh_tokenizer")
                .build();
        TestAnalysis analysis = createTestAnalysis(settings);
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);
        TokenStream ts = analyzer.analyzer().tokenStream("test", "១២៣៤៥៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 1234567អ្នកចេះនិយាយភាសាខ្មែរទេ?");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        for (String expected : new String[]{"១២៣៤៥៦៧", "អ្នកចេះ", "និយាយ", "ភាសាខ្មែរ", "ទេ", "1234567", "អ្នកចេះ", "និយាយ", "ភាសាខ្មែរ", "ទេ"}) {
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term.toString(), equalTo(expected));
        }
        assertThat(ts.incrementToken(), equalTo(false));
    }

    public void testKhmerAnalyzerWithCustomTokenizerWithStopWords() throws IOException {
        Settings settings = Settings.builder()
                .put("index.analysis.analyzer.kh_analyzer.tokenizer", "my_tokenizer")
                .put("index.analysis.analyzer.kh_analyzer.filter", "kh_stop")
                .put("index.analysis.tokenizer.my_tokenizer.type", "kh_tokenizer")
                .build();
        TestAnalysis analysis = createTestAnalysis(settings);
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);
        TokenStream ts = analyzer.analyzer().tokenStream("test", "១២៣៤៥៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 1234567អ្នកចេះនិយាយភាសាខ្មែរទេ?");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        //"ទេ" is removed since it is in stopwords list
        for (String expected : new String[]{"១២៣៤៥៦៧", "អ្នកចេះ", "និយាយ", "ភាសាខ្មែរ", "1234567", "អ្នកចេះ", "និយាយ", "ភាសាខ្មែរ"}) {
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term.toString(), equalTo(expected));
        }
        assertThat(ts.incrementToken(), equalTo(false));
    }

    public void testKhmerAnalyzerWithCustomTokenizerKhmerNumber() throws IOException {
        Settings settings = Settings.builder()
                .put("index.analysis.analyzer.kh_analyzer.tokenizer", "my_tokenizer")
                .put("index.analysis.analyzer.kh_analyzer.filter", "kh_stop, kh_number")
                .put("index.analysis.tokenizer.my_tokenizer.type", "kh_tokenizer")
                .build();
        TestAnalysis analysis = createTestAnalysis(settings);
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);
        TokenStream ts = analyzer.analyzer().tokenStream("test", "១២៣៤៥.៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 12345.67អ្នកចេះនិយាយភាសាខ្មែរទេ?");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        //"ទេ" is removed since it is in stopwords list
        for (String expected : new String[]{"12345.67", "អ្នកចេះ", "និយាយ", "ភាសាខ្មែរ", "12345.67", "អ្នកចេះ", "និយាយ", "ភាសាខ្មែរ"}) {
//            assertThat(ts.incrementToken(), equalTo(true));
            ts.incrementToken();
            assertThat(term.toString(), equalTo(expected));
        }
        assertThat(ts.incrementToken(), equalTo(false));
    }

    public void testKhmerlangTokenizer() throws IOException {
        TestAnalysis analysis = createTestAnalysis(Settings.EMPTY);
        TokenizerFactory tokenizerFactory = analysis.tokenizer.get("kh_tokenizer");
        assertNotNull(tokenizerFactory);

        Tokenizer tokenizer = tokenizerFactory.create();
        assertNotNull(tokenizer);

        tokenizer.setReader(new StringReader("ខ្ញុំស្រលាញ់កម្ពុជា។"));
        assertTokenStreamContents(tokenizer, new String[]{"ខ្ញុំ", "ស្រលាញ់", "កម្ពុជា"});
    }

    public void testKhmerAnalyzerWithCustomTokenizerWithSynonym() throws IOException {
        Settings settings = Settings.builder()
                .put("index.analysis.analyzer.kh_analyzer.tokenizer", "my_tokenizer")
                .put("index.analysis.analyzer.kh_analyzer.filter", "kh_synonym_graph")
                .put("index.analysis.tokenizer.my_tokenizer.type", "kh_tokenizer")
                .build();
        TestAnalysis analysis = createTestAnalysis(settings);
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("kh_analyzer");
        assertNotNull(analyzer);
        TokenStream ts = analyzer.analyzer().tokenStream("test", "ខ្ញុំ");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        for (String expected : new String[]{"ខ្ញុំ", "អញ", "យើង"}) {
            ts.incrementToken();
            assertThat(term.toString(), equalTo(expected));
        }
    }

    public TestAnalysis createTestAnalysis(Settings analysisSettings) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetadata.SETTING_VERSION_CREATED, Version.CURRENT)
                .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir())
                .put(analysisSettings)
                .build();
        return AnalysisTestsHelper.createTestAnalysisFromSettings(settings, new AnalysisKhmerPlugin());
    }
}
