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

import voldemort.store.cachestore.voldeimpl.KeyValue;

import java.io.Serializable;
import java.util.List;

public class KeyValueParas implements Serializable {
    private OpType opType;
    private List<KeyValue> list;
    private String queryStr;

    public KeyValueParas(){
        super();
    }

    public KeyValueParas(OpType opType, List<KeyValue> list) {
        this.opType = opType;
        this.list = list;
    }

    public KeyValueParas(OpType opType, List<KeyValue> list, String queryStr) {
        this.opType = opType;
        this.list = list;
        this.queryStr = queryStr;
    }

    public OpType getOpType() {
        return opType;
    }

    public List<KeyValue> getList() {
        return list;
    }

    public int getSize() {
        if ( list == null ) return 0;
        else return list.size();
    }

    public String getQueryStr() {
        return queryStr;
    }

}
