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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Invoker;
import com.sm.store.client.ClusterClient;
import com.sm.store.client.ClusterClientFactory;
import com.sm.store.client.RemoteClientImpl;
import com.sm.store.client.RemoteValue;
import com.sm.store.client.grizzly.GZRemoteClientImpl;
import com.sm.store.client.netty.NTRemoteClientImpl;
import com.sm.store.client.netty.ScanClientImpl;
import com.sm.test.Person;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;

public class TestRemoteCall {
    private static final Log logger = LogFactory.getLog(TestRemoteCall.class);

    //@Test
    public void testStoreProc() {
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:7100", null, "store");
        Invoker invoker = new Invoker("StoreProc.groovy", "sayHello", new Object[] {"test" } );
        String res = (String) client.invoke( invoker);
        logger.info("result "+res);
    }

    //@Test(groups ="{Select}")
    public void testSelect(){
        Person.Address address = new Person.Address( new Person.Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, null);
        String queryStr = "select  address.zip , name, age, male, address.city, address.street from Person where address.zip > 90320 ";
        //queryStr = "select  name, age, male from Person ";
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:7100", null, "store");
        System.out.println("size "+client.query( queryStr).size());
        String json = client.query4Json("select  name, age, male, address.zip from Person where address.city = \"cypress\" ");
        System.out.println( "json :"+json);
//        for ( int i = 0 ; i < 10 ; i++) {
//            client.put(Key.createKey(i), person);
//            Value value =client.selectQuery(Key.createKey(i), queryStr );
//            System.out.println( "i "+i+" "+value.getData().toString());
//        }
//        queryStr = "replace Person set name=\"Test\", age = 10, male = false, address = { zip = 90630, city = \"cypress\" } ; ";
//        for ( int i = 0 ; i < 10 ; i++) {
//            client.put(Key.createKey(i), person);
//            client.updateQuery(Key.createKey(i), new RemoteValue(null, 0, (short) 0), queryStr);
//            Value value = client.get(Key.createKey(i));
//            System.out.println( "i "+i+" "+value.getData().toString());
//        }
        queryStr = "select name from Person ";
        for ( int i = 0 ; i < 10 ; i++) {
            Value value =client.selectQuery(Key.createKey(i), queryStr );
            System.out.println( "i "+i+" "+value.getData().toString());
        }

    }

    //@Test(groups = {"populate"})
    public void testPopulate(){
        HessianSerializer serializer = new HessianSerializer();
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:7100", null, "store");
        for ( int i = 0 ; i < 30 ; i++) {
            //new Person.Address(new Person.Street(i, "test-1"+i ), (90300+i), "cypress");
            Person person = new Person("mickey", i, 4000.00, true, new Person.Address(new Person.Street(i, "test-1"+i ), (90300+i), "cypress"));
            client.put(Key.createKey(i), person);
        }
        client.close();
    }

    //@Test(groups = {"replace"})
    public void testReplace() {
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:7100", null, "store");
        List list = client.query("replace Person set income = 20.0 where age <= 3 and age >= 0");
        System.out.println("size "+list.size()+" "+list.toString());
        String json = client.query4Json("select  name, age, male, address from Person where key# >= 0 and key# <= 3");
        System.out.println( "json :"+json);
    }

    //@Test(groups = {"Cluster"})
    public void testCluster() {
        ClusterClientFactory ccf = ClusterClientFactory.connect("localhost:6172", "userCrm");
        ClusterClient client = ccf.getDefaultStore(3000);
        client.get(Key.createKey(20));

    }

    //@Test(groups = {"FullQuery"})
    public void testFullQuery() throws Exception {
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:7100", null, "store");
        String json = client.query4Json("select  name, age, male, address from Person where key# >= 2 and key# <= 10");
        System.out.println( "json :"+json);
        System.out.println(client.query("select  name, age, male, address from Person where key# >= 2 and key# <= 10").toString());
    }

    //@Test(groups = {"multiUpdate"})
    public void testMultiPut() {
        Person.Address address = new Person.Address( new Person.Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, null);
        HessianSerializer serializer = new HessianSerializer();
        ScanClientImpl client = new ScanClientImpl("localhost:7100", null, "store");
        int times = 30;
        List<Key> lst = new ArrayList<Key>();
        for ( int i= 0; i < times; i ++ ) {
            lst.add(Key.createKey(i));
        }
        List<KeyValue> ton = client.multiRemoves(lst);
        System.out.println("multi removes "+ton.toString());
        List<KeyValue> list = new ArrayList<KeyValue>(times);
        for ( int i= 0; i < times; i ++ ) {
            Value value = new RemoteValue( serializer.toBytes(person) , 0, (short) 0);
            list.add(new KeyValue(Key.createKey(i),value));
        }
        List<KeyValue> toReturn = client.multiPuts( list);
        System.out.println("toReturn "+toReturn.toString());
        //toReturn = client.multiPuts( list);
        List<Key> keyList = new ArrayList<Key>(times);
        for ( int i= 0; i < times; i ++ ) {
            keyList.add(Key.createKey(i));
        }
        List<KeyValue> keyValueList = client.multiGets(keyList);
        toReturn = client.multiUpdateQuery(keyList, "replace Person set age=20, income=1000.00 ");
        keyValueList = client.multiGets(keyList);
        System.out.println(keyValueList.toString());

    }

