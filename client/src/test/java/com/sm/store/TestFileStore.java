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
import com.sm.store.utils.FileStore;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.voldeimpl.StoreIterator;
import voldemort.utils.Pair;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static voldemort.store.cachestore.BlockUtil.checkPath;


public class TestFileStore {

    //////@Test(groups = {"stores"})
    public void testStore() {
        checkPath("./data");
        FileStore fileStore = new FileStore("./data", "test");
        for (int i = 0; i < 10 ; i ++) {
            fileStore.writeRecord(Key.createKey(i), ("test-"+i).getBytes() );
        }
        fileStore.close();
        StoreIterator storeIterator = new StoreIterator("./data/test");
        int j = 0;
        while ( storeIterator.hasNext()) {
            try {
                Pair<Key, byte[]> pair = storeIterator.next();
                int k = (Integer) pair.getFirst().getKey();
                int l = j % 10 ;
                assertEquals( k  , l );
                assertEquals( pair.getSecond(), ("test-"+l).getBytes() );
                j++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            storeIterator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////@Test(groups = "files")
    public void testIterator() {
        StoreIterator storeIterator = new StoreIterator("/Users/mhsieh/test/log/concurrents.0");
        HessianSerializer serializer = new HessianSerializer();
        int j = 0;
        while ( storeIterator.hasNext()) {
            try {
                Pair<Key, byte[]> pair = storeIterator.next();
                String k = (String) pair.getFirst().getKey();
                System.out.println("second "+pair.getSecond() );
                byte[] bs =  (byte[]) serializer.toObject( pair.getSecond());
                System.out.println(" data "+ serializer.toObject( bs));
                j++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            storeIterator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
