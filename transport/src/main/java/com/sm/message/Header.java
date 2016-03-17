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

package com.sm.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public final class Header implements Serializable {

    public static enum SerializableType {
        Default ((byte) 0), Hessian ( (byte) 1), Json ((byte) 2), String ( (byte) 3), Avro ( (byte) 4), GPB ((byte) 5),
        Thrift ((byte) 6), PassThrough ((byte) 16);

        final byte value;
        SerializableType(byte value) {
            this.value = value;
        }

        public static SerializableType getSerializableType(byte value) {
            switch ( value ) {
                case 0 : return Default;
                case 1 : return Hessian;
                case 2 : return Json;
                case 3 : return String;
                case 4 : return Avro;
                case 5 : return GPB;
                case 6 : return Thrift;
                case 16 : return PassThrough;
                default: return Default;
            }
        }

    }

    private static Log logger = LogFactory.getLog(Header.class);

    //length is in one byte, which means name can not longer than 245
    public static final int FIRST = 1 + 8 + 8 ;
    private String name;
    private long version;
    private byte release;
    private long nodeId;
    private byte serializeVersion;

    public Header(String name, long version, byte release, long nodeId) {
        this.name = name;
        this.version = version;
        this.release = release;
        this.nodeId = nodeId;
    }

    public Header(String name, long version, byte release) {
        this(name, version, release, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public byte getRelease() {
        return release;
    }

    public void setRelease(byte release) {
        this.release = release;
    }

    public int getReleaseAsInt() {
        return (int) ( 0xFF & release );
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public byte getSerializeVersion() {
        return serializeVersion;
    }

    public SerializableType getSerializableType() {
        return SerializableType.getSerializableType( serializeVersion);
    }

    public void setSerializableType( SerializableType type) {
        serializeVersion = type.value;
    }

    public void setSerializeVersion(byte serializeVersion) {
        this.serializeVersion = serializeVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (version != header.version) return false;
        if (release != header.release) return false;
        if (name != null ? !name.equals(header.name) : header.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) version;
        result = 31 * result + (int) release;
        return result;
    }

    @Override
    public String toString() {
        return name+"."+ release+"."+nodeId+"."+ version ;
    }

    @Override
    public Header clone() {
        return new Header( name, version, release, nodeId ) ;
    }


    public byte[] toByte() {
        try {
            byte[] n = name.getBytes("UTF-8");
            byte[] bytes = new byte[ FIRST + n.length ];
            int i = 0;
            bytes[i] = release;
            System.arraycopy( ByteBuffer.allocate(8).putLong(version).array(), 0, bytes, i+1, 8);
            System.arraycopy( ByteBuffer.allocate(8).putLong(nodeId).array(), 0, bytes, i+9, 8);
            System.arraycopy( n, 0, bytes, i+FIRST, n.length );
            return bytes;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Header toHeader(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap( data);
        byte release = buf.get();
        long version = buf.getLong();
        long node = buf.getLong();
        byte[] n = new byte[ data.length - FIRST ];
        buf.get(n);
        try {
            String name =  new String( n, "UTF-8");
            return new Header(name, version, release, node);
        } catch (UnsupportedEncodingException ex) {
            throw  new RuntimeException( ex.getMessage() );
        }


    }
}
