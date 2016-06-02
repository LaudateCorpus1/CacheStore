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

package com.sm.connector.client;

import com.sm.connector.Aggregate;
import com.sm.message.Invoker;
import com.sm.query.utils.QueryUtils;
import com.sm.store.client.RemoteClientImpl;
import com.sm.utils.ThreadPoolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by mhsieh on 2/25/16.
 */
public class PartitionClient<T> {
    private static final Log logger = LogFactory.getLog(PartitionClient.class);
    Class<Aggregate<T>> tClass;
    //long timeout = 60000L;
    Invoker invoker;
    String filename;

    public PartitionClient(Invoker invoker, String filename, Class<Aggregate<T>> tClass) {
        this.invoker = invoker;
        this.filename = filename;
        this.tClass = tClass;

    }

    public T execute(List<RemoteClientImpl> list, int batchSize) {
        logger.info("execute "+ filename +" threads "+ list.size()+" invoker "+ invoker.toString());
        int noOfThread = list.size();
        //List<RemoteClientImpl> list = createClients( urls);
        //CountDownLatch countDownLatch = new CountDownLatch(noOfThread);
        ExecutorService executor = Executors.newFixedThreadPool(noOfThread, new ThreadPoolFactory("Partition"));
        Aggregate<T> aggregate = null;
        try {
            aggregate = (Aggregate<T>) QueryUtils.createInstance(tClass);
        } catch ( Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
        List<Future<Aggregate<T>>> results = new ArrayList<Future<Aggregate<T>>>(noOfThread);
        for ( int i = 0; i < noOfThread ; i++ ) {
            try {
                Aggregate<T> ft = (Aggregate<T>) QueryUtils.createInstance(tClass);
                RunThread runThread =  new RunThread(i, list.get(i), batchSize, noOfThread, ft);
                Future<Aggregate<T>> t = executor.submit( runThread );
                results.add(t);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        for ( Future<Aggregate<T>> each : results) {
            try {
                aggregate.aggregate( each.get().get());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        executor.shutdown();
        return aggregate.get();
    }

    public class RunThread implements Callable<Aggregate<T>> {
        //CountDownLatch countDownLatch;
        RemoteClientImpl client;
        MD5Reader local;
        long begin = 0 ;
        long end;
        Aggregate<T> t;
        int taskNo;
        int noOfThread;
        int batchSize ;

        public RunThread(int taskNo, RemoteClientImpl client, int batchSize, int noOfThread, Aggregate<T> t) {
            this.taskNo = taskNo;
            this.client = client;
            this.batchSize = batchSize;
            this.noOfThread = noOfThread;
            this.t = t;
            init();
        }

        private void init() {
            long b = System.currentTimeMillis();
            local = new MD5Reader( filename);
            long totalLen = local.getLength();
            long blockSize = ( totalLen % noOfThread == 0 ? totalLen /noOfThread : (totalLen /noOfThread)+1);
            begin = blockSize * taskNo ;
            if ( taskNo == noOfThread -1 )
               end = totalLen;
            else
                end = begin + blockSize ;
            logger.info("open file ms "+(System.currentTimeMillis() -b));
        }


        @Override
        public Aggregate<T> call() {
            logger.info("begin position "+begin+ " end "+end+" "+t.getClass().getSimpleName());
            List<byte[]> list = new ArrayList<byte[]>(batchSize);
            int i = 0, error = 0 ;
            long b = System.currentTimeMillis();
            local.setCurrent( begin);
            while (! local.isEnd(end) ) {
                try {
                    byte[] bs = local.next();
                    list.add(bs);
                    i++;
                    if ( list.size() >= batchSize ) {
                        Invoker ivk = new Invoker( invoker.getClassName(), invoker.getMethod(), new Object[] { list });
                        try {
                            T ivt = (T) client.invoke(ivk);
                            t.aggregate(ivt);
                            logger.info(t.toString() + " ivt " + ivt.toString());
                        } catch ( Exception ex) {
                            logger.error( ex.getMessage());
                        } finally {
                            list = new ArrayList<byte[]>(batchSize);
                        }
                    }
                } catch (Exception ex) {
                    logger.error( ex.getMessage());
                    error++;
                }
            }
            if ( list.size() > 0 ) {
                try {
                    Invoker ivk = new Invoker(invoker.getClassName(), invoker.getMethod(), new Object[]{list});
                    T ivt = (T) client.invoke(ivk);
                    t.aggregate(ivt);
                } catch ( Exception ex) {
                    logger.error( ex.getMessage());
                }
            }
            logger.info("total record sent "+i+" error "+ error +" duration ms "+ (System.currentTimeMillis() -b) );
            return t;
        }
    }
}
