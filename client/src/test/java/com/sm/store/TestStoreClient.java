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

import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;

import java.util.List;

import static com.sm.transport.Utils.getOpts;



public class TestStoreClient {
   private static final Log logger = LogFactory.getLog(TestStoreClient.class);
   public static void main(String[] args) {
       String[] opts = new String[] {"-store","-url", "-times"};
       String[] defaults = new String[] {"store","localhost:7100", "10"};
       String[] paras = getOpts( args, opts, defaults);
       String store = paras[0];
       String url = paras[1];
       int times =  Integer.valueOf( paras[2]);
       RemotePersistence client = new NTRemoteClientImpl(url, null, store, false);
       for ( int i = 0; i < times ; i ++ ) {
            try {
                Key key = Key.createKey(i);
                client.put( key, "times-"+i);
                Value value = client.get( key);
                logger.info(value == null ? "null" : value.getData().toString()+ " "+value.getVersion());
                //value.setVersion( value.getVersion() -1);
                client.put( key, value);
                value = client.get( key);
                logger.info(value == null ? "null" : value.getData().toString()+ " "+value.getVersion());
                client.put( key, "times-"+i);
                value = client.get( key);
                logger.info(value == null ? "null" : value.getData().toString()+ " "+value.getVersion());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);

            }

       }
       List<Key> list =client.getKeyList();
       logger.info("key size "+ list.size());
       client.close();
       System.exit(0);

    }
}
