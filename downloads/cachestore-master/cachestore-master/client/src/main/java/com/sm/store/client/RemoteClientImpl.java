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
 */package com.sm.store.client;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Header;
import com.sm.message.Invoker;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import com.sm.store.KeyValueParas;
import com.sm.store.OpType;
import com.sm.store.RemotePersistence;
import com.sm.store.StoreParas;
import com.sm.transport.Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.versioning.ObsoleteVersionException;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.sm.store.Utils.*;

public abstract class RemoteClientImpl implements RemotePersistence {
    private static final Log logger = LogFactory.getLog(RemoteClientImpl.class);

    // name of store, use for server side to look up cachestore
    protected String store;
    //separate by , format host:port
    protected String url;
    //client side only, server send back byte[]
    protected Serializer serializer;
    // connection to server
    protected Client client;
    // for request sequence no
    protected AtomicLong seqno = new AtomicLong(1);
    // index
    protected int index = -1;
    //url in array format
    protected String[] urlArrays;
    //embedded HessianSerializer provided by the system
    protected Serializer embeddedSerializer = new HessianSerializer();
    protected boolean nio = true;
    protected long timeout ;
    protected Header.SerializableType type = Header.SerializableType.Default;


    protected void init() {
        for ( String each : urlArrays) {
            String str[] = each.split(":");
            if (str.length != 2) throw new RuntimeException("Malform url "+ each);
        }
        getTCPClient();
    }

    protected abstract void getTCPClient() ;

    protected Client getClient() {
        return client;
    }

    protected void setClient(Client client) {
        this.client = client;
    }

    public String getStore() {
        return store;
    }

    public void setType(Header.SerializableType type) {
        this.type = type;
    }

    protected Response sendRequest(Request request) {
        try {
            if ( client != null && client.isConnected() ) return client.sendRequest( request);
            else {
                // release resource and create a new connection
                if ( client != null ) client.close();
                logger.info("try to reconnect "+ url);
                init();
                if ( client == null ) throw new RuntimeException("fail to connect "+ url);
                return client.sendRequest( request );
            }
        } catch (Exception ex) {
            throw new StoreException( ex.getMessage(), ex);
        }

    }




    @Override
    public void put(Key key, Object data) {
        Value value = new RemoteValue( data, 0L, (short) 0);
        put(key, value ) ;
    }

    protected Request createRequest(byte[] payload, Request.RequestType requestType) {
        Request request =new Request(new Header(store, seqno.getAndIncrement(), (byte)0, 1) , payload, requestType);
        request.getHeader().setSerializableType( type);
        return request;
    }

    @Override
    public void put(Key key, Value value) {
        updateQuery(key, value, null);
    }

    public void updateQuery(Key key, Value value, String queryStr) {
        //increment version when it is not version 0
        if ( value.getVersion() != 0)
            value.setVersion( value.getVersion() +1);
        StoreParas paras ;
        if ( queryStr == null )
            paras =new StoreParas(OpType.Put, key, createCacheValue(value, serializer));
        else {
            Value v =createCacheValue( value, serializer);
            v.setData( queryStr.getBytes());
            paras = new StoreParas( OpType.UpdateQuery, key, v);
        }
        put( key, paras);
    }

    public void sendPayload(byte[] bytes) {
        Request request = createRequest( bytes, Request.RequestType.Normal);
        try {
            Response response = sendRequest( request );
        } catch (RuntimeException ex) {
            logger.error( ex.getMessage(), ex);
            throw ex;
        }
    }

    public void put4Php(Key key, Value value) {
        put(key, value);
    }

