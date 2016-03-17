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

package com.sm.localstore.impl;

import com.sm.replica.client.ReplicaClient;
import com.sm.replica.client.netty.NTReplicaClient;
import com.sm.storage.Persistence;
import com.sm.storage.Serializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxGetter;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.*;
import voldemort.store.cachestore.impl.*;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.store.cachestore.voldeimpl.WriteLog;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import static voldemort.store.cachestore.voldeimpl.VoldeUtil.*;
@JmxManaged(description = "Local store")
public class LocalStoreImpl<T> implements Persistence<T> {

    protected static Log logger = LogFactory.getLog(LocalStoreImpl.class);
    //cachestore impl
    protected CacheStore store;
    // path
    protected String path;
    //filename
    protected String filename;
    //user defined serializer
    protected Serializer serializer;
    protected boolean delay = false;
    protected BlockSize blockSize = null;
    // keep track of recent cache stats  and move to constructor
    protected RecentCacheStats recentCacheStats ;
   // keep track of timestamp for each operation, pack backup and purge
    protected List<Long> statList = new CopyOnWriteArrayList<Long>();
    protected int mode = 0;
    //implement performance metric
    protected AtomicLong getFreq = new AtomicLong(0);
    // in micro seconds
    protected AtomicLong getTimes = new AtomicLong(0);
    protected AtomicLong putFreq = new AtomicLong(0);
    // in micro seconds
    protected AtomicLong putTimes = new AtomicLong(0);
    protected AtomicLong removeFreq = new AtomicLong(0);
    // in micro seconds
    protected AtomicLong removeTimes = new AtomicLong(0);
    protected boolean isSorted = false ;


    public LocalStoreImpl() {
    }

    public LocalStoreImpl(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize,
                         int mode, boolean isSorted) {
        this.path = path;
        this.filename = filename;
        this.serializer = serializer;
        this.delay = delay;
        this.blockSize = blockSize;
        this.mode =mode;
        this.isSorted = isSorted;
        init();

    }

    public LocalStoreImpl(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode) {
        this(filename, serializer, path, delay, blockSize, mode, false);
    }

    public LocalStoreImpl(String filename, Serializer serializer, boolean delay, int mode) {
        String[] names = checkPath(filename);
        this.filename = names[1];
        this.serializer = serializer;
        this.path = names[0];
        this.delay = delay;
        this.mode = mode;
        init();
    }

    public LocalStoreImpl(String filename, Serializer serializer, int mode) {
        this(filename, serializer, false, mode);
    }

    public LocalStoreImpl(CacheStore cacheStore, boolean delay, int mode) {
        this.delay = delay;
        this.mode = mode;
        init(cacheStore);
    }

    protected void init(CacheStore cacheStore) {
        store = cacheStore;
        filename = store.getNamePrefix();
        for ( int i = 0; i < 6 ; i++) statList.add(0L);
        this.recentCacheStats = new RecentCacheStats(store);
    }

