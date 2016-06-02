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
 */package com.sm.store.client;

import voldemort.store.cachestore.Value;

import java.io.Serializable;

public class RemoteValue implements Value, Serializable {
    private Object data;
    private long version ;
    private short node;

    public RemoteValue(Object data, long version, short node) {
        this.data = data;
        this.version = version;
        this.node = node;
    }

    @Override
    public Object getData() {
        return data;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setData(Object data) {
        this.data = data;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getVersion() {
        return version;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short getNode() {
        return node;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setNode(short node) {
        this.node = node;
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
