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

package com.sm.store.server;
import com.sm.query.Predicate;
import com.sm.query.Result;
import com.sm.query.utils.QueryException;
import com.sm.store.server.RemoteScanStore;
import com.sm.store.server.RemoteStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;
import voldemort.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentNavigableMap;

import static com.sm.query.Predicate.Operator.GreaterEQ;



public class QueryIterator implements Iterator {
    private static final Log logger = LogFactory.getLog(QueryIterator.class);
    //Predicate condition;
    boolean tableScan;
    RemoteStore remoteStore;
    Stack<Predicate> predicateStack = new Stack<Predicate>() ;
    //Stack<Direction> directionStack = new Stack<Direction>();

    public QueryIterator(Stack<Predicate> predicateStack, boolean tableScan, RemoteStore remoteStore) {
        this.predicateStack = predicateStack;
        this.tableScan = tableScan;
        this.remoteStore = remoteStore;
        if ( remoteStore == null )
            throw new QueryException("remoteStore is null");
        init();
    }

    Iterator<Key> iterator;
    boolean end = false;
    Result.Type keyType;
    boolean sorted;

    private void init() {
        if ( remoteStore.getStore().getMap().keySet().isEmpty()) {
            logger.info("remoteStore is empty");
            end = true;
        }
        else {
            keyType = findKeyType();
            sorted = remoteStore instanceof RemoteScanStore ? true : false;
        }
        if ( predicateStack.isEmpty()) {
            if ( tableScan ) {
                iterator = remoteStore.getStore().getMap().keySet().iterator();
            }
            else {
                end = ! tableScan;
            }
        }
        else {
            if ( needTableScan() ) {
                logger.info("needTableScan true for "+remoteStore.getStoreName());
                tableScan = true;
                iterator = remoteStore.getStore().getMap().keySet().iterator();
            }
            else {
                //set tableScan to false
                tableScan = false;
                iterator = findIterator(predicateStack.pop());
            }
        }
    }

    private boolean needTableScan() {
        //check tableScan
        if ( tableScan) return true;
        else {
            if (!sorted) {
                Stack<Predicate> stack = (Stack<Predicate>) predicateStack.clone();
                while (!stack.empty()) {
                    if (traverse(stack.pop()))
                        return true;
                }
            }
            return false;
        }
    }

    private boolean traverse(Predicate predicate) {
        switch ( predicate.getOperator()) {
            case Or:
                if ( traverse( predicate.left() ) )
                    return true;
                if ( traverse( predicate.right()))
                    return true;
                return false;
            case In :
            case Equal:
                return false;
            default:
                return true;

        }
    }



    private Result.Type findKeyType() {
        Key key = remoteStore.getStore().getMap().keySet().iterator().next();
        switch (key.getType()) {
            case STRING :
                return Result.Type.STRING;
            case INT:
                return Result.Type.INT;
            case LONG:
                return Result.Type.LONG;
            case BYTEARY:
            case BARRAY:
                return Result.Type.ARRAY;
            default:
                return Result.Type.OBJECT;
        }

    }

    private Iterator<Key> findIterator(Predicate predicate) {
        if ( predicate != null ) {
            switch (predicate.getOperator()) {
                case Or :
                    predicateStack.push( predicate.right());
                    //if ( predicate.left().getOperator() == Predicate.Operator.Or) {
                    return findIterator( predicate.left());
                //}
                case In :
                    return buildInIterator( predicate.right().getValue());
                case Range :
                    return buildRangeIteration( predicate.left(), predicate.right() );
                case Greater:
                case Less:
                case GreaterEQ:
                case LessEQ:
                    return buildRest(predicate);
                case NotEqual:
                    tableScan = true;
                    return remoteStore.getStore().getMap().keySet().iterator();
                case Equal:
                    return buildInIterator( predicate.right().getValue());
                default:
                    throw new QueryException("wrong keyType of operator "+predicate.getOperator());
            }
        }
        else
            throw new QueryException("predicate is null");
    }

