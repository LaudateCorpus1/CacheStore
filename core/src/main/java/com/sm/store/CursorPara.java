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
import voldemort.store.cachestore.voldeimpl.KeyValue;
import java.io.Serializable;
import java.util.List;

public class CursorPara implements Serializable{
    public static enum CursorType { Scan ((byte) 1), KeySet ((byte) 2) , KeyValueSet ((byte) 3), SelectQuery ((byte) 4);
        final byte value;

        CursorType(byte value) {
            this.value = value;
        }

        public static CursorType getCursorType(byte value) {
            switch ( value ) {
                case 1 : return Scan;
                case 2 : return KeySet;
                case 3 : return KeyValueSet;
                case 4 : return SelectQuery;
                default: return KeySet;
            }
        }

        public byte getValue() {
            return value;
        }
    }
    // store name
    private String store;
    // key for cursor Map
    private long cursorId = 0;
    // end of iterator flag from server
    private boolean end = false;
    //has been started
    private boolean start =false ;
    //client side for close cursor
    private boolean stop = false;
    //size of transfer batch
    private short batchSize;
    //total record has been retrieve
    private Key from;
    private Key to;
    private List<KeyValue> keyValueList;
    private byte cursorType;
    private String queryStr;


    public CursorPara(String store, short batchSize, Key from) {
        this(store, batchSize, from, from);
    }

    public CursorPara(String store, short batchSize, Key from, Key to) {
        this( store, batchSize, from , to, null);
    }

    public CursorPara(String store, short batchSize, Key from, Key to, String queryStr) {
        this.store = store;
        this.batchSize = batchSize;
        this.from = from;
        this.to = to;
        this.cursorType = CursorType.Scan.value;
        this.queryStr = queryStr;
    }

    public CursorPara(String store, short batchSize, byte cursorType){
        this( store, batchSize, cursorType, null);
    }

    public CursorPara(String store, short batchSize, byte cursorType, String queryStr){
//        if ( cursorType != CursorType.KeySet.value && cursorType != CursorType.KeyValueSet.value  )
//            throw new RuntimeException("run cursorType "+CursorType.getCursorType(cursorType) );
        this.store = store;
        this.batchSize = batchSize ;
        this.cursorType = cursorType;
        this.queryStr = queryStr;
    }


    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public String getStore() {
        return store;
    }

    public long getCursorId() {
        return cursorId;
    }

    public void setCursorId(long cursorId) {
        this.cursorId = cursorId;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public short getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(short batchSize) {
        this.batchSize = batchSize;
    }

    public Key getFrom() {
        return from;
    }

    public void setFrom(Key from) {
        this.from = from;
    }

    public Key getTo() {
        return to;
    }

    public void setTo(Key to) {
        this.to = to;
    }

    public List<KeyValue> getKeyValueList() {
        return keyValueList;
    }

    public void setKeyValueList(List<KeyValue> keyValueList) {
        this.keyValueList = keyValueList;
    }

    public CursorType getCursorType() {
        return CursorType.getCursorType(cursorType);
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }
}
