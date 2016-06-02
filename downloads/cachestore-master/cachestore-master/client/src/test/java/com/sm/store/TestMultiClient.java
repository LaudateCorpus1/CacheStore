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
 */package com.sm.store;


import com.sm.store.client.grizzly.GZRemoteClientImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;

import static com.sm.transport.Utils.getOpts;

public class TestMultiClient {
    private static Log logger = LogFactory.getLog(TestClient.class);



    static class RunnerThread implements Runnable {
        com.sm.store.client.RemoteClientImpl client;
        int times;
        long id;

        public RunnerThread(long id, int times, com.sm.store.client.RemoteClientImpl client) {
            this.client = client;
            this.times = times;
            this.id = id;
        }

        RunnerThread(com.sm.store.client.RemoteClientImpl client, int times, long id) {
            this.client = client;
            this.times = times;
            this.id = id;
        }

        @Override
        public void run() {
            int error = 0 ;
            for ( int i = 0; i < times ; i ++) {
                try {
                    int no = client.getSeqNoInt(Key.createKey("key") );
                    logger.info("key "+i+" => "+ no) ;
                } catch (Exception ex) {
                    error ++;
                    logger.error(ex.getMessage(), ex);
                }
            }
            logger.info("times "+times+" error "+error);
        }
    }

    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-thread","-times","-host","-port", "-store"};
        String[] defaults = new String[] {"2","10", "localhost", "7100", "keystore" };
        String[] paras = getOpts( args, opts, defaults);
        int threads = Integer.valueOf(paras[0]);
        int times  = Integer.valueOf( paras[1]);
        String host = paras[2];
        int port = Integer.valueOf( paras[3]);
        String store = paras[4];

        for ( int i = 0; i < threads ; i ++) {
            com.sm.store.client.RemoteClientImpl client =new GZRemoteClientImpl(host+":"+port, null , store)  ;
            logger.info("start thread  =>"+ i);
            new Thread(new RunnerThread(client, times, i), "thread-"+i ).start();
        }
    }
}
