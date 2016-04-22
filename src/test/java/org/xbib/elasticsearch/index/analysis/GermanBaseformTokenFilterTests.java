package org.xbib.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.NamedAnalyzer;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.elasticsearch.common.io.Streams.copyToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GermanBaseformTokenFilterTests {

    static NamedAnalyzer analyzer;

    @BeforeClass
    public static void create() throws IOException {
        Settings settings = Settings.settingsBuilder()
                .loadFromSource(copyToStringFromClasspath(("/org/xbib/elasticsearch/index/analysis/baseform_de.json")))
                .build();
        AnalysisService analysisService = MapperTestUtils.analysisService(settings);
        analyzer = analysisService.analyzer("baseform");
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
