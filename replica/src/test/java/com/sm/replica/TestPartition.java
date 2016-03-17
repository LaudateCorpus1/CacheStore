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

package com.sm.replica;

import com.sm.replica.client.PartitionFilter;
import com.sm.store.OpType;
import com.sm.store.StoreParas;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.voldeimpl.BlockValue;
import voldemort.utils.ByteArray;
import voldemort.utils.FnvHashFunction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.testng.AssertJUnit.assertEquals;

public class TestPartition {

    @BeforeClass
    public void setup(){

    }

    @Test( groups = {"parts"})
    public void testFilter() {
        int size = 4;
        List<StoreParas> list = new CopyOnWriteArrayList<StoreParas>();
        for ( int i = 0 ; i < size ; i++) {
            ByteArray by = new ByteArray(("test-"+i).getBytes());
            list.add( new StoreParas(OpType.Put, Key.createKey(by), new BlockValue(("test-"+i).getBytes()
            ,i, (short ) 0)) );
        }
        int[] p = new int[] {0,1};
        PartitionFilter filter = new PartitionFilter(p, new FnvHashFunction(), 4);
        List<StoreParas> nList = filter.applyFilter( list);
        assertEquals( nList.size(), 2);

    }
}
