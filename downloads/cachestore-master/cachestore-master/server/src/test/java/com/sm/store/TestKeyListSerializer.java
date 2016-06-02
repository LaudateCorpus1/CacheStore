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
 */

package com.sm.store;

import com.sm.localstore.impl.HessianSerializer;
import org.testng.annotations.Test;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.impl.CacheValue;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class TestKeyListSerializer {

    @Test(groups = {"test"})
    public void testKeyParas() {
        int j = 10;
        List<Key> ls = new ArrayList<Key>(j);
        for ( int i = 0; i < j ; i++) {
            ls.add(Key.createKey(i));
        }
        KeyParas list = new KeyParas(ls);
        HessianSerializer hs = new HessianSerializer();
        byte[] serBytes = hs.toBytes( list);
        KeyParas list1 = (KeyParas) hs.toObject( serBytes);
        assertEquals(list1.getList().size(), list.getList().size());
        for ( int i=0 ; i < j ; i++) {
            assertEquals( (Integer) list.getList().get(i).getKey() ,(Integer) list1.getList().get(i).getKey()  );
        }
    }


    @Test(groups = {"test"})
    public void testStoreParas() {
       int j = 10;
        HessianSerializer hs = new HessianSerializer();
        List<StoreParas> ls = new ArrayList<StoreParas>(j);
        for ( int i = 0; i < j ; i++) {
            StoreParas paras = new StoreParas(OpType.Get, Key.createKey(i), CacheValue.createValue( new byte[]{ (byte) i}, i) );
            byte[] bytes = hs.toBytes( paras);
            StoreParas paras1 = (StoreParas) hs.toObject( bytes);
            System.out.println("i= "+i);
            assertEquals(paras.getErrorCode(), paras1.getErrorCode());
            assertEquals( (Integer) paras.getKey().getKey(), (Integer) paras1.getKey().getKey());

        }
    }
}
