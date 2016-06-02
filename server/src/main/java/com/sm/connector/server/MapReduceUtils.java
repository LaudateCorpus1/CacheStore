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

package com.sm.connector.server;

import com.sm.store.BuildRemoteConfig;
import com.sm.store.RemoteConfig;
import com.sm.store.StoreConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mhsieh on 3/18/16.
 */
public class MapReduceUtils {
    public final static String Config = "configPath";
    private static MapReduceUtils instance = new MapReduceUtils();
    private volatile Map<String, ServerStore> map = null;
    private static Lock lock = new ReentrantLock();
    private static String configPath;
    private RemoteConfig bsc ;

    private MapReduceUtils() {
        configPath = System.getProperty(Config,"./config");
    }


    public static MapReduceUtils getInstance() {
        return instance;
    }

    public Map<String, ServerStore> createStoreMap(String filename) {
        if ( map == null) {
            synchronized ( MapReduceUtils.class ) {
                if ( map == null ) {
                    map = new HashMap<String, ServerStore>();
                    bsc = new BuildRemoteConfig(configPath + "/" + filename).build();
                    for (StoreConfig each : bsc.getConfigList()) {
                        ServerStore serverStore = new ServerStore(each.getDataPath() + "/" + each.getStore());
                        map.put(each.getStore(), serverStore);
                    }
                }
            }
        }
        return map;
    }

   public StoreConfig getColumnMap(String store) {
       for ( StoreConfig each :bsc.getConfigList()) {
           if ( each.getStore().equals(store)) {
                return each;
           }
       }
       throw new RuntimeException(store+" did not exist in stores.xml");
   }

}
