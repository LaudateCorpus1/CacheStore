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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.StoreConfig;
import com.sm.store.cluster.ClusterNodes;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheStore;
import voldemort.store.cachestore.voldeimpl.BlockValue;

import java.io.Serializable;
import java.util.List;
import static com.sm.store.cluster.Utils.*;

public class AdminStore implements Serializable {
    private List<ClusterNodes> clusterNodesList;
    private List<StoreConfig> storeConfigList;
    private Key cluster;
    private Key storeConfig;
    private CacheStore cacheStore;
    private Serializer serializer = new HessianSerializer();


    public AdminStore(List<ClusterNodes> clusterNodesList, List<StoreConfig> storeConfigList) {
        this.clusterNodesList = clusterNodesList;
        this.storeConfigList = storeConfigList;
        validateNull();
        init();
        initPut();

    }

    private void init() {
        this.cluster = Key.createKey(CLUSTER_KEY);
        this.storeConfig = Key.createKey(STORE_CONFIG_KEY);
        this.cacheStore = new CacheStore(ADMIN_PATH, null, 0, ADMIN_FILENAME);
    }


    private void initPut() {
        cacheStore.put(cluster, createValue(serializer.toBytes( clusterNodesList)) );
        cacheStore.put(storeConfig, createValue(serializer.toBytes(storeConfigList)) );
    }

    private void validateNull() {
        if (clusterNodesList == null || clusterNodesList.size() == 0  || storeConfigList == null ||
                storeConfigList.size() == 0)
            throw new StoreException("invalidate clusterNodesList or storeConfigList");
    }

    private Value<byte[]> createValue(byte[] bytes) {
        return new BlockValue<byte[]>(bytes, 0, (short) 0);
    }

    public List<ClusterNodes> getClusterNodesList() {
        return (List<ClusterNodes>) serializer.toObject( (byte[])cacheStore.get(cluster).getData() );
    }

    public List<StoreConfig> getStoreConfigList() {
        return (List<StoreConfig>) serializer.toObject( (byte[]) cacheStore.get(storeConfig).getData() );
    }

    public void putClusterNodesList(List<ClusterNodes> clusterNodesList) {
        cacheStore.put(cluster, createValue(serializer.toBytes( clusterNodesList)) );
    }

    public void putStoreConfigList(List<StoreConfig> storeConfigList) {
        cacheStore.put(storeConfig, createValue(serializer.toBytes(storeConfigList)) );
    }

    public void put(Key key, Value value) {
        cacheStore.put(key, value);
    }

    public Value get(Key key) {
        return cacheStore.get( key);
    }

    public void putObject(Key key, Object object) {
        cacheStore.put(key, createValue( serializer.toBytes( object)));
    }

    public Object getObject(Key key) {
        return serializer.toObject( (byte[]) cacheStore.get(key).getData() );
    }

    public CacheStore getCacheStore() {
        return cacheStore;
    }
}