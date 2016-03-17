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

package com.sm.replica;

import com.sm.replica.client.ReplicaClient;
import com.sm.replica.client.netty.NTReplicaClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.impl.CacheStore;

import java.util.ArrayList;
import java.util.List;

public class TestRClient {
    private static final Log logger = LogFactory.getLog(TestRClient.class);

    public static void main(String[] args) {
        int i = 10;
        List<String> list = new ArrayList<String>(i);
        for (int j=0; j < i ; j++)
            list.add(null);
        logger.info("freq "+list.size());

        String logPath = "/Users/mhsieh/java/open/voldemort-0.81/config/addon-1/data/log";
        String store = "test";
        CacheStore trxLog = new CacheStore(logPath, null, 0, store,  false );
        ReplicaClient client = new NTReplicaClient("localhost:6910",store, trxLog, logPath);
        new Thread( client).start();
    }

}
