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

package com.sm.replica;

import com.sm.store.OpType;
import com.sm.store.StoreParas;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.voldeimpl.BlockValue;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class TestParaList {

    @BeforeClass
    public void setup(){

    }

    @Test(groups = { "test"})
    public void TestParaList() {
        int size = 3;
        List<StoreParas> list = new ArrayList<StoreParas>(size);
        for ( int i = 0 ; i < size ; i++) {
            list.add( new StoreParas(OpType.Put, Key.createKey("test-"+i), new BlockValue(("test-"+i).getBytes()
            ,i, (short ) 0)) );
        }
        byte[] bytes = new ParaList( list).toBytes();
        ParaList paraList = ParaList.toParaList( bytes);
        assertEquals(paraList.getSize(), list.size());
        int i = 0;
        for ( StoreParas each : paraList.getLists()){
            assertEquals( each.getOpType(), list.get(i).getOpType() );
            assertEquals( (byte[]) each.getValue().getData(), (byte[]) list.get(i++).getValue().getData());
        }
    }

//    public static void main(String[] args){
//        TestParaList ts = new TestParaList();
//        ts.TestParaList();
//    }
}
