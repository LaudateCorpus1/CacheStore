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

package com.sm.replica.client.grizzly;

import com.sm.replica.Filter;
import com.sm.replica.client.ReplicaClient;
import com.sm.transport.grizzly.TCPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.impl.CacheStore;

import static voldemort.store.cachestore.voldeimpl.VoldeUtil.POSTFIX;

public class GZReplicaClient extends ReplicaClient {
    private static final Log logger = LogFactory.getLog(GZReplicaClient.class);

    public GZReplicaClient(String url, String storeName, CacheStore trxLog, String logPath, int idx) {
        this( url, storeName, trxLog, logPath, idx, null);
    }

    public GZReplicaClient(String url, String storeName, CacheStore trxLog, String logPath, int idx, Filter filter) {
        this.url = url;
        this.trxStore = trxLog;
        this.storeName = storeName;
        this.logPath = logPath;
        this.filter = filter;
        this.indexKey = Key.createKey(storeName + POSTFIX);
        setKey(idx);
        init(true);
    }

    public GZReplicaClient(String url, String storeName, CacheStore trxLog, String logPath) {
        this(url, storeName, trxLog, logPath, 0);
    }

    @Override
    protected void connect() {
        String[] str = url.split(":");
        if (str.length != 2) throw new RuntimeException("Malform url "+url);
        try {
            ReplicaClientFilter filter = new ReplicaClientFilter(findTimeout());
            client = TCPClient.start(str[0], Integer.valueOf(str[1]), filter );
        } catch (Exception ex) {
            logger.warn("fail to connect to "+ url);
            sleep4Time();
        }
    }
}
