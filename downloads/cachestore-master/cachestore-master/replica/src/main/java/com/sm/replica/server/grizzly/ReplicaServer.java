/*
 *
 *  * Copyright 2012-2015 Viant.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package com.sm.replica.server.grizzly;

import com.sm.transport.grizzly.TCPServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.impl.CacheStore;

import java.util.Map;

public class ReplicaServer {

    private static final Log logger = LogFactory.getLog(ReplicaServer.class);

    private TCPServer server;
    private int port;
    private Map<String, CacheStore> storeMap;
    private ReplicaServerFilter replicaServerFilter;

    public ReplicaServer(int port, Map<String, CacheStore> storeMap) {
        this(port, storeMap, null);
    }

    public ReplicaServer(int port, Map<String, CacheStore> storeMap, ReplicaServerFilter replicaServerFilter) {
        this.port = port;
        this.storeMap = storeMap;
        this.replicaServerFilter = replicaServerFilter;
        init();

    }

    private void init() {
        if ( replicaServerFilter == null )
            this.replicaServerFilter = new ReplicaServerFilter(storeMap);
        this.server = TCPServer.start(port, replicaServerFilter);

    }

    public CacheStore getCacheStore(String store) {
        return storeMap.get( store);
    }

    public void addStore(String storeName, CacheStore cacheStore) {
        if ( storeMap != null)
            storeMap.put(storeName, cacheStore);
        else
            logger.error("storeMap did not exist in handle");
    }

    @JmxOperation(description = "start")
    public void start() {
        init();
    }

    @JmxOperation(description = "stop")
    public void stop() {
        logger.warn("stop the server");
        server.shutdown();
    }

    @JmxOperation(description = "setFreq")
    public void setFreq(int size) {
        replicaServerFilter.setFreq(size);
    }


    public void shutdown() {
        logger.warn("Call shutdown from server");
        server.shutdown();
        replicaServerFilter.shutdown();
    }

}
