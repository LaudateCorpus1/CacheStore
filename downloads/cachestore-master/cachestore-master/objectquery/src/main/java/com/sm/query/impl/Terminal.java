package com.sm.query.impl;

import com.sm.query.Predicate;
import com.sm.query.QueryVisitorImpl;
import com.sm.query.Result;

/**
 * Created by mhsieh on 7/18/15.
 */
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
    public boolean isNotExist() {
        return false;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "value='" + getValue() == null ? "null": getValue() +
                "}";
    }
}
