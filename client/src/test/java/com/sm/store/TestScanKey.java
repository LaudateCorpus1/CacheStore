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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.impl.SortedCacheStore;

import java.util.concurrent.ConcurrentSkipListMap;

import static com.sm.transport.Utils.getOpts;

public class TestScanKey {
    private static final Log logger = LogFactory.getLog(TestScanKey.class);

    public static void main(String[] args) throws Exception {
       String[] opts = new String[] {"-store","-path", "-port", "-mode", "-sorted"};
        String[] defaults = new String[] {"store","./data", "7100", "0", "true" };
        String[] paras = getOpts( args, opts, defaults);
        String store = paras[0];
        int port = Integer.valueOf( paras[2]);
        String path = paras[1];
        int mode = Integer.valueOf(paras[3]);
        SortedCacheStore sortedStore = new SortedCacheStore(path, null, 0, store, true, mode );
        Key from = findFloorKey( sortedStore.getSkipListMap(), Key.createKey("cmp1000000"));
        Key to = findCeilKey( sortedStore.getSkipListMap() , Key.createKey("cmp1000101") );
        System.out.print("test "+from.toString()+" "+to.toString());
    }

    public static Key findFloorKey(ConcurrentSkipListMap map, Key key){
        Key from = (Key) map.floorKey(key);
        if ( from == null )
            from = (Key) map.ceilingKey( key);
        return from;
    }

    public static Key findCeilKey(ConcurrentSkipListMap map, Key key) {
        Key to = (Key) map.ceilingKey( key);
        if ( to == null )
            to = (Key) map.floorKey(key);
        return to;
    }
}
