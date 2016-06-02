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

package com.sm.store.cluster;


import com.sm.store.StoreConfig;

import java.io.Serializable;
import java.util.List;

public class ClusterServerConfig implements Serializable {

    private List<ClusterNodes> clusterNodesList;
    private List<StoreConfig> storeConfigList;
    private int clusterNo;
    private String dataPath;
    private String configPath;
    private int maxQueue;
    private int maxThread;
    private int port;
    private int replicaPort;
    private String host;
    private int freq;

    public ClusterServerConfig(List<ClusterNodes> clusterNodesList, List<StoreConfig> storeConfigList, int clusterNo, String dataPath, String configPath,
                               int maxQueue, int maxThread, int port, int replicaPort, String host, int freq) {
        this.clusterNodesList = clusterNodesList;
        this.storeConfigList = storeConfigList;
        this.clusterNo = clusterNo;
        this.dataPath = dataPath;
        this.configPath = configPath;
        this.port = port;
        this.replicaPort = replicaPort;
        this.host = host;
        this.freq = freq;
        if ( maxThread == 0)
            this.maxThread = Runtime.getRuntime().availableProcessors()*20;
        else
            this.maxThread = maxThread;
        if ( maxQueue == 0)
            this.maxQueue = this.maxThread * 500;
        else
            this.maxQueue = maxQueue;
    }

    public ClusterServerConfig(List<ClusterNodes> clusterNodesList, List<StoreConfig> storeConfigList, int clusterNo,
                               String dataPath, String configPath, int port, int replicaPort, String host) {
        this( clusterNodesList, storeConfigList, clusterNo, dataPath, configPath, port, replicaPort, host, 0, 0);
    }

    public ClusterServerConfig(List<ClusterNodes> clusterNodesList, List<StoreConfig> storeConfigList, int clusterNo,
                               String dataPath, String configPath, int port, int replicaPort,String host, int maxQueue, int maxThread) {
        this(clusterNodesList, storeConfigList, clusterNo, dataPath, configPath, maxQueue, maxThread, port, replicaPort, host, 1);
    }

    public List<ClusterNodes> getClusterNodesList() {
        return clusterNodesList;
    }



    public ClusterNodes findClusterNodes(int clusterNo){
        for( ClusterNodes each : clusterNodesList) {
            if ( each.getId() == (short) clusterNo )
                return each;
        }
        throw new RuntimeException("Can not find cluster no"+clusterNo);
    }

    public List<StoreConfig> getStoreConfigList() {
        return storeConfigList;
    }

    public int getClusterNo() {
        return clusterNo;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public int getMaxQueue() {
        return maxQueue;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public int getPort() {
        return port;
    }

    public int getReplicaPort() {
        return replicaPort;
    }

    public String getHost() {
        return host;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    @Override
    public String toString() {
        return "ClusterServerConfig{" +
                "clusterNo=" + clusterNo +
                ", dataPath='" + dataPath + '\'' +
                ", configPath='" + configPath + '\'' +
                ", maxQueue=" + maxQueue +
                ", maxThread=" + maxThread +
                ", port=" + port +
                ", replicaPort=" + replicaPort +
                '}';
    }
}
