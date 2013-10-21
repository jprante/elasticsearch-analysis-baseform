
package org.xbib.elasticsearch.plugin.analysis.baseform;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.xbib.elasticsearch.index.analysis.BaseformTokenFilterFactory;

public class AnalysisBaseformPlugin extends AbstractPlugin {

    @Override public String name() {
        return "analysis-baseform";
    }

    @Override public String description() {
        return "A baseform token filter for german and other languages";
    }

    public void onModule(AnalysisModule module) {
        module.addTokenFilter("baseform", BaseformTokenFilterFactory.class);
    }
}
