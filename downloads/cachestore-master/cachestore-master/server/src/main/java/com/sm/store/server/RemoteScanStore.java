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

package com.sm.store.server;

import com.sm.query.QueryListenerImpl;
import com.sm.query.QueryVisitorImpl;
import com.sm.storage.Serializer;
import com.sm.store.Cursor;
import com.sm.store.CursorPara;
import com.sm.store.ScanPersistence;
import com.sm.store.client.RemoteValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.*;
import voldemort.store.cachestore.impl.SortedCacheStore;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.utils.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.atomic.AtomicLong;

import static voldemort.store.cachestore.voldeimpl.VoldeUtil.checkPath;

public class RemoteScanStore extends RemoteStore implements ScanPersistence {
    protected static final Log logger = LogFactory.getLog(RemoteScanStore.class);
    protected AtomicLong seqno = new AtomicLong(1);
    protected ConcurrentMap<Long, Cursor> cursorMap = new ConcurrentHashMap<Long, Cursor>(119);
    private CleanUpCursor cleanUpCursor;

    public RemoteScanStore(String filename, Serializer serializer, int mode) {
        this(filename, serializer, false, mode);
    }

    public RemoteScanStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode) {
        this(filename, serializer, path, delay, blockSize, mode, false);
    }

    public RemoteScanStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode, boolean isSorted) {
        //super(filename, serializer, path, delay, blockSize, mode, isSorted);
        this.path = path;
        this.filename = filename;
        this.serializer = serializer;
        this.delay = delay;
        this.blockSize = blockSize;
        this.mode =mode;
        this.isSorted = isSorted;
        init();
    }

     public RemoteScanStore(String filename, Serializer serializer, boolean delay, int mode) {
         this(filename, serializer, delay, mode, false);
     }

    public RemoteScanStore(String filename, Serializer serializer, boolean delay, int mode, boolean isSorted) {
        String[] names = checkPath(filename);
        this.filename = names[1];
        this.serializer = serializer;
        this.path = names[0];
        this.delay = delay;
        this.mode = mode;
        this.isSorted = isSorted;
        init();
    }

    protected void init() {
        super.init();
        logger.info("Starting clean up cursor thread");
        this.cleanUpCursor = new CleanUpCursor( 6000);

    }

    public boolean isSorted() {
        return isSorted ;
    }

    @Override
    public List<KeyValue> scan(Key from, Key to) {
        try {
            List<KeyValue> list = getSortedStore().scan(from, to);
            if ( list == null ) return new ArrayList<KeyValue>();
            else return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ArrayList<KeyValue>();
        }
    }


    public  List<KeyValue> scan(Key from) {
        return scan(from, from);
    }

    public SortedCacheStore getSortedStore() {
        if (isSorted)
            return  (SortedCacheStore) getStore();
        else
            throw new StoreException("cache store did not support scan, make sure stores.xml sorted=true");
    }

    public void nextCursor(CursorPara cursorPara) {
        Cursor serverCursor = null;
        try {
            if (cursorPara.isStop()) {
                serverCursor = cursorMap.get(cursorPara.getCursorId());
                if (serverCursor == null)
                    logger.warn("try to close cursor but " + cursorPara.getCursorId() + " not in the map");
                else {
                    logger.info("close cursor for " + cursorPara.getCursorId());
                    cursorMap.remove(cursorPara.getCursorId());
                }
            } else if (cursorPara.isStart()) {
                if (cursorMap.containsKey(cursorPara.getCursorId())) {
                    // get Cursor which has iterator from
                    serverCursor = cursorMap.get(cursorPara.getCursorId());
                } else {
                    logger.warn("isStart true but " + cursorPara.getCursorId() + " not in the map");
                    throw new StoreException(cursorPara.getCursorId() + " is not in map");
                }
            } else {
                serverCursor = new Cursor(cursorPara.getStore(), seqno.getAndIncrement(), cursorPara.getBatchSize(),
                        cursorPara.getFrom(), cursorPara.getTo());
                Cursor tmp = cursorMap.putIfAbsent(serverCursor.getCursorId(), serverCursor);
                if (tmp != null)
                    serverCursor = tmp;
                else {
                    if (cursorPara.getCursorType() == CursorPara.CursorType.SelectQuery)
                        serverCursor.setIterator(createQueryIterator(cursorPara.getQueryStr()));
                    else
                        serverCursor.setIterator(getIterator(cursorPara));

                }
            }
        } catch (Exception ex) {
            logger.error( ex.getMessage(), ex);
            //set CursorPara to emptyList, end to true and return to caller
            cursorPara.setKeyValueList( new ArrayList<KeyValue>());
            cursorPara.setEnd( true);
            return ;
        }
        synchronized ( serverCursor) {
            cursorPara.setCursorId( serverCursor.getCursorId());
            if ( cursorPara.getCursorType() == CursorPara.CursorType.SelectQuery)
                cursorPara.setKeyValueList(nextBatchQuery(serverCursor, cursorPara));
            else
                cursorPara.setKeyValueList( nextBatch(serverCursor, cursorPara /*.getCursorType()*/ ));

            cursorPara.setStart( serverCursor.isStart());
            cursorPara.setEnd( serverCursor.isEnd());
        }
    }


    private QueryIterator createQueryIterator(String queryStr) {
        QueryListenerImpl queryListener = new QueryListenerImpl( queryStr );
        queryListener.walkTree();
        return new QueryIterator( queryListener.getPredicateStack(), queryListener.isTableScan(), this);
    }


    protected Iterator getIterator(CursorPara cursorPara) {
        if ( cursorPara.getCursorType() == CursorPara.CursorType.Scan) {
            Key floor = getSortedStore().findFloorKey(getSortedStore().getSkipListMap(), cursorPara.getFrom());
            Key ceil = getSortedStore().findCeilKey(getSortedStore().getSkipListMap(), cursorPara.getTo() );
            //empty map return null
            if ( floor == null || ceil == null ) {
                return null;
            }
            ConcurrentNavigableMap<Key, CacheBlock> subMap = getSortedStore().getSkipListMap().subMap(floor, true, ceil, true);
            return subMap.keySet().iterator();
        } else {
            if ( isSorted )
                return getSortedStore().getSkipListMap().keySet().iterator();
            else
                return getStore().getMap().keySet().iterator();
        }
    }

    protected List<KeyValue> nextBatchQuery(Cursor cursor, CursorPara cursorPara){
        List<KeyValue> list = new ArrayList<KeyValue>(cursor.getBatchSize());
        //check empty map
        if ( cursor.getIterator() == null ){
            cursor.setEnd(true);
            return list;
        }
        QueryIterator iterator = (QueryIterator) cursor.getIterator();
        QueryVisitorImpl visitor = new QueryVisitorImpl(cursorPara.getQueryStr());
        int i =0;
        for ( ; i < cursor.getBatchSize() ; /*i++*/) {
            if (iterator.hasNext()) {
                try {
                    Pair<Key, Value> pair = iterator.next();
                    if (pair.getSecond() != null && pair.getSecond().getData() != null) {
                        Object source = serializer.toObject((byte[]) pair.getSecond().getData());
                        visitor.setKey(pair.getFirst());
                        Object result = visitor.runQuery(source);
                        if (result != null) {
                            i++;
                            if (visitor.getStatementType() == QueryVisitorImpl.StatementType.Select) {
                                //need to create a new instance of Value, but use RemoteValue, not CacheValue
                                Value value = new RemoteValue(result, pair.getSecond().getVersion(), pair.getSecond().getNode());
                                list.add(new KeyValue(pair.getFirst(), value));
                            } else {
                                //deserialize the data and increment version
                                pair.getSecond().setData(serializer.toBytes(visitor.getSource()));
                                pair.getSecond().setVersion(pair.getSecond().getVersion() + 1);
                                store.put(pair.getFirst(), pair.getSecond());
                            }
                        }
                    }
                } catch ( Exception ex) {
                    //swallow exception
                    logger.error( ex.getMessage(), ex);
                }
            }
            else {
                cursor.setEnd(true);
                break;
            }
        }
        //update currentRecord and set LastTime
        cursor.setCurrentRecord( cursor.getCurrentRecord() + i);
        cursor.setLastTime( System.currentTimeMillis() );
        return list;
    }


    protected List<KeyValue> nextBatch(Cursor cursor, CursorPara cursorPara) {
        List<KeyValue> toReturn = new ArrayList<KeyValue>(cursor.getBatchSize());
        //check empty map
        if ( cursor.getIterator() == null ){
            cursor.setEnd(true);
            return toReturn;
        }
        int i =0;
        for ( ; i < cursor.getBatchSize() ; /*i++*/) {
            if (cursor.getIterator().hasNext() ) {
                Key key = (Key) cursor.getIterator().next();
                if ( key != null &&  !skipKey(key, cursorPara.getFrom(), cursorPara.getTo(), cursorPara.getCursorType()) ) {
                    Value value = get(key);
                    // check cursorType and increment when it find value
                    if ( value != null ) {
                        i++;
                        if ( cursorPara.getCursorType() == CursorPara.CursorType.KeySet )
                            toReturn.add(new KeyValue(key, null));
                        else
                            toReturn.add( new KeyValue( key, value) );
                    }
                    else
                        logger.info("iterator cursor id "+cursor.getCursorId()+" key "+key.toString()+" not in the map");
                }
            }
            else {
               cursor.setEnd( true); ;
               break;
            }
        }
        //update currentRecord and set LastTime
        cursor.setCurrentRecord( cursor.getCurrentRecord() + i);
        cursor.setLastTime( System.currentTimeMillis() );
        return toReturn;
    }

    // if key < from or key > to
    //return false if it want to bypass skip function for not scan type
    private boolean skipKey(Key key, Key from, Key to, CursorPara.CursorType type) {
        if ( type != CursorPara.CursorType.Scan  ) return false;
        else
            return  ((Comparable) from).compareTo( (Comparable) key ) > 0 ||
                ((Comparable) to).compareTo( (Comparable) key ) < 0  ;
    }

    public class CleanUpCursor {
        private Long interval = 30* 60* 1000L;
        private Timer timer = new Timer();
        //time to live, cursor ideal more than 60 minutes
        private long toLive = 60 * 60 * 1000;
        public CleanUpCursor(long intervalMiniSeconds) {
            if ( intervalMiniSeconds > 5* 60*1000)
                this.interval = intervalMiniSeconds ;
            timer.schedule(
                    new TimerTask(){
                        public void run(){
                            try {
                                if ( cursorMap != null && cursorMap.size() > 0) {
                                    Iterator iterator = cursorMap.entrySet().iterator();
                                    while (iterator.hasNext()) {
                                        Map.Entry<Long, Cursor> entrySet =   (Map.Entry< Long, Cursor>) iterator.next();
                                        Cursor cursor = entrySet.getValue();
                                        synchronized (cursor) {
                                            if ( cursor.isStart() && cursor.getLastTime() + toLive < System.currentTimeMillis() ) {
                                                logger.info("expire cursor "+cursor.getCursorId()+" last access "+new Date(cursor.getLastTime()).toString()
                                                    +" remove from map");
                                                cursor.setEnd( true);
                                                cursorMap.remove( entrySet.getKey() );
                                            }
                                        }
                                    }

                                }
                                else
                                    logger.info("cursorMap size is zero");
                            } catch (Throwable th) {
                                logger.error(th.getMessage(), th);
                            }
                        }
                    }, this.interval, this.interval
            );
        }
    }
}
