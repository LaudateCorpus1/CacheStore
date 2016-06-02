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
 */package com.sm.transport.netty;

import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.transport.Client;
import com.sm.transport.ConnectionException;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TCPClient implements Client {
    private String host;
    private int port;
    private ChannelFuture future;
    private ClientBootstrap bootstrap;
    private ChannelUpstreamHandler clientHandler;
    // default for 5 seconds
    public final static long TIME_OUT = 5000L;


    private TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public ClientBootstrap getBootstrap() {
        return bootstrap;
    }

    /**
     *
     * @param host
     * @param port
     * @param clientHandler
     * @param encoder
     * @param decoder must be thread safe
     * @return
     */
    public static TCPClient start(String host, int port, final ChannelUpstreamHandler clientHandler, final ChannelDownstreamHandler encoder,
                                  final FrameDecoder decoder){
        return start(host, port, clientHandler, encoder, decoder, true);

    }

    /**
     * Use nonSharableChannelPipeline which create a decoder for each connection
     * @param host
     * @param port
     * @param clientHandler
     * @return
     */
    public static TCPClient start(String host, int port, final ChannelUpstreamHandler clientHandler ){
        //return start(host, port, clientHandler, new Encoder(), new Decoder());
        return start(host, port, true, clientHandler, nonSharableChannelPipelineFactory(clientHandler, new Encoder()));
    }

    /**
     * Use nonSharableChannelPipeline which create a decoder for each connection
     * @param host
     * @param port
     * @param clientHandler
     * @return
     */
    public static TCPClient startOio(String host, int port, final ChannelUpstreamHandler clientHandler ){
        //return startOio(host, port, clientHandler, new Encoder(), new Decoder());
        return start(host, port, false, clientHandler, nonSharableChannelPipelineFactory(clientHandler, new Encoder()));
    }

    /**
     *
     * @param host
     * @param port
     * @param clientHandler
     * @param encoder
     * @param decoder must be thread safe
     * @return
     */
    public static TCPClient startOio(String host, int port, final ChannelUpstreamHandler clientHandler, final ChannelDownstreamHandler encoder,
                                  final FrameDecoder decoder){
        return start( host, port, clientHandler, encoder, decoder, false);


    }

    private static TCPClient start(String host, int port, final boolean nio,final ChannelUpstreamHandler clientHandler, final ChannelPipelineFactory factory ) {
        TCPClient client = new TCPClient(host, port);
        client.clientHandler= clientHandler;
        // Configure the client.
        client.bootstrap = new ClientBootstrap( getClientSocketChannelFactory(nio));
        client.bootstrap.setPipelineFactory( nonSharableChannelPipelineFactory( clientHandler, new Encoder()));
        // Start the connection attempt.
        client.future = client.bootstrap.connect(new InetSocketAddress(host, port));
        if (client.getFuture().awaitUninterruptibly(TIME_OUT) && client.getFuture().getChannel().isConnected())
            return client ; //logger.info("wait ok");
        else {
            client.future.getChannel().close();
            client.bootstrap.releaseExternalResources();
            throw new ConnectionException("not able to connect "+ client.toString());
        }
    }

    private static TCPClient start(String host, int port, final ChannelUpstreamHandler clientHandler, final ChannelDownstreamHandler encoder,
                              final FrameDecoder decoder, boolean nio){
        TCPClient client = new TCPClient(host, port);
        client.clientHandler= clientHandler;
        // Configure the client.
        client.bootstrap = new ClientBootstrap( getClientSocketChannelFactory(nio));
        // Set up the pipeline factory.
        client.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        encoder,
                        decoder,
                        clientHandler);
            }
        });

        // Start the connection attempt.
        client.future = client.bootstrap.connect(new InetSocketAddress(host, port));
        if (client.getFuture().awaitUninterruptibly(TIME_OUT) && client.getFuture().getChannel().isConnected())
            return client ; //logger.info("wait ok");
        else {
            client.future.getChannel().close();
            client.bootstrap.releaseExternalResources();
            throw new ConnectionException("not able to connect "+ client.toString());
        }

    }

    private static ChannelPipelineFactory nonSharableChannelPipelineFactory( final ChannelUpstreamHandler clientHandler,
                                                                             final Encoder encoder) {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        encoder,
                        new Decoder(),
                        clientHandler);
            }
        };
    }

    private static ClientSocketChannelFactory getClientSocketChannelFactory(boolean nio) {
        if ( nio)
            return new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());
        else
            return new OioClientSocketChannelFactory(
                    Executors.newCachedThreadPool());
    }

    public static TCPClient start(String host, int port, final ChannelUpstreamHandler clientHandler, byte version) {
        //return start(host, port, clientHandler, new Encoder(version), new Decoder(version));
        return start(host, port, true, clientHandler, nonSharableChannelPipelineFactory(clientHandler, new Encoder(version)));
    }

    public static TCPClient start(String host, int port){
        return start(host, port, new ClientAsynHandler());
    }

    public boolean isConnected() {
        //switch from isOpen()
        return future.getChannel().isConnected() ;
    }

    public Client reconnect() {
        future = bootstrap.connect(new InetSocketAddress(host, port));
        if (future.awaitUninterruptibly(TIME_OUT) && isConnected())
            return this ;
        else
            return null;
    }

    public ChannelUpstreamHandler getClientHandler() {
        return clientHandler;
    }

    public Response sendRequest(Request request) {
        //if ( request.getType() == Request.RequestType.Async ) throw new RuntimeException("sendRequest did not support asynchronized call ");
        return ((Send) clientHandler).sendRequest( request, future.getChannel() );
    }

    public void sendMessage(Request request) {
        if (  request.getType() != Request.RequestType.Async ) throw new RuntimeException("sendMessage support async call");
        future.getChannel().write( request);
    }

    public Response invoke(Request request) {
         return ((Send) clientHandler).invoke( request, future.getChannel() );
    }

    public void close() {
        future.getChannel().close();
    }

    public void shutdown() {
        bootstrap.shutdown();
        bootstrap.releaseExternalResources();
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

    @Override
    public String toString() {
        return host+" "+port;
    }


}
