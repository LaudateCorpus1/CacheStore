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

package com.sm.replica;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import voldemort.store.cachestore.Key;


public class TestRecovery {

    static class Unison implements UnisonCallBack {

        @Override
        public void processParaList(ParaList prarList) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void recovery(Key key, Object value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static void main(String[] args){
        Unison us = new Unison();
        Serializer sr = new HessianSerializer();
//        LocalStoreImpl local = new LocalStoreImpl("/Users/mhsieh/java/test/data/recoveryStore.ser", sr,0);
//        local.put(Key.createKey("cmpTest"), "test");
//        UnisonServerHandler ush = new UnisonServerHandler(us, local);
//        System.out.println("test");
    }
}
