package com.sm.query;

import com.sm.query.utils.QueryUtils;

/**
 * Created by mhsieh on 12/29/14.
 */
public class Result {
    public static enum Type { CHAR, CHARS, BYTE, BYTES, BOOLEAN, BOOLEANS, SHORT, SHORTS, INT, INTS, LONG, LONGS,
        FLOAT, FLOATS, DOUBLE, DOUBLES, STRING, OBJECT, MAP, ARRAY, LIST, HASHSET, NULL, NUMBER, SKIP, JOBJECT, BARRAY ;

    }

    Type type;
    Object value;

    public Result(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Result(Object value) {
        this.value = value;
        init();
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    private void init(){
        if ( value == null)
            type = Type.NULL;
        else {
            type = QueryUtils.getType(value.getClass().getName());
        }
    }



    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if ( obj instanceof Result ){
            return value.equals(((Result) obj).getValue());
        }
        else
            return false;
    }

    @Override
    public String toString() {
        return  value == null ? null: value.toString();
    }

}
