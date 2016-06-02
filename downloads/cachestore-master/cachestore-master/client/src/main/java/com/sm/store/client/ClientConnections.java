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

package com.sm.store.client;

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.storage.Serializer;
import com.sm.store.Hash;
import com.sm.store.cluster.ClusterNodes;
import com.sm.store.cluster.Connection;
import com.sm.transport.Client;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxGetter;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxSetter;
import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import static com.sm.store.client.Utils.buildClusterConnection;
import static com.sm.store.client.Utils.getConnectionListStr;
import static com.sm.store.cluster.Utils.buildPartitionMap;

@JmxManaged(description = "ClientConnections")
public class ClientConnections {
    private static final Log logger = LogFactory.getLog(ClientConnections.class);

    private List<ClusterNodes> clusterNodesList;
    private ConcurrentMap<Integer, Short> partitionMap;
    private ConcurrentMap<Short, List<Connection> > clusterConnections;
    private int totalPartition;
    protected AtomicLong seqno = new AtomicLong(1);
    private BlockingQueue<Connection> reconnectQueue ;
    private Thread connectThread;
    private Serializer serializer;
    private long timeout = 2000L;
    private volatile boolean stop = false;
    protected volatile boolean nio = true;
    protected TCPClientFactory.ClientType clientType ;
    protected Header.SerializableType type = Header.SerializableType.Default;

   public ClientConnections(List<ClusterNodes> clusterNodesList, Serializer serializer) {
        this( clusterNodesList, serializer, TCPClientFactory.ClientType.Grizzly );
    }

    public ClientConnections(List<ClusterNodes> clusterNodesList, Serializer serializer,
                             TCPClientFactory.ClientType clientType) {
        this.serializer = serializer;
        this.clientType =clientType;
        init(clusterNodesList);
    }

    private void init(List<ClusterNodes> clusterNodesList) {
        this.clusterNodesList = clusterNodesList;
        partitionMap = buildPartitionMap(clusterNodesList);
        clusterConnections = buildClusterConnection(clusterNodesList, clientType);
        totalPartition = partitionMap.size();
        reconnectQueue = new LinkedBlockingQueue<Connection>();
        logger.info("start reconnect thread");
        connectThread = new Thread( new ReconnectThread(reconnectQueue));
        connectThread.start();
    }

    public Header.SerializableType getType() {
        return type;
    }

    public void setType(Header.SerializableType type) {
        this.type = type;
    }

    public TCPClientFactory.ClientType getClientType() {
        return clientType;
    }

    /**
     * add hash interface if there is customize hash function
     * if not set, return key.hashCode()
      * @param key
     * @return
     */
    public Client getConnection(Key key) {
        // find out which partition key belongs to
        //int part =  hash(key);
        //key.hashCode() % totalPartition;
        Short cluster = getPartition( key);
        if ( cluster != null ) {
            List<Connection> connections = clusterConnections.get(cluster);
            if ( connections != null ) {
                return findConnection( hash(key), connections);
            }
            else
                throw new ConnectionException("missing key clusterConnections hash "+key.toString() );
        }
        else  {
            throw new ConnectionException("missing key partitionMap hash "+key.toString() );
        }
    }

    public Client getConnection(short cluster, int index) {
        List<Connection> connections = clusterConnections.get(cluster);
        if ( connections != null ) {
            return findConnection( index, connections);
        }
        else
            throw new ConnectionException("can not find clusterConnections cluster "+cluster+" index "+index );
    }

//    protected TCPClient getOioConnection(int hash,List<Connection> connections ) {
//        int size = connections.size();
//        Connection connection = connections.get(hash % size);
//        TCPClient tcpClient = TCPClient.start( connection.getHost(), connection.getPort(), new ClusterClientHandler(timeout, new HessianSerializer()), (byte) 1);
//        if ( tcpClient != null && tcpClient.isConnected())
//            return tcpClient;
//        else
//            return null;
//    }

//    public TCPClient getConnection(short cluster) {
//        List<Connection> connections = clusterConnections.get(cluster);
//        if ( connections != null )
//            return findConnection( cluster, connections);
//        else
//            throw new ConnectionException("missing key clusterConnections hash "+cluster );
//    }

    private Hash hash = null ;

    /**
     * set cutomer hash function
     * @param hash
     */
    public void setHash(Hash hash) {
        this.hash = hash;
    }

    public void setNio(boolean nio) {
        this.nio = nio;
    }

    public void setClientType(TCPClientFactory.ClientType clientType) {
        this.clientType = clientType;
    }

    @JmxGetter(name = "getConnectionList")
    public String getConnectionList() {
        return getConnectionListStr(clusterConnections);
    }

    private int hash(Key key ) {
        if ( hash == null )
            return Math.abs( key.hashCode());
        else
            return Math.abs( hash.hash( key));
    }

    public Short getPartition(Key key) {
        int part = hash(key) % partitionMap.size();
        return partitionMap.get(part);
    }

    public int getPartitionNo(Key key){
        return hash(key) % partitionMap.size();
    }

    public ConcurrentMap<Integer, Short> getPartitionMap() {
        return partitionMap;
    }

    public Pair<Short, Integer> getPartitionIndex(Key key) {
        int code = hash(key);
        int part = code % partitionMap.size();
        short cluster = partitionMap.get(part);
        int index =  code % clusterConnections.get(cluster).size();
        return new Pair<Short, Integer> (cluster, index);
    }

