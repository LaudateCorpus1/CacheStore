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

package com.sm.replica;

import com.sm.replica.server.ReplicaServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.impl.CacheStore;

import java.util.HashMap;

import static com.sm.transport.Utils.getOpts;

public class TestServer {
private static final Log logger = LogFactory.getLog(TestServer.class);

    public static void main(String[] args) {
        String[] opts = new String[] {"-store","-path", "-port", "-mode"};
        String[] defaults = new String[] {"store","./data", "6900", "0" };
        String[] paras = getOpts( args, opts, defaults);
        String store = paras[0];
        int port = Integer.valueOf( paras[2]);
        String path = paras[1];
        int mode = Integer.valueOf(paras[3]);
        CacheStore cacheStore = new CacheStore(path, null, 0, store, false, mode);
        HashMap<String, CacheStore> storesMap = new HashMap<String, CacheStore>();
        storesMap.put(store, cacheStore);
        logger.info("start server at "+port);
        ReplicaServer server = new ReplicaServer( port, storesMap);

  }
}
