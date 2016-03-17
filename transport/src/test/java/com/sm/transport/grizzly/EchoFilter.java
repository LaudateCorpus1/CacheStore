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

package com.sm.transport.grizzly;

import com.sm.transport.TestClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

public class EchoFilter extends BaseFilter {
    private static Log logger = LogFactory.getLog(TestClient.class);

    protected boolean server = true;

    public EchoFilter(boolean server) {
        this.server = server;
    }

    /**
     * Handle just read operation, when some message has come and ready to be
     * processed.
     *
     * @param ctx Context of {@link org.glassfish.grizzly.filterchain.FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleRead(FilterChainContext ctx)
            throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Object peerAddress = ctx.getAddress();

        final String message = ctx.getMessage();
        logger.info("reeving from "+ctx.getConnection().getPeerAddress().toString()+" length "+message.length());
        if (server)
            ctx.write(peerAddress, "message length "+message.length(), null);
        else
            logger.info("msg "+message.toString());
        return ctx.getStopAction();
    }
}
