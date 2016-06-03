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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Header;
import com.sm.message.Invoker;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import com.sm.store.*;
import com.sm.store.cluster.ClusterNodes;
import com.sm.transport.Client;
import com.sm.utils.ThreadPoolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.utils.Pair;
import voldemort.versioning.ObsoleteVersionException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.sm.store.Utils.createCacheValue;
import static com.sm.store.Utils.createRemoteValue;

@JmxManaged(description = "ClusterClient")
public class ClusterClient implements ScanPersistence {
    private static final Log logger = LogFactory.getLog(ClusterClient.class);

    protected Serializer serializer ;
    protected String store;
    protected ClientConnections clientConnections;
    //default 6 seconds for timeout
    protected long timeout = 6000L;
    //default is true and ClientConnection
    protected volatile boolean nio = true;
    protected Serializer embeddedSerializer = new HessianSerializer();
    protected TCPClientFactory.ClientType clientType;

    public ClusterClient(List<ClusterNodes> clusterNodesList, String store, Serializer serializer,
                         TCPClientFactory.ClientType clientType) {
        if (clusterNodesList == null || store == null )
            throw new RuntimeException("clusterNodeList and store can not be null");
        this.store = store;
        if ( serializer == null )
            this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        this.clientType = clientType;
        this.clientConnections = new ClientConnections(clusterNodesList, embeddedSerializer, clientType);
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    /**
     * set connection timeout
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout ;
        clientConnections.setTimeout( timeout);
    }

    public void setNio(boolean nio) {
        this.nio = nio;
        clientConnections.setNio( nio);
    }

   public void setClientType(TCPClientFactory.ClientType clientType) {
       clientConnections.setClientType( clientType);
   }

    /**
     * to support return as json string,using Header.SerializableType.Json
      * @param serializeTpe
     */
   public void setSerializeTpe(Header.SerializableType serializeTpe) {
       clientConnections.setType( serializeTpe);
   }

   public void setHashFunction(Hash hash) {
       logger.info("set hash function "+hash.toString());
       clientConnections.setHash( hash);
   }

    @Override
    public void put(Key key, Object data) {
        Value value = new RemoteValue( data, 0L, (short) 0);
        put(key, value ) ;
    }

    @JmxOperation(description = "insert")
    @Override
    public void insert(Key key, Value value) {
        CacheValue v = createCacheValue(value, serializer);
        StoreParas paras = new StoreParas(OpType.Insert, key, v);
        runRequest( paras);
    }

    @JmxOperation(description = "put")
    @Override
    public void put(Key key, Value value) {
        updateQuery(key, value, null);
    }

    private void closeNio(Client tcpClient) {
        if ( nio == false ) {
            if ( tcpClient != null ) {
                tcpClient.close();
            }
        }
    }

    @Override
    public Value get(Key key) {
        return selectQuery( key, null);
    }


    @Override
    public boolean remove(Key key) {
        StoreParas paras = new StoreParas(OpType.Remove, key, null);
        //byte[] payload = paras.toBytes();
        byte[] payload = embeddedSerializer.toBytes( paras);
        Request request = clientConnections.createRequest(payload, store);
        Client tcpClient = null ;
        try {
            tcpClient = clientConnections.getConnection( key);
            Response response = tcpClient.sendRequest(request);
            if ( response.getPayload() instanceof StoreParas) {
                paras = (StoreParas) response.getPayload() ;
                if ( paras.isRemove() ) return true;
                else return false;
            }
            else {
                throw new StoreException("expect StoreParas but get "+response.getPayload().getClass().getName()+" "+
                        response.getPayload().toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex );
        }finally {
            closeNio(tcpClient);
        }
    }

    public Value selectQuery(Key key, String queryStr ) {
        StoreParas paras;
        if ( queryStr == null) //get with QueryStr == null and OpType assign according
            paras = new StoreParas(OpType.Get, key, null);
        else  //pass byte[] by String.getByte[], instead of serialized, without supporting UTF-8
            paras = new StoreParas(OpType.SelectQuery, key, CacheValue.createValue(queryStr.getBytes()));
        return runRequest(paras);
    }

    public void updateQuery(Key key, Value value, String queryStr) {
        StoreParas paras;
        if ( queryStr == null ) //put with queryStr == null and OpType assign according
            paras = new StoreParas(OpType.Put, key, createCacheValue(value, serializer));
        else {
            //keep version, but overwrite value with queryStr
            RemoteValue v = new RemoteValue( queryStr.getBytes(), value.getVersion(), value.getNode());
            paras = new StoreParas(OpType.UpdateQuery, key, v);
        }
        runRequest(paras);
    }

    private Value runRequest(StoreParas paras) {
        byte[] payload = embeddedSerializer.toBytes(paras);
        Request request = clientConnections.createRequest( payload, store);
        Client tcpClient = null ;
        try {
            tcpClient = clientConnections.getConnection( paras.getKey());
            Response response = tcpClient.sendRequest(request);
            if ( response.getPayload() instanceof StoreParas ) {
                //check error code handle exception
                StoreParas storeParas = ((StoreParas) response.getPayload());
                if ( storeParas.getErrorCode() == StoreParas.OBSOLETE) {
                    throw new ObsoleteVersionException("key "+ paras.getKey().toString());
                }
                else if ( storeParas.getErrorCode() == StoreParas.STORE_EXCEPTION ) {
                    throw new StoreException("key "+ paras.getKey().toString()) ;
                }
                if ( storeParas.getValue() == null ) return null;
                else
                    return createRemoteValue(storeParas.getValue(), serializer );
            }
            else  {
                throw new StoreException("expect StoreParas but get "+response.getPayload().getClass().getName()+" "+
                        response.getPayload().toString());
            }
        } finally {
            closeNio( tcpClient);
        }
    }


    /**
     *
     * @param from
     * @param to
     * @return
     */
    public List<KeyValue> scan(Key from, Key to) {
        return scan( from, to , null);
    }

    /**
     * OpType to determine deserialze payload during merge
     * MultiGets means payload is byte[], else payload is serialized object
     * @param from
     * @param to
     * @param queryStr
     * @return
     */
    public List<KeyValue> scan(Key from, Key to, String queryStr) {
        List<KeyCluster> keyClusterList = findAll();
        OpType opType = ( queryStr == null ? OpType.MultiGets : OpType.MultiSelectQuery);
        //return executeRequest( keyClusterList, opType);
        int size = keyClusterList.size();
        List<Key> keyList = new ArrayList<Key>(2);
        keyList.add(from); keyList.add(to);
        ScanParaList scanParaList = new ScanParaList(OpType.Get, keyList, queryStr);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Runnable> runnableList = new ArrayList<Runnable>(size);
        for ( int i = 0; i < size ; i++ ) {
            Request request = createScanRequest(scanParaList);
            RunThread runThread =  new RunThread(request, keyClusterList.get(i), countDownLatch, OpType.Scan);
            runnableList.add( runThread);
            executor.submit( runThread );
        }
        try {
            countDownLatch.await(timeout *5, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            return mergeResponse( runnableList);
        }
    }

    protected Request createScanRequest(ScanParaList scanParaList) {
        return clientConnections.createRequest(embeddedSerializer.toBytes(scanParaList), store, Request.RequestType.Scan);
    }

    protected Request createCursorRequest(CursorPara cursorPara) {
        return clientConnections.createRequest(embeddedSerializer.toBytes(cursorPara), store, Request.RequestType.Scan);
    }



    public List<KeyValue> query(String queryStr) {
        List<KeyCluster> keyClusterList = findAll();
        OpType opType = OpType.QueryStatement;
        int size = keyClusterList.size();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Runnable> runnableList = new ArrayList<Runnable>(size);
        for ( int i = 0; i < size ; i++ ) {
            Request request = create4GetPutRequest(keyClusterList.get(i).getKeyList(), opType, queryStr);
            RunThread runThread =  new RunThread(request, keyClusterList.get(i), countDownLatch, OpType.QueryStatement);
            runnableList.add( runThread);
            executor.submit( runThread );
        }
        try {
            countDownLatch.await(timeout*5, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            return mergeResponse( runnableList);
        }
    }

    public List<String> query4Json(String queryStr){
        List<KeyCluster> keyClusterList = findAll();
        OpType opType = OpType.QueryStatement;
        int size = keyClusterList.size();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Runnable> runnableList = new ArrayList<Runnable>(size);
        for ( int i = 0; i < size ; i++ ) {
            Request request = create4GetPutRequest(keyClusterList.get(i).getKeyList(), opType, queryStr);
            //set SerializableType to json
            request.getHeader().setSerializableType(Header.SerializableType.Json);
            RunThread runThread =  new RunThread(request, keyClusterList.get(i), countDownLatch, OpType.QueryStatement);
            runnableList.add( runThread);
            executor.submit( runThread );
        }
        try {
            countDownLatch.await(timeout*5, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            List<String> toReturn = new ArrayList<String>();
            for (Object each :  mergeStoreProc( runnableList))
                toReturn.add( (String) each);
            return toReturn ;
        }

    }


    public List<KeyValue> multiGets(List<Key> keys, String queryStr) {
        List<KeyCluster> keyClusterList = findKeyGroups( keys);
        OpType opType = ( queryStr == null ? OpType.MultiGets : OpType.MultiSelectQuery);
        return executeRequest( keyClusterList, opType, queryStr, keys);
    }

    @Override
    public List<KeyValue> multiGets(List<Key> keys) {
        return multiGets( keys, null);
    }

    public List<KeyValue>  multiPuts(List<KeyValue> list, String queryStr) {
        List<KeyCluster> keyClusterList = findKeyGroups( getKeyList(list));
        OpType opType = ( queryStr == null ? OpType.MultiPuts : OpType.MultiUpdateQuery);
        return executeRequest( keyClusterList, opType, queryStr, list);
    }
    @Override
    public List<KeyValue>  multiPuts(List<KeyValue> list) {
        return multiPuts( list, null);
    }

    @Override
    public List<KeyValue> scan(Key from) {
        return scan(from, from);
    }

    public List<KeyValue> multiRemoves(List<Key> keys) {
        List<KeyCluster> keyClusterList = findKeyGroups( keys);
        return executeRequest( keyClusterList, OpType.MultiRemoves, null, keys);
    }

    private List<KeyValue> executeRequest(List<KeyCluster> keyClusterList, OpType opType, String queryStr, List list) {
        int size = keyClusterList.size();
        //OpType opType = ( queryStr == null ? OpType.MultiGets : OpType.MultiSelectQuery);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Runnable> runnableList = new ArrayList<Runnable>(size);
        for ( int i = 0; i < size ; i++ ) {
            List paraList ;
            if ( opType == OpType.MultiUpdateQuery || opType ==OpType.MultiPuts) {
                paraList = findList( keyClusterList.get(i).getKeyList(), list);
            }
            else {
                paraList =  keyClusterList.get(i).getKeyList();
            }
            Request request = create4GetPutRequest(paraList, opType, queryStr);
            RunThread runThread =  new RunThread(request, keyClusterList.get(i).getCluster(), countDownLatch,
                    keyClusterList.get(i).getKeyList().get(0), opType) ;
            runnableList.add( runThread);
            executor.submit( runThread );
        }
        try {
            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            return mergeResponse( runnableList);
        }
    }

    protected List<KeyValue> findList(List<Key> keys, List<KeyValue> keyValues) {
        List<KeyValue> toReturn = new ArrayList<KeyValue>();
        for (Key each : keys) {
            for ( KeyValue values : keyValues) {
                if ( each.getKey().equals( values.getKey().getKey() )){
                    toReturn.add( values);
                    break;
                }
            }
        }
        return toReturn;
    }

    protected List<Key> getKeyList(List<KeyValue> list) {
        List<Key> toReturn = new ArrayList<Key>();
        for (KeyValue each : list) {
            toReturn.add( each.getKey());
        }
        return toReturn;
    }

    public ClusterCursor openScanCursor(short batchSize, Key from, Key to) {
        return nextCursor( new ClusterCursor( new CursorPara(store, batchSize, from, to)));
    }



    public ClusterCursor openKeyCursor(short batchSize) {
        return opeCursorType(batchSize, CursorPara.CursorType.KeySet);
    }

    /**
     * Table scan of key set to return List<Key> in key set
     * cursorPara.isEnd() is true, means end of cursor
     * to retrieve List<KeyValye> by calling cursorPara.getKeyValue()
     * @param batchSize
     * @return cursorPara
     */
    public ClusterCursor openKeyValueCursor(short batchSize) {
        return opeCursorType(batchSize, CursorPara.CursorType.KeyValueSet);
    }

    private ClusterCursor opeCursorType(short batchSize, CursorPara.CursorType cursorType) {
        return nextCursor(new ClusterCursor(new CursorPara(store, batchSize, cursorType.getValue()) ));
    }

    /**
     * nextCursor will continue call to each cluster node to iterate through server side cursor
     * isEnd() return true, mean the end of cursor for each cluster
      * @param clusterCursor
     * @return  ClusterCursor
     */
    public ClusterCursor nextCursor(ClusterCursor clusterCursor) {
        //clear out keyValueList first
        clusterCursor.resetCursorParaList();
        if ( clusterCursor.isEnd()) {
            logger.info("isEnd true for clusterCursor  size "+clusterCursor.getCursorParaList().size());
            return clusterCursor;
        }
        int size = clusterCursor.getKeyClusterList().size();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Runnable> runnableList = new ArrayList<Runnable>(size);
        for ( int i = 0; i < size ; i++ ) {
            //create request for nextCursor
            Request request = createCursorRequest( clusterCursor.getCursorParaList().get(i));
            RunThread runThread =  new RunThread(request, clusterCursor.getKeyClusterList().get(i), countDownLatch, OpType.Scan);
            runnableList.add( runThread);
            executor.submit( runThread );
        }
        try {
            // add 5 times to handle large package for store proc
            countDownLatch.await(timeout *5, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            return mergeCluster(runnableList, clusterCursor);
        }
    }

    public ClusterCursor mergeCluster(List<Runnable> runnableList,ClusterCursor clusterCursor )  {
        for ( int i = 0; i < runnableList.size() ; i ++ ) {
            if ( ((RunThread) runnableList.get(i)).getStoreProc() != null ) {
                CursorPara cursorPara = (CursorPara) ((RunThread) runnableList.get(i)).getStoreProc() ;
                clusterCursor.getCursorParaList().set(i, cursorPara );
            }
        }
        return clusterCursor;
    }


    protected List<KeyValue> mergeResponse(List<Runnable> runnableList ) {
        List<KeyValue> toReturn = new ArrayList<KeyValue>();
        for ( Runnable each : runnableList ) {
            if ( ((RunThread) each).getKeyValueList() != null ) {
                toReturn.addAll(((RunThread) each).getKeyValueList());
            }
        }
        return toReturn;
    }

    protected Request create4GetPutRequest(List list, OpType opType, String queryStr) {
        if ( list.get(0) instanceof KeyValue) {
            List<KeyValue> keyValueList = new ArrayList<KeyValue>();
            for ( KeyValue each : (List<KeyValue>) list ) {
                if ( each.getValue() != null && each.getValue().getData() != null) {
                    CacheValue v = createCacheValue(each.getValue(), serializer);
                    keyValueList.add( new KeyValue( each.getKey(), v));
                }
            }
            KeyValueParas keyValueParas = new KeyValueParas(opType, keyValueList, queryStr);
            return clientConnections.createRequest(embeddedSerializer.toBytes(keyValueParas), store, Request.RequestType.Scan);
        }
        else {
            if ( queryStr == null ) { //MultiGet
                KeyParas keyParas = new KeyParas(opType, list);
                return clientConnections.createRequest(embeddedSerializer.toBytes(keyParas), store, Request.RequestType.Scan);
            }
            else {    //MultiSelectQuery
                List<KeyValue> keyValueList = new ArrayList<KeyValue>();
                for ( Key each : (List<Key>) list) {
                    keyValueList.add( new KeyValue( each, null));
                }
                KeyValueParas keyValueParas = new KeyValueParas(opType, keyValueList, queryStr);
                return clientConnections.createRequest(embeddedSerializer.toBytes(keyValueParas), store, Request.RequestType.Scan);
            }
        }
    }

    final int executorSize = Runtime.getRuntime().availableProcessors()*20;;
    ExecutorService futureExecutor = Executors.newFixedThreadPool(executorSize, new ThreadPoolFactory("client"));
    /**
     *
     * @param invoker
     * @param keys
     * @return future
     */
    public Future clusterStoreProcFuture(final Invoker invoker, final List<Key> keys) {
        AtomicReference<FutureTask<List<Object>>> future =
                new AtomicReference<FutureTask<List<Object>>> (new FutureTask<List<Object>>
                        (new Callable<List<Object>>() {
                    public List<Object> call() {
                        return clusterStoreProc(invoker, keys);
                    }
                }));
        futureExecutor.execute(future.get());
        return future.get();
    }

    /**
     * invoke store proc across clusters without passing keyList
     * one node per cluster
     * @param invoker
     * @return
     */
    public List<Object> invoke(Invoker invoker) {
        List<KeyCluster> keyClusterList = findAll();
        return executeStoreProc(invoker, keyClusterList, false);
    }

    /**
     * invoke MapReduce store proc across the cluster, The goal is to use max number of node
     * so it try to invoke every node in each cluster, instead of one node per cluster
     * data node will be provided partition information through additional parameter add to invoker
     * in the format of (node#, total nodes)
     * @param invoker
     * @return
     */
    public List<Object> invokeMapReduce(Invoker invoker) {
        List<Pair<Pair<Short, Integer>, String>> list = clientConnections.getMapClientFromAll();
        List<KeyCluster> keyClusterList = new ArrayList<KeyCluster>(list.size());
        for ( Pair<Pair<Short, Integer>, String> each : list) {
            //key is represented by node 1 separate by , total nodes example 1,3
            //will passed by key to store proc to determine the how partition data node
            KeyCluster keyCluster = new KeyCluster(each.getFirst().getFirst(), Key.createKey(each.getSecond()),
                    each.getFirst().getSecond() );
            keyClusterList.add( keyCluster);
        }
        //add Key must be true, key carry information about how to split cluster
        return executeStoreProc(invoker, keyClusterList, true);
    }

    /**
     *
     * @param invoker
     * @param keys list of keys to send to cluster of nodes
     * @return
     */
    public List<Object> clusterStoreProc(Invoker invoker, List<Key> keys) {
//        List<KeyCluster> keyClusterList = findKeyGroups( keys);
//        if ( keyClusterList.size() == 0) throw new RuntimeException("keyCluster size = 0, keys size "+keys.size());
        return clusterStoreProc(invoker, keys, null);
    }

    public List<Object> clusterStoreProc(Invoker invoker, List<Key> keys, String queryStr) {
        List<KeyCluster> keyClusterList = findKeyGroups( keys);
        if ( keyClusterList.size() == 0) throw new RuntimeException("keyCluster size = 0, keys size "+keys.size());
        //try to rebalance the size of key for each node
        List<KeyCluster> kcList = rebalance( keyClusterList) ;
        return executeStoreProc(invoker, kcList, true, queryStr );
    }

    protected List<KeyCluster> rebalance(List<KeyCluster> list) {
        List<KeyCluster> toReturn = new ArrayList<KeyCluster>(list.size());
         while ( list.size() > 0) {
             Pair<KeyCluster, KeyCluster> pair = balance(list);
             toReturn.add( pair.getFirst());
             if ( pair.getSecond() != null)
                 toReturn.add( pair.getSecond());
         }
         return toReturn;
    }

    private Pair<KeyCluster, KeyCluster> balance(List<KeyCluster> list) {
        if (list.size() == 0 ) return null;
        //get the first one
        KeyCluster first = list.get(0);
        KeyCluster second = null;
        list.remove( first);
        for (KeyCluster each :list ) {
            if (each.getCluster() == first.getCluster()) {
                int dif = Math.abs(each.getKeyList().size() - first.getKeyList().size());
                int max = Math.max(each.getKeyList().size() , first.getKeyList().size()) ;
                if ( dif > 500  && (dif * 100)/max >= 30 ) {
                    int remove = dif /2 ;
                    logger.info("rebalance max "+max+" diff "+dif+" remove "+remove);
                    // move half of diff
                    List<Key> toRemove = new ArrayList<Key>(dif/2);
                    if (each.getKeyList().size() > first.getKeyList().size()) {
                        for (int j = 0; j < remove; j++) {
                            Key k = each.getKeyList().get(j);
                            toRemove.add(k);
                        }
                        first.getKeyList().addAll(toRemove);
                        each.getKeyList().removeAll( toRemove);
                    } else if (each.getKeyList().size() < first.getKeyList().size()) {
                        for (int j = 0; j < remove; j++) {
                            Key k = first.getKeyList().get(j);
                            toRemove.add(k);
                        }
                        each.getKeyList().addAll(toRemove);
                        first.getKeyList().removeAll( toRemove);
                    }
                    logger.info("rebalance "+first.toString()+" "+each.toString());
                }
                second = each;
                break;
            }
        }
        if ( second != null ) list.remove( second);
        return new Pair<KeyCluster, KeyCluster>(first, second);
    }

    protected List<Object> executeStoreProc(Invoker invoker, List<KeyCluster> keyClusterList, boolean addKey){
        return executeStoreProc(invoker, keyClusterList, addKey, null);
    }


    protected List<Object> executeStoreProc(Invoker invoker, List<KeyCluster> keyClusterList, boolean addKey, String queryStr) {
        int size = keyClusterList.size();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Runnable> runnableList = new ArrayList<Runnable>(size);
        for ( int i = 0; i < size ; i++ ) {
            // need to clone a new invoker and keyList into corresponding cluster
            Invoker iv = new Invoker( invoker.getClassName(), invoker.getMethod(), invoker.getParams());
            if ( addKey) {
                iv.addParas(keyClusterList.get(i).getKeyList());
                if ( queryStr != null )
                    iv.addParas(queryStr);
            }
            //create cluster from new invoker instance
            Request request = createStoreProcRequest(iv);
            RunThread runThread =  new RunThread(request, keyClusterList.get(i), countDownLatch, OpType.Scan) ;
            runnableList.add( runThread);
            executor.submit( runThread );
        }
        try {
            // add 5 times to handle large package for store proc
            countDownLatch.await(timeout *5, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            return mergeStoreProc(runnableList);
        }
    }

    protected Request createStoreProcRequest(Invoker invoker) {
        return clientConnections.createRequest(embeddedSerializer.toBytes(invoker), store, Request.RequestType.Invoker);
    }

    protected List<KeyCluster> findAll() {
        List<Pair<Short, Integer>> list = clientConnections.getOneFromAllCluster();
        List<KeyCluster> toReturn = new ArrayList<KeyCluster>(list.size());
        for ( Pair<Short, Integer> each : list) {
            KeyCluster keyCluster = new KeyCluster(each.getFirst(), Key.createKey(1), each.getSecond() );
            toReturn.add( keyCluster);
        }
        return toReturn;
    }



    protected List<Object> mergeStoreProc(List<Runnable> runnableList ) {
        List<Object> toReturn = new ArrayList<Object>();
        for ( Runnable each : runnableList ) {
            if ( ((RunThread) each).getStoreProc() != null ) {
                toReturn.add(((RunThread) each).getStoreProc()) ;
            }
        }
        return toReturn;
    }


    public class RunThread implements Runnable {
        Key key = null;
        Request request;
        short cluster;
        //for multiple get and put
        List<KeyValue> keyValueList;
        CountDownLatch countDownLatch;
        //store procedure return
        Object storeProc;
        OpType opType;
        KeyCluster keyCluster = null;

        public RunThread(Request request, short cluster,  CountDownLatch countDownLatch, Key key) {
            this( request, cluster, countDownLatch, key, OpType.Scan);
        }

        public RunThread(Request request, short cluster,  CountDownLatch countDownLatch, Key key, OpType opType) {
            this.request = request;
            this.cluster = cluster;
            this.countDownLatch = countDownLatch;
            this.key = key;
            this.opType = opType;
        }

        public RunThread(Request request, KeyCluster keyCluster,CountDownLatch countDownLatch, OpType opType ) {
            this.request = request;
            this.countDownLatch = countDownLatch;
            this.opType = opType;
            this.keyCluster = keyCluster;
        }

        /**
         * support query and scan which did not have key, like multiGet
         * @return
         */
        private Client findClient() {
            if ( key != null )
                return clientConnections.getConnection(key);
            else {  //use keyCluster to find client
                return clientConnections.getConnection( keyCluster.getCluster(), keyCluster.getIndex());
            }
        }

        public void run() {
           Client tcpClient = null ;
            try {
                //tcpClient = clientConnections.getConnection(key);
                tcpClient = findClient();
                Response response = tcpClient.sendRequest(request);
                if ( ! response.isError()) {
                    if ( response.getPayload() instanceof KeyValueParas) {
                        //create a empty list to handle de serialize return byte[]
                        List<KeyValue> list = new ArrayList<KeyValue>();
                        keyValueList = ((KeyValueParas) response.getPayload()).getList();
                        if (opType == OpType.MultiGets || opType == OpType.MultiPuts || opType == OpType.MultiRemoves) {
                        //de serialize the value and create a KeyValueList
                            for ( KeyValue each : keyValueList) {
                                if ( each.getValue() != null  && each.getValue().getData() != null ) {
                                    try {
                                        list.add(new KeyValue(each.getKey(), createRemoteValue(each.getValue(), serializer)));
                                    } catch (Exception ex) {
                                        //swallow exception
                                        logger.error("key "+convert2Str(each.getKey())+" "+ex.getMessage());
                                    }
                                }
                                else list.add( each);
                            }
                            //reassign back to keyValueList
                            keyValueList = list;
                        }
                        //else do nothing for OpType = OpType.QueryStatement, since the value.getData is in object type
                    }
                    else
                        storeProc =  response.getPayload();
                }
                else {
                    logger.error( response.getPayload().toString());
                }
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex );
            } finally {
                //countDown by 1
                countDownLatch.countDown();
                if (nio == false) {
                    if ( tcpClient != null ) {
                        tcpClient.close();
                    }
                }
            }
        }

        public List<KeyValue> getKeyValueList() {
            return keyValueList;
        }

        public Object getStoreProc() {
            return storeProc;
        }
    }

    private String convert2Str(Key key) {
        try {
            if (key.getKey() instanceof byte[]) {
                return new String((byte[]) key.getKey());
            }
            else {
                return key.getKey().toString();
            }
        }catch (Exception ex) {
            return ex.getMessage();
            //do nothing
        }
    }

    public class ClusterCursor {
        List<CursorPara> cursorParaList;
        List<KeyCluster> keyClusterList;

        public ClusterCursor(CursorPara cursorPara) {
            keyClusterList = findAll();
            cursorParaList = new ArrayList<CursorPara>( keyClusterList.size());
            for ( int i = 0 ; i < keyClusterList.size() ; i ++) {
                cursorParaList.add( new CursorPara( cursorPara.getStore(), cursorPara.getBatchSize(), cursorPara.getFrom(),
                    cursorPara.getTo(), cursorPara.getQueryStr()));
            }
        }

        public List<KeyValue> getResult() {
            List<KeyValue> toReturn = new ArrayList<KeyValue>();
            for (CursorPara each : cursorParaList ) {
                if ( each.getKeyValueList() != null ) {
                    for ( KeyValue e : each.getKeyValueList())
                        toReturn.add(e);
                }
            }
            return toReturn;
        }

        public boolean isEnd() {
            for (CursorPara each : cursorParaList ) {
                if ( ! each.isEnd() ) return false;
            }
            return true;
        }

        public boolean isStop() {
            for (CursorPara each : cursorParaList ) {
                if ( ! each.isStop() ) return false;
            }
            return true;
        }

        public void resetCursorParaList() {
            for (CursorPara each : cursorParaList ) {
                each.setKeyValueList( null);
            }
        }

        public List<CursorPara> getCursorParaList() {
            return cursorParaList;
        }

        public List<KeyCluster> getKeyClusterList() {
            return keyClusterList;
        }

        public ClusterCursor close() {
            logger.info("close: set each CursorPara.stop to true");
            for (CursorPara each : cursorParaList ) {
                if ( each != null ) {
                    each.setStop( true);
                }
            }
            logger.info("call nextCursor size "+cursorParaList.size());
            return nextCursor( this);
        }
    }


    protected List<KeyCluster> findKeyGroups(List<Key> list) {
        List<KeyCluster> toReturn = new ArrayList<KeyCluster>();
        for ( Key each : list) {
            findKeyCluster( each, toReturn);
        }
        return toReturn;
    }

    protected void findKeyCluster(Key key, List<KeyCluster> list) {
        //short cluster = clientConnections.getPartition(key);
        Pair<Short, Integer> clusterIndex = clientConnections.getPartitionIndex( key);
        for ( KeyCluster each : list ) {
            if ( each.isSameCluster( clusterIndex) ) {
                // find the cluster, add key and exit
                each.addKey( key);
                return ;
            }
        }
        //not in list
        list.add( new KeyCluster(clusterIndex.getFirst() , key, clusterIndex.getSecond() ) );
    }



    public class KeyCluster {
        short cluster;
        int index;
        List<Key> keyList;

        public KeyCluster(short cluster,  Key key, int index) {
            this.cluster = cluster;
            this.keyList = new ArrayList<Key>();
            this.keyList.add( key);
            this.index = index;
        }

        public KeyCluster(short cluster, Key key) {
            this.cluster = cluster;
            this.keyList = new ArrayList<Key>();
            this.keyList.add( key);
        }

        public short getCluster() {
            return cluster;
        }

        public int getIndex() {
            return index;
        }

        public List<Key> getKeyList() {
            return keyList;
        }

        public boolean isSameCluster(Pair<Short, Integer> pair) {
            if (pair.getFirst() == cluster && pair.getSecond() == index)
                return true;
            else
                return false;
        }

        public void addKey(Key key) {
            keyList.add(key);
        }

        @Override
        public String toString(){
            return "cluster "+cluster+" index "+index +" size "+keyList.size();
        }
    }

    @Override
    public void close() {
        logger.info("close all client connections and shutdown");
        clientConnections.stop();
        clientConnections.shutdown();
    }

    public void shutdown() {
        clientConnections.shutdown();
    }

    @Override
    public Iterator getKeyIterator() {
        logger.warn("getKeyIterator is not support");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        logger.warn("size is not support");
        return 0;
    }

    @Override
    public void pack(int rate) {
        logger.warn("pack is not support");
    }

    @Override
    public void backup(String path, int rate) {
        logger.warn("backup is not support");
    }


}
