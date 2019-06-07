package org.xbib.elasticsearch.index.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.test.ESTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xbib.elasticsearch.plugin.analysis.baseform.AnalysisBaseformPlugin;

public class GermanBaseformTokenFilterTests extends ESTestCase {

    static NamedAnalyzer analyzer;

    @BeforeClass
    public static void create() throws IOException {
        TestAnalysis analysis = createTestAnalysis("org/xbib/elasticsearch/index/analysis/baseform_de.json");
        analyzer = analysis.indexAnalyzers.get("baseform");
    }

    @Test
    public void test1() throws IOException {

        String source = "Die Jahresfeier der Rechtsanwaltskanzleien auf dem Donaudampfschiff hat viel Ökosteuer gekostet";

        String[] expected = {
            "Die",
            "Jahresfeier",
            "der",
            "Rechtsanwaltskanzleien",
            "Rechtsanwaltskanzlei",
            "auf",
            "dem",
            "Donaudampfschiff",
            "hat",
            "haben",
            "viel",
            "Ökosteuer",
            "gekostet",
            "kosten"
        };
        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);
    }

    @Test
    public void test2() throws IOException {
        String source = "Das sind Autos, die Nudeln transportieren.";
        String[] expected = {
                "Das",
                "sind",
                "Autos",
                "Auto",
                "die",
                "der",
                "Nudeln",
                "Nudel",
                "transportieren"
        };
        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);
    }

    @Test
    public void test3() throws IOException {
        String source = "dieser test dauert lange";
        String[] expected = {
                "dieser",
                "dies",
                "test",
                "dauert",
                "dauern",
                "lange",
                "lang"
        };
        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);
    }

    private static TestAnalysis createTestAnalysis(String resource) throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .loadFromStream(resource, ClassLoader.getSystemClassLoader().getResourceAsStream(resource), false)
                .build();
        IndexMetaData indexMetaData = IndexMetaData.builder("test")
                .settings(settings)
                .numberOfShards(1)
                .numberOfReplicas(1)
                .build();
        Settings nodeSettings = Settings.builder()
        			.put(AnalysisBaseformPlugin.SETTING_MAX_CACHE_SIZE.getKey(), 131072)
                .put("path.home", System.getProperty("path.home", "/tmp"))
                .build();
        TestAnalysis analysis = createTestAnalysis(new IndexSettings(indexMetaData, nodeSettings), nodeSettings, new AnalysisBaseformPlugin(nodeSettings), new CommonAnalysisPlugin());
        return analysis;
    }

    private void assertSimpleTSOutput(TokenStream stream, String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        assertNotNull(termAttr);
        int i = 0;
        while (stream.incrementToken()) {
            assertTrue(i < expected.length);
            assertEquals(expected[i++], termAttr.toString());
        }
        assertEquals(i, expected.length);
        stream.close();
    }
}
