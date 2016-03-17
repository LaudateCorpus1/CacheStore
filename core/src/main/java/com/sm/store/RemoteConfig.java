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

package com.sm.store;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;

import java.io.Serializable;
import java.util.List;

public class RemoteConfig implements Serializable {

    private int maxQueue;
    private int maxThread;
    private int port;
    private String serializeClassName;
    private Serializer serializer;
    private List<StoreConfig > configList;
    //server handler of message received log frequency
    private int replicaPort;
    private int freq;
    private boolean useNio ;

    public RemoteConfig(int port, List<StoreConfig> configList) {
        this(Runtime.getRuntime().availableProcessors()* 100, Runtime.getRuntime().availableProcessors(), port, null, configList);
    }

    public RemoteConfig(int maxQueue, int maxThread, int port, String serializeClassName,  List<StoreConfig> configList) {
        this(maxQueue, maxThread, port, serializeClassName, configList, 0);
    }

    public RemoteConfig(int maxQueue, int maxThread, int port, String serializeClassName,  List<StoreConfig> configList, int replicaPort) {
        this(maxQueue, maxThread, port, serializeClassName, configList, replicaPort, 10);
    }

    public RemoteConfig(int maxQueue, int maxThread, int port, String serializeClassName,  List<StoreConfig> configList, int replicaPort, int freq) {
        this.maxQueue = maxQueue;
        this.maxThread = maxThread;
        this.port = port;
        this.serializeClassName = serializeClassName;
        this.serializer = createSerializer();
        this.configList = configList;
        this.replicaPort = replicaPort;
        this.freq = freq;
    }

    public Serializer createSerializer() {
        if (serializeClassName == null || serializeClassName.length() ==0)
            return new HessianSerializer();
        else {
            try {
                Serializer instance =  (Serializer) Class.forName(serializeClassName).newInstance();
                return instance;
            } catch ( Exception ex) {
                throw new RuntimeException( ex.getMessage(), ex);
            }
        }
    }

    public int getReplicaPort() {
        return replicaPort;
    }

    public void setReplicaPort(int replicaPort) {
        this.replicaPort = replicaPort;
    }

    public String getSerializeClassName() {
        return serializeClassName;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public int getMaxQueue() {
        return maxQueue;
    }

    public void setMaxQueue(int maxQueue) {
        this.maxQueue = maxQueue;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<StoreConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<StoreConfig> configList) {
        this.configList = configList;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public boolean isUseNio() {
        return useNio;
    }

    public void setUseNio(boolean useNio) {
        this.useNio = useNio;
    }
}
