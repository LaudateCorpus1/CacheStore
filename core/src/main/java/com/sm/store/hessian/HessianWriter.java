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

import com.caucho.hessian.io.Hessian2Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class HessianWriter  implements Hessian2Constants{
    private static final Log logger = LogFactory.getLog(HessianWriter.class);
    public static final int SIZE = 4091;

    Map<String, String> nameMap;
    //byte array output
    volatile byte[] buffer;
    //buffer index position
    volatile int offset;
    //concurrent lock
    Lock lock = new ReentrantLock();
     // map of types
    HashMap<String,Integer> typeRefs;
    IdentityHashMap<Object, Integer> _refs ;

    public HessianWriter(Map nameMap) {
        this.nameMap = nameMap;
        this.buffer = new byte[SIZE];
        offset = 0;
    }

    public String getMappingName(String name) {
        String str = nameMap.get(name);
        if ( str == null) {
            logger.warn(name+" not in the map");
            return name;
        }
        else return str;
    }

    public Map<String, String> getNameMap() {
        return nameMap;
    }

    public HashMap<String, Integer> getTypeRefs() {
        return typeRefs;
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void writeBoolean(boolean value) {
        if ( value) write( (byte) 'T');
        else write((byte) 'F');
    }


    public void writeInt(int value) {
        //max 5 bytes
        checkOverFlow( 5);
        int offset = this.offset;

        if (INT_DIRECT_MIN <= value && value <= INT_DIRECT_MAX)
            buffer[offset++] = (byte) (value + BC_INT_ZERO);
            else if (INT_BYTE_MIN <= value && value <= INT_BYTE_MAX) {
            buffer[offset++] = (byte) (BC_INT_BYTE_ZERO + (value >> 8));
            buffer[offset++] = (byte) (value);
        }
        else if (INT_SHORT_MIN <= value && value <= INT_SHORT_MAX) {
            buffer[offset++] = (byte) (BC_INT_SHORT_ZERO + (value >> 16));
            buffer[offset++] = (byte) (value >> 8);
            buffer[offset++] = (byte) (value);
        }
        else {
            buffer[offset++] = (byte) ('I');
            buffer[offset++] = (byte) (value >> 24);
            buffer[offset++] = (byte) (value >> 16);
            buffer[offset++] = (byte) (value >> 8);
            buffer[offset++] = (byte) (value);
        }

        this.offset = offset;
    }

    public void checkOverFlow(int len) {
        if (offset + len >= buffer.length ) {
            if ( offset + len >= buffer.length ) {
                logger.info("expand length "+buffer.length+" to "+ (2* buffer.length));
                byte[] dest = new byte[ buffer.length *2 ];
                System.arraycopy(buffer, 0, dest, 0, buffer.length);
                buffer = dest;
            }
            else logger.info("pass "+buffer.length);
        }
    }


    public void writeString(String value) {
        int len = 5;
        //check overflow first
        checkOverFlow(len);
        int offset = this.offset;

        if (value == null) {
          buffer[offset++] = (byte) 'N';
          this.offset = offset;
        }
        else {
          int length = value.length();
          int strOffset = 0;

          while (length > 0x8000) {
            int sublen = 0x8000;
            //check overflow
            checkOverFlow(len+sublen);

            offset = this.offset;
            // chunk can't end in high surrogate
            char tail = value.charAt(strOffset + sublen - 1);

            if (0xd800 <= tail && tail <= 0xdbff)
              sublen--;

            buffer[offset + 0] = (byte) BC_STRING_CHUNK;
            buffer[offset + 1] = (byte) (sublen >> 8);
            buffer[offset + 2] = (byte) (sublen);

            this.offset = offset + 3;

            printString(value, strOffset, sublen);

            length -= sublen;
            strOffset += sublen;
          }
          //check overflow
          checkOverFlow(len+length);
          offset = this.offset;

          if (length <= STRING_DIRECT_MAX) {
            buffer[offset++] = (byte) (BC_STRING_DIRECT + length);
          }
          else if (length <= STRING_SHORT_MAX) {
            buffer[offset++] = (byte) (BC_STRING_SHORT + (length >> 8));
            buffer[offset++] = (byte) (length);
          }
          else {
            buffer[offset++] = (byte) ('S');
            buffer[offset++] = (byte) (length >> 8);
            buffer[offset++] = (byte) (length);
          }

          this.offset = offset;

          printString(value, strOffset, length);
        }
    }

    public byte[] getBytes() {
        if ( offset < buffer.length) {
            byte[] dest = new byte[offset];
            System.arraycopy(buffer, 0, dest, 0, offset);
            return dest;
        }
        else {
            if ( offset > buffer.length)
                throw new RuntimeException("offset "+ offset +" > "+buffer.length);
            return buffer;
        }
    }

    /**
     * Prints a string to the stream, encoded as UTF-8
     *
     * @param v the string to print.
     */
    public void printString(String v, int strOffset, int length)
    {
      int offset = this.offset;
      //byte [] buffer = _buffer;

      for (int i = 0; i < length; i++) {

        char ch = v.charAt(i + strOffset);

        if (ch < 0x80)
          buffer[offset++] = (byte) (ch);
        else if (ch < 0x800) {
          buffer[offset++] = (byte) (0xc0 + ((ch >> 6) & 0x1f));
          buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
        }
        else {
          buffer[offset++] = (byte) (0xe0 + ((ch >> 12) & 0xf));
          buffer[offset++] = (byte) (0x80 + ((ch >> 6) & 0x3f));
          buffer[offset++] = (byte) (0x80 + (ch & 0x3f));
        }
      }

      this.offset = offset;
    }



  /**
   * Writes a byte array to the stream.
   * The array will be written with the following syntax:
   *
   * <code><pre>
   * B b16 b18 bytes
   * </pre></code>
   *
   * If the value is null, it will be written as
   *
   * <code><pre>
   * N
   * </pre></code>
   *
   * param value the string value to write.
   */
  public void writeBytes(byte[] data)
  {
    if (data == null) {
      //buffer[offset++] = 'N';
      write( (byte) 'N');
    }
    else
      writeBytes(data, 0, data.length);
  }

  /**
   * Writes a byte array to the stream.
   * The array will be written with the following syntax:
   *
   * <code><pre>
   * B b16 b18 bytes
   * </pre></code>
   *
   * If the value is null, it will be written as
   *
   * <code><pre>
   * N
   * </pre></code>
   *
   * param value the string value to write.
   */
  public void writeBytes(byte[] data, int offset, int length)
  {
    if (data == null) {
      //buffer[offset++] = (byte) 'N';
      write((byte) 'N');
    }
    else {
      while (SIZE - 3 < length) {
        checkOverFlow( 3+ length);
        int sublen = SIZE - 3;
        buffer[this.offset++] = (byte) BC_BINARY_CHUNK;
        buffer[this.offset++] = (byte) (sublen >> 8);
        buffer[this.offset++] = (byte) sublen;

        System.arraycopy(data, offset, buffer, this.offset, sublen);
        this.offset += sublen;

        length -= sublen;
        offset += sublen;
      }

      if (length <= BINARY_DIRECT_MAX) {
        buffer[this.offset++] = (byte) (BC_BINARY_DIRECT + length);
      }
      else if (length <= BINARY_SHORT_MAX) {
        buffer[this.offset++] = (byte) (BC_BINARY_SHORT + (length >> 8));
        buffer[this.offset++] = (byte) (length);
      }
      else {
        buffer[this.offset++] = (byte) 'B';
        buffer[this.offset++] = (byte) (length >> 8);
        buffer[this.offset++] = (byte) (length);
      }

      System.arraycopy(data, offset, buffer, this.offset, length);
      this.offset += length;
    }
  }

    /**
     * Writes a double value to the stream.  The double will be written
     * with the following syntax:
     *
     * <code><pre>
     * D b64 b56 b48 b40 b32 b24 b16 b8
     * </pre></code>
     *
     * @param value the double value to write.
     */
    public void writeDouble(double value)
    {
      checkOverFlow(10);
      int offset = this.offset;

      int intValue = (int) value;

      if (intValue == value) {
        if (intValue == 0) {
          buffer[offset++] = (byte) BC_DOUBLE_ZERO;

          this.offset = offset;

          return;
        }
        else if (intValue == 1) {
          buffer[offset++] = (byte) BC_DOUBLE_ONE;

          this.offset = offset;

          return;
        }
        else if (-0x80 <= intValue && intValue < 0x80) {
          buffer[offset++] = (byte) BC_DOUBLE_BYTE;
          buffer[offset++] = (byte) intValue;

          this.offset = offset;

          return;
        }
        else if (-0x8000 <= intValue && intValue < 0x8000) {
          buffer[offset + 0] = (byte) BC_DOUBLE_SHORT;
          buffer[offset + 1] = (byte) (intValue >> 8);
          buffer[offset + 2] = (byte) intValue;

          this.offset = offset + 3;

          return;
        }
      }

      int mills = (int) (value * 1000);

      if (0.001 * mills == value) {
        buffer[offset + 0] = (byte) (BC_DOUBLE_MILL);
        buffer[offset + 1] = (byte) (mills >> 24);
        buffer[offset + 2] = (byte) (mills >> 16);
        buffer[offset + 3] = (byte) (mills >> 8);
        buffer[offset + 4] = (byte) (mills);

        this.offset = offset + 5;

        return;
      }

      long bits = Double.doubleToLongBits(value);

      buffer[offset + 0] = (byte) 'D';
      buffer[offset + 1] = (byte) (bits >> 56);
      buffer[offset + 2] = (byte) (bits >> 48);
      buffer[offset + 3] = (byte) (bits >> 40);
      buffer[offset + 4] = (byte) (bits >> 32);
      buffer[offset + 5] = (byte) (bits >> 24);
      buffer[offset + 6] = (byte) (bits >> 16);
      buffer[offset + 7] = (byte) (bits >> 8);
      buffer[offset + 8] = (byte) (bits);

      this.offset = offset + 9;
  }

    /**
      * Writes the list header to the stream.  List writers will call
      * <code>writeListBegin</code> followed by the list contents and then
      * call <code>writeListEnd</code>.
      *
      * <code><pre>
      * list ::= V type value* Z
      *      ::= v type int value*
      * </pre></code>
      *
      * @return true for variable lists, false for fixed lists
      */
     public boolean writeListBegin(int length, String type)
     {

       if (length < 0) {
         if (type != null) {
           //buffer[offset++] = (byte) BC_LIST_VARIABLE;
           write((byte) BC_LIST_VARIABLE);
           writeType(type);
         }
         else
           write((byte) BC_LIST_VARIABLE_UNTYPED);
           //buffer[offset++] = (byte) BC_LIST_VARIABLE_UNTYPED;
         return true;
       }
       else if (length <= LIST_DIRECT_MAX) {
         if (type != null) {
           //buffer[offset++] = (byte) (BC_LIST_DIRECT + length);
           write( (byte) (BC_LIST_DIRECT + length));
           writeType(type);
         }
         else {
           //buffer[offset++] = (byte) (BC_LIST_DIRECT_UNTYPED + length);
           write((byte) (BC_LIST_DIRECT_UNTYPED + length));
         }
         return false;
       }
       else {
         if (type != null) {
           //buffer[offset++] = (byte) BC_LIST_FIXED;
           write((byte) BC_LIST_FIXED);
           writeType(type);
         }
         else {
           //buffer[offset++] = (byte) BC_LIST_FIXED_UNTYPED;
           write((byte) BC_LIST_FIXED_UNTYPED);
         }
         writeInt(length);
         return false;
       }
     }

     /**
      * Writes the tail of the list to the stream for a variable-length list.
      */
     public void writeListEnd()
     {
       //buffer[offset++] = (byte) BC_END;
         write((byte) BC_END);
     }

 /**
   * <code><pre>
   * type ::= string
   *      ::= int
   * </code></pre>
   */
  private void writeType(String type) {
    int len = type.length();
    if (len == 0) {
      throw new IllegalArgumentException("empty type is not allowed");
    }
    if (typeRefs == null)
      typeRefs = new HashMap<String,Integer>();

    Integer typeRefV = typeRefs.get(type);
    if (typeRefV != null) {
      int typeRef = typeRefV.intValue();
      writeInt(typeRef);
    }
    else {
      typeRefs.put(type, Integer.valueOf(typeRefs.size()));
      writeString(type);
    }
  }


   public void writeObject(Object object)
   {
     if (object == null) {
       writeNull();
       return;
     }
//      writeDefinition20(out);
//      writeObjectBegin(cl.getName());
//      writeInstance(obj, out);
   }

  private void writeDefinition20(ClassDef classDef)
  {
    write(classDef.getName().getBytes());
    writeInt( classDef.fields.size());
    for (FieldDef each : classDef.fields) {
      //writeString(each.getName());
        write( each.getName().getBytes());
    }
  }

  public boolean addRef(Object object)
  {
    // return false if there is no IdentifyMap
    if ( _refs == null  ) return false;
    int ref;
    if ( _refs.containsKey( object))
       ref = _refs.get(object);
     else
       ref = -1;

//    int newRef = _refs.size();
//    int ref = _refs.put(object, newRef, false);
//    if (ref != newRef) {
    if ( ref > 0 ) {
      writeRef(ref);

      return true;
    }
    else {
      return false;
    }
  }

   protected void writeRef(int value)  {
        write((byte) BC_REF);
        writeInt(value);
   }

 /**
   * Writes the map header to the stream.  Map writers will call
   * <code>writeMapBegin</code> followed by the map contents and then
   * call <code>writeMapEnd</code>.
   *
   * <code><pre>
   * map ::= M type (<value> <value>)* Z
   *     ::= H (<value> <value>)* Z
   * </pre></code>
   */
  public void writeMapBegin(String type)
  {
    if (type != null) {
      //buffer[offset++] = BC_MAP;
      write((byte) BC_MAP);
      writeType(type);
    }
    else
      write((byte) BC_MAP_UNTYPED);
      //buffer[offset++] = BC_MAP_UNTYPED;
  }

  /**
   * Writes the tail of the map to the stream.
   */
  public void writeMapEnd()
  {
    //buffer[offset++] = (byte) BC_END;
      write((byte) BC_END);
  }


  public void writeNull()
  {
    write( (byte) 'N');

  }

  public void write(byte b) {
    checkOverFlow(1);
    buffer[ offset++] = b;
  }


  public void write(byte[] bytes) {
    checkOverFlow( bytes.length);
    for (int i=0 ; i < bytes.length; i++) {
        buffer[offset++] = bytes[i];
    }
  }


}
