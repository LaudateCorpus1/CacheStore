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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TupleUtils {
    public static void validateNull(Object... objects) {
        if ( objects == null ) throw new RuntimeException("it is null") ;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < objects.length ; i++) {
            if ( objects[i] == null ) sb.append("i "+i+" is null ");
        }
        if (sb.length() > 0 ) throw new RuntimeException( sb.toString() );
    }

    public static  Pair<Integer, Double>[] aggreate(Pair<Integer, Double>[] target, Pair<Integer, Double>[] source) {
        List<Pair<Integer, Double> > list = new ArrayList<Pair <Integer, Double>>();
        for (Pair<Integer, Double> each : source) {
            boolean find = false ;
            for ( Pair<Integer, Double> item : target) {
                if ( item.getFirst() == each.getFirst() ) {
                    find = true;
                    item.setSecond(item.getSecond() + each.getSecond());
                    break;
                }
            }
            // expand array
            if ( find == false ) {
                list.add( each);
            }
        } // source
        if ( list.size() == 0 ) return target;
        else {
            //overflow create a new array
            int size = target.length + list.size();
            Pair<Integer, Double>[] toReturn = new Pair[size];
            System.arraycopy(target, 0, toReturn, 0, target.length);
            // copy from list
            int j = 0;
            for ( int i = target.length ; i < size ; i ++)
                toReturn[ i] = list.get(j++);

            return toReturn;
        }
    }




    public static  void aggreate(List<Pair> target, List<Pair> source) {
        List<Pair<Integer, Double> > list = new ArrayList<Pair <Integer, Double>>();
        for (Pair<Integer, Double> each : source) {
            boolean find = false ;
            for ( Pair<Integer, Double> item : target) {
                if ( item.getFirst() == each.getFirst() ) {
                    find = true;
                    item.setSecond(item.getSecond() + each.getSecond());
                    break;
                }
            }
            // expand array
            if ( find == false ) {
                target.add( each);
            }
        } // source
    }

    public static  void aggreate(Map<Integer, Double> target, Map<Integer, Double> source) {
        Iterator<Map.Entry<Integer, Double>> it = source.entrySet().iterator();
        while ( it.hasNext()) {
            Map.Entry<Integer, Double> entry = it.next();
            if ( target.containsKey( entry.getKey() )) {
                double value = target.get( entry.getKey() ) + entry.getValue();
                target.put( entry.getKey(), value);
            }
            else {
                target.put( entry.getKey(), entry.getValue());
            }
        } // source
    }


}
