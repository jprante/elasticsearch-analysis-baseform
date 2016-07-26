package org.xbib.elasticsearch.plugin.baseform;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.junit.Test;

public class BaseformPluginTest extends NodeTestUtils {

    private final static ESLogger logger = ESLoggerFactory.getLogger(BaseformPluginTest.class.getName());

    @Test
    public void test() {
        Client client = client("1");
        // TODO
        client.close();
    }

}
