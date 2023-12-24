package com.khmerlang.index.analysis;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.node.info.PluginsAndModules;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugin.analysis.kh.AnalysisKhmerPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.PluginDescriptor;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.test.ESIntegTestCase.Scope.TEST;
import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ESIntegTestCase.ClusterScope(numDataNodes = 1, numClientNodes = 0)
public class KhmerlangAnalysisIntegrationTests extends ESIntegTestCase {
    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singleton(AnalysisKhmerPlugin.class);
    }

    public void testPluginIsLoaded() throws Exception {
        NodesInfoResponse response = client().admin().cluster().prepareNodesInfo().get();
        for (NodeInfo nodeInfo : response.getNodes()) {
            boolean pluginFound = false;
            for (PluginDescriptor pluginInfo : nodeInfo.getInfo(PluginsAndModules.class).getPluginInfos()) {
                if (pluginInfo.getName().equals(AnalysisKhmerPlugin.class.getName())) {
                    pluginFound = true;
                    break;
                }
            }
            assertThat(pluginFound, is(true));
        }
    }

    public void testKhmerAnalyzer() throws ExecutionException, InterruptedException {

        AnalyzeAction.Response response = client().admin().indices()
                .prepareAnalyze("ខ្ញុំស្រលាញ់កម្ពុជា។").setAnalyzer("kh_analyzer")
                .execute().get();
        String[] expected = {"ខ្ញុំ", "ស្រលាញ់", "កម្ពុជា"};

        assertThat(response, notNullValue());
        assertThat(response.getTokens().size(), is(3));
        for (int i = 0; i < expected.length; i++) {
            assertThat(response.getTokens().get(i).getTerm(), is(expected[i]));
        }
    }

    public void testKhmerAnalyzerInMapping() throws ExecutionException, InterruptedException, IOException {
        createIndex("test");
        ensureGreen("test");
        final XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("_doc")
                .startObject("properties")
                .startObject("foo")
                .field("type", "text")
                .field("analyzer", "kh_analyzer")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        client().admin().indices().preparePutMapping("test").setSource(mapping).get();
        final XContentBuilder source = jsonBuilder()
                .startObject()
                .field("foo", "ខ្ញុំស្រលាញ់កម្ពុជា។")
                .endObject();
        index("test", "1", source);
        refresh();
        SearchResponse response = client().prepareSearch("test").
                setQuery(
                        QueryBuilders.matchQuery("foo", "ខ្ញុំស្រលាញ់")
                ).execute().actionGet();
        assertThat(response.getHits().getTotalHits().toString(), is("1 hits"));
    }
}