    protected void init() {
        if ( serializer == null ) throw new StoreException("Serializer can not be null");
        if (isSorted)
            this.store = new SortedCacheStore(path, blockSize, 0, filename, delay, mode );
        else
            this.store = new CacheStore(path, blockSize, 0, filename, delay, mode );
        for ( int i = 0; i < 6 ; i++) statList.add(0L);
        this.recentCacheStats = new RecentCacheStats(store);
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void put(Key key, Value value) {
        long begin = System.nanoTime();
        try {
            store.put(key, value);
        } finally {
            long duration = (System.nanoTime() - begin) / 1000;
            putFreq.getAndIncrement();
            putTimes.addAndGet( duration);
        }
    }

    public void put(Key key, T object) {
        Value<byte[]> value = CacheValue.createValue( serializer.toBytes(object), 0L, (short) 0);
        put(key, value);
    }


    public void putInMap(Key key, T object) {
        long begin = System.nanoTime();
        try {
            Value<byte[]> value = CacheValue.createValue( serializer.toBytes(object), 0L, (short) 0);
        store.putInMap(key, value);
        } finally {
            long duration = (System.nanoTime() - begin) / 1000;
            putFreq.getAndIncrement();
            putTimes.addAndGet( duration);
        }
    }

    protected Value getRemote(Key key) {
        long begin = System.nanoTime();
        try {
            return store.get( key);
        } finally {
            long duration = (System.nanoTime() - begin) / 1000;
            getFreq.getAndIncrement();
            getTimes.addAndGet( duration);
        }
    }

    public T get(Key key) {
        Value<byte[]> value = getRemote( key);
        if ( value != null )
            return (T) serializer.toObject( value.getData() );
        else
            return null;
    }


    public boolean remove(Key key) {
        long begin = System.nanoTime();
        try {
            return store.remove( key);
        } finally {
            long duration = (System.nanoTime() - begin) / 1000;
            removeFreq.getAndIncrement();
            removeTimes.addAndGet( duration);
        }
    }

    public boolean containsKey(Key key) {
        return store.getMap().containsKey( key);
    }

    public void close() {
        store.close();
    }

    public Iterator getKeyIterator() {
        return store.getMap().keySet().iterator();
    }

    public int size() {
        return store.getMap().size();
    }

    public CacheStore getStore() {
        return store;
    }

    @Override
    @JmxOperation(description = "Pack data for Embedded Storage. rate - MB/sec")
    public void pack(int rate) {
        statList.set(0, System.currentTimeMillis());
        store.pack( rate);
        statList.set(1, System.currentTimeMillis());
    }

    @JmxOperation(description = "Backup data for Embedded Storage. rate - MB/sec")
    @Override
    public void backup(String path, int rate) {
        store.backup(path, rate);
    }
    private String toDate(long timestamp) {
        return new Date( timestamp).toString();
    }

    private String getStatList() {
        StringBuilder sb = new StringBuilder();
        if ( statList.size() == 0) return "No records";
        else {
            sb.append("Pack Data ");
            if ( statList.get(0) != 0 && statList.get(1) != 0)
                sb.append(" begin "+toDate(statList.get(0)) + " end " + toDate( statList.get(1)) +
                sb.append(" duration "+ (statList.get(1) - statList.get(0)) ) );
            else sb.append(" no record");
            sb.append(", Back up Data ");
            if ( statList.get(2) != 0 && statList.get(3) != 0)
                sb.append(" begin "+toDate(statList.get(2)) + " end " + toDate( statList.get(3)) +
                sb.append(" duration "+ (statList.get(3) - statList.get(2) ) ) );
            else sb.append(" no record ");
            sb.append(", Purge Data ");
            if ( statList.get(4) != 0 && statList.get(5) != 0)
                sb.append(" begin "+toDate(statList.get(4)) + " end " + toDate( statList.get(5)) +
                sb.append(" duration "+ (statList.get(5) - statList.get(4)) ) );
            else sb.append(" no record");
            return sb.toString() ;
        }
    }


    @JmxOperation(description = "Get statistic from Embedded Storage.")
    public String getStoreStat() {
        return "Store "+ filename+ store.getStat()+"\n" + getStatList() ;
    }

    @JmxGetter(name="Store Name")
    public String getStoreName(){ return filename; }
    @JmxGetter(name="Total Records")
    public long getTotalRecords(){ return store.getList().get(store.getCurIndex()).getTotalRecord(); }
    @JmxGetter(name="Total Deleted Records")
    public long getTotalDeletedRecords(){ return store.getDeleted(); }
    @JmxGetter(name="Total Active Records")
    public long getTotalActiveRecords(){ return (store.getList().get(store.getCurIndex()).getTotalRecord()-store.getDeleted()); }
    @JmxGetter(name="Total Active Records Percentage")
    public long getTotalActiveRecordsPercentage(){
        long hit = getTotalActiveRecords();
        long miss = getTotalDeletedRecords();
        return ( miss == 0 ? 100 : ( hit * 100 / (hit+miss) )); }
    @JmxGetter(name="File Size")
    public long getFileSize(){ return store.getList().get(store.getCurIndex()).getDataOffset(); }
    @JmxGetter(name="Block Overflow")
    public long getBlockOverflow(){ return store.getOverflow().get(); }
    @JmxGetter(name="Purge Trigger")
    public long getPurgeTrigger(){ return store.getTrigger(); }
    @JmxGetter(name="Cache Hits")
    public long getCacheHits(){ return store.getCacheHit().get(); }
    @JmxGetter(name="Cache Misses")
    public long getCacheMisses(){ return store.getCacheMis().get(); }
    @JmxGetter(name="Cache Hit Percentage")
    public long getCacheHitPercentage(){
        long hit = store.getCacheHit().get();
        long miss = store.getCacheMis().get();
        return ( miss == 0 ? 100 : ( hit * 100 / (hit+miss) )); }
    @JmxGetter(name="Recent Cache Hits")
    public long getRecentCacheHits(){ return recentCacheStats.getRecentCacheHits(); }
    @JmxGetter(name="Recent Cache Misses")
    public long getRecentCacheMisses(){ return recentCacheStats.getRecentCacheMisses(); }
    @JmxGetter(name="Recent Cache Hit Percentage")
    public long getRecentCacheHitPercentage(){
        long hit = recentCacheStats.getRecentCacheHits();
        long miss = recentCacheStats.getRecentCacheMisses();
        return ( miss == 0 ? 100 : ( hit * 100 / (hit+miss) )); }
    @JmxGetter(name="Pack Start Time")
    public String getPackStartTime(){ if ( statList.get(0) != 0 && statList.get(1) != 0)  return ""+toDate(statList.get(0)); else return "no record";  }
    @JmxGetter(name="Pack End Time")
    public String getPackEndTime(){ if ( statList.get(0) != 0 && statList.get(1) != 0)  return ""+toDate( statList.get(1)); else return "no record";  }
    @JmxGetter(name="Pack Duration")
    public String getPackDuration(){ if ( statList.get(0) != 0 && statList.get(1) != 0)  return ""+ (statList.get(1) - statList.get(0)); else return "no record";  }
    @JmxGetter(name="Backup Start Time")
    public String getBackupStartTime(){ if ( statList.get(2) != 0 && statList.get(3) != 0)  return ""+toDate(statList.get(2));  else return "no record"; }
    @JmxGetter(name="Backup End Time")
    public String getBackupEndTime(){ if ( statList.get(2) != 0 && statList.get(3) != 0)  return ""+toDate( statList.get(3));  else return "no record"; }
    @JmxGetter(name="Backup Duration")
    public String getBackupDuration(){ if ( statList.get(2) != 0 && statList.get(3) != 0)  return ""+ (statList.get(3) - statList.get(2));  else return "no record"; }
    @JmxGetter(name="Purge Start Time")
    public String getPurgeStartTime(){ if ( statList.get(4) != 0 && statList.get(5) != 0) return ""+toDate(statList.get(4)); else return "no record"; }
    @JmxGetter(name="Purge End Time")
    public String getPurgeEndTime(){ if ( statList.get(4) != 0 && statList.get(5) != 0) return ""+toDate( statList.get(5)); else return "no record"; }
    @JmxGetter(name="Purge Duration")
    public String getPuregeDuration(){ if ( statList.get(4) != 0 && statList.get(5) != 0)  return ""+ (statList.get(5) - statList.get(4)); else return "no record"; }
    @JmxGetter(name="Get current store info")
    public String getStoreInfo() {
        ChannelStore cs = store.getList().get( store.getCurIndex());
        return "key "+cs.getKeyOffset()+" data "+cs.getDataOffset();
    }

    @JmxGetter(name="Recent Count")
    public long getRecentCount() {
        return recentCacheStats.getRecentCount();
    }
    @JmxGetter(name="Recent Skip")
    public long getRecentSkips() {
        return recentCacheStats.getRecentSkips();
    }
    @JmxGetter(name="Recent Empty")
    public long getRecentEmpty(){
        return recentCacheStats.getRecentEmpty();
    }
    @JmxGetter(name="GetFrequency")
    public long getGetFreq() {
        return getFreq.get();
    }
    @JmxGetter(name="GetAvgTime")
    public long getGetTimes() {
        if ( getFreq.get() > 0)
            return getTimes.get()/ getFreq.get();
        else
            return 0;
    }
    @JmxGetter(name="PutFrequency")
    public long getPutFreq() {
        return putFreq.get();
    }
    @JmxGetter(name="PutAvgTime")
    public long getPutTimes() {
        if ( putFreq.get() > 0)
            return putTimes.get()/putFreq.get();
        else
            return 0;
    }
    @JmxGetter(name="RemoveFrequency")
    public long getRemoveFreq() {
        return removeFreq.get();
    }
    @JmxGetter(name="RemoveAvgTime")
    public long getRemoveTimes() {
        if ( removeFreq.get() > 0)
            return removeTimes.get() / removeFreq.get();
        else
            return 0;
    }

    @JmxGetter(name="Delay queue remain capacity")
    public int getDelayQueueCapacity() {

        if (store.getDelayWriteQueue() == null )
            return 0;
        else {
            return store.getDelayWriteQueue().remainingCapacity() ;
        }
    }

    @JmxOperation(description="validate link")
    public String validateLink() {
        return store.validateLink();
    }


    @JmxGetter(name="CacheMemory setting")
    public String getCache() {
        return store.getCacheMemoryStat() ;
    }

    @JmxOperation(description = "Dump DoubleLinkedList")
    public String dumpLinkQueue(){
        AccessQueue<Reference> list = store.getDoubleLinkedList();
        StringBuilder sb = new StringBuilder();
        if ( list != null ) {
            List<Reference> blockList = list.getAll();
            for ( int i = 0; i < blockList.size() ; i++) {
                //CacheBlock<byte[]> block = (CacheBlock<byte[]>)  blockList.get(i);
                Reference ref = blockList.get(i);
                sb.append(" # "+i+" "+  linkToString( ref) + " next"+ linkToString(ref.getNext()) +
                " prev"+ linkToString(ref.getPrev()) );

            }
        }
        logger.info("dump "+sb.toString() );
        return sb.toString();
    }

    private String linkToString(Reference ref){
        if ( ref == null ) return "null";
        else if ( ref instanceof CacheBlock )
            return " "+((CacheBlock )ref).getRecordNo() ;
        else return " "+ref.toString();

    }

    @JmxGetter(name="Writeback threads and status")
    public String getWriteBackThread() {
        StringBuffer toReturn =  new StringBuffer("threads "+writeBackThread) ;
        if ( writeBackThread > 0  &&  writeThreadList != null ) {
            for ( int i = 0 ; i < writeBackThread ; i ++ ) {
                toReturn.append(" t "+i+" "+ writeThreadList.get(i).getStat()+" ");
            }
        }
        return toReturn.toString();
    }

    private int writeBackThread = 0;
    private List<CacheStore.WriteBackThread> writeThreadList = null;

    @JmxOperation(description = "startWriteThread")
    public void startWriteThread(int no) {
        this.writeBackThread = no;
        writeThreadList = store.startWriteThread(no);
    }


    private Purge purge;
    @JmxOperation(description = "Purge data for storage.")
    public void purge() {
        statList.set(4, System.currentTimeMillis());
        if ( purge == null ) {
            logger.warn("no purge interface configured, process stop !!!");
        }
        else {
            new PurgeThread( purge, store ).run();
        }
        statList.set(5, System.currentTimeMillis());
    }

    public void setPurge(Purge purge) {
        logger.info("setting purge instance "+purge.toString() );
        this.purge = purge;
    }

    //1M records for log channel
    protected LinkedBlockingQueue<KeyValue> logQueue = new LinkedBlockingQueue<KeyValue>(MAX_RECORD);
    //protected Thread writeLogThread;
    //protected WriteLog writeLog;
    protected volatile boolean needLogThread = false;
    protected List<ReplicaClient> replicaClientList = new ArrayList<ReplicaClient>();
    //protected CacheStore trxLog;
    protected String logPath;
    protected LinkedBlockingQueue<KeyValue> pstQueue = new LinkedBlockingQueue<KeyValue>(MAX_RECORD);
    //protected CacheStore pstLog;
    protected volatile boolean needPstThread = false;
    //protected WriteLog writePst;

    /**
     *
     * @param logPath - for write ahead log which appends log path with current data path
     * @param urls - list of url for replica client, format (machine:port)
     */
    @JmxOperation(description = "startWriteLogThread")
    public void startWriteLogThread(String logPath, List<String> urls) {
        if ( needLogThread ) {
            logger.warn("WriteLogThread had been started!!!");
        }
        else {
            logger.info("Start writeLogThread path "+logPath);
            //make sure done path was created
            createPath(logPath + "/done");
            this.logPath = logPath;
            CacheStore trxLog = new CacheStore(logPath, null, 0, META_PREFIX+filename);
            WriteLog writeLog = new WriteLog(logPath, logQueue, trxLog, filename);
            Thread writeLogThread = new Thread( writeLog);
            writeLogThread.start();
            //replicaClientList = new ArrayList<ReplicaClient>( urls.freq());
            for (int i =0 ; i < urls.size() ; i++ ) {
                logger.info("Start replica client "+urls.get(i)+" "+store);
                ReplicaClient replicaClient = new NTReplicaClient(urls.get(i), filename, trxLog, logPath, i);
                Thread clientThread = new Thread(replicaClient, filename+"-"+i);
                replicaClientList.add(replicaClient);
                clientThread.start();
            }
            needLogThread = true;
        }
    }

    public void startPstLogThread(String logPath, List<String> urls){
        if ( needPstThread ) {
            logger.warn("PstLogThread had been started!!!");
        }
        else {
            logger.info("Start pstLogThread path "+logPath);
            //make sure done path was created
            createPath(logPath + "/done");
            this.logPath = logPath;
            CacheStore pstLog = new CacheStore(logPath, null, 0, META_PREFIX+filename+".pst");
            WriteLog writePst = new WriteLog(logPath, pstQueue, pstLog, filename+".pst");
            Thread writeLogThread = new Thread( writePst);
            writeLogThread.start();
            //replicaClientList = new ArrayList<ReplicaClient>( urls.freq());
            for (int i =0 ; i < urls.size() ; i++ ) {
                logger.info("Start pst replica client "+urls.get(i)+" "+store);
                logger.info("pst replica url "+urls.get(i));
                ReplicaClient replicaClient = new NTReplicaClient(urls.get(i), filename+".pst", pstLog, logPath, i);
                Thread clientThread = new Thread(replicaClient, filename+".pst"+"-"+i);
                replicaClientList.add(replicaClient);
                clientThread.start();
            }
            needPstThread = true;
        }
    }


    /**
      * make sure the path is exist, otherwise create it
      * @param path of data directory
      */
     protected void createPath(String path) {
         File file = new File(path);
         if ( file.isDirectory()) {
             return ;
         }
         else {
             if ( ! file.exists() ) {
                 if ( ! file.mkdirs() )
                     throw new RuntimeException("Fail to create path "+path);
             }
             else
                 throw new RuntimeException(path+" is a file");
         }
     }


    @JmxOperation(description = "stopReplicaClients")
    public void stopReplicaClients() {
        for (ReplicaClient client : replicaClientList ) {
            logger.warn("stop ReplicaClient "+client.toString());
            client.shutdown();
        }
    }


    @JmxOperation(description = "restartReplicaClients")
    public void restartReplicaClients() {
        int i = 0;
        for (ReplicaClient client : replicaClientList ) {
            logger.info("restart ReplicaClient "+client.toString());
            if ( client.restart() ) {
                Thread clientThread = new Thread(client, filename+"-"+i++);
                clientThread.start();
                logger.info("successful restart "+client.getUrl() );
            }
        }
    }

    @JmxOperation(description = "resetIndex")
    public void resetIndex(int index, String url, int record) {
        int i = 0;
        for (ReplicaClient client : replicaClientList ) {
            if ( client.getUrl().equals( url)) {
                logger.info("find url "+url+" shut down "+client.toString());
                client.shutdown();
                client.resetIndex(index, record);
                if ( client.restart() ) {
                    Thread clientThread = new Thread(client, filename+"-"+i++);
                    clientThread.start();
                    logger.info("successful restart "+client.getUrl() );
                }
            }
        }
    }


    public List<ReplicaClient> getReplicaClientList() {
        return replicaClientList;
    }

    @JmxGetter(name = "ReplicaClientInfo")
    public String getReplicaClientInfo() {
        if ( replicaClientList == null || replicaClientList.size() ==0 ) return "N/A";
        else {
            StringBuilder sb = new StringBuilder();
            for ( ReplicaClient each : replicaClientList ) {
                sb.append( each.toString() +"\t");
            }
            return sb.toString();
        }
    }


    @JmxOperation(description = "setReplicaClientBatchSize")
    public void setReplicaBatcSize(int size) {
        if ( replicaClientList != null && size > 0 ) {
            for (ReplicaClient each : replicaClientList) {
                logger.info("set batch freq "+size+" "+each.getUrl());
                each.setBatchSize( size);
            }
        }
    }

    /**
     * shutdown replica client
     */
    protected void shutdownClient() {
        if ( logQueue != null && logQueue.remainingCapacity() > 0 ) {
            logger.info("wait for 100 mini seconds to shutdown ReplicaClient");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                //swallow exception
            }
        }
        if (replicaClientList != null ) {
            for ( ReplicaClient each : replicaClientList ) {
                logger.warn("shutdown replica client ");
                each.shutdown();
            }
        }
    }

