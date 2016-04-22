package org.xbib.elasticsearch.index.analysis.baseform;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;
import org.xbib.elasticsearch.common.fsa.Dictionary;

import java.io.IOException;
import java.io.InputStreamReader;

public class BaseformTokenFilterFactory extends AbstractTokenFilterFactory {

    private final Dictionary dictionary;

    @Inject
    public BaseformTokenFilterFactory(Index index,
                                      IndexSettingsService indexSettingsService,
                                      @Assisted String name,
                                      @Assisted Settings settings) {
        super(index, indexSettingsService.indexSettings(), name, settings);
        this.dictionary = createDictionary(settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new BaseformTokenFilter(tokenStream, dictionary);
    }

    private Dictionary createDictionary(Settings settings) {
        try {
            String lang = settings.get("language", "de");
            String path = "/baseform/" + lang + "-lemma-utf8.txt";
            return new Dictionary().load(new InputStreamReader(getClass().getResourceAsStream(path), "UTF-8"));
        } catch (IOException e) {
            throw new ElasticsearchException("resources in settings not found: " + settings, e);
        }
    }
}