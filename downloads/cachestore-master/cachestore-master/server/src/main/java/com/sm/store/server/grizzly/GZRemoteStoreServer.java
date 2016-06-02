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

package com.sm.store.server.grizzly;

import com.sm.Service;
import com.sm.storage.Serializer;
import com.sm.store.RemoteConfig;
import com.sm.store.server.RemoteStore;
import com.sm.store.server.RemoteStoreServer;
import com.sm.store.server.StoreCallBack;

import com.sm.transport.grizzly.TCPServer;
import com.sm.utils.TupleThree;
import voldemort.annotations.jmx.JmxOperation;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GZRemoteStoreServer extends RemoteStoreServer {
    private TCPServer server;
    private StoreServerFilter handler;

    public GZRemoteStoreServer() {
    }
    public GZRemoteStoreServer(int port, List<TupleThree<String, String, Integer>> storeList, boolean delay) {
        super(port, storeList, delay);
    }

    public GZRemoteStoreServer(int port, List<TupleThree<String, String, Integer>> storeList, Serializer serializer, boolean delay){
        super( port, storeList, serializer, delay);
    }

    public GZRemoteStoreServer(RemoteConfig remoteConfig){
        super( remoteConfig);
    }

    @Override
    protected void init(boolean useNio) {
        logger.info("start StoreCallBack "+storeList.toString() );
        callBack = new StoreCallBack( storeList, serializer, delay);
        handler = new StoreServerFilter(callBack, maxThread, maxQueue);
        server = TCPServer.start(port, handler);
        //callBack.setServer( server);
        serverState = Service.State.Start;
    }

    protected TCPServer getGZServer() {
        return server;
    }

    protected void setGZServer(TCPServer server) {
        this.server = server;
    }

    protected StoreServerFilter getGZHandler() {
        return handler;
    }

    protected void setGZHandler(StoreServerFilter handler) {
        this.handler = handler;
    }

    @JmxOperation(description = "shutdown")
    @Override
    public void shutdown() {
        logger.warn("Call shutdown from server");
        if (replicaServer != null){
            logger.warn("shut down replica server "+replicaServer.toString() );
            replicaServer.shutdown();
        }
        //callBack.shutdown();
        logger.warn("Server entered shutdown serverState, waiting for 200 mini seconds at "+  new Date(System.currentTimeMillis()).toString() );
        serverState = Service.State.Shutdown;
        try {
            Thread.sleep(100L*2);
        } catch(Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.warn("Server enter shutdown serverState unbind acceptor " );
        //if (server != null ) server.getChannel().unbind() ;
        server.shutdown();
        Iterator<Map.Entry<String, RemoteStore>> iterator = callBack.getStoreMaps().entrySet().iterator();
        while (iterator.hasNext() ) {
            Map.Entry<String, RemoteStore> it = iterator.next();
            logger.info("Close Store "+ it.getKey().toString() );
            it.getValue().close();
        }
    }

    @JmxOperation(description = "stopServer")
    @Override
    public void stopServer() {
        logger.warn("stop server "+port);
        server.shutdown();
        server= null;
    }

    @JmxOperation(description = "startServer")
    @Override
    public void startServer() {
        if ( server != null ) {
            logger.warn("server is up !!!");
        }
        else {
            logger.info("start server at port "+port);
            server = TCPServer.start(port, handler);
            //callBack.setServer( server);
        }
    }
}
