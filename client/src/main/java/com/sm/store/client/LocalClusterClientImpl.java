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

package com.sm.store.client;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.StorePersistence;
import com.sm.store.server.RemoteStore;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;

import java.util.Iterator;

import static com.sm.store.Utils.createCacheValue;
import static com.sm.store.Utils.createRemoteValue;

public class LocalClusterClientImpl implements StorePersistence {
    private RemoteStore store;
    private Serializer serializer;

    public LocalClusterClientImpl(RemoteStore store) {
        this(store, null);
    }

    public LocalClusterClientImpl(RemoteStore store, Serializer serializer) {
        this.store = store;
        if ( serializer == null ) this.serializer = new HessianSerializer();
        else
            this.serializer = serializer;
    }

    /**
     *
     * @param key
     * @param value is RemoteValue
     * convert RemoteValue to CacheValue
     */
    @Override
    public void put(Key key, Value value) {
        store.put( key,  createCacheValue(value, serializer));
    }

    @Override
    public void put(Key key, Object data) {
        // form cache store, return CacheValue
        Value value = new RemoteValue( data, 0L, (short) 0);
        put( key, value);
    }

    /**
     *
     * @param key
     * @return a RemoteValue
     */
    @Override
    public Value get(Key key) {
        Value value = store.get(key);
        if ( value == null ) return null ;
        else return createRemoteValue( value, serializer  ) ;

    }

    @Override
    public void insert(Key key, Value value) {
        if ( store.get(key ) != null)
            throw new StoreException("insert failure due to existing key "+key.toString());
        else
            put(key, value);
    }


    @Override
    public boolean remove(Key key) {
        return store.remove( key);
    }

    @Override
    public void close() {
        store.close();
    }

    @Override
    public Iterator getKeyIterator() {
        return store.getKeyIterator();
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public void pack(int rate) {
        store.pack( rate);
    }

    @Override
    public void backup(String path, int rate) {
        store.backup( path, rate);
    }
}
