package com.sm.query;

/**
 * Created by mhsieh on 7/18/15.
 */
public interface Predicate {
    public enum Operator { And, Or, In, Equal, NotEqual, Greater, GreaterEQ, Less, LessEQ, Range, Function ;
        public static Operator getOperator(String op ) {
            if ( op.equals("and")) return And;
            else if ( op.equals("or")) return Or;
            else if ( op.equals("in")) return In;
            else if ( op.equals("=")) return Equal;
            else if ( op.equals("!=")) return NotEqual;
            else if ( op.equals(">")) return Greater;
            else if ( op.equals("<")) return Less;
            else if ( op.equals(">=")) return GreaterEQ;
            else if ( op.equals("<=")) return LessEQ;
            else if ( op.equals("[]")) return Range;
            else return Function ;
            //else throw new QueryException("unknown ops "+op);
        }
    }

    boolean isLeaf();
    Predicate left();
    Predicate right();
    String getValue();
    Operator getOperator();
    boolean isLogicalOperator();
    boolean isKey();
    Result.Type getType();
    boolean isAllTrue();
    boolean isNotExist();
}
