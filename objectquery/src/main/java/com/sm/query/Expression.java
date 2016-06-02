package com.sm.query;

import com.sm.query.utils.QueryUtils.Comparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhsieh on 7/3/15.
 */
public class Expression {

    Comparator comparator;
    List<String> operands;
    boolean isKey;
    boolean not =false;
    Result.Type type;
    boolean isNull;

    public Expression(Comparator comparator) {
        this(comparator, true);
    }

    public Expression(Comparator comparator, boolean isKey) {
        this(comparator, new ArrayList<String>(), isKey);
    }

    public Expression(Comparator comparator, List<String> operands) {
        this( comparator, operands, true);
    }

    public Expression(Comparator comparator, List<String> operands, boolean isKey) {
        this.comparator = comparator;
        this.operands = operands;
        this.isKey = isKey;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public List<String> getOperands() {
        return operands;
    }

    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public boolean isKey() {
        return isKey;
    }

    public boolean isNot() {
        return not;
    }

    public Result.Type getType() {
        return type;
    }

    public void setType(Result.Type type) {
        this.type = type;
    }

    public void setOperands(List<String> operands) {
        this.operands = operands;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean isNull) {
        this.isNull = isNull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expression)) return false;

        Expression that = (Expression) o;

        if (comparator != that.comparator) return false;
        if (operands != null ? !operands.equals(that.operands) : that.operands != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = comparator.hashCode();
        result = 31 * result + (operands != null ? operands.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "comparator=" + comparator +
                ", operands=" + operands +
                ", isKey=" + isKey +
                '}';
    }
}
