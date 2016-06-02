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

import com.sm.store.client.ClusterClient;
import com.sm.store.client.ClusterClientFactory;
import org.apache.commons.beanutils.converters.ByteArrayConverter;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.utils.ByteArray;

import java.io.Serializable;

public class TestClient {

    public static final String HOST_CONNECT_URL = "localhost:6172";
    //        "las1-cookie001:6272,las1-cookie002:6272,las1-cookie003:6272,las1-cookie004:6272,las1-cookie005:6272,las1-cookie006:6272,las1-cookie007:6272,las1-cookie008:6272";

    public static final String STORENAME = "store1";
    //        "partnerId";

    private static ClusterClientFactory ccf;

    private static ClusterClient client;

    public static void main(String[] args) {
        ccf =ClusterClientFactory.connect(HOST_CONNECT_URL, STORENAME);
        client = ccf.getDefaultStore();
        while ( true) {
            try {
                long time = System.currentTimeMillis()/1000;
                ByteArrayConverter converter = new ByteArrayConverter();
                Key<ByteArray> key = Key.createKey(new ByteArray("1.abcdef".getBytes()));
                Bar bar = new Bar();
                client.put(key, new ByteArray("FooBar".getBytes()));
                Value<ByteArray> value = client.get(key);
                System.out.println("" + new String(value.getData().get()) );
                try {
                    Thread.sleep( 3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage() );
            }
            if ( false ) break;
        }
        client.close();
        ccf.close();
    }

    public static class Bar implements Serializable {

        private static final long serialVersionUID = 1L;
        protected final Integer a = 10;
        protected final Long b = 99L;
        protected final String c = "FooBar";

        public Bar() {
        }
        @Override
        public String toString() {
            return "Bar [a=" + a + ", b=" + b + ", c=" + c + "]";
        }
    }

    public static class Foo implements Serializable {
        private static final long serialVersionUID = 2L;

        protected final Integer pid = 1;
        protected final String id = "abcde";

    }

}
