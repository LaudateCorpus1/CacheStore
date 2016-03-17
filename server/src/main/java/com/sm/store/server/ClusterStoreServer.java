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

package com.sm.store.server;

import com.sm.Service;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.replica.client.ReplicaClient;
import com.sm.replica.server.ReplicaServer;
import com.sm.storage.Serializer;
import com.sm.store.Delta;
import com.sm.store.StoreConfig;
import com.sm.store.cluster.ClusterServerConfig;
import com.sm.transport.netty.TCPServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.impl.CacheStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.sm.store.cluster.Utils.*;
import static com.sm.store.utils.ClassBuilder.*;

@JmxManaged(description = "ClusterStoreServer")
public class ClusterStoreServer implements Service {
    protected static final Log logger = LogFactory.getLog(ClusterStoreServer.class);

    private TCPServer server;
    private ClusterServerHandler handler;
    protected ClusterStoreCallBack callBack;
    protected Serializer serializer;
    protected ReplicaServer replicaServer = null ;
    protected ConcurrentMap<String, RemoteStore> storeMaps;
    protected Service.State state;
    protected ClusterServerConfig serverConfig;
    protected int clusterNo;


    public ClusterStoreServer(ClusterServerConfig serverConfig, Serializer serializer) {
        this.serverConfig = serverConfig;
        if ( serializer == null )
            this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        init();
    }

//    public ClusterStoreServer(RemoteConfig remoteConfig){
//        throw new IllegalAccessError("constructor for remoteConfig");
//    }


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
            //add support of persist client and persist replica URL
            if ( each.getPstReplicaUrl() != null && each.getPstReplicaUrl().size() > 0 ) {
                //useLRU property to represent blockValue flag ; but only for luster store
                if ( each.isUseLRU()) {
                    logger.info("useLRU is true, will send blockValue to true and bypass Delta");
                    store.setBlockValue( each.isUseLRU());
                }
                else {
                    logger.info("check for delta interface");
                    if (store.getDelta() == null) {
                        logger.info("inject UnisonDelta..");
                        //store.setDelta(new UnisonDelta(null));
                        store.setDelta( (Delta)createInstance(UNISON_DELTA));
                    }
                }
                logger.info("start persist replica client for "+each.getStore());
                store.startPstReplica("log",each.getPstReplicaUrl() );
                //add support replica client timeout
                List<ReplicaClient> clist = store.getReplicaClientList();
                for (ReplicaClient client : clist) {
                    client.setTimeout(each.getReplicaTimeout());
                }
            }
            //call remote store to start customerize replication outside of cluster
            //store.startReplica(each.getLogPath(), each.getReplicaUrl());
            storeMaps.put( each.getStore(), store);
            //setup trigger from storeConfig
            store.setupTrigger2Cache(each);
        }
        startAdminStore();
        callBack = new ClusterStoreCallBack(storeMaps, serializer, serverConfig.findClusterNodes(clusterNo));
        handler = new ClusterServerHandler(callBack, serverConfig.getMaxThread(), serverConfig.getMaxQueue(), serializer);
        // setup the server handler login freqency
        ((ClusterServerHandler) handler).setFreq( serverConfig.getFreq());
    }


    protected String findPath(String dataPath) {
        if (dataPath.startsWith("/"))
            return dataPath;
        else {
            if ( dataPath.length() == 0)
                return "./";
            else
                return "./"+dataPath+"/" ;
        }
    }

    protected void startAdminStore() {
        logger.info("load admin store ...");
        AdminClusterStore adminStore = new AdminClusterStore(ADMIN_FILENAME, serializer, findPath(serverConfig.getDataPath()+"/admin"), false,
                null, 0,  serverConfig.findClusterNodes(clusterNo));
        // put clusterNodesList and storeConfigList into adminStore
        //setServerConfig will trigger setList()
        adminStore.setServerConfig(serverConfig);
        //adminStore.setList(serverConfig.getClusterNodesList(), serverConfig.getStoreConfigList());
        storeMaps.put(ADMIN_STORE, adminStore);
    }

    public ClusterStore getStore(String store) {
        return (ClusterStore) storeMaps.get(store);
    }

    public void hookShutdown() {
        Runtime.getRuntime().addShutdownHook( new Thread( new Shutdown()));
    }

    public List<String> getAllStoreNames() {
        Set<String> sets = storeMaps.keySet();
        ArrayList<String> list = new ArrayList<String>( sets);
        return list;
    }

    protected Map<String, CacheStore> buildMap(Map<String, RemoteStore> storeMap) {
        Map<String, CacheStore> cacheMap = new ConcurrentHashMap<String, CacheStore>();
        Iterator<String> it = storeMap.keySet().iterator();
        while ( it.hasNext()) {
            String name = it.next();
            RemoteStore store = storeMap.get(name);
            cacheMap.put(name, store.getStore());
        }
        return cacheMap;
    }

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
        if (server != null ) server.getChannel().unbind() ;
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



    @JmxOperation(description = "stopClusterServer")
    public void stopServer() {
        server.shutdown();
        server = null;
    }

    @JmxOperation(description = "stopReplicaServer")
    public void stopReplicaServer() {
        replicaServer.shutdown();
        replicaServer = null;
    }

    @JmxOperation(description = "startReplicaServer")
    public void startReplicaServer() {
        if ( replicaServer !=null ) {
            logger.warn("replica server is up for port "+serverConfig.getReplicaPort());
        }
        else {
            logger.info("start replica server port "+ serverConfig.getReplicaPort() );
            replicaServer = new ReplicaServer( serverConfig.getReplicaPort(), buildMap(storeMaps));
        }
    }

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

    public ConcurrentMap<String, RemoteStore> getStoreMaps() {
        return  storeMaps;
    }

    public ReplicaServer getReplicaServer() {
        return replicaServer;
    }

    public TCPServer getServer() {
        return server;
    }

    public ClusterServerHandler getHandler() {
        return handler;
    }

    public ClusterStoreCallBack getCallBack() {
        return callBack;
    }

    public State getState() {
        return state;
    }

    public int getClusterNo() {
        return clusterNo;
    }

    public ClusterServerConfig getServerConfig() {
        return serverConfig;
    }

    class Shutdown implements Runnable {

        public Shutdown() {
        }

        public void run() {
            shutdown();
        }
    }

}
