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

import com.sm.store.CursorPara;
import com.sm.store.client.grizzly.GZScanClientImpl;
import com.sm.store.client.netty.ScanClientImpl;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.voldeimpl.KeyValue;

public class ScanIterator {
    String url;
    String store;
    Key from;
    Key to;
    short size ;
    ScanClientImpl scanClient;
    CursorPara cursorPara;
    short index = 0 ;

    public ScanIterator(String url, String store, Key from, Key to) {
        this(url, store, from, to, (short) 100);
    }

    public ScanIterator(String url, String store, Key from, Key to, short size) {
        this.url = url;
        this.store = store;
        this.from = from;
        this.to = to;
        this.size = size;
        init();
    }

    private void init() {
        scanClient = new GZScanClientImpl(url, null, store);
        cursorPara = scanClient.openScanCursor( size, from, to);
    }


    private boolean lastRecord() {
        if (cursorPara.isEnd() && cursorPara.getKeyValueList().size() == index)
            return true;
        else
            return false;
    }

    public boolean hasNext() {
        if (lastRecord())
            return false;
        else {
            if (cursorPara.getKeyValueList().size() == index) {
                //get next cursor
                cursorPara = scanClient.nextCursor( cursorPara);
                //reset index
                index = 0;
                if ( lastRecord() )
                    return false;
                else
                    return true;
            }
            else
                return true;
        }
    }

    public KeyValue next() {
        if ( cursorPara.getKeyValueList().size() == index )
            throw new RuntimeException("must call hasNext() before Next()");
        return cursorPara.getKeyValueList().get(index ++);
    }

    public void close() {
        scanClient.closeCursor(cursorPara);
        scanClient.close();
    }
}

