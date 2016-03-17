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

import com.sm.store.client.RemoteClientImpl;
import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.sm.transport.Utils.getOpts;

public class TestPhp {
    private static Log logger = LogFactory.getLog(TestPhp.class);


    public static byte[] readFile(String filename) throws IOException {
        File file = new File( filename);
        long size = file.length();
        byte[] data = new byte[ (int) size];
        FileInputStream fip = new FileInputStream( file);
        int len = fip.read( data);
        if ( len != data.length ) throw new RuntimeException("size expect "+data.length+" get "+len);
        else return data;
    }

    public static void main(String[] args) throws Exception {

        String[] opts = new String[] {"-store","-url", "-times"};
        String[] defaults = new String[] {"keystore","localhost:7100", "10"};
        String[] paras = getOpts( args, opts, defaults);
        String store = paras[0];
        String url = paras[1];
        int times =  Integer.valueOf( paras[2]);
        RemoteClientImpl client = new NTRemoteClientImpl(url, null, store, false);
        //Value<byte[]> value = CacheValue.createValue(Utils.putInt(100000), 0, (short) 0);
        //client.put4Value(Key.createKey("key"), value);
        int startCrmId = client.getSeqNoInt(Key.createKey("key"));
        logger.info("key "+startCrmId);
//        String filename = args[0];
//        byte[] data = readFile( filename);
//        HessianReader hr = new HessianReader(data);
//        hr.readObject();
//        System.out.println(new String( hr.getBytes()));
//        HessianPhp hessianPhp = new HessianPhp(getNameMap());
//        byte[] d1 = hessianPhp.php2Hessian( data);
//        System.out.println("size "+d1.length+ " "+ new String(d1));
    }
}
