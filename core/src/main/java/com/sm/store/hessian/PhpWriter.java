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

package com.sm.store.hessian;

import java.util.Map;

import static com.sm.store.hessian.PhpTokenizer.*;

public class PhpWriter {
    byte[] buffer;
    Map<String, String> nameMap;
    int offset;

    public PhpWriter(Map<String, String> nameMap) {
        this.nameMap = nameMap;
        this.buffer = new byte[4092];
        this.offset = 0;
    }

    public byte[] getBytes() {
        if ( offset < buffer.length ) {
            byte[] dest = new byte[offset];
            System.arraycopy( buffer, 0, dest, 0, offset);
            return dest;
        }
        else
            return buffer;
    }

    void writeField(String fieldName, PhpDataType type, Object value) {
        byte[] fieldPrefix = ("s"+COLON+fieldName.length()+COLON +DOUBLE+fieldName+DOUBLE+SEMI).getBytes();
        byte[] fieldPostfix;
        if ( type == PhpDataType.String ) {
            String val = value.toString();
            fieldPostfix = ("s"+COLON+ val.length()+COLON+DOUBLE+val+DOUBLE+SEMI).getBytes();
        }
        else if ( type == PhpDataType.Int) {
            int val = ((Integer) value);
            fieldPostfix = ("i"+COLON+ val+SEMI).getBytes();
        }
        else if ( type == PhpDataType.Double ) {
            double val = ((Double) value);
            fieldPostfix = ("d"+COLON+ val+SEMI).getBytes();
        }
        else if ( type == PhpDataType.Boolean ) {
            int  val = (Boolean) value ? 1 : 0  ;
            fieldPostfix = ("b"+COLON+ val+SEMI).getBytes();
        }
        else if ( type == PhpDataType.Object) {
            fieldPostfix = null;
        }
        else if ( type == PhpDataType.Array) {
            fieldPostfix = null;
        }
        else
            fieldPostfix = null;

        if ( fieldPostfix != null ) {
            write( fieldPrefix);
            write( fieldPostfix);
        }
    }

    void writeArrayDef(ClassDef classDef) {
         write(("a" + COLON + classDef.getFields().size() + COLON).getBytes());
    }

    void writeObjectDef(ClassDef classDef) {
        String name = classDef.getName();
        String clsName ;
        if ( nameMap.containsKey( name)) {
            clsName = nameMap.get(name);
        }
        else {
            clsName =  name.substring(name.lastIndexOf(".")+1);
        }
        write( ("O"+COLON+clsName.length()+COLON+DOUBLE+clsName+DOUBLE+COLON+ classDef.getFields().size()+COLON).getBytes());
    }

    public void write(byte b) {
        checkOverFlow(1);
        buffer[ offset++] = b;
    }


    public void write(byte[] bytes) {
        checkOverFlow( bytes.length);
        for (int i=0 ; i < bytes.length; i++) {
            buffer[offset ++] = bytes[i];
        }
    }

   public void checkOverFlow(int len) {
        if (offset + len >= buffer.length ) {
            if ( offset + len >= buffer.length ) {
                //logger.info("expand length "+buffer.length+" to "+ (2* buffer.length));
                byte[] dest = new byte[ buffer.length *2 ];
                System.arraycopy(buffer, 0, dest, 0, buffer.length);
                buffer = dest;
            }
        }
   }
}
