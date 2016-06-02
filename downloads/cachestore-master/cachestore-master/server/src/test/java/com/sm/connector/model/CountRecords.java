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

package com.sm.connector.model;

import com.sm.connector.MapReduce;
import com.sm.connector.server.ExecMapReduce;
import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.StoreMap;
import com.sm.store.server.RemoteStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by mhsieh on 12/20/15.
 */
public class CountRecords implements MapReduce<Value>, StoreMap {

    private static final Log logger = LogFactory.getLog(CountRecords.class);
    Map<String, RemoteStore> storeMaps ;
    Serializer serializer = new HessianSerializer();

    public Value aggregate(String store, int count) {
        ExecMapReduce execMapReduce = new ExecMapReduce(storeMaps, Value.class, this, serializer);
        Value ttl = (Value) execMapReduce.execute(store, count);
        return ttl;
    }


    @Override
    public void beforeMapStart(Value record, int taskNo, Map<String, Object> context) {

    }

    @Override
    public void map(Pair<Key, Object> pair, Value record, Map<String, Object> map) {
        record.count();
    }

    @Override
    public Object reduce(List<Value> list) {
        Value count = new Value() ;
        for ( Value each : list) {
            count.addCount( each.getCount());
        }
        logger.info("count "+count);
        return count;
    }

    @Override
    public void afterMapComplete(Value record, int taskNo, Map<String, Object> context) {

    }


    @Override
    public void setStoreMap(ConcurrentMap<String, RemoteStore> storeMaps) {
        this.storeMaps = storeMaps;
    }
}
