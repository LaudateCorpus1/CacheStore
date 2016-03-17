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

package com.sm.store.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.Service.State;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.*;
import com.sm.message.Request.RequestType;
import com.sm.query.ObjectQueryVisitorImpl;
import com.sm.query.QueryListenerImpl;
import com.sm.query.QueryVisitorImpl;
import com.sm.query.utils.QueryException;
import com.sm.storage.Serializer;
import com.sm.store.*;
import com.sm.store.client.RemoteValue;
import com.sm.store.server.RemoteStore;
import com.sm.utils.TupleThree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.utils.Pair;
import voldemort.versioning.ObsoleteVersionException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.sm.store.StoreParas.*;
import static com.sm.store.Utils.*;


public class StoreCallBack implements TCPCallBack {

    protected static final Log logger = LogFactory.getLog(StoreCallBack.class);

    protected Serializer serializer;
    protected ConcurrentMap<String, RemoteStore> storeMaps = new ConcurrentHashMap<String, RemoteStore>(11);
    //protected TCPServer server;
    protected State serverState;
    protected List<TupleThree<String, String, Integer>> storeList;
    protected ServerWSHandler WSHandler;
    protected boolean delay ;
    protected Serializer embeddedSerializer = new HessianSerializer();


    public StoreCallBack() {
    }

    /**
     *
     * @param storeList : list <Pair <storename, path>>
     * create map<storename, RemoteStore>
     */

    public StoreCallBack(List<TupleThree<String, String, Integer>> storeList) {
        this( storeList, null);
    }

    public StoreCallBack(List<TupleThree<String, String, Integer>> storeList, Serializer serializer)  {
        this(storeList, serializer, false);
    }

    public StoreCallBack(RemoteConfig remoteConfig ) {
        serializer = remoteConfig.getSerializer();
        storeList = new ArrayList<TupleThree<String, String, Integer>>();
        //@TODO will support by store in the future
        for (StoreConfig storeConfig : remoteConfig.getConfigList() ) {
            storeList.add( new TupleThree<String, String, Integer>(storeConfig.getStore(), storeConfig.getDataPath(),
                    storeConfig.getMode() ) );
            delay = storeConfig.isDelay();
        }
        init(delay);
        //add trigger routine
        for (StoreConfig storeConfig : remoteConfig.getConfigList() ) {
            RemoteStore store = storeMaps.get(storeConfig.getStore());
            if ( store != null)
                store.setupTrigger2Cache(storeConfig);
            else
                logger.warn( storeConfig.getStore()+" setup trigger failure");
        }
    }

    /**
     *
      * @param storeList
     * @param serializer
     * @param delay
     */
    public StoreCallBack(List<TupleThree<String, String, Integer>> storeList, Serializer serializer, boolean delay) {
        if (serializer == null)
             this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        //storeMaps = new ConcurrentHashMap<String, RemoteStore>(11);
        if ( storeList == null || storeList.size() == 0 ) throw new StoreException("store list is empty");
        //this.serverState = State.Start;
        this.storeList = storeList;
        init(delay);
    }

    private void init(boolean delay) {
        serverState = State.Start;
        int i = 0;
        for ( TupleThree<String, String, Integer> p : storeList) {
            try {
                RemoteStore store = new RemoteStore(p.getFirst(), serializer, p.getSecond(), delay, null, p.getThird());
                logger.info("put " +p.getFirst()+ " path "+ p.getSecond() );
                storeMaps.put( p.getFirst(), store);
                if ( delay) {
                    //passing parameter in the future, default to 2
                    logger.info("startWriteThread =2");
                    store.startWriteThread(2);
                }
            } catch (Exception ex) {
                // swallow exception
                logger.error(p.getFirst()+" "+ex.getMessage(), ex);
                i++;
            }
        }
        if ( i > 0 ) throw new StoreException("fail to open store "+i);
        //after it constructs storeMaps
        WSHandler = new ServerWSHandler(storeMaps);
    }

    public ConcurrentMap<String, RemoteStore> getStoreMaps() {
        return storeMaps;
    }

    public RemoteStore getRemoteStore(String store){
        return storeMaps.get( store);
    }

    /**
     * provide an way to overwrite ServerWSHandler to
     * handle dynamic class loader
     */
    public void setWSHandler(ServerWSHandler serverWSHandler) {
        this.WSHandler = serverWSHandler;
    }

