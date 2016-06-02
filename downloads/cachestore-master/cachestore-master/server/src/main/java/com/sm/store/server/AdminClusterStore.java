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

import com.sm.storage.Serializer;
import com.sm.store.StoreConfig;
import com.sm.store.cluster.ClusterNodes;
import com.sm.store.cluster.ClusterServerConfig;
import voldemort.store.cachestore.BlockSize;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.voldeimpl.BlockValue;

import java.util.List;

import static com.sm.store.cluster.Utils.CLUSTER_KEY;
import static com.sm.store.cluster.Utils.STORE_CONFIG_KEY;

public class AdminClusterStore extends ClusterStore {
    private List<ClusterNodes> clusterNodesList;
    private List<StoreConfig> storeConfigList;
    private Key cluster;
    private Key storeConfig;
    private ClusterServerConfig serverConfig;

    public AdminClusterStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode, ClusterNodes clusterNodes) {
        super(filename, serializer, path, delay, blockSize, mode, clusterNodes);
    }


    public List<ClusterNodes> getClusterNodesList() {
        return clusterNodesList;
    }

    public ClusterServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ClusterServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        setList( serverConfig.getClusterNodesList(), serverConfig.getStoreConfigList());
    }

    public void setList(List<ClusterNodes> clusterNodesList, List<StoreConfig> storeConfigList) {
        this.clusterNodesList = clusterNodesList;
        this.storeConfigList = storeConfigList;
        initPut();
    }

    protected void initPut() {
        this.cluster = Key.createKey(CLUSTER_KEY);
        this.storeConfig = Key.createKey(STORE_CONFIG_KEY);
        put(cluster, createValue(serializer.toBytes(clusterNodesList)));
        put(storeConfig, createValue(serializer.toBytes(storeConfigList)));
    }

    private Value<byte[]> createValue(byte[] bytes) {
        return new BlockValue<byte[]>(bytes, 0, (short) 0);
    }

    public List<StoreConfig> getStoreConfigList() {
        return storeConfigList;
    }


    public Key getCluster() {
        return cluster;
    }

    public void setCluster(Key cluster) {
        this.cluster = cluster;
    }

    public Key getStoreConfig() {
        return storeConfig;
    }

    public void setStoreConfig(Key storeConfig) {
        this.storeConfig = storeConfig;
    }

    public void putObject(Key key, Object object) {
        put(key, createValue( serializer.toBytes( object)));
    }

    public Object getObject(Key key) {
        return  serializer.toObject( (byte[]) get(key).getData() );
    }

}
