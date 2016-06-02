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
 */package com.sm.test;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Invoker;
import com.sm.store.RemotePersistence;
import com.sm.store.client.grizzly.GZRemoteClientImpl;
import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestFailure {
    private static final Log logger = LogFactory.getLog(TestFailure.class);

    public static void main(String[] args) throws Exception {

        final HessianSerializer serializer = new HessianSerializer();
        final String fakeUrl = "some.faulty:8888";

        final RemotePersistence rpcClient = new GZRemoteClientImpl(fakeUrl, serializer, "rpc");
        final RemotePersistence ntyClient = new NTRemoteClientImpl(fakeUrl, serializer, "rpc");

        final Invoker invoker = new Invoker("someClass", "someMethod", new Object[]{});
        final int invokeCount = 100;

        for (int i = 0; i < invokeCount; i++) {
            try {

                ntyClient.invoke(invoker);
            } catch (Exception e) {
                logger.info("NT exception");
                logger.info(Thread.activeCount());
            }
        }

        for (int i = 0; i < invokeCount; i ++) {
            try {
                rpcClient.invoke(invoker);
            } catch (Exception e) {
                logger.info("GZ exception");
                logger.info(Thread.activeCount());
            }
        }
    }
}
