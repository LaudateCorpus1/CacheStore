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

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.impl.CacheValue;

import java.nio.ByteBuffer;

public class TestStorePara {
    byte[] data;
    StoreParas paras;


    @BeforeTest
    public void init() {
        CacheValue ca = CacheValue.createValue(new byte[1024], 1L, (short) 2);
        paras = new StoreParas(OpType.Get, Key.createKey("test"), ca);
        data = paras.toBytes();

    }

    //@Test(groups = { "serialize" })
    public void testSer() {
        StoreParas p = StoreParas.toStoreParas( data);
        Assert.assertEquals( paras.getKey(), p.getKey() );
        Assert.assertEquals( paras.getOpType(), p.getOpType() );
        Assert.assertEquals( paras.getValue().getData(), p.getValue().getData());

    }

    //@Test( groups = { "serialize"})
    public void testByte() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[] { 0X20 });
        short t = buf.get();
        Assert.assertEquals( t, (short) 32);

    }
}
