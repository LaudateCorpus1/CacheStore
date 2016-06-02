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

package com.sm.transport.netty;

import com.sm.message.Header;
import com.sm.message.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import static com.sm.message.Request.RequestType.getRequestType;
import static com.sm.transport.Utils.*;

public class Decoder extends FrameDecoder {
    private static final Log logger = LogFactory.getLog(Decoder.class);
    // transport version no, default will be 0
    private byte version;
    //max package 64M
    //public final static int MAX_LEN = 1 << 26 ;

    public Decoder(byte version) {
        this.version = version;
    }

    public Decoder() {
        this( (byte) 0);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        //logger.info("decoding...");
        if (buffer.readableBytes() < TOTAL) return null;
        //buffer.markReaderIndex();
        int index = buffer.readerIndex();
        //getByte and getUnsignedInt did not change buffer index position
        //main goal is to validate signature byte and length of package is position int
        byte signature = buffer.getByte(index+ 0);
        int len = (int) buffer.getUnsignedInt(index+ LENGTH_OFFSET);
        if (signature != SIGNATURE  ) {
            //channel.close();
            throw new Exception("expect "+SIGNATURE+" but get "+signature+" len "+len+" "+channel.getRemoteAddress().toString()
                    +" "+buffer.toString());
        }
        if ( len < 0 || len > MAX_LEN ) {
            //channel.close();
            throw new Exception(len+" Exceed Integer.MAX_VALUE or MAX_LEN"+" "+channel.getRemoteAddress().toString());
        }
        //logger.info("read len "+len);
        if ( buffer.readableBytes() >= len ) {
            signature = buffer.readByte();
            byte ver = buffer.readByte();
            int totalLen = buffer.readInt();
            if ( len != totalLen )  logger.warn("mismatch len "+len+" totalLen "+totalLen+" "+signature+" "+" "+
                    channel.getRemoteAddress().toString());
            Request request= doDecode( buffer, len, ver);
            if ( buffer.readerIndex() != index + len )
                logger.warn("readerIndex "+buffer.readerIndex()+" get "+ (index+len));
            return request;
        }
        else {
            return null;
        }
    }


    private Request doDecode(ChannelBuffer buffer, int len, byte ver) {
        //read all byte from buffer first, in case exception during de serialize
        short headLen = buffer.readShort();
        byte[] bytes = new byte[ (int) headLen];
        buffer.readBytes( bytes);
        //read request type before payload
        byte type = buffer.readByte();
        byte[] payload = new byte[ len - headLen - TOTAL - R_SIZE ];
        buffer.readBytes( payload);
        Header header = Header.toHeader( bytes);
        // assign serializeVersion
        header.setSerializeVersion( ver);
        return new Request( header, payload, getRequestType( type));
    }

}
