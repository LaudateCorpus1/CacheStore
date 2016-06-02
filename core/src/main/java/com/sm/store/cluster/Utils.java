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
 */package com.sm.store.cluster;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.transport.netty.TCPClient;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {
    protected static Log logger = LogFactory.getLog(Utils.class);
    public static final String ADMIN_FILENAME =".metaStore";
    public static final String ADMIN_PATH ="admin";
    public static final String ADMIN_STORE ="__admin__";
    public static final int DEFAULT_TCP_PORT = 7444;
    public static final String CLUSTER_KEY =  "_clusterNode_";
    public static final String STORE_CONFIG_KEY = "_storeConfig_";



    public static ConcurrentMap<Integer, Short> buildPartitionMap(List<ClusterNodes> nodes) {
        ConcurrentMap<Integer, Short> map = new ConcurrentHashMap<Integer, Short> (nodes.size());
        for (ClusterNodes node : nodes ) {
            for ( int i =0; i < node.getPartitionArray().length; i++ ) {
                map.put( node.getPartitionArray()[i], node.getId());
            }
        }
        return map;
    }





    public static List<String> convertList(String[] strs) {
        List<String> list = new CopyOnWriteArrayList<String>();
        for ( String str : strs) {
            list.add( str);
        }
        return list;
    }


    public static List<TCPClient> buildEmptyList(int size) {
        List<TCPClient> list = new CopyOnWriteArrayList<TCPClient>();
        for ( int i= 1 ; i < size; i++)
            list.add(null);
        return list;
    }

    public static ConcurrentMap<Short, String[]> buildServerMap(List<ClusterNodes> nodes){
        ConcurrentMap<Short, String[]> map = new ConcurrentHashMap<Short, String[]> (nodes.size());
        for (ClusterNodes node : nodes ) {
             map.put( node.getId(), node.getServerArray() );
        }
        return map;
    }

    public static String validateId(List<ClusterNodes> nodes) {
        StringBuilder sb = new StringBuilder();
        ConcurrentMap<Integer, Short> map = new ConcurrentHashMap<Integer, Short> (nodes.size());
        for (ClusterNodes node : nodes ) {
            for ( int i =0; i < node.getPartitionArray().length; i++ ) {
                if( map.putIfAbsent( node.getPartitionArray()[i], node.getId()) != null )
                    sb.append(" id ="+node.getId());
            }
        }
        return sb.toString();
    }

    public static String validateServer(List<ClusterNodes> nodes) {
        StringBuilder sb = new StringBuilder();
        ConcurrentMap<String, Short> map = new ConcurrentHashMap<String, Short> (nodes.size());
        for (ClusterNodes node : nodes ) {
            for ( int i =0; i < node.getServerArray().length; i++ ) {
                if( map.putIfAbsent(  node.getServerArray()[i], node.getId() ) != null )
                    sb.append(" id ="+node.getId());
            }
        }
        return sb.toString();
    }

    public static String validateCluster(List<ClusterNodes> nodes){
        return validateId(nodes)+ validateServer( nodes);
    }

    public static String list2String(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for ( String each : list ) {
            if (sb.length() > 0 )
                sb.append(" "+each);
            else
                sb.append( each);
        }
        return sb.toString();
    }


    public static String getLocalHost() {

        try {
            return Inet4Address.getLocalHost().getHostName();
        } catch ( IOException iex) {
            logger.error( iex.getMessage());
            throw new RuntimeException(iex);
        }
    }


    public static Serializer buildSerializer(String serializerClass) {
        Serializer serializer = null;
        if ( serializerClass != null ) {
            try {
                serializer = (Serializer) Class.forName(serializerClass).newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("unable to load "+serializerClass+" "+ex.getMessage());
            }
        }
        else
            serializer = new HessianSerializer();

        return serializer;
    }

    public static boolean isLog(long seqNo, int freq) {
        if ( freq <= 1) return true;
        else{
            if ( seqNo % freq == 0) return true;
            else return false;
        }
    }

    public static NodeConfig getNodeConfig(String filename) {
        try {
            PropertiesConfiguration properties = new PropertiesConfiguration( filename);
            String host = properties.getString("host","");
            int port = properties.getInt("port", 0);
            return new NodeConfig(host, port );

        } catch (ConfigurationException ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }

    public static short findClusterNo(String url, List<ClusterNodes> clusterNodesList) {
        for (ClusterNodes clusterNode : clusterNodesList) {
            for ( String each  : clusterNode.getServerArray()) {
                if ( each.equals(url )) {
                    return clusterNode.getId();
                }
            }
        }
        throw new RuntimeException("Can not find "+url+" in clusterServerConfig ");
    }

}
