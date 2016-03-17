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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



public class BuildRemoteConfig {
    protected static final Log logger = LogFactory.getLog(BuildRemoteConfig.class);

    private String fileName;
    private XMLConfiguration config;
    private int freq;
    public static final String STORE = "store";

    public BuildRemoteConfig(String fileName) {
        this.fileName = fileName;
        init();
    }

    private void init() {
        try {
            config = new XMLConfiguration(fileName);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public RemoteConfig build() {
        int port = config.getInt("port", 7210);
        String className = config.getString("className");
        int maxQueue = config.getInt("maxQueue", Runtime.getRuntime().availableProcessors() * 100);
        int maxThread = config.getInt("maxThread", Runtime.getRuntime().availableProcessors());
        int replicaPort = config.getInt("replicaPort", 7310);
        freq = config.getInt("freq", 10);
        boolean useNio = config.getBoolean("useNio", false);
        List<HierarchicalConfiguration> list = (List<HierarchicalConfiguration>) config.configurationsAt(STORE);
        if (list == null || list.size() == 0 ) throw new RuntimeException("list is null or freq ==0 for node "+STORE);
        List<StoreConfig> toReturn = new CopyOnWriteArrayList<StoreConfig>();
        for( HierarchicalConfiguration each : list) {
            toReturn.add( buildStoreConfig( each));
        }
        RemoteConfig remoteConfig = new RemoteConfig(maxQueue, maxThread, port, className, toReturn, replicaPort, freq);
        remoteConfig.setUseNio( useNio);
        return remoteConfig;

    }

    private StoreConfig buildStoreConfig(HierarchicalConfiguration configuration) {
        StringBuilder sb = new StringBuilder();
        String name = configuration.getString("name");
        if ( name == null ) sb.append("store name is not defined ");
        String path = configuration.getString("path","data");
        String filename = configuration.getString("filename", name);
        boolean delay = configuration.getBoolean("delay", true);
        int mode = configuration.getInt("mode", 0);
        int freq = configuration.getInt("freq", this.freq == 0 ? 1 : this.freq );
        int batchSize = configuration.getInt("batchSize", 10);
        String logPath = configuration.getString("logPath","log");
        List<String> replicaUrl = configuration.getList("replicaUrl");
        long replciaTimeout = configuration.getLong("replicaTimeout", 60000);
        if (replicaUrl == null ) sb.append("no replicaUrl is defined");
        boolean sorted = configuration.getBoolean("sorted", false);
        if ( sb.length() > 0 ) {
            throw new RuntimeException("error on buildStoreConfig "+sb.toString());
        }
        else {
            StoreConfig storeConfig = new StoreConfig(name, path, freq, replicaUrl, batchSize, delay, mode, sorted);
            storeConfig.setGetTriggerName( configuration.getString("getTrigger"));
            storeConfig.setPutTriggerName( configuration.getString("putTrigger"));
            storeConfig.setDeleteTriggerName( configuration.getString("deleteTrigger"));
            storeConfig.setUseMaxCache(configuration.getBoolean("useMaxCache", false));
            storeConfig.setMaxCacheMemory(configuration.getInt("maxCacheMemory", 20000));
            storeConfig.setUseLRU(configuration.getBoolean("useLRU", false));
            storeConfig.setLogPath( logPath);
            storeConfig.setReplicaTimeout( replciaTimeout);
            //add pstReplicaURl
            storeConfig.setPstReplicaUrl( configuration.getList("pstReplicaUrl"));
            return storeConfig ;
        }

    }

}
