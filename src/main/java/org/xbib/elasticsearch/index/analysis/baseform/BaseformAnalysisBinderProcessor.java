package org.xbib.elasticsearch.index.analysis.baseform;

import org.elasticsearch.index.analysis.AnalysisModule;

public class BaseformAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {

    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("baseform", BaseformTokenFilterFactory.class);
    }
}
