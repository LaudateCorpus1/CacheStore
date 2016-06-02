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
 */package com.sm.message;

import java.io.Serializable;
import java.util.Arrays;

public final class Request implements Serializable {
    public static enum RequestType {
        Normal ((byte) 0), Resend ( (byte) 1), Failover ((byte) 2), Sync ((byte) 3),
        Async ( (byte) 4), Response ((byte) 5), Message ((byte) 6), Invoker ((byte) 7), Streaming ((byte) 8),
        StreamFailover ((byte) 9), StreamResend ((byte) 10), Scan   ((byte) 11), KeyIterator ((byte) 12),
        Cluster ((byte) 13)  ;

        final byte value;
        RequestType(byte value) {
            this.value = value;
        }
        public static RequestType getRequestType(byte value) {
            switch ( value ) {
                case 0 : return Normal;
                case 1 : return Resend;
                case 2 : return Failover;
                case 3 : return Sync;
                case 4 : return Async;
                case 5 : return Response;
                case 6 : return Message;
                case 7 : return Invoker;
                case 8 : return Streaming;
                case 9 : return StreamFailover;
                case 10 : return StreamResend;
                case 11 : return Scan;
                case 12 : return KeyIterator;
                case 13 : return Cluster;
                default: return Normal;
            }
        }

    }

    private Header header;
    private byte[] payload;
    private RequestType type;

    public Request(Header header, byte[] payload, RequestType type) {
        if ( header == null || payload == null) throw new RuntimeException("header or payload can not be null");
        this.payload = payload;
        this.header = header;
        this.type = type;
    }

    public Request(Header header, byte[] payload) {
        this( header, payload, RequestType.Normal);
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (header != null ? !header.equals(request.header) : request.header != null) return false;
        if (!Arrays.equals(payload, request.payload)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = header != null ? header.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return header.toString()+ " type "+ type + ( payload != null ? " payload.length "+payload.length : "");
    }



//    public void writeExternal(ObjectOutput out) throws IOException {
//        out.write( type.value );
//        byte[] h = header.toByte();
//        out.writeShort( (short) h.length);
//        out.write(h);
//        // write length of payload
//        if ( payload == null ) out.write( 0);
//        else {
//            out.write( payload.length);
//            out.write( payload);
//        }
//    }


//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        type = RequestType.getRequestType( in.readByte());
//        int hLen = (int)  in.readShort();
//        byte[] head = new byte[hLen];
//        in.readFully( head);
//        header = Header.toHeader(head);
//        int pLen = in.readInt();
//        if (pLen > 0 ) {
//            payload = new byte[ pLen];
//            in.readFully( payload);
//        }
//    }
}
