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

package com.sm.store.client;

import com.sm.storage.Serializer;
import com.sm.store.StorePersistence;
import com.sm.store.client.netty.NTRemoteClientImpl;
import com.sm.store.server.RemoteStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


public class ClusterClientImpl implements StorePersistence {
    private static final Log logger = LogFactory.getLog(ClusterClientImpl.class);
    //clusters of url, separate by ,
    private List<String> urls;
    private Serializer serializer;
    //name of file name without path
    private String store;
    //list of clients
    private List<StorePersistence> clients;
    //
    private ConcurrentMap<String, RemoteStore> remoteStores;

    /**
     *
     * @param urls can inprocess+fullpath+mode or hostname:port
     * @param store
     */
    public ClusterClientImpl(List<String> urls, String store) {
        this(urls, store, null);
    }

    /**
     * \
     * @param urls urls can inprocess+fullpath+mode or hostname:port
     * @param store
     * @param serializer
     */
    public ClusterClientImpl(List<String> urls, String store, Serializer serializer) {
        this(urls, store, serializer, null);

    }

    public ClusterClientImpl(List<String> urls, String store, Serializer serializer, ConcurrentMap<String, RemoteStore> remoteStores) {
        if ( urls.size() ==0 ) throw new RuntimeException("urls can not empty");
        this.urls = urls;
        this.serializer = serializer;
        this.store = store;
        this.remoteStores = remoteStores;
        init();
    }

    private void init(){
        clients = new ArrayList<StorePersistence>(urls.size());
        for (String url : urls) {
            if ( isInprocess( url )) {
                logger.info("starting inprocess client "+url);
                if ( remoteStores == null ) throw new RuntimeException("remoteStores can not be null");
                else {
                    RemoteStore rm = remoteStores.get(store);
                    if ( rm == null )  throw new RuntimeException("can not find "+store + " in remoteStores");
                    else {
                        clients.add( new LocalClusterClientImpl( rm));
                    }
                }
            }
            else {
                logger.info("starting client "+url );
                clients.add( new NTRemoteClientImpl(url, serializer, store));
            }
        }

    }


    private static String IN_PROCESS = "inprocess";
    //example inprocess/opt/store/datapath/0  prefix inprocess follow by full path and mode
    //use full path + store as filename path
    private boolean isInprocess(String url) {
        if (url.indexOf(IN_PROCESS) == 0)
            return true;
        else
            return false;
    }

    @Override
    public void put(Key key, Value value) {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                clients.get(i).put( key, value);
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+urls.get(i)+" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to put "+errorMsg);
        }
    }

    @Override
    public void put(Key key, Object data) {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                clients.get(i).put( key, data );
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+urls.get(i)+" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to put "+errorMsg);
        }
    }

    @Override
    public Value get(Key key) {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                return clients.get(i).get( key );
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+urls.get(i)+" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to get "+errorMsg);
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void insert(Key key, Value value) {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                clients.get(i).insert( key, value);
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+urls.get(i)+" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to put "+errorMsg);
        }
    }

    @Override
    public boolean remove(Key key) {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                clients.get(i).remove(key);
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+urls.get(i)+" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to get "+errorMsg);
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        for (StorePersistence client : clients) {
            if ( client instanceof RemoteClientImpl) {
                client.close();
                logger.info("close connection on client side");
            }
            else
                logger.info("close() does nothing on client side");
        }
    }

    @Override
    public Iterator getKeyIterator() {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                return clients.get(i).getKeyIterator();
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to get "+errorMsg);
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        int error = 0;
        String errorMsg = "";
        for ( int i=0 ; i < urls.size() ; i++) {
            try {
                return clients.get(i).size();
            } catch (StoreException ex) {
                logger.error( ex.getMessage(), ex);
                errorMsg = errorMsg +" "+ex.getMessage() ;
                error++;
            }
        }
        if ( error == urls.size()  ) {
            throw new StoreException("fail to get "+errorMsg);
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pack(int rate) {
        logger.warn("Client side did not support pack");
    }

    @Override
    public void backup(String path, int rate) {
        logger.warn("Client side did not support backup");
    }
}