    private void put(Key key, StoreParas storeParas) {
        byte[] payload = storeParas.toBytes();
        Request request = createRequest( payload, Request.RequestType.Normal);
        try {
            Response response = sendRequest( request );
            StoreParas paras = ((StoreParas) response.getPayload());
            if ( paras.getErrorCode() == StoreParas.OBSOLETE) {
                throw new ObsoleteVersionException("key "+key.getKey().toString());
            }
            else if ( paras.getErrorCode() == StoreParas.STORE_EXCEPTION ) {
                throw new StoreException("key "+key.getKey().toString()) ;
            }
        } catch (RuntimeException ex) {
            logger.error( ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * put4Value is primary to put Value into KeyStore
     * KeyStore value is encoded different, It bypass the serializer
     * use Utils.putLong and putInt to encode byte[], use RemoteValue to construct value object
     * @param key
     * @param value
     */
    public void put4Value(Key key, Value value) {
        StoreParas paras = new StoreParas(OpType.Put, key, value);
        put( key, paras);
    }

    /**
     * return the value and bypass the serializer
     * @param key
     * @return Value without calling serializer.toObject()
     */
    public Value get4Php(Key key){
        StoreParas paras = new StoreParas(OpType.Get, key, null);
        byte[] payload = paras.toBytes();
        Request request = createRequest(payload, Request.RequestType.Normal);
        Response response = sendRequest( request );
        paras = (StoreParas) response.getPayload() ;
        if ( paras.getValue() == null ) return null;
        else return paras.getValue();
    }


    @Override
    public Value get(Key key) {
        return selectQuery(key, null);
    }

    public Value selectQuery(Key key, String queryStr) {
        StoreParas paras;
        if ( queryStr == null )    //from get with queryStr == null and OpType assigned accordingly
            paras = new StoreParas(OpType.Get, key, null);
        else
            paras = new StoreParas(OpType.SelectQuery, key, new RemoteValue(queryStr.getBytes(), 0 , (short) 0));
        byte[] payload = paras.toBytes();
        Request request = createRequest(payload, Request.RequestType.Normal);
        Response response = sendRequest( request );
        paras = (StoreParas) response.getPayload() ;
        if ( paras.getValue() == null ) return null;
        return createRemoteValue(paras.getValue(), serializer );

    }

    public List<KeyValue> query(String queryStr){
        KeyValueParas keyValueParas = new KeyValueParas( OpType.QueryStatement, null, queryStr);
        byte[] payload = embeddedSerializer.toBytes( keyValueParas);
        Request request = createRequest(payload, Request.RequestType.Scan);
        Response response = sendRequest( request);
        if ( response.getPayload() instanceof KeyValueParas) {
            return ((KeyValueParas) response.getPayload()).getList();
        }
        else
            throw new StoreException("Error "+response.getPayload().getClass().getName() );
    }

    public String query4Json(String queryStr){
        KeyValueParas keyValueParas = new KeyValueParas( OpType.QueryStatement, null, queryStr);
        byte[] payload = embeddedSerializer.toBytes( keyValueParas);
        Request request = createRequest(payload, Request.RequestType.Scan);
        request.getHeader().setSerializableType(Header.SerializableType.Json);
        Response response = sendRequest( request);
        if ( response.getPayload() instanceof String)
            return (String) response.getPayload();
        else
            throw new StoreException("Error "+response.getPayload().getClass().getName());
    }


    /**
     *
     * @param key
     * @param value
     * throw store exception if key is exist
     */
    @Override
    public void insert(Key key, Value value) {
        CacheValue v = createCacheValue(value, serializer);
        StoreParas paras = new StoreParas(OpType.Insert, key, v);
        byte[] payload = paras.toBytes();
        Request request = createRequest( payload, Request.RequestType.Normal);
        try {
            Response resp = sendRequest( request );
        } catch (RuntimeException ex) {
            logger.error( ex.getMessage(), ex);
            throw ex;
        }
    }


    @Override
    public boolean remove(Key key) {
        StoreParas paras = new StoreParas(OpType.Remove, key, null);
        byte[] payload = paras.toBytes();
        Request request = createRequest(payload, Request.RequestType.Normal);
        try {
            Response response = sendRequest( request );
            paras = (StoreParas) response.getPayload() ;
            if ( paras.isRemove() ) return true;
            else return false;
        } catch (RuntimeException ex) {
            logger.error( ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public void close() {
        logger.info("close client connection");
        client.close();
        client.shutdown();
    }

    @Override
    public Iterator getKeyIterator() {
        logger.info("getKeyIterator() does nothing on client side");
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void pack(int rate) {
        logger.info("pack() does nothing on client side");
    }

    @Override
    public long getSeqNo(Key key) {
        StoreParas paras = getSequence(OpType.GetSeqNo, key);
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) paras.getValue().getData());
        return buf.getLong();
    }

    public int getSeqNoInt(Key key){
        StoreParas paras = getSequence(OpType.GetSeqNoInt, key);
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) paras.getValue().getData());
        return buf.getInt();
    }

    @Override
    public long getSeqNoBlock(Key key, int block) {
        StoreParas paras = getSequence(OpType.GetSeqNoBlock, key, block);
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) paras.getValue().getData());
        return buf.getLong();
    }

    @Override
    public int getSeqNoBlockInt(Key key, int block) {
        StoreParas paras = getSequence(OpType.GetSeqNoBlockInt, key, block);
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) paras.getValue().getData());
        return buf.getInt();
    }

    private StoreParas getSequence(OpType opType, Key key) {
        return getSequence(opType, key, -1);
    }

    private StoreParas getSequence(OpType opType, Key key, int block){
        StoreParas paras = new StoreParas(opType, key);
        // if block > 0, set Value to be block
        if ( block > 0 ) {
            Value v = CacheValue.createValue( putInt(block) , 0 ) ;
            paras.setValue( v);
        }
        byte[] payload = paras.toBytes();
        Request request = createRequest(payload, Request.RequestType.Normal);
        Response response = sendRequest( request );
        return (StoreParas) response.getPayload() ;
    }

    public Object invoke(Invoker invoker) {
        Request request = createRequest( embeddedSerializer.toBytes(invoker), Request.RequestType.Invoker);
        //request.setType(RequestType.Invoker );
        Response response = sendRequest( request );
        return response.getPayload();
    }

    @Override
    public void backup(String path, int rate) {
        logger.info("backup() does nothing on client side");
    }

    public List<Key> getKeyList() {
        Request request = createRequest(embeddedSerializer.toBytes(Key.createKey("getKeyList")), Request.RequestType.KeyIterator);
        //request.setType( RequestType.KeyIterator);
        Response response = sendRequest( request);
        return (List<Key>) response.getPayload();
    }

    @Override
    public String toString() {
        return "url "+ url+ " store "+store+ " timeout "+timeout+" nio "+nio;
    }

}
