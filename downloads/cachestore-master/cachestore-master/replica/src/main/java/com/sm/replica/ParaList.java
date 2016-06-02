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

package com.sm.replica;

import com.sm.store.StoreParas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.sm.store.StoreParas.putInt;
import static com.sm.store.StoreParas.toStoreParas;

public class ParaList implements Serializable {
 private static final Log logger = LogFactory.getLog(ParaList.class);

    private int size;
    private List<StoreParas> lists;

    public ParaList(List<StoreParas> lists) {
        if ( lists == null ) throw new RuntimeException("lists can not be null");
        this.lists = lists;
        this.size = lists.size();

    }

    public int getSize() {
        return size;
    }


    public List<StoreParas> getLists() {
        return lists;
    }

    public void setLists(List<StoreParas> lists) {
        this.lists = lists;
        this.size = lists.size();
    }

    public  byte[] toBytes(){
        // int len + byte[]
        int total = 4 * (size+1);
        byte[][] bytes = new byte[size][];
        for ( int i= 0 ; i < size ; i++){
            bytes[i] = lists.get(i).toBytes();
            total += bytes[i].length;
        }
        byte[] toReturn = new byte[total];
        int pos = 0;
        System.arraycopy(putInt( size), 0, toReturn, 0, 4);
        pos += 4;
        for ( int i=0; i < size ; i++) {
            System.arraycopy(putInt(bytes[i].length), 0, toReturn, pos, 4);
            pos += 4;
            System.arraycopy(bytes[i], 0, toReturn, pos, bytes[i].length);
            pos += bytes[i].length;
        }
        return toReturn;
    }

    public static ParaList toParaList(byte[] bytes){
        ByteBuffer buf = ByteBuffer.wrap( bytes);
        int size = buf.getInt();
        ArrayList<StoreParas> list = new ArrayList<StoreParas>(size);
        for ( int i =0; i < size ; i ++){
            // length of StoreParas
            int s = buf.getInt();
            byte[] ds = new byte[s];
            buf.get( ds);
            list.add( toStoreParas( ds));

        }
        return new ParaList(list);

    }
}