    //@Test
    public void testJson() {
        ObjectMapper mapper = new ObjectMapper();
        int i = 1;
        Person.Address address = new Person.Address(new Person.Street(i, "test-1"+i ), (90300+i), "cypress");
        Person person = new Person("mickey", i, 4000.00, true, address);
        try {
            System.out.println(mapper.writeValueAsString(person));
            System.out.println( mapper.writeValueAsString( address));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    //@Test(groups ="{remoteProc}")
    public void testRemoteCall() {
        RemoteClientImpl client = new NTRemoteClientImpl("192.168.19.56:6172", null, "store");
        Invoker invoker = new Invoker("com.sm.crm.CrmMatch","getUserLists", new Object[] {"./file/01/oldEmailHashs2K.csv120", "ip", new Integer(100)} );
        Object obj = client.invoke( invoker);
        System.out.println(obj.toString());
    }

    //@Test(groups ="{remoteProc}")
    public void testSsusRemoteCall() {
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:7100", null, "store");
        Invoker invoker = new Invoker("com.sm.crm.SSUSProfile","getJsonB2C", new Object[] { new Long(3279152402342471027L) } );
        Object obj = client.invoke( invoker);
        System.out.println(obj.toString());
    }

    public final static int times = 20;
    //@Test(groups ="{remoteProc}")
    public void testGZRemoteCall() {
        RemoteClientImpl client = new GZRemoteClientImpl("localhost:6240", null, "test");
        int  i =0;
        long begin = System.nanoTime();
        int c = 0;
        Key key = Key.createKey(234);
        for ( ; i < times ; i++) {
            if ( client.get(key) == null)
                c++;
        }
        long dur = System.nanoTime() - begin;
        System.out.println("i ="+i +"  avg micro sec "+ dur/1000/times +" null "+c);
    }

    //@Test(groups ="{remoteProc}")
    public void testNTRemoteCall() {
        RemoteClientImpl client = new NTRemoteClientImpl("localhost:6240,remote1:6240", null, "test");
        int  i =0;
        long begin = System.nanoTime();
        int c = 0;
        Key key = Key.createKey(234);
        for ( ; i < times ; i++) {
            if ( client.get(key) == null)
                c++;
        }
        long dur = System.nanoTime() - begin;
        System.out.println("i ="+i +"  avg micro sec "+ dur/1000/times +" null "+c);

    }



    public static void main(String[] args) {
        String[] opts = new String[] {"-store","-url", "-times"};
        String[] defaults = new String[] {"store","localhost:7100", "10"};
        String[] paras = getOpts( args, opts, defaults);
        String store = paras[0];
        String url = paras[1];
        int times =  Integer.valueOf( paras[2]);
        RemoteClientImpl client = new NTRemoteClientImpl(url, null, store);

        for ( int i = 0; i < times ; i ++ ) {
         try {

             int j = i % 4;
             Integer res = null ;
             Invoker invoker ;
             switch (j ) {
                   case 0:
                       logger.info("+  j= "+j);
                       invoker = new Invoker("com.sm.store.Operation","add", new Integer[]{j++,j++} );
                       res = (Integer) client.invoke( invoker);
                       break;
                   case 1 :
                       logger.info("-  j= "+j);
                       invoker = new Invoker("com.sm.store.Operation","substract", new Integer[]{j+2,j} );
                       res = (Integer) client.invoke( invoker);
                       break;
                   case 2 :
                       logger.info("X  j= "+j);
                       invoker = new Invoker("com.sm.store.Operation","multiply", new Integer[]{j+5,j} );
                       res = (Integer) client.invoke( invoker);
                       break;
                   case 3 :
                       logger.info("Add  j= "+j);
                       invoker = new Invoker("com.sm.store.Operation","div", new Integer[]{j+10,j++} );
                       res = (Integer) client.invoke( invoker);
                       break;
                   default :
                       logger.info("unknown bucket j="+j);
             }
             logger.info("result i "+i+" j "+j+" res "+ res.toString());
             invoker = new Invoker("com.sm.store.Hello","greeting", new String[]{ "test-"+i} );
             logger.info("greeting "+ (String) client.invoke(invoker));

         } catch (Exception ex) {
             logger.error(ex.getMessage(), ex);
         }

        }
         client.close();
    }

}
