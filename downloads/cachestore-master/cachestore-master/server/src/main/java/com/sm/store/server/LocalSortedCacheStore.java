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

package com.sm.store.server;

import com.sm.localstore.impl.LocalStoreImpl;
import com.sm.storage.Serializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.BlockSize;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.impl.SortedCacheStore;
import static voldemort.store.cachestore.voldeimpl.VoldeUtil.*;

public class LocalSortedCacheStore extends LocalStoreImpl {
    protected static Log logger = LogFactory.getLog(LocalSortedCacheStore.class);


    public LocalSortedCacheStore(String filename, Serializer serializer, String path, boolean delay, BlockSize blockSize, int mode) {
        this.path = path;
        this.filename = filename;
        this.serializer = serializer;
        this.delay = delay;
        this.blockSize = blockSize;
        this.mode =mode;
        init();
    }

    public LocalSortedCacheStore(String filename, Serializer serializer, boolean delay, int mode) {
        String[] names = checkPath(filename);
        this.filename = names[1];
        this.serializer = serializer;
        this.path = names[0];
        this.delay = delay;
        this.mode = mode;
        init();
    }

    public LocalSortedCacheStore(String filename, Serializer serializer, int mode) {
        this(filename, serializer, false, mode);
    }

    protected void init() {
        if ( serializer == null ) throw new StoreException("Serializer can not be null");
        this.store = new SortedCacheStore(path, blockSize, 0, filename, delay, mode );
        for ( int i = 0; i < 6 ; i++) statList.add(0L);
        this.recentCacheStats = new RecentCacheStats(store);
    }

    public SortedCacheStore getSortedStore() {
        return (SortedCacheStore) store ;
    }
}
