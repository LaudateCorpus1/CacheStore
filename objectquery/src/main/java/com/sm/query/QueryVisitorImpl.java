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

import com.sm.query.parser.QueryBaseVisitor;
import com.sm.query.parser.QueryLexer;
import com.sm.query.parser.QueryParser;
import com.sm.query.utils.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.io.StringReader;
import java.util.*;

import static com.sm.query.utils.QueryUtils.*;

public class QueryVisitorImpl extends QueryBaseVisitor<Result> {
    private static final Log logger = LogFactory.getLog(PredicateVisitorImpl.class);

    public enum StatementType { None, Select, Update, Insert }
    private Map<String, FieldInfo> idMap = new HashMap<String, FieldInfo>();
    private Stack<Object> idStack = new Stack<Object>();
    private Map<String, ClassInfo> classInfoMap = new HashMap<String, ClassInfo>();
    private String queryStr;
    private Object source;
    private Object selectObj;
    private ParserRuleContext tree;
    //private boolean keyPredicate =false;
    private Key key;
    public static String KEY_FIELD = "key#";
    public static String ALL = "*";
    private StatementType statementType = StatementType.None ;

    public QueryVisitorImpl(String queryStr, Object source) {
        this.queryStr = queryStr;
        this.source = source;
        init();
    }

    public QueryVisitorImpl(String queryStr) {
        this.queryStr = queryStr;
        init();
    }

