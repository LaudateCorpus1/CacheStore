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

import com.sm.Service.State;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Invoker;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import com.sm.store.ServerWSHandler;
import com.sm.store.StoreParas;
import com.sm.store.cluster.ClusterNodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.StoreException;

import java.util.concurrent.ConcurrentMap;

import static com.sm.store.StoreParas.NO_ERROR;

public class ClusterStoreCallBack extends Scan4CallBack {
    protected static final Log logger = LogFactory.getLog(ClusterStoreCallBack.class);
    protected ClusterNodes clusterNodes;

    public ClusterStoreCallBack(ConcurrentMap<String, RemoteStore> storeMaps, Serializer serializer,
                                ClusterNodes clusterNodes) {
        if (serializer == null)
             this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        this.clusterNodes = clusterNodes;
        this.storeMaps = storeMaps;
        init();
    }

    protected void init() {
        //support of trigger was done by ClusterServer.init()
        //store.setupTrigger2Cache( each) was called by ClusterStoreServer
        WSHandler = new ServerWSHandler(storeMaps);
        serverState = State.Start;
    }


    @Override
    public Response processRequest(Request request) {
        Response resp;
        // check invoker type first
        if ( request.getType() == Request.RequestType.Invoker ) {
            Invoker invoker = (Invoker) embeddedSerializer.toObject( request.getPayload());
            resp = WSHandler.invoke(invoker);
            serializerResponse( request.getHeader(), resp);
        }
        else if (request.getType() == Request.RequestType.Cluster) {
            StoreParas paras = processStoreParas( request);
            boolean err = ( paras.getErrorCode() == NO_ERROR ? false : true );
            resp = new Response( embeddedSerializer.toBytes(paras), err);
        }
        else if ( request.getType() == Request.RequestType.KeyIterator) {
            resp = processKeyIterator( request);
        }
        else if (request.getType() == Request.RequestType.Scan) {
            //scan type for KeyParas or KeyValueParas
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

    @Override
    protected StoreParas processStoreParas(Request request) {
        ClusterStore store = (ClusterStore) storeMaps.get( request.getHeader().getName() );
        if ( store == null ) throw new StoreException("Store "+request.getHeader().getName()+" not exits");
        StoreParas paras ;
        if ( request.getType() == Request.RequestType.Cluster )
            paras = (StoreParas) embeddedSerializer.toObject(request.getPayload());
        else
            paras =  StoreParas.toStoreParas(request.getPayload());

        processParas( paras, store);
        return paras;
    }


}
