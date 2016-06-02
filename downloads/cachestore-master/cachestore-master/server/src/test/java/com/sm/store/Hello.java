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

import com.sm.store.server.RemoteStore;
import voldemort.store.cachestore.Key;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class Hello implements StoreMap {
    private ConcurrentMap<String, RemoteStore> storeMaps ;

    public void setStoreMap(ConcurrentMap<String, RemoteStore> storeMaps ) {
        this.storeMaps = storeMaps;
    }

    public String greeting(String greeting) {
        return "Hello "+greeting;
    }

    public String echo(List<Key> key) {
        return "Hello key "+key.size();
    }



}
