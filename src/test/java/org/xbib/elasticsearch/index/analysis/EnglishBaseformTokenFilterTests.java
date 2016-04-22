package org.xbib.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.elasticsearch.common.io.Streams.copyToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EnglishBaseformTokenFilterTests {

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
        Settings settings = Settings.settingsBuilder()
                .loadFromSource(copyToStringFromClasspath("/org/xbib/elasticsearch/index/analysis/baseform_en.json"))
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("path.home", System.getProperty("path.home"))
                .put("client.type", "node")
                .build();
        AnalysisService analysisService = MapperTestUtils.analysisService(settings);

        NamedAnalyzer analyzer = analysisService.analyzer("baseform");

        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);

    }

    private static String copyToStringFromClasspath(String path) throws IOException {
        return copyToString(new InputStreamReader(EnglishBaseformTokenFilterTests.class.getResourceAsStream(path), "UTF-8"));
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
