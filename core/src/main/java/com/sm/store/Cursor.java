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

package com.sm.store;

import voldemort.store.cachestore.Key;

import java.io.Serializable;
import java.util.Iterator;

public class Cursor implements Serializable{
    // store name
    private String store;
    // key for cursor Map
    private long cursorId;
    // server key iterator
    private transient Iterator iterator;
    // end of iterator flag
    private boolean end;
    //has been started
    private boolean start;
    //time stamp
    private long beginTime;
    //last access time stamp for back ground thred clean up
    private long lastTime;
    //size of transfer batch
    private short batchSize;
    //total record has been retrieve
    private long currentRecord;
    private Key from;
    private Key to;

    public Cursor(String store, long cursorId, short batchSize, Key from, Key to) {
        this.store = store;
        this.cursorId = cursorId;
        this.batchSize = batchSize;
        this.from = from;
        this.to = to;
        this.start = true;
        this.beginTime = System.currentTimeMillis();
        this.lastTime = beginTime;
    }

    public String getStore() {
        return store;
    }

    public long getCursorId() {
        return cursorId;
    }

    public Iterator getIterator() {
        return iterator;
    }


    public boolean isEnd() {
        return end;
    }

    public void setCurrentRecord(long currentRecord) {
        this.lastTime = System.currentTimeMillis();
        this.currentRecord = currentRecord;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public short getBatchSize() {
        return batchSize;
    }

    public long getCurrentRecord() {
        return currentRecord;
    }

    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public Key getFrom() {
        return from;
    }


    public Key getTo() {
        return to;
    }

}
