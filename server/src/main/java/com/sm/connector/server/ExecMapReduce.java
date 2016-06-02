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

import com.sm.connector.*;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.query.Filter;
import com.sm.query.PredicateAlias;
import com.sm.query.PredicateVisitorImpl;
import com.sm.query.SchemaPredicateVisitorImpl;
import com.sm.query.utils.Column;
import com.sm.query.utils.QueryUtils;
import com.sm.storage.Serializer;
import com.sm.store.StoreConfig;
import com.sm.store.server.RemoteStore;
import com.sm.utils.ThreadPoolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by mhsieh on 12/16/15.
 */
public class ExecMapReduce<T> {
    private static final Log logger = LogFactory.getLog(ExecMapReduce.class);

    Map<String, ServerStore> serverStoreMap;
    ServerStore serverStore ;
    Class<T> tClass;
    MapReduce<T> mapReduce;
    List<T> list = new CopyOnWriteArrayList<T>();
    long timeout = 60000L*5 ;
    String predicateQuery ;
    Serializer serializer ;
    boolean runPredicate = false;
    Filter.Impl impl;
    Map<String, RemoteStore> remoteStoreMap;
    public final static String SCAN = "scan.xml";
    public final static String COLUMN = "column";
    StoreConfig storeConfig;

    /**
     *
     * @param remoteStoreMap
     * @param tClass for Aggregation
     * @param mapReduce
     */
    public ExecMapReduce(Map<String, RemoteStore> remoteStoreMap, Class<T> tClass, MapReduce<T> mapReduce) {
        this(remoteStoreMap, tClass, mapReduce, null);
    }

    /**
     *
     * @param remoteStoreMap
     * @param tClass
     * @param mapReduce
     * @param serializer
     */
    public ExecMapReduce(Map<String, RemoteStore> remoteStoreMap, Class<T> tClass, MapReduce<T> mapReduce, Serializer serializer) {
        this(remoteStoreMap, tClass, mapReduce, serializer, null, Filter.Impl.Schema);
    }

