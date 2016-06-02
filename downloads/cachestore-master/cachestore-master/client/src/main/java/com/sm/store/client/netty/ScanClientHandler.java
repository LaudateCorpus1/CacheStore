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

package com.sm.store.client.netty;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.transport.AsynReq;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ScanClientHandler extends StoreClientHandler {
    private static final Log logger = LogFactory.getLog(ScanClientHandler.class);
    protected Serializer serializer;

    public ScanClientHandler(long timeout, Serializer serializer) {
        this.timeout = timeout;
        this.serializer = serializer;
        if ( this.serializer == null) this.serializer = new HessianSerializer();
        this.map = new ConcurrentHashMap<Long, AsynReq>(119);
    }

    public ScanClientHandler() {
        this(6000, new HessianSerializer());
    }

    public Serializer getSerializer() {
        return serializer;
    }


}
