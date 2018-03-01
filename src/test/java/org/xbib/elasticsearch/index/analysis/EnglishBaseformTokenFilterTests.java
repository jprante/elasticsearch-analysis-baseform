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
import org.junit.Test;
import org.xbib.elasticsearch.plugin.analysis.baseform.AnalysisBaseformPlugin;

public class EnglishBaseformTokenFilterTests extends ESTestCase {

    @Test
    public void test1() throws IOException {

        String source = "“I have a dream that one day this nation will rise up, and live out the true meaning of its creed: ‘We hold these truths to be self-evident: that all men are created equal.’\n" +
                "I have a dream that one day on the red hills of Georgia the sons of former slaves and the sons of former slave owners will be able to sit down together at a table of brotherhood.\n" +
                "I have a dream that one day even the state of Mississippi, a state sweltering with the heat of injustice and sweltering with the heat of oppression, will be transformed into an oasis of freedom and justice.\n" +
                "I have a dream that my four little children will one day live in a nation where they will not be judged by the color of their skin but by the content of their character.\n" +
                "I have a dream today!”";

        String[] expected = new String[]{
                "I",
                "have",
                "a",
                "dream",
                "that",
                "one",
                "day",
                "this",
                "nation",
                "will",
                "rise",
                "up",
                "and",
                "live",
                "out",
                "the",
                "true",
                "meaning",
                "mean",
                "of",
                "its",
                "creed",
                "We",
                "hold",
                "these",
                "truths",
                "truth",
                "to",
                "be",
                "self",
                "evident",
                "all",
                "men",
                "man",
                "are",
                "created",
                "create",
                "equal",
                "on",
                "red",
                "hills",
                "hill",
                "Georgia",
                "sons",
                "son",
                "former",
                "slaves",
                "slave",
                "owners",
                "owner",
                "able",
                "sit",
                "down",
                "together",
                "at",
                "table",
                "brotherhood",
                "even",
                "state",
                "Mississippi",
                "sweltering",
                "swelter",
                "with",
                "heat",
                "injustice",
                "oppression",
                "transformed",
                "transform",
                "into",
                "an",
                "oasis",
                "freedom",
                "justice",
                "my",
                "four",
                "little",
                "children",
                "child",
                "in",
                "where",
                "they",
                "not",
                "judged",
                "judge",
                "by",
                "color",
                "their",
                "skin",
                "but",
                "content",
                "character",
                "today"
        };
        TestAnalysis analysis = createTestAnalysis("org/xbib/elasticsearch/index/analysis/baseform_en.json");
        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("baseform");
        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);

    }

    private TestAnalysis createTestAnalysis(String resource) throws IOException {
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
