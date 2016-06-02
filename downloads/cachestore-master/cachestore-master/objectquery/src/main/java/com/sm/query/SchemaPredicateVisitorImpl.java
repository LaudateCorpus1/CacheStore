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
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.sm.query.utils.QueryUtils.*;

/**
 * Created by mhsieh on 3/7/16.
 */
public class SchemaPredicateVisitorImpl extends PredicateBaseVisitor<Result> implements Filter {
    private static final Log logger = LogFactory.getLog(PredicateVisitorImpl.class);

    private Map<String, FieldInfo> idMap = new HashMap<String, FieldInfo>();
    private Map<String, ClassInfo> classInfoMap = new HashMap<String, ClassInfo>();
    private String queryStr;
    private byte[][] source;
    private ParserRuleContext tree;
    private Map<String, Column> columnMap ;
    public final static String SOURCE_FIELD = "this";
    public final static String AND = "and";
    public final static String OR = "or";
    private List<String> classNameList = new ArrayList<String>() ;

    public SchemaPredicateVisitorImpl(String queryStr, Map<String, Column> columnMap) {
        this.queryStr = queryStr;
        this.columnMap = columnMap;
        init();
    }

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
                return true;
            }
            //assign source
            this.source = new byte[source.length][];
            classNameList.clear();
            for ( int i = 0; i < source.length ; i++) {
                if ( source[i] == null) {
                    this.source[i] = null;
                    classNameList.add(null);
                }
                else {
                    this.source[i] = (byte[]) source[i];
                    char[] c1 = {(char) ((int) 'A' + i)};
                    classNameList.add(new String(c1));
                }
            }
            //ParseTreeWalker walker = new ParseTreeWalker();
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
                collection =  QueryUtils.collectObjectField(ctx.objectField(1).getText(), collection, idMap, classInfoMap);
        }
        else
            throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
        //Result right = visit( ctx.expression());
        return new Result(QueryUtils.existInIt(collection,  visit( ctx.expression())) );
    }

    @Override public Result visitBitsOr(PredicateParser.BitsOrContext ctx) {
        Result result = visitObjectField( ctx.objectField());
        byte[] bytes = (byte[]) result.getValue();
        for ( int i = 0; i < ctx.expression().size() ; i++) {
            int v = (int) convertLong(visit(ctx.expression(i))) ;
            if ( checkFlag(bytes, v))
               return new Result(true);
        }
        return new Result(false);
    }

    @Override public Result visitBitsAnd(PredicateParser.BitsAndContext ctx) {
        Result result = visitObjectField( ctx.objectField());
        byte[] bytes = (byte[]) result.getValue();

        for ( int i = 0; i < ctx.expression().size() ; i++) {
            int v = (int) convertLong(visit(ctx.expression(i))) ;
            if ( ! checkFlag(bytes, v))
                return new Result(false);
        }
        return new Result(true);
    }

    private Pair<byte[], String> getSource(String text) {
        String[] ary = text.split(DOT4R);
        if (classNameList.size() == 1) {
            if ( ary.length > 1)
                return new Pair(source[0], getRestOf(ary));
            else
                return new Pair(source[0], text);
        }
        else {
            char ch = ary[0].charAt(0) ;
            for ( int i = 0; i < classNameList.size() ; i++) {
                if ( classNameList.get(i) == null )
                    return new Pair(null, text );
                else {
                    if ( classNameList.get(i).charAt(0) == ch) {
                        return new Pair(source[i], getRestOf(ary));
                    }
                }
            }
            throw new QueryException("alias not find for "+text);
        }
    }

    @Override
    public Result visitObjectField(@NotNull PredicateParser.ObjectFieldContext ctx)  {
        //find the right object from collection
        final String objectFieldText = ctx.getText();
        Pair<byte[], String> sourcePair = getSource(objectFieldText);
        if ( sourcePair.getFirst() == null) {
            return new Result(null);
        }
        else {
            String text = sourcePair.getSecond();
            if ( text.equals(SOURCE_FIELD))
                return new Result( sourcePair.getFirst());
            else {
                Column column = columnMap.get(objectFieldText);
                if ( column == null ) throw new RuntimeException("can not find column "+ objectFieldText);
                if (sourcePair.getFirst().length < column.getOffset() + column.getLen()  )
                    throw new RuntimeException("source.length "+ source.length+" < " + (column.getOffset() + column.getLen()) );
                return convert( column, sourcePair.getFirst());
            }
        }
    }
    /**
     *
     * @param bytes is 0 base array
     * @param pos is 1 base, start with 1 ...
     * @return
     */
    boolean checkFlag(byte[] bytes, int pos) {
        // since pos is 1-offset and array access is 0-offset
        int arrayOffset = pos - 1;
        int i = arrayOffset / 8 ;   //index position
        int off = arrayOffset % 8 ;  //offset bit location
        if ( i >= bytes.length )
            throw new QueryException("source index out of bound len "+source.length+ " index "+i +" pos "+pos );
        byte b = bytes[i];
        return (b & ( 0x01 << off )) > 0 ;

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
            if (ctx.logicalOperator().getText().equals(AND) && ! (Boolean) left.getValue() )
                return left;
            else if (ctx.logicalOperator().getText().equals(OR) && (Boolean) left.getValue() )
                return left;
            //no short cut case, run through full evaluation
            Result right = visit(ctx.predicate(1));
            if (ctx.logicalOperator().getText().equals(AND)) {
                return new Result((Boolean) left.getValue() && (Boolean) right.getValue());
            } else {
                return new Result((Boolean) left.getValue() || (Boolean) right.getValue());
            }
        }
    }


    private Result convert(Column column, byte[] sourceBytes) {
        switch (column.getType()) {
            case INT:
                int i = getInt(sourceBytes, column.getOffset());
                return new Result(i);
            case BYTE:
                int b = sourceBytes[column.getOffset()] & 0xFF;
                return new Result(b);
            case SHORT:
                short s = getShort(sourceBytes, column.getOffset());
                return new Result( s);
            case LONG:
                long l = getLong(sourceBytes, column.getOffset());
                return new Result( l);
            case DOUBLE:
                double d = getDouble(sourceBytes, column.getOffset());
                return new Result( d);
            case FLOAT:
                float f = getFloat(sourceBytes, column.getOffset());
                return new Result( f);
            case BOOLEAN:
                boolean bl = getBoolean(sourceBytes, column.getOffset());
                return new Result(bl);
            case STRING:
                try {
                    String str = new String(sourceBytes, column.getOffset() , column.getLen(), "UTF-8");
                    return new Result(str);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException( e.getMessage(), e);
                }
            default:
                throw new RuntimeException("unsupported type "+column.getType());
        }
    }

    //@Override
