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

package com.sm.store.server;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.message.TCPCallBack;
import com.sm.store.StoreParas;
import com.sm.utils.ThreadPoolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import java.util.concurrent.*;

import static com.sm.store.Utils.createError;
import static com.sm.store.Utils.isOverLoaded;
public class StoreServerHandler extends SimpleChannelUpstreamHandler {
    private static final Log logger = LogFactory.getLog(StoreServerHandler.class);

    //call back for incoming request
    protected  TCPCallBack callback;
    protected int maxThreads;
    protected int maxQueue ;
    protected Executor threadPools;
    protected int freq = 1;

    public StoreServerHandler(TCPCallBack callback, int maxThreads, int maxQueue) {
        if ( callback == null ) throw new RuntimeException("call back can not be null");
        this.callback = callback;
        this.maxThreads = maxThreads;
        this.maxQueue = maxQueue;
        init();
    }

    public StoreServerHandler(TCPCallBack callBack, int maxThreads) {
        this( callBack, maxThreads, maxThreads * 1000 );
    }

   private void init() {
        if ( Runtime.getRuntime().availableProcessors() > maxThreads )
            this.maxThreads = Runtime.getRuntime().availableProcessors();
        if ( maxQueue < maxThreads * 1000 )
            maxQueue = maxThreads * 1000;
        BlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>(maxQueue );
        threadPools = new ThreadPoolExecutor( maxThreads, maxThreads , 30, TimeUnit.SECONDS , queue, new ThreadPoolFactory("store") );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
      // Let SimpleChannelHandler call actual event handler methods below.
      super.handleUpstream(ctx, e);
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        if ( freq > 1)
            this.freq = freq;
        else logger.warn("can not less 1 freq "+freq);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Request req =  (Request) e.getMessage();
        // make sure fre > 1 and default 30
        if ( req.getHeader().getVersion() % freq == 0)
            logger.info("receive " + req.toString()+" from "+e.getRemoteAddress().toString());
        StoreParas paras = null ;
        try {
            if ( isOverLoaded( (ThreadPoolExecutor) threadPools))  {
              paras = createError( req, "thread pools overload");
              req.setPayload( paras.toBytes() );
              ctx.getChannel().write( req);
            }
            else
                threadPools.execute( new SyncCall( callback, req, ctx));
        } catch (Exception ex) {
             logger.error(ex.getMessage(), ex);
             paras = createError( req, ex.getMessage());
             req.setPayload( paras.toBytes() );
             ctx.getChannel().write( req);
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      // Close the connection when an exception is raised.
      logger.error( e.getCause().getMessage(), e.getCause() );
      e.getChannel().close();
    }


    class SyncCall implements Runnable {
        private TCPCallBack syncCallback;
        private Request request;
        private ChannelHandlerContext ctx;

        public SyncCall(TCPCallBack syncCallback, Request request, ChannelHandlerContext ctx) {
            this.syncCallback = syncCallback;
            this.request = request;
            this.ctx = ctx;
        }


        @Override
        public void run() {
            Response response = null ;
            try {
                response = syncCallback.processRequest(request);
            } catch ( Exception ex) {
                // swallow exception, for type of Invoker, Scan, KeyIterator
                String msg =  ex.getMessage() == null ? "null" :ex.getMessage();
                logger.error( msg, ex);
                response = new Response( toByte(msg), true );
            }
            Request req ;
            if (request.getType() != Request.RequestType.Normal) {
                req = new Request(request.getHeader(), (byte[]) response.getPayload(), request.getType()  );
            }
            else {
                byte[] payload = ((StoreParas) response.getPayload()).toBytes();
                req = new Request(request.getHeader(), payload ,  request.getType()  );
            }
            ctx.getChannel().write(req);
        }

        private byte[] toByte(String msg) {
            //check null
            if ( syncCallback != null &&((StoreCallBack) syncCallback).getSerializer() != null)
                return ((StoreCallBack) syncCallback).getSerializer().toBytes( msg);
            else
                return msg.getBytes();
        }
    }


}
