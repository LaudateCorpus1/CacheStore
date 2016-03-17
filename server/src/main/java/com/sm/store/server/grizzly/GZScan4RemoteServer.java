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

package com.sm.store.server.grizzly;

import com.sm.store.RemoteConfig;
import com.sm.store.server.Scan4CallBack;
import com.sm.transport.grizzly.TCPServer;

public class GZScan4RemoteServer extends GZRemoteStoreServer {
    private RemoteConfig remoteConfig;

    public GZScan4RemoteServer(RemoteConfig remoteConfig) {
        this.remoteConfig = remoteConfig;
        this.serializer = remoteConfig.getSerializer();
        this.port = remoteConfig.getPort();
        if ( remoteConfig.getConfigList() == null || remoteConfig.getConfigList().size() == 0)
            throw new RuntimeException("ConfigList for store is empty");
        initConfig(remoteConfig);
    }

    @Override
    protected void init(boolean nio) {
        logger.info("start StoreCallBack "+storeList.toString() );
        callBack = new Scan4CallBack(remoteConfig);
        //handler = new StoreServerHandler(callBack, maxThread, maxQueue);
        super.setGZHandler(new StoreServerFilter(callBack, maxThread, maxQueue));
        logger.info("start server port "+port);
        super.setGZServer( TCPServer.start(port, super.getGZHandler()) );
        if ( remoteConfig.getFreq() > 0)
            super.getGZHandler().setFreq( remoteConfig.getFreq() );
        //callBack.setServer( server);
    }
}
