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

package com.sm.utils;

public class Tuple <T> {
    private T[] values;
    private int size;

    public Tuple(T... values) {
        this.values = values;
        this.size = values.length ;
        for ( int i = 0; i < size ; i++)
            if ( values[i] == null ) throw new RuntimeException("Tuple values in pos "+ i+" can not be null");
    }


    public int getSize() {
        return size;
    }

    public T getValue(int i) {
        if ( i < size ) return values[i];
        else throw new RuntimeException(i+" index out of bound size "+size);
    }

    public T getValue(int i, Class<T> cls){
        return  (T) values[i] ;
    }

    public void setValue(int i, T value) {
        if ( i > size -1 ) throw new RuntimeException(i+" index out of bound size "+size);
        values[i] = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;
        if ( this.getSize() != tuple.getSize() ) return false;

        for (int i = 0 ; i < size ; i ++ ) {
            if ( values[i].getClass() !=  tuple.getValue( i).getClass()  || ! values[i].equals( tuple.getValue( i)) )
                return false;
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 17 ;
        for (T value : values) {
            if (value != null) {
                hash = hash * 37 + value.hashCode();
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0 ; i < size ; i++)
            sb.append((i+1)+"elems -> "+ values[i].toString() +" ");

        return sb.toString();
    }
}

