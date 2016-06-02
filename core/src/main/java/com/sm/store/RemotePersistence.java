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
 */package com.sm.store;

import com.sm.message.Invoker;
import voldemort.store.cachestore.Key;

import java.util.List;

public interface RemotePersistence extends StorePersistence {
    //int sequence key, return next sequence no
    public long getSeqNo(Key key);
    //return next sequence no
    public int getSeqNoInt(Key key);
    //return next block starting no
    public long getSeqNoBlock(Key key, int block);
    public int getSeqNoBlockInt(Key key, int block);
    // invoke remote service call
    public Object invoke(Invoker invoker);
    public List<Key> getKeyList();
}
