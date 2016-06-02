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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.store.client.RemoteValue;
import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;

public class TestRemotePopulate {
    private static final Log logger = LogFactory.getLog(TestRemotePopulate.class);

    public static void main(String[] args) {
        String[] opts = new String[] {"-store","-url", "-times"};
        String[] defaults = new String[] {"campaignsperday","las1-ssusd001.sm-us.sm.local:7240", "10"};
        String[] paras = getOpts(args, opts, defaults);
        String store = paras[0];
        String url = paras[1];
        int times =  Integer.valueOf( paras[2]);
        HessianSerializer serializer = new HessianSerializer();

        RemotePersistence client = new NTRemoteClientImpl(url, null, store);
        for ( int i = 0; i < times ; i ++ ) {
             try {
                 //Key key = Key.createKey("cmp"+i);
                 //Campaign campaign = new Campaign(1, 1, i, false, false, "test"+i, null, null, null, null, null);
                 //logger.info(campaign);
                 //byte[] campaignBytes = serializer.toBytes(campaign);
                 // Value val = new RemoteValue(campaignBytes, 0, (short) 0);

                 Key key = Key.createKey("usr"+i);
                 List<String> campaignIds = new ArrayList<String>();
                 campaignIds.add("cmp"+i);
                 ////campaignIds.add("cmp"+i+i);
                 //byte[] campaignIdsBytes = serializer.toBytes(campaignIds);
                 Value val = new RemoteValue(campaignIds, 0, (short) 0);
//
//	                Key key = Key.createKey("cmp"+i+"."+20130116);
//	                Delivery delivery = new Delivery(i, i+1, i*2, (i*2)+1, i*3, (i*3)+1, i*4, (i*4)+1, i*5, (i*5)+1);
//	                byte[] deliveryBytes = serializer.toBytes(delivery);
//		            Value val = new RemoteValue(deliveryBytes, 0, (short) 0);
//	                client.put( key, val);
//
//	                key = Key.createKey("cmp"+i+"."+20130117);
//	                delivery = new Delivery(i, i+2, i*2, (i*2)+2, i*3, (i*3)+2, i*4, (i*4)+2, i*5, (i*5)+2);
//	                deliveryBytes = serializer.toBytes(delivery);
//		            val = new RemoteValue(deliveryBytes, 0, (short) 0);

                 client.put( key, val);

                 Value value = client.get( key);
                 logger.info("campaign value data: "+(List<String>) value.getData());
                 logger.info(value == null ? "null" : value.getData().toString());

             } catch (Exception ex) {
                 logger.error(ex.getMessage(), ex);
             }

         }
         client.close();

     }


}
