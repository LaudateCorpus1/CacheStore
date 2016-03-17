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
import com.sm.query.QueryVisitorImpl;
import com.sm.query.Result;

public class Terminal implements Predicate {
    String value;
    Result.Type type;

    public Terminal(String value,Result.Type type) {
        this.value = value;
        this.type = type;
    }


    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Predicate left() {
        return null;
    }

    @Override
    public Predicate right() {
        return null;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Operator getOperator() {
        return null;
    }

    @Override
    public boolean isLogicalOperator() {
        return false;
    }

    @Override
    public boolean isKey() {
        if ( value != null)
            return value.equals(QueryVisitorImpl.KEY_FIELD);
        else
            return false;
    }

    @Override
    public Result.Type getType() {
        return type;
    }

    @Override
    public boolean isAllTrue() {
        return false;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "value='" + getValue() == null ? "null": getValue() + '\'' +
                '}';
    }
}
