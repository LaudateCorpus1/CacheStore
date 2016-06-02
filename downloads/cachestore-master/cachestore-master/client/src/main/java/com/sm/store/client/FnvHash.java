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

package com.sm.store.client;

import com.sm.store.Hash;
import voldemort.store.cachestore.BlockUtil;
import voldemort.store.cachestore.Key;
import voldemort.utils.ByteArray;

public class FnvHash implements Hash {
    public static final long FNV_BASIS = 0x811c9dc5;
    public static final long FNV_PRIME = (1 << 24) + 0x193;

    /**
     *  Use FnvHashFunction to perform hash funtion instead
     *  Key.hashCode
      * @param key
     * @return int
     */
    @Override
    public int hash(Key key) {
        if ( key.getType() == BlockUtil.KeyType.INT)
            return hash( (Integer) key.getKey());
        else if ( key.getType() == BlockUtil.KeyType.LONG)
            return hash( (Long) key.getKey());
        else if (key.getType() == BlockUtil.KeyType.BYTEARY)
            return hash( (byte[]) key.getKey());
        else if (key.getType() == BlockUtil.KeyType.BARRAY)
            return hash ( ((ByteArray) key.getKey()).get());
        else
            return hash( key.hashCode() );


    }

    public int hash(byte[] key) {
        long hash = FNV_BASIS;
        for(int i = 0; i < key.length; i++) {
            hash ^= 0xFF & key[i];
            hash *= FNV_PRIME;
        }

        return (int) hash;
    }

    /**
     * Static function to apply fnv hash function
     * @param key - an integer
     * @return int
     */
    public int hash(int key) {
        long hash = FNV_BASIS;
        for(int i = 0; i < 4; i++) {
            hash ^= 0xFF & key << (8*i);
            hash *= FNV_PRIME;
        }
        return (int) hash;
    }

    /**
     * Static function to apply fnv hash function
     * @param key
     * @return int
     */
    public static int hash(long key) {
        long hash = FNV_BASIS;
        for(int i = 0; i < 8; i++) {
            hash ^= 0xFF & key << (8*i);
            hash *= FNV_PRIME;
        }
        return (int) hash;
    }
}
