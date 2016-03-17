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

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.netty.TCPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import static com.sm.transport.Utils.getOpts;

public class TestClient {
    private static Log logger = LogFactory.getLog(TestClient.class);



    public static TCPClient createClient(String host, int port) {
        TCPClient client =  TCPClient.start(host, port, new AppClientHandler(1000));
        if (client.getFuture().awaitUninterruptibly(5000)) logger.info("wait ok");
        if (client.getFuture().getChannel().isConnected() ) logger.info("connected");
        return client;
    }

    static class RunnerThread implements Runnable {
        TCPClient client;
        int times;
        long id;

        public RunnerThread(long id, int times, TCPClient client) {
            this.client = client;
            this.times = times;
            this.id = id;
        }

        RunnerThread(TCPClient client, int times, long id) {
            this.client = client;
            this.times = times;
            this.id = id;
        }

        @Override
        public void run() {
            for ( int i = 0; i < times ; i ++) {
                try {
                    logger.info("send "+ (i+id*100));
                    Response resp= client.sendRequest( new Request(new Header("test",i+id*100, (byte) 0, id), new byte[2048]) );
                    logger.info("resp =>"+resp.toString());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }

        }
    }
    public static void main(String[] args) throws Exception {
       String[] opts = new String[] {"-thread","-times","-host","-port"};
        String[] defaults = new String[] {"2","10", "localhost", "7100" };
        String[] paras = getOpts( args, opts, defaults);
        int threads = Integer.valueOf(paras[0]);
        int times  = Integer.valueOf( paras[1]);
        String host = paras[2];
        int port = Integer.valueOf( paras[3]);


        for ( int i = 0; i < threads ; i ++) {
            TCPClient client = createClient(host, port);
            logger.info("start thread  =>"+ i);
            new Thread(new RunnerThread(client, times, i), "thread-"+i ).start();
        }
    }
}
