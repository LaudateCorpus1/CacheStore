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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Invoker;
import com.sm.storage.Serializer;
import com.sm.store.client.RemoteValue;
import com.sm.store.client.grizzly.GZScanClientImpl;
import com.sm.store.client.netty.ScanClientImpl;
import com.sm.test.Person;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;
import static org.testng.Assert.assertEquals;

public class TestScanStoreClient {
    private static final Log logger = LogFactory.getLog(TestScanStoreClient.class);
    String store;
    String url;
    ScanClientImpl client;
    Serializer serializer = new HessianSerializer();

    public TestScanStoreClient(String store, String url) {
        this.store = store;
        this.url = url;
        client = new GZScanClientImpl(url, null, store, 100, false);
    }


    public void testStorePara(int times) {
        for ( int i = 0; i < times ; i ++ ) {
            try {
                Key key = Key.createKey(i);
                client.put( key, "times-"+i);
                //Thread.sleep( 1000L* i);
                Value value = client.get( key);
                logger.info(value == null ? "null" : value.getData().toString());
                //logger.info("remove key " + key + " " + client.remove(key));
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        //client.close();
    }

    public void testMultiGet(int times) {
        List<Key> list = new ArrayList<Key>(times);
        for ( int i= 0; i < times; i++) {
            list.add(Key.createKey(i));
        }
        List<KeyValue> kvList = client.multiGets(list);
        logger.info("multiget " + kvList.size()+ " "+list.size() +" match "+ (kvList.size() == list.size() ? "true" : "false") );
        for ( int i =0 ; i < times ; i++) {
            assertEquals( list.get(i).getKey(), kvList.get(i).getKey().getKey());
            //assertEquals( "times-"+i, (String) serializer.toObject( (byte[]) kvList.get(i).getValue().getData() ));
        }
    }

    public void testMultiplePut(int times) {
        List<KeyValue> list = new ArrayList<KeyValue>(times);
        for ( int i= 0; i < times; i ++ ) {
            Value value = new RemoteValue( serializer.toBytes(new Person("times-" + i, i)) , 0, (short) 0);
            list.add(new KeyValue(Key.createKey(i),value));
        }
        List<KeyValue> toReturn = client.multiPuts( list);
        assertEquals( toReturn.size(), list.size());
    }

    public void testScan(int times) {
//        List<KeyValue> list = client.scan(Key.createKey("cmp1000013.grdA.day1365145200"),
//                Key.createKey("cmp1000013.grdA.day1367996399"));
//        Value value = client.get(Key.createKey("cmp1000001.grdA.day1364799600"));
//        logger.info("value "+ (value == null ? "null": value.getVersion())+" list size "+list.size());
        List<KeyValue> list = client.scan(Key.createKey(0), Key.createKey(10), "select  name, age from Person ");
        logger.info("scan size "+list.size()+ " "+ times );
        assertEquals(list.size(), times);
    }


    private void render(CursorPara cursorPara) {
        for ( KeyValue keyValue : cursorPara.getKeyValueList()) {
            logger.info("keyValue "+keyValue.getKey().toString()+" "+
                  (keyValue.getValue() == null ? "null" : serializer.toObject( (byte[]) keyValue.getValue().getData())) );
        }
    }

    public void testGetTrigger() {
        for ( int i =0 ; i < 10 ; i++) {
            Value value = client.get(Key.createKey(i));
            logger.info("key "+i+ " "+value.getData().toString());
        }
    }

    public void testCursor(){
        //CursorPara cursorPara = new CursorPara("test", (short) 10, Key.createKey("test-1"), Key.createKey("test-200") );
        CursorPara cursorPara = client.openScanCursor((short) 10, Key.createKey(1), Key.createKey(30));
        render( cursorPara);
        cursorPara = client.nextCursor( cursorPara);
        render(cursorPara);
        for ( int i = 0; i < 5 ; i++) {
            cursorPara = client.nextCursor( cursorPara);
            render(cursorPara);
        }
        client.closeCursor(cursorPara);
    }

    public void testKeyValueSet() {
        CursorPara  cursorPara = client.openKeyValueCursor((short) 10) ;
        render( cursorPara);
        int i = 0;
        while (! cursorPara.isEnd() ) {
            cursorPara = client.nextCursor( cursorPara);
            logger.info("times "+ (++i));
        }

        client.closeCursor(cursorPara);
    }

    public void testKeySet() {
        CursorPara  cursorPara = client.openKeyCursor((short) 10) ;
        render( cursorPara);
        int i = 0;
        while (! cursorPara.isEnd() ) {
            cursorPara = client.nextCursor( cursorPara);
            logger.info("times "+ (++i));
        }
        client.closeCursor(cursorPara);
    }

    public void test4Basic() {
        for ( int i =0 ; i < 40 ; i++) {
            Key key = Key.createKey(i);
            Value value = client.get(key);
            logger.info("key "+i+ " "+value.getData().toString());
            client.put(key, value);
            client.remove(key);
            client.insert(key, value);
        }
    }

    public void testInvoker() {
        Invoker invoker = new Invoker("com.sm.store.Operation", "add", new Object[] { 1 , 2});
        int result = (Integer) client.invoke( invoker);
        assertEquals( result, 3);
        logger.info("pass invoker test");
    }

    public void close() {
        client.close();
    }

    public static void main(String[] args) {
        String[] opts = new String[] {"-store","-url", "-times"};
        String[] defaults = new String[] {"store","localhost:7220", "40"};
        String[] paras = getOpts( args, opts, defaults);
        String store = paras[0];
        String url = paras[1];
        int times =  Integer.valueOf( paras[2]);
        TestScanStoreClient tc = new TestScanStoreClient( store, url);
        tc.testMultiplePut(times);
        tc.testMultiGet(times);
//        tc.test4Basic();
        tc.testInvoker();
        tc.testCursor();
//        tc.testKeySet();
//        tc.testGetTrigger();
//        tc.testStorePara(times);

        tc.testScan( times);
        tc.close();
        System.exit(0);

    }
}
