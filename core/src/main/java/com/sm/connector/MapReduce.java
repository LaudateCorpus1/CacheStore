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

package com.sm.connector;

import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by mhsieh on 3/17/16.
 */
public interface MapReduce<T> {

    public void beforeMapStart(T record, int taskNo, Map<String, Object> context);
    public void map(Pair<Key, Object> pair, T record, Map<String, Object> map);
    public Object reduce(List<T> list);
    public void afterMapComplete(T record, int taskNo, Map<String, Object> context);
}
