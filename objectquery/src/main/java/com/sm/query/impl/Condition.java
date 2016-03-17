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

package com.sm.query.impl;

import com.sm.query.Predicate;
import com.sm.query.Result;
import com.sm.query.utils.QueryException;

public class Condition implements Predicate {
    String value;
    Predicate left;
    Predicate right;
    boolean isAllTrue =false;


    public Condition(String value, Predicate left, Predicate right) {
        this.value = value;
        this.left = left;
        this.right = right;
        //check null
        if (value == null || left == null || right == null)
            throw new QueryException(" value , left and right must be not null") ;
        if ( Operator.getOperator(value) == null)
            throw new QueryException("wrong operator");
    }

    public boolean isAllTrue() {
        return isAllTrue;
    }

    public void setAllTrue(boolean isAllTrue) {
        this.isAllTrue = isAllTrue;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Predicate left() {
        return left;
    }

    @Override
    public Predicate right() {
        return right;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Operator getOperator() {
        return Operator.getOperator(value);
    }

    @Override
    public boolean isLogicalOperator() {
        return value.equals("and") || value.equals("or") ;
    }

    @Override
    public boolean isKey() {
        if ( left != null )
            return left.isKey();
        else
            return false;
    }

    @Override
    public Result.Type getType() {
        return left.getType();
    }

    @Override
    public String toString() {
        return "Condition{" +
                "value='" + value + '\'' +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
