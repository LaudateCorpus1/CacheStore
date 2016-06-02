package com.sm.query;

import com.sm.query.Result.Type;
import com.sm.query.parser.ObjectQueryBaseVisitor;
import com.sm.query.parser.ObjectQueryLexer;
import com.sm.query.parser.ObjectQueryParser;
import com.sm.query.utils.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.utils.Pair;

import java.io.StringReader;
import java.util.*;

import static com.sm.query.utils.QueryUtils.*;

//import static com.sm.query.utils.QueryUtils.findClassInfo;

/**
 * Created by mhsieh on 1/1/15.
 */
public class ObjectQueryVisitorImpl extends ObjectQueryBaseVisitor<Result> {
    private static final Log logger = LogFactory.getLog(PredicateVisitorImpl.class);

    private Stack<Object> idStack = new Stack<Object>();
    private Map<String, ClassInfo> classInfoMap = new HashMap<String, ClassInfo>();
    private String queryStr;
    private Object source;
    private Object selectObj;
    private ParserRuleContext tree;

    public ObjectQueryVisitorImpl(String queryStr) {
        this.queryStr = queryStr;
        init();
    }

    public ObjectQueryVisitorImpl(String queryStr, Object source) {
        this.queryStr = queryStr;
        this.source = source;
        init();
    }

    private void init() {
        try {
            ObjectQueryLexer lexer = new ObjectQueryLexer(new ANTLRInputStream(new StringReader(queryStr)));
            CommonTokenStream token = new CommonTokenStream(lexer);
            ObjectQueryParser parser = new ObjectQueryParser(token);
            parser.setBuildParseTree(true);
            tree = parser.script();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
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


    public Object getSelectObj() {
        return selectObj;
    }

    public Object getSource() {
        return source;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
        init();
    }

    @Override
    public Result visitAssignObject(@NotNull ObjectQueryParser.AssignObjectContext ctx) {
        Pair<Object, FieldInfo> pair = findObjectId( ctx.objectField().getText(), selectObj, classInfoMap);
        boolean pop = false;
        // if it is object,array, list and map
        Type type  = getType(pair.getSecond().getField().getType().getName());
        if ( isObjectType(type) ) {
            idStack.push( selectObj );
            pop = true;
            try {
                Object obj = pair.getSecond().getField().get(selectObj);
                if ( obj == null ) {
                    //create a new instance
                    //obj = pair.getSecond().getField().getType().newInstance() ;
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
    public Result visitWhereStatement(@NotNull ObjectQueryParser.WhereStatementContext ctx) {
        ctx.searchCondition();
        return null;
    }

    @Override
    public Result visitReplaceStats(@NotNull ObjectQueryParser.ReplaceStatsContext ctx) {
        selectObj = findId( ctx.objectField().getText());
        visit(ctx.assignments());
        return new Result( selectObj);
    }

    @Override
    public Result visitInsertStats(@NotNull ObjectQueryParser.InsertStatsContext ctx) {
        selectObj = findId( ctx.objectField().getText());
        Type type =getType(selectObj.getClass().getName());
        if ( type != Type.LIST)
            throw new QueryException("insert statement expect array list type but get "+ type);
        //@TODO need more work, currently do nothing, just return selectObj
        Result record = visit( ctx.whereStatement());
        return  new Result( selectObj);
    }

    @Override
    public Result visitAssignStats(@NotNull ObjectQueryParser.AssignStatsContext ctx) {
        for ( ObjectQueryParser.AssignmentsContext each : ctx.assignments()) {
            visit( each);
        }
        return null;
    }

    @Override
    public Result visitScript(@NotNull ObjectQueryParser.ScriptContext ctx) {
        if (ctx.selectStatement() != null)
            return visit( ctx.selectStatement());
        else{
            for (ObjectQueryParser.UpdateStatementContext each : ctx.updateStatement() ) {
                Result result =visit( each);
                System.out.println( result.getValue().toString());
            }
            return new Result(true);
        }
    }

    @Override
    public Result visitArrayValue(@NotNull ObjectQueryParser.ArrayValueContext ctx) {
        for( ObjectQueryParser.ValueContext each : ctx.value()) {
            ((ArrayList) selectObj).add( visit(each).getValue() );
        }
        return new Result( selectObj);
    }

    @Override
    public Result visitSearchObjectId(@NotNull ObjectQueryParser.SearchObjectIdContext ctx) {
        Result left = visit( ctx.objectField());
        //Result right = visit( ctx.)
        //ctx.objectField(); ctx.NUMBER().getText();
        return new Result(true);
    }

    @Override
    public Result visitSelectStats(@NotNull ObjectQueryParser.SelectStatsContext ctx) {
        List<String> list = new ArrayList<String>();
        for ( ObjectQueryParser.ObjectFieldContext each : ctx.objectField())
            list.add( each.getText() );
        //sort the list in order of each context
        Collections.sort(list);
        selectObj = findId( ctx.identifier().getText() );
        trim( selectObj, list, "");
        return new Result( selectObj);
    }

    private Object findId(String id) {
        if ( id.toLowerCase().equals( source.getClass().getSimpleName().toLowerCase()))
            return source;
        else {
            Pair<Object, FieldInfo> pair = findObjectId(id, source, classInfoMap);
            try {
                if ( pair.getFirst() == null) return new Result(null);
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
                if ( ! QueryUtils.isPrimitive( each.getType()) ) {
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
    public Result visitObjectEmpty(@NotNull ObjectQueryParser.ObjectEmptyContext ctx) {
        return new Result(selectObj);
    }


    @Override
    public Result visitObjectField(@NotNull ObjectQueryParser.ObjectFieldContext ctx) {
        //first is object, second is field
        Pair<Object, FieldInfo> pair = findObjectId(ctx.getText(), source, classInfoMap);
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
    public Result visitArrayEmpty(@NotNull ObjectQueryParser.ArrayEmptyContext ctx) {
        return new Result(selectObj);
    }

    @Override
    public Result visitSearchRecord(@NotNull ObjectQueryParser.SearchRecordContext ctx) {
        return null;
    }

    @Override
    public Result visitIdentifier(@NotNull ObjectQueryParser.IdentifierContext ctx) {
        return null;
    }

    @Override
    public Result visitObjectAssigns(@NotNull ObjectQueryParser.ObjectAssignsContext ctx) {
        visit(ctx.assignments());
        return new Result( selectObj);
    }

    @Override public Result visitStrings(@NotNull ObjectQueryParser.StringsContext ctx) {
        String str = ctx.getText().substring(1, ctx.getText().length() -1);
        return new Result(str); }

    @Override public Result visitBooleans(@NotNull ObjectQueryParser.BooleansContext ctx) {
        boolean value = Boolean.valueOf(ctx.getText());
        return new Result(value);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Result visitNulls(@NotNull ObjectQueryParser.NullsContext ctx) {
        return new Result( null );
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Result visitNumbers(@NotNull ObjectQueryParser.NumbersContext ctx) {
        return new Result( Type.NUMBER, ctx.getText());
    }


    @Override
    public Result visitErrorNode(@NotNull ErrorNode errorNode) {
        return null;
    }
}
