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

package com.sm.store.server.netty;

import com.sm.store.RemoteConfig;
import com.sm.store.server.RemoteStoreServer;
import com.sm.store.server.Scan4CallBack;
import com.sm.store.server.StoreServerHandler;
import com.sm.transport.netty.TCPServer;

public class Scan4RemoteServer extends RemoteStoreServer {
    private RemoteConfig remoteConfig;

    public Scan4RemoteServer(RemoteConfig remoteConfig) {
         this.remoteConfig = remoteConfig;
         this.port = remoteConfig.getPort();
         this.maxQueue = remoteConfig.getMaxQueue();
         this.maxThread = remoteConfig.getMaxThread();
         this.serializer = remoteConfig.getSerializer();
         if ( remoteConfig.getConfigList() == null || remoteConfig.getConfigList().size() == 0)
             throw new RuntimeException("ConfigList for store is empty");
         initConfig(remoteConfig);
     }


    @Override
    protected void init(boolean nio) {
        logger.info("start StoreCallBack "+storeList.toString() );
        callBack = new Scan4CallBack(remoteConfig);
        super.setTCPHandler( new StoreServerHandler(callBack, maxThread, maxQueue));
        logger.info("start server port "+port);
        //check useNio
        if (remoteConfig.isUseNio() )
            super.seTCPtServer( TCPServer.start(port, super.getTCPHandler()));
        else
            super.seTCPtServer(TCPServer.startOio(port, super.getTCPHandler()));
        //callBack.setServer( server);
        if (remoteConfig.getFreq() > 1)
            super.getTCPHandler().setFreq( remoteConfig.getFreq());
    }

}
