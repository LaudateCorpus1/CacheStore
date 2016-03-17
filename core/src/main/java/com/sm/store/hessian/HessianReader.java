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
import com.sm.store.hessian.PhpTokenizer.PhpDataType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class HessianReader implements Hessian2Constants {
    private static Log logger = LogFactory.getLog(HessianReader.class);
    byte[] stream;
    int current =0 ;
    //PhpTokenizer.ClassDef classDef ;
    protected AtomicInteger seqno = new AtomicInteger(0);
    //Map<String, ClassDef> classDefMap = new HashMap<String, ClassDef>();
    protected ArrayList<Object> _refs   = new ArrayList<Object>();
    protected ArrayList<ClassDef> _classDefs    = new ArrayList<ClassDef>();
    protected ArrayList<String> _types   = new ArrayList<String>();    // true if this is the last chunk
    private boolean _isLastChunk;
    // the chunk length
    private int _chunkLength;
    private StringBuffer _sbuf = new StringBuffer();
    private PhpWriter writer;

    public HessianReader(byte[] stream) {
        this.stream = stream;
        this.writer = new PhpWriter(new HashMap<String, String>());
    }

    public HessianReader(byte[] stream, PhpWriter writer) {
        this.stream = stream;
        this.writer = writer;
    }

    /**
     *
     * @return the current php writer contents
     */
    public byte[] getBytes() {
        return writer.getBytes();
    }

    public int read() {
        if (EOF() ) throw new RuntimeException("stream overflow");
        else return  (int) stream[current++] & 0xff ;
    }

    int peek() {
        if (EOF() ) throw new RuntimeException("stream overflow");
        else return  (int) stream[current] & 0xff ;
    }

    PhpDataType peekType() {
        if (EOF() ) throw new RuntimeException("stream overflow");
        int tag =  (int) stream[current] & 0xff ;
        return findType( tag);
    }

    boolean EOF() {
        if ( current == stream.length ) return true;
        else return false;
    }

    public PhpDataType findType(int tag) {
        switch (tag) {
            case 'C' :
                return PhpDataType.Class;
            case 'O' :
                return PhpDataType.Object;
            case 'I' :
                return PhpDataType.Int;
            case 'S' :
                return PhpDataType.String;
            case 'D' :
            case 'F' :
                return PhpDataType.Double;
            case 'H' :
            case 'M' :
                return PhpDataType.Map;
            case 'L' :
                return PhpDataType.Long;
            case 'N' :
                return PhpDataType.Null;
            default :
               if ( tag >= 0x80 && tag <= 0xbf )
                    return PhpDataType.Int ;
                else if (tag >= 0xc0 && tag <= 0xcf)
                    return PhpDataType.Int;
                else if ( tag >= 0xd0 && tag <= 0xd7)
                    return PhpDataType.Long;
                else if ( tag >= 0xd8 && tag <= 0xef)
                    return PhpDataType.Long;
                else if (tag >= 0xf0 && tag <= 0xff )
                    return PhpDataType.Long ;
                else if (tag >= 0x38 && tag <= 0x3f )
                    return PhpDataType.Long;
                else if ( tag >= 0x00 && tag <= 0x1f ) {
                   return PhpDataType.String;
                }
                else if ( tag >=0x70 && tag <= 0x7f)
                   return PhpDataType.List;
                else
                    throw new RuntimeException("wrong type tag "+ tag);
        }
    }

    /**
     * Reads an arbitrary object from the input stream when the type
     * is unknown.
     */
    public Object readObject() {
      if(EOF() ) return null;
      int tag =  read();
      switch (tag) {
      case 'N':
        return null;
      case 'H': {
          readMap();
          return null;
          //return findSerializerFactory().readMap(this, null);
      }
      case 'I':
      case BC_LONG_INT:
        return ((read() << 24)
            + (read() << 16)
            + (read() << 8)
            + read());
      case 'L':
        return parseLong();

      case BC_DOUBLE_ZERO:
        return 0;

      case BC_DOUBLE_ONE:
        return 1;

      case BC_DOUBLE_BYTE:
        return (byte) read() ;

      case BC_DOUBLE_SHORT:
        return ((short) (256 * read() + read()));

      case BC_DOUBLE_MILL:  {
          int mills = parseInt();
          return (0.001 * mills);
      }

      case 'D':
        return parseDouble();

      case 'S':
      case BC_STRING_CHUNK:
        _isLastChunk = tag == 'S';
        _chunkLength = (read() << 8) + read();
        _sbuf.setLength(0);
        int ch;
        while ((ch = parseChar()) >= 0)
          _sbuf.append((char) ch);

        return _sbuf.toString();
      case 'M': {
        //String type = readType();
        //return findSerializerFactory().readMap(this, type);
        readMap();
        return null;
      }
      case 'C': {
            ClassDef classDef = readClassDef() ;
            _classDefs.add( classDef);
            return readObject();
      }
      case 'O': {
          int ref = readInt();
          if (_classDefs.size() <= ref)    throw new RuntimeException("Illegal object reference #" + ref);
          ClassDef classDef = _classDefs.get(ref);
          writer.writeObjectDef( classDef);
          return readObjectInstance(classDef);
      }
      case BC_REF: {
          int ref = readInt();
          return ref;
      }
      case BC_LIST_FIXED: {
      // fixed length lists
      //String type = readType();
          throw new RuntimeException("BC_LIST_FIXED not supported");
      }
      case BC_LIST_FIXED_UNTYPED: {
      // fixed length lists
          int length = readInt();
          readList( length);
          return null;
      }
      default:
        if ( tag >= 0X60 && tag <= 0X6f ) {
            int ref = tag - 0x60;
            if (_classDefs.size() < ref)  throw new RuntimeException("No classes defined at reference "+ Integer.toHexString(tag) );
            ClassDef classDef = _classDefs.get(ref);
            writer.writeObjectDef( classDef);
            return readObjectInstance( classDef);
        }
        else if( tag >= 0x70 && tag <= 0x77) {
        	int length = tag - 0x70;
        	String type = readType();
            readList( length);
            return null;
            //throw new RuntimeException("not support fixed length lists");
        }
        else if (tag >=0x78 && tag <= 0x7f) {
          int length = tag - 0x78;
          readList( length);
          return null;
        }
        else if ( tag >= 0x80 && tag <= 0xbf )
            return (tag - BC_INT_ZERO );
        else if (tag >= 0xc0 && tag <= 0xcf)
            return ( ((tag - BC_INT_BYTE_ZERO) << 8) + read());
        else if ( tag >= 0xd0 && tag <= 0xd7)
            return (((tag - BC_LONG_SHORT_ZERO) << 16) + 256 *  read() + read());
        else if ( tag >= 0xd8 && tag <= 0xef)
            return (tag - BC_LONG_ZERO);
        else if (tag >= 0xf0 && tag <= 0xff )
            return ( ((tag - BC_LONG_BYTE_ZERO) << 8) + read() ) ;
        else if (tag >= 0x38 && tag <= 0x3f )
            return ( ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 * read() + read() );
        else if ( tag >= 0x00 && tag <= 0x1f ) {
            _isLastChunk = true;
            _chunkLength = tag - 0x00;
            _sbuf.setLength(0);
            while ((ch = parseChar()) >= 0) {
              _sbuf.append((char) ch);
            }
            writer.write( ("s:"+_sbuf.length()+":"+_sbuf.toString()+";").getBytes() );
            return _sbuf.toString();
        }
        else
            throw new RuntimeException("unknown code " + tag);
      }
    }


    public void readMap(){
        int pos = current+2;
        //set size to 0 for time being
        writer.write(("a:0:").getBytes());
        writer.write((byte)'{');
        int i = 0;
        while ( true) {
            //read key
            readObject();
            // read value
            readObject();
            i++;
            if (  EOF() ) break;
            else {
                int tag = peek();
                if ( tag == 'Z')
                    break;
            }
        }
        writer.write((byte)'}');
        //@TODO need two path or use copy to ratify the map size issue
    }

    public void readList(int len) {
        writer.write(("a:"+len+"{").getBytes());
        for ( int i =0 ; i < len ; i++) {
            readObject();
            if ( EOF()) break;
            else {
                int tag = peek();
                if ( tag == 'Z') break;
            }
        }
        writer.write( (byte) '}');
    }


    public Object readObjectInstance(ClassDef classDef) {
        //write left bracket
        writer.write((byte) '{');
        for (FieldDef fieldDef : classDef.getFields() ) {
            if ( fieldDef.getType() == null || fieldDef.getType() == PhpDataType.Undefined ) {
                int tag =peek();
                fieldDef.type = findType( tag);
            }
            Object obj = null ;
            switch ( fieldDef.getType() ) {
                case Class:
                    readObject();
                    break;
                case Int:
                    obj = readInt();
                    break;
                case Long:
                    obj = readLong();
                    break;
                case String :
                    obj = readString();
                    break;
                case Double:
                    obj = readDouble();
                    break;
                case Boolean:
                    obj = readBoolean();
                    break;
                case Object:
                case List:
                case Map:
                     readObject();
                     break;
                case Null:
                    writer.write( (byte) 'N');
                    break;
                default:
                    throw new RuntimeException("not support type "+fieldDef.getType());
            }
            if ( obj != null ) {
                writer.writeField( fieldDef.getName(), fieldDef.getType(), obj);
                //logger.info(fieldDef.getName() + " " + fieldDef.getType() + " " + obj.toString());
            }
        }
        writer.write( (byte)'}');
        return null;
    }


  public long readLong()  {
    int tag = read();

    switch (tag) {
    case 'N':
      return 0;

    case 'F':
      return 0;

    case 'T':
      return 1;
      //case LONG_BYTE:
    case BC_DOUBLE_BYTE:
      return (byte)  read();

    case BC_DOUBLE_SHORT:
      return (short) (256 * read() + read());

    case 'I':
    case BC_LONG_INT:
      return parseInt();

    case 'L':
      return parseLong();

    case BC_DOUBLE_ZERO:
      return 0;

    case BC_DOUBLE_ONE:
      return 1;

    case BC_DOUBLE_MILL: {
        int mills = parseInt();
        return (long) (0.001 * mills);
    }
    case 'D':
      return (long) parseDouble();

    default:
        if ( tag >= 0x80 && tag <= 0xbf )
            return tag - BC_INT_ZERO;
        else if (tag >= 0xc0 && tag <= 0xcf)
            return ((tag - BC_INT_BYTE_ZERO) << 8) + read();
        else if ( tag >= 0xd0 && tag <= 0xd7)
            return ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 *  read() + read();
        else if ( tag >= 0xd8 && tag <= 0xef)
            return tag - BC_LONG_ZERO;
        else if (tag >= 0xf0 && tag <= 0xff )
            return ((tag - BC_LONG_BYTE_ZERO) << 8) + read();
        else if (tag >= 0x38 && tag <= 0x3f )
            return ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 * read() + read();
        else
           throw new RuntimeException("long "+ tag);
    }
  }

 public boolean readBoolean()   {
    int tag = read();

    switch (tag) {
    case 'T':
        return true;
    case 'F':
        return false;
    case 'L':
      return parseLong() != 0;

    case BC_DOUBLE_ZERO:
      return false;

    case BC_DOUBLE_ONE:
      return true;

    case BC_DOUBLE_BYTE:
      return read() != 0;

    case BC_DOUBLE_SHORT:
      return (0x100 * read() + read()) != 0;

    case 'D':
      return parseDouble() != 0.0;

    case 'N':
      return false;

    default:
      throw new RuntimeException("boolean "+tag);
    }
  }
    /**
     * Reads an integer
     *
     * <pre>
     * I b32 b24 b16 b8
     * </pre>
     */
    public final int readInt()
    {
      //int tag = offset < _length ? (_buffer[offset++] & 0xff) : read();
      int tag = read();

      switch (tag) {
      case 'N':
        return 0;
      case 'F':
        return 0;
      case 'T':
        return 1;
      case 'I':
      case BC_LONG_INT:
        return ((read() << 24)
            + (read() << 16)
            + (read() << 8)
            + read());
      case 'L':
        return (int) parseLong();
      case BC_DOUBLE_ZERO:
        return 0;
      case BC_DOUBLE_ONE:
        return 1;
      case BC_DOUBLE_BYTE:
        return (byte) read();
      case BC_DOUBLE_SHORT:
        return (short) (256 * read() + read());

      case BC_DOUBLE_MILL: {
        int mills = parseInt();
        return (int) (0.001 * mills);
      }
      case 'D':
        return (int) parseDouble();

    default:
        if ( tag >= 0x80 && tag <= 0xbf )
            return tag - BC_INT_ZERO;
        else if (tag >= 0xc0 && tag <= 0xcf)
            return ((tag - BC_INT_BYTE_ZERO) << 8) + read();
        else if ( tag >= 0xd0 && tag <= 0xd7)
            return ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 *  read() + read();
        else if ( tag >= 0xd8 && tag <= 0xef)
            return tag - BC_LONG_ZERO;
        else if (tag >= 0xf0 && tag <= 0xff )
            return ((tag - BC_LONG_BYTE_ZERO) << 8) + read();
        else if (tag >= 0x38 && tag <= 0x3f )
            return ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 * read() + read();
        else
            throw new RuntimeException("Expect Int get " + tag);
    }
  }

  public double readDouble()
  {
    int tag = read();

    switch (tag) {
    case 'N':
      return 0;

    case 'F':
      return 0;

    case 'T':
      return 1;

    case 'I':
    case BC_LONG_INT:
      return parseInt();
    case 'L':
      return (double) parseLong();

    case BC_DOUBLE_ZERO:
      return 0;

    case BC_DOUBLE_ONE:
      return 1;

    case BC_DOUBLE_BYTE:
      return (byte) (read() );

    case BC_DOUBLE_SHORT:
      return (short) (256 * read() + read());

    case BC_DOUBLE_MILL:  {
    	int mills = parseInt();
	    return 0.001 * mills;
    }
    case 'D':
      return parseDouble();

    default:
       if ( tag >= 0x80 && tag <= 0xbf )
            return tag - BC_INT_ZERO;
        else if (tag >= 0xc0 && tag <= 0xcf)
            return ((tag - BC_INT_BYTE_ZERO) << 8) + read();
        else if ( tag >= 0xd0 && tag <= 0xd7)
            return ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 *  read() + read();
        else if ( tag >= 0xd8 && tag <= 0xef)
            return tag - BC_LONG_ZERO;
        else if (tag >= 0xf0 && tag <= 0xff )
            return ((tag - BC_LONG_BYTE_ZERO) << 8) + read();
        else if (tag >= 0x38 && tag <= 0x3f )
            return ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 * read() + read();
        else
            throw new RuntimeException("double "+ tag);
    }
  }


    public String readString() {
      int tag = read();

      switch (tag) {
      case 'N':
        return null;
      case 'T':
        return "true";
      case 'F':
        return "false";

      case 'I':
      case BC_LONG_INT:
        return String.valueOf(parseInt());
        // direct long

      case 'L':
        return String.valueOf(parseLong());

      case BC_DOUBLE_ZERO:
        return "0.0";

      case BC_DOUBLE_ONE:
        return "1.0";

      case BC_DOUBLE_BYTE:
        return String.valueOf((byte) read() );

      case BC_DOUBLE_SHORT:
        return String.valueOf(((short) (256 * read() + read())));

      case BC_DOUBLE_MILL:  {
          int mills = parseInt();
          return String.valueOf(0.001 * mills);
      }

      case 'D':
        return String.valueOf(parseDouble());

      case 'S':
      case BC_STRING_CHUNK:
        _isLastChunk = tag == 'S';
        _chunkLength = (read() << 8) + read();

        _sbuf.setLength(0);
        int ch;

        while ((ch = parseChar()) >= 0)
          _sbuf.append((char) ch);

        return _sbuf.toString();

      case 0x30: case 0x31: case 0x32: case 0x33:
        _isLastChunk = true;
        _chunkLength = (tag - 0x30) * 256 + read();

        _sbuf.setLength(0);

        while ((ch = parseChar()) >= 0)
          _sbuf.append((char) ch);

        return _sbuf.toString();

      default:
        if ( tag >= 0x80 && tag <= 0xbf )
            return String.valueOf(tag - BC_INT_ZERO );
        else if (tag >= 0xc0 && tag <= 0xcf)
            return String.valueOf( ((tag - BC_INT_BYTE_ZERO) << 8) + read());
        else if ( tag >= 0xd0 && tag <= 0xd7)
            return String.valueOf(((tag - BC_LONG_SHORT_ZERO) << 16) + 256 *  read() + read());
        else if ( tag >= 0xd8 && tag <= 0xef)
            return String.valueOf(tag - BC_LONG_ZERO);
        else if (tag >= 0xf0 && tag <= 0xff )
            return String.valueOf( ((tag - BC_LONG_BYTE_ZERO) << 8) + read() ) ;
        else if (tag >= 0x38 && tag <= 0x3f )
            return String.valueOf( ((tag - BC_LONG_SHORT_ZERO) << 16) + 256 * read() + read() );
        else if ( tag >= 0x00 && tag <= 0x1f ) {
            _isLastChunk = true;
            _chunkLength = tag - 0x00;
            _sbuf.setLength(0);
            while ((ch = parseChar()) >= 0) {
              _sbuf.append((char) ch);
            }
            return _sbuf.toString();
        }
        else
            throw new RuntimeException("wrong tag for readString "+ tag);
      }
  }

    void checkOverFlow(int len) {
        if (current + len >= stream.length ) {
            throw new RuntimeException("Overflow length "+stream.length+" current "+current+" "+len);
        }
    }

    ClassDef readClassDef() {
        String clsName = readString();
        ClassDef classDef = new ClassDef( clsName, new ArrayList<FieldDef>(), PhpDataType.Object , seqno.getAndIncrement() );
        int no = readInt();
        for (int i = 0 ; i < no ; i++) {
            classDef.getFields().add( nextField() );
        }
        return classDef;
    }

    FieldDef nextField() {
        String filedName = readString();
        return new FieldDef(filedName, PhpDataType.Undefined, null);
    }


  private double parseDouble()  {
    long bits = parseLong();
    return Double.longBitsToDouble(bits);
  }

  public String readType()  {
    int code =  read();

    switch (code) {
    case 0x00: case 0x01: case 0x02: case 0x03:
    case 0x04: case 0x05: case 0x06: case 0x07:
    case 0x08: case 0x09: case 0x0a: case 0x0b:
    case 0x0c: case 0x0d: case 0x0e: case 0x0f:

    case 0x10: case 0x11: case 0x12: case 0x13:
    case 0x14: case 0x15: case 0x16: case 0x17:
    case 0x18: case 0x19: case 0x1a: case 0x1b:
    case 0x1c: case 0x1d: case 0x1e: case 0x1f:

    case 0x30: case 0x31: case 0x32: case 0x33:
    case BC_STRING_CHUNK: case 'S': {
    	String type = readString();
	    if (_types == null)
	        _types = new ArrayList();
       	_types.add(type);
       	return type;
     }

    default: {
        int ref = readInt();

        if (_types.size() <= ref)
          throw new IndexOutOfBoundsException("type ref #" + ref + " is greater than the number of valid types (" + _types.size() + ")");
        else
           return (String) _types.get(ref);
    }//default
   }// switch
  }

 /**
   * Reads a character from the underlying stream.
   */
  private int parseChar()
  {
    while (_chunkLength <= 0) {
      if (_isLastChunk)
        return -1;

      int code =  read();

      switch (code) {
      case BC_STRING_CHUNK:
        _isLastChunk = false;

        _chunkLength = (read() << 8) + read();
        break;

      case 'S':
        _isLastChunk = true;

        _chunkLength = (read() << 8) + read();
        break;

      case 0x30: case 0x31: case 0x32: case 0x33:
	    _isLastChunk = true;
	    _chunkLength = (code - 0x30) * 256 + read();
	    break;

      default:
           if ( code >= 0x00 && code <= 0x1f) {
                _isLastChunk = true;
                _chunkLength = code - 0x00;
            }
            else
                throw new RuntimeException("readString "+ code);
      } //switch

   } //while

    _chunkLength--;

    return parseUTF8Char();
  }

    /**
      * Parses a single UTF8 character.
      */
    private int parseUTF8Char()
    {
        int ch =  read();

        if (ch < 0x80)
         return ch;
        else if ((ch & 0xe0) == 0xc0) {
         int ch1 = read();
         int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);
         return v;
        }
        else if ((ch & 0xf0) == 0xe0) {
         int ch1 = read();
         int ch2 = read();
         int v = ((ch & 0x0f) << 12) + ((ch1 & 0x3f) << 6) + (ch2 & 0x3f);
         return v;
        }
        else
            throw new RuntimeException("bad utf-8 encoding at "+ ch);
    }

 /**
   * Parses a 64-bit long value from the stream.
   *
   * <pre>
   * b64 b56 b48 b40 b32 b24 b16 b8
   * </pre>
   */
  private long parseLong()
  {

    long b64 = read();
    long b56 = read();
    long b48 = read();
    long b40 = read();
    long b32 = read();
    long b24 = read();
    long b16 = read();
    long b8 = read();

    return ((b64 << 56)
	    + (b56 << 48)
	    + (b48 << 40)
	    + (b40 << 32)
	    + (b32 << 24)
	    + (b24 << 16)
	    + (b16 << 8)
	    + b8);
  }
   private int parseInt()
   {
       int b32 = read();
       int b24 = read();
       int b16 = read();
       int b8 = read();
       return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;

   }
}
