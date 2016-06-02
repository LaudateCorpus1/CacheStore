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
 */package com.sm.store.server;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.localstore.impl.LocalStoreImpl;
import com.sm.storage.Serializer;
import com.sm.store.*;
import com.sm.store.loader.ThreadLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxGetter;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.*;
import voldemort.store.cachestore.impl.CacheStore;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.store.cachestore.impl.Purge;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.utils.ReflectUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An Remote store is primary for remote Client, value.getData() is a serialized byte[]
 * which imply an CacheValue
 */
@JmxManaged(description = "Remote store")
public class RemoteStore extends LocalStoreImpl implements StorePersistence {
    private static Log logger = LogFactory.getLog(RemoteStore.class);
    //private ReplicaServer replicaServer = null ;
    //GetTrigger default to be null
    protected volatile GetTrigger getTrigger;
    //PutTrigger
    protected volatile PutTrigger putTrigger;
    //delete trigger
    protected volatile DeleteTrigger deleteTrigger;
    protected volatile Delta delta;
    //queue time out
    public final static long DELAY_TIMEOUT = 200L;
    protected Serializer embeddedSerializer = new HessianSerializer();
    protected ThreadLoader threadLoader = new ThreadLoader();
    protected String getTriggerName;
    protected String putTriggerName;
    protected String deleteTriggerName;
    //add CacheBlock for pstQueue. It must be either delta or blockValue
    protected volatile boolean blockValue = false;


    public RemoteStore() {
        super();
    }

    public RemoteStore(String filename, Serializer serializer, int mode) {
        super(filename, serializer, mode);

    }

