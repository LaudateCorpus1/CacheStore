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

package com.sm.store.server.netty;

import com.sm.store.BuildRemoteConfig;
import com.sm.utils.JmxService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sm.store.Utils.getPath;
import static com.sm.transport.Utils.getOpts;

public class RemoteScan4ReplicaServer {
   private static final Log logger = LogFactory.getLog(RemoteScan4ReplicaServer.class);

    public static void main(String[] args) {
        String[] opts = new String[] {"-configPath", "-start" };
        String[] defaults = new String[] {"./config", "true"};
        String[] paras = getOpts( args, opts, defaults);

        String configPath = paras[0];
        if (  configPath.length() == 0 ) {
            logger.error("missing config path");
            throw new RuntimeException("missing -configPath");
        }
        //make sure path is in proper format
        configPath = getPath( configPath);
        boolean start = Boolean.valueOf(paras[1]);
        logger.info("read stores.xml from "+configPath);
        BuildRemoteConfig bsc = new BuildRemoteConfig(configPath+"/stores.xml");
        Scan4RemoteServer cs = new Scan4RemoteServer( bsc.build());
        logger.info("hookup jvm shutdown process");
        cs.hookShutdown();
        if (start ) {
            logger.info("server is starting "+cs.toString() );
            cs.startServer();
        }
        else
            logger.warn("server is staged and wait to be started");
        List list = new ArrayList();
        //add jmx metric
        List<String> stores = cs.getAllStoreNames();
        for (String store : stores ) {
            list.add(cs.getRemoteStore(store));
        }
        // add additional name into list for Scan4RemoteServer
        list.add(cs);
        stores.add("Scan4RemoteServer");
        JmxService jms = new JmxService(list, stores);
    }

}
