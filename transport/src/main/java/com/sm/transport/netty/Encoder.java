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

import com.sm.message.Header;
import com.sm.message.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.util.concurrent.atomic.AtomicInteger;

import static com.sm.transport.Utils.*;
import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

public class Encoder extends OneToOneEncoder {
    private static final Log logger = LogFactory.getLog(Encoder.class);
    // transport version no, default will be 0
    private byte version;
    private AtomicInteger seqNo = new AtomicInteger(0);


    public Encoder(byte version) {
        this.version = version;
    }

    public Encoder() {
        this( (byte) 0);
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        //logger.info("encode msg "+msg.getClass().getName());
        if ( (msg instanceof ChannelBuffer) ) return msg;
        else{
            if ( msg instanceof Request ) {
                return doEncode( (Request) msg , ctx, channel);
            }
            else if ( msg instanceof byte[] ) {
                return doEncode( (byte[]) msg, ctx, channel);
            }
            else {
                // close channel
                channel.close();
                throw new Exception( msg.getClass().getName()+" is not supported");
            }
        }
    }


    private Object doEncode(Request request, ChannelHandlerContext ctx, Channel channel) throws Exception {
        //version from request header;
        byte ver = request.getHeader().getSerializeVersion();
        //overwrite if request header == 0 and version != 0, use version
        if ( ver == 0 && version != 0)
            ver = version;
        int pLen = request.getPayload() == null ? 0 : request.getPayload().length;
        byte[] header = request.getHeader().toByte();
        // encoding length , and check the max value of integer, should be 2GB
        int len = TOTAL + header.length + pLen + R_SIZE;
        if ( len > Integer.MAX_VALUE ) {
            channel.close();
            throw new Exception( len+" exceeding Integer.MAX_VALUE");
        }
        ChannelBufferOutputStream bout =   new ChannelBufferOutputStream(dynamicBuffer(
                    len, ctx.getChannel().getConfig().getBufferFactory()));
        bout.write(SIGNATURE);
        bout.write(ver);
        bout.writeInt(len);
        bout.writeShort((short) header.length);
        bout.write(header);
        // request type
        bout.write((byte) request.getType().ordinal());
        bout.write( request.getPayload());
        bout.flush();
        bout.close();
        ChannelBuffer encoded = bout.buffer();
        return encoded;
    }

    private Object doEncode(byte[] payload, ChannelHandlerContext ctx, Channel channel ) throws Exception {
        Header header = new Header("bytes", seqNo.getAndIncrement(), version );
        Request request = new Request( header, payload);
        return doEncode(request, ctx, channel);
    }

}
