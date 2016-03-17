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

package com.sm.test;

import com.sm.store.GetTrigger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheStore;

public class StoreGetTrigger implements GetTrigger {
    private static final Log logger = LogFactory.getLog(StoreGetTrigger.class);

    @Override
    public boolean beforeGet(Key key, CacheStore store) {
        logger.info("before get key "+key.toString() +" "+store.toString());
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Value afterGet(Key key, Value value, CacheStore store) {
        logger.info("after get key "+key.toString()+" "+store.toString() );
        return value;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
