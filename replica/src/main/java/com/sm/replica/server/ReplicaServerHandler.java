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

package com.sm.replica.server;

import com.sm.message.Request;
import com.sm.replica.ParaList;
import com.sm.store.OpType;
import com.sm.store.StoreParas;
import com.sm.transport.Utils.ServerState;
import com.sm.utils.ThreadPoolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheStore;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.versioning.ObsoleteVersionException;

import java.util.Map;
import java.util.concurrent.*;

import static com.sm.store.StoreParas.*;
import static com.sm.store.Utils.isOverLoaded;

public class ReplicaServerHandler extends SimpleChannelUpstreamHandler  {
    private static final Log logger = LogFactory.getLog(ReplicaServerHandler.class);

    private Map<String, CacheStore> storeMap;
    protected ServerState serverState;
    protected int freq =20;
    protected int maxThreads;
    protected int maxQueue ;
    protected Executor threadPools;

    public ReplicaServerHandler() {
    }

    public ReplicaServerHandler(Map<String, CacheStore> storeMap) {
        this(storeMap,  Runtime.getRuntime().availableProcessors() );
    }

    public ReplicaServerHandler(Map<String, CacheStore> storeMap, int maxThreads) {
        this.storeMap = storeMap;
        serverState = ServerState.Active;
        this.maxThreads = maxThreads;
        init();
    }

    private void init() {
        if ( Runtime.getRuntime().availableProcessors() > maxThreads )
            this.maxThreads = Runtime.getRuntime().availableProcessors();
        if ( maxQueue < maxThreads * 1000 )
            maxQueue = maxThreads * 1000;
        BlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>(maxQueue );
        threadPools = new ThreadPoolExecutor( maxThreads, maxThreads , 30, TimeUnit.SECONDS , queue, new ThreadPoolFactory("Replica") );
    }

    public void shutdown() {
        logger.warn("server in shutdown state");
        serverState = ServerState.Shutdown;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
      super.handleUpstream(ctx, e);
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Request req =  (Request) e.getMessage();
        if ( req.getHeader().getVersion() % freq == 0)
            logger.info("receive " + req.toString()+" from "+e.getRemoteAddress().toString());
        try {
            if ( isOverLoaded( (ThreadPoolExecutor) threadPools))  {
                logger.warn( "thread pools overload !!! close connection");
                ctx.getChannel().close();
            }
            else
                threadPools.execute( new SyncReplCall( req, ctx));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ctx.getChannel().close();
        }
    }

    protected void setError(ParaList paraList, String message) {
        for (StoreParas each : paraList.getLists()) {
            // set for Store Exception
            each.setErrorCode(2 );
            if ( each.getOpType() == OpType.Put ) {
                each.getValue().setData( message.getBytes());
            }
        }
    }

    public void processParaList(Request req, ChannelHandlerContext ctx) {
        ParaList paraList = null;
        try {
            CacheStore store = storeMap.get( req.getHeader().getName() );
            if ( store == null )
                throw new StoreException("Store "+req.getHeader().getName()+" not exits");
            //check request type it must be Normal which was send by Replica Client
            if ( req.getType() != Request.RequestType.Normal) {
                logger.error("wrong request type expect Normal but get "+req.getType().toString());
                ctx.getChannel().close();
            }
            else {
                paraList = ParaList.toParaList( req.getPayload() );
                if ( store.isShutDown() ) {
                    logger.error("server in shut down state, close channel");
                    ctx.getChannel().close();
                    return;
                }
                for ( int i=0; i< paraList.getSize() ; i++) {
                    processRequest( store, paraList.getLists().get(i));
                }
                req.setPayload( paraList.toBytes());
                ctx.getChannel().write( req);
            }
        } catch (Exception ex) {
            logger.error("close channel "+ex.getMessage()+" "+ctx.getChannel().getRemoteAddress(), ex);
            if ( paraList != null ) {
                setError( paraList, ex.getMessage());
                req.setPayload(  paraList.toBytes());
                ctx.getChannel().write(req);
            }
            else
                ctx.getChannel().close();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      logger.error( e.getCause().getMessage(), e.getCause() );
      e.getChannel().close();
    }

    protected void processRequest(CacheStore store, StoreParas paras) {
        try {
             switch( paras.getOpType() ) {
                 case Get: {
                     Value value = processGet(store, paras.getKey() );
                     paras.setErrorCode(NO_ERROR );
                     paras.setValue(value);
                     break;
                 }
                 case Put: {
                     processPut(store, paras.getKey(), paras.getValue() );
                     paras.setErrorCode( NO_ERROR);
                     paras.setValue(null);
                     break;
                 }
                 case Remove: {
                     boolean rs = processRemove(store, paras.getKey());
                     paras.setErrorCode( NO_ERROR);
                     paras.setRemove( rs ? (byte) 0 : (byte) 1);
                     break;
                 }
                 default:  {
                     logger.warn("unknown type "+paras.getKey().toString());
                     paras.setErrorCode( STORE_EXCEPTION);
                     checkValue( paras);
                     paras.getValue().setData( "unknown type".getBytes());
                 }
             }
        } catch( ObsoleteVersionException sx) {
             paras.setErrorCode( OBSOLETE);
             paras.getValue().setData( sx.getMessage().toString().getBytes());

        } catch (Exception ex) {
             logger.error( ex.getMessage(), ex);
             paras.setErrorCode( STORE_EXCEPTION);
             byte[] data ;
             if ( ex.getMessage() != null )
                 data = ex.getMessage().getBytes();
             else
                 data = new byte[0];
             checkValue( paras);
             paras.getValue().setData(data );
        }

    }

    // make sure value is not null
    private void checkValue(StoreParas paras) {
        if ( paras.getValue() == null  ) {
            paras.setValue(CacheValue.createValue(new byte[0]));
        }
    }


    private Value processGet(CacheStore store, Key key) {
        return store.get(key);
    }

    private void processPut(CacheStore store, Key key, Value value) {
        store.put(key, value);

    }

    private boolean processRemove(CacheStore store, Key key){
        return store.remove(key);
    }

    class SyncReplCall implements Runnable {

        private Request request;
        private ChannelHandlerContext ctx;

        public SyncReplCall(Request request, ChannelHandlerContext ctx) {

            this.request = request;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                processParaList( request, ctx);
            } catch (Throwable th) {
                logger.error(th.getMessage(), th);
            }
        }

    }

}
