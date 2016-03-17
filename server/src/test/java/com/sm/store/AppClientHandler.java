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

package com.sm.store;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.ConnectionException;
import com.sm.transport.netty.Send;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppClientHandler extends SimpleChannelUpstreamHandler implements Send {
    private static final Log logger = LogFactory.getLog(AppClientHandler.class);
    // request timeout
    protected long timeout;
    protected ConcurrentMap<Long, AsynReq> map;

    public AppClientHandler(long timeout) {
        this.timeout = timeout;
        this.map = new ConcurrentHashMap<Long, AsynReq>(119);
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
                //nameMap.remove( request.getHeader().getVersion() );
                //swallow exception
                return new Response("InterruptedException", true);
            } finally {
                asynReq.getLock().unlock();
            }
            if ( flag == false )
                return new Response("time out ms"+timeout, true);
            else
                return new Response(asynReq.getRequest().getHeader().toString() +" Successful");

        } finally {
            if ( asynReq != null ) {
                map.remove( asynReq.getRequest().getHeader().getVersion() );
            }
        }
    }

    @Override
    public void sendMessage(Request request, Channel channel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Response invoke(Request request, Channel channel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // construct response
        if( e.getMessage() instanceof Request) {
            Request req = (Request) e.getMessage();
            logger.info("receive "+ req.toString() +" from "+e.getRemoteAddress().toString() );
            AsynReq async = map.get(req.getHeader().getVersion() );
            if ( async != null ) {
                async.getLock().lock();
                try {
                    //remove from nameMap, let origial thread does remove
                    //nameMap.remove( req.getHeader().getVersion() );
                    //async.setResponse( new Response(req.getHeader().toString()));
                    if (req.getPayload().length  > 0) {
                        logger.info("response :" + req.getHeader().toString());
                    }
                    else logger.warn("request payload len = 0 "+req.getHeader());
                    async.getReady().signal();
                } finally {
                    async.lock.unlock();
                }
            }
            else logger.warn(req.getHeader().toString()+" is not longer in nameMap");
        }
        else {
            logger.warn( e.getMessage().getClass().getName()+" is not supported "+
                    e.getChannel().getRemoteAddress().toString() );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }




    public class AsynReq {
        Request request;
        Lock lock = new ReentrantLock();
        Condition ready = lock.newCondition();
//        Response response;
        public AsynReq(Request request) {
            this.request = request;
        }



        public Request getRequest() {
            return request;
        }

        public Lock getLock() {
            return lock;
        }

        public Condition getReady() {
            return ready;
        }
    }
}