    /**
     * for those function which did not have keySet reference
     * It will random select one node from cluster
     * @return
     */
    public List<Pair<Short, Integer>> getOneFromAllCluster(){
        List<Pair<Short, Integer>> list = new ArrayList<Pair<Short, Integer>>();
        Random random = new Random();
        for ( short each : clusterConnections.keySet()) {
            int size = clusterConnections.get(each).size();
            int i = random.nextInt( size );
            list.add(new Pair(each, i));
        }
        return list;
    }

    public List<Pair<Pair<Short, Integer>, String>> getMapClientFromAll(){
        List<Pair<Pair<Short, Integer>, String>> list = new ArrayList<Pair<Pair<Short, Integer>, String>>();
        for ( short each : clusterConnections.keySet()) {
            int size = clusterConnections.get(each).size();
            // it is one base, not zero, examples for two nodes, 1,2 and 2,2
            for ( int j =0; j < size ; j++) {
                String part = (j+1)+","+size;
                list.add(new Pair(new Pair(each, j), part ));
            }
        }
        return list;
    }

    @JmxGetter(name="getClusterNodeStr")
    public String getClusterNodeStr() {
        return clusterNodesList.toString();
    }

    public long getTimeout() {
        return timeout;
    }

    @JmxSetter(name="timeout")
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void stop() {
        logger.info("close all connections and stop queue");
        stop = true;
        Iterator<List<Connection>> it =clusterConnections.values().iterator();
        while ( it.hasNext()) {
            List<Connection> list = it.next();
            for ( Connection con : list) {
                logger.info("close connection " + con.toString());
                con.close();
            }
        }
    }

   public void shutdown() {
       Iterator<List<Connection>> it =clusterConnections.values().iterator();
       while ( it.hasNext()) {
           List<Connection> list = it.next();
           for ( Connection con : list) {
               if ( con != null) {
                    logger.info("shutdown connection " + con.toString());
                    con.shutdown();
               }
           }
       }
   }

    public int getTotalPartition() {
        return totalPartition;
    }

    public ConcurrentMap<Short, List<Connection>> getClusterConnections() {
        return clusterConnections;
    }

    protected Client findConnection(int hashCode, List<Connection> connectionList) {
        int size = connectionList.size();
        int index = hashCode % size;
        //loop through the cluster as a ring to find the first available connection
        for (int i = 0  ; i < size ; i++ ) {
            int pos = (index + i) % size;
            try {
                Connection connection = connectionList.get(pos);
                if (connection.isConnected() ) {
                    return connection.getTcpClient();
                }
                else {
                    if ( ! connection.isDown() ) {
                        if ( connection.getTcpClient() == null ) {
                            if ( connection.connect(timeout) ) {
                                return connection.getTcpClient();
                            }
                        }
                        else {
                            if ( connection.reconnect() )
                                return connection.getTcpClient();
                        }
                    }
                    else {
                        connection.putIfAbsent( reconnectQueue);
                    }
                }

            } catch (Exception iex) {
                logger.error("fail to connect "+ connectionList.get(pos)+" "+iex.getMessage(), iex);
            }
        }
        // could find or create connection
        throw new ConnectionException("no connection among "+connectionList.toString() );
    }

    public Request createRequest(byte[] payload, String store) {
         return createRequest(payload, store, Request.RequestType.Cluster);
    }

    public Request createRequest(byte[] payload, String store, Request.RequestType type ) {
        Request request = new Request(new Header(store, seqno.getAndIncrement(), (byte) 0, 0) , payload, type);
        //logger.info("header "+request.getHeader().toString()+" type "+type.toString() );
        return request;
    }


    public void connectAll() {
        logger.info("create all connections");
        Collection< List<Connection>> collection = clusterConnections.values();
        for ( List<Connection> each : collection ) {
            for ( int i = 0 ; i < each.size() ; i++) {
                if ( each.get(i).connect(timeout))
                    logger.info("connect to "+each.toString());
            }
        }
    }

    class ReconnectThread implements Runnable {
        BlockingQueue<Connection> reconnectQueue;

        ReconnectThread(BlockingQueue<Connection> reconnectQueue) {
            this.reconnectQueue = reconnectQueue;
        }


        public void run() {
            long counter = 0;
            while ( true) {
                if ( stop ) {
                    logger.info("stop ReconnectThread");
                    break;
                }
                try {
                    if ( reconnectQueue.size() > 0) logger.info("queue size "+reconnectQueue.size());
                    Iterator<Connection> it =reconnectQueue.iterator();
                    while ( it.hasNext() ) {
                        try {
                            Connection connection = it.next();
                            logger.info("reconnectQueue try to reconnect "+connection.toString() );
                            if (connection.reconnect( timeout) ) {
                                logger.info("reconnectQueue tsuccessful connect to "+connection.toString());
                                connection.remoteFromQueue( reconnectQueue);
                            }
                        } catch (Exception ex) {
                            // swallow exception
                        }
                    }
                    if (reconnectQueue.size() >= 0 ) {
                        long sec = (counter % 4)+ 1;
                        counter++;
                        Thread.sleep(100L*sec);
                    }
                    else
                        Thread.sleep(100L*5);
                } catch (Throwable th) {
                    logger.warn( th.getMessage(), th);
                }
            }
        }

    }

}
