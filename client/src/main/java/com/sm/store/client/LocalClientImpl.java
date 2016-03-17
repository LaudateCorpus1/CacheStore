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

import com.sm.localstore.impl.LocalStoreImpl;
import com.sm.storage.Serializer;
import com.sm.store.StorePersistence;
import voldemort.store.cachestore.BlockSize;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import static com.sm.store.Utils.*;


public class LocalClientImpl extends LocalStoreImpl implements StorePersistence {


    public LocalClientImpl(String filename, Serializer serializer, int mode) {
        super(filename, serializer, mode);
    }

    public LocalClientImpl(String filename, Serializer serializer, boolean delay, int mode) {
        super(filename, serializer, delay, mode);
    }

    public LocalClientImpl(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode) {
        super(filename, serializer, path, delay, blockSize, mode);
    }

    @Override
    public void put(Key key, Object data) {
        super.put( key, data);
    }

    /**
     *
     * @param key
     * @return an RemoteValue, getData() return an object which had been deserialized
     */
    @Override
    public Value get(Key key) {
        Value value = store.get(key);
        if ( value == null ) return null;
        else
            return createRemoteValue( value, serializer);
    }

    @Override
    public void insert(Key key, Value value) {
        if ( store.get( key) != null)
            throw new StoreException("insert failure due to existing key "+key.toString());
        else
            super.put(key, value);
    }

//    public RemoteValue createRemoteValue(Value value) {
//        return new RemoteValue( serializer.toObject( (byte[]) value.getData()) , value.getVersion(), value.getNode()) ;
//    }
//
//    /**
//     *
//     * @param key
//     * @param value an RemoteValue which need to convert to CacheValue
//     */
//    @Override
//    public void put(Key key, Value value) {
//        store.put( key, createCacheValue(value, serializer));
//     }

//     private CacheValue createCacheValue(Value value, Serializer serializer) {
//         return CacheValue.createValue( this.serializer.toBytes(value.getData()), value.getVersion()+1, value.getNode() ) ;
//     }


}
