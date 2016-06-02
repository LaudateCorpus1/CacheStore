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

package com.sm.test;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.RemotePersistence;
import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.store.cachestore.voldeimpl.StoreIterator;
import voldemort.utils.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;

public class TestPst {
    private static final Log logger = LogFactory.getLog(TestPst.class);

    StoreIterator storeIterator;
    Serializer serializer = new HessianSerializer();

    public TestPst(String filename) {
        this.storeIterator = new StoreIterator(filename);
    }

    public List<Value> getListValues(String key, RemotePersistence client ) {
        int i = 0;
        List<Value> list = new ArrayList<Value>();
        List<Key> keys = new ArrayList<Key>();
        while( storeIterator.hasNext()) {
            try {
                Pair<Key, byte[]> pair = storeIterator.next();
                logger.info("find " + pair.getFirst().toString());
                //if ( pair.getFirst().getKey().toString().equals("camp100000454")) {
                Value orders = CacheValue.createValue(pair.getSecond());
                if ( orders != null)
                    keys.add( pair.getFirst() );
                    list.add(orders);
                i++;
                //}
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        for (i =0 ; i < list.size() ; i++) {
            client.put(keys.get(i), list.get(i));
        }
        return list;
    }

    public static void main(String[] args) {
        String[] opts = new String[]{"-store", "-url", "-times"};
        String[] defaults = new String[]{"campaigns", "localhost:7220", "10", "/Users/mhsieh/test/files", "campaigns.0"};
        String[] paras = getOpts(args, opts, defaults);
        String store = paras[0];
        String url = paras[1];
        int times = Integer.valueOf(paras[2]);
        String path = paras[3];
        String file = paras[4];
        RemotePersistence client = new NTRemoteClientImpl(url, null, store, false);
        TestPst testPst = new TestPst( path+"/"+file);
        testPst.getListValues("camp100000454", client);
        client.close();
        System.exit(0);
    }
}