    public ServerWSHandler getWSHandler() {
        return WSHandler;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public Serializer getEmbeddedSerializer() {
        return embeddedSerializer;
    }

    public boolean addStore(String storeName, String path, int mode) {
        int i = storeList.size();
        try {
            RemoteStore store = new RemoteStore(path, serializer, storeName, delay, null, mode);
            logger.info("put " +storeName+ " path "+ path );
            storeMaps.put( storeName, store);
            if ( delay) {
                //passing parameter in the future, default to 2
                logger.info("startWriteThread =2");
                store.startWriteThread(2);
            }
            return true;
        } catch (Exception ex) {
            // swallow exception
            logger.error(storeName+" "+ex.getMessage(), ex);
            return false;
        }
    }

    public boolean addStore(RemoteStore remoteStore) {
        if ( storeMaps.get(remoteStore.getStoreName()) == null ) {
            logger.info("add "+remoteStore.getStoreName()+" into storeMaps");
            storeMaps.put( remoteStore.getStoreName(), remoteStore);
            return true;
        }
        else
            return true;
    }

    @Override
    public Response processRequest(Request request) {
        Response resp;
      // check invoker type first
        if ( request.getType() == RequestType.Invoker ) {
            Invoker invoker = (Invoker) embeddedSerializer.toObject( request.getPayload());
            resp = WSHandler.invoke(invoker);
            serializerResponse( request.getHeader(), resp);
        }
        else if ( request.getType() == RequestType.KeyIterator) {
            resp = processKeyIterator( request);
            //resp.setPayload( embeddedSerializer.toBytes( resp.getPayload()));
            serializerResponse( request.getHeader(), resp);
        }
        else if ( request.getType() == RequestType.Scan) {
            resp = processScan( request);
            //resp.setPayload( embeddedSerializer.toBytes( resp.getPayload()));
            serializerResponse( request.getHeader(), resp);

        }

        else {
            StoreParas paras = processStoreParas( request);
            boolean err = ( paras.getErrorCode() == NO_ERROR ? false : true );
            resp = new Response(paras, err);
        }
        return resp;
    }

    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * check type of header SerializableType
     * @param header
     * @param response
     */
    protected void serializerResponse(Header header, Response response) {
        switch (header.getSerializableType()) {
            case Json:
                String result;
                try {
                    result = mapper.writeValueAsString(response.getPayload());
                } catch (JsonProcessingException jsp) {
                    logger.error( jsp.getMessage(), jsp);
                    result = "ERROR :"+jsp.getMessage();
                }
                response.setPayload( embeddedSerializer.toBytes( result));
                break;
            case PassThrough:
                if ( ! (response.getPayload() instanceof byte[]) )
                    response.setPayload( embeddedSerializer.toBytes( response.getPayload()));
                break;
            default:
                response.setPayload( embeddedSerializer.toBytes( response.getPayload()));
        }
    }

    protected StoreParas processStoreParas(Request request) {
        RemoteStore store = storeMaps.get( request.getHeader().getName() );
        StoreParas paras = StoreParas.toStoreParas(request.getPayload());
        if ( store == null ) {
            paras.setErrorCode(STORE_EXCEPTION);
            checkValue( paras);
            paras.getValue().setData(("Store "+request.getHeader().getName()+" not exits").getBytes() );
            logger.warn("Store " + request.getHeader().getName() + " not exits");

        }
        else
            processParas( paras, store);
        return paras;

    }

    protected Response processKeyIterator(Request request) {
        RemoteStore store = storeMaps.get( request.getHeader().getName() );
        if ( store == null ) throw new StoreException("Store "+request.getHeader().getName()+" not exits");
        Iterator<Key> it = store.getKeyIterator();
        List<Key> toReturn = new ArrayList<Key>();
        while (it.hasNext() ) {
            toReturn.add( it.next());
            if (toReturn.size() > 5000 ) {
                logger.warn("getIterator size over 5000");
                break;
            }
        }
        Response resp = new Response(toReturn);
        return resp;
    }

    protected void processParas(StoreParas paras, RemoteStore store) {
        Value value;
        try {
            switch( paras.getOpType() ) {
                case Get: {
                    value = processGet(store, paras.getKey() );
                    paras.setErrorCode(NO_ERROR );
                    paras.setValue(value);
                    break;
                }
                case Put: {
                    processPut(store, paras.getKey(), paras.getValue());
                    paras.setErrorCode( NO_ERROR);
                    paras.setValue(null);
                    break;
                }
                case Remove: {
                    boolean rs = processRemove(store, paras.getKey());
                    paras.setErrorCode( NO_ERROR);
                    paras.setRemove( rs ? (byte) 1 : (byte) 0);
                    break;
                }
                case GetSeqNoInt: {
                    value = processGetSeqNoInt(store, paras.getKey());
                    paras.setErrorCode(NO_ERROR);
                    paras.setValue( value);
                    break;
                }
                case GetSeqNoBlockInt: {
                    value = processGetNoBlockInt(store, paras);
                    paras.setErrorCode(NO_ERROR);
                    paras.setValue( value);
                    break;
                }
                case GetSeqNo: {
                    value = processGetSeqNo(store, paras.getKey());
                    paras.setErrorCode(NO_ERROR);
                    paras.setValue( value);
                    break;
                }
                case GetSeqNoBlock: {
                    value = processGetNoBlock(store, paras);
                    paras.setErrorCode(NO_ERROR);
                    paras.setValue( value);
                    break;
                }
                case Insert: {
                    processInsert(store, paras.getKey(), paras.getValue());
                    paras.setErrorCode( NO_ERROR);
                    paras.setValue(null);
                    break;
                }
                case SelectQuery: {
                    value = processSelectQuery(store, paras.getKey(), paras.getValue());
                    paras.setValue(value);
                    break;
                }
                case UpdateQuery: {
                    processUpdateQuery(store, paras.getKey(), paras.getValue());
                    paras.setValue(null);
                    paras.setErrorCode(NO_ERROR);
                    break;
                }
                default:  {
                    paras.setErrorCode( STORE_EXCEPTION);
                    checkValue( paras);
                    paras.getValue().setData( "unknown type".getBytes());
                }
            }
        } catch( ObsoleteVersionException sx) {
            paras.setErrorCode( OBSOLETE);
            paras.getValue().setData( sx.getMessage().toString().getBytes());

        } catch (Exception ex) {

            paras.setErrorCode( STORE_EXCEPTION);
            byte[] data ;
            if ( ex.getMessage() != null )
                data = ex.getMessage().getBytes();
            else
                data = new byte[0];
            checkValue( paras);
            paras.getValue().setData(data );
            logger.error(ex.getMessage(), ex);
            //paras.getValue().setData( ex.getMessage().toString().getBytes());
            // decide error code
        } finally {
           // return paras;
        }
    }


    // make sure value is not null
    protected void checkValue(StoreParas paras) {
        if ( paras.getValue() == null  ) {
            paras.setValue(CacheValue.createValue( new byte[0]));
        }
    }


    protected Value processGet(RemoteStore store, Key key) {
        return store.get(key);
    }

    protected void processInsert(RemoteStore store, Key key, Value value) {
         if ( store.getStore().getMap().containsKey(key ) ) {
            logger.info("key "+key.toString()+ " insert should fail");
            throw new StoreException("insert failure due to existing key "+key.toString());
        }
        else
            processPut(store, key, value);
    }

    protected void processPut(RemoteStore store, Key key, Value value) {
        store.put(key, value);

    }

    protected boolean processRemove(RemoteStore store, Key key){
        return store.remove(key);
    }

    protected Value processSelectQuery(RemoteStore store, Key key, Value value){
        String queryStr = new String( (byte[]) value.getData());
        Value v = store.get(key);
        if ( v == null ) throw new StoreException("value is null key "+key.toString() );
        Object source = serializer.toObject( (byte[]) v.getData());
        ObjectQueryVisitorImpl visitor = new ObjectQueryVisitorImpl(queryStr);
        v.setData( serializer.toBytes(visitor.runQuery(source)));
        return v;
    }

    protected void processUpdateQuery(RemoteStore store, Key key, Value value){
        String queryStr = new String( (byte[]) value.getData());
        Value v = store.get(key);
        if ( v == null ) throw new StoreException("value is null key "+key.toString() );
        Object source = serializer.toObject( (byte[]) v.getData());
        ObjectQueryVisitorImpl visitor = new ObjectQueryVisitorImpl(queryStr);
        visitor.runQuery(source);
        //v.setVersion( v.getVersion() +1);
        value.setData( serializer.toBytes( visitor.getSource()));
        store.put( key, value);
    }


    protected Value processGetSeqNoInt(RemoteStore store, Key key) {
        Value v = store.get(key);
        if ( v == null ) {
            // must exist first, will not create
            throw new RuntimeException(key.toString()+" did not exist");
        }
        else {
            Value toReturn;
            synchronized ( v) {
                addOne(v);
                store.put(key, v);
                //clone a new value
                toReturn = CacheValue.createValue( (byte[]) v.getData(), v.getVersion(), v.getNode());
            }
            return toReturn;
        }
    }

    protected Value processGetSeqNo(RemoteStore store, Key key){
       Value v = store.get(key);
        if ( v == null ) {
            throw new RuntimeException(key.toString()+" did not exist");

        }
        else {
            Value toReturn;
            synchronized ( v) {
                addOneLong(v);
                store.put(key, v);
                //clone a new value before it return
                toReturn = CacheValue.createValue( (byte[]) v.getData(), v.getVersion(), v.getNode());
            }
            return toReturn;
        }
    }

    protected Value processGetNoBlock(RemoteStore store,StoreParas paras){
        Key key = paras.getKey();
        Value v = store.get(key);
        if ( v == null || paras.getValue() == null) {
            throw new RuntimeException(paras.getKey().toString()+" did not exist or paras value is null");
        }
        else {

            Value toReturn;
            synchronized ( v) {
                addBlockLong(v, paras.getValue());
                store.put(key, v);
                //clone a new value before it return
                toReturn = CacheValue.createValue( (byte[]) v.getData(), v.getVersion(), v.getNode());
            }
            return toReturn;
        }
    }

    protected Value processGetNoBlockInt(RemoteStore store,StoreParas paras){
        Key key = paras.getKey();
        Value v = store.get(key);
        if ( v == null || paras.getValue() == null) {
            throw new RuntimeException(paras.getKey().toString()+" did not exist or paras value is null");
        }
        else {

            Value toReturn;
            synchronized ( v) {
                addBlockInt(v, paras.getValue());
                store.put(key, v);
                //clone a new value before it return
                toReturn = CacheValue.createValue( (byte[]) v.getData(), v.getVersion(), v.getNode());
            }
            return toReturn;
        }
    }

    protected Response processScan(Request request) {
        Object msg= embeddedSerializer.toObject(request.getPayload());
        RemoteStore store = storeMaps.get( request.getHeader().getName() );
        Response response;
        KeyValueParas keyValueParas = null;
        if (msg instanceof KeyValueParas) {
            KeyValueParas kVParas = (KeyValueParas) msg;
            if ( kVParas.getOpType() == OpType.QueryStatement) {
                QueryListenerImpl queryListener = new QueryListenerImpl( kVParas.getQueryStr() );
                queryListener.walkTree();
                QueryIterator queryIterator = new QueryIterator( queryListener.getPredicateStack(), queryListener.isTableScan(),
                    store);
                List<KeyValue> list = processFullQuery( queryIterator, kVParas.getQueryStr(), store );
                response = new Response( new KeyValueParas( OpType.QueryStatement, list, kVParas.getQueryStr()));
            }
            else
                response = processQueryPut( kVParas, store );
        }
        else if (msg instanceof KeyParas) {
            KeyParas keyParas = (KeyParas) msg;
            List<KeyValue> list;
            if ( keyParas.getOpType() == OpType.MultiRemoves) {
                list = store.multiRemove( keyParas.getList());
                keyValueParas = new KeyValueParas(keyParas.getOpType(), list);
            }
            else {
                list = store.multiGets(keyParas.getList());
                if ( keyParas.getOpType() == OpType.MultiSelectQuery)
                    keyValueParas = processQueryStr( list, keyParas.getQueryStr());
                else
                    keyValueParas = new KeyValueParas(keyParas.getOpType(), list);
            }
            response = new Response( keyValueParas );
        }
        else if (msg instanceof StoreParas) {
            StoreParas storeParas = (StoreParas) msg;
            processParas(storeParas, store);
            response = new Response( storeParas);
        }
        else
            throw new StoreException("payload type does not exits "+msg.getClass().getName() );

        return response;
    }
    //maximum return size of select statement
    public final static int MAX_SIZE = 5000;

    protected List<KeyValue> processFullQuery(QueryIterator queryIterator, String queryStr, RemoteStore store){
        List<KeyValue> list = new ArrayList<KeyValue>();
        int empty = 0, error = 0 ;
        QueryVisitorImpl visitor = new QueryVisitorImpl(queryStr);
        while ( queryIterator.hasNext()) {
            try {
                Pair<Key, Value> pair = queryIterator.next();
                if (pair.getSecond() != null && pair.getSecond().getData() != null) {
                    Object source = serializer.toObject((byte[]) pair.getSecond().getData());
                    visitor.setKey( pair.getFirst() );
                    Object result = visitor.runQuery(source);
                    if ( result != null ) {
                        if (visitor.getStatementType() == QueryVisitorImpl.StatementType.Select) {
                            //need to create a new instance of Value, but use RemoteValue, not CacheValue
                            Value value = new RemoteValue(result, pair.getSecond().getVersion(), pair.getSecond().getNode());
                            list.add(new KeyValue(pair.getFirst(), value));
                            // check size
                            if (list.size() > MAX_SIZE) {
                                logger.warn("list size exceed "+MAX_SIZE+" for "+queryStr+" store "+store.getStore().getNamePrefix());
                                return list;
                            }
                        } else {
                            //deserialize the data and increment version
                            pair.getSecond().setData(serializer.toBytes(visitor.getSource()));
                            pair.getSecond().setVersion(pair.getSecond().getVersion() + 1);
                            store.put(pair.getFirst(), pair.getSecond());
                        }
                    }
                    else empty++;
                } else empty++;
            } catch (Exception ex) {
                //swallow exception
                error++;
                logger.error( ex.getMessage(), ex);
            }
        }
        logger.info("error "+error+" null value "+empty+ " for "+queryStr);
        return list;
    }

    protected KeyValueParas processQueryStr(List<KeyValue> list, String queryStr) {
        return processQueryStr(list, queryStr, OpType.MultiSelectQuery);
    }

    protected KeyValueParas processQueryStr(List<KeyValue> list, String queryStr, OpType opType) {
        if ( queryStr != null && queryStr.length() > 0) {
            ObjectQueryVisitorImpl visitor =  new ObjectQueryVisitorImpl(queryStr) ;
            for ( KeyValue each : list) {
                try {
                    if ( each.getValue() == null || each.getValue().getData() == null  )
                        continue;
                    Object source = serializer.toObject((byte[]) each.getValue().getData());
                    visitor.runQuery(source);
                    if (visitor.getSelectObj() != null) {
                        each.getValue().setData(serializer.toBytes(visitor.getSource()));
                    }
                } catch (Exception ex) {
                    logger.error( ex.getMessage() +" key "+each.getKey().toString(), ex);
                }
            }

        }
        return new KeyValueParas( opType, list);

    }

    /**
     * will deprecate MultiSelectQuery. just for backward compatibility
     * @param keyValueParas
     * @param store
     * @return
     */

    protected Response processQueryPut(KeyValueParas keyValueParas, RemoteStore store) {
        List<KeyValue> list = new ArrayList<KeyValue>( keyValueParas.getList().size());
        KeyValueParas kvs = null;
        if ( keyValueParas.getOpType() == OpType.MultiPuts) {
            list = store.multiPuts(keyValueParas.getList());
            kvs = new KeyValueParas( OpType.MultiPuts, list);
        }
        else if ( keyValueParas.getOpType() == OpType.MultiSelectQuery ) {
            ObjectQueryVisitorImpl visitor = new ObjectQueryVisitorImpl(keyValueParas.getQueryStr());
            for ( KeyValue keyValue : keyValueParas.getList()) {
                try {
                    Value v = store.get( keyValue.getKey());
                    if ( v != null ) {
                        Object source = serializer.toObject((byte[]) v.getData());
                        Object result = visitor.runQuery( source);
                        if ( result != null) {
                            v.setData(serializer.toBytes(visitor.getSource() ));
                            list.add(new KeyValue(keyValue.getKey(), v));
                        }
                    }
                } catch ( Exception ex) {
                    String msg = ex.getMessage() == null ? "null" : ex.getMessage();
                    list.add(new KeyValue(keyValue.getKey(), CacheValue.createValue( embeddedSerializer.toBytes(msg),
                            0, (short) 0) ));
                }
            }
            kvs = new KeyValueParas( OpType.MultiSelectQuery, list);
        }
        else if (keyValueParas.getOpType() == OpType.MultiUpdateQuery) {
            ObjectQueryVisitorImpl visitor = new ObjectQueryVisitorImpl(keyValueParas.getQueryStr());
            for ( KeyValue keyValue : keyValueParas.getList()) {
                try {
                    Value v = store.get( keyValue.getKey());
                    if ( v != null ) {
                        Object source = serializer.toObject((byte[]) v.getData());
                        visitor.runQuery( source);
                        v.setData(serializer.toBytes(visitor.getSource()));
                        v.setVersion( v.getVersion()+1);
                        store.put( keyValue.getKey(), v);
                        list.add( new KeyValue( keyValue.getKey(), null));
                    }
                } catch ( Exception ex) {
                    String msg = ex.getMessage() == null ? "null" : ex.getMessage();
                    list.add(new KeyValue(keyValue.getKey(), CacheValue.createValue( embeddedSerializer.toBytes(msg),
                            0, (short) 0) ));
                }
            }
            kvs = new KeyValueParas( OpType.MultiUpdateQuery, list);
        }
        else   //error
            throw new QueryException("Wrong query type "+ keyValueParas.getOpType());

       return new Response(kvs);
    }


}
