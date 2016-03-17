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

package com.sm.replica.server.grizzly;

import com.sm.message.Request;
import com.sm.replica.ParaList;
import com.sm.replica.UnisonCallBack;
import com.sm.storage.Persistence;
import com.sm.store.StoreParas;
import com.sm.transport.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import voldemort.store.cachestore.Key;

import java.io.IOException;
import java.util.Iterator;

public class UnisonFilter extends ReplicaServerFilter {

    private static final Log logger = LogFactory.getLog(ReplicaServerFilter.class);
    private UnisonCallBack callback;
    private Persistence localStore;

    public UnisonFilter(UnisonCallBack callback)  {
        this(callback, null);
    }

    public UnisonFilter(UnisonCallBack callback, Persistence localStore ) {
        if ( callback == null ) throw new RuntimeException("call back can not be null");
        this.callback = callback;
        this.localStore = localStore;
        init();
    }

    protected void init() {
        super.init();
        if ( localStore != null && isNeedRecovery() ) {
            logger.warn("there is pending transactions for recovery");
            recoveryTrx();
        }
    }

    private void recoveryTrx() {
        Iterator<Key> iterator = localStore.getKeyIterator();
        int i = 1;
        while ( iterator.hasNext() ) {
            Key key = iterator.next();
            //remove any constrain
            logger.info("i "+ (i++) +" key "+key.toString());
            Object value = localStore.get(key);
            callback.recovery(key, value);
        }
    }


    private boolean isNeedRecovery() {
        Iterator<Key> iterator = localStore.getKeyIterator();
        while ( iterator.hasNext() ) {
            Key key = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx)
            throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Object peerAddress = ctx.getAddress();

        final Request req = ctx.getMessage();
        if ( req.getHeader().getVersion() % freq == 0)
            logger.info("receive "+ req.getHeader().toString()+ " from "+ctx.getAddress().toString()+" payload len "
                    + (req.getPayload() == null ?  0  :  req.getPayload().length ) ) ;

        processParaList( req, ctx, peerAddress);
        ctx.write(peerAddress, req, null);
        return ctx.getStopAction();
    }

    @Override
    protected void processParaList(Request req, FilterChainContext ctx, Object peerAddress) {
        ParaList paraList = null;
        try {
            if ( serverState == Utils.ServerState.Shutdown) {
                logger.error("server in shut down state, close channel");
                ctx.getConnection().close();
            }
            else if ( req.getType() != Request.RequestType.Normal) {
                logger.error("wrong request type expect Normal but get "+req.getType().toString());
                ctx.getConnection().close();
            }
            else {
                paraList = ParaList.toParaList( req.getPayload() );
                callback.processParaList(paraList);
                //set value to null for item has no error to reduce payload
                for ( StoreParas each : paraList.getLists() ) {
                    if ( each.getErrorCode() == StoreParas.NO_ERROR )
                        each.setValue( null);
                }
                req.setPayload( paraList.toBytes());
            }
        } catch (Exception ex) {
            logger.error("close channel "+ex.getMessage()+" "+ctx.getConnection().getPeerAddress().toString(), ex);
            if ( paraList != null ) {
                setError( paraList, ex.getMessage());
                req.setPayload(  paraList.toBytes());
            }
            else
                ctx.getConnection().close();
        }
    }

}
