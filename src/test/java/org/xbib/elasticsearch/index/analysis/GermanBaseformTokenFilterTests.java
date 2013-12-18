package org.xbib.elasticsearch.index.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xbib.elasticsearch.plugin.analysis.baseform.AnalysisBaseformPlugin;

public class GermanBaseformTokenFilterTests extends Assert {

    NamedAnalyzer analyzer;

    @BeforeClass
    public void create() {
        AnalysisService analysisService = createAnalysisService();
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
                "langen"
        };
        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);
    }

    private AnalysisService createAnalysisService() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .loadFromClasspath("org/xbib/elasticsearch/index/analysis/baseform_de.json").build();

        Index index = new Index("test");

        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(settings),
                new EnvironmentModule(new Environment(settings)),
                new IndicesAnalysisModule())
                .createInjector();

        AnalysisModule analysisModule = new AnalysisModule(settings, parentInjector.getInstance(IndicesAnalysisService.class));
        new AnalysisBaseformPlugin().onModule(analysisModule);

        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, settings),
                new IndexNameModule(index),
                analysisModule)
                .createChildInjector(parentInjector);

        return injector.getInstance(AnalysisService.class);
    }

    private static void assertSimpleTSOutput(TokenStream stream,
            String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        assertNotNull(termAttr);
        int i = 0;
        while (stream.incrementToken()) {
            assertTrue(i < expected.length);
            assertEquals(expected[i++], termAttr.toString(), "expected different term at index " + i);
        }
        assertEquals(i, expected.length, "not all tokens produced");
    }
}
