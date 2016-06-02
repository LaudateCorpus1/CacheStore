package com.sm.query;

import com.sm.query.parser.PredicateBaseVisitor;
import com.sm.query.parser.PredicateLexer;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.QueryException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.sm.query.utils.QueryUtils.DOT4R;

/**
 * Created by mhsieh on 4/29/16.
 */
public class PredicateAlias extends PredicateBaseVisitor {
    private static final Log logger = LogFactory.getLog(PredicateAlias.class);
    private String queryStr;
    private ParserRuleContext tree;
    private List<String> classNameList = new ArrayList<String>() ;
    private Filter.Impl filterType;

    public PredicateAlias(String queryStr) {
        this.queryStr = queryStr;
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


    public List<String> findAlias(Filter.Impl filterType) {
        try {
            if ( queryStr == null ||  queryStr.length() == 0) {
                logger.info("source is null or queryStr is empty");
                return classNameList;
            }
            //assign source, class name is null that represent source[i] is null
            this.filterType = filterType;
            classNameList.clear();
            Object result = visit( tree);
            return classNameList ;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }


    @Override
    public Object visitObjectField(@NotNull PredicateParser.ObjectFieldContext ctx)  {
        //find the right object from collection
        findSource( ctx.getText());
        return null;
    }

    private void findSource(String text ) {
        String[] ary = text.split(DOT4R);
        //more than 1 source object
        if ( ary.length > 1) {
            for ( String each : classNameList)  {
                if ( each.equals(ary[0]))
                    return;
            }
            if ( filterType == Filter.Impl.Schema) {
                if (ary[0].length() > 1) {
                    logger.error("schema alias length > 1 "+ary[0] );
                    return ;
                }
            }
            classNameList.add( ary[0]);
        }
    }



}
