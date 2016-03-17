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
import com.sm.store.StoreParas;
import com.sm.transport.ConnectionException;
import com.sm.transport.netty.Send;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;
import voldemort.store.cachestore.StoreException;
import voldemort.versioning.ObsoleteVersionException;
import com.sm.transport.AsynReq;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class StoreClientHandler extends SimpleChannelUpstreamHandler implements Send {

    private static final Log logger = LogFactory.getLog(StoreClientHandler.class);

    protected long timeout;
    protected ConcurrentMap<Long, AsynReq> map;
    protected Serializer embeddedSerializer = new HessianSerializer();

    public StoreClientHandler(long timeout) {
        this.timeout = timeout;
        this.map = new ConcurrentHashMap<Long, AsynReq>(119);
    }

    public StoreClientHandler() {
        this(6000);
    }


    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
      // Let SimpleChannelHandler call actual event handler methods below.
      super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
      logger.info("connected "+e.getChannel().getRemoteAddress());
    }

    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) {
      // Keep sending messages whenever the current socket buffer has room.
      //generateTraffic(e);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("Unexpected exception from downstream " + e.getCause());
        e.getChannel().close();
    }

    /**
     * based upon request type there are different way to handle request payload
     * Invoker type will just pass through, let client handle the
     * @param ctx
     * @param e
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // construct response
        if( e.getMessage() instanceof Request) {
            Request req = (Request) e.getMessage();
            logger.info("receive "+ req.toString() +" from "+e.getRemoteAddress().toString() );
            AsynReq async = map.get(req.getHeader().getVersion() );
            if ( async != null ) {
                int i = 0;
                boolean exited= false;
                // add logic to fix local host concurrency issue
                while ( true ) {
                    async.getLock().lock();
                    try {
                        if ( async.isEntered() || i > 400 ) {
                            // make sure sender has enter wait state or greater 2 seconds
                            if (async.getRequest().getType() == Request.RequestType.Invoker) {
                                async.setResponse( new Response( embeddedSerializer.toObject(req.getPayload()) ));
                            }
                            else if (async.getRequest().getType() == Request.RequestType.KeyIterator) {
                                async.setResponse( new Response( embeddedSerializer.toObject(req.getPayload()) ));
                            }
                            else if (async.getRequest().getType() == Request.RequestType.Scan) {
                                async.setResponse( new Response( embeddedSerializer.toObject(req.getPayload()) ));
                            }
                            else {  //the rest of case use StoreParas
                                StoreParas paras = StoreParas.toStoreParas( req.getPayload());
                                boolean error = paras.getErrorCode() == StoreParas.NO_ERROR ? false : true ;
                                async.setResponse( new Response( paras, error));
                            }
                            async.getReady().signal();
                            exited = true;
                        }

                    } finally {
                        async.getLock().unlock();
                    }
                    i ++;
                    if ( exited ) break;
                    else {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {
                            //do nothing
                        }
                    }//else
                } //while
            } //if !=null
            else logger.warn(req.getHeader().toString()+" is not longer in map");
        }
        else {
            logger.warn( e.getMessage().getClass().getName()+" is not supported "+
                    e.getChannel().getRemoteAddress().toString() );
        }
    }

    private String dumpMap() {
        StringBuilder sb= new StringBuilder();
        Iterator<Long> it = map.keySet().iterator();
        while ( it.hasNext()) {
            Long key = it.next();
            sb.append( key+ " -> " + map.get( key).toString()+" ");
        }
        return sb.toString();
    }

    /**
     *
     * @param request
     * @param channel
     * @return
     */
    @Override
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
                // set enter flag to true, for notify message received thread
                asynReq.setEntered(true);
                flag = asynReq.getReady().await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //map.remove( request.getHeader().getVersion() );
                //swallow exception
                throw new StoreException("InterruptedException");
            } finally {
                asynReq.getLock().unlock();
            }
            // add debug code
            if ( flag == false ) {
                logger.error("map size "+map.size()+" "+dumpMap());
                throw new StoreException("time out ms "+timeout);
            }
            else {
                if ( asynReq.getResponse().isError() ) {
                    StoreParas paras = (StoreParas) asynReq.getResponse().getPayload() ;
                    byte[] str = new byte[0];
                    if ( paras.getValue() != null )
                        str = (byte[])paras.getValue().getData();
                    if ( paras.getErrorCode() == StoreParas.OBSOLETE )
                        throw new ObsoleteVersionException( new String(str));
                    else
                        throw new StoreException( new String( str));
                }
                else
                    return asynReq.getResponse() ;
            }

        } finally {
            if ( asynReq != null ) {
                map.remove( asynReq.getRequest().getHeader().getVersion() );
            }
        }
    }


    public void sendMessage(Request request, Channel channel) {
        channel.write(request);
    }

    public Response invoke(Request request, Channel channel) {
        if ( request.getType() != Request.RequestType.Invoker )
            throw new ConnectionException("request type "+request.getType()+" is not an invoker");
        else
            return sendRequest( request, channel );
    }
}
