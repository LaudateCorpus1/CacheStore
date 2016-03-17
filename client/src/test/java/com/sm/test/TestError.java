/*
 *
 *
 * Copyright 2012-2015 Viant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 */

package com.sm.test;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.client.ClusterClient;
import com.sm.store.client.ClusterClientFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;

import static com.sm.transport.Utils.getOpts;

public class TestError {
    private static final Log logger = LogFactory.getLog(TestError.class);
    ClusterClient client;
    Serializer serializer;

    public TestError(ClusterClient client) {
        this.client = client;
        this.serializer = new HessianSerializer();
    }
    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-configPath", "-url", "-store", "-times"};
        String[] defaults = new String[] {"", "localhost:6172", "store1", "2"};
        String[] paras = getOpts( args, opts, defaults);

        String configPath = paras[0];
        if (  configPath.length() == 0 ) {
            logger.error("missing config path or host");
            throw new RuntimeException("missing -configPath or -host");
        }

        String url = paras[1];
        String store = paras[2];
        int times = Integer.valueOf( paras[3]);
        ClusterClientFactory ccf =ClusterClientFactory.connect(url, store);
        ClusterClient client = ccf.getDefaultStore(8000L);
        TestError testClient = new TestError ( client);
        for ( int i = 0; i < times ; i ++ ) {
            try {
                Key key = Key.createKey("test"+i);
                //client.put(key, "times-" + i);
                Value value = client.get( key);
                value.setVersion( value.getVersion() -2);
                client.put( key, value);
                logger.info(value == null ? "null" : value.getData().toString());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        testClient.client.close();
        ccf.close();
    }

}
