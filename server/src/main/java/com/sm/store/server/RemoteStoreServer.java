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

package com.sm.store.server;

import com.sm.Service;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.replica.client.ReplicaClient;
import com.sm.replica.server.ReplicaServer;
import com.sm.storage.Serializer;
import com.sm.store.Delta;
import com.sm.store.RemoteConfig;
import com.sm.store.StoreConfig;
import com.sm.transport.netty.TCPServer;
import com.sm.utils.TupleThree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.impl.CacheStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static com.sm.store.utils.ClassBuilder.*;

@JmxManaged(description = "RemoteStoreServer")
public class RemoteStoreServer {
    protected static final Log logger = LogFactory.getLog(RemoteStoreServer.class);

    private TCPServer server;
    // server listening port
    protected int port;
    // list of pair<storename, path>
    protected List<TupleThree<String, String, Integer>> storeList;
    private StoreServerHandler handler;
    protected StoreCallBack callBack;
    protected Serializer serializer;
    protected int maxThread;
    protected int maxQueue;
    protected ReplicaServer replicaServer = null ;
    protected boolean delay;
    protected Service.State serverState;



    public RemoteStoreServer() {
    }

    public RemoteStoreServer(int port, List<TupleThree<String, String, Integer>> storeList) {
        this(port, storeList, null, false);
    }

   public RemoteStoreServer(int port, List<TupleThree<String, String, Integer>> storeList, boolean delay) {
        this(port, storeList, null, delay);
    }

    public RemoteStoreServer(int port, List<TupleThree<String, String, Integer>> storeList, Serializer serializer, boolean delay) {
        this( port, storeList, serializer, Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() *1000, delay);

    }

    public RemoteStoreServer(RemoteConfig remoteConfig) {
        this.port = remoteConfig.getPort();
        this.maxQueue = remoteConfig.getMaxQueue();
        this.maxThread = remoteConfig.getMaxThread();
        this.serializer = remoteConfig.getSerializer();
        if ( remoteConfig.getConfigList() == null || remoteConfig.getConfigList().size() == 0)
            throw new RuntimeException("ConfigList for store is empty");
        initConfig(remoteConfig);

    }


    protected void initConfig(RemoteConfig remoteConfig) {
        storeList = new ArrayList<TupleThree<String, String, Integer>>();
        for (StoreConfig storeConfig : remoteConfig.getConfigList() ) {
            storeList.add( new TupleThree<String, String, Integer>(storeConfig.getStore(), storeConfig.getDataPath(),
                    storeConfig.getMode() ) );

        }
        init(remoteConfig.isUseNio());
        startReplica(remoteConfig);
        serverState = Service.State.Start;
    }



    /**
     *
     * @param port
     * @param storeList
     * @param serializer
     * @param maxThread
     * @param maxQueue
     * @param delay
     */
    public RemoteStoreServer(int port, List<TupleThree<String, String, Integer>> storeList, Serializer serializer,
                             int maxThread, int maxQueue, boolean delay) {
        this.port = port;
        this.storeList = storeList;
        this.serializer = serializer;
        if (this.serializer == null )
            this.serializer = new HessianSerializer();
        this.maxThread = maxThread;
        this.maxQueue = maxQueue ;
        this.delay = delay;
        init( true);
    }

    protected void init(boolean useNio) {
        logger.info("start StoreCallBack "+storeList.toString() );
        callBack = new StoreCallBack( storeList, serializer, delay);
        handler = new StoreServerHandler(callBack, maxThread, maxQueue);
        if ( useNio)
            server = TCPServer.start( port, handler );
        else
            server = TCPServer.startOio( port, handler);
        //callBack.setServer( server);
        serverState = Service.State.Start;
    }


