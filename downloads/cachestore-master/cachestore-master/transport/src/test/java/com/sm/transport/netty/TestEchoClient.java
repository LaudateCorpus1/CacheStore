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
 */package com.sm.transport.netty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import static com.sm.transport.Utils.getOpts;

public class TestEchoClient {
    private static Log logger = LogFactory.getLog(TestEchoClient.class);


    public static TCPClient createTCPClient( String host, int port) {
        TCPClient client =  TCPClient.start(host, port,new EchoClientHandler(), new ObjectEncoder(1024*1024), new ObjectDecoder());
        if (client.getFuture().awaitUninterruptibly(5000)) logger.info("wait ok");
        if ( client.getFuture().getChannel().isConnected() ) logger.info("connected");
        return client;

    }

    public static String createString(int size) {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0 ; i < size ; i ++)
            sb.append("String createString, client send "+i);
        return sb.toString();
    }

    static class ClientThread implements Runnable {
        TCPClient client;
        int times;
        int size;

        ClientThread(TCPClient client, int times, int size) {
            this.client = client;
            this.times = times;
            this.size = size;
        }

        @Override
        public void run() {
            int error = 0;
            for ( int i = 0; i < times ; i ++) {
                logger.info("send "+i);
                try {
                    String message = createString(size + i * 100);
                    checkClient();
                    ((EchoClientHandler) client.getClientHandler()).sendObject( message, client.getFuture().getChannel());

                } catch (Throwable th) {
                    error++;
                    logger.error( th.getMessage());
                    checkClient();
                }
            }
            logger.info("ERROR # "+ error);
        }

        private void checkClient(){
            //logger.info("check client "+client.toString());
            if ( ! client.isConnected() ) {
                String host = client.getHost();
                int port = client.getPort();
                client.close();
                client.shutdown();
                client = TCPClient.start(host, port,new EchoClientHandler(), new ObjectEncoder(1024*1024), new ObjectDecoder());
                if (client.getFuture().awaitUninterruptibly(5000)) logger.info("wait ok");
                if ( client.getFuture().getChannel().isConnected() ) logger.info("connected");

            }
        }

    }



    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-thread","-times","-host","-port", "-size"};
        String[] defaults = new String[] {"2", "10", "localhost", "7120", "2000" };
        String[] paras = getOpts( args, opts, defaults);
        int threads = Integer.valueOf(paras[0]);
        int times  = Integer.valueOf( paras[1]);
        String host = paras[2];
        int size = Integer.valueOf( paras[4]);
        int port = Integer.valueOf( paras[3]);
        for ( int i=0; i < threads; i++ ) {
            logger.info("start thread # "+i);
            TCPClient client = createTCPClient(host, port);
            new Thread( new ClientThread(client, times, size)).start();
        }
        //System.exit(0);
    }
}
