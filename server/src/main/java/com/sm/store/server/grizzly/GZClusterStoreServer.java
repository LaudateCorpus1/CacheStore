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

package com.sm.store.server.grizzly;

import com.sm.replica.client.ReplicaClient;
import com.sm.storage.Serializer;
import com.sm.store.StoreConfig;
import com.sm.store.cluster.ClusterServerConfig;
import com.sm.store.server.ClusterStore;
import com.sm.store.server.ClusterStoreCallBack;
import com.sm.store.server.ClusterStoreServer;
import com.sm.store.server.RemoteStore;
import com.sm.transport.grizzly.TCPServer;
import voldemort.annotations.jmx.JmxOperation;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.sm.store.cluster.Utils.buildSerializer;

import static com.sm.store.cluster.Utils.buildSerializer;

public class GZClusterStoreServer extends ClusterStoreServer{
    private TCPServer server;
    private StoreServerFilter handler;

    public GZClusterStoreServer(ClusterServerConfig serverConfig, Serializer serializer) {
        super(serverConfig, serializer);
    }

    @Override
    protected void init() {
        clusterNo = serverConfig.getClusterNo();
        logger.info("start Cluster Server " + serverConfig.findClusterNodes(clusterNo).toString()+" for "+
                serverConfig.getStoreConfigList().size()+" store" );
        state = State.Starting;
        storeMaps = new ConcurrentHashMap<String, RemoteStore>();
        for (StoreConfig each : serverConfig.getStoreConfigList()) {
            logger.info("start store "+each.toString());
            ClusterStore store = new ClusterStore(each.getFileName(), buildSerializer(each.getSerializer()), findPath(each.getDataPath()), each.isDelay(), each.getBlockSize(), each.getMode(), each.isSorted(),
                    serverConfig.findClusterNodes(clusterNo));
            if ( each.isDelay()) {
                logger.info("writeThread "+each.getDelayThread());
                store.startWriteThread( each.getDelayThread());
            }
            logger.info("start replica client in cluster path "+each.getLogPath());
            //pass localUrl to start replicate thread to all other nodes in the same cluster
            store.startReplica(each.getLogPath(), serverConfig.getHost() + ":" + serverConfig.getPort());
            //start replica url that was defined in each store
            if ( each.getReplicaUrl() != null && each.getReplicaUrl().size() > 0 ) {
                logger.info("start replica client defined in stores.xml size "+each.getReplicaUrl().size());
                store.startWriteLogThread( each.getLogPath(), each.getReplicaUrl() );
            }
            //add support replica client timeout
            List<ReplicaClient> clientList = store.getReplicaClientList();
            for (ReplicaClient client : clientList) {
                client.setTimeout(each.getReplicaTimeout());
            }
            //call remote store to start customerize replication outside of cluster
            //store.startReplica(each.getLogPath(), each.getReplicaUrl());
            storeMaps.put( each.getStore(), store);
            //setup trigger from storeConfig
            store.setupTrigger2Cache( each);
        }
        startAdminStore();
        callBack = new ClusterStoreCallBack(storeMaps, serializer, serverConfig.findClusterNodes(clusterNo));
        handler = new ClusterStoreFilter(callBack, serverConfig.getMaxThread());
        // setup the server handler login freqency
        ((ClusterStoreFilter) handler).setFreq( serverConfig.getFreq());
    }

    @Override
    public void shutdown() {
        // turn on shutdown flag
        logger.warn("Server entered shutdown serverState, waiting for 2 seconds at "+  new Date(System.currentTimeMillis()).toString() );
        //serverState = State.Shutdown;
        try {
            Thread.sleep(1000L*2);
        } catch(Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        logger.warn("Server enter shutdown serverState unbind acceptor " );
        //if (server != null ) server.getChannel().unbind() ;
        if (replicaServer != null){
            logger.warn("shut down replica server "+replicaServer.toString() );
            replicaServer.shutdown();
        }
        Iterator<Map.Entry<String, RemoteStore>> iterator = storeMaps.entrySet().iterator();
        while (iterator.hasNext() ) {
            Map.Entry<String, RemoteStore> it = iterator.next();
            logger.info("Close Store "+ it.getKey().toString() );
            it.getValue().close();
        }

    }

    @Override
    public void start() {
        startServer();
        state = State.Start;
    }

    @Override
    public void stop() {
        state = State.Shutdown;
        stopServer();
    }

    @Override
    public State getStatus() {
        return state;
    }

    @JmxOperation(description = "startClusterServer")
    public void startServer() {
        if ( server != null) {
            logger.warn("server is up for port "+serverConfig.getPort());
        }
        else {
            logger.info("start cluster server port "+serverConfig.getPort());
            server = TCPServer.start(serverConfig.getPort(), handler);
            //callBack.setServer( server);
        }
    }

    @JmxOperation(description = "stopClusterServer")
    public void stopServer() {
        server.shutdown();
        server = null;
    }

    public TCPServer getGZServer() {
        return server;
    }

    public StoreServerFilter getGZHandler() {
        return handler;
    }

    public void setGZServer(TCPServer server) {
        this.server = server;
    }

    public void setGZHandler(StoreServerFilter handler) {
        this.handler = handler;
    }
}