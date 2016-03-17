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

package com.sm.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

import static com.sm.transport.Utils.getOpts;

public class TestConnect {
    private static final Log logger = LogFactory.getLog(TestConnect.class);

    @Test
    public void testRandom() {
        Random random = new Random();
        System.out.println("out side of loop");
        for (int i =0 ; i < 20; i++) {
            int r = random.nextInt(2);
            System.out.println("i " + i + " r " + r);
        }


        System.out.println("inside of loop");
        for (int i =0 ; i < 20; i++) {
            Random r = new Random();
            System.out.println("i " + i + " r " + r.nextInt(2));
        }

    }

    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-host","-port", "-times"};
        String[] defaults = new String[] {"ssusd003.ir.dc","6666", "500"};
        String[] paras = getOpts( args, opts, defaults);
        String host = paras[0];
        int port = Integer.valueOf( paras[1]);
        int timeout = Integer.valueOf(paras[2]);

//        logger.info("pos="+"/test/".indexOf("/"));
//        logger.info("/test/".substring(0, "/test/".indexOf("/")));
//        BuildClusterNodes bc = new BuildClusterNodes("./clusterStore/src/test/resources/config.xml");
//        logger.info(bc.build().toString());
//        BuildStoreConfig bs = new BuildStoreConfig("./clusterStore/src/test/resources/config.xml");
//        logger.info(bs.build().toString());
       long begin = System.currentTimeMillis();
       SocketAddress inet = new InetSocketAddress(host, port);
        try {

            Socket socket = new Socket();
            socket.connect( inet, timeout);
            socket.close();
        } catch (Exception ex) {
            logger.error("ex "+ex.toString());
        }
        logger.info("time in Socket ms "+(System.currentTimeMillis() - begin));
//        begin = System.currentTimeMillis();
//        try {
//            TCPClient client = TCPClient.start(host, port);
//            client.close();
//        } catch (Exception ex) {
//            logger.error( ex.getMessage());
//        }
//        logger.info("time in TCPClient ms "+(System.currentTimeMillis() - begin));


    }
}
