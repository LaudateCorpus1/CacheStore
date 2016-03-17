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

import com.sm.message.Header;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.store.OpType;
import com.sm.store.StoreParas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.voldeimpl.BlockValue;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;


//import com.sun.tools.internal.ws.processor.model.Response;
//import org.omg.CORBA.Request;


public class TestReplicaClient {
    private static final Log logger = LogFactory.getLog(TestReplicaClient.class);


    public static ParaList createParaList(int batch, int index) {

        List<StoreParas> list = new ArrayList<StoreParas>(batch);
        for( int i = batch * index; i < batch *(index+1); i++) {
            Key key = Key.createKey(i);
            if ( i % 2 == 0) {
                Value<byte[]> value = new BlockValue<byte[]>(("times-"+i).getBytes(), 0, (short) 0 );
                list.add( new StoreParas(OpType.Put, key, value));
            }
            else
                list.add( new StoreParas(OpType.Remove, key));
        }
        return new ParaList( list);
    }

    public static int errorNo(ParaList paraList) {
        int no = 0;
        for ( StoreParas each : paraList.getLists()) {
            if ( each.getErrorCode() == 2)  no++;
        }
        return no;
    }


    public static void main(String[] args) {
       String[] opts = new String[] {"-store","-url", "-times"};
       String[] defaults = new String[] {"campaigns","localhost:7320", "3"};
       String[] paras = getOpts( args, opts, defaults);
       String store = paras[0];
       String url = paras[1];
       int times =  Integer.valueOf( paras[2]);
       MockClient client = new MockClient(url, store);

       for ( int i = 0; i < times ; i ++ ) {
            try {
                Header header = new Header(store, i, (byte) 0, 1 );
                ParaList paraList = createParaList(1, i);
                Request request = new Request(header, paraList.toBytes());
                Response resp = client.sendRequest( request);
                if ( resp.getPayload() instanceof  ParaList) {
                    ParaList pList = (ParaList) resp.getPayload();
                    logger.info(resp.toString() + " pList " + pList.getLists().size() + " error " + errorNo(pList));
                }
                else
                    logger.info("class "+resp.getPayload().getClass().getName()+ " "+resp.getPayload().toString());
            } catch (Exception ex){
                logger.error(ex.getMessage(), ex);
            }

        }
        //client.close();

    }    
}
