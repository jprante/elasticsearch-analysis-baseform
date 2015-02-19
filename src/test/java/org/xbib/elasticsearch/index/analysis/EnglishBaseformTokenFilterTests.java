package org.xbib.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.xbib.elasticsearch.plugin.analysis.baseform.AnalysisBaseformPlugin;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnglishBaseformTokenFilterTests {

    static NamedAnalyzer analyzer;

    @BeforeClass
    public static void create() {
        AnalysisService analysisService = createAnalysisService();
        analyzer = analysisService.analyzer("baseform");
    }

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

        assertSimpleTSOutput(analyzer.tokenStream("content", source), expected);

    }

    private static AnalysisService createAnalysisService() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .loadFromClasspath("org/xbib/elasticsearch/index/analysis/baseform_en.json")
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();

        Index index = new Index("test");

        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(settings),
                new EnvironmentModule(new Environment(settings)),
                new IndicesAnalysisModule())
                .createInjector();

        AnalysisModule analysisModule = new AnalysisModule(settings, parentInjector.getInstance(IndicesAnalysisService.class));
        new AnalysisBaseformPlugin(ImmutableSettings.EMPTY).onModule(analysisModule);

        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, settings),
                new IndexNameModule(index),
                analysisModule)
                .createChildInjector(parentInjector);

        return injector.getInstance(AnalysisService.class);
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
