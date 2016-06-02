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

package com.sm.replica.client;

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.replica.Filter;
import com.sm.replica.ParaList;
import com.sm.store.OpType;
import com.sm.store.StoreParas;
import com.sm.transport.Client;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxGetter;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheStore;
import voldemort.store.cachestore.impl.LogChannel;
import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.sm.transport.Utils.ServerState;
import static voldemort.store.cachestore.voldeimpl.VoldeUtil.*;
import static voldemort.store.cachestore.voldeimpl.VoldeUtil.toValue;

@JmxManaged(description = "ReplicaClient")
public abstract class ReplicaClient implements Runnable {
    private static final Log logger = LogFactory.getLog(ReplicaClient.class);

    // name of storeName, use for server side to look up cachestore
    protected String storeName;
    // url to connect to server
    protected String url;
    // connection to server
    protected Client client;
    // for request sequence no
    protected AtomicLong seqno = new AtomicLong(1);
    // queue freq default
    protected LogChannel logChannel ;
    //private int currentRec;
    protected CacheStore trxStore;
    protected String logPath;
    protected Value<byte[]> lastIndex;
    protected Value<byte[]> lastRecord;
    protected Value<byte[]> index;
    protected volatile ServerState state;
    protected Key indexKey;
    protected Key lastIndexKey;
    protected Key lastRecordKey;
    protected long error =0;
    protected long timeout =0 ;
    protected Filter filter;


//    public ReplicaClient(String url, String storeName, CacheStore trxLog, String logPath, int idx) {
//        this( url, storeName, trxLog, logPath, idx, null);
//    }
//
//    public ReplicaClient(String url, String storeName, CacheStore trxLog, String logPath, int idx, Filter filter) {
//        this.url = url;
//        this.trxStore = trxLog;
//        this.storeName = storeName;
//        this.logPath = logPath;
//        this.filter = filter;
//        this.indexKey = Key.createKey(storeName+POSTFIX);
//        setKey(idx);
//        init(true);
//    }
//
//    public ReplicaClient(String url, String storeName, CacheStore trxLog, String logPath) {
//        this(url, storeName, trxLog, logPath, 0);
//    }