//    public Result oldVisitObjectField(@NotNull PredicateParser.ObjectFieldContext ctx)  {
//        if ( ctx.getText().equals(SOURCE_FIELD))
//            return new Result( source);
//        else {
//            Column column = columnMap.get(ctx.getText());
//            if ( column == null ) throw new RuntimeException("can not find column "+ctx.getText());
//            if ( source.length < column.getOffset() + column.getLen()  )
//                throw new RuntimeException("source.length "+ source.length+" < " + (column.getOffset() + column.getLen()) );
//            return convert( column);
//        }
//    }

//    private byte[] findSource(String text) {
//        String[] ary = text.split(DOT4R);
//        if ( source.length > 1) {
//            if ( ary.length > 1 && ary[0].length() == 1) {
//                int offset = ((int) ary[0].charAt(0)) - (int) 'A';
//                if ( offset >= 0 && offset < source.length) {
//                    return source[offset];
//                }
//                else
//                    throw new QueryException("wrong alias for "+text);
//            }
//            else
//                throw new QueryException("wrong length "+ source.length+" and alias for "+text);
//        }
//        else
//            return source[0];
//
//    }

    @Override
    public Result visitExpParen(@NotNull PredicateParser.ExpParenContext ctx) {
        return visit( ctx.expression());
    }

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
