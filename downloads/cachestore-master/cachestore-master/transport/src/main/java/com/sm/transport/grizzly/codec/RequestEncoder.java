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

package com.sm.transport.grizzly.codec;

import com.sm.message.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.AttributeStorage;

import static com.sm.transport.Utils.*;

public class RequestEncoder extends AbstractTransformer<Request, Buffer> {
    private static final Log logger = LogFactory.getLog(RequestEncoder.class);
    private byte version;

    public RequestEncoder() {
        this( (byte) 0);
    }

    public RequestEncoder(byte version) {
        this.version = version;
    }

    @Override
    protected TransformationResult<Request, Buffer> transformImpl(AttributeStorage storage, Request request) throws TransformationException {
        byte ver = request.getHeader().getSerializeVersion() ;
        int pLen = request.getPayload() == null ? 0 : request.getPayload().length;
        byte[] header = request.getHeader().toByte();
        // encoding length , and check the max value of integer, should be 2GB
        int totalLen = OFFSET + header.length + pLen + R_SIZE + H_SIZE;
        if ( totalLen > Integer.MAX_VALUE ) {
            throw new TransformationException( totalLen +" exceeding Integer.MAX_VALUE");
        }
        //logger.info("total "+totalLen+" hd "+header.length+" payload "+pLen);
        // Retrieve the memory manager
        //final MemoryManager memoryManager = ctx.getConnection().getTransport().getMemoryManager();

        // allocate the buffer of required size
        final Buffer bout = obtainMemoryManager(storage).allocate(totalLen);
        // Instruct the FilterChain to call the next filter
        bout.put(SIGNATURE);
        bout.put(ver);
        bout.putInt(totalLen);
        bout.putShort((short) header.length);
        bout.put(header);
        // request type
        bout.put((byte) request.getType().ordinal());
        bout.put( request.getPayload());

        bout.flip();
        bout.allowBufferDispose(true);

        return TransformationResult.createCompletedResult(bout, null);
    }

    @Override
    public String getName() {
         return "RequestDecoder";
    }

    @Override
    public boolean hasInputRemaining(AttributeStorage storage, Request input) {
        return input != null;
    }


}
