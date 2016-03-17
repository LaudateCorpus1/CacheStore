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

public class TupleThree<T1, T2, T3> {
    private T1 first;
    private T2 second;
    private T3 third;

    public TupleThree(T1 first, T2 second, T3 third) {
        validateNull(first, second, third);
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        validateNull( first);
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        validateNull( second);
        this.second = second;
    }

    public T3 getThird() {
        return third;
    }


    public void setThird(T3 third) {
        validateNull( third);
        this.third = third;
    }


    public static void validateNull(Object... objects) {
         if ( objects == null ) throw new RuntimeException("it is null") ;
         StringBuilder sb = new StringBuilder();
         for (int i=0; i < objects.length ; i++) {
             if ( objects[i] == null ) sb.append("i "+i+" is null ");
         }
         if (sb.length() > 0 ) throw new RuntimeException( sb.toString() );
     }


    @Override
    public String toString() {
        return "TupleThree{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TupleThree)) return false;

        TupleThree tupleTree = (TupleThree) o;

        if (first != null ? !first.equals(tupleTree.first) : tupleTree.first != null) return false;
        if (second != null ? !second.equals(tupleTree.second) : tupleTree.second != null) return false;
        if (third != null ? !third.equals(tupleTree.third) : tupleTree.third != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }
}
