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

package com.sm.store;

import com.sm.store.client.netty.NTRemoteClientImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static com.sm.transport.Utils.getOpts;

public class TestStoreCases {
    protected static Log logger = LogFactory.getLog(TestStoreCases.class);

    //private String url;
    protected long times;
    protected int size;
    public final int base = 300;
    protected Random random = new Random();
    protected RemotePersistence client;
    protected long keyRange;
    protected long lbase;
    protected long pace;
    protected int threads;
    protected String[] tests;
    protected boolean show ;

    public TestStoreCases(){
        super();
    }

    public TestStoreCases(long keyRange, int size, Random random, RemotePersistence client, long times, long lbase,
            int threads, String[] tests, long pace, boolean show) {
        this.keyRange = keyRange;
        this.times = times;
        this.size = size;
        this.random = random;
        this.client = client;
        this.lbase = lbase;
        this.threads = threads;
        this.tests = tests;
        this.pace = pace;
        this.show = show;
    }

    public void output(long begin, long end) {
        logger.info("Duration in ms "+ (end -begin) +" iteration "+times+" keyRange "+keyRange+" freq "+size+" bytes" );
    }

    public long genLong(long seed) {
        if ( seed < Integer.MAX_VALUE ) return random.nextInt( (int) seed);
        else {
            return (long) Math.random() * seed ;
        }
    }

    public void randomRW() {
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            // add lbase back to random number
            long key = genLong( keyRange -lbase) +lbase ;
            try {
            Value value = client.get(Key.createKey( key) )  ;
            if ( value != null)
                client.put(Key.createKey(key), value);
            else
                logger.warn("can not find key "+key);
            } catch (Exception ex) {
                logger.error( ex.getMessage());
            }
        }
        output(begin, System.currentTimeMillis());
    }


    public void populate() {
        int l = (size - base ) *2 ;

        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            int s = random.nextInt(l) + base ;
            client.put( Key.createKey(i), new byte[s]);
        }
        output(begin, System.currentTimeMillis());
    }

    public void populateFixSize() {
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            client.put( Key.createKey(i), new byte[size]);
        }
        output(begin, System.currentTimeMillis());
    }

    public void randomRead() {
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            long key = genLong( keyRange -lbase) +lbase ;
            Value value = client.get( Key.createKey(key))  ;
            if ( value == null ) logger.warn("can not found key "+key);
        }
        output(begin, System.currentTimeMillis());

    }

    public void randomWrite() {
        int l = (size - base ) *2 ;
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            long key = genLong( keyRange -lbase) +lbase ;
            int s = random.nextInt(l) + base ;
            try {
            client.put( Key.createKey(key), new byte[s]);
            } catch (Exception ex) {
                logger.error( ex.getMessage());
            }
        }
        output(begin, System.currentTimeMillis());

    }

    public void seqRW() {
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            Value value = client.get(Key.createKey(i));
            try {
            if (value != null)
                client.put(Key.createKey(i), value);
            else
                logger.warn("can not find key "+i);
            } catch( Exception ex) {
                logger.error( ex.getMessage());
            }
        }
        output(begin, System.currentTimeMillis());

    }

    public void repopulate() {
        for ( long i=0; i < times ; i++) {
            Key key = Key.createKey(i); client.put(key, new byte[0]);
            Value versionedValue = client.get(key);
            versionedValue.setData(new byte[1]);
            client.put(key, versionedValue);
        }
    }



    public void seqRead() {
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            Value value = client.get( Key.createKey(i))  ;
            if ( value == null ) logger.warn("can not found key "+i);
        }
        output(begin, System.currentTimeMillis());
    }

    public void seqWrite(){
        int l = (size - base ) *2 ;
        long begin = System.currentTimeMillis();
        for ( long i=0; i < times ; i++) {
            int s = random.nextInt(l) + base ;
            try{
            client.put( Key.createKey(i+lbase), new byte[s]);
            } catch (Exception ex) {
                logger.error( ex.getMessage());
            }
        }
        output(begin, System.currentTimeMillis());

    }

    public void runTest(String[] actions) {
        for(String action : actions) {
            if ( action.equals("randomRW")) randomRW();
            else if (action.equals("randomRead")) randomRead();
            else if (action.equals("randomWrite")) randomWrite();
            else if (action.equals("seqRW")) seqRW();
            else if (action.equals("seqRead")) seqRead();
            else if (action.equals("seqWrite")) seqWrite();
            else if (action.equals("populate")) populate();
            else if (action.equals("repopulate")) repopulate();
            else if (action.equals("populateFixSize")) populateFixSize();
            else {
                logger.warn("no action support "+action);
            }
        }
    }

    public static boolean checkThread( String[] testcases) {
        for (String test : testcases ) {
            if ( test.startsWith("populate") ) {
                logger.warn("Can not have more than one thread running populate");
                return false;
            }
        }
        return true;
    }

    public static String cmdLine(String[] opts, String[] defaults) {
        StringBuilder sb = new StringBuilder();
        for ( int i=0; i < opts.length ; i++ ) sb.append( opts[i]+" "+defaults[i]+ " ");
        return sb.toString();
    }

    static class RunThread implements Runnable {
        private TestStoreCases client;
        private String[] testcases;

        public RunThread(TestStoreCases client, String[] testcases) {
            this.client = client;
            this.testcases = testcases;
        }

        public void run() {
            client.runTest( testcases);

        }

    }

    public static InputStream getClientProperties(String filename){
        try {
            logger.info("loading client properties using "+filename);
            return new FileInputStream(new File(filename));
        } catch (IOException io) {
            logger.info("error, using default setting");
            return null;
        }
    }

    public static TestStoreCases createClient(String[] args){
        String[] opts = new String[] {"-url","-store","-keyRange","-freq", "-testcase" , "-thread", "-times",
            "-begin" ,"-pace" , "-show" };
        String[] defaults = new String[] {"tcp://localhost:7100","test","100", "1024","populate", "1", "0" ,
            "0" ,"0", "false"};
        String[] paras = getOpts(args, opts, defaults);
        String url = paras[0];
        String store = paras[1];

        long keyRange = Long.valueOf(paras[2]);
        int size = Integer.valueOf( paras[3]);
        RemotePersistence client = new NTRemoteClientImpl(url, null, store);
        String[] tests = (paras[4].split(","));
        int threads = Integer.valueOf(paras[5]);
        long times = Long.valueOf( paras[6]);
        if ( times == 0 ) times = keyRange;
        long begin = Long.valueOf( paras[7]);
        long pace = Long.valueOf(paras[8]);
        boolean show = Boolean.valueOf(paras[9]);
        logger.info("using "+ cmdLine( opts, defaults));
        return new TestStoreCases( keyRange , size, new Random(), client , times, begin, threads, tests, pace , show) ;
    }



    public static void main(String[] args) {
        TestStoreCases client = createClient( args);
        if ( client.threads <= 1)
        // create a client that executes operations on a single store
            client.runTest( client.tests );
        else {
            if ( checkThread( client.tests)  ) {
                for ( int i=0; i < client.threads ; i++) {
                    new Thread(new RunThread( client, client.tests), "thread-"+i).start();
                }
            }
        }

    }
    
}