    protected void setKey(int idx) {
        lastIndexKey =  Key.createKey(storeName+".lastIndex"+idx);
        lastRecordKey =  Key.createKey(storeName+".lastRec"+idx);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    protected void init(boolean create) {
        if ( trxStore.get( lastIndexKey ) == null ) {
            updateLastIndex(0);
            updateLastRecord( 0);
            //lastIndex  = toValue( 0, 0);
            //lastRecord = toValue( 0, 0);
            //trxStore.put(lastIndexKey, lastIndex );
            //trxStore.put(lastRecordKey, lastRecord) ;
        }
        else {
            lastIndex = (Value<byte[]>) trxStore.get( lastIndexKey);
            lastRecord = (Value<byte[]>) trxStore.get( lastRecordKey);
            if ( lastRecord == null ) {
                logger.warn("last record is null for " + toInt(lastIndex));
                lastRecord = toValue( 0, 0);
            }
        }
        index = (Value<byte[]>) trxStore.get( indexKey);
        if ( index == null) {
            index = toValue( 0, 0);
        }
        logger.info("last index "+toInt(lastIndex)+" last record "+toInt(lastRecord));
        if ( create)
            logChannel = getLogChannel(toInt(lastIndex));
        int total = logChannel.getComputeTotal();
        logger.info(logChannel.getFilename()+" total records "+total);
        // total number  of record should not be zero

        if( total == 0 && toInt(lastIndex ) > 0 ) {
           logger.error("total record is 0 for "+logChannel.getFilename() );
        }
        else {
            if ( toInt(lastRecord) > total ) {
                logger.error("last record "+toInt(lastRecord ) +" > "+ total ) ;
                lastRecord = toValue( total, 0 );
            }
        }
        connect();
        this.state = ServerState.Active;
    }

    private final long sleepInterval = 500;
    protected abstract void connect();

    protected long findTimeout() {
        if ( timeout == 0 ) return 60000;
        else return timeout;
    }

    protected void sleep4Time() {
        error++;
        long reminder =  error % 10 ;
        try {
            Thread.sleep( reminder * sleepInterval);
        } catch (InterruptedException e) {
            //swaloow exception
        }
    }

    @JmxOperation(description = "shutdown replica client")
    public void shutdown(){
        logger.warn("shutdown for replica client "+toString());
        state = ServerState.Shutdown;
    }

    @JmxOperation(description ="restart replica client")
    public boolean restart() {
        logger.info("Restart "+toString());
        if (state == ServerState.Shutdown ) {
            init(true);
            if (state == ServerState.Active) logger.info("successful restart "+getUrl());
            return true;
        }
        else {
            logger.warn("can not restart, due to state ="+state);
            return false;
        }
    }

    public void resetIndex(int lastInd, int lastRec) {
        if ( lastInd > toInt(index) ) {
            logger.warn("lastIndex "+lastInd+" > "+toInt( index)+" will reset to index");
            lastInd = toInt(index);
        }
        if ( lastRec > MAX_RECORD ) {
            logger.warn("last record "+lastRec+" > "+MAX_RECORD);
            lastRec = MAX_RECORD ;
        }
        logger.info(" update trxStore lastIndex "+lastInd+" record "+lastRec );
        trxStore.put(lastIndexKey, toValue(lastInd, 0) );
        trxStore.put(lastRecordKey, toValue(lastRec, 0) );
    }

    private boolean isMoreIndex() {
        index = (Value<byte[]>) trxStore.get( indexKey);
        if ( index != null && toInt(index) > toInt(lastIndex ) ) return true;
        else return false;
    }

    private boolean isChannelFull(int curRecord) {
        if (  curRecord >= (MAX_RECORD - 1) ) return true;
        else return false;
    }

    public String getLogPath() {
        return logPath;
    }

    public CacheStore getTrxStore() {
        return trxStore;
    }

    public static final String PST = ".pst";
    private LogChannel getLogChannel(int no) {
        logger.info("trxStore "+trxStore.getNamePrefix()+" "+storeName );
        return  new LogChannel(storeName+"."+no, no, logPath);
    }

    public void run() {
        if (logChannel == null ) {
            logger.fatal("log channel is null. thread stop!!!!");
            return;
        }
        int i = 0 ;
        int retry = 0;
        while (true) {
            if ( state == ServerState.Shutdown ) {
                logger.warn("shutdown client and close store");
                trxStore.close();
                logChannel.close();
                return;
            }
            int curRecord = toInt(lastRecord );
            try {
                boolean EOF = logChannel.isEOF(curRecord );
                boolean moreIndex = isMoreIndex();
                if (EOF && ! moreIndex) {
                    if ( i++ % 600 == 0 && index != null)
                        logger.info("EOF "+logChannel.getFilename()+" "+logChannel.getComputeTotal()+" wait 10 minutes"+
                        " index "+toInt(index)+" last index "+toInt(lastIndex));
                    Thread.sleep(1000L);
                }
                else if (EOF && moreIndex) {
                    int no = toInt(lastIndex ) + 1;
                    logger.info("index "+toInt(index)+" last index "+toInt(lastIndex)+" last record "+toInt(lastRecord));
                    logChannel.close();
                    logChannel = getLogChannel(no);
                    logger.info("get new log channel "+logChannel.getFilename()+" no "+no);
                    // start with beginning
                    updateLastRecord( 0);
                    updateLastIndex( no);
                    //lastRecord = toValue( 0, 0);
                    //lastIndex = toValue( no, 0);
                    //trxStore.put(lastIndexKey,lastIndex );
                    //trxStore.put(lastRecordKey, lastRecord );
                }
                else {
                    List<StoreParas> list = createParasList(curRecord);
                    if ( list.size() == 0) {
                        logger.error("skip one record "+curRecord+ " of lastIndex "+ toInt(lastIndex));
                        updateLastRecord( curRecord +1 );
                        //lastRecord = toValue( curRecord + 1, 0);
                        //trxStore.put(lastRecordKey, lastRecord );
                    }
                    else if ( list.size() > 0 ) {
                        //must record size before apply filter if any
                        int s = list.size();
                        ParaList paraList;
                        //apply filter if it is defined
                        if ( filter != null )
                            paraList =  new ParaList(filter.applyFilter( list));
                        else
                            paraList = new ParaList( list);

                        int err = 0;
                        if ( list. size() > 0 ) {
                            // send out request after apply filter
                            Request request = new Request( createHeader(), paraList.toBytes() );
                            Response resp= sendRequest(request);
                            if ( resp.getPayload() instanceof ParaList ) {
                                err = listError((ParaList) resp.getPayload());
                            }
                            else {
                                err ++;
                                logger.error("response payload "+resp.getClass().getName()+" "+resp.getPayload().toString());
                            }
                            if ( err > 0  ) {
                                logger.warn("pList error " + err+" cur record# "+curRecord+" retry "+retry+" "
                                    + ( request == null ? "null" :request.getHeader().toString()) );
                            }
                        }
                        if ( err > 0  && retry < 3) {
                            // error and will retry not more than 3 times
                            retry ++;
                        }
                        else {
                            //reset retry when it is successful
                            retry = 0;
                            // check to rotate channel
                            if ( isChannelFull( s + curRecord) ) {
                                if ( isMoreIndex()) {
                                    int no = toInt(lastIndex ) + 1;
                                    logChannel.close();
                                    logChannel = getLogChannel(no);
                                    // start with beginning
                                    updateLastRecord( 0 );
                                    //lastRecord = toValue( 0, 0);
                                    updateLastIndex( no);
                                    //lastIndex = toValue( no, 0);
                                    //trxStore.put(lastIndexKey,lastIndex );
                                    //trxStore.put(lastRecordKey, lastRecord );
                                }
                                else {
                                   updateLastRecord( curRecord +s );
                                   //lastRecord = toValue( curRecord + s, 0 );
                                   //trxStore.put(lastRecordKey, lastRecord );
                                }

                            }
                            else {
                                updateLastRecord( curRecord +s );
                                //lastRecord = toValue( curRecord + s, 0);
                                //trxStore.put(lastRecordKey, lastRecord );
                            }
                        }
                    }

                }
            } catch( ConnectionException cnx) {
                try {
                    Thread.sleep(1000L);
                } catch( InterruptedException ie) {
                }
            } catch (StoreException se) {
                if ( se.getMessage() != null && se.getMessage().indexOf("time out")>=0 ) {
                    logger.info("close connection due to time out");
                    client.close();
                }
            } catch (Throwable th) {
                //swallow exception
                error++;
                logger.error( th.getMessage()+" cur# "+curRecord, th);
            }
        }
    }

    private void updateLastRecord(int record) {
        lastRecord = toValue( record, 0 );
        trxStore.put(lastRecordKey, lastRecord );
    }

    private void updateLastIndex(int no) {
        lastIndex = toValue( no, 0);
        trxStore.put(lastIndexKey, lastIndex);
    }

    private int listError(ParaList list){
        int error = 0;
        for (StoreParas each : list.getLists()) {
            // 1 is version obsolete, 2 store exception
            if ( each.getErrorCode() > StoreParas.OBSOLETE )
                error++;
        }
        return error;
    }

    private Response sendRequest(Request request) {
        if ( client != null && client.isConnected() ) return client.sendRequest( request);
        else {
            // release resource and create a new connection
            if ( client != null ) client.close();
            logger.info("try to reconnect "+url );
            connect();
            if ( client == null ) throw new ConnectionException("fail to connect "+url);
            return client.sendRequest( request );
        }

    }

    private int batchSize = 1;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getUrl() {
        return url;
    }

    @JmxGetter(name="Replica Client Info")
    @Override
    public String toString() {
        return url+ " "+logPath+" last index "+ toInt(lastIndex)+" rec# "+ toInt(lastRecord)+" cur index "
                +toInt(index)+" batch "+batchSize ;
    }

    // indicate payload type
    public static final byte PARAS_LIST = 1;
    public Header createHeader() {
        return new Header(storeName, seqno.getAndIncrement(), (byte) PARAS_LIST);
    }


    private List<StoreParas> createParasList(int curRecord) {
        List<StoreParas> list = new ArrayList<StoreParas>(batchSize);
        try {

            for ( int i = 0 ; i < batchSize ; i ++ ) {
                if ( logChannel.isEOF( curRecord +i ))
                    break;
                else {
                    KeyValue keyValue = logChannel.readRecord( curRecord+i);
                    StoreParas paras;
                    if ( keyValue.getValue() == null )
                        paras= new StoreParas(OpType.Remove, keyValue.getKey() );
                    else
                        paras = new StoreParas(OpType.Put, keyValue.getKey(), keyValue.getValue() );
                    list.add(paras);
                }
            }
        } catch ( Exception ex) {
            logger.error( ex.getMessage(), ex );
        } finally {
            return list;
        }
    }


}
