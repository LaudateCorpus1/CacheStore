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

package com.sm.store.client.netty;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import com.sm.store.*;
import com.sm.store.client.RemoteClientImpl;
import com.sm.store.client.netty.ScanClientHandler;
import com.sm.transport.netty.TCPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.util.ArrayList;
import java.util.List;



public class ScanClientImpl extends RemoteClientImpl implements ScanPersistence {
    private static final Log logger = LogFactory.getLog(ScanClientImpl.class);


    public ScanClientImpl(){}

    public ScanClientImpl(String url, Serializer serializer, String store) {
        this(url, serializer, store, 1000);
    }

    public ScanClientImpl(String url, Serializer serializer, String store, int queueSize) {
        this( url, serializer, store, queueSize, true);
    }

    public ScanClientImpl(String url, Serializer serializer, String store, int queueSize, boolean nio) {
        this(url, serializer, store, queueSize, nio, 6000L);
    }

    public ScanClientImpl(String url, Serializer serializer, String store, int queueSize, boolean nio, long timeout) {
        this.url = url;
        this.store = store;
        this.nio = nio ;
        this.timeout = timeout;
        if (serializer == null)
            this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        urlArrays = url.split(",");
        init();
    }

    protected void getTCPClient() {
        int count = urlArrays.length;
        for ( int i = 0; i < count; i++ ) {
            index = (++index) % count ;
            try {
                String[] str = urlArrays[index].split(":");
                if (nio)
                    setClient(TCPClient.start(str[0], Integer.valueOf(str[1]), new ScanClientHandler(timeout, serializer)));
                else
                    setClient(TCPClient.startOio(str[0], Integer.valueOf(str[1]), new ScanClientHandler(timeout, serializer)));

                break;
            } catch (RuntimeException ex) {
                logger.warn("fail to connect to "+ url);
            }
        }
    }


    /**
     *
     * @param from
     * @param to
     * @return List<KeyValeu></KeyValeu>
     */
    @Override
    public List<KeyValue> scan(Key from, Key to) {
        List<Key> keyList = new ArrayList<Key>(2);
        keyList.add(from); keyList.add(to);
        ScanParaList scanParaList = new ScanParaList(OpType.Get, keyList);
        //KeyParas keyParas = new KeyParas(keyList);
        Request request =  createRequest(embeddedSerializer.toBytes(scanParaList), Request.RequestType.Scan);
        try {
            Response response = sendRequest( request );
            if ( ! response.isError()) {
                List<KeyValue> list = ((KeyValueParas) response.getPayload()).getList();
                return list;
            }
            else {
                logger.error( (String) response.getPayload());
                return null ;
            }
        } catch (RuntimeException ex) {
                logger.error(ex.getMessage(), ex );
                return null;
        }
    }

