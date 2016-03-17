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

package com.sm.query;

import com.sm.query.utils.QueryException;

public interface Predicate {
    public enum Operator { And, Or, In, Equal, NotEqual, Greater, GreaterEQ, Less, LessEQ, Range ;
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
            else throw new QueryException("unknown ops "+op);
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
}
