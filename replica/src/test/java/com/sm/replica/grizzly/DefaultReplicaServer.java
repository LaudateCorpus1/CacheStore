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

package com.sm.replica.grizzly;

import com.sm.replica.ParaList;
import com.sm.replica.UnisonCallBack;
import com.sm.replica.server.grizzly.ReplicaServer;
import com.sm.replica.server.grizzly.UnisonFilter;
import com.sm.store.StoreParas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.impl.CacheStore;
import voldemort.store.cachestore.impl.LogChannel;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sm.transport.Utils.getOpts;

public class DefaultReplicaServer implements UnisonCallBack {
    private static final Log logger = LogFactory.getLog(DefaultReplicaServer.class);
    LogChannel logChannel;
    int index;
    String store;
    String path;

//    public DefaultReplicaServer(LogChannel cacheStore) {
//        this(cacheStore, 0);
//    }
//
//    public DefaultReplicaServer(LogChannel logChannel, int index) {
//        this.logChannel = logChannel;
//        this.index = index;
//    }

    public DefaultReplicaServer(LogChannel logChannel, int index, String store, String path) {
        this.logChannel = logChannel;
        this.index = index;
        this.store = store;
        this.path = path;
    }

    public static void main(String[] args) {
        String[] opts = new String[] {"-store","-path", "-port", "-mode", "index"};
        String[] defaults = new String[] {"replica","./data", "7120", "0" , "0" };
        String[] paras = getOpts( args, opts, defaults);
        String store = paras[0];
        int port = Integer.valueOf( paras[2]);
        String path = paras[1];
        int mode = Integer.valueOf(paras[3]);
        int index = Integer.valueOf( paras[4]);
        CacheStore cacheStore = new CacheStore(path, null, mode);
        LogChannel logChannel = new LogChannel(store, index, path);
        DefaultReplicaServer defaultReplicaServer = new DefaultReplicaServer( logChannel, index, store, path);
        HashMap<String, CacheStore> storesMap = new HashMap<String, CacheStore>();
        storesMap.put(store,  cacheStore);
        UnisonFilter unisonServerFilter = new UnisonFilter( defaultReplicaServer);
        unisonServerFilter.setFreq(1);
        logger.info("start server at "+port);
        ReplicaServer server = new ReplicaServer( port, storesMap, unisonServerFilter);
        defaultReplicaServer.hookShutdown();
        logger.info("set main thread to wait()");
        try {
            //System.in.read();
            Object obj = new Object();
            synchronized (obj) {
                obj.wait();
            }
        } catch (Exception io) {
            logger.error( io.getMessage(), io);
        }
    }

    public Lock lock = new ReentrantLock();
    public final int MAX_RECORD = 1000000; //1 million records
    @Override
    public void processParaList(ParaList prarList) {
        if ( prarList.getSize() == 0 ) return ;
            try {
                for (StoreParas each : prarList.getLists()) {
                    // delete operation if value == null
                    if (each.getValue() == null)
                        logChannel.writeDelete(each.getKey());
                    else
                        logChannel.writeRecord(each.getKey(), each.getValue());
                }
        } catch (Exception ex) {
            logger.error( ex.getMessage(), ex);
        }

        if ( logChannel.getComputeTotal() >= MAX_RECORD) {
            lock.lock();
            try {
                logChannel.close();
                index ++;
                logger.info("reach maxrecord "+MAX_RECORD+" create new channel index "+index+ " store "+store);
                logChannel = new LogChannel(store, index, path);
            } catch ( Exception ex) {
                logger.error( ex.getMessage(), ex);
            } finally {
                lock.unlock();
            }

        }

    }

    @Override
    public void recovery(Key key, Object value) {
        //do nothing
    }

    public void shutdown() {
        logger.warn("shut down server close "+logChannel.getFilename());
        logChannel.close();
    }

    public void hookShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
    }

    class Shutdown implements Runnable {

        public Shutdown() {
        }

        public void run() {
            shutdown();
        }
    }
}
