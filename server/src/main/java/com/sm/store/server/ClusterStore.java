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

import com.sm.storage.Serializer;
import com.sm.store.cluster.ClusterNodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.store.cachestore.BlockSize;

import java.util.ArrayList;
import java.util.List;

@JmxManaged(description = "Cluster store")
public class ClusterStore extends RemoteScanStore {

    protected static final Log logger = LogFactory.getLog(ClusterStore.class);
    // list of ClusterNodes
    protected ClusterNodes clusterNodes;

    public ClusterStore(String filename, Serializer serializer, int mode, ClusterNodes clusterNodes) {
        super(filename, serializer, mode);
        setCluster(clusterNodes);
    }

    public ClusterStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode,
                        ClusterNodes clusterNodes) {
        super(filename, serializer, path, delay, blockSize, mode);
        setCluster(clusterNodes);
    }

    public ClusterStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode,
                       boolean isSorted, ClusterNodes clusterNodes) {
        super(filename, serializer, path, delay, blockSize, mode, isSorted);
        setCluster(clusterNodes);
    }

    public ClusterStore(String filename, Serializer serializer, boolean delay, int mode, ClusterNodes clusterNodes) {
        super(filename, serializer, delay, mode);
        setCluster(clusterNodes);
    }



    private void setCluster(ClusterNodes clusterNodes) {
        this.clusterNodes = clusterNodes;

    }


    public List<String> getReplicaCluster(ClusterNodes clusterNodes, String url){
        List<String> toReturn = new ArrayList<String>( clusterNodes.getServerArray().length -1);
        for (String each : clusterNodes.getServerArray() ) {
            if ( ! each.equals( url))  {
                String[] hostPort = each.split(":");
                toReturn.add(hostPort[0]+":"+( Integer.valueOf(hostPort[1])+1) );
            }
        }
        return toReturn;
    }

    public ClusterNodes getClusterNodes() {
        return clusterNodes;
    }


    /**
     *
     * @param logPath - path for WAG
     */
    public void startReplica(String logPath, String localUrl) {
        if ( logPath != null ) {
            //logPath = path+"/"+logPath;
            logger.info("Start write log thread and path "+logPath);
            List<String> urls = getReplicaCluster(clusterNodes, localUrl);
            if (urls != null && urls.size() > 0 ) {
                super.startReplica(logPath, urls );
            }
            else
                logger.warn("replica.client.url property is null or 0, will not start client");
        }
    }

    public void startWriteThread(int no) {
        store.startWriteThread( no);
    }
}
