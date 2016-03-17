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

package com.sm.transport.netty;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.AsynReq;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ClientAsynHandler extends SimpleChannelUpstreamHandler implements Send {
    private static final Log logger = LogFactory.getLog(ClientAsynHandler.class);
    // request timeout
    protected long timeout;
    protected ConcurrentMap<Long, AsynReq> map;

    public ClientAsynHandler(long timeout) {
        this.timeout = timeout;
        this.map = new ConcurrentHashMap<Long, AsynReq>(119);
    }

    public ClientAsynHandler() {
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
                            async.setResponse( new Response(req.getHeader().toString()));
                            if (req.getPayload().length  > 0) {
                                //the first byte indicate error flag, 1 is error
                                if (req.getPayload()[0] == 1 ) {
                                    async.getResponse().setError( true);
                                    try {
                                        String error = new String( req.getPayload(), 1 , req.getPayload().length-1 , "UTF-8");
                                        async.getResponse().setPayload( req.getHeader().toString()+" "+ error);
                                    } catch (UnsupportedEncodingException ex) {
                                        logger.error( ex.getMessage() );
                                    }
                                }
                            }
                            else logger.warn("request payload len = 0 "+req.getHeader());
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
            } //if != null
            else logger.warn(req.getHeader().toString()+" is not longer in map");
        }
        else {
            logger.warn( e.getMessage().getClass().getName()+" is not supported "+
                    e.getChannel().getRemoteAddress().toString() );
        }
    }

    // do nothing yet
    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("Unexpected exception from downstream " + e.getCause());
        e.getChannel().close();
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
                // set enter flag to true, for notify message received thread
                asynReq.setEntered(true);
                flag = asynReq.getReady().await( timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //map.remove( request.getHeader().getVersion() );
                //swallow exception
                return new Response("InterruptedException", true);
            } finally {
                asynReq.getLock().unlock();
            }
            if ( flag == false )
                return new Response("time out ms"+timeout, true);
            else
                return asynReq.getResponse() ;

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
