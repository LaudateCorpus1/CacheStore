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
import com.sm.store.BuildRemoteConfig;
import com.sm.store.cluster.BuildClusterNodes;
import com.sm.store.cluster.ClusterNodes;
import com.sm.store.cluster.ClusterServerConfig;
import com.sm.store.cluster.NodeConfig;
import com.sm.utils.JmxService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sm.store.cluster.Utils.*;
import static com.sm.transport.Utils.getOpts;

public class ClusterServer {
    private static final Log logger = LogFactory.getLog(ClusterServer.class);

    public static void main(String[] args) {
        String[] opts = new String[] {"-host","-configPath","-dataPath", "-port", "replicaPort", "-start", "-freq"};
        String[] defaults = new String[] {"","","", "0", "0" ,"true", "0"};
        String[] paras = getOpts( args, opts, defaults);

        String configPath = paras[1];
        if (  configPath.length() == 0 ) {
            logger.error("missing config path");
            throw new RuntimeException("missing -configPath");
        }
        //set configPath to system properties
        System.setProperty("configPath", configPath);
        NodeConfig nodeConfig = getNodeConfig( configPath+"/node.properties");
        //int clusterNo = Integer.valueOf( paras[0]);
        String dataPath = paras[2];
        if ( dataPath.length() == 0 ) {
            dataPath = "./data";
        }
        int port = Integer.valueOf( paras[3]);
        int replicaPort = Integer.valueOf(paras[4]);
        //get from command line, if not form nodeConfig
        if ( port == 0 ) port = nodeConfig.getPort();
        if ( port == 0 ) {
            throw new RuntimeException("port is 0");
        }
        else {
            if (replicaPort == 0 )
                replicaPort = port + 1;
        }
        boolean start = Boolean.valueOf(paras[5]);
        String host = paras[0];
        //get from command line, if not form nodeConfig or from getHost
        if ( host.length() == 0 ) host = nodeConfig.getHost();
        if (host.length() == 0 ) host = getLocalHost();
        int freq = Integer.valueOf(paras[6]);
        logger.info("read clusterNode and storeConfig from "+configPath);
        BuildClusterNodes bcn = new BuildClusterNodes(configPath+"/clusters.xml");
        //BuildStoreConfig bsc = new BuildStoreConfig(configPath+"/stores.xml");
        BuildRemoteConfig brc = new BuildRemoteConfig(configPath+"/stores.xml");
        List<ClusterNodes> clusterNodesList = bcn.build();
        short clusterNo = findClusterNo(host+":"+port, clusterNodesList);
        logger.info("create cluster server config for cluster " + clusterNo + " host " + host + " port " + port + " replica port " + replicaPort);
        ClusterServerConfig serverConfig = new ClusterServerConfig(clusterNodesList, brc.build().getConfigList(), clusterNo, dataPath, configPath,
                port, replicaPort, host );
        //over write config from command line if freq > 0
        if ( freq > 0)
            serverConfig.setFreq( freq);
        logger.info("create cluster server");
        ClusterStoreServer cs = new ClusterStoreServer( serverConfig, new HessianSerializer() );
        // start replica server
        cs.startReplicaServer();
        logger.info("hookup jvm shutdown process");
        cs.hookShutdown();
        if (start ) {
            logger.info("server is starting "+cs.getServerConfig().toString() );
            cs.start();
        }
        else
            logger.warn("server is staged and wait to be started");

        List list = new ArrayList();
        //add jmx metric
        List<String> stores = cs.getAllStoreNames();
        for (String store : stores ) {
            list.add(cs.getStore(store));
        }
        list.add(cs);
        stores.add("ClusterServer");
        JmxService jms = new JmxService(list, stores);
    }


}