    public ExecMapReduce(Map<String, RemoteStore> remoteStoreMap, Class<T> tClass, MapReduce<T> mapReduce, Serializer serializer,
                         String predicateQuery, Filter.Impl impl) {
        this.remoteStoreMap = remoteStoreMap;
        this.tClass = tClass;
        this.mapReduce = mapReduce;
        this.predicateQuery = predicateQuery;
        this.impl = impl;
        if ( predicateQuery != null )
            runPredicate = true;
        if ( serializer == null)
            this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        this.serverStoreMap = MapReduceUtils.getInstance().createStoreMap(SCAN);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     *
     * @param store
     * @param noOfThread
     * @param partition has format nodeNo, totalNodes which enable all node to participate the mapreduce
     * examples, two nodes in 1,2 and 2,2
     * @return
     */
    public Object execute(String store, int noOfThread, String partition) {
        Pair<Integer, Integer> pair = findPartition(partition, store);
        return execute(store, noOfThread, pair.getFirst(), pair.getSecond());
    }


    public Object execute(String store, int noOfThread) {
        serverStore = serverStoreMap.get(store);
        if ( serverStore == null )
            throw new RuntimeException("can not find ServerStore "+store);
        int totalRec = serverStore.getTotalRecord();
        return execute(store, noOfThread, 0, totalRec);
    }

    /**
     * each thread will process 3 of record (end -begin) /noOfThread
     * @param store -name of store
     * @param noOfThread how many thread to run concurrently
     * @param begin record#  for this node
     * @param end record#  for this node
     * @return by reducer
     */
    public Object execute(String store, int noOfThread, int begin, int end) {
        logger.info("execute "+store+" threads "+noOfThread+" begin "+begin+" end "+end);
        if ( noOfThread <= 0 || begin >= end )
            throw new RuntimeException("number of thread "+ noOfThread+" must be > 0  or begin "+begin+ " >= end "+end);
        serverStore = serverStoreMap.get(store);
        if ( serverStore == null )
            throw new RuntimeException("can not find ServerStore "+store);
        //how many record need to be process
        int totalRec = end - begin;
        int blockSize = ( totalRec % noOfThread == 0 ? totalRec /noOfThread : (totalRec/noOfThread)+1);
        CountDownLatch countDownLatch = new CountDownLatch(noOfThread);
        ExecutorService executor = Executors.newFixedThreadPool(noOfThread, new ThreadPoolFactory("exec"));
        List<Runnable> runnableList = new ArrayList<Runnable>(noOfThread);
        logger.info("start to run "+noOfThread+" threads block size "+blockSize+" for "+store +" total "+totalRec);
        for ( int i = 0; i < noOfThread ; i++ ) {
            try {
                //T t = tClass.newInstance();
                T t = (T) QueryUtils.createInstance(tClass);
                if ( i < noOfThread -1 ) {
                    RunThread runThread =  new RunThread(countDownLatch, begin+blockSize* i, begin+blockSize*(i+1), t , i, store);
                    runnableList.add( runThread);
                    executor.submit( runThread );
                }
                else {  //the last block
                    RunThread runThread =  new RunThread(countDownLatch, begin+blockSize* i, begin+totalRec , t , i, store);
                    runnableList.add( runThread);
                    executor.submit( runThread );
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        try {
            countDownLatch.await(timeout*10, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            executor.shutdown();
            return mapReduce.reduce( list);
        }

    }

    /**
     * for string of "1,2" that means first node of two nodes, if total records is 10,000 records
     * the first node, get from 0 - 5,000 ; second node get 5,000 - 10,000
     * @param partition
     * @param store
     * @return
     */
    private Pair<Integer, Integer> findPartition(String partition, String store) {
        String[] strings = partition.split(",");
        if (strings.length != 2)
            throw new RuntimeException("Invalidate String for partition "+partition);
        serverStore = serverStoreMap.get(store);
        if ( serverStore == null )
            throw new RuntimeException("can not find ServerStore "+store);
        int total = serverStore.getTotalRecord();
        int first = Integer.valueOf(strings[0]);
        int second = Integer.valueOf(strings[1]);
        if ( second <= 1 )
            return new Pair<Integer, Integer>( 0, total);
        else {
            if ( first > second || first < 1 ) throw new RuntimeException("first "+first+ " > second "+store);
            //second > 1
            int block = total / second ;
            if ( first == second ) {   //last block
                int b = block *(first -1) ;
                return new Pair<Integer, Integer>( b , total );
            }
            else {  //anything other than last block
                return new Pair<Integer, Integer>( block* (first-1), block *first );
            }

        }
    }

    protected Serializer colSerialize = new HessianSerializer();
    private Filter createFilter(String query, String store) {
        if ( impl == Filter.Impl.Schema) {
            Value value = remoteStoreMap.get(COLUMN).get(Key.createKey(store));
            Map<String, Column> columnStore;
            if ( value == null || value.getData() == null ) {
                logger.warn("store "+store +" does not exist ");
                columnStore =  new HashMap<String, Column>();
            }
            else {
                columnStore = (Map<String, Column>) colSerialize.toObject((byte[])value.getData());
            }
            return new SchemaPredicateVisitorImpl(query, columnStore);
        }
        else
            return new PredicateVisitorImpl(query);
    }

    /**
     * use StoreConfig.IsUrl to determine which (MRStore or ServerStore) tp create
     * MRStore assume that all records are physical consective
     */
    public class RunThread implements Runnable {
        CountDownLatch countDownLatch;
        MRIterator local;
        int begin;
        int end;
        T t;
        int taskNo;
        String store;
        Filter filter = null;
        List<String> collection = new ArrayList<String>();

        public RunThread(CountDownLatch countDownLatch,int begin, int end, T t, int taskNo, String store) {
            this.countDownLatch = countDownLatch;
            this.t = t;
            this.begin = begin;
            this.end = end;
            this.taskNo = taskNo;
            this.store = store;
            init();
        }

        private void init() {
            long b = System.currentTimeMillis();
            if (MapReduceUtils.getInstance().getColumnMap(store).isUseLRU() )
                local = new MRStore( serverStore.getFilename());
            else
                local = new ServerStore(serverStore.getFilename());
            local.setCurrent( begin);
            logger.info("open file ms "+(System.currentTimeMillis() -b));
        }


        @Override
        public void run() {
            Map<String, Object> context = new ConcurrentHashMap<String, Object>(11);
            //Filter filter = null;
            logger.info("begin record# "+begin+ " end "+end+" "+t.getClass().getSimpleName());
            int delete = 0, error = 0, s= 0, total = end -begin ;
            long b = System.currentTimeMillis();
            if ( runPredicate) {
                logger.info("create predicate object for "+predicateQuery);
                filter = createFilter(predicateQuery, store);
                //build the actual collection except store
                PredicateAlias predicateAlias = new PredicateAlias( predicateQuery);
                collection = predicateAlias.findAlias( impl);
                logger.info("impl "+impl.toString()+" "+collection.toString());
            }
            //call beforeStartMap()
            mapReduce.beforeMapStart(t, taskNo, context);
            //loop through isEnd == false
            while (! local.isEnd(end) ) {
                try {
                    Pair<Key, byte[]> pair = local.next();
                    if ( pair == null ) {
                        delete++;
                        continue;
                    }
                    if ( runMapfilter( pair, t, context)) {
                        s++;
                    }
                } catch (Throwable ex) {
                    error++;
                    logger.error(ex.getMessage(), ex);
                }
            }
            logger.info("process ms "+( System.currentTimeMillis() -b)+" total " +total +" error "+error+" success "+
                    s + " delete "+delete);
            //call after completion
            mapReduce.afterMapComplete(t, taskNo, context);
            try {
                local.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                list.add(t);
                countDownLatch.countDown();
                //call afterMap()
            }
        }



        private boolean runMapfilter( Pair<Key, byte[]> pair, T record, Map<String, Object> map) {
            if ( mapReduce instanceof JoinMapReduce  ) {
                JoinMapReduce join = (JoinMapReduce) mapReduce;
                return join.joinMap(pair, t , map, collection, filter);
            }
            else {
                Object source ;
                if (impl == Filter.Impl.Schema )
                    source = pair.getSecond();
                else
                    source = serializer.toObject( pair.getSecond());

                if ( runPredicate)  {
                    //skip for predicate false
                    if ( ! filter.runPredicate( source) )
                        return false;
                }
                Pair<Key, Object> pairKO = new Pair<Key, Object>( pair.getFirst(), source);
                mapReduce.map(pairKO, t, map);
                return true;
            }
        }
    }



}
