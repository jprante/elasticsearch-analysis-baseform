package org.xbib.elasticsearch.index.analysis.baseform;

import java.io.IOException;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;

public class BaseformTokenFilterAnalysisProvider implements AnalysisProvider<TokenFilterFactory>{

	private final long maxBaseformEntries;
	
	public BaseformTokenFilterAnalysisProvider(long maxBaseformEntries) {
		this.maxBaseformEntries = maxBaseformEntries;
	}
	
	@Override
	public TokenFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings)
			throws IOException {
		return new BaseformTokenFilterFactory(indexSettings, name, settings, maxBaseformEntries);
	}

}
