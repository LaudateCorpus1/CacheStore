package com.sm.query.delayedresolution;

import com.sm.query.parser.PredicateLexer;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.Column;
import com.sm.query.utils.QueryException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.util.Map;

/**
 * Created on 4/25/16.
 */
public class DelayedResolutionPredicateRunner {
    private static final Log logger = LogFactory.getLog(DelayedResolutionPredicateVisitorImpl.class);

    private final String queryString;
    private final Map<String, Column> columnMap;
    private final PredicateParser.ScriptContext scriptContext;
    private final Map<String, Boolean> remoteSourcesLoaded;

    private DelayedResolutionPredicateVisitorImpl impl;



    public DelayedResolutionPredicateRunner(String queryString, Map<String, Column> columnMap, Map<String, Boolean> remoteSourcesLoaded) {
        this.queryString = queryString;
        this.columnMap = columnMap;
        this.remoteSourcesLoaded = remoteSourcesLoaded;

        try {
            final StringReader stringReader = new StringReader(queryString);
            final ANTLRInputStream ais = new ANTLRInputStream(stringReader);
            final PredicateLexer lexer = new PredicateLexer(ais);
            final CommonTokenStream token = new CommonTokenStream(lexer);
            final PredicateParser parser = new PredicateParser(token);
            parser.setBuildParseTree(true);
            scriptContext = parser.script();
        } catch (Exception ex) {
            logger.error(ex);
            throw new QueryException(ex.getMessage(), ex);
        }
    }

    public DelayedResolvable runPredicate(Object source) {
        if (impl == null) {
            final ObjectFieldContextVisitor objectFieldContextVisitor = new ObjectFieldContextVisitor(columnMap, remoteSourcesLoaded);
            impl = new DelayedResolutionPredicateVisitorImpl(source, objectFieldContextVisitor);
        }

        final DelayedResolvable unresolved = impl.runPredicate(scriptContext);
        return unresolved;
    }
}
