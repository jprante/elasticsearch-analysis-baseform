package org.xbib.elasticsearch.index.analysis.baseform;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.xbib.elasticsearch.common.fsa.Dictionary;

public class BaseformTokenFilterFactory extends AbstractTokenFilterFactory {

    private final Dictionary dictionary;

    private final long maxCacheSize;

    @Inject
    public BaseformTokenFilterFactory(IndexSettings indexSettings, @Assisted String name, @Assisted Settings settings, long maxCacheSize) {
        super(indexSettings, name, settings);
        this.dictionary = createDictionary(settings);
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new BaseformTokenFilter(tokenStream, dictionary, maxCacheSize);
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