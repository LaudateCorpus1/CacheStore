package com.sm.query;

import com.sm.query.parser.PredicateBaseVisitor;
import com.sm.query.parser.PredicateLexer;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.utils.Pair;

import java.io.StringReader;
import java.util.*;

import static com.sm.query.utils.QueryUtils.findObjectId;
import static com.sm.query.utils.QueryUtils.findSource;

/**
 * Created by mhsieh on 12/30/14.
 */
public class PredicateVisitorImpl extends PredicateBaseVisitor<Result> implements Filter {
    private static final Log logger = LogFactory.getLog(PredicateVisitorImpl.class);

    private Map<String, FieldInfo> idMap = new HashMap<String, FieldInfo>();
    private Map<String, ClassInfo> classInfoMap = new HashMap<String, ClassInfo>();
    private String queryStr;
    private Object[] source;
    private ParserRuleContext tree;
    private List<String> classNameList = new ArrayList<String>() ;

    public PredicateVisitorImpl(String queryStr) {
        this.queryStr = queryStr;
        init();
    }

//    public PredicateVisitorImpl(String queryStr, Object... source) {
//        this.queryStr = queryStr;
//        this.source = source;
//        init();
//    }

    private void init() {
        try {
            if ( queryStr == null || queryStr.length() == 0  ) return ;
            PredicateLexer lexer = new PredicateLexer(new ANTLRInputStream(new StringReader(queryStr)));
            CommonTokenStream token = new CommonTokenStream(lexer);
            PredicateParser parser = new PredicateParser(token);
            parser.setBuildParseTree(true);
            tree = parser.script();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    public boolean runPredicate(Object... source) {
        try {
            if ( source == null || queryStr == null ||  queryStr.length() == 0) {
                //logger.info("source is null or queryStr is empty");
                return true;
            }
            //assign source, class name is null that represent source[i] is null
            this.source = source;
            classNameList.clear();
            for ( Object each : source) {
                if ( each == null)
                    classNameList.add(null);
                else
                    classNameList.add( each.getClass().getSimpleName());
            }
            Result result = visit( tree);
            return (Boolean ) result.getValue() ;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
        init();
    }

    @Override
    public Result visitBooleans(@NotNull PredicateParser.BooleansContext ctx) {
        boolean value = Boolean.valueOf( ctx.getText());
        return new Result( value);
    }

    @Override
    public Result visitParenPredicate(@NotNull PredicateParser.ParenPredicateContext ctx) {
        return visit( ctx.predicate());
        //return visitChildren( ctx);
    }

    @Override
    public Result visitScript(@NotNull PredicateParser.ScriptContext ctx) {

        if ( ctx.getChildCount() == 1 )  //empty query, just EOF
            return new Result(true);
        else
            return visit(ctx.predicate());

    }

    @Override
    public Result visitNotPredicate(@NotNull PredicateParser.NotPredicateContext ctx) {
        return new Result( !  (Boolean) visit(ctx.predicate()).getValue() );
    }


    @Override public Result visitExistExpr(PredicateParser.ExistExprContext ctx) {
        Result left = visit(ctx.objectField(0));
        Collection collection;
        if ( left.getValue() instanceof Collection ) {
             collection = ((Collection) left.getValue());
            if ( ctx.objectField().size() > 1)
                collection =  QueryUtils.collectObjectField( ctx.objectField(1).getText(), collection, idMap, classInfoMap);
        }
        else
            throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
        //Result right = visit( ctx.expression());
        return new Result(QueryUtils.existInIt(collection,  visit( ctx.expression())) );
    }

    @Override public Result visitExistListOr(PredicateParser.ExistListOrContext ctx) {
        Result left = visit(ctx.objectField(0));
        Collection collection;
        for ( int i = 0; i < ctx.expression().size() ; i++) {
            //Iterator iterator;
            if ( left.getValue() instanceof Collection ) {
                collection = ((Collection) left.getValue());
                if ( ctx.objectField().size() > 1)
                    collection =  QueryUtils.collectObjectField( ctx.objectField(1).getText(), collection, idMap, classInfoMap);
            }
            else
                throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
            //if one match, exit with true
            if( QueryUtils.existInIt(collection, visit(ctx.expression(i)) ) )
                return new Result(true);
        }
        // all false
        return new Result(false);

    }

    @Override public Result visitExistListAnd(PredicateParser.ExistListAndContext ctx) {
        Result left = visit(ctx.objectField(0));
        Collection collection;
        for ( int i = 0; i < ctx.expression().size() ; i++) {
            if ( left.getValue() instanceof Collection ) {
                collection = ((Collection) left.getValue());
                if ( ctx.objectField().size() > 1)
                    collection =  QueryUtils.collectObjectField( ctx.objectField(1).getText(), collection, idMap, classInfoMap);
            }
            else
                throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
            //for and, if one did not match, exit with false
            if( ! QueryUtils.existInIt(collection, visit(ctx.expression(i)) ) )
                return new Result(false);
        }
        // all true
        return new Result(true);

    }

    @Override public Result visitCountExpr(PredicateParser.CountExprContext ctx) {
        Result left = visit(ctx.objectField(0));
        Iterator iterator;
        if ( left.getValue() instanceof Collection ) {
            Collection collection = ((Collection) left.getValue());
            if ( ctx.objectField().size() > 1)
                iterator = QueryUtils.collectObjectField( ctx.objectField(1).getText(), collection, idMap, classInfoMap).iterator();
            else
                iterator = collection.iterator();
        }
        else
            throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
        Result right = visit(ctx.expression(0));
        int count = 0;
        while ( iterator.hasNext()) {
            Result lt = null ;
            Object obj = iterator.next();
            if ( obj instanceof Collection) {
                Iterator inside = ((Collection) obj).iterator();
                while (inside.hasNext()) {
                    lt = new Result( inside.next());
                    if (QueryUtils.compare( lt, "=", right) )
                        break;
                }
            }
            else
                lt = new Result( obj);
            if ( QueryUtils.compare( lt, "=", right) ) {
                count ++;
            }
        }
        String op = ctx.comparisonOperator().getText();
        int c = Integer.valueOf( (String) visit(ctx.expression(1)).getValue());
        if ( QueryUtils.compare( new Result(count), op, new Result(c)) )
            return new Result(true );
        else
            return new Result(false );
    }

    @Override public Result visitLowerExpr(PredicateParser.LowerExprContext ctx) {
        String left =  ((String) visit(ctx.objectField()).getValue()).toLowerCase();
        String right = (String) visit(ctx.expression()).getValue();
        if ( ctx.comparisonOperator().getText().equals("="))
            return new Result( left.equals( right));
        else
            return new Result( ! left.equals( right));


    }

    @Override public Result visitUpperExpr(PredicateParser.UpperExprContext ctx) {
        String left =  ((String) visit(ctx.objectField()).getValue()).toUpperCase();
        String right = (String) visit(ctx.expression()).getValue();
        if ( ctx.comparisonOperator().getText().equals("="))
            return new Result( left.equals( right));
        else
            return new Result( ! left.equals( right));
    }

    @Override public Result visitSubstrExpr(PredicateParser.SubstrExprContext ctx) {
        String left =  ((String) visit(ctx.objectField()).getValue());
        int index = Integer.valueOf( (String) visit(ctx.expression(0)).getValue()) ;
        String right = (String) visit(ctx.expression(1)).getValue();
        if ( ctx.comparisonOperator().getText().equals("="))
            return new Result(left.substring( index).equals( right));
        else
            return new Result( ! left.substring( index).equals( right));

    }

    @Override
    public Result visitInComp(@NotNull PredicateParser.InCompContext ctx) {
        Result objectId = visit( ctx.objectField());
        List<Result> listExpr = new ArrayList<Result>();
        for ( PredicateParser.ExpressionContext each : ctx.expression())
            listExpr.add( visit(each));
        boolean result = QueryUtils.isInList( objectId, listExpr);
        return new Result( result);
    }

    @Override
    public Result visitComparison(@NotNull PredicateParser.ComparisonContext ctx) {
        Result left = visit( ctx.objectField());
        Result right = visit(ctx.expression());
        String operator = ctx.comparisonOperator().getText();
        boolean result = QueryUtils.compare( left, operator, right);
        return new Result(result);

    }


    @Override
    public Result visitUnaryOperator(@NotNull PredicateParser.UnaryOperatorContext ctx) {
        return null;
    }

    @Override
    public Result visitStrings(@NotNull PredicateParser.StringsContext ctx) {
        String str = ctx.getText().substring(1, ctx.getText().length() - 1);
        return new Result( str);
    }

    @Override
    public Result visitLogicalOperator(@NotNull PredicateParser.LogicalOperatorContext ctx) {
        return null;
    }

    @Override
    public Result visitNormal(@NotNull PredicateParser.NormalContext ctx) {
        if ( ctx.predicate().size() == 1 ) return  visit(ctx.predicate(0));
        else {
            Result left = visit(ctx.predicate(0));
            // implement short cut to skip right side for left is false and AND return false
            if (ctx.logicalOperator().getText().equals("and") && ! (Boolean) left.getValue() )
                return left;
            else if (ctx.logicalOperator().getText().equals("or") && (Boolean) left.getValue() )
                return left;
            //no short cut case, run through full evaluation
            Result right = visit(ctx.predicate(1));
            if (ctx.logicalOperator().getText().equals("and")) {
                return new Result((Boolean) left.getValue() && (Boolean) right.getValue());
            } else {
                return new Result((Boolean) left.getValue() || (Boolean) right.getValue());
            }
        }
    }



    @Override
    public Result visitObjectField(@NotNull PredicateParser.ObjectFieldContext ctx)  {
        //find the right object from collection
        Pair<Object, String> sourcePair = findSource( ctx.getText(), source, classNameList);
        //if source null that return null without traverse
        if ( sourcePair.getFirst() == null)
            return new Result(null);
        //first is object, second is field
        Pair<Object, FieldInfo> pair = findObjectId(sourcePair.getSecond(), sourcePair.getFirst(), classInfoMap);
        idMap.put(ctx.getText(), pair.getSecond());
        try {
            if ( pair.getFirst() == null) return new Result(null);
            else {
                Object object = pair.getSecond().getField().get(pair.getFirst());
                return new Result(object);
            }
        } catch (IllegalAccessException e) {
            throw new ObjectIdException( e.getMessage(), e);
        }
    }


    @Override
    public Result visitExpParen(@NotNull PredicateParser.ExpParenContext ctx) {
        return visit( ctx.expression());
    }

//    @Override
//    public Result visitComparisonOperator(@NotNull PredicateParser.ComparisonOperatorContext ctx) {
//        return null;
//    }

    @Override
    public Result visitNumbers(@NotNull PredicateParser.NumbersContext ctx) {
        return new Result( Result.Type.NUMBER, ctx.getText());
    }

    @Override
    public Result visitExpr(@NotNull PredicateParser.ExprContext ctx) {
        Result left = visit(ctx.expression(0));
        Result right = visit( ctx.expression(1));
        String binaryOperator = ctx.binaryOperator().getText();
        return QueryUtils.binaryOp( left, binaryOperator, right);
   }

    @Override
    public Result visitCheckNull(@NotNull PredicateParser.CheckNullContext ctx) {
        Result result = visit( ctx.objectField());
        if ( result.getType() == Result.Type.NULL )
            return new Result( true);
        else
            return new Result( false);
    }

}
