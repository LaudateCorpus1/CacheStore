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
import com.sm.message.TCPCallBack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

public class ServerFilter extends BaseFilter {
    private static final Log logger = LogFactory.getLog(ServerFilter.class);

    //call back for incoming request
    private TCPCallBack callback;

    public ServerFilter() {
        this(null);
    }

    public ServerFilter(TCPCallBack callback) {
        this.callback = callback;
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx)
            throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Object peerAddress = ctx.getAddress();

        final Request message = ctx.getMessage();
        logger.info("receive "+message.getHeader().toString()+ " from "+ctx.getAddress().toString()+" payload len "
            + (message.getPayload() == null ?  0  :  message.getPayload().length ) ) ;
        Request req = new Request( message.getHeader(), new byte[] {0} );
        ctx.write(peerAddress, req, null);
        return ctx.getStopAction();
    }

}