    public RemoteStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode) {
        super(filename, serializer, path, delay, blockSize, mode);
    }

    public RemoteStore(String filename, Serializer serializer, boolean delay, int mode) {
        super(filename, serializer, delay, mode);
    }

    public RemoteStore(CacheStore cacheStore, boolean delay, int mode) {
        super(cacheStore, delay, mode);
        this.serializer = new HessianSerializer();
    }
    @JmxGetter(name="GetTrigger")
    public GetTrigger getGetTrigger() {
        return getTrigger;
    }

    @JmxGetter(name="PutTrigger")
    public PutTrigger getPutTrigger() {
        return putTrigger;
    }

    @JmxOperation(description = "setGetTrigger")
    public void setGetTrigger(String triggerName){
        setGetTrigger( triggerName, null);
    }

    public void setGetTrigger(String triggerName, StoreConfig storeConfig) {
        try {
            getTriggerName = triggerName;
            if (storeConfig == null) {
                getTrigger = (GetTrigger) threadLoader.loadTrigger( triggerName);
            }
            else {
                Class cls = Class.forName(triggerName);
                getTrigger = checkProxy(cls, storeConfig);
            }
        } catch ( Exception ex) {
            logger.error( ex.getMessage());
        }
    }

    private GetTrigger checkProxy(Class cls, StoreConfig storeConfig) throws Exception {
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        if ( constructors.length == 1 && constructors[0].getParameterTypes().length == 2 &&
                cls.getName().endsWith("RebalanceProxy")) {
            Constructor<?> constructor = constructors[0];
            return (GetTrigger) constructor.newInstance( new Object[] { this, storeConfig } );
        }
        else
            return (GetTrigger) cls.newInstance();
    }
    @JmxOperation(description = "setPutTrigger")
    public void setPutTrigger(String triggerName) {
        try {
            putTriggerName = triggerName;
            //putTrigger = (PutTrigger) Class.forName(triggerName).newInstance();
            putTrigger = (PutTrigger) threadLoader.loadTrigger( triggerName);
        } catch ( Exception ex) {
            logger.error( ex.getMessage());
        }
    }

    @JmxGetter(name="DeleteTrigger")
    public DeleteTrigger getDeleteTrigger() {
        return deleteTrigger;
    }

    @JmxOperation(description = "setDeleteTrigger")
    public void setDeleteTrigger(String triggerName) {
        try {
            deleteTriggerName = triggerName;
            //deleteTrigger = (DeleteTrigger) Class.forName(triggerName).newInstance();
            deleteTrigger = (DeleteTrigger) threadLoader.loadTrigger(triggerName);
        } catch ( Exception ex) {
            logger.error( ex.getMessage());
        }
    }

    public Delta getDelta() {
        return delta;
    }

    public void setDelta(Delta delta) {
        this.delta = delta;
    }

    public void setBlockValue(boolean blockValue) {
        this.blockValue = blockValue;
    }

    /**
     * look up storeConfig to determine if trigger had been defined or not
     * if it is, load each interface implementation use Class.forName
     * also if useMaxCache set to true, it use FIFO queue and  getMaxCacheMemory to configure the cache by number
     * of objects, not the capacity of memory bytes
     * useLRU flag to determine LRU algorithm had been used or not
     * all implementations ars located at Scan4CallBack handler when request message wree processed
     * @param storeConfig
     */
    public void setupTrigger2Cache(StoreConfig storeConfig) {
        try {
            //use system properties to pass store name to rebalance trigger if needed
            //it is reset as each store change
            System.setProperty("store", storeConfig.getStore());
            if ( storeConfig.getGetTriggerName() != null ) {
                logger.info("hookup getTrigger "+storeConfig.getGetTriggerName());
                setGetTrigger(storeConfig.getGetTriggerName(), storeConfig);
                //check if RebalanceProxy
            }
            if ( storeConfig.getPutTriggerName() != null ) {
                logger.info("hookup putTrigger "+storeConfig.getPutTriggerName() );
                setPutTrigger( storeConfig.getPutTriggerName());
            }
            if ( storeConfig.getDeleteTriggerName() != null ) {
                logger.info("hookup deleteTrigger "+storeConfig.getDeleteTriggerName() );
                setDeleteTrigger(storeConfig.getDeleteTriggerName());
            }
            if ( storeConfig.isUseMaxCache() ) {
                logger.info("enable useMaxCache, object cache size " + storeConfig.getMaxCacheMemory() +" useLRU is " +storeConfig.isUseLRU());
                //dependency call setMaxCacheMemory before startDrainQueue
                store.setMaxCacheMemory( storeConfig.getMaxCacheMemory());
                logger.info("startDrainQueue ....");
                store.startDrainQueue();
            }
            //adding purge instance
            String purgeClassName = storeConfig.getPurgeClass();
            if ( purgeClassName != null ) {
                logger.info("using purge interface of "+ purgeClassName );
                Class purgeClass = ReflectUtils.loadClass(purgeClassName);
                Purge purge = (Purge) purgeClass.newInstance() ;
                setPurge( purge);
            }

        } catch ( Exception ex) {
            logger.error( ex.getMessage(), ex);
        }
    }

    @JmxOperation(description = "reloadTrigger")
    public void reloadTrigger() {
        try {
            logger.info("reload Trigger");
            if (getTriggerName != null && getTrigger != null)
                getTrigger = (GetTrigger) threadLoader.reloadTrigger(getTriggerName, getTrigger);
            if (putTriggerName != null && putTrigger != null)
                putTrigger = (PutTrigger) threadLoader.reloadTrigger(putTriggerName, putTrigger);
            if (deleteTriggerName != null && deleteTrigger != null)
                deleteTrigger = (DeleteTrigger) threadLoader.reloadTrigger(deleteTriggerName, deleteTrigger);
        } catch ( Exception ex) {
            logger.error( ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param logPath - path for WAG
     * @param urls - urls for replica client
     */
    @JmxOperation(description = "startReplica")
    public void startReplica(String logPath, List<String> urls) {
        if ( logPath != null ) {
            if ( ! logPath.startsWith("/"))
                logPath = path+"/"+logPath;
            logger.info("Start write log thread and path "+logPath);
            if (urls != null )
                startWriteLogThread(logPath, urls);
            else
                logger.error("replica.client.url property is null and fail to start client");
        }
    }

    public void startPstReplica(String logPath, List<String> urls) {
        if ( logPath != null ) {
            if ( ! logPath.startsWith("/"))
                logPath = path+"/"+logPath;
            logger.info("Start pst write log thread and path "+logPath);
            if (urls != null )
                startPstLogThread(logPath, urls);
            else
                logger.error("pst.replica.client.url property is null and fail to start client");
        }
    }


    /**
     * close remote server, replica client and server if it existed
     */
    public void close() {
        super.close();
        shutdownClient();
    }
    /**
     *
     * @param key
     * @param value CacheValue
     */
    @Override
    public void put(Key key, Value value) {
        Value old = null;
        if ( needPstThread && delta != null) {
            old = super.getRemote(key);
        }
        if ( putTrigger == null)
            super.put( key, value);
        else {
            putTrigger.beforePut(key, value, store);
            super.put(key, value);
            putTrigger.afterPut(key, value, store );
        }
        if (needLogThread ) {
            try {
                boolean flag = logQueue.offer( new KeyValue(key, value), DELAY_TIMEOUT, TimeUnit.MILLISECONDS);
                if ( ! flag ) logger.error("fail to add put log queue key "+key.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        if ( needPstThread ) {
            if ( delta != null) {
                // compute the delta and return pstValue
                try {
                    Value pstValue = delta.compute(value, old);
                    logger.info("delta compute value pstValue " + (pstValue == null ? "null" : pstValue.getData().toString()));
                    if (pstValue != null) {
                        boolean flag = pstQueue.offer(new KeyValue(key, pstValue), DELAY_TIMEOUT, TimeUnit.MILLISECONDS);
                        if (!flag) logger.error("fail to add pstValue to queue key " + key.toString());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            else if ( blockValue ) {
                try {
                    int record = super.getStore().getMap().get(key).getRecordNo();
                    // stream to another node, need to preserver record #, no need for version #
                    Value bv = CacheValue.createValue((byte[]) value.getData(), value.getVersion(),value.getNode());
                    //call encode version and record to version which is long; record is in first 32 bits
                    bv.setVersion( encodeRV( bv.getVersion(), record));
                    boolean flag = pstQueue.offer(new KeyValue(key, bv), DELAY_TIMEOUT, TimeUnit.MILLISECONDS);
                    if (!flag) logger.error("fail to add bv to pstQueue key " + key.toString());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }


            }
        }
    }

    public static long encodeRV(long version, int record) {
        long rv = (long) record << 32;
        return ( rv | ( 0x0000000FFFFFFFFL & version) );
    }

    public static int decodeRecord(long rv) {
        int value = (int) ((rv & 0xFFFFFFFF00000000L) >>> 32);
        return value;
    }

    public static long decodeVersion(long rv) {
        return  0x0000000FFFFFFFFL & rv ;
    }


    /**
     *
     * @param key
     * @return true for success and false for failure
     * @throws StoreException
     */
    @Override
    public boolean remove(Key key) throws StoreException {
        Value old = null;
        if ( needPstThread ) {
            old = super.getRemote(key);
        }
        if ( deleteTrigger != null ) {
            deleteTrigger.beforeDelete(key, store);
        }
        boolean toReturn = super.remove( key);
        if ( needPstThread) {
            if (delta != null) {
                // compute the delta and return pstValue
                try {
                    Value pstValue = delta.compute(null, old);
                    logger.info("delta compute value pstValue " + (pstValue == null ? "null" : pstValue.getData().toString()));
                    if (pstValue != null) {
                        boolean flag = pstQueue.offer(new KeyValue(key, pstValue), DELAY_TIMEOUT, TimeUnit.MILLISECONDS);
                        if (!flag) logger.error("fail to add put log queue key " + key.toString());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else if (blockValue) {
                try {
                    int record = super.getStore().getMap().get(key).getRecordNo();
                    // stream to another node, need to preserver record #, no need for version #
                    Value bv = CacheValue.createValue( null, record, (short) 0);
                    boolean flag = pstQueue.offer(new KeyValue(key, bv), DELAY_TIMEOUT, TimeUnit.MILLISECONDS);
                    if (!flag) logger.error("fail to add bv to pstQueue key " + key.toString());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
        if (needLogThread ) {
            try {
                boolean flag = logQueue.offer( new KeyValue(key, null), DELAY_TIMEOUT, TimeUnit.MILLISECONDS);
                if ( ! flag ) logger.error("fail to add remove log queue  key "+key.toString());
            } catch (InterruptedException e) {
                logger.error( e.getMessage(), e);
            } finally {
                return toReturn;
            }
        }
        else
            return toReturn;


    }

    /**
     *
     * @param key
     * @return an CacheValue
     */
    @Override
    public Value get(Key key) {
        if ( getTrigger == null)
            return super.getRemote(key);
        else {
            getTrigger.beforeGet(key, store);
            Value value = super.getRemote(key);
            return getTrigger.afterGet(key, value, store);
        }
    }

    /**
     * insert will perform get to make sure key did not exist
     * @param key
     * @param value
     */
    @Override
    public void insert(Key key, Value value) {
        if ( super.containsKey(key ))
            throw new StoreException("insert failure due to existing key "+key.toString());
        else
            put(key, value);
    }

    /**
     * we don't support this for remote client
     * @param key
     * @param object
     */
    @Override
    public void put(Key key, Object object){
        throw new StoreException("Remote store did not support value as Object");
    }

    /**
     *
     * @param keys
     * @return List of KeyValue
     */
    public List<KeyValue> multiGets(List<Key> keys) {
        ArrayList<KeyValue> list = new ArrayList<KeyValue>();
        for (Key key : keys) {
            Value value = get(key);
            list.add( new KeyValue( key, value));
        }
        return list;
    }

    /**
     *
     * @param keys - list of key
     * @return list which only contain successful, value == null
     *         if it fail, it will not return any thing
     */

    public List<KeyValue> multiRemove(List<Key> keys){
        ArrayList<KeyValue> list = new ArrayList<KeyValue>();
        for (Key key : keys) {
            if ( remove(key))
                list.add( new KeyValue( key, null));
        }
        return list;
    }

    /**
     *
     * @param list of KeyValue
     * @return success value is null, failure value will be string of error message
     */
    public List<KeyValue> multiPuts(List<KeyValue> list) {
        List<KeyValue> toReturn = new ArrayList<KeyValue>( list.size());
        for (KeyValue keyValue : list) {
            try {
                put(keyValue.getKey(), keyValue.getValue() );
                //success set value to boolean
                toReturn.add(new KeyValue(keyValue.getKey(), null));
            } catch (Exception ex) {
                String msg = ex.getMessage() == null ? "null" : ex.getMessage();
                logger.error(msg, ex);
                toReturn.add(new KeyValue(keyValue.getKey(), CacheValue.createValue( embeddedSerializer.toBytes(msg),
                        0, (short) 0)) );
            }
        }
        return toReturn;
    }


}
