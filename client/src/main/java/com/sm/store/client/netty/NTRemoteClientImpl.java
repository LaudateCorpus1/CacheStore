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

package com.sm.store.client.netty;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.client.RemoteClientImpl;
import com.sm.store.client.netty.StoreClientHandler;
import com.sm.transport.netty.TCPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class NTRemoteClientImpl extends RemoteClientImpl {
    private static final Log logger = LogFactory.getLog(StoreClientHandler.class);

    public NTRemoteClientImpl(String url, Serializer serializer, String store) {
        this(url, serializer, store, true);
    }

    public NTRemoteClientImpl(String url, Serializer serializer, String store, boolean nio) {
        this(url, serializer, store, nio, 6000L);
    }

    public NTRemoteClientImpl(String url, Serializer serializer, String store, boolean nio, long timeout){
        this.url = url;
        this.store = store;
        this.nio = nio;
        this.timeout = timeout;
        if (serializer == null)
            this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
        urlArrays = url.split(",");
        init();
    }

    @Override
    protected void getTCPClient() {
        int count = urlArrays.length;
        for ( int i = 0; i < count; i++ ) {
            index = (++index) % count ;
            try {
                String[] str = urlArrays[index].split(":");
                if (nio)
                    client = TCPClient.start(str[0], Integer.valueOf(str[1]), new StoreClientHandler(timeout));
                else
                    client = TCPClient.startOio(str[0], Integer.valueOf(str[1]), new StoreClientHandler(timeout));
                // exit when it find a connection
                break;
            } catch (RuntimeException ex) {
                client = null;
                logger.warn("fail to connect to "+ url);
            }
        }

    }
}
