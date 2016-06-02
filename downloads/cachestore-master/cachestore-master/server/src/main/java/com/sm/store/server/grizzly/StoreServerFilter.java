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

package com.sm.store.server.grizzly;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.message.TCPCallBack;
import com.sm.store.StoreParas;
import com.sm.store.server.StoreCallBack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

public class StoreServerFilter extends BaseFilter {
    private static final Log logger = LogFactory.getLog(StoreServerFilter.class);
    //call back for incoming request
    protected  TCPCallBack callback;
    protected int maxThreads;
    protected int maxQueue ;
    protected int freq = 1;

    public StoreServerFilter(TCPCallBack callback, int maxThreads, int maxQueue) {
        if ( callback == null ) throw new RuntimeException("call back can not be null");
        this.callback = callback;
        this.maxThreads = maxThreads;
        this.maxQueue = maxQueue;
    }

    public StoreServerFilter(TCPCallBack callBack, int maxThreads) {
        this( callBack, maxThreads, maxThreads * 1000 );
    }


    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Request req = ctx.getMessage();
        if ( req.getHeader().getVersion() % freq == 0)
            logger.info("receive " + req.toString()+" from "+ctx.getAddress().toString());

        processRequest( req, ctx);
        return ctx.getStopAction();
    }


    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    protected void  processRequest(Request request, FilterChainContext ctx ) {
        Response response = null ;
        try {
            response = callback.processRequest(request);
        } catch ( Exception ex) {
            // swallow exception, for type of Invoker, Scan, KeyIterator
            String msg =  ex.getMessage() == null ? "null" :ex.getMessage();
            logger.error( msg, ex);
            response = new Response( toByte(msg), true );
        }
        Request req ;
        if (request.getType() == Request.RequestType.Invoker || request.getType() == Request.RequestType.Scan
                || request.getType() == Request.RequestType.KeyIterator || request.getType() == Request.RequestType.Cluster ) {
            req = new Request(request.getHeader(), (byte[]) response.getPayload(), request.getType()  );
        }
        else {
            byte[] payload = ((StoreParas) response.getPayload()).toBytes();
            req = new Request(request.getHeader(), payload ,  request.getType()  );
        }
        final Object peerAddress = ctx.getAddress();
        ctx.write(peerAddress, req, null);
    }

    private byte[] toByte(String msg) {
        //check null
        if ( callback != null && callback instanceof StoreCallBack && ((StoreCallBack) callback).getEmbeddedSerializer() != null) {
            return ((StoreCallBack) callback).getEmbeddedSerializer().toBytes(msg);
        }
        else
            return msg.getBytes();
    }

}
