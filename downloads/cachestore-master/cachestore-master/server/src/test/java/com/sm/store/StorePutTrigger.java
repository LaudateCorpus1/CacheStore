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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheStore;

public class StorePutTrigger implements PutTrigger {
    private static final Log logger = LogFactory.getLog(StorePutTrigger.class);

    HessianSerializer serializer = new HessianSerializer();



    @Override
    public boolean beforePut(Key key, Value value, CacheStore store) {
        try {
            logger.info("before putTrigger v "+value.getVersion()+" k "+key.toString()+" s "+ ((byte[]) value.getData()).length);
            serializer.toObject((byte[]) value.getData());
        } catch (Exception ex) {
            logger.warn( "before fail to serialize "+ex.getMessage());
            throw new StoreException("before fail to serialize "+key.toString());
        }
        return false;
    }

    @Override
    public Value afterPut(Key key, Value value, CacheStore store) {
//        try {
//            logger.info("after putTrigger v "+value.getVersion()+" k "+key.toString()+" s "+ ((byte[]) value.getData()).length);
//            Value v = store.get(key);
//            if ( v != null ) serializer.toObject((byte[]) v.getData());
//        } catch (Exception ex) {
//            logger.warn( "after fail to serialize "+ex.getMessage());
//        }
        return value;
    }
}
