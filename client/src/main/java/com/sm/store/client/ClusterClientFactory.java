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
 */package com.sm.store.client;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import com.sm.store.OpType;
import com.sm.store.StoreConfig;
import com.sm.store.StoreParas;
import com.sm.store.cluster.ClusterNodes;
import com.sm.transport.Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;

import java.util.List;

import static com.sm.store.cluster.Utils.*;

@JmxManaged(description = "ClusterClientFactory")
public class ClusterClientFactory {
    private static final Log logger = LogFactory.getLog(ClusterClientFactory.class);

    private String url;
    private List<ClusterNodes> clusterNodesList;
    private List<StoreConfig> storeConfigList;
    private String store;
    private Client adminClient;
    private StoreConfig currentStore;
    private Serializer serializer;
    private ClusterClient clusterClient ;
    private Serializer embeddedSerializer = new HessianSerializer();
    //default ot netty
    private TCPClientFactory.ClientType clientType ;
    private long timeout;
    private boolean reload = false;

    private ClusterClientFactory(String url, String store) {
        this( url, store, null);

    }

    private ClusterClientFactory(String url, String store, Serializer serializer) {
        this(url, store, serializer, TCPClientFactory.ClientType.Grizzly);
    }

    private ClusterClientFactory(String url, String store, Serializer serializer, TCPClientFactory.ClientType clientType) {
        this.url = url;
        this.store = store;
        if ( serializer != null)
            this.serializer = serializer;
        else
            this.serializer = embeddedSerializer;
        this.clientType = clientType;
        connectTo();
    }

    public static ClusterClientFactory connect(String url, String store) {
        return new ClusterClientFactory(url, store);
    }

    public static ClusterClientFactory connect(String url, String store, Serializer serializer) {
        return new ClusterClientFactory(url, store, serializer);
    }

    public static ClusterClientFactory connect(String url, String store, TCPClientFactory.ClientType clientType) {
        return new ClusterClientFactory(url, store, null, clientType);
    }

    public static ClusterClientFactory connect(String url, String store, Serializer serializer,TCPClientFactory.ClientType clientType) {
        return new ClusterClientFactory(url, store, serializer, clientType);
    }

    public ClusterClient getDefaultStore(long timeout) {
        return getDefaultStore( timeout, true);
    }

    public ClusterClient getDefaultStore(long timeout, boolean nio) {
        this.timeout = timeout;
        if ( reload || clusterClient == null) {
            clusterClient = new ClusterClient( clusterNodesList, currentStore.getStore() , serializer, clientType);
            clusterClient.setTimeout( timeout);
            clusterClient.setNio( nio);
            clusterClient.setClientType( clientType);
        }
        return clusterClient;
    }


    public ClusterClient getDefaultStore() {
        return getDefaultStore( 6000L);
    }

    @JmxOperation(description = "switchStore")
    public ClusterClient switchStore(String name) {
        this.reload = true;
        if ( ! store.equals( name)) {
            currentStore = findStoreConfig( name);
            store = name;
            return getDefaultStore();
        }
        else {
            return getDefaultStore();
        }
    }

    public String getUrl() {
        return url;
    }

    public List<ClusterNodes> getClusterNodesList() {
        return clusterNodesList;
    }

    public List<StoreConfig> getStoreConfigList() {
        return storeConfigList;
    }

    public String getStore() {
        return store;
    }

    public Client getAdminClient() {
        return adminClient;
    }

    public StoreConfig getCurrentStore() {
        return currentStore;
    }

    private void connectTo() {
        logger.info("try to connect "+ url );
        adminClient = findAdminClient();
        currentStore = findStoreConfig( store );
    }

    private Client findAdminClient() {
        String[] urlList = url.split(",");
        if ( urlList.length == 0 ) {
            throw new RuntimeException("url format error "+url);
        }
        Client client = null;
        for ( String each : urlList ) {
            try {
                //client = TCPClient.start( urlString[0], port, new ClusterClientHandler(2000L, embeddedSerializer), (byte) 1);
                client = TCPClientFactory.createClient( clientType, each, 2000L);
                if ( client != null && client.isConnected() )  {
                    logger.info("retrieve cluster node and store config list");
                    Response response = client.sendRequest( adminRequest(CLUSTER_KEY));
                    if ( response.isError() || ! (response.getPayload() instanceof StoreParas) )
                        logger.error(CLUSTER_KEY+" => "+ response.getPayload().toString() );
                    clusterNodesList = (List<ClusterNodes>) embeddedSerializer.toObject( (byte[]) ((StoreParas)response.getPayload()).getValue().getData()) ;
                    response = client.sendRequest(adminRequest(STORE_CONFIG_KEY));
                    if ( response.isError() || ! (response.getPayload() instanceof StoreParas) )
                        logger.error( STORE_CONFIG_KEY+" => "+response.getPayload().toString() );
                    storeConfigList = (List<StoreConfig>) embeddedSerializer.toObject( (byte[]) ((StoreParas)response.getPayload()).getValue().getData());
                    return client;
                }
            } catch (RuntimeException ex) {
                //swallow exception
            }
        }
        if ( client == null ) throw new RuntimeException("fail to create admin client "+url);
        else {
            return client;
        }
    }

    @JmxOperation(description = "reloadClusterClient")
    public ClusterClient reloadClusterClient() {
        logger.info("reload admin and cluster client");
        adminClient.close();
        adminClient.shutdown();
        adminClient = findAdminClient();
        clusterClient.close();
        clusterClient.shutdown();
        //set reload to true
        reload = true;
        clusterClient = getDefaultStore(timeout);
        //set it back to false
        reload = false;
        return clusterClient;
    }

    private StoreConfig findStoreConfig(String name) {
        for (StoreConfig each : storeConfigList ) {
            if ( each.getStore().equals( name) )
                return each;
        }
        throw new StoreException("can not find store "+name);
    }

    private Request adminRequest(String key) {
        StoreParas paras = new StoreParas(OpType.Get, Key.createKey(key), null);
        return new Request(new Header(ADMIN_STORE, 0, (byte) 1) , embeddedSerializer.toBytes( paras), Request.RequestType.Cluster );
    }

    @JmxOperation(description = "close")
    public void close() {
        logger.info("close admin client");
        adminClient.close();
        adminClient.shutdown();
        clusterClient.close();
        clusterClient.shutdown();
    }

}