    @JmxGetter(name="RecentGetMetric")
    public String getGetMetric() {
        return "Get throughput "+recentCacheStats.recentGet+" avg in micro secs "+recentCacheStats.recentGetAvgTime;
    }

    @JmxGetter(name="RecentPutMetric")
    public String getPutMetric() {
        return "Put throughput "+recentCacheStats.recentPut+" avg in micro secs "+recentCacheStats.recentPutAvgTime;
    }

    @JmxGetter(name="RecentRemoveMetric")
    public String getRemoveMetric() {
        return "Remove throughput "+recentCacheStats.recentRemove+" avg in micro secs "+recentCacheStats.recentRemoveAvgTime;
    }

//    @JmxGetter(name ="WriteLogInfo")
//    public String getWriteLogInfo() {
//        if ( writeLog == null ) return "N/A";
//        else return writeLog.toString();
//    }


    private void moveWriteLogFiles() {
        if ( needLogThread && replicaClientList != null && replicaClientList.size() > 0) {
            for ( ReplicaClient each : replicaClientList) {
                try {
                    int no = findNo(each.getTrxStore());
                    if (no >= 0) {
                        for (; no >= 0; no--) {
                            if (!move2Done(no, each.getLogPath()))
                                break;
                            else
                                logger.info("successful move file " + filename + "." + no);
                        }
                    }
                } catch (Exception ex) {
                    logger.error("fail to moveWriteLogFiles " + ex.getMessage());
                }
            }
        }
    }
    /*
    keep the last tow files
     */
    private int findNo(CacheStore store) {
        int toReturn = -1 ;
        try {
            int idx = toInt( (Value <byte[]>) store.get(Key.createKey(filename+POSTFIX)) );
            for ( int i= 0 ; i < replicaClientList.size() ; i++ ) {
                int n = toInt( (Value <byte[]>) store.get(Key.createKey(filename+".lastIndex"+i)) );
                // first time for loop
                if ( toReturn < 0 ) toReturn = n ;
                else {
                    toReturn = Math.min( n, toReturn );
                }
            }
            //keep two files
            return (toReturn -2);
        } catch (Exception ex) {
            logger.error(ex.getMessage() );
            return -1;
        }
    }

