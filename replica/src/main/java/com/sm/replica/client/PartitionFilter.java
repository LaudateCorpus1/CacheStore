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

package com.sm.replica.client;

import com.sm.replica.Filter;
import com.sm.store.StoreParas;
import voldemort.store.cachestore.Key;
import voldemort.utils.ByteArray;
import voldemort.utils.HashFunction;

import java.util.ArrayList;
import java.util.List;

public class PartitionFilter implements Filter {
    private int[] partitions;
    private HashFunction hash;
    private int totalPartition;

    public PartitionFilter(int[] partitions, HashFunction hash, int totalPartition) {
        if ( partitions == null || hash == null ) throw new RuntimeException("partition or hash can not be null");
        this.partitions = partitions;
        this.hash = hash;
        this.totalPartition = totalPartition;
    }

    @Override
    public List<StoreParas> applyFilter(List<StoreParas> list) {
        List<StoreParas> toReturn = new ArrayList<StoreParas>( list.size());
        for ( StoreParas each : list) {
            if ( isInPartition(each)) {
                toReturn.add(each);
            }
        }
        return toReturn;
    }

    private boolean isInPartition(StoreParas paras) {
        Key<ByteArray> key = paras.getKey();
        int part = hash.hash( key.getKey().get() ) % totalPartition;
        for ( int i=0; i < partitions.length ; i++) {
            if ( partitions[i] == part )
                return true;
        }
        return false;
    }
}