    public void startReplica(RemoteConfig remoteConfig) {
        //int i = 0;
        for (StoreConfig storeConfig : remoteConfig.getConfigList() ) {
           try {
                RemoteStore store = callBack.getRemoteStore(storeConfig.getStore());
                if ( storeConfig.getReplicaUrl() != null && storeConfig.getReplicaUrl().size() > 0 ) {
                    logger.info("start replica client for "+storeConfig.getStore());
                    store.startReplica("log",storeConfig.getReplicaUrl() );
                    //add support replica client timeout
                    List<ReplicaClient> clientList = store.getReplicaClientList();
                    for (ReplicaClient client : clientList) {
                        client.setTimeout(storeConfig.getReplicaTimeout());
                    }
                }
                //check persist replica URL
               if ( storeConfig.getPstReplicaUrl() != null && storeConfig.getPstReplicaUrl().size() > 0 ) {
                   logger.info("check for delta interface");
                   if ( store.getDelta() == null ) {
                       logger.info("inject UnisonDelta..");
                       //store.setDelta( new UnisonDelta(null));
                       store.setDelta( (Delta) createInstance(UNISON_DELTA));
                   }
                   logger.info("start persist replica client for "+storeConfig.getStore());
                   store.startPstReplica("log",storeConfig.getPstReplicaUrl() );
                   //add support replica client timeout
                   List<ReplicaClient> clientList = store.getReplicaClientList();
                   for (ReplicaClient client : clientList) {
                       client.setTimeout(storeConfig.getReplicaTimeout());
                   }
               }
            } catch (Exception ex) {
                logger.error("fail to startReplica "+storeConfig.getStore()+" "+ex.getMessage(), ex);
            }
        }
        if (replicaServer == null && remoteConfig.getReplicaPort() > 0 ) {
            logger.info("create new replica server port "+remoteConfig.getReplicaPort());
            replicaServer = new ReplicaServer(remoteConfig.getReplicaPort(), buildMap(callBack.getStoreMaps()) );
        }
    }

    private Map<String, CacheStore> buildMap(Map<String, RemoteStore> storeMap) {
        Map<String, CacheStore> cacheMap = new ConcurrentHashMap<String, CacheStore>();
        Iterator<String> it = storeMap.keySet().iterator();
        while ( it.hasNext()) {
            String name = it.next();
            RemoteStore store = storeMap.get(name);
            cacheMap.put(name, store.getStore());
        }
        return cacheMap;
    }

    protected void seTCPtServer(TCPServer server) {
        this.server = server;
    }

    protected TCPServer getTCPServer() {
        return server;
    }

    protected void setTCPHandler(StoreServerHandler handler) {
        this.handler = handler;
    }

    public StoreServerHandler getTCPHandler() {
        return handler;
    }

    public RemoteStore getRemoteStore(String store) {
        return callBack.getRemoteStore( store);
    }

    @JmxOperation(description = "addRemoteStore")
    public boolean addRemoteStore(String storeName,String path, int mode ) {
        if ( callBack.getStoreMaps().get(storeName) != null ) {
            logger.warn(storeName +" has already existed, fail to add store");
            return false;
        }
        else {
            //add new store
            return callBack.addStore(storeName, path, mode);
        }
    }

    public boolean addRemoteStore(RemoteStore remoteStore) {
        return callBack.addStore( remoteStore);
    }


    public List<String> getAllStoreNames() {
        Set<String> sets = callBack.getStoreMaps().keySet();
        ArrayList<String> list = new ArrayList<String>( sets);
        return list;
    }

    @JmxOperation(description = "shutdown")
    public void shutdown() {
        logger.warn("Call shutdown from server");
        if (replicaServer != null){
            logger.warn("shut down replica server "+replicaServer.toString() );
            replicaServer.shutdown();
        }
        //callBack.shutdown();
        logger.warn("Server entered shutdown serverState, waiting for 200 mini seconds at "+  new Date(System.currentTimeMillis()).toString() );
        serverState = Service.State.Shutdown;
        if (server != null ) server.getChannel().unbind() ;
        try {
            Thread.sleep(100L*2);
        } catch(Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.warn("Server enter shutdown serverState unbind acceptor " );
        //if (server != null ) server.getChannel().unbind() ;
        server.shutdown();
        Iterator <Map.Entry<String, RemoteStore>> iterator = callBack.getStoreMaps().entrySet().iterator();
        while (iterator.hasNext() ) {
            Map.Entry<String, RemoteStore> it = iterator.next();
            logger.info("Close Store "+ it.getKey().toString() );
            it.getValue().close();
        }
    }



    @JmxOperation(description = "stopServer")
    public void stopServer() {
        logger.warn("stop server "+port);
        server.shutdown();
        server= null;
    }

    @JmxOperation(description = "startServer")
    public void startServer() {
        if ( server != null ) {
            logger.warn("server is up !!!");
        }
        else {
            logger.info("start server at port "+port);
            server = TCPServer.start( port, handler );
            //callBack.setServer( server);
        }
    }

    public void hookShutdown() {
        Runtime.getRuntime().addShutdownHook( new Thread( new Shutdown()));
    }

    class Shutdown implements Runnable {

        public Shutdown() {
        }

        public void run() {
            shutdown();
        }
    }
}
