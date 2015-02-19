package org.xbib.elasticsearch.plugin.analysis.baseform;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.xbib.elasticsearch.index.analysis.baseform.BaseformAnalysisBinderProcessor;

public class AnalysisBaseformPlugin extends AbstractPlugin {

    private final Settings settings;

    @Inject
    public AnalysisBaseformPlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String name() {
        return "analysis-baseform-" +
                Build.getInstance().getVersion() + "-" +
                Build.getInstance().getShortHash();
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
