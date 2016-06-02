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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BuildClusterNodes {
    private String fileName;
    private XMLConfiguration config;
    public static final String CLUSTERS = "cluster";

    public BuildClusterNodes(String fileName) {
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

    public List<ClusterNodes> build() {
        List<HierarchicalConfiguration> list = (List<HierarchicalConfiguration>) config.configurationsAt(CLUSTERS);
        if (list == null || list.size() == 0 ) throw new RuntimeException("list is null or freq ==0 for node "+CLUSTERS);
        List<ClusterNodes> toReturn = new CopyOnWriteArrayList<ClusterNodes>();
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<Short> sets = new HashSet<Short>();
        int error = 0;
        for (HierarchicalConfiguration each : list) {
            ClusterNodes clusterNode = buildClusterNodes(each);
            if (sets.contains( clusterNode.getId())) {
                error++;
                stringBuilder.append(" cluster no "+ clusterNode.getId());
            }
            else
                sets.add( clusterNode.getId() );
            toReturn.add( clusterNode);
        }
        if ( error > 0 ) {
            throw new RuntimeException(error+" duplicate clusters , "+stringBuilder.toString()+" in clusters.xml" );
        }
        else
            return toReturn;
    }

    HashSet<Integer> sets = new HashSet<Integer>();
    HashSet<String> serverSets = new HashSet<String>();
    private ClusterNodes buildClusterNodes(HierarchicalConfiguration configuration) {
        short no = configuration.getShort("no", (short) -1);
        //int port = configuration.getInt("port", DEFAULT_TCP_PORT);
        List<String> serverList = configuration.getList("servers");
        List<String> partitionList = configuration.getList("partitions");
        StringBuilder sb = new StringBuilder();

        if ( no == -1 ) sb.append("no is not defined, ");
        if (serverList == null || serverList.size() == 0) sb.append(" servers is not defined,");
        else {
            int error = 0;
            for ( String each : serverList) {
                if ( serverSets.contains( each)) {
                    error ++;
                    sb.append(" "+each);
                }
                else
                    serverSets.add(each);
            }
            if ( error > 0) throw new RuntimeException(error+" duplicate server url"+sb.toString());
        }
        if ( partitionList == null || partitionList.size() == 0) sb.append(" partitionList is not defined");
        if ( sb.length() > 0 ) throw new RuntimeException("error on buildClusterNodes "+sb.toString());
        else {
            int error = 0;
            String[] servers = new String[serverList.size()];
            int[] partitions = new int[ partitionList.size()];
            for ( int i =0 ; i < partitionList.size() ; i++ ) {
                partitions[i]= Integer.valueOf(partitionList.get(i));
                if (sets.contains( partitions[i] )) {
                    error++;
                    sb.append(" partitions # "+partitions[i]);
                }
                else
                    sets.add( partitions[i]);
            }
            if ( error > 0 ) {
                throw new RuntimeException(error+" duplicate"+sb.toString() );
            }
            return new ClusterNodes(no, serverList.toArray(servers), partitions );
        }
    }


}
