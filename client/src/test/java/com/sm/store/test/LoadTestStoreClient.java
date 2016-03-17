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

package com.sm.store.test;

import com.sm.store.client.RemoteClientImpl;
import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;

import static com.sm.transport.Utils.getOpts;
import static org.testng.Assert.assertEquals;

public class LoadTestStoreClient {
    private static Log logger = LogFactory.getLog(LoadTestStoreClient.class);


    public static String createString(int size) {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0 ; i < size ; i ++)
            sb.append("String createString, client send "+i);
        return sb.toString();
    }

    static class ClientThread implements Runnable {
        RemoteClientImpl client;
        int times;

        ClientThread(RemoteClientImpl client, int times) {
            this.client = client;
            this.times = times;
        }

        @Override
        public void run() {
            int error = 0;
            for ( int i = 0; i < times ; i ++) {
                logger.info("send "+i);
                try {
                    Key key = Key.createKey(i);
                    String v = createString(i);
                    client.put( key, v);
                    Value value = client.get(key);
                    assertEquals( v, value.getData());
                } catch (Throwable th) {
                    error++;
                    logger.error( th.getMessage());

                }
            }
            logger.info("ERROR # "+ error);
        }
    }


    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-thread","-times","-url","-store"};
        String[] defaults = new String[] {"2", "10", "localhost:7100", "store" };
        String[] paras = getOpts( args, opts, defaults);
        int threads = Integer.valueOf(paras[0]);
        int times  = Integer.valueOf( paras[1]);
        String url = paras[2];
        String store =  paras[3];
        RemoteClientImpl client = new NTRemoteClientImpl(url, null, store);
        for ( int i=0; i < threads; i++ ) {
            logger.info("start thread # "+i);
            new Thread( new ClientThread(client, times)).start();
        }
        //System.exit(0);
    }
}

