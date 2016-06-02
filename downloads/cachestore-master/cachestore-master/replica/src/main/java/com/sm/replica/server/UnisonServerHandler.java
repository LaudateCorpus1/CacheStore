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

package com.sm.replica.server;

import com.sm.message.Request;
import com.sm.replica.ParaList;
import com.sm.replica.UnisonCallBack;
import com.sm.storage.Persistence;
import com.sm.store.StoreParas;
import com.sm.transport.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import voldemort.store.cachestore.Key;

import java.util.Iterator;

public class UnisonServerHandler extends ReplicaServerHandler {
    private static final Log logger = LogFactory.getLog(UnisonServerHandler.class);

    protected UnisonCallBack callback;
    protected Persistence localStore;
    public final static String CMP = "cmp";


    public UnisonServerHandler(UnisonCallBack callback) {
        this(callback, null);
    }

    public UnisonServerHandler(UnisonCallBack callback, Persistence localStore) {
        if ( callback == null ) throw new RuntimeException("call back can not be null");
        this.callback = callback;
        this.localStore = localStore;
        init();
    }

    protected void init() {
        if ( localStore != null && isNeedRecovery() ) {
            logger.warn("there is pending transactions for recovery");
            recoveryTrx();
        }
    }

    public Persistence getLocalStore() {
        return localStore;
    }

    protected void recoveryTrx() {
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


    protected boolean isNeedRecovery() {
        Iterator<Key> iterator = localStore.getKeyIterator();
        while ( iterator.hasNext() ) {
            Key key = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Request req =  (Request) e.getMessage();
        if ( req.getHeader().getVersion() % freq == 0) {
            logger.info("receive " + req.toString() + " from " + e.getRemoteAddress().toString());
        }
        ParaList paraList = null;
        try {
            if ( serverState == Utils.ServerState.Shutdown) {
                logger.error("server in shut down state, close channel");
                ctx.getChannel().close();
                return;
            }
            else {
                // convert payload to paraList
                paraList = ParaList.toParaList( req.getPayload() );
                callback.processParaList(paraList);
                //set value to null for item has no error to reduce payload
                for ( StoreParas each : paraList.getLists() ) {
                    if ( each.getErrorCode() == StoreParas.NO_ERROR )
                        each.setValue( null);
                }
                req.setPayload( paraList.toBytes());
                ctx.getChannel().write( req);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage()+" "+ctx.getChannel().getRemoteAddress().toString(), ex);
            if ( paraList != null ) {
                setError( paraList, ex.getMessage());
                req.setPayload(  paraList.toBytes());
                ctx.getChannel().write(req);
            }
            else
                ctx.getChannel().close();
        }
    }



}
