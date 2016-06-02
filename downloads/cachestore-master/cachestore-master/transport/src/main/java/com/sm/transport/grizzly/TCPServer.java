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
 */package com.sm.transport.grizzly;

import com.sm.transport.grizzly.codec.RequestCodecFilter;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TCPServer {
    private int port;
    private TCPNIOTransport transport;

    private TCPServer(int port) {
        this.port = port;
    }


    public int getPort() {
        return port;
    }


    @Override
    public String toString() {
        try {
            return InetAddress.getLocalHost().getHostName()+" "+port;
        } catch (UnknownHostException e) {
            return "host unknown port "+port;
        }
    }

    public void shutdown() {
        transport.shutdown();
    }

    public static TCPServer start(int port) {
        return start(port, new RequestCodecFilter((byte) 0), new ServerFilter() );
    }

    public static TCPServer start(int port,  BaseFilter serverFilter){
        return start(port, new RequestCodecFilter((byte) 0), serverFilter);
    }

    public static TCPServer start(int port, BaseFilter requestCodecFilter, BaseFilter serverFilter)  {
        TCPServer server = new TCPServer( port);
        // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add filters to the chain
        filterChainBuilder.add(new TransportFilter())
            .add(requestCodecFilter)
            .add(serverFilter);

        // Create TCP NIO transport
        server.transport = TCPNIOTransportBuilder.newInstance()
                /*.setReadBufferSize(1024 * 1024 * 16)
                .setTcpNoDelay(true)
                .setMemoryManager(new ByteBufferManager())*/
                .build();
        server.transport.setProcessor(filterChainBuilder.build());
        try {
            // Bind server socket and start transport
            server.transport.bind(port);
            server.transport.start();
            return server;
        } catch (Exception ex) {
            if ( server.transport != null) {
                server.transport.shutdown();
            }
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }
}
