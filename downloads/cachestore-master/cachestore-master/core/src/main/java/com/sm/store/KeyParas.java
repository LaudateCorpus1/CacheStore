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

import voldemort.store.cachestore.Key;

import java.io.Serializable;
import java.util.List;


public class KeyParas implements Serializable {
    private OpType opType ;
    private List<Key> list;
    private String queryStr;

    public KeyParas() {
        super();
    }

    public KeyParas(OpType opType, List<Key> list) {
        this.opType = opType;
        this.list = list;
    }

    public KeyParas(List<Key> list) {
        this( OpType.Scan, list);
    }

    public KeyParas(OpType opType, List<Key> list, String queryStr) {
        this.opType = opType;
        this.list = list;
        this.queryStr = queryStr;
    }

    public String getQueryStr() {
        return queryStr;
    }


    public OpType getOpType() {
        return opType;
    }

    public List<Key> getList() {
        return list;
    }

}
