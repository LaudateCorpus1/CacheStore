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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.TCPCallBack;
import com.sm.storage.Serializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClusterServerHandler extends StoreServerHandler {
    protected static final Log logger = LogFactory.getLog(ClusterServerHandler.class);

    protected Serializer serializer;
    //log frequency
    protected long error ;

    public ClusterServerHandler(TCPCallBack callback, int maxThreads, int maxQueue, Serializer serializer) {
        super(callback, maxThreads, maxQueue);
        if ( serializer == null)
            this.serializer = new HessianSerializer();
        else
            this.serializer = serializer ;
    }
//
//    public int getFreq() {
//        return freq;
//    }
//
//    public void setFreq(int freq) {
//        this.freq = freq;
//    }
//
//    @Override
//    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
//      super.handleUpstream(ctx, e);
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
//      // Close the connection when an exception is raised.
//        if ( isLog(error++, freq))
//          logger.error( e.getCause().getMessage(), e.getCause() );
//        e.getChannel().close();
//    }
//
//
//    @Override
//    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
//        Request req =  (Request) e.getMessage();
//        if ( isLog( req.getHeader().getVersion(), freq))
//            logger.info("receive " + req.toString()+" from "+e.getRemoteAddress().toString());
//        Response response = null;
//        try {
//            if ( isOverLoaded( (ThreadPoolExecutor) threadPools))  {
//                writeError("thread pool overload", req);
//                ctx.getChannel().write( req);
//            }
//            else {
//                threadPools.execute( new SyncCall( callback, req, ctx));
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//            writeError( ex.getMessage(), req);
//            ctx.getChannel().write( req);
//        }
//    }
//
//    private void writeError(String msg, Request req) {
//        Response response = new Response( msg, true);
//        req.setType(Request.RequestType.Response );
//        req.setPayload( serializer.toBytes(response));
//    }
//
//   class SyncCall implements Runnable {
//        private TCPCallBack syncCallback;
//        private Request request;
//        private ChannelHandlerContext ctx;
//
//        public SyncCall(TCPCallBack syncCallback, Request request, ChannelHandlerContext ctx) {
//            this.syncCallback = syncCallback;
//            this.request = request;
//            this.ctx = ctx;
//        }
//
//
//        @Override
//        public void run() {
//            Response response = null ;
//            try {
//                response = syncCallback.processRequest(request);
//            } catch ( Exception ex) {
//                // swallow exception
//                logger.error( ex.getMessage(), ex);
//                response = new Response( ex.getMessage().getBytes(), true );
//            }
//            if (request.getType() != Request.RequestType.Normal ) {
//                request.setPayload( (byte[]) response.getPayload() );
//            }
//            else {
//                byte[] payload; //= ((StoreParas) response.getPayload()).toBytes();
//                if (request.getHeader().getSerializeVersion() == (byte) 0)
//                    payload = ((StoreParas) response.getPayload()).toBytes();
//                else
//                    payload = serializer.toBytes(response.getPayload());
//                request.setPayload( payload);
//            }
//            ctx.getChannel().write(request);
//        }
//    }
}
