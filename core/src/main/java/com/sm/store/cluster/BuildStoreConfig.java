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

package com.sm.store.cluster;

import com.sm.store.StoreConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import voldemort.store.cachestore.BlockSize;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class BuildStoreConfig {
    private String fileName;
    private XMLConfiguration config;
    public static final String STORE = "store";

    public BuildStoreConfig(String fileName) {
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

    public List<StoreConfig> build() {
        List<HierarchicalConfiguration> list = (List<HierarchicalConfiguration>) config.configurationsAt(STORE);
        if (list == null || list.size() == 0 ) throw new RuntimeException("list is null or freq ==0 for node "+STORE);
        List<StoreConfig> toReturn = new CopyOnWriteArrayList<StoreConfig> ();
        for( HierarchicalConfiguration each : list) {
            toReturn.add( buildStoreConfig( each));
        }
        return toReturn;
    }

    private StoreConfig buildStoreConfig(HierarchicalConfiguration configuration) {
        StringBuilder sb = new StringBuilder();
        String name = configuration.getString("name");
        if ( name == null ) sb.append("store name is not defined ");
        String path = configuration.getString("path","data");
        String filename = configuration.getString("filename", name);
        boolean delay = configuration.getBoolean("delayWrite", true);
        int mode = configuration.getInt("mode", 0);
        int delayThread = configuration.getInt("delayThread", 2);
        String serializeClass = configuration.getString("serializer", "com.sm.localstore.impl.HessianSerializer");
        String blockSizeClass = configuration.getString("blockSize");
        BlockSize blockSize = null;
        if ( blockSizeClass != null ) {
            try {
                blockSize = (BlockSize) Class.forName(blockSizeClass).newInstance();
            } catch (Exception ex) {
                sb.append(("unable to load "+blockSizeClass+" "+ex.getMessage()));
            }
        }
        boolean useCache = configuration.getBoolean("useCache", false);
        long maxCache = configuration.getLong("maxCache", 1000 * 1000 * 1000L);
        String logPath = configuration.getString("logPath", "log");
        String replicaUrl = configuration.getString("replicaUrl");
        String purgeClass = configuration.getString("purgeClass");
        if ( sb.length() > 0 ) {
            throw new RuntimeException("error on buildStoreConfig "+sb.toString());
        }
        else {
            StoreConfig storeConfig = new StoreConfig(name, path, 10, null );
            storeConfig.setDelay( delay);
            storeConfig.setDelayThread(delayThread);
            storeConfig.setLogPath( logPath);
            storeConfig.setBlockSize( blockSize);
            storeConfig.setPurgeClass(purgeClass);
//            return new StoreConfig(name, path, filename, delay, mode, writeThread, useCache, maxCache, maxCache,
//                    logPath, serializeClass, blockSize, replicaUrl);
            return storeConfig;
        }
    }

}
