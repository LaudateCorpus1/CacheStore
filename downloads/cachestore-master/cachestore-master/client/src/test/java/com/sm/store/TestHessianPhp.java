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
import com.sm.message.Header;
import com.sm.store.hessian.HessianPhp;
import com.sm.store.hessian.HessianReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHessianPhp {
    private static Log logger = LogFactory.getLog(TestHessianPhp.class);
    public static String header ="O:6:\"Header\":4:{s:4:\"name\";s:6:\"test-1\";s:7:\"version\";i:10;s:7:\"release\";i:3;s:6:\"nodeId\";i:1;}";
    public static String array ="a:3:{s:2:\"a1\";i:10;s:2:\"b1\";i:20;s:2:\"C1\";O:6:\"Header\":4:{s:4:\"name\";s:6:\"test-1\";s:7:\"version\";i:10;s:7:\"release\";";
    public static String storePara ="O:10:\"StoreParas\":5:{s:7:\"optType\";i:3;s:3:\"key\";s:7:\"store-1\";s:5:\"value\";O:5:\"Value\":3:{s:7:\"version\";i:1;s:4:\"node\";i:2;s:5:\"datas\";s:7:\"value-1\";}s:9:\"errorCode\";i:0;s:6:\"remove\";i:0;}";

    public static Map<String, String> getNameMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Header", "com.sm.message.Header");
        map.put("StoreParas", "com.sm.store.StoreParas");
        map.put("Value","com.sm.store.Value");
        map.put("array", "java.util.HashMap");
        return map;
    }

    public static void main(String[] args) throws Exception {
        HessianSerializer hs = new HessianSerializer();
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("Header", "com.sm.message.Header");
//        map.put("StoreParas", "com.sm.store.StoreParas");
//        map.put("Value","com.sm.store.Value");
//        map.put("array", "java.util.HashMap");
        HessianPhp hessianPhp = new HessianPhp(getNameMap());
        Map<String, Header> mapH = new HashMap<String, Header>();
        mapH.put("key-1", new Header("test-1", 1 , (byte)2, 3));
        mapH.put("key-2", new Header("test-2", 2, (byte) 3, 4));
//        byte[] m1 = hs.toBytes( mapH);
//        System.out.println("m1 "+m1.length);
//        Header hd = new Header("test-1", 2, (byte)1, 3);
//        byte[] d2 = hs.toBytes( hd);
//        HessianReader reader = new HessianReader( m1);
//        reader.readObject();
//        System.out.println(new String( reader.getBytes()));
        List list = new ArrayList();
        list.add( new Header("test-1", 1 , (byte)2, 3));
        list.add(new Header("test-2", 2, (byte) 3, 4));
        byte[] d3 = hs.toBytes(list);
        HessianReader reader = new HessianReader( d3);
        reader.readObject();
        System.out.println(new String( reader.getBytes()));
        //        byte[] data = hessianPhp.php2Hessian( header.getBytes());
//        Header header = new Header("test-1",10, (byte) 3,1 );
//        byte[] d1 = hs.toBytes( header);
        //logger.info("len "+data.length+" "+d1.length);
        //Object obj = hs.toObject(data);
        //logger.info(obj.getClass().getName()+" obj "+obj.toString());
//        byte[] s1 = hessianPhp.php2Hessian( storePara.getBytes());
//        Object obj = hs.toObject( s1);
//        logger.info(obj.toString());

    }
}