    private boolean move2Done(int no, String logPath) {
        File source = new File(logPath+"/"+filename+"."+no+".key");
        File target;
        if (source.exists() ){
            target = new File(logPath+"/done/"+filename+"."+no+".key");
            if ( ! source.renameTo( target ) )
                logger.info("not able to move "+target.getName());
            else
                target.delete();
            source = new File(logPath+"/"+filename+"."+no+".ndx");
            target = new File(logPath+"/done/"+filename+"."+no+".ndx");
            if ( ! source.renameTo( target ) )
                logger.info("not able to move "+target.getName());
            else
                target.delete();
            source = new File(logPath+"/"+filename+"."+no+".data");
            target = new File(logPath+"/done/"+filename+"."+no+".data");
            if ( ! source.renameTo( target ) )
                logger.info("not able to move "+target.getName());
            else
                target.delete();
            return true;
        }
        else return false;
    }

  public class RecentCacheStats{

        private final CacheStore cacheStore;
        private Long millisecondsPerInterval = 5*60000L;
        private Timer timer = new Timer();

        private long recentCacheHits;
        private long recentCacheMisses;

        private long cumulativeCacheHits;
        private long cumulativeCacheMisses;
        // add writeThread
        private long recentSkips;
        private long recentEmpty;
        private long recentCount;
        private long cumSkips;
        private long cumEmpty;
        private long cumCount;
        //performance
        private long recentGet =0;
        private long recentGetAvgTime ;
        private long cumGet = 0;
        private long cumGetTime;
        private long recentPut =0;
        private long recentPutAvgTime ;
        private long cumPut =0 ;
        private long cumPutTime;
        private long recentRemove =0;
        private long recentRemoveAvgTime;
        private long cumRemove = 0;
        private long cumRemoveTime;

