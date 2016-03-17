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

package com.sm.query;

import com.sm.query.utils.QueryUtils;

public class Result {
    public static enum Type { CHAR, CHARS, BYTE, BYTES, BOOLEAN, BOOLEANS, SHORT, SHORTS, INT, INTS, LONG, LONGS,
        FLOAT, FLOATS, DOUBLE, DOUBLES, STRING, OBJECT, MAP, ARRAY, LIST, HASHSET, NULL, NUMBER, SKIP, JOBJECT ;

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
