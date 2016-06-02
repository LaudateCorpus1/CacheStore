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
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.concurrent.TimeUnit;

public class EchoClientHandler extends ClientHandler {

    private static final Log logger = LogFactory.getLog(EchoClientHandler.class);

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
                logger.info( e.getMessage().getClass().getName()+" len " + e.getMessage().toString().length()
                        +" "+ e.getChannel().getRemoteAddress().toString() );
                resp = null;
            }
            //throw new RuntimeException( e.getMessage().getClass().getName()+" is not supported");
        } finally {
            cleanUp();
            lock.unlock();
        }
        // Server is supposed to send nothing.  Therefore, do nothing.
    }

    public void sendObject(Object request, Channel channel) {
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

                    logger.info("complete "+request.toString().length());
                }
            } catch (Exception ex) {
                cleanUp();
                logger.error( ex.getMessage(), ex);
            }
        } finally {
            lock.unlock();
        }
    }
}
