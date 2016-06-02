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

package com.sm.connector.server;

import com.sm.connector.MapReduce;
import com.sm.query.Filter;
import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by mhsieh on 4/29/16.
 */
public interface JoinMapReduce<T> extends MapReduce<T> {
    // ability to run join with Multiple collections
    public boolean joinMap(Pair<Key, Object> pair, T record, Map<String, Object> context, List<String> collection, Filter filter);
}
