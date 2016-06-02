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

package com.sm.transport.grizzly;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.AsynReq;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import voldemort.store.cachestore.StoreException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ClientFilter extends BaseFilter implements Send {
    private static final Log logger = LogFactory.getLog(ServerFilter.class);
    protected long timeout;
    protected ConcurrentMap<Long, AsynReq> map = new ConcurrentHashMap<Long, AsynReq>(119) ;

    public ClientFilter() {
        this( 6000L);
    }

    public ClientFilter(long timeout) {
        this.timeout = timeout;
    }


    @Override
    public NextAction handleRead(FilterChainContext ctx)
            throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Object peerAddress = ctx.getAddress();

        final Request req = ctx.getMessage();
        logger.info("receive "+ req.getHeader().toString()+ " from "+ctx.getAddress().toString()+" payload len "
                + (req.getPayload() == null ?  0  :  req.getPayload().length ) ) ;
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
        //Request req = new Request( req.getHeader(), new byte[] {0} );
        //ctx.write(peerAddress, req, null);
        return ctx.getStopAction();
    }

    private String dumpMap() {
        StringBuilder sb= new StringBuilder();
        Iterator<Long> it = map.keySet().iterator();
        while ( it.hasNext()) {
            Long key = it.next();
            sb.append( key+ " -> " + map.get( key).toString()+" ");
        }
        return sb.toString();
    }


    @Override
    public Response sendRequest(Request request, Connection connection) {
        AsynReq asynReq = new AsynReq( request);
        try {
            AsynReq tmp = map.putIfAbsent(request.getHeader().getVersion(), asynReq);
            if ( tmp != null )
                throw new ConnectionException(request.getHeader().toString() +" was submitted twice");
            connection.write( request);
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
            if ( flag == false ) {
                logger.error("map size "+map.size()+" "+dumpMap());
                throw new StoreException("time out ms "+timeout);
            }
            else
                return asynReq.getResponse() ;

        } finally {
            if ( asynReq != null ) {
                map.remove( asynReq.getRequest().getHeader().getVersion() );
            }
        }
        //return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(Request request, Connection connection) {
        connection.write( request);
    }

    @Override
    public Response invoke(Request request, Connection connection) {
        if ( request.getType() != Request.RequestType.Invoker )
            throw new ConnectionException("request type "+request.getType()+" is not an invoker");
        else
            return sendRequest( request, connection );
    }
}
