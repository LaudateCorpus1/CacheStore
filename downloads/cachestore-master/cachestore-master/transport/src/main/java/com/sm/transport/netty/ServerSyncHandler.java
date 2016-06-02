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

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.message.TCPCallBack;
import com.sm.transport.SyncCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;
import java.util.concurrent.*;

import static com.sm.transport.Utils.isOverLoaded;
import static com.sm.transport.Utils.response2Bytes;

public class ServerSyncHandler extends SimpleChannelUpstreamHandler {
    private static final Log logger = LogFactory.getLog(ServerSyncHandler.class);

    private int maxThreads;
    private int maxQueue ;
    private Executor threadPools;
    private TCPCallBack callback;

    public ServerSyncHandler(TCPCallBack callback, int maxThreads, int maxQueue) {
        this.callback = callback;
        this.maxThreads = Runtime.getRuntime().availableProcessors();
        if ( maxThreads > this.maxThreads  )
            this.maxThreads = maxThreads;
        this.maxQueue = this.maxThreads * 100 ;
        if ( maxQueue > this.maxQueue)
            this.maxQueue = maxQueue;
        init();
    }

    private void init() {
        BlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>(maxQueue );
        threadPools = new ThreadPoolExecutor( maxThreads, maxThreads , 30, TimeUnit.SECONDS , queue );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
      // Let SimpleChannelHandler call actual event handler methods below.
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Request req =  (Request) e.getMessage();
        logger.info("receive " + req.toString()+" from "+e.getRemoteAddress().toString());
        // it might need to create a different copy
        if (isOverLoaded((ThreadPoolExecutor)threadPools)) {
            writeMessage(ctx, req.getHeader(), "overloaded remaining "+
                    ((ThreadPoolExecutor) threadPools).getQueue().remainingCapacity(), true );
        }
        else
          threadPools.execute(new SyncCall(req, ctx));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      // Close the connection when an exception is raised.
      logger.error( e.getCause().getMessage(), e.getCause() );
      e.getChannel().close();
    }


    private void writeMessage(ChannelHandlerContext ctx,Header header, String message, boolean error) {
        Response response = new Response( message, error);
        Request request = new Request(header, response2Bytes(response),  Request.RequestType.Response  );
        ctx.getChannel().write(request);
    }


    public class SyncCall implements Runnable, SyncCallback {
        private Request request;
        private ChannelHandlerContext ctx;
        //private TCPCallBack callback;

        public SyncCall(Request request, ChannelHandlerContext ctx) {
            this.request = request;
            this.ctx = ctx;
        }

        public void processRequest(Request request, ChannelHandlerContext ctx) {
            Response response;
            if ( callback != null )
                response = callback.processRequest( request);
            else
                response = new Response(request.getHeader().toString() );

            Request req = new Request(request.getHeader(), response2Bytes(response),  Request.RequestType.Response  );
            logger.info("write "+req.getHeader().toString() );
            ctx.getChannel().write(req);
        }

        @Override
        public void run() {
            try {
                //Thread.sleep( 100L);
                processRequest(request, ctx);
            } catch ( Exception ex) {
                // swallow exception
                logger.error( ex.getMessage(), ex);
                writeMessage(ctx, request.getHeader(), ex.getMessage(), true);

            }
        }
    }
}
