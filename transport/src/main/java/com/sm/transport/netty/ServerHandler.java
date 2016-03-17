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

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.message.TCPCallBack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import static com.sm.transport.Utils.response2Bytes;

public class ServerHandler extends SimpleChannelUpstreamHandler {

    private static final Log logger = LogFactory.getLog(ServerHandler.class);

    //call back for incoming request
    private TCPCallBack callback;

    public ServerHandler() {
        this(null);
    }

    public ServerHandler(TCPCallBack callback) {
        this.callback = callback;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
      // Let SimpleChannelHandler call actual event handler methods below.
      //logger.info("handleUpstream "+e.getChannel().toString()+ " ops "+ e.getChannel().getInterestOps());
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
      Request req =  (Request) e.getMessage();
      logger.info("receive " + req.toString()+" from "+e.getRemoteAddress().toString());
      // it might need to create a different copy
      Header header = new Header( req.getHeader().getName(), req.getHeader().getVersion(), req.getHeader().getRelease(),
              req.getHeader().getNodeId());
      Response response = null;
      try {
          if( callback != null ) {
              response = callback.processRequest( req);
          }
          else {
              response = new Response("successful");
          }
          Request request = new Request(header, response2Bytes(response),  Request.RequestType.Response  );

          ctx.getChannel().write(request);
      } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
          writeMessage(ctx, req.getHeader(), ex.getMessage(), true);
      }

    }

    private void writeMessage(ChannelHandlerContext ctx,Header header, String message, boolean error) {
        Response response = new Response( message, error);
        Request request = new Request(header, response2Bytes(response),  Request.RequestType.Response  );
        ctx.getChannel().write(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      // Close the connection when an exception is raised.
      logger.error( e.getCause().getMessage(), e.getCause() );
      e.getChannel().close();
    }
}
