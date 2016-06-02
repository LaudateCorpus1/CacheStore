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

package com.sm.replica;

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.replica.client.grizzly.ReplicaClientFilter;
import com.sm.transport.grizzly.TCPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.atomic.AtomicLong;


public class MockClient {
    private static final Log logger = LogFactory.getLog(MockClient.class);

    // name of storeName, use for server side to look up cachestore
    private String storeName;
    // url to conneøt to server
    private String url;
    // client side only, server send back byte[]
    //private Serializer serializer;
    // connection to server
    private TCPClient client;
    // for request sequence no
    private AtomicLong seqno = new AtomicLong(1);
    // queue freq default


    public MockClient(String url, String storeName) {
        this.url = url;
        //this.trxStore = trxLog;
        this.storeName = storeName;
        //this.logPath = logPath;
        init();
    }

    private void init() {
        String str[] = url.split(":");
        if (str.length != 2) throw new RuntimeException("Malform url "+url);
        try {
            //client = TCPClient.start(str[0], Integer.valueOf(str[1]), new ReplicaClientHandler(2));
            client = TCPClient.start(str[0], Integer.valueOf(str[1]), new ReplicaClientFilter(6000));
        } catch (RuntimeException ex) {
            logger.warn("fail to connect to "+ url);
        }
    }



    public Response sendRequest(Request request) throws Exception {
        if ( client != null && client.isConnected() ) return client.sendRequest( request);
        else {
            // release resource and create a new connection
            if ( client != null ) client.close();
            logger.info("try to reconnect "+url );
            init();
            if ( client == null ) throw new RuntimeException("fail to connect "+url);
            return client.sendRequest( request );
        }

    }

    private int batchSize = 10;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Header createHeader() {
        return new Header(storeName, seqno.getAndIncrement(), (byte) batchSize);
    }



}
