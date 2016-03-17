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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import voldemort.store.cachestore.Key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class TestStoreParaList implements Serializable  {

    HessianSerializer hs;

    @BeforeTest
    public void init() {
        hs = new HessianSerializer();
    }

    @Test(groups = {"test"})
    public void testResponse() {
//        StoreParas paras = new StoreParas(OpType.Get, Key.createKey("test-1"));
//
//        Response resp = new Response("test-1");
//        byte[] bytes = hs.toBytes( resp);
//
//        Object obj = hs.toObject(bytes);
//        System.out.println(obj.getClass().getName());
//        assertEquals( obj, resp);

    }

//    @Test(groups = {"test"})
//    public void testList() {
//        int j = 2;
//        List<StoreParas> ls = new ArrayList<StoreParas>(j);
//        for ( int i = 0; i < j ; i++) {
//            ls.add(new StoreParas(OpType.Get, Key.createKey(i), CacheValue.createValue(new byte[]{(byte) i}, i) ));
//        }
//        ScanParaList list = new ScanParaList(OpType.Get, ls);
//        HessianSerializer hs = new HessianSerializer();
//        byte[] bs = hs.toBytes( list);
//        //write2File( bs, "./test.log");
//        ScanParaList ls1 = (ScanParaList) hs.toObject( bs);
//        byte[] serBytes = hs.toBytes( list);
//        ScanParaList list1 = (ScanParaList) hs.toObject( serBytes);
//        assertEquals(ls.size(), ls1.size());
//        for ( int i=0 ; i < j ; i++) {
//            assertEquals((Integer) list.getList().get(i).getKey().getKey(), (Integer) list1.getList().get(i).getKey().getKey());
//        }
//
//    }
}