    private void init() {
        try {
            QueryLexer lexer = new QueryLexer(new ANTLRInputStream(new StringReader(queryStr)));
            CommonTokenStream token = new CommonTokenStream(lexer);
            QueryParser parser = new QueryParser(token);
            parser.setBuildParseTree(true);
            tree = parser.script();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Object getSelectObj() {
        return selectObj;
    }

    public Object getSource() {
        return source;
    }

    public Key getKey() {
        return key;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr ;
        init();
    }

    public Object runQuery(Object source){
        try {
            this.source = source;
            Result result = visit( tree);
            return result.getValue() ;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    public Stack<Predicate> runKeyPredicate() {
        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        QueryListenerImpl queryListener = new QueryListenerImpl( queryStr);
        parseTreeWalker.walk( queryListener, tree);
        return queryListener.getPredicateStack();
    }

    @Override
    public Result visitScript(@NotNull QueryParser.ScriptContext ctx) {

        if ( ctx.getChildCount() == 1 )  //empty query, just EOF
            return new Result(true);
        else {
            if (ctx.selectStatement() != null) {
                statementType = StatementType.Select;
                return visit(ctx.selectStatement());
            }
            else {
                statementType = StatementType.Update;
                return visit(ctx.updateStatement());
            }
        }
    }

    @Override
    public Result visitSelectStats(@NotNull QueryParser.SelectStatsContext ctx) {
        selectObj = findId(ctx.identifier().getText());
        // if where staetment fail
        if ( ! checkWhereStatement( ctx.whereStatement()) )
            selectObj = null;
        else {
            //check for select *
            if (ctx.objectField().size() == 1 && ctx.objectField(0).getText().equals(ALL) )
                selectObj = findId(ctx.identifier().getText());
            else {
                List<String> list = new ArrayList<String>();
                for (QueryParser.ObjectFieldContext each : ctx.objectField())
                    list.add(each.getText());
                //sort the list in order of each context
                Collections.sort(list);
                selectObj = findId(ctx.identifier().getText());
                trim(selectObj, list, "");
            }
        }
        return new Result( selectObj);
    }

    @Override
    public Result visitWhereStatement(@NotNull QueryParser.WhereStatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Result visitAssignObject(@NotNull QueryParser.AssignObjectContext ctx) {
        Pair<Object, FieldInfo> pair = findObjectId( ctx.objectField().getText(), selectObj, classInfoMap);
        boolean pop = false;
        // if it is object,array, list and map
        Result.Type type  = getType(pair.getSecond().getField().getType().getName());
        if ( isObjectType(type) ) {
            idStack.push( selectObj );
            pop = true;
            try {
                Object obj = pair.getSecond().getField().get(selectObj);
                if ( obj == null ) {
                    //create a new instance
                    //obj = pair.getSecond().getField().getKeyType().newInstance() ;
                    obj = createInstance(pair.getSecond().getField().getType());
                }
                selectObj = obj;
            } catch (Exception e) {
                throw new QueryException(e.getMessage(), e);
            }
        }
        Result result = visit(ctx.value());
        try {
            if ( pop ) {
                selectObj = idStack.pop();
            }
            pair.getSecond().getField().set( selectObj, convert(type, result) );
        } catch (IllegalAccessException e) {
            throw new QueryException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Result visitKeys(@NotNull QueryParser.KeysContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Result visitReplaceStats(@NotNull QueryParser.ReplaceStatsContext ctx) {
        if ( ! checkWhereStatement( ctx.whereStatement()) )
            selectObj = null;
        else {
            selectObj = findId(ctx.objectField().getText());
            visit(ctx.assignments());
        }
        return new Result( selectObj);
    }

    @Override
    public Result visitInsertStats(@NotNull QueryParser.InsertStatsContext ctx) {
        if ( ! checkWhereStatement( ctx.whereStatement()) )
            selectObj = null;
        else {
            selectObj = findId(ctx.objectField().getText());
            Result.Type type = getType(selectObj.getClass().getName());
            if (type != Result.Type.LIST)
                throw new QueryException("insert statement expect array list type but get " + type);
        }
        return  new Result( selectObj);
    }

    @Override
    public Result visitAssignStats(@NotNull QueryParser.AssignStatsContext ctx) {
        for ( QueryParser.AssignmentsContext each : ctx.assignments()) {
            visit( each);
        }
        return null;
    }


    @Override
    public Result visitArrayValue(@NotNull QueryParser.ArrayValueContext ctx) {
        for( QueryParser.ValueContext each : ctx.value()) {
            ((ArrayList) selectObj).add( visit(each).getValue() );
        }
        return new Result( selectObj);
    }



    private boolean checkWhereStatement(@NotNull QueryParser.WhereStatementContext ctx) {
        if ( ctx != null && ctx.getChildCount() > 1) {
            int pos = ctx.getChildCount() -1 ;
            //the last child is predicate
            Result result = visit( ctx.getChild(pos));
            //check if not in front predicate
            if ( pos > 2 )
                return ! (Boolean) result.getValue() ;
            else
                return (Boolean) result.getValue() ;
        }
        else
            return true;
    }

    private Object findId(String id) {
        if ( id.toLowerCase().equals( source.getClass().getSimpleName().toLowerCase()))
            return source;
        else {
            Pair<Object, FieldInfo> pair = findObjectId(id, source, classInfoMap);
            try {
                if ( pair.getFirst() == null) return null;
                else return pair.getSecond().getField().get( pair.getFirst() );
            } catch (IllegalAccessException e) {
                throw new ObjectIdException( e.getMessage(), e);
            }
        }
    }

    /**
     * treat String as non primitive
     * @param from
     * @param list
     * @return target object let populate only in field list
     */
    private void trim(Object from, List<String> list, String prefix) {
        ClassInfo classInfo = findClassInfo(from, classInfoMap);
        for ( FieldInfo each : classInfo.getFieldInfos()) {
            int rs = inList( prefix+each.getField().getName(), list);
            if ( rs == 0 ){  //no match and not primitive
                if ( ! isPrimitive( each.getType()) ) {
                    try {
                        each.getField().set(from, null);
                    } catch (IllegalAccessException e) {
                        //swallow exception
                        logger.error( e.getMessage());
                    }
                }
            }
            else if ( rs == 2 ) {  //match but it is object with field
                try {
                    Object obj = each.getField().get(from);
                    if ( obj != null ) {  //it is object that has field in the list
                        //with prefix of simple name +"."
                        trim( obj, list, obj.getClass().getSimpleName()+".");
                    }
                } catch (IllegalAccessException e) {
                    //swallow exception
                    logger.error( e.getMessage());
                }

            }
            else if ( rs ==1 ) ;
                // match  do nothing
            else {
                logger.warn("wrong inList " + rs + " for " + each.getField().getName());
            }
        }
    }

    private int inList(String field, List<String> list) {
        for ( String each : list) {
            if ( field.equals(each) )
                return 1;
            else if ( each.indexOf(field+".") >=0 )
                return 2;

        }
        return 0;
    }

    @Override
    public Result visitObjectEmpty(@NotNull QueryParser.ObjectEmptyContext ctx) {
        return new Result(selectObj);
    }


    @Override
    public Result visitArrayEmpty(@NotNull QueryParser.ArrayEmptyContext ctx) {
        return new Result(selectObj);
    }


    @Override
    public Result visitIdentifier(@NotNull QueryParser.IdentifierContext ctx) {
        return null;
    }

    @Override
    public Result visitObjectAssigns(@NotNull QueryParser.ObjectAssignsContext ctx) {
        visit(ctx.assignments());
        return new Result( selectObj);
    }

//    @Override public Result visitStrings(@NotNull QueryParser.StringsContext ctx) {
//        String str = ctx.getText().substring(1, ctx.getText().length() -1);
//        return new Result(str); }
//
//    @Override public Result visitBooleans(@NotNull QueryParser.BooleansContext ctx) {
//        boolean value = Boolean.valueOf(ctx.getText());
//        return new Result(value);
//    }

    @Override public Result visitNulls(@NotNull QueryParser.NullsContext ctx) {
        return new Result( null );
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
//    @Override public Result visitNumbers(@NotNull QueryParser.NumbersContext ctx) {
//        return new Result( Result.Type.NUMBER, ctx.getText());
//    }


    @Override
    public Result visitErrorNode(@NotNull ErrorNode errorNode) {
        return null;
    }

    @Override
    public Result visitBooleans(@NotNull QueryParser.BooleansContext ctx) {
        boolean value = Boolean.valueOf( ctx.getText());
        return new Result( value);
    }

    @Override
    public Result visitParenPredicate(@NotNull QueryParser.ParenPredicateContext ctx) {
        return visit( ctx.predicate());
        //return visitChildren( ctx);
    }



    @Override
    public Result visitObjPredicate(@NotNull QueryParser.ObjPredicateContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.objectPredicate());
        } else {
            return new Result(!(Boolean) visit(ctx.objectPredicate()).getValue());
        }
    }


    @Override
    public Result visitInComp(@NotNull QueryParser.InCompContext ctx) {
        //Result objectId = visit( ctx.objectField());
        Result left = visitObjectField( ctx.objectField());;
        List<Result> listExpr = new ArrayList<Result>();
        for ( QueryParser.ExpressionContext each : ctx.expression())
            listExpr.add( visit(each));
        boolean result = isInList( left, listExpr);
        return new Result( result);
    }

    @Override
    public Result visitComparison(@NotNull QueryParser.ComparisonContext ctx) {
        Result left = visitObjectField( ctx.objectField());
        Result right = visit(ctx.expression());
        String operator = ctx.comparisonOperator().getText();
        boolean result = compare(left, operator, right);
        return new Result(result);
    }

    @Override
    public Result visitExpressionPredicate(QueryParser.ExpressionPredicateContext ctx) {
        Result left = visit( ctx.expression(0));
        Result right = visit(ctx.expression(1) );
        String operator = ctx.comparisonOperator().getText();
        boolean result = compare(left, operator, right);
        return new Result(result);
    }

    private Result visitObjectField(QueryParser.ObjectFieldContext objectField) {
        Result left;

        if ( objectField.getText().equals(KEY_FIELD)) {
            if ( key == null ) throw new QueryException(" key is null");
            left = new Result( key.getKey());
        }
        else if ( objectField.getText().equals(ALL)) {
            //return null
            left = new Result( null);
        }
        else {
            left = visit(objectField);
        }
        return left;
    }

    @Override
    public Result visitUnaryOperator(@NotNull QueryParser.UnaryOperatorContext ctx) {
        return null;
    }

    @Override
    public Result visitStrings(@NotNull QueryParser.StringsContext ctx) {
        String str = ctx.getText().substring(1, ctx.getText().length() - 1);
        return new Result( str);
    }

    @Override
    public Result visitLogicalOperator(@NotNull QueryParser.LogicalOperatorContext ctx) {
        return null;
    }

    @Override
    public Result visitLogicPredicate(@NotNull QueryParser.LogicPredicateContext ctx) {
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


//    @Override
//    public Result visitBinaryOperator(@NotNull PredicateParser.BinaryOperatorContext ctx) {
//        return null;
//    }

    @Override
    public Result visitIDS(@NotNull QueryParser.IDSContext ctx)  {
        //first is object, second is field
        Pair<Object, FieldInfo> pair = findObjectId(ctx.getText(), source, classInfoMap);
        idMap.put(ctx.getText(), pair.getSecond());
        try {
            if ( pair.getFirst() == null)
                return new Result( null);
            else {
                Object object = pair.getSecond().getField().get(pair.getFirst());
                return new Result(object);
            }
        } catch (IllegalAccessException e) {
            throw new ObjectIdException( e.getMessage(), e);
        }
    }

    @Override
    public Result visitExpParen(@NotNull QueryParser.ExpParenContext ctx) {
        return visit( ctx.expression());
    }

//    @Override
//    public Result visitComparisonOperator(@NotNull PredicateParser.ComparisonOperatorContext ctx) {
//        return null;
//    }

    @Override
    public Result visitNumbers(@NotNull QueryParser.NumbersContext ctx) {
        return new Result( Result.Type.NUMBER, ctx.getText());
    }

    @Override
    public Result visitExpr(@NotNull QueryParser.ExprContext ctx) {
        Result left = visit(ctx.expression(0));
        Result right = visit( ctx.expression(1));
        String binaryOperator = ctx.binaryOperator().getText();
        return binaryOp( left, binaryOperator, right);
    }

    @Override
    public Result visitSubstrExpr(QueryParser.SubstrExprContext ctx) {
        String left =  ((String) visit(ctx.objectField()).getValue());
        int index = Integer.valueOf( (String) visit(ctx.expression()).getValue()) ;
        return new Result(left.substring( index) );
    }


    @Override
    public Result visitLowerExpr(QueryParser.LowerExprContext ctx) {
        return  new Result(((String) visit(ctx.objectField()).getValue()).toLowerCase() );
    }

    @Override
    public Result visitUpperExpr(QueryParser.UpperExprContext ctx) {
        return  new Result(((String) visit(ctx.objectField()).getValue()).toUpperCase() );
    }


    @Override
    public Result visitCountExpr(QueryParser.CountExprContext ctx) {
        Result left = visit(ctx.objectField(0));
        Iterator iterator;
        if ( left.getValue() instanceof Collection ) {
            Collection collection = ((Collection) left.getValue());
            if ( ctx.objectField().size() > 1)
                iterator =  QueryUtils.collectObjectField( ctx.objectField(1).getText(), collection, idMap, classInfoMap).iterator();
            else
                iterator = collection.iterator();
        }
        else
            throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
        //return QueryUtils.existInIterator(iterator,visit(ctx.expression()) );
        Result right = visit(ctx.expression());
        int count = 0;
        while ( iterator.hasNext()) {
            Result lt = null ;
            Object obj = iterator.next();
            if ( obj instanceof Collection) {
                Iterator inside = ((Collection) obj).iterator();
                while (inside.hasNext()) {
                    lt = new Result( inside.next());
                    if (QueryUtils.compare(lt, "=", right) )
                        break;
                }
            }
            else
                lt = new Result( obj);
            if ( QueryUtils.compare( lt, "=", right) ) {
                count ++;
            }
        }
        return new Result(count);
    }

    @Override public Result visitExistExpr(QueryParser.ExistExprContext ctx) {
        Result left = visit(ctx.objectField(0));
        Iterator iterator;
        if ( left.getValue() instanceof Collection ) {
            Collection collection = ((Collection) left.getValue());
            if ( ctx.objectField().size() > 1)
                iterator =  QueryUtils.collectObjectField( ctx.objectField(1).getText(), collection, idMap, classInfoMap).iterator();
            else
                iterator = collection.iterator();
        }
        else
            throw new ObjectIdException("require collection type "+left.getValue().getClass().getName());
        Result right = visit(ctx.expression());
        return QueryUtils.existInIterator( iterator, right);
//        while ( iterator.hasNext()) {
//            Result lt = null ;
//            Object obj = iterator.next();
//            if ( obj instanceof Collection) {
//                Iterator inside = ((Collection) obj).iterator();
//                while (inside.hasNext()) {
//                    lt = new Result( inside.next());
//                    if (QueryUtils.compare(lt, "=", right) )
//                        return new Result(true);
//                }
//            }
//        }
//        return new Result(false);
    }
}
