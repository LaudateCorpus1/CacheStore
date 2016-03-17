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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Invoker;
import com.sm.storage.Serializer;
import com.sm.store.client.ClusterClient;
import com.sm.store.client.ClusterClientFactory;
import com.sm.store.client.RemoteValue;
import com.sm.store.client.TCPClientFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;
import static org.testng.Assert.assertEquals;

public class TestClusterClient {
    private static final Log logger = LogFactory.getLog(TestClusterClient.class);
    ClusterClient client;
    Serializer serializer;

    public TestClusterClient(ClusterClient client) {
        this.client = client;
        this.serializer = new HessianSerializer();
    }

    public void testMultiGet(int times) {
        List<Key> list = new ArrayList<Key>(times);
        for ( int i= 0; i < times; i++) {
            list.add(Key.createKey("test-"+i));
        }
        List<KeyValue> kvList = client.multiGets(list);
        logger.info("multi get " + kvList.size()+ " "+list.size() +" match "+ (kvList.size() == list.size() ? "true" : "false") );
//        for ( int i =0 ; i < times ; i+= 2) {
//            assertEquals( list.get(i).getKey(), kvList.get(i).getKey().getKey());
//            assertEquals( "times-"+i, (String) serializer.toObject( (byte[]) kvList.get(i).getValue().getData() ));
//        }
    }

    public void testMultiPut(int times) {
        List<KeyValue> list = new ArrayList<KeyValue>(times);
        for ( int i= 0; i < times; i ++ ) {
            Value value = new RemoteValue((serializer.toBytes("times" + i)), 0, (short) 0);
            list.add(new KeyValue(Key.createKey("test-"+i),value));
        }
        List<KeyValue> kvList = client.multiPuts( list);
        assertEquals(kvList.size(), list.size());

    }

    public void testScan(int times) {
        List<KeyValue> kvList = client.scan( Key.createKey("test-0"), Key.createKey("test-"+(times -1)));
        logger.info("size = "+ kvList.size() );
    }

    public void testScanCursor(int times) {
        ClusterClient.ClusterCursor clusterCursor = client.openScanCursor( (short) 50, Key.createKey("test-0"), Key.createKey("test-499"));
        System.out.println( "size "+clusterCursor.getResult().size());
        while ( ! clusterCursor.isEnd()) {
            client.nextCursor( clusterCursor);
            System.out.println( "size "+clusterCursor.getResult().size());

        }
    }

    public List<Key> createKeyList(int no) {
        Key key = Key.createKey("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        List<Key> list = new ArrayList<Key>(no);
        for ( int i = 0; i < no ; i ++) {
            list.add(key);
        }
        return list;
    }

    public void testInvoker(int no, int times) {
        Invoker invoker = new Invoker("com.sm.store.Hello", "echo", new Object[] {});
//        List<Key> list = new ArrayList<Key>();
//        list.add(Key.createKey(1));
        List<Key> list = createKeyList(no);
        for ( int i =0 ; i < times; i ++) {
            List<Object> lst = client.clusterStoreProc(invoker, list);
        }
        invoker = new Invoker("com.sm.store.Hello", "greeting", new Object[] {"store proc"});
        List<Object> lst1 = client.invoke(invoker);
        //int result = (Integer) client.invoke( invoker);
        //assertEquals( result, 3);
        logger.info("pass invoker test");
    }

    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-configPath", "-url", "-store", "-times", "-nio", "-size"};
        String[] defaults = new String[] {"", "localhost:6172", "store", "2", "true", "2000"};
        String[] paras = getOpts( args, opts, defaults);

        String configPath = paras[0];
        if (  configPath.length() == 0 ) {
            //logger.error("missing config path or host");
            //throw new RuntimeException("missing -configPath or -host");
        }

        String url = paras[1];
        String store = paras[2];
        int times = Integer.valueOf( paras[3]);
        boolean nio = Boolean.valueOf( paras[4]);
        int size = Integer.valueOf( paras[5]);
        ClusterClientFactory ccf =ClusterClientFactory.connect(url, store, new HessianSerializer(), TCPClientFactory.ClientType.Netty);
        ClusterClient client = ccf.getDefaultStore(3000);
        TestClusterClient testClient = new TestClusterClient( client);
        client.put(Key.createKey("test-0"), "test-0");
//        testClient.testMultiPut(500);
//        testClient.testMultiGet(500);
//        testClient.testScan(500);
//        testClient.testScanCursor(500);
        Value v = client.get(Key.createKey("test-0")) ;
        System.out.println( v.getData() );
        //ccf.reloadClusterClient();
        client.close();
        ccf.close();
    }
}
