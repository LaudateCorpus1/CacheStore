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
 */package com.sm.store;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.utils.ClassBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.BlockSize;

import java.io.Serializable;
import java.util.List;

public class StoreConfig implements Serializable {
    protected static final Log logger = LogFactory.getLog(StoreConfig.class);

    private String store;
    private String dataPath;
    //log frequency
    private int freq = 1 ;
    private List<String> replicaUrl;
    private int batchSize;
    private boolean delay ;
    private int delayThread = 2;
    private int mode = 0;
    private boolean sorted =false ;
    private String getTriggerName;
    private String putTriggerName;
    private String deleteTriggerName;
    protected volatile int maxCacheMemory ;
    //useMaxCache
    protected volatile boolean useMaxCache = false;
    protected volatile boolean useLRU = false;
    protected String logPath;
    protected BlockSize blockSize;
    protected long replicaTimeout;
    protected List<String> pstReplicaUrl;
    //protected String fileName;
    protected String purgeClass;
    private String serializeClass;
    private transient Serializer serializer;

    public StoreConfig(String store, String dataPath, int freq, List<String> replicaUrl) {
        this(store, dataPath, freq, replicaUrl, 10, true, 0);
    }

    public StoreConfig(String store, String dataPath, int freq, List<String> replicaUrl, int batchSize, boolean delay, int mode) {
        this(store, dataPath, freq, replicaUrl, batchSize, delay, mode, false);

    }

   public StoreConfig(String store, String dataPath, int freq, List<String> replicaUrl, int batchSize, boolean delay, int mode,
                      boolean sorted) {
        this.store = store;
        this.dataPath = dataPath;
        this.freq = freq;
        this.replicaUrl = replicaUrl;
        this.batchSize = batchSize;
        this.delay = delay;
        this.mode = mode;
        this.sorted = sorted;
    }

    public List<String> getPstReplicaUrl() {
        return pstReplicaUrl;
    }

    public void setPstReplicaUrl(List<String> pstReplicaUrl) {
        this.pstReplicaUrl = pstReplicaUrl;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public List<String> getReplicaUrl() {
        return replicaUrl;
    }

    public void setReplicaUrl(List<String> replicaUrl) {
        this.replicaUrl = replicaUrl;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean isDelay() {
        return delay;
    }

    public void setDelay(boolean delay) {
        this.delay = delay;
    }

    public int getDelayThread() {
        return delayThread;
    }

    public void setDelayThread(int delayThread) {
        this.delayThread = delayThread;
    }

    public boolean isSorted() {
        return sorted;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }
    public String getPutTriggerName() {
        return putTriggerName;
    }

    public void setPutTriggerName(String putTriggerName) {
        this.putTriggerName = putTriggerName;
    }

    public String getGetTriggerName() {
        return getTriggerName;
    }

    public void setGetTriggerName(String getTriggerName) {
        this.getTriggerName = getTriggerName;
    }

    public String getDeleteTriggerName() {
        return deleteTriggerName;
    }

    public void setDeleteTriggerName(String deleteTriggerName) {
        this.deleteTriggerName = deleteTriggerName;
    }

    public int getMaxCacheMemory() {
        return maxCacheMemory;
    }

    public void setMaxCacheMemory(int maxCacheMemory) {
        this.maxCacheMemory = maxCacheMemory;
    }

    public boolean isUseMaxCache() {
        return useMaxCache;
    }

    public void setUseMaxCache(boolean useMaxCache) {
        this.useMaxCache = useMaxCache;
    }

    public boolean isUseLRU() {
        return useLRU;
    }

    public void setUseLRU(boolean useLRU) {
        this.useLRU = useLRU;
    }

    public String getFileName() {
        return store;
    }

    public String getLogPath() {
        return logPath;
    }

    public Serializer getSerializer() {
        return this.serializer ;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setBlockSize(BlockSize blockSize) {
        this.blockSize = blockSize;
    }

    public BlockSize getBlockSize() {
        return  blockSize;
    }

    public long getReplicaTimeout() {
        return replicaTimeout;
    }

    public void setReplicaTimeout(long replicaTimeout) {
        this.replicaTimeout = replicaTimeout;
    }

    public String getPurgeClass() {
        return purgeClass;
    }

    public void setPurgeClass(String purgeClass) {
        this.purgeClass = purgeClass;
    }

    public String getSerializeClass() {
        return serializeClass;
    }

    /**
     * Create Serializer instance from class name string
     * @param serializeClass
     */
    public void setSerializeClass(String serializeClass) {
        this.serializeClass = serializeClass;
        if ( serializeClass != null ) {
            logger.info("generate serializer using "+serializeClass);
            this.serializer = (Serializer) ClassBuilder.createInstance(serializeClass);
        }
        else {
            logger.info("generate serializer using  HessianSerializer");
            this.serializer = new HessianSerializer();
        }
    }


}
