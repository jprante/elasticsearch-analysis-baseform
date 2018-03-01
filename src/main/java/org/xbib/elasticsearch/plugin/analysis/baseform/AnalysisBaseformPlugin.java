package org.xbib.elasticsearch.plugin.analysis.baseform;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.xbib.elasticsearch.index.analysis.baseform.BaseformTokenFilterAnalysisProvider;

public class AnalysisBaseformPlugin extends Plugin implements AnalysisPlugin {

	private static final Logger LOG = LogManager.getLogger(AnalysisBaseformPlugin.class);

	public static final Setting<Long> SETTING_MAX_CACHE_SIZE = 
			Setting.longSetting("baseform_max_cache_size", 8388608, 131072, Setting.Property.NodeScope);

	private final long maxCacheSize;
	
    @Inject
    public AnalysisBaseformPlugin(Settings settings) {
    		this.maxCacheSize = SETTING_MAX_CACHE_SIZE.get(settings);
    		LOG.info("Maximum Cache Size AnalysisBaseformPlugin: " + this.maxCacheSize);
    		
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
    		return Collections.singletonMap("baseform", new BaseformTokenFilterAnalysisProvider(this.maxCacheSize));
    }

    @Override
	public List<Setting<?>> getSettings() {
		return Stream.of(SETTING_MAX_CACHE_SIZE).collect(Collectors.toList());
	}
}
