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

package com.sm.store.hessian;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PhpTokenizer {
    private static final Log logger = LogFactory.getLog(PhpTokenizer.class);

    public enum PhpDataType { Object, Class, String, Int, Double, Boolean, Long, Map, List, Null, Resource, Array, KeyValue, Undefined ;
    }
    public static final char COLON = ':' ;
    public static final char SEMI = ';' ;
    public static final char LEFTBracket = '{' ;
    public static final char RIGHTBracket = '}' ;
    public static final char QUOTE ='"' ;
    public static final char SLASH ='/';
    public static final char SINGLE ='\'';
    public static final char DOUBLE = '"';

    byte[] stream ;
    int current = 0;
    AtomicInteger seqno = new AtomicInteger(0);
    AtomicInteger arrno = new AtomicInteger(0);

    HessianWriter writer;
    //boolean delay = false;
    Map<String, ClassDef> classMap = new HashMap<String, ClassDef>();
    Map<String, ClassDef> arrayMap = new HashMap<String, ClassDef>();
    // map of classes
    //private final IdentityIntMap classRefs = new IdentityIntMap(256);
    volatile boolean traverse = true;
    int arrayNo = 0;


    PhpTokenizer(byte[] stream, HessianWriter writer) {
        this.stream = stream;
        this.writer = writer;
    }

    void parse() {

        PhpDataType type = resetStream(true);
        //skipDelimiter();
        if ( type == PhpDataType.Object ) {
            buildClassDef(type);
            //set traverse to false
            type = resetStream( false);
            //reset position
            readObject(type);
        }
        else if (type == PhpDataType.Array)  {
            buildClassDef( type);
            type = resetStream( false);
            readArray(type);
        }
        else {
            nextField(false);
        }
    }

    PhpDataType resetStream(boolean flag) {
        current = 0;
        traverse = flag;
        PhpDataType type = peekTokenType();
        skipDelimiter();
        return type;
    }

    byte[] getHessianBytes() {
        return writer.getBytes();
    }

    void buildClassDef(PhpDataType type) {
        if ( type == PhpDataType.Object ) {
            readObject(type);
        }
        else if (type == PhpDataType.Array)  {
            readArray(type);
        }
        else {
            // do nothing
        }

    }

    FieldDef readObject(PhpDataType type) {
        int l = readInt();
        String clsName = readString();
        //after readString, call skip again
        skipDelimiter();
        int no = readInt();
        char c = read();

        if ( c != LEFTBracket ) throw new RuntimeException("object expect left bracket get "+c);
        boolean writeFiledName = false ;
        //int refSize ;
        //int begin = writer.getOffset();
        ClassDef classDef = null;
        if ( classMap.containsKey( clsName)) {
            if ( traverse ) {
                writeFiledName = false;
            }
            else {
                classDef = classMap.get(clsName);
                if ( classDef.isFirstVisit() ) {
                    writeFiledName = true;
                    String name = writer.getMappingName(clsName);
                    writer.write((byte) 'C');
                    writer.writeString(name);
                    //write number of fields
                    writeClassDef( classDef);
                    writer.write((byte)'O');
                    writer.writeInt( classDef.getRefNo());
                    classDef.setFirstVisit( false);
                }
                else {
                    writeFiledName = false;
                    writer.write((byte)'O');
                    writer.writeInt( classDef.getRefNo());
                }

            }
        }
        else {
            //write class name first
            if ( traverse ) {
                classDef = new ClassDef(clsName, new ArrayList<FieldDef>(no), type, seqno.getAndIncrement());
                classMap.put(clsName, classDef);
                writeFiledName =true;
            }
            else {
                throw new RuntimeException("should not happen for travers false and "+clsName+" not exist");
            }
       }
        //int pos = writer.getOffset();
        logger.info("cls "+clsName+ " no "+no);
        for (int i = 0; i < no ; i ++) {
            FieldDef fieldDef = nextField(writeFiledName);
            if (writeFiledName || traverse) {
                if ( classDef != null ) {
                    classDef.getFields().add( fieldDef);
                    logger.info(fieldDef.getName()+" "+fieldDef.getType());
                }
            }
        }
        if ( (c =read()) != RIGHTBracket )
             throw new RuntimeException("object expect right bracket get "+c);
        return new FieldDef(clsName, type, classDef);
    }



//    void writeInstance(ClassDef classDef) {
//        writer.write((byte)'O');
//        writer.writeInt( classDef.getRefNo());
//        for (FieldDef each : classDef.getFields()) {
//            switch ( each.getType()) {
//                case Int:
//                    writer.writeInt( (Integer) each.getValue());
//                    break;
//                case Double :
//                    writer.writeDouble( (Double) each.getValue());
//                    break;
//                case Boolean:
//                    writer.writeBoolean( (Boolean) each.getValue());
//                    break;
//                case String:
//                    writer.writeString( (String) each.getValue());
//                    break;
//                case Object:
//                    if ( each.getValue() instanceof ClassDef)
//                        writeInstance( (ClassDef) each.getValue());
//                    else if ( each.getValue() instanceof  FieldDef )
//                        writeInstance( (ClassDef) ((FieldDef) each.getValue()).getValue());
//                    else
//                        throw new RuntimeException("wrong value for class "+each.getValue().getClass().getName());
//                    break;
//                case Null:
//                    writer.writeNull();
//                    break;
//                case Array:
//                    break;
//                default:
//                    throw new RuntimeException("unknown type "+each.getType());
//            }
//        }
//    }

    void writeClassDef(ClassDef classDef) {
         if ( classDef.getFields().size() > 0) {
             writer.writeInt( classDef.getFields().size());
             for (FieldDef each : classDef.getFields()) {
                 writer.writeString( each.getName());
             }
         }
         else
             logger.info(classDef.getName()+" size is 0");
    }

//    boolean isPrimitive(PhpDataType type ) {
//        if ( type == PhpDataType.String || type == PhpDataType.Int || type == PhpDataType.Double
//                || type == PhpDataType.Boolean)
//            return true;
//        else
//            return false;
//    }


    FieldDef readArray(PhpDataType type) {
        int no = readInt();
        char c = read();
        if ( c != LEFTBracket ) throw new RuntimeException("object expect left bracket get "+c);
        // for array delay is false
        //boolean delay = false;
        ClassDef classDef = null;
        if (traverse ) {
            int seq = arrno.getAndIncrement();
            classDef = new ClassDef("array"+seq, new ArrayList<FieldDef>(no), PhpDataType.List, seq);
            arrayMap.put(classDef.getName(), classDef);
        }
        else {
            String name = "array"+arrayNo++;
            if ( ! arrayMap.containsKey( name )) throw new RuntimeException(name+" is not in arrayMap");
            classDef = arrayMap.get(name);
            if ( classDef.getType() == PhpDataType.Map)
                writer.writeMapBegin(null);
            else  {
                writer.writeListBegin(no, null);
            }
        }
        logger.info("cls array no " + no);
        for (int i = 0; i < no ; i ++) {
            KeyValue keyValue = nextKeyValue( traverse);
            if ( traverse ) {
                if ( keyValue.getKey() == PhpDataType.String ) {
                    classDef.setType( PhpDataType.Map);
                }
                else {
                    if ( (Integer) keyValue.getKey() != no )
                        classDef.setType( PhpDataType.Map);
                }
            }
        }
        if ( traverse ) logger.info(classDef.toString() );
        else {
            if ( classDef.getType() == PhpDataType.Map )
                writer.writeMapEnd();
            else
                writer.writeListEnd();
        }
        if ( (c =read()) != RIGHTBracket )
            throw new RuntimeException("object expect right bracket get "+c);
        // write out after pop
        return new FieldDef("array", type, classDef);
    }

    KeyValue nextKeyValue(boolean writeFieldName) {
        PhpDataType kType = peekTokenType();
        skipDelimiter();
        Object key ;
        PhpDataType type;
        if ( kType == PhpDataType.Int) {
            int k = readInt();
            if ( !traverse)
                writer.writeInt( k);
            key = new Integer(k);
            type = PhpDataType.Int;
        }
        else {
            int k = readInt();
            key = readString();
            if (! traverse)
                writer.writeString( (String) key);
            skipDelimiter();
            type = PhpDataType.String ;
        }
        PhpDataType vType = peekTokenType();
        skipDelimiter();
        Object value = parseObject( vType, writeFieldName);
        return new KeyValue( key, value);
    }

    FieldDef nextField(boolean writeFieldName) {
        String t= nextToken();
        int l = readInt();
        String fldName = readString();
        skipDelimiter();
        PhpDataType type = peekTokenType();
        skipDelimiter();
        Object obj = parseObject(type, writeFieldName);
        if (writeFieldName)
            return new FieldDef( fldName, type, obj);
        else
            return null;

    }

    Object parseObject(PhpDataType type, boolean writeFieldName) {
        Object obj = null;
        switch ( type ) {
            case String:
                int len = readInt();
                obj = readString();
                assert (len == obj.toString().length());
                skipDelimiter();
                if ( ! traverse)
                    writer.writeString( (String) obj);
                break;
            case Int :
                obj = readInt();
                if ( !traverse)
                    writer.writeInt( (Integer) obj);
                break;
            case Double:
                obj= readDouble();
                if ( !traverse)
                    writer.writeDouble((Double) obj );
                break;
            case Boolean :
                obj = readBoolean();
                if ( ! traverse);
                    writer.writeBoolean( (Boolean) obj );
                break;
            case Object:
                obj = readObject(type);
                if ( ! traverse)
                    writer.writeObject( obj);
                break;
            case Null :
                obj = 'N';
                skipDelimiter();
                if ( !traverse)
                    writer.writeNull();
                break;
            case Array :
                obj = readArray(type);
                //if ( !writeFieldName)
                //write array
                break;
            default :
                throw new RuntimeException("fail read object for "+type);
        }
        return obj;
    }

    int readInt() {
        StringBuilder sb = new StringBuilder();
        char c;
        while (true) {
            c = read();
            if ( c == COLON || c == SEMI)
                break;
            else
                sb.append(c);
        }
        return Integer.valueOf( sb.toString());
    }

    double readDouble() {
        StringBuilder sb = new StringBuilder();
        char c;
        while (true) {
            c = read();
            if ( c == COLON || c == SEMI)
                break;
            else
                sb.append(c);
        }
        return Double.valueOf( sb.toString());
    }

    boolean readBoolean() {
        char c = read();
        if ( c == '1' || c == 't' || c == 'T')
            return true;
        else
            return false;
    }

    void skipDelimiter() {
        char c ;
        while (true) {
            c = read();
            if ( c == COLON || c == SEMI)
                break;
        }
    }


    public String readString() {
        if ( read()  != QUOTE )
            throw new RuntimeException("not begin of string "+stream[current -1]);
        else {
            char prev = QUOTE;
            StringBuilder sb = new StringBuilder();
            while (true) {
                char nextChar = read();
                if ( prev != SLASH && nextChar == QUOTE )
                    return sb.toString();
                else
                    if ( nextChar != SLASH )
                        sb.append(nextChar);
                // assign prev
                prev = nextChar;
            }
        }
    }

    public String nextToken() {
        //skipDelimiter();
        if (EOF()  ) return null;
        StringBuilder sb = new StringBuilder();
        char nextChar;
        while(  (nextChar = read()) != COLON && nextChar != SEMI ) {
            sb.append( nextChar);
        }
        return sb.toString();
    }

    public char read() {
        if (EOF() ) return (char) -1;
        else return  (char) stream[current++] ;
    }


    PhpDataType peekTokenType() {
        if (EOF() ) return null;
        else {
            char nextChar = read() ;
            switch (nextChar) {
                case 'O' : return PhpDataType.Object;
                case 's' : return PhpDataType.String;
                case 'i' : return PhpDataType.Int;
                case 'd' : return PhpDataType.Double;
                case 'b' : return PhpDataType.Boolean;
                case 'N' : return PhpDataType.Null;
                case 'R' : return PhpDataType.Resource;
                case 'a' : return PhpDataType.Array;
                default: throw new RuntimeException("invalid token type "+nextChar);
            }
        }
    }

    boolean EOF() {
        if ( current == stream.length ) return true;
        else return false;
    }

}
