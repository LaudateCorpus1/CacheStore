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

package com.sm.store.client.grizzly;

import com.sm.storage.Serializer;
import com.sm.store.client.netty.ScanClientImpl;
import com.sm.transport.grizzly.TCPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GZScanClientImpl extends ScanClientImpl {
    private static final Log logger = LogFactory.getLog(GZScanClientImpl.class);

    public GZScanClientImpl(String url, Serializer serializer, String store) {
        this(url, serializer, store, 1000);
    }

    public GZScanClientImpl(String url, Serializer serializer, String store, int queueSize) {
        this( url, serializer, store, queueSize, true);
    }

    public GZScanClientImpl(String url, Serializer serializer, String store, int queueSize, boolean nio) {
        this(url, serializer, store, queueSize, nio, 6000L);
    }

    public GZScanClientImpl(String url, Serializer serializer, String store, int queueSize, boolean nio, long timeout) {
        super(url, serializer, store, queueSize, nio, timeout);
    }
    protected void getTCPClient() {
        int count = urlArrays.length;
        for ( int i = 0; i < count; i++ ) {
            index = (++index) % count ;
            try {
                String[] str = urlArrays[index].split(":");
                setClient(TCPClient.start(str[0], Integer.valueOf(str[1]), new StoreClientFilter(timeout)));
                break;
            } catch (RuntimeException ex) {
                logger.warn("fail to connect to "+ url);
            }
        }
    }
}