    @Override
    public boolean hasNext() {
        if ( end ) return false;
        if ( iterator.hasNext() ) return true;
        else {
            //check stack
            if ( predicateStack.empty())
                return false;
            else {
                if ( tableScan ) {
                    logger.info("tableScan "+tableScan+" predicateStack size "+predicateStack.size());
                    return false;
                }
                else {
                    iterator = findIterator(predicateStack.pop());
                    return iterator.hasNext();
                }
            }
        }
    }

    @Override
    public Pair<Key, Value> next() {
        Key key = iterator.next();
        Value value = remoteStore.get(key);
        return new Pair(key, value);
    }

    @Override
    public void remove() {

    }


    Iterator<Key> buildInIterator(String value) {
        String[] list = value.split("\t");
        if ( list.length == 0 ) throw new QueryException("invalidate value "+value);
        List<Key> listKey = new ArrayList<Key>();
        for (String each : list) {
            listKey.add( createKey(each, keyType));
        }
        return listKey.iterator();
    }

    private Key createKey(String each, Result.Type type) {
        switch (type) {
            case STRING :
                return Key.createKey(each);
            case INT:
            case INTS:
                return Key.createKey(Integer.valueOf(each));
            case LONG:
            case LONGS:
                return Key.createKey( Long.valueOf( each));
            default:
                throw new QueryException("create Key wrong keyType "+type);
        }
    }

    private Iterator<Key> buildRangeIteration(Predicate left, Predicate right) {
        if ( ! sorted ) {
            tableScan = true;
            return remoteStore.getStore().getMap().keySet().iterator();
        }
        else {
            boolean inclusive = (left.getOperator()== GreaterEQ || left.getOperator() ==  Predicate.Operator.LessEQ) ? true : false ;
            ConcurrentNavigableMap sortedStore = (ConcurrentNavigableMap) ((RemoteScanStore) remoteStore).getSortedStore().getMap();
            if ( left.getOperator() ==  Predicate.Operator.GreaterEQ || left.getOperator() == Predicate.Operator.Greater) {
                Key from =createKey(left.right().getValue(), keyType);
                Key to =createKey( right.right().getValue(), keyType);
                return sortedStore.subMap(from, inclusive, to, inclusive).keySet().iterator();
            }
            else  {
                Key from =createKey(right.right().getValue(), keyType);
                Key to =createKey( left.right().getValue(), keyType);
                return sortedStore.subMap(from, inclusive, to, inclusive).keySet().iterator();
            }
        }
    }


    private Iterator<Key> buildRest(Predicate predicate) {
        if ( ! sorted ) {
            tableScan = true;
            return remoteStore.getStore().getMap().keySet().iterator();
        }
        else {
            boolean inclusive = (predicate.getOperator()== GreaterEQ || predicate.getOperator() ==  Predicate.Operator.LessEQ) ? true : false ;
            ConcurrentNavigableMap sortedStore = (ConcurrentNavigableMap) ((RemoteScanStore) remoteStore).getSortedStore().getMap();
            if ( predicate.getOperator() ==  Predicate.Operator.GreaterEQ || predicate.getOperator() == Predicate.Operator.Greater) {
                Key from =createKey(predicate.right().getValue(), keyType);
                return sortedStore.tailMap(from,inclusive).keySet().iterator();
            }
            else  {
                Key from =createKey(predicate.right().getValue(), keyType);
                return sortedStore.headMap( from,inclusive).keySet().iterator();
            }
        }
    }

    public boolean isTableScan() {
        return tableScan;
    }

    public RemoteStore getRemoteStore() {
        return remoteStore;
    }

    public Stack<Predicate> getPredicateStack() {
        return predicateStack;
    }

    public Iterator<Key> getIterator() {
        return iterator;
    }

    public boolean isEnd() {
        return end;
    }

    public Result.Type getKeyType() {
        return keyType;
    }

    public boolean isSorted() {
        return sorted;
    }
}
