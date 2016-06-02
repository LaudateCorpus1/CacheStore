package com.sm.query;

import com.sm.query.Predicate.Operator;
import com.sm.query.Result.Type;
import com.sm.query.impl.Condition;
import com.sm.query.impl.Terminal;
import com.sm.query.parser.QueryBaseListener;
import com.sm.query.parser.QueryLexer;
import com.sm.query.parser.QueryParser;
import com.sm.query.utils.QueryException;
import com.sm.query.utils.QueryUtils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by mhsieh on 6/27/15.
 */
public class QueryListenerImpl extends QueryBaseListener {
    private static final Log logger = LogFactory.getLog(QueryBaseListener.class);

    private Stack<Result> valueStack = new Stack<Result>();
    private String queryStr;
    private Stack<Predicate> predicateStack = new Stack<Predicate>();
    public final String TAB = "\t";
    private boolean tableScan = false;
    public static final String RANGE ="[]";
    public static final String AND = "and";
    public static final String OR = "or" ;
    public static final String IN = "in";
    public static final String EQ = "=";



    public QueryListenerImpl(String queryStr) {
        this.queryStr = queryStr;
    }



    public void walkTree() {
        try {
            init();
            QueryLexer lexer = new QueryLexer(new ANTLRInputStream(new StringReader(queryStr)));
            CommonTokenStream token = new CommonTokenStream(lexer);
            QueryParser parser = new QueryParser(token);
            parser.setBuildParseTree(true);
            QueryParser.ScriptContext tree = parser.script(); // parse
            ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
            parseTreeWalker.walk( this, tree);
            //check for key# if size = 1
            checkPredicateStack();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    private void init() {
        if ( ! valueStack.isEmpty())
            valueStack.empty();
        if ( ! predicateStack.isEmpty())
            predicateStack = new Stack<Predicate>();
    }

    private void checkPredicateStack() {
        if ( predicateStack.size() == 1)  {
            Predicate predicate = predicateStack.peek();
            if ( predicate == null ||  ! predicate.isKey() ) {
                System.out.println("remove predicateStack size = 1  it is null or not key");
                predicateStack.pop();
                tableScan = true;
            }
        }
        else if ( predicateStack.size() == 0)
            tableScan =true;

    }

    public void walTree(QueryParser.ScriptContext tree)  {
        init();
        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        parseTreeWalker.walk( this, tree);
        checkPredicateStack();
    }


    @Override public void enterExpr(@NotNull QueryParser.ExprContext ctx) {
    }


    @Override public void exitExpr(@NotNull QueryParser.ExprContext ctx) {
        //System.out.println(ctx.binaryOperator().getText());
        Result right = valueStack.pop();
        Result left  = valueStack.pop();
        Result temp =  QueryUtils.binaryOp(left, ctx.binaryOperator().getText(), right);
        valueStack.push( temp);
    }


    @Override
    public void enterComparison(@NotNull QueryParser.ComparisonContext ctx) {
    }

    @Override
    public void exitComparison(@NotNull QueryParser.ComparisonContext ctx) {
        //System.out.println(ctx.comparisonOperator().getText());
        //boolean isKey = ctx.objectField().getText().equals(KEY) ? true : false;
        String op = ctx.comparisonOperator().getText().equals(EQ) ? IN : ctx.comparisonOperator().getText() ;
        if ( valueStack.size() == 0) {
            logger.info("value stack is empty, comparison "+ctx.objectField().getText()+" "+op);
            return;
        }
        Result result = valueStack.pop();
        //add predicate impl
        Terminal left = new Terminal( ctx.objectField().getText(), result.getType());
        Terminal right ;
        if ( result.getType() == Type.ARRAY)
           right = new Terminal( new String((byte[]) result.getValue()), result.getType());
        else
           right = new Terminal( result.toString(), result.getType());
        Condition condition = new Condition(op, left, right);
        predicateStack.push( condition);
        //System.out.println( ctx.objectField().getText()+" "+ctx.comparisonOperator().getText()+" "+ctx.expression().getText());
    }


    @Override public void enterExpValues(@NotNull QueryParser.ExpValuesContext ctx) { }

    @Override public void exitExpValues(@NotNull QueryParser.ExpValuesContext ctx) {
        //System.out.println(ctx.getText());
        Result result;
        if ( ctx.value() instanceof QueryParser.NumbersContext)
            result = new Result(Type.NUMBER, ctx.getText());
        else if ( ctx.value() instanceof QueryParser.StringsContext)
            result = new Result( Type.STRING, ctx.getText().substring(1, ctx.getText().length() - 1));
        else if ( ctx.value() instanceof QueryParser.BooleansContext)
            result = new Result(Type.BOOLEAN, Boolean.valueOf(ctx.getText()));
        else if ( ctx.value() instanceof QueryParser.NullsContext)
            result = new Result(null);
        else
            result = null;
        if ( result != null )
            valueStack.push(result);
        else
            throw new QueryException("result is not Number or String v= "+ctx.getText());
    }

    @Override public void exitStrToBytesFuncExpr(QueryParser.StrToBytesFuncExprContext ctx) {
        String str = ctx.STRING().getText().substring(1, ctx.STRING().getText().length() - 1);
        valueStack.push(new Result(str.getBytes()));
    }

    @Override public void exitFunctionExp(QueryParser.FunctionExpContext ctx) {
        String op = ctx.getChild(0).getText();
        if ( op.indexOf("strToBytes(") < 0)
            valueStack.push( new Result(op));
    }


    public Stack<Predicate> getPredicateStack() {
        return predicateStack;
    }

    public boolean isTableScan() {
        return tableScan;
    }

    @Override public void enterLogicPredicate(@NotNull QueryParser.LogicPredicateContext ctx) {
        //System.out.println( ctx.logicalOperator().getText() +" "+ctx.predicate().get(0).getText()+ " " + ctx.predicate().get(1).getText());
   }

    @Override public void exitLogicPredicate(@NotNull QueryParser.LogicPredicateContext ctx){
        if ( ctx.logicalOperator().getText().equals(OR)) {
            or();
        }
        else {  //and
            and();
        }
        //System.out.println(ctx.logicalOperator().getText());
    }

    private boolean checkSkip(Predicate predicate) {
        if ( predicate == null || ! predicate.isKey() || predicate.isAllTrue())
            return true;
        else
            return false;
    }

    private void or() {
        Predicate right = predicateStack.pop();
        if ( checkSkip(right)) {
            //check left
            Predicate left = predicateStack.pop();
            //push null, since it need to table scan
//            if ( ! checkSkip(left) )
//                predicateStack.push(left);
//            else
                predicateStack.push(null);
        }
        else {
            Predicate left = predicateStack.pop();
            if ( checkSkip(left) ) {
                //push null
                predicateStack.push( null);
                return;
            }
            else {
                if ( left.isLogicalOperator() && ! right.isLogicalOperator()) {
                    Predicate l = mergeOr( left.left(), right);
                    Predicate r = mergeOr( left.right(), right);
                    pushStack( l, r);
                }
                else if ( ! left.isLogicalOperator() &&  right.isLogicalOperator() ) {
                    Predicate l = mergeOr( left, right.left());
                    Predicate r = mergeOr( left, right.right());
                    pushStack(l, r);
                }
                else if ( left.isLogicalOperator() &&  right.isLogicalOperator() ) {
                    throw new QueryException("left and right both or l= "+left.toString()+" r="+right.toString()) ;
                }
                else {
                    Predicate predicate = mergeOr(left, right);
                    predicateStack.push(predicate);
                }
            }
        }
    }

    private void pushStack(Predicate left, Predicate right) {
        if ( left == null && right == null)
            predicateStack.push(null);
        else if ( left == null && right != null )
            predicateStack.push(right);
        else if ( left != null && right == null)
            predicateStack.push(left);
        else
            predicateStack.push( new Condition(OR , left, right));
    }

    private void and() {
        Predicate right = predicateStack.pop();
        if ( checkSkip(right) ) {
            Predicate left = predicateStack.pop();
            if ( ! checkSkip(left) )
                predicateStack.push(left);
            else
                predicateStack.push(null);
            return;
        }
        else {
            Predicate left = predicateStack.pop();
            if ( checkSkip(left) ) {
                //push right back
                predicateStack.push( right);
                return;
            }
            else {
                if ( left.isLogicalOperator() || right.isLogicalOperator()) {
                    predicateStack.push( new Condition(AND, left, right));
                }
                else {
                    Predicate predicate = combineAnd(left, right);
//                    if ( predicate == null)
//                        tableScan = false;
                    predicateStack.push(predicate);
                }
            }
        }
    }


    private boolean greater(Predicate left, Predicate right, boolean eq) {
        switch ( left.getType()) {
            case LONG:
            case INT:
            case NUMBER:
                long diff = Long.valueOf( left.getValue()) - Long.valueOf(right.getValue());
                if ( eq ) {
                    if ( diff >= 0 )
                        return true;
                    else
                        return false;
                }
                else {
                    if (diff > 0)
                        return true;
                    else
                        return false;
                }
            case STRING:
                if ( eq) {
                    if ( left.getValue().compareTo( right.getValue()) >=0 )
                        return true;
                    else
                        return false;
                }
                else {
                    if ( left.getValue().compareTo( right.getValue()) > 0 )
                        return true;
                    else
                        return false;
                }
            default:
                throw new QueryException("less wrong type "+left.getType().toString());
        }
    }

    private boolean less(Predicate left, Predicate right, boolean eq) {
        switch ( left.getType()) {
            case LONG:
            case INT:
            case NUMBER:
                long diff = Long.valueOf( left.getValue()) - Long.valueOf(right.getValue());
                if ( eq ) {
                    if ( diff <= 0 )
                        return true;
                    else
                        return false;
                }
                else {
                    if (diff < 0)
                        return true;
                    else
                        return false;
                }
            case STRING:
                if ( eq) {
                    if ( left.getValue().compareTo( right.getValue()) <=0 )
                        return true;
                    else
                        return false;
                }
                else {
                    if ( left.getValue().compareTo( right.getValue()) < 0 )
                        return true;
                    else
                        return false;
                }
            default:
                throw new QueryException("less wrong type "+left.getType().toString());
        }
    }

    private Predicate combineAnd(Predicate left, Predicate right){
        if ( left.getOperator() == right.getOperator() ) {
            if ( left.getOperator() == Operator.In || left.getOperator() == Operator.NotEqual ) {
                ArrayList<String> leftList = new ArrayList<String>(Arrays.asList(left.right().getValue().split(TAB)));
                ArrayList<String> rightList = new ArrayList<String>(Arrays.asList(right.right().getValue().split(TAB)));
                List<String> list = new ArrayList<String>();
                for (String each : rightList) {
                    if (inList(leftList, each))
                        list.add(each);
                }
                // no match, set tableScan to true
                if (list.size() == 0) {
                    tableScan = true;
                    return null;
                } else {
                    Terminal terminal = new Terminal(fromList(list), left.getType());
                    //return new Left predicate to be push to stack
                    return new Condition(left.getValue(), left.left(), terminal);
                }
            }
            else if ( left.getOperator() == Operator.Greater || left.getOperator() == Operator.GreaterEQ) {
                String rs = findMax( left.getType(), left.right().getValue(), right.right().getValue(), left.getOperator() );
                return new Condition( left.getValue(), left, new Terminal( rs, left.getType()) ) ;
            } else if ( left.getOperator() == Operator.Less || left.getOperator() == Operator.LessEQ) {
                String rs = findMin(left.getType(), left.right().getValue(), right.right().getValue(),left.getOperator() );
                return new Condition(left.getValue(), left, new Terminal(rs, left.getType()));
            } else {
                //@ToDo check out and, or later
                throw new QueryException("unknown operator "+left.getOperator().toString());
            }
        } else {
            String rs ;
            switch (left.getOperator()) {
                case In :
                    return findEqualRange4And( left.getType(), left, right, right.getOperator());
                case Greater:
                    switch ( right.getOperator()) {
                        case In:
                            return findEqualRange4And( left.getType(), right, left, left.getOperator());
                        case Less:
                            if ( less (left.right(), right.right(), false))
                                return new Condition(RANGE, left.right(), right.right());
                            else
                                return null;
                        case LessEQ:
                             if ( less (left.right(), right.right(), true))
                                 return new Condition(RANGE, left, right);
                              else
                                return null;
                        case GreaterEQ:
                              rs = findMax(left.getType(), left.getValue(), right.getValue(), right.getOperator());
                            return new Condition( left.getValue(), left.left(), new Terminal(rs, left.getType()) );
                            //@ToDo and or                                                                                      `
                    }
                case GreaterEQ:
                    switch ( right.getOperator()) {
                        case In:   //switch left and right; operator as well
                            return findEqualRange4And( left.getType(), right, left, left.getOperator());
                        case Less:
                        case LessEQ:
                            if ( less(left.right(), right.right(), true))
                                return new Condition( RANGE, left, right);
                            else
                                return null;
                        case Greater:
                            if ( less(left.right(), right.right(), true))
                                return new Condition( left.getValue(), left.left(), new Terminal(left.right().getValue(), left.getType()) );
                            else
                                return new Condition( left.getValue(), left.left(), new Terminal(right.right().getValue(), left.getType()) );

                    }
                case Less:
                    switch ( right.getOperator()) {
                        case In: //switch left and right; operator as well
                            return findEqualRange4And( left.getType(), right, left, left.getOperator());
                        case Greater:
                        case GreaterEQ:  //switch right to left, because enforce range left always less right
                            if ( greater(left.right(), right.right(), true))
                               return new Condition( RANGE, right, left);
                            else
                                return null;
                        case LessEQ:
                            if ( greater(left.right(), right.right(), false) )
                                return new Condition( left.getValue(), left.left(), new Terminal(left.right().getValue(), left.getType()) );
                            else
                                return new Condition( left.getValue(), left.left(), new Terminal(right.right().getValue(), left.getType()) );
                    }
                case LessEQ:
                    switch ( right.getOperator()) {
                        case In:
                            return findEqualRange4And( left.getType(), right, left, left.getOperator());
                        case Greater:
                        case GreaterEQ:
                            if ( greater(left.right(), right.right(), true))
                                return new Condition( RANGE, right, left);
                            else
                                return null;
                        case Less:
                            if ( greater(left.right(), right.right(), true) )
                                return new Condition( left.getValue(), left.left(), new Terminal(left.right().getValue(), left.getType()) );
                            else
                                return new Condition( left.getValue(), left.left(), new Terminal(right.right().getValue(), left.getType()) );
                    }
                case Range:
                    switch ( right.getOperator()) {
                        case In:
                            return findEqualRange4And( left.getType(), right, left, left.getOperator());
                        case Greater:
                        case GreaterEQ:
                            //compare < <= side
                            if ( greater(left.right().right(), right.right(), true))
                                return left;
                            else
                                return null;
                        case Less:
                        case LessEQ:
                            if ( greater(left.left().right(), right.right(), true))
                                return null;
                            else
                                return left;
                    }
                default:
                    //
            }
        }
        return null;
    }

    /**
     * mergeOr is or condition
     * @param left
     * @param right
     * @return
     */
    private Predicate mergeOr(Predicate left, Predicate right) {
        if ( left.getOperator() == right.getOperator() ) {
            if ( left.getOperator() == Operator.In || left.getOperator() == Operator.NotEqual )  {
                ArrayList<String> leftList = new ArrayList<String>(Arrays.asList(left.right().getValue().split(TAB)));
                ArrayList<String> rightList = new ArrayList<String>(Arrays.asList(right.right().getValue().split(TAB)));
                for ( String each : rightList) {
                    if ( ! inList(leftList, each))
                        leftList.add(each) ;
                }
                Terminal terminal = new Terminal( fromList(leftList), left.getType());
                //return new Left predicate to be push to stack
                return new Condition( left.getValue(), left.left(), terminal );
            } else if ( left.getOperator() == Operator.Greater || left.getOperator() == Operator.GreaterEQ) {
                String rs = findMin( left.getType(), left.right().getValue(), right.right().getValue(), left.getOperator() );
                return new Condition( left.getValue(), left, new Terminal( rs, left.getType()) ) ;
            } else if ( left.getOperator() == Operator.Less || left.getOperator() == Operator.LessEQ) {
                String rs = findMax(left.getType(), left.right().getValue(), right.right().getValue(),left.getOperator() );
                return new Condition(left.getValue(), left, new Terminal(rs, left.getType()));
            } else {
                //@ToDo check out and, or later
                throw new QueryException("unknown operator "+left.getOperator().toString());
            }
        } else {
            //check not EQ
            if ( left.getOperator() == Operator.NotEqual || right.getOperator() == Operator.NotEqual ) {
                tableScan = true;
                return null;
            }
            else {
                //String rs ;
                switch (left.getOperator()) {
                    case In:
                        return findEqualRange4Or(left.getType(), left, right, right.getOperator());
                    case Greater:
                        switch (right.getOperator()) {
                            case In:
                                return findEqualRange4Or(left.getType(), right, left, left.getOperator());
                            case Less:
                                if (less(left, right, false)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case LessEQ:
                                if (less(left.right(), right.right(), true)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case GreaterEQ:
                                if (less(left, right, false))
                                    return new Condition(left.getValue(), left.left(), right.right());
                                else
                                    return new Condition(right.getValue(), left.left(), left.right());
                            default:
                                return new Condition(OR, left, right);
                            //and or
                        }
                    case GreaterEQ:
                        switch (right.getOperator()) {
                            case In:
                                return findEqualRange4Or(left.getType(), right, left, left.getOperator());
                            case Less:
                                if (less(left.right(), right.right(), false)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case LessEQ:
                                if (less(left.right(), right.right(), true)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case Greater:
                                if (less(left.right(), right.right(), false))
                                    return left;
                                else
                                    return right;
                            default:
                                return new Condition(OR, left, right);

                        }
                    case Less:
                        switch (right.getOperator()) {
                            case In:
                                return findEqualRange4Or(left.getType(), right, left, left.getOperator());
                            case Greater:
                                if (greater(left.right(), right.right(), false)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case GreaterEQ:
                                if (greater(left.right(), right.right(), true)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case LessEQ:
                                if (greater(left.right(), right.right(), false))
                                    return left;
                                else
                                    return right;
                            default:
                                return new Condition(OR, left, right);
                        }
                    case LessEQ:
                        switch (right.getOperator()) {
                            case In:
                                return findEqualRange4Or(left.getType(), right, left, left.getOperator());
                            case Greater:
                                if (greater(left.right(), right.right(), false)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case GreaterEQ:
                                if (greater(left.right(), right.right(), true)) {
                                    Condition condition = new Condition(OR, left, right);
                                    condition.setAllTrue(true);
                                    return condition;
                                } else {
                                    return new Condition(RANGE, left, right);
                                }
                            case Less:
                                if (greater(left.right(), right.right(), false))
                                    return right;
                                else
                                    return left;
                            default:
                                return new Condition(OR, left, right);
                        }
                    case Range:
                        //switch left to right , recursive call
                        return mergeOr(right, left);
                    default:
                        //
                }
            }
        }
        return null;
    }

    private String fromList(List<String> list){
        StringBuffer sb = new StringBuffer();
        for ( String each : list) {
            if ( each != null)
                sb.append(each).append(TAB);
        }
        return sb.toString();
    }

    /**
     *
     * @param type
     * @param left is always in predicate
     * @param right is anything else
     * @param comparator
     * @return
     */
    private Predicate findEqualRange4Or(Type type, Predicate left, Predicate right, Operator comparator) {
        ArrayList<String> leftList = new ArrayList<String>(Arrays.asList(left.right().getValue().split(TAB)));
        ArrayList<String> toReturn = new ArrayList<String>();
        for (String each : leftList)  {
            if ( comparator == Operator.Range) {
                switch (type) {
                    case INT:
                    case INTS:
                    case LONG:
                    case LONGS:
                    case NUMBER:
                        if ( ! (Long.valueOf( each ) >= Long.valueOf(right.left().right().getValue()) &&
                                Long.valueOf( each) <= Long.valueOf( right.right().right().getValue() )))
                            toReturn.add( each);
                        break;
                    case STRING:
                        if ( ! (each.compareTo(right.left().right().getValue()) >=0 && each.compareTo( right.right().right().getValue()) <= 0))
                            toReturn.add( each);
                        break;
                    default:
                        throw new QueryException("findEqualRange4Or wrong type " + type);
                }

            }
            else {
                String rs = findEqualRange(type, each, right.right().getValue(), comparator);
                if (rs == null)
                    toReturn.add(each);
            }
        }
        if ( toReturn.size() == 0 )
            return right;
        else {
            String v =  fromList( toReturn);
            Condition lf = new Condition(IN, left.left(), new Terminal(v, left.getType()) );
            return new Condition(OR, lf, right);
        }
    }


    private Predicate findEqualRange4And(Type type, Predicate left, Predicate right, Operator comparator) {
        ArrayList<String> leftList = new ArrayList<String>(Arrays.asList(left.right().getValue().split(TAB)));
        ArrayList<String> toReturn = new ArrayList<String>();
        for (String each : leftList)  {
            if ( comparator == Operator.Range) {
                switch (type) {
                    case INT:
                    case LONG:
                    case NUMBER:
                        if ( Long.valueOf( each ) >= Long.valueOf(right.left().right().getValue()) &&
                                Long.valueOf( each) <= Long.valueOf(right.right().right().getValue()) )
                            toReturn.add( each);
                        break;
                    case STRING:
                        if (  each.compareTo(right.left().right().getValue()) >=0 && each.compareTo( right.right().right().getValue()) <= 0)
                            toReturn.add( each);
                        break;
                    default:
                        throw new QueryException("findMax wrong type " + type);
                }
            }
            else {
                String rs = findEqualRange(type, each, right.right().getValue(), comparator);
                if (rs != null)
                    toReturn.add(each);
            }
        }
        if ( toReturn.size() == 0 )
            return null;
        else {
            String v =  fromList( toReturn);
            return  new Condition(IN, left.left(), new Terminal(v, left.getType()) );
        }
    }



    private Predicate checkEQ(Type type, Predicate left, Predicate right, Operator comparator){
        ArrayList<String> leftList = new ArrayList<String>(Arrays.asList(left.right().getValue().split(TAB)));
        for ( String each : leftList) {
            each = findEqualRange(left.getType(), each, right.getValue(), comparator);
        }
        return checkNull(left, leftList);
    }

    private Predicate checkNull(Predicate predicate, List<String> list) {
        boolean empty = true;
        for ( String each : list) {
            if ( each != null)
                empty = false;
        }
        if (empty ) {
            String rs = fromList( list);
            Terminal terminal = new Terminal( rs, predicate.getType());
            return new Condition(predicate.getValue(), predicate.left() , terminal );
        }
        else {
            Terminal terminal = new Terminal( "@"+predicate.left().getValue()+"@", predicate.getType());
            //make sure isKey() return false; it will be skip
            return new Condition(predicate.getValue(), terminal , terminal  );
        }

    }

    private String findMax(Type type, String left, String right, Operator comparator) {
        switch (type) {
            case INT:
            case LONG:
            case NUMBER:
                if ( comparator == Operator.Greater)
                    if ( Long.valueOf(left ) > Long.valueOf( right))
                        return left;
                    else
                        return right;
                else
                    if ( Long.valueOf(left ) >= Long.valueOf( right))
                        return left;
                    else
                        return right;

            case STRING:
                if ( comparator == Operator.Greater)
                    if ( left.compareTo(right) > 0)
                        return left;
                    else
                        return right;
                else
                    if ( left.compareTo(right) >= 0)
                        return left;
                    else
                        return right;

            default:
                throw new QueryException("findMax wrong type "+type);
        }
    }

    private String findMin(Type type, String left, String right, Operator comparator) {
        switch (type) {
            case INT:
            case LONG:
            case NUMBER:
                if ( comparator == Operator.Less)
                    if ( Long.valueOf(left ) < Long.valueOf( right))
                        return left;
                    else
                        return right;
                else
                    if ( Long.valueOf(left ) <= Long.valueOf( right))
                        return left;
                    else
                        return right;

            case STRING:
                if ( comparator == Operator.Less)
                    if ( left.compareTo(right) < 0)
                        return left;
                    else
                        return right;
                else
                    if ( left.compareTo(right) <= 0)
                        return left;
                    else
                        return right;

            default:
                throw new QueryException("findMax wrong type "+type);
        }
    }

    private boolean findRange(Type type, String left, String right, boolean eq) {
        //left greater right less
        switch (type) {
            case INT:
            case LONG:
            case NUMBER:
                if (eq) {
                    if (Long.valueOf(left) > Long.valueOf(right))
                        return false;
                    else
                        return true;
                }
                else {
                    if (Long.valueOf(left) >= Long.valueOf(right))
                        return false;
                    else
                        return true;
                }
            case STRING:
                if (eq) {
                    if (left.compareTo(right) > 0)
                        return false;
                    else
                        return true;
                }
                else {
                    if (left.compareTo(right) >= 0)
                        return false;
                    else
                        return true;
                }
            default:
                throw new QueryException("findMax wrong type "+type);
        }
    }

    /**
     *
     * @param type
     * @param left must be from equal
     * @param right must be range   >, >=, <, <=
     * @param comparator
     * @return
     */
    private String findEqualRange(Type type, String left, String right, Operator comparator) {
        switch (type) {
            case INT:
            case INTS:
            case LONG:
            case LONGS:
            case NUMBER:
                switch ( comparator) {
                    case Greater:
                        if (Long.valueOf(left) > Long.valueOf(right))
                            return left;
                        else
                            return null;
                    case GreaterEQ:
                        if (Long.valueOf(left) >= Long.valueOf(right))
                            return left;
                        else
                            return null;
                    case Less:
                        if (Long.valueOf(left) < Long.valueOf(right))
                            return left;
                        else
                            return null;
                    case LessEQ:
                        if (Long.valueOf(left) <= Long.valueOf(right))
                            return left;
                        else
                            return null;
                    case NotEqual:
                        if (Long.valueOf(left) != Long.valueOf(right))
                            return left;
                        else
                            return null;
                    default:
                        throw new QueryException("findEqualRange wrong type " + comparator.toString());
                }

            case STRING:
                switch ( comparator) {
                    case Greater:
                        if (left.compareTo(right) > 0)
                            return left;
                        else
                            return null;

                    case GreaterEQ:
                        if (left.compareTo(right) >= 0)
                            return left;
                        else
                            return null;
                    case Less:
                        if (left.compareTo(right) < 0)
                            return left;
                        else
                            return null;
                    case LessEQ:
                        if (left.compareTo(right) <= 0)
                            return left;
                        else
                            return null;
                    case NotEqual:
                        if ( left.compareTo(right) != 0)
                            return left;
                        else
                            return null;

                    default:
                        throw new QueryException("findEqualRange wrong type " + comparator.toString());
                }

          default:
                throw new QueryException("findMax wrong type " + type);
        }
    }


    private boolean inList(List<String> list, String str) {
        for ( String each : list ) {
            if ( each.equals( str)) return true;
        }
        return false;
    }

    @Override public void enterInComp(@NotNull QueryParser.InCompContext ctx) {   }


    @Override public void exitInComp(@NotNull QueryParser.InCompContext ctx){
        Result result = null ;
        StringBuffer sb = new StringBuffer();
        for( QueryParser.ExpressionContext each : ctx.expression() ) {
            //expression = findExpression(IN);
            result = valueStack.pop();
            sb.append(result.toString()).append(TAB);
            //expression.getOperands().add( result.toString());
            //System.out.print( each.getText()+" ") ;
        }
        Terminal left = new Terminal( ctx.objectField().getText(), result.getType());
        Terminal right = new Terminal(sb.toString(), result.getType());
        Condition condition = new Condition(IN, left, right);
        predicateStack.push( condition);
        //System.out.println(IN);
    }


}
