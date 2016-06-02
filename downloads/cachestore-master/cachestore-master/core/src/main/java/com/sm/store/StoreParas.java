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
 */package com.sm.store;

import com.caucho.hessian.io.External;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import voldemort.store.cachestore.BlockUtil;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.CacheValue;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class StoreParas implements Serializable, External {
    public static final int NO_ERROR = 0;
    public static final int OBSOLETE = 1;
    public static final int STORE_EXCEPTION = 2;

    private OpType opType;
    private Key key;
    private Value value;
    private byte remove = 0;

    private int errorCode = 0;

    public StoreParas(){
        super();
    }

    public StoreParas(OpType opType, Key key, Value value) {
        this.opType = opType;
        this.key = key;
        this.value = value;
    }

    public OpType getOpType() {
        return opType;
    }

    public void setOpType(OpType opType) {
        this.opType = opType;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public StoreParas(OpType opType, Key key) {
        this( opType, key, null);
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setRemove(byte remove) {
        this.remove = remove;
    }

    public boolean isRemove() {
        if ( remove == 1 ) return true;
        else return false;
    }
    // value len - int, key length int, opcode 1, error code 4
    public static final int H_LEN = 4 + 4 + 1+ 4 + 1 ;
    public static final int O_LEN = 2 + 8;

    /**
     * use raw protocol without any dependency
     * @return byte[] that serialize StoreParas to byte array
     */
    public byte[] toBytes() {
        int vlen = 0 ;
        if (value != null)
            vlen = ((byte[]) value.getData()).length;
        int vOther = ( vlen > 0 ? O_LEN : 0 );
        byte[] kBytes = BlockUtil.toKeyBytes( key);
        int len = vlen + H_LEN + kBytes.length + vOther ;
        byte[] toReturn = new byte[ len];
        // first
        int i = 0;
        toReturn[i] = opType.value;
        i = 1;
        toReturn[i] = remove;
        // error code
        System.arraycopy(putInt(errorCode), 0, toReturn, i+1, 4 );
        System.arraycopy(putInt(kBytes.length), 0, toReturn, i+5, 4 );
        System.arraycopy(kBytes, 0, toReturn, i+9, kBytes.length );
        System.arraycopy(putInt(vlen), 0, toReturn, i+9+kBytes.length , 4 );
        if ( vlen > 0 ) {
            byte[] data =valueToBytes( vlen, value);
            System.arraycopy( data, 0, toReturn, i+9+4+kBytes.length,  data.length);
        }
        return toReturn;
    }

    public static byte[] putInt(int k) {
        return ByteBuffer.allocate(4).putInt(k).array() ;
    }

    public static byte[] putLong(long k){
        return ByteBuffer.allocate(8).putLong(k).array() ;
    }

    public static byte[] putShort(short k) {
        return ByteBuffer.allocate(2).putShort(k).array() ;
    }

    /**
     * use raw protocol without any dependency
     * @param len
     * @param value
     * @return byte[] that serialize value
     */
    public byte[] valueToBytes(int len, Value value) {
        if ( value == null ) return new byte[0];
        else {
            //
            byte[] bytes = new byte[ len + O_LEN ];
            long v = value.getVersion() ;
            short node = value.getNode() ;
            byte[] data = (byte[]) value.getData() ;
            System.arraycopy(putLong(v), 0, bytes, 0, 8 );
            System.arraycopy(putShort(node), 0, bytes, 8, 2 );
            System.arraycopy(data, 0 , bytes, O_LEN, data.length) ;
            return bytes;
        }
    }


    /**
     * use raw protocol without any dependency
     * @param bytes serialize byte[] by toByte()
     * @return instance StoreParas
     */

    public static StoreParas toStoreParas(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap( bytes);
        OpType op = OpType.getOpType( buf.get());
        byte remove = buf.get();
        int error = buf.getInt();
        int klen = buf.getInt();
        byte[] ks = new byte[klen];
        buf.get(ks);
        Key key = BlockUtil.toKey( ks);
        int vlen = buf.getInt();
        StoreParas pa= new StoreParas(op, key, null);
        pa.setErrorCode( error);
        pa.setRemove(remove );
        if ( vlen == 0 ) {
            //pa.setErrorCode( error);
        }
        else {
            long ver = buf.getLong();
            short node = buf.getShort();
            byte[] data = new byte[ vlen];
            buf.get( data);
            CacheValue ca = CacheValue.createValue( data, ver, node);
            pa.setValue( ca);
        }
        return pa;
    }


    /**
     * using Hessian-SM serialize protocol
     * @param out - Hessian2Output
     * @throws IOException
     */

    @Override
    public void writeExternal(Hessian2Output out) throws IOException {
        out.writeInt(opType.value);
        out.writeInt(remove);
        out.writeInt(errorCode);
        out.writeBytes( BlockUtil.toKeyBytes( key));
        if ( value == null )
            out.writeBoolean(true);
        else {
            out.writeBoolean(false);
            out.writeBytes( Utils.valueToBytes( value));
        }

    }

    /**
     * using Hessian-SM serialize protocol
     * @param in Hessian2Input
     * @throws IOException
     */
    @Override
    public void readExternal(Hessian2Input in) throws IOException {
        opType = OpType.getOpType( (byte) in.readInt());
        remove = (byte) in.readInt();
        errorCode = in.readInt();
        key = BlockUtil.toKey( in.readBytes());
        //value is not null
        boolean isNull = in.readBoolean();
        if ( ! isNull  ) {
            value = Utils.bytesToValue( in.readBytes());
        }
    }

}
