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

package com.sm.replica.client.grizzly;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.replica.ParaList;
import com.sm.store.StoreParas;
import com.sm.transport.AsynReq;
import com.sm.transport.grizzly.ClientFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

public class ReplicaClientFilter extends ClientFilter {
    private static final Log logger = LogFactory.getLog(ReplicaClientFilter.class);
    private int size = 20;

    public ReplicaClientFilter(long timeout) {
        super(timeout);
    }


    @Override
    public NextAction handleRead(FilterChainContext ctx)
            throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Object peerAddress = ctx.getAddress();

        final Request req = ctx.getMessage();
        if ( req.getHeader().getVersion() % size == 0)
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
        } //if != null
        else logger.warn(req.getHeader().toString()+" is not longer in map");
        return ctx.getStopAction();
    }

    private boolean isErrorInList(ParaList paraList) {
        for (StoreParas each : paraList.getLists()) {
            if ( each.getErrorCode() > StoreParas.OBSOLETE ) return true;
        }
        return false;
    }

}