    public List<KeyValue> scan(Key from, Key to, String queryStr) {
        List<Key> keyList = new ArrayList<Key>(2);
        keyList.add(from); keyList.add(to);
        ScanParaList scanParaList = new ScanParaList(OpType.Get, keyList, queryStr);
        //KeyParas keyParas = new KeyParas(keyList);
        Request request =  createRequest(embeddedSerializer.toBytes(scanParaList), Request.RequestType.Scan);
        try {
            Response response = sendRequest( request );
            if ( ! response.isError()) {
                List<KeyValue> list = ((KeyValueParas) response.getPayload()).getList();
                return list;
            }
            else {
                logger.error( (String) response.getPayload());
                return null ;
            }
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage(), ex );
            return null;
        }
    }


    /**
     *
     * @param from
     * @return List<KeyValue>
     */
    public List<KeyValue> scan(Key from) {
        return scan(from, from);
    }

    /**
     *
     * @param keys
     * @return list which only contain successful, value == null
     *         if it fail, it will not return any thing
     */
    public List<KeyValue> multiRemoves(List<Key> keys) {
        KeyParas keyParas = new KeyParas(OpType.MultiRemoves, keys);
        Request request = createRequest( embeddedSerializer.toBytes(keyParas), Request.RequestType.Scan);
        Response response = sendRequest( request );
        if ( response.isError())
            throw new RuntimeException( (String) response.getPayload() );
        else {
            List<KeyValue> list = ((KeyValueParas) response.getPayload()).getList();
            return list;
        }
    }

    /**
     * batch get
     * @param keys List<Key>
     * @return List<KeyValue>
     */
    @Override
    public List<KeyValue> multiGets(List<Key> keys) {
        KeyParas keyParas = new KeyParas(OpType.MultiGets, keys);
        Request request = createRequest( embeddedSerializer.toBytes(keyParas), Request.RequestType.Scan);
        try {
            Response response = sendRequest( request );
            if ( ! response.isError()) {
                List<KeyValue> list = ((KeyValueParas) response.getPayload()).getList();
                return list;
            }
            else {
               logger.error( (String) response.getPayload());
                return null ;
            }

        } catch (RuntimeException ex) {
            logger.error(ex.getMessage(), ex );
            return null;
        }
    }

    public List <KeyValue> multiSelectQuery(List<Key> keys, String queryStr) {
        KeyParas keyValueParas =  new KeyParas(OpType.MultiSelectQuery, keys, queryStr);
        Request request = createRequest( embeddedSerializer.toBytes(keyValueParas), Request.RequestType.Scan);
        Response response = sendRequest( request );
        return ((KeyValueParas) response.getPayload()).getList();
    }

    private KeyValueParas makeKeyValueParas(List<Key> keys, String queryStr, OpType optye) {
        List<KeyValue> list = new ArrayList<KeyValue>( keys.size());
        for ( Key each : keys) {
            list.add( new KeyValue( each, null));
        }
        return new KeyValueParas(optye, list, queryStr);
    }

    public List<KeyValue> multiUpdateQuery(List<Key> keys, String queryStr) {
        KeyValueParas keyValueParas =  makeKeyValueParas(keys, queryStr, OpType.MultiUpdateQuery);
        Request request = createRequest( embeddedSerializer.toBytes(keyValueParas), Request.RequestType.Scan);
        Response response = sendRequest( request );
        if ( response.isError())
            throw new RuntimeException( (String) response.getPayload() );
        else
            return ((KeyValueParas) response.getPayload()).getList() ;
    }

    public List<KeyValue> multiValueUpdateQuery(List<KeyValue> keyValues, String queryStr) {
        KeyValueParas keyValueParas =  makeKeyValueParaVersion(keyValues, OpType.MultiUpdateQuery, queryStr);
        Request request = createRequest( embeddedSerializer.toBytes(keyValueParas), Request.RequestType.Scan);
        Response response = sendRequest( request );
        if ( response.isError())
            throw new RuntimeException( (String) response.getPayload() );
        else
            return ((KeyValueParas) response.getPayload()).getList() ;
    }

    /**
     * perform batch put
     * @param list of KeyValue
     */
    @Override
    public List<KeyValue> multiPuts(List<KeyValue> list) {
        KeyValueParas keyValueParas = makeKeyValueParaVersion(list, OpType.MultiPuts, null);
        Request request = createRequest( embeddedSerializer.toBytes(keyValueParas), Request.RequestType.Scan);
        Response response = sendRequest( request );
        if ( response.isError())
            throw new RuntimeException( (String) response.getPayload() );
        else
            return ((KeyValueParas) response.getPayload()).getList() ;
    }

    private KeyValueParas makeKeyValueParaVersion(List<KeyValue> list, OpType opType, String queryStr) {
        //assume the value is raw object, that means it need to be serialized
        for ( KeyValue each : list) {
            if ( each.getValue().getVersion() > 0 )
                each.getValue().setVersion(  each.getValue().getVersion() +1 );
            each.getValue().setData( serializer.toBytes( each.getValue().getData()));
        }
        return new KeyValueParas(opType, list, queryStr);
    }

    /**
     * Table scan of key set to return List<Key> in key set
     * cursorPara.isEnd() is true, means end of cursor
     * to retrieve List<KeyValye> by calling cursorPara.getKeyValue()
     * @param batchSize
     * @return cursorPara
     */
    public CursorPara openKeyCursor(short batchSize) {
        return opeCursorType(batchSize, CursorPara.CursorType.KeySet);
    }

    public CursorPara openKeyCursor(short batchSize, String queryStr) {
        return opeCursorType(batchSize, CursorPara.CursorType.KeySet, queryStr);
    }

    /**
     * Table scan of key set to return List<Key> in key set
     * cursorPara.isEnd() is true, means end of cursor
     * to retrieve List<KeyValye> by calling cursorPara.getKeyValue()
     * @param batchSize
     * @return cursorPara
     */
    public CursorPara openKeyValueCursor(short batchSize) {
        return opeCursorType(batchSize, CursorPara.CursorType.KeyValueSet);
    }

    public CursorPara openKeyValueCursor(short batchSize, String queryStr) {
        return opeCursorType(batchSize, CursorPara.CursorType.KeyValueSet, queryStr);
    }

    private CursorPara opeCursorType(short batchSize, CursorPara.CursorType cursorType) {
        return nextCursor(new CursorPara(store, batchSize, cursorType.getValue()));
    }

    private CursorPara opeCursorType(short batchSize, CursorPara.CursorType cursorType, String queryStr) {
        return nextCursor(new CursorPara(store, batchSize, cursorType.getValue(), queryStr) );
    }

    /**
     * Table scan of key set to return List<Key> in key set
     * cursorPara.isEnd() is true, means end of cursor
     * to retrieve List<KeyValye> by calling cursorPara.getKeyValue()
     * @param batchSize
     * @return cursorPara
     */
    public CursorPara openScanCursor(short batchSize, Key from, Key to) {
        return nextCursor( new CursorPara(store, batchSize, from, to));
    }

    public CursorPara openScanCursor(short batchSize, Key from, Key to, String queryStr) {
        return nextCursor( new CursorPara(store, batchSize, from, to, queryStr));
    }

    public CursorPara openQueryCursor(short batchSize, String queryStr) {
        return nextCursor( new CursorPara(store, batchSize, CursorPara.CursorType.SelectQuery.getValue(), queryStr));
    }

    /**
     * close cursor on server side
     * @param cursorPara
     */
    public void closeCursor(CursorPara cursorPara) {
        //set flag to stop
        cursorPara.setStop(true);
        nextCursor( cursorPara);
    }

    /**
     * Table scan of key set to return List<Key> in key set
     * cursorPara.isEnd() is true, means end of cursor
     * to retrieve List<KeyValye> by calling cursorPara.getKeyValue()
     * @return cursorPara
     */
    public CursorPara nextCursor(CursorPara cursorPara) {
        //clear out keyValueList first
        cursorPara.setKeyValueList( null);
        Request request = createRequest( embeddedSerializer.toBytes(cursorPara), Request.RequestType.Scan);
        try {
            Response response = sendRequest( request );
            return (CursorPara) response.getPayload();
        } catch (RuntimeException ex) {
            logger.error( ex.getMessage(), ex);
            throw ex;
        }
    }
}
