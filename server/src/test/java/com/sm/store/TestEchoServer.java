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

import com.sm.store.server.RemoteStore;
import com.sm.transport.netty.TCPServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.sm.transport.Utils.getOpts;

public class TestEchoServer {
  private static final Log logger = LogFactory.getLog(TestEchoServer.class);

   public static void main(String[] args) {
        String[] opts = new String[] {"-store","-path", "-port" } ;
        String[] defaults = new String[] {"store","./storepath", "7420"  };
        String[] paras = getOpts( args, opts, defaults);
        String p0 = paras[0];
        int port = Integer.valueOf( paras[2]);
        logger.info("start server at "+port);
        TCPServer s =  TCPServer.start( port, new AppSyncHandler(2, 100));
        RemoteStore remoteStore = new RemoteStore("Test", null, 1);

    }
}
