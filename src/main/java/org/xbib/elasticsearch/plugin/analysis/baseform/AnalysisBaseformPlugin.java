package org.xbib.elasticsearch.plugin.analysis.baseform;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;
import org.xbib.elasticsearch.index.analysis.baseform.BaseformAnalysisBinderProcessor;

public class AnalysisBaseformPlugin extends Plugin {

    private final Settings settings;

    @Inject
    public AnalysisBaseformPlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String name() {
        return "analysis-baseform";
    }

    @Override
    public String description() {
        return "A baseform token filter for german and other languages";
    }

    public void onModule(AnalysisModule module) {
        if (settings.getAsBoolean("plugins.baseform.enabled", true)) {
            module.addProcessor(new BaseformAnalysisBinderProcessor());
        }
    }
}
