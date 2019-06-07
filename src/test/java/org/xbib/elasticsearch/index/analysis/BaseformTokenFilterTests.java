package org.xbib.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.test.ESTestCase;
import org.junit.Test;
import org.xbib.elasticsearch.plugin.analysis.baseform.AnalysisBaseformPlugin;

public class BaseformTokenFilterTests extends ESTestCase {
	
    @Test
    public void testOne() throws IOException {

        String source = "Die Jahresfeier der Rechtsanwaltskanzleien auf dem Donaudampfschiff hat viel Ökosteuer gekostet";

        String[] expected = {
                "Die",
                "Jahresfeier",
                "der",
                "Rechtsanwaltskanzleien",
                "Rechtsanwaltskanzlei",
                "auf",
                "dem",
                "der",
                "Donaudampfschiff",
                "hat",
                "haben",
                "viel",
                "Ökosteuer",
                "gekostet",
                "kosten"
        };
        TestAnalysis analysis = createTestAnalysis();
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("baseform");
        Tokenizer tokenizer = analysis.tokenizer.get("standard").create();
        tokenizer.setReader(new StringReader(source));
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testTwo() throws IOException {

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
        TestAnalysis analysis = createTestAnalysis();
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("baseform");
        Tokenizer tokenizer = analysis.tokenizer.get("standard").create();
        tokenizer.setReader(new StringReader(source));
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }


    @Test
    public void testThree() throws IOException {

        String source = "wurde zum tollen gemacht";

        String[] expected = {
                "wurde",
                "werden",
                "zum",
                "tollen",
                "gemacht",
                "machen"
        };
        TestAnalysis analysis = createTestAnalysis();
        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("baseform");
        Tokenizer tokenizer = analysis.tokenizer.get("standard").create();
        tokenizer.setReader(new StringReader(source));
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }
    
    private TestAnalysis createTestAnalysis() throws IOException {
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
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
        TestAnalysis analysis = createTestAnalysis(new IndexSettings(indexMetaData, nodeSettings), nodeSettings, new AnalysisBaseformPlugin(nodeSettings));
        return analysis;
    }

    private void assertSimpleTSOutput(TokenStream stream, String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        assertNotNull(termAttr);
        int i = 0;
        while (stream.incrementToken()) {
            assertTrue(i < expected.length);
            assertEquals(expected[i], termAttr.toString());
            i++;
        }
        assertEquals(i, expected.length);
        stream.close();
    }
}
