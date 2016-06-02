package com.sm.query.impl;

import com.sm.query.Predicate;
import com.sm.query.Result;
import com.sm.query.utils.QueryException;

/**
 * Created by mhsieh on 7/18/15.
 */
public class Condition implements Predicate {
    String value;
    Predicate left;
    Predicate right;
    boolean isAllTrue =false;
    boolean notExist = false;


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

    @Override
    public boolean isNotExist() {
        return notExist;
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

    public void setNotExist(boolean notExist) {
        this.notExist = notExist;
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
                ", isAllTrue=" + isAllTrue +
                ", notExist=" + notExist +
                '}';
    }
}
