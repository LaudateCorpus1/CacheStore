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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import java.util.concurrent.*;

public class AppSyncHandler extends SimpleChannelUpstreamHandler {
    private static final Log logger = LogFactory.getLog(AppSyncHandler.class);

    private int maxThreads;
    private int maxQueue ;
    private Executor threadPools;

    public AppSyncHandler(int maxThreads, int maxQueue) {
        this.maxThreads = maxThreads;
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
      threadPools.execute(new SyncCall(req, ctx));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      // Close the connection when an exception is raised.
      logger.error( e.getCause().getMessage(), e.getCause() );
      e.getChannel().close();
    }


    public class SyncCall implements Runnable, SyncCallback {
        private Request request;
        private ChannelHandlerContext ctx;

        public SyncCall(Request request, ChannelHandlerContext ctx) {
            this.request = request;
            this.ctx = ctx;
        }

        @Override
        public void processRequest(Request request, ChannelHandlerContext ctx) {
            Request req = new Request(request.getHeader(), request.getHeader().toByte() ,  Request.RequestType.Response  );
            logger.info("write "+req.getHeader().toString() );
            ctx.getChannel().write(request);
        }

        @Override
        public void run() {
            try {
                //Thread.sleep( 100L);
                processRequest(request, ctx);
            } catch ( Exception ex) {
                // swallow exception
                logger.error( ex.getMessage(), ex);
            }

        }
    }

}
