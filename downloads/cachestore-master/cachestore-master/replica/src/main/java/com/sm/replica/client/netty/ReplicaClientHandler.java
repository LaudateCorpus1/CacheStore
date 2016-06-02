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

package com.sm.replica.client.netty;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.replica.ParaList;
import com.sm.store.StoreParas;
import com.sm.transport.AsynReq;
import com.sm.transport.ConnectionException;
import com.sm.transport.netty.ClientAsynHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;
import voldemort.store.cachestore.StoreException;

import java.util.concurrent.TimeUnit;

public class ReplicaClientHandler extends ClientAsynHandler {
    private static final Log logger = LogFactory.getLog(ReplicaClientHandler.class);
    private int size = 20;

    public ReplicaClientHandler(long timeout, int size) {
        super(timeout);
        this.size = size;
    }

    public ReplicaClientHandler(int size) {
        this(6000L, size);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        //swallow exception and close connection
        logger.error( e.getCause().toString());
        e.getChannel().close();
    }

    @Override
    public void writeComplete(
            ChannelHandlerContext ctx, WriteCompletionEvent e) {
//        logger.info("write amt "+ e.getWrittenAmount()+" "+e.getChannel().getLocalAddress().toString()+" "+
//                e.getChannel().getRemoteAddress().toString());
        ctx.sendUpstream(e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // construct response
        if( e.getMessage() instanceof Request) {
            Request req = (Request) e.getMessage();
            if ( req.getHeader().getVersion() % size == 0)
                logger.info("receive "+ req.toString() +" from "+e.getRemoteAddress().toString() );
            AsynReq async = map.get(req.getHeader().getVersion() );
            if ( async != null ) {
                int i = 0;
                boolean exited= false;
                while (true) {
                    async.getLock().lock();
                    try {
                         if ( async.isEntered() || i > 400 ) {
                            // make sure sender has enter wait state or greater 2 seconds
                            ParaList paras = ParaList.toParaList( req.getPayload());
                            boolean error = isErrorInList(paras);
                            async.setResponse( new Response( paras, error));
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
            }
            else logger.warn(req.getHeader().toString()+" is no longer in map "+ctx.getChannel().getRemoteAddress().toString());
        }
        else {
            logger.warn( e.getMessage().getClass().getName()+" is not supported "+
                    e.getChannel().getRemoteAddress().toString() );
        }
    }

    private boolean isErrorInList(ParaList paraList) {
        for (StoreParas each : paraList.getLists()) {
            if ( each.getErrorCode() > StoreParas.OBSOLETE ) return true;
        }
        return false;
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
            if ( flag == false )
                throw new StoreException("time out ms "+timeout+" "+channel.getRemoteAddress().toString());
            else {
                if ( asynReq.getResponse().isError() ) {
                    logger.error("error "+request.getHeader().toString());
                    return asynReq.getResponse() ;
                    //StoreParas paras = (StoreParas) asynReq.getResponse().getPayload() ;
                    //byte[] str = new byte[0];
                    //if ( paras.getValue() != null )
                    //    str = (byte[])paras.getValue().getData();
                    //if ( paras.getErrorCode() == StoreParas.OBSOLETE )
                    //   throw new ObsoleteVersionException( new String(str));
                    //else
                    //    throw new StoreException( new String( str));
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
}

