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

package com.sm.transport.netty;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler extends SimpleChannelUpstreamHandler implements Send {

    private static final Log logger = LogFactory.getLog(ClientHandler.class);

    protected volatile boolean ready ;
    protected final Lock lock = new ReentrantLock();
    protected Condition notReady = lock.newCondition();
    protected final AtomicLong transferredBytes = new AtomicLong(0);
    // request timeout
    protected long timeout;
    protected volatile Request resp;


    /**
     * default time out is 6000 millisecond
      */
    public ClientHandler() {
        this( 6000L);
    }

    /**
     *
     * @param timeout - request timeout
     */
    public ClientHandler(long timeout){
        this.timeout = timeout;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
//      if (e instanceof ChannelStateEvent) {
//          if (((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
//              logger.info(e.toString());
//          }
//      }

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
       lock.lock();
        try {
            // construct response
            if( e.getMessage() instanceof Request) {
                Request req = (Request) e.getMessage();
                resp =req;
                logger.info("receive "+ req.toString() +" from "+e.getRemoteAddress().toString() );
            }
            else {
                logger.warn( e.getMessage().getClass().getName()+" is not supported "+
                        e.getChannel().getRemoteAddress().toString() );
                resp = null;
            }
                //throw new RuntimeException( e.getMessage().getClass().getName()+" is not supported");
        } finally {
            cleanUp();
            lock.unlock();
        }
      // Server is supposed to send nothing.  Therefore, do nothing.
    }

    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) {
      transferredBytes.addAndGet(e.getWrittenAmount());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        logger.error("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }

    /**
     *
     * @param request
     * @param channel
     * @return Response,
     * isError() - true means error occurs, payload will be string of error message
     * 1. time out 2. response is null 3. mismatch request and response
     * 4. error + type of error 5. connection broken
     */

    public Response sendRequest(Request request, Channel channel){
        if ( ! channel.isConnected() ) throw new ConnectionException("broken connection");
        lock.lock();
        try {
            channel.write( request);
            ready = false ;
            try {
                boolean flag = notReady.await(timeout, TimeUnit.MILLISECONDS );
                if ( ! flag ) {
                    cleanUp();
                    channel.disconnect();
                    throw new ConnectionException(timeout+ " seconds time out from "+channel.getRemoteAddress());
                }
                // return response
                else {
                    // check null value of response
                    if ( resp == null ) return new Response("response is null", true);
                    if ( resp.getHeader().equals( request.getHeader())) {
                        boolean result = resp.getPayload()[0] == (byte) 0 ? false : true;
                        String str = "";
                        if (resp.getPayload().length > 1  ) {
                            str = new String(resp.getPayload(), 1, resp.getPayload().length-1, "UTF-8");
                        }
                        //match the request
                        return new Response( str, result);
                    }
                    else {
                        String errorStr = "mismatch in "+resp.getHeader().toString()+
                                " out "+request.getHeader().toString();
                        logger.error(errorStr + ", disconnect " + channel.getRemoteAddress());
                        channel.disconnect();
                        throw new ConnectionException(errorStr);
                    }
                }
            } catch (Exception ex) {
                cleanUp();
                logger.error( ex.getMessage(), ex);
                return new Response("error "+ex.getMessage(), true);
            }
        } finally {
            lock.unlock();
        }
    }

    public void sendMessage(Request request, Channel channel) {
        channel.write( request);
    }

    public Response invoke(Request request, Channel channel) {
        if ( request.getType() != Request.RequestType.Invoker )
            throw new ConnectionException("request type "+request.getType()+" is not an invoker");
        else
            return sendRequest( request, channel );
    }

    protected void cleanUp() {
        if ( ! ready ) {
            ready = true ;
            notReady.signal();
        }
    }


}
