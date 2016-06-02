package com.sm.query;

import com.sm.query.impl.Condition;
import com.sm.query.impl.Terminal;
import com.sm.query.parser.PredicateBaseListener;
import com.sm.query.parser.PredicateLexer;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.QueryException;
import com.sm.query.utils.QueryUtils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.util.Stack;

/**
 * Created by mhsieh on 4/18/16.
 */
public class PredicateNodes extends PredicateBaseListener {
    private static final Log logger = LogFactory.getLog(PredicateNodes.class);

    private Stack<Result> valueStack = new Stack<Result>();
    private String queryStr;
    private Stack<String> idStack = new Stack<String>();
    private Stack<Predicate> predicateStack = new Stack<Predicate>();


    public void walkTree() {
        try {
            init();
            PredicateLexer lexer = new PredicateLexer(new ANTLRInputStream(new StringReader(queryStr)));
            CommonTokenStream token = new CommonTokenStream(lexer);
            PredicateParser parser = new PredicateParser(token);
            parser.setBuildParseTree(true);
            PredicateParser.ScriptContext tree = parser.script(); // parse
            ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
            parseTreeWalker.walk( this, tree);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    private void init() {
        if ( ! valueStack.isEmpty())
            valueStack.empty();
        if ( ! idStack.isEmpty())
            idStack = new Stack<String>();
        if ( ! predicateStack.isEmpty()) {
            predicateStack.empty();
        }
    }

    /**
     *
     * @param queryStr - predicate string
     * @return rate of estimation
     */
    public Predicate generatePredicate(String queryStr) {
        if ( queryStr == null || queryStr.length() == 0) {
            logger.info("queryStr is null or length = 0, return 1 as 100%") ;
            return null;
        }
        else {
            this.queryStr = queryStr;
            logger.info("query "+queryStr) ;
            walkTree();
            if ( predicateStack.size() > 0) {
                return predicateStack.peek();
            }
            else
                return null;
        }
    }


    @Override
    public void exitExpValue(PredicateParser.ExpValueContext ctx) {
        Result result;
        if ( ctx.value() instanceof PredicateParser.NumbersContext)
            result = new Result(Result.Type.NUMBER, ctx.getText());
        else if ( ctx.value() instanceof PredicateParser.StringsContext)
            result = new Result( Result.Type.STRING, ctx.getText().substring(1, ctx.getText().length() - 1));
        else if ( ctx.value() instanceof PredicateParser.BooleansContext)
            result = new Result(Result.Type.BOOLEAN, Boolean.valueOf(ctx.getText()));
        else if ( ctx.value() instanceof PredicateParser.NullsContext)
            result = new Result(null);
        else
            result = null;
        if ( result != null )
            valueStack.push(result);
        else
            throw new QueryException("result is not Number or String v= "+ctx.getText());
    }

    @Override
    public void exitExpr(@NotNull PredicateParser.ExprContext ctx) {
        //System.out.println(ctx.binaryOperator().getText());
        Result right = valueStack.pop();
        Result left  = valueStack.pop();
        Result temp =  QueryUtils.binaryOp(left, ctx.binaryOperator().getText(), right);
        valueStack.push( temp);
    }

    @Override
    public void exitComparison(PredicateParser.ComparisonContext ctx) {
        String op = ctx.comparisonOperator().getText();
        if ( valueStack.size() == 0) {
            logger.info("value stack is empty, comparison "+ctx.objectField().getText()+" "+op);
            return;
        }
        Result result = valueStack.pop();
        //add predicate impl
        Terminal left = new Terminal( ctx.objectField().getText(), result.getType());
        Terminal right ;
        if ( result.getType() == Result.Type.ARRAY)
            right = new Terminal( new String((byte[]) result.getValue()), result.getType());
        else
            right = new Terminal( result.toString(), result.getType());
        Condition condition = new Condition(op, left, right);
        predicateStack.push( condition);
    }

    @Override
    public void exitNormal(PredicateParser.NormalContext ctx) {
        if ( predicateStack.size() < 2)
            throw new QueryException("predicateStack size "+predicateStack.size()+ " < 2");
        Predicate right = predicateStack.pop();
        Predicate left  = predicateStack.pop();
        String op =  ctx.logicalOperator().getText();
        Predicate condition = new Condition( op, left, right);
        predicateStack.push( condition);
    }

    @Override public void exitNotPredicate(PredicateParser.NotPredicateContext ctx) {
        Predicate predicate = predicateStack.pop();
        ((Condition) predicate).setNotExist( true);
        predicateStack.push(predicate);
    }

}
