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

import com.sm.Service;
import com.sm.message.Invoker;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.query.QueryListenerImpl;
import com.sm.store.*;
import com.sm.store.server.RemoteScanStore;
import com.sm.store.server.RemoteStore;
import com.sm.utils.TupleThree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.util.ArrayList;
import java.util.List;

import static com.sm.store.StoreParas.NO_ERROR;




public class Scan4CallBack extends StoreCallBack {

    protected static final Log logger = LogFactory.getLog(Scan4CallBack.class);
    protected RemoteConfig remoteConfig;

    public Scan4CallBack() {}

    public Scan4CallBack(RemoteConfig remoteConfig ) {
        this.remoteConfig = remoteConfig;
        serializer = remoteConfig.getSerializer();
        storeList = new ArrayList<TupleThree<String, String, Integer>>();
        //@TODO backward compatbility, eventually will eliminate storeList using RemoteConfig
        for (StoreConfig storeConfig : remoteConfig.getConfigList() ) {
            storeList.add( new TupleThree<String, String, Integer>(storeConfig.getStore(), storeConfig.getDataPath(),
                    storeConfig.getMode() ) );
            delay = storeConfig.isDelay();
        }
        init();
    }

    /**
     * sorted == true, create RemoteScanStore, otherwise RemoteStore
     */
    protected void init() {
        serverState = Service.State.Start;
        int i = 0;
        for (StoreConfig each : remoteConfig.getConfigList() ) {
            try {
                RemoteStore store;
                if ( each.isSorted() ) {
                    store = new RemoteScanStore(each.getStore(), serializer, each.getDataPath(),
                            each.isDelay(), null, each.getMode(), each.isSorted() );
                }
                else {
                    store = new RemoteStore(each.getStore(), serializer, each.getDataPath(),
                            delay, null, each.getMode()) ;
                }
                logger.info("put " +each.getStore()+ " path "+ each.getDataPath() );
                storeMaps.put( each.getStore(), store);
                if ( each.isDelay() ) {
                    logger.info("startWriteThread =2");
                    store.startWriteThread(2);
                }
                store.setupTrigger2Cache(each);
            } catch (Exception  ex) {
                logger.error(each.getStore()+" "+ex.getMessage(), ex);
                i++;
            }

        }
        if ( i > 0 ) throw new StoreException("fail to open store "+i);
        //after initialize storeMap, pass it to stored procedure
        WSHandler = new ServerWSHandler(storeMaps);
    }



    /**
     * try to reuse StoreServerHandler which assume response payload had been serialized
     * unlike ScanStore which assume response payload is a object
     * @param request
     * @return
     */
    @Override
    public Response processRequest(Request request) {
        Response resp;
      // check invoker type first
        if ( request.getType() == Request.RequestType.Invoker ) {
            Invoker invoker = (Invoker) embeddedSerializer.toObject( request.getPayload());
            resp = WSHandler.invoke(invoker);
            serializerResponse( request.getHeader(), resp);
        }
        else if ( request.getType() == Request.RequestType.KeyIterator) {
            resp =  processKeyIterator( request);
            serializerResponse( request.getHeader(), resp);
        }
        else if (request.getType() == Request.RequestType.Scan) {
            //scan type for RemoteScanStore
            resp = processScan( request);
            serializerResponse( request.getHeader(), resp);
        }
        else {
            StoreParas paras = processStoreParas( request);
            boolean err = ( paras.getErrorCode() == NO_ERROR ? false : true );
            resp = new Response( paras, err);
        }
        return resp;
    }

    /**
     * process scan request type that includes scan range, cursor, KeyParas, KeySet
     * @param request
     * @return Response
     */
    @Override
    protected Response processScan(Request request) {
        Object msg= embeddedSerializer.toObject(request.getPayload());
        //it could be RemoteScanStore or RemoteStore,it depends on sorted configuration
        //Query support both type store, but scan only support for RemoteScanStore
        RemoteStore store = storeMaps.get( request.getHeader().getName() );
        Response response;
        if (msg instanceof KeyValueParas) {
            KeyValueParas keyValueParas = (KeyValueParas) msg;
            List<KeyValue> list;
            if ( keyValueParas.getOpType() == OpType.QueryStatement) {
                list = processFullQuery( createQueryIterator( keyValueParas.getQueryStr(), store), keyValueParas.getQueryStr(),
                        store );
                response = new Response( new KeyValueParas(OpType.QueryStatement, list, keyValueParas.getQueryStr() ));
            }
            else if (keyValueParas.getOpType() == OpType.MultiUpdateQuery) {
                return processQueryPut( keyValueParas, store);
            }
            else {
                list = store.multiPuts(keyValueParas.getList());
                return new Response( processQueryStr( list, keyValueParas.getQueryStr(), keyValueParas.getOpType()) );
            }
        }
        else if (msg instanceof KeyParas) {
            KeyParas keyParas = (KeyParas) msg;
            List<KeyValue> list;
            KeyValueParas kVParas = null;
            if ( keyParas.getOpType() == OpType.MultiRemoves) {
                list = store.multiRemove(keyParas.getList());
                kVParas = new KeyValueParas(keyParas.getOpType(), list);
            }
            else {
                list = store.multiGets(keyParas.getList());
                if ( keyParas.getOpType() == OpType.MultiSelectQuery)
                    kVParas = processQueryStr( list, keyParas.getQueryStr(), keyParas.getOpType());
                else
                    kVParas = new KeyValueParas(keyParas.getOpType(), list);
            }
            response = new Response( kVParas );
        }
        else if (msg instanceof ScanParaList ) {
            ScanParaList scanParaList = (ScanParaList) msg;
            Key from, to;
            if ( scanParaList.getList().size()== 2) {
                from = scanParaList.getList().get(0);
                to = scanParaList.getList().get(1);
            }
            else {
                from = scanParaList.getList().get(0);
                to = from;
            }
            List<KeyValue> list = ((RemoteScanStore) store).scan(from, to);
            KeyValueParas keyValueParas = processQueryStr(list, scanParaList.getQueryStr(), scanParaList.getOpType());
            response = new Response(keyValueParas );
        }
        else if (msg instanceof CursorPara) {
            CursorPara cursorPara = (CursorPara) msg;
            if ( cursorPara.getCursorType() == CursorPara.CursorType.SelectQuery) {
                ((RemoteScanStore) store).nextCursor( cursorPara);

            }
            else {
                ((RemoteScanStore) store).nextCursor(cursorPara);
                //apply the query string if defined
                List<KeyValue> lst = processQueryStr(cursorPara.getKeyValueList(), cursorPara.getQueryStr()).getList();
                cursorPara.setKeyValueList(lst);
            }
            response = new Response( cursorPara);
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

    private QueryIterator createQueryIterator(String queryStr, RemoteStore store) {
        QueryListenerImpl queryListener = new QueryListenerImpl( queryStr );
        queryListener.walkTree();
        return new QueryIterator( queryListener.getPredicateStack(), queryListener.isTableScan(), store);
    }



}
