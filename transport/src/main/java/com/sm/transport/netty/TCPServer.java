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

package com.sm.transport.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class TCPServer {
    private int port;
    private Channel channel;
    private ServerBootstrap bootstrap;

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

    /**
     * Use nonSharableChannelPipeline which create a decoder for each connection
     * encoder is thread safe for netty
     * @param port
     * @param serverHandler
     * @return
     */
    public static TCPServer start(int port, final ChannelUpstreamHandler serverHandler) {
        return start(port, true, nonSharableChannelPipelineFactory(serverHandler, new Encoder()));
    }

    /**
     * decoder must be thread safe, other wise data corruption
     * @param port
     * @param serverHandler
     * @param decoder
     * @param encoder
     * @return
     */
    public static TCPServer start(int port, final ChannelUpstreamHandler serverHandler, final FrameDecoder decoder,
                                  final ChannelDownstreamHandler encoder) {
        return start(port, serverHandler, decoder, encoder, true);

    }

    /**
     * decoder must be thread safe, other wise data corruption
     * @param port
     * @param serverHandler
     * @param decoder
     * @param encoder
     * @return
     */
    public static TCPServer startOio(int port, final ChannelUpstreamHandler serverHandler, final FrameDecoder decoder,
                                     final ChannelDownstreamHandler encoder) {
        return start(port, serverHandler, decoder, encoder, false);

    }

    private static TCPServer start(int port, final boolean nio, final ChannelPipelineFactory factory ) {
        TCPServer server = new TCPServer(port);
        server.bootstrap = new ServerBootstrap( getChannelFactory(nio) );
        // Set up the pipeline factory.
        server.bootstrap.setPipelineFactory(factory) ;
        // Bind and start to accept incoming connections.
        server.channel = server.bootstrap.bind(new InetSocketAddress(port));
        return server;
    }

    private static TCPServer start(int port, final ChannelUpstreamHandler serverHandler, final FrameDecoder decoder,
            final ChannelDownstreamHandler encoder, final boolean nio) {
        TCPServer server = new TCPServer(port);
        server.bootstrap = new ServerBootstrap( getChannelFactory(nio) );
        // Set up the pipeline factory.
        server.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        decoder,
                        encoder,
                        serverHandler);
            }
        });

        // Bind and start to accept incoming connections.
        server.channel = server.bootstrap.bind(new InetSocketAddress(port));
        return server;
    }


    private static ChannelPipelineFactory nonSharableChannelPipelineFactory( final ChannelUpstreamHandler serverHandler,
                                                                         final Encoder encoder) {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new Decoder(),
                        encoder,
                        serverHandler);
            }
        };
    }

    public static ChannelFactory getChannelFactory(boolean nio) {
        if ( nio )
            return new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        else
            return new OioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
    }

    public static TCPServer startOio(int port, final ChannelUpstreamHandler serverHandler) {
        return start(port, false, nonSharableChannelPipelineFactory(serverHandler, new Encoder()));
        //return startOio(port, serverHandler, new Decoder(), new Encoder());
    }

    public static TCPServer start(int port) {
        return start(port, new ServerHandler());
    }

    public Channel getChannel() {
        return channel;
    }

    public void shutdown() {
        channel.unbind();
        channel.close();
        bootstrap.releaseExternalResources();
    }

}
