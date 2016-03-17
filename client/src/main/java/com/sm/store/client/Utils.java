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

package com.sm.store.client;

import com.sm.store.cluster.ClusterNodes;
import com.sm.store.cluster.Connection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {

    public static ConcurrentMap<Short, List<Connection>> buildClusterConnection( List<ClusterNodes> clusterNodesList,
            TCPClientFactory.ClientType clientType) {
        ConcurrentMap<Short, List<Connection>> map = new ConcurrentHashMap<Short, List<Connection>>();
        for (ClusterNodes node : clusterNodesList ) {
            map.put( node.getId(), buildConnections(node.getServerArray(), clientType) );
        }
        return map;
    }



    public static List<Connection> buildConnections(String[] urls, TCPClientFactory.ClientType clientType) {
        List<Connection> list = new CopyOnWriteArrayList<Connection>();
        for (String each : urls ) {
            String[] strs = each.split(":");
            list.add( new Connection(strs[0], Integer.valueOf(strs[1]), clientType) );
        }
        return list;
    }

    public static String getConnectionListStr(Map<Short, List<Connection>> clusterConnections ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Short> it = clusterConnections.keySet().iterator();
        while ( it.hasNext()) {
            Short key = it.next();
            List<Connection> collection = clusterConnections.get( key);
            sb.append(" cluster "+ key+" ");
            for ( Connection each : collection ) {
                sb.append(each.toString());
            }
        }
        return sb.toString();
    }

}
