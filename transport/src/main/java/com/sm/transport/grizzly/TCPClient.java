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

package com.sm.transport.grizzly;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.Client;
import com.sm.transport.ConnectionException;
import com.sm.transport.grizzly.codec.RequestCodecFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;



public class TCPClient implements Client {
    private static final Log logger = LogFactory.getLog(TCPClient.class);
    private String host;
    private int port;
    private Connection connection;
    private BaseFilter clientFilter;
    private TCPNIOTransport transport;
    // default for 6 seconds
    public final static long TIME_OUT = 6000L;

    private TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void shutdown() {
        logger.info("Stopping transport...");
        // shutdownNow the transport
        transport.shutdown();
        logger.info("Stopped transport...");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return host+" "+port;
    }

    public boolean isConnected() {
        if ( connection != null && connection.isOpen()) return true;
        else return false;
    }

    public Client reconnect() {
        if ( isConnected() ) return this;
        else {
            close();
            shutdown();
            return start( host, port, new RequestCodecFilter( (byte) 0), clientFilter );
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static TCPClient start(String host, int port, BaseFilter coder, BaseFilter clientFilter) {
        TCPClient client = new TCPClient( host, port);
        client.clientFilter = clientFilter;
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());

        // StringFilter is responsible for Buffer <-> String conversion
        filterChainBuilder.add(coder);

        // EchoFilter is responsible for echoing received messages
        filterChainBuilder.add(clientFilter);

        // Create TCP transport
        client.transport = TCPNIOTransportBuilder.newInstance().build();

        client.transport.setProcessor(filterChainBuilder.build());

        try {
            // binding transport to start listen on certain host and port
            try {
                client.transport.start();
                // perform async. connect to the server
                Future<Connection> future = client.transport.connect(client.host, client.port);
                // wait for connect operation to complete
                client.connection = future.get(5, TimeUnit.SECONDS);
                if ( client.connection.isOpen())
                    return client;
                else
                    throw new ConnectionException("can not connect to "+client.toString());
                //System.in.read();
            } catch (Exception e) {
                //shutdown transport thread
                if ( client.transport != null )
                    client.transport.shutdown();
                throw new RuntimeException( e.getMessage(), e);
            }

        } finally {

        }
    }
    @Override
    public boolean equals(Object client) {
        if ( client == null ) return false;
        else {
            if (! ( client instanceof TCPClient) ) return false;
            if ( ((TCPClient) client).getPort() == port && ((TCPClient)client).getHost().equals( host ))
                return true;
            else
                return false;
        }
    }

    public void close() {
        if (connection != null)
            connection.close();
    }

    public static TCPClient start(String host, int port, BaseFilter clientFilter) {
        return start(host, port, new RequestCodecFilter((byte) 0), clientFilter);
    }

    public static TCPClient start(String host, int port) {
        ClientFilter clientFilter = new ClientFilter( TIME_OUT);
        return start( host, port, new RequestCodecFilter((byte)0 ) , clientFilter);
    }

    public Response sendRequest(Request request) {
       return ((Send) clientFilter).sendRequest( request, connection);
    }

    public Response invoke(Request request) {
        return ((Send) clientFilter).invoke( request, connection);
    }

    public void sendMessage(Request request) {
        ((Send) clientFilter).sendMessage( request, connection);
    }

}
