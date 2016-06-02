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

package com.sm.store.client.netty;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import com.sm.store.StoreParas;
import com.sm.transport.AsynReq;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.sm.store.cluster.Utils.isLog;

public class ClusterClientHandler extends StoreClientHandler {
    private static final Log logger = LogFactory.getLog(ClusterClientHandler.class);
    protected Serializer serializer;
    protected int freq = 1 ;
    protected long error ;

   public ClusterClientHandler(long timeout, Serializer serializer) {
        this.timeout = timeout;
        this.serializer = serializer;
        this.map = new ConcurrentHashMap<Long, AsynReq>(119);
    }

    public ClusterClientHandler() {
        this(6000, new HessianSerializer());
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        if ( isLog( error++, freq))
            logger.error("Unexpected exception from downstream " + e.getCause());
        e.getChannel().close();
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // construct response
        if( e.getMessage() instanceof Request) {
            Request req = (Request) e.getMessage();
            if ( isLog(req.getHeader().getVersion(), freq) )
                logger.info("receive "+ req.toString() +" from "+e.getRemoteAddress().toString() );
            AsynReq async = map.get(req.getHeader().getVersion() );
            if ( async != null ) {
                async.getLock().lock();
                try {
                    if ( async.getRequest().getType() == Request.RequestType.Normal) {
                        StoreParas paras = StoreParas.toStoreParas( req.getPayload());
                        boolean error = paras.getErrorCode() == StoreParas.NO_ERROR ? false : true ;
                        async.setResponse( new Response( paras, error));
                    }
                    else {
                        Response response = new Response(embeddedSerializer.toObject(req.getPayload()));
                        async.setResponse(response);
                    }
                    async.getReady().signal();
                } finally {
                    async.getLock().unlock();
                }
            }
            else logger.warn(req.getHeader().toString()+" is not longer in map");
        }
        else {
            logger.warn( e.getMessage().getClass().getName()+" is not supported "+
                    e.getChannel().getRemoteAddress().toString() );
        }
    }


    /**
     *
     * @param request
     * @param channel
     * @return
     */

    public Response sendRequest(Request request, Channel channel) {
        AsynReq asynReq = new AsynReq( request);
        try {
            AsynReq tmp = map.putIfAbsent(request.getHeader().getVersion(), asynReq);
            if ( tmp != null )
                throw new ConnectionException(request.getHeader().toString() +" was submitted twice");
            channel.write( request);
            boolean flag = false;
            asynReq.getLock().lock();
            try {
                flag = asynReq.getReady().await( timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //map.remove( request.getHeader().getVersion() );
                //swallow exception
                return new Response("InterruptedException", true);
            } finally {
                asynReq.getLock().unlock();
            }
            if ( flag == false )
                return new Response("time out ms "+timeout, true);
            else
                return asynReq.getResponse() ;

        } finally {
            if ( asynReq != null ) {
                map.remove( asynReq.getRequest().getHeader().getVersion() );
            }
        }
    }

}
