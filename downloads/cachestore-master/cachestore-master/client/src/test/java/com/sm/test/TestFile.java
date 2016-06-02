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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.voldeimpl.KeyValue;
import voldemort.store.cachestore.voldeimpl.StoreIterator;
import voldemort.utils.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.sm.transport.Utils.getOpts;

public class TestFile {
    private static final Log logger = LogFactory.getLog(TestFile.class);
    ConcurrentMap<Integer, Short> partitionMap = new ConcurrentHashMap<Integer, Short>();
    int size ;

    public TestFile(short mapSize, int range, int size) {
        init( mapSize, range);
        this.size = size;

    }

    void init(short mapSize, int range) {

        int k = 0;
        for ( short s = 0 ; s <  mapSize; s++) {
            for (int j =0 ; j < range; j++) {
                partitionMap.put( (k+j), s);
            }
            k +=  range;
        }

    }

    public static void main(String[] args) throws Exception {
        String[] opts = new String[] {"-filename", "-mapSize", "-range", "-listSize", "-times", "-url", "-store",};
        String[] defaults = new String[] {"/Users/mhsieh/test/data/userCrm", "5", "4", "2" , "100","las1-crmp001:6172", "userCrm" };
        String[] paras = getOpts( args, opts, defaults);

        String filename = paras[0];
        short mapSize = Short.valueOf(paras[1]);
        int range = Integer.valueOf(paras[2]);
        int listSize = Integer.valueOf(paras[3]);
        int times = Integer.valueOf(paras[4]);
        String url = paras[5];
        String store = paras[6];
        TestFile testFile = new TestFile( mapSize, range, listSize);
        ClusterClientFactory ccf =ClusterClientFactory.connect(url, store);
        ClusterClient client = ccf.getDefaultStore();

        StoreIterator storeIterator = new StoreIterator(filename);
        int i = 0;
        while ( storeIterator.hasNext()) {
//            Pair<Key, byte[]> pair = storeIterator.next();
//            short c1 = testFile.getPartitionIndex( pair.getFirst()).getFirst();
//            short c2 = testFile.finePartition( pair.getFirst());
//            if ( c1 != c2) {
//                logger.info( "mismatch i "+i + " c1 "+c1 + " c2 "+c2+ " "+ toStr( (byte[]) pair.getFirst().getKey() )+ " "+pair.getFirst().hashCode() );
//            }
            List<Key> list = getKeyList( storeIterator, 1000);
            List<KeyValue> keyValues = client.multiGets(list);
            int ma = match( keyValues) ;
            if ( ma != list.size()  ) {
                logger.info("expect "+list.size() +" get "+ma);
            }
            i++;
            if ( i > times) break;

        }
        logger.info("close file "+filename);
        storeIterator.close();
    }

    public static String toStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        for ( int i = 0 ; i < bytes.length ; i++) {
            sb.append( bytes[i]);
        }
        return sb.toString();
    }

    public static int match(List<KeyValue> list) {
        int i = 0;
        for ( KeyValue each : list) {
            if ( each.getValue() != null ) i++;
        }
        return i;
    }

    public static List<Key> getKeyList(StoreIterator storeIterator, int size) {
        List<Key> list = new ArrayList<Key>();
        while ( storeIterator.hasNext()) {
            try {
                Pair<Key, byte[]> pair = storeIterator.next();
                list.add(pair.getFirst());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ( list.size() >= size) break;
        }
        return list;
    }

    public short finePartition(Key key) {
        int code = key.hashCode() % 20 ;
        if ( code < 4) return 0;
        else if ( code < 8 ) return 1;
        else if ( code < 12 ) return 2;
        else if ( code < 16 ) return 3;
        else return 4;
    }

    public Pair<Short, Integer> getPartitionIndex(Key key) {
        int code = key.hashCode() ;
        int part = code % partitionMap.size();
        short cluster = partitionMap.get(part);
        int index =  code % size;
        return new Pair<Short, Integer> (cluster, index);
    }

}
