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

package com.sm.store.cluster;

import com.sm.store.client.TCPClientFactory;
import com.sm.transport.Client;
import com.sm.transport.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection {
private static final Log logger = LogFactory.getLog(Connection.class);
    private String host;
    private int port;
    //make add version 1 to support different decode /encode for store paras
    protected volatile Client tcpClient;
    private long lastConnected;
    private volatile short failCount;
    private long lastTry;
    public static final short MAX_TRY = 2;
    private Lock lock = new ReentrantLock();
    private volatile boolean inQueue;
    protected TCPClientFactory.ClientType clientType;

    public Connection(String host, int port) {
        this(host, port, TCPClientFactory.ClientType.Grizzly);
    }

    public Connection(String host, int port, TCPClientFactory.ClientType clientType) {
        this.host = host;
        this.port = port;
        this.clientType = clientType;
        this.failCount = 0;
        this.inQueue = false;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isDown() {
        lock.lock();
        try {
            //return false;
            if ( failCount >= MAX_TRY ) return true;
            else return false;
        } finally {
            lock.unlock();
        }
    }

    public Client getTcpClient() {
        return tcpClient;
    }

    public boolean connect(long timeout) {
        if ( tcpClient == null ) {
            lock.lock();
            try {
                //double check
                if (tcpClient == null ) {
                    //tcpClient = TCPClient.start( host, port, handler, (byte) 1);
                    tcpClient = TCPClientFactory.createClient(clientType, host+":"+port, timeout);
                    if ( tcpClient != null && tcpClient.isConnected()) {
                        failCount = 0;
                        lastConnected = System.currentTimeMillis();
                        return  true;
                    }
                    else {
                        failCount ++;
                        lastTry = System.currentTimeMillis();
                        return false;
                    }
                }
                else {
                    if ( tcpClient.isConnected())
                        return true;
                    else
                        return false;
                }
            } catch (ConnectionException ex) {
                failCount ++;
                lastTry = System.currentTimeMillis();
                return false;
            }
            finally {
                lock.unlock();
            }
        }
        else {
            return tcpClient.isConnected();
        }
    }


    public boolean isConnected() {
        if (tcpClient != null && tcpClient.isConnected())
            return true;
        else
            return false;
    }

    public boolean reconnect(long timeout) {
        Client client = null;
        try {
            client = TCPClientFactory.createClient(clientType,  host+":"+port, timeout);
        } catch (Exception ex) {
            return false;
        }
        lock.lock();
        try {
            if ( client != null ) {
                tcpClient.close();
                tcpClient = client;
                lastConnected = System.currentTimeMillis();
                failCount = 0;
                return true;
            }
            else {
                lastTry = System.currentTimeMillis();
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean reconnect() {
        if (tcpClient == null ) return false;
        else {
            lock.lock();
            try {
                // double check for multiple threads
                if ( tcpClient.isConnected() ){
                    // reset failCount
                    failCount = 0;
                    return true;
                }
                else {
                    //boolean toReturn = tcpClient.reconnect();
                    Client toReturn = null;
                    try {
                        toReturn = tcpClient.reconnect() ;
                    } catch (Exception ex)  {

                    }
                    if (  toReturn == null) {
                        failCount ++;
                        lastTry = System.currentTimeMillis();
                    }
                    else {
                        failCount = 0;
                        lastConnected = System.currentTimeMillis();
                        tcpClient = toReturn;
                    }
                    return ( toReturn == null ? false : true );

                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void close() {
        if ( tcpClient != null)
            tcpClient.close();
    }
    public void shutdown() {
        if (tcpClient != null)
            tcpClient.shutdown();
    }

//    public void setTcpClient(TCPClient tcpClient) {
//        this.tcpClient = tcpClient;
//    }

    public long getLastConnected() {
        return lastConnected;
    }

    public short getFailCount() {
        return failCount;
    }


    public long getLastTry() {
        return lastTry;
    }

    public void putIfAbsent(BlockingQueue<Connection> queue) {
        lock.lock();
        try {
            if ( ! inQueue) {
                try {
                    logger.info("put in reconnectQueue " + toString());
                    boolean flag = queue.offer(this, 200, TimeUnit.MILLISECONDS);
                    if ( flag ) inQueue = true;
                    else
                        logger.warn("unable put in reconnectQueue "+toString());
                } catch (InterruptedException e) {
                    //swallow exception
                }
            }
        } finally {
            lock.unlock();
        }
    }


    public void remoteFromQueue(BlockingQueue<Connection> queue){
        lock.lock();
        try {
            boolean flag = queue.remove(this);
            if ( flag ) inQueue = false;
            else
                logger.warn("unable to remove from reconnectQueue "+toString());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (port != that.port) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return   host + " " +
                " " + port +
                " " + new Date(lastConnected).toString() +
                " failCount " + failCount +
                " lastTry " + new Date(lastTry).toString() ;
    }
}
