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

import com.sm.message.Header;
import com.sm.message.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sm.message.Request.RequestType.getRequestType;
import static com.sm.transport.Utils.*;


public class RequestCoder extends BaseFilter {
    private static final Log logger = LogFactory.getLog(RequestCoder.class);
    // transport version no, default will be 0
    private byte version;
    private AtomicInteger seqNo = new AtomicInteger(0);
    //public final static int MAX_LEN = 1 << 26 ;

    public RequestCoder(byte version) {
        this.version = version;
    }


    @Override
    public NextAction handleRead(final FilterChainContext ctx) throws IOException {
        final Buffer buffer = ctx.getMessage();

        final int sourceBufferLength = buffer.remaining();
        // If source buffer doesn't contain header
        if (sourceBufferLength < TOTAL) {
            // shutdownNow the filterchain processing and store sourceBuffer to be
            // used next time
            return ctx.getStopAction(buffer);
        }
        byte signature = buffer.get();
        byte ver = buffer.get();
        int totalLen = buffer.getInt();
        if (signature != SIGNATURE  ) {
            //channel.close();
            throw new IOException("expect "+SIGNATURE+" but get "+signature+" "+ ctx.getAddress().toString()
                    +" "+buffer.toString());
        }
        if ( totalLen < 0 || totalLen > MAX_LEN ) {
            //channel.close();
            throw new IOException(totalLen+" Exceed Integer.MAX_VALUE or MAX_LEN"+" "+ctx.getAddress().toString());
        }

        if ( sourceBufferLength < totalLen) {
            logger.info("notready bufferlength "+sourceBufferLength+" totallen "+totalLen);
            // shutdownNow the filterchain processing and store sourceBuffer to be
            // used next time
            return ctx.getStopAction(buffer);
        }
        else
            logger.info("ready bufferlength "+sourceBufferLength+" totallen "+totalLen);
        // shutdownNow the filterchain processing and store sourceBuffer to be
        // Check if the source buffer has more than 1 complete GIOP message
        // If yes - split up the first message and the remainder
        final Buffer remainder = sourceBufferLength > totalLen?
                buffer.split( totalLen) : null;

        ctx.setMessage( doDecode( buffer, totalLen, ver));
        // We can try to dispose the buffer
        buffer.tryDispose();
        // Instruct FilterChain to store the remainder (if any) and continue execution
        return ctx.getInvokeAction(remainder);
    }

    /**
     * Method is called, when we write a data to the Connection.
     *
     * We override this method to perform GIOPMessage -> Buffer transformation.
     *
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleWrite(final FilterChainContext ctx) throws IOException {
        final Request request = ctx.getMessage();

        byte ver = version;
        int pLen = request.getPayload() == null ? 0 : request.getPayload().length;
        byte[] header = request.getHeader().toByte();
        // encoding length , and check the max value of integer, should be 2GB
        int totalLen = TOTAL + header.length + pLen + R_SIZE;
        if ( totalLen > Integer.MAX_VALUE ) {
            throw new IOException( totalLen +" exceeding Integer.MAX_VALUE");
        }
        logger.info("total "+totalLen+" hd "+header.length+" payload "+pLen);
        // Retrieve the memory manager
        final MemoryManager memoryManager =
                ctx.getConnection().getTransport().getMemoryManager();

        // allocate the buffer of required size
        final Buffer bout = memoryManager.allocate(totalLen);
        // Instruct the FilterChain to call the next filter
        bout.put(SIGNATURE);
        bout.put(ver);
        bout.putInt(totalLen);
        bout.putShort((short) header.length);
        bout.put(header);
        // request type
        bout.put((byte) request.getType().ordinal());
        bout.put( request.getPayload());
        // Set the Buffer as a context message
        ctx.setMessage( bout.flip() );
        return ctx.getInvokeAction();
    }

    protected Request doDecode(Buffer buffer, int len, byte ver) {
        //read all byte from buffer first, in case exception during de serialize
        short headLen = buffer.getShort();
        byte[] bytes = new byte[ (int) headLen];
        buffer.get( bytes);
        //read request type before payload
        byte type = buffer.get();
        byte[] payload = new byte[ len - headLen - TOTAL - R_SIZE ];
        buffer.get( payload);
        Header header = Header.toHeader( bytes);
        // assign serializeVersion
        header.setSerializeVersion( ver);
        return new Request( header, payload, getRequestType( type));
    }

}