        public long getRecentCacheHits(){ return recentCacheHits; }
        public long getRecentCacheMisses(){ return recentCacheMisses; }
        public long getRecentSkips() {
            return recentSkips;
        }

        public long getRecentEmpty() {
            return recentEmpty;
        }

        public long getRecentCount() {
            return recentCount;
        }

        public RecentCacheStats(CacheStore store){
            this.cacheStore = store;

            timer.schedule(
                        new TimerTask(){
                            public void run(){
                                long currCacheHits = cacheStore.getCacheHit().get();
                                long currCacheMisses = cacheStore.getCacheMis().get();
                                recentCacheHits = currCacheHits - cumulativeCacheHits;
                                recentCacheMisses = currCacheMisses - cumulativeCacheMisses;
                                cumulativeCacheHits = currCacheHits;
                                cumulativeCacheMisses = currCacheMisses;
                                if ( writeBackThread > 0  &&  writeThreadList != null ) {
                                        long skips =0 , empty = 0 , count = 0;
                                        for ( int i = 0 ; i < writeBackThread ; i ++ ) {
                                            skips += writeThreadList.get(i).getDirtyNo() ;
                                            empty += writeThreadList.get(i).getEmptyNo();
                                            count += writeThreadList.get(i).getCount();
                                        }
                                        recentSkips = skips - cumSkips;
                                        recentEmpty = empty - cumEmpty;
                                        recentCount = count - cumCount;

                                        cumSkips = skips;
                                        cumEmpty = empty;
                                        cumCount = count;
                                } // if
                                if ( cumGet > 0 && getFreq.get() > cumGet )  {
                                    recentGet = getFreq.get() - cumGet;
                                    recentGetAvgTime = (getTimes.get() - cumGetTime) / recentGet ;
                                }
                                if ( cumPut >  0 &&  putFreq.get()  > cumPut)  {
                                    recentPut = putFreq.get() - cumPut;
                                    recentPutAvgTime = (putTimes.get() - cumPutTime ) / recentPut;
                                }
                                if ( cumRemove > 0 && removeFreq.get() > cumRemove ) {
                                    recentRemove = removeFreq.get() - cumRemove;
                                    recentRemoveAvgTime = ( removeTimes.get() - cumRemoveTime ) / recentRemove;
                                }
                                cumGet = getFreq.get();
                                cumPut = putFreq.get();
                                cumRemove = removeFreq.get();
                                cumGetTime = getTimes.get();
                                cumPutTime = putTimes.get();
                                cumRemoveTime = removeTimes.get();
                                // check replication log file
                                moveWriteLogFiles();
                            }
                        },
                        millisecondsPerInterval, millisecondsPerInterval
                );
            }

    }

}