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

package com.sm.transport.grizzly.codec;

import com.sm.message.Header;
import com.sm.message.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.AbstractTransformer;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.TransformationException;
import org.glassfish.grizzly.TransformationResult;
import org.glassfish.grizzly.attributes.Attribute;
import org.glassfish.grizzly.attributes.AttributeStorage;

import static com.sm.message.Request.RequestType.getRequestType;
import static com.sm.transport.Utils.R_SIZE;
import static com.sm.transport.Utils.SIGNATURE;
import static com.sm.transport.Utils.*;

public class RequestDecoder extends AbstractTransformer<Buffer, Request> {
    private static final Log logger = LogFactory.getLog(RequestDecoder.class);

    protected final Attribute<VersionLength> lengthAttribute;
    //public final static int MAX_LEN = 1 << 26 ;
    private byte version;


    public RequestDecoder() {
        this( (byte) 0);
    }

    public RequestDecoder(byte version) {
        this.version = version;
        lengthAttribute = attributeBuilder.createAttribute("Request.Size");
    }


    @Override
    protected TransformationResult<Buffer, Request> transformImpl(AttributeStorage storage, Buffer input) throws TransformationException {
        //Integer packSize = lengthAttribute.get(storage);
        VersionLength versionLength = lengthAttribute.get(storage);
        if (versionLength == null) {
            if (input.remaining() < OFFSET) {
                return TransformationResult.createIncompletedResult(input);
            }
            byte signature = input.get();
            byte ver = input.get();
            int packSize = input.getInt();
            if (signature != SIGNATURE  ) {
                //channel.close();
                throw new TransformationException("expect "+SIGNATURE+" but get "+signature+" "+" "+input.toString());
            }
            if ( packSize < 0 || packSize > MAX_LEN ) {
                //channel.close();
                throw new TransformationException(packSize+" Exceed Integer.MAX_VALUE or MAX_LEN");
            }
            //use versionlength
            versionLength = new VersionLength(packSize, ver);
            //lengthAttribute.set(storage, packSize);
            lengthAttribute.set(storage, versionLength);
        }

        if (input.remaining() < (versionLength.getLength() -OFFSET) ) {
            //logger.info("packSize "+versionLength.getLength() + " remaining "+input.remaining() );
            return TransformationResult.createIncompletedResult(input);
        }
        int tmpLimit = input.limit();
        input.limit(input.position() + (versionLength.getLength() -OFFSET) );
        Request request= doDecode(input, versionLength.getLength(), versionLength.getVersion()  );
        input.position(input.limit());
        input.limit(tmpLimit);
        return TransformationResult.createCompletedResult(
                request, input);
    }


    protected Request doDecode(Buffer buffer, int len, byte ver) {
        //read all byte from buffer first, in case exception during de serialize
        short headLen = buffer.getShort();
        byte[] bytes = new byte[ (int) headLen];
        buffer.get( bytes);
        //read request type before payload
        byte type = buffer.get();
        byte[] payload = new byte[ len - headLen - OFFSET - R_SIZE - H_SIZE ];
        buffer.get( payload);
        Header header = Header.toHeader( bytes);
        // assign serializeVersion
        header.setSerializeVersion( ver);
        return new Request( header, payload, getRequestType( type));
    }

    @Override
    public String getName() {
        return "RequestDecoder";
    }

    @Override
    public boolean hasInputRemaining(AttributeStorage storage, Buffer input) {
        boolean result = input != null && input.hasRemaining();
        //logger.info("hasInputRemaining "+result +" storage "+storage.getAttributes().toString() + " buffer "+input.toString());
        return result;
    }

    @Override
    public void release(AttributeStorage storage) {
        lengthAttribute.remove(storage);
        super.release(storage);
    }

    public byte getVersion() {
        return version;
    }

    public static class VersionLength {
        int length;
        byte version;

        public VersionLength(int length, byte version) {
            this.length = length;
            this.version = version;
        }

        public int getLength() {
            return length;
        }

        public byte getVersion() {
            return version;
        }
    }
}
