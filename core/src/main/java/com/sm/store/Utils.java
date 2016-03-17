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

import com.sm.storage.Serializer;
import com.sm.store.client.RemoteValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheValue;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadPoolExecutor;

import static com.sm.store.StoreParas.STORE_EXCEPTION;




public class Utils {
    private static final Log logger = LogFactory.getLog(Utils.class);

    /**
     *
     * @param request
     * @param message
     * @return StorePara
     */
    public static StoreParas createError(com.sm.message.Request request, String message) {
        StoreParas paras = StoreParas.toStoreParas( request.getPayload() );
        paras.setErrorCode( STORE_EXCEPTION );
        paras.getValue().setData( message.getBytes() );
        return paras;
    }


    /**
     * Check if the thread pool is overload or not
     * @param pools
     * @return true or false
     */
    public static boolean isOverLoaded(ThreadPoolExecutor pools) {
        int remain = pools.getQueue().remainingCapacity();
        int active =  pools.getActiveCount();
        if ( remain < active  ) {
            logger.info("Remaining capacity "+ remain +  " is less than active threads " + active);
            return true;
        }
        else return false;
    }

    /**
     *
     * @param value an CacheValue
     * @param serializer
     * @return RemoteValue
     */
    public static RemoteValue createRemoteValue(Value value, Serializer serializer) {
        return new RemoteValue( serializer.toObject( (byte[]) value.getData()) , value.getVersion(), value.getNode()) ;
    }


    /**
      *
      * @param value an RemoteValue
      * @return CeacheValue that increase version no + 1
      */
    public static CacheValue createCacheValue(Value value, Serializer serializer) {
     if ( value.getVersion() == 0 )
         return CacheValue.createValue( serializer.toBytes( value.getData()), value.getVersion(), value.getNode() ) ;
     else
        return CacheValue.createValue( serializer.toBytes( value.getData()), value.getVersion()+1, value.getNode() ) ;
    }



    public static byte[] putInt(int k) {
        return ByteBuffer.allocate(4).putInt(k).array() ;
    }

    public static byte[] putLong(long k){
        return ByteBuffer.allocate(8).putLong(k).array() ;
    }

    public static byte[] putShort(short k) {
        return ByteBuffer.allocate(2).putShort(k).array() ;
    }

    public static byte[] valueToBytes(Value value) {
        int len = 10 + ((byte[]) value.getData()).length;
        byte[] bytes = new byte[ len ];
        long v = value.getVersion() ;
        short node = value.getNode() ;
        byte[] data = (byte[]) value.getData() ;
        System.arraycopy(putLong(v), 0, bytes, 0, 8 );
        System.arraycopy(putShort(node), 0, bytes, 8, 2 );
        System.arraycopy(data, 0 , bytes, 10, data.length) ;
        return bytes;
    }

    public static Value bytesToValue(byte[] bytes){
        int len = bytes.length;
        ByteBuffer buf = ByteBuffer.wrap( bytes);
        long ver = buf.getLong();
        short node = buf.getShort();
        byte[] data = new byte[ len - 10];
        buf.get( data);
        return CacheValue.createValue( data, ver, node);
    }

    public static void addOneLong(Value value) {
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) value.getData());
        long k = buf.getLong() + 1;
        byte[] data = putLong(k);
        value.setData( data);
        value.setVersion( value.getVersion() +1 );
    }

    public static void addBlockLong(Value value, Value block) {
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) value.getData());
        //block value is integer
        long k = buf.getLong() + ByteBuffer.wrap( (byte[]) block.getData()).getInt();
        byte[] data = putLong(k);
        value.setData( data);
        value.setVersion( value.getVersion() +1 );
    }

    public static void addBlockInt(Value value, Value block) {
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) value.getData());
        int k = buf.getInt() + ByteBuffer.wrap( (byte[]) block.getData()).getInt();
        byte[] data = putInt(k);
        value.setData( data);
        value.setVersion( value.getVersion() +1 );
    }

    public static void addOne(Value value) {
        ByteBuffer buf = ByteBuffer.wrap( (byte[]) value.getData());
        int k = buf.getInt() + 1;
        byte[] data = putInt(k);
        value.setData( data);
        value.setVersion( value.getVersion() +1 );
    }

    public static String getPath(String configPath) {
        int pos = configPath.lastIndexOf("/");
        if (pos == 0 ) {
            return ".";
        }
        else return configPath;
    }
}
