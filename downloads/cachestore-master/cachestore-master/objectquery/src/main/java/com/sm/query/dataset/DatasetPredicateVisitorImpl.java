package com.sm.query.dataset;

import com.sm.query.parser.PredicateBaseVisitor;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.QueryException;
import com.sm.query.utils.QueryUtils;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


public class DatasetPredicateVisitorImpl extends PredicateBaseVisitor<DataSetLoadPlan> {
    private static final Log logger = LogFactory.getLog(DatasetPredicateVisitorImpl.class);
    public final static String AND = "and";

    private final Object source;
    private Map<String, RemoteSource> remoteSources = new HashMap<String, RemoteSource>();

    public DatasetPredicateVisitorImpl(Object source) {
        this.source = source;
    }

    public DataSetLoadPlan runPredicate(PredicateParser.ScriptContext tree) {
        try {
            if (source == null) {
                return new DataSetLoadPlan();
            }

            return visit(tree);
        } catch (Exception ex) {
            logger.error(ex);
            throw new QueryException(ex.getMessage(), ex);
        }
    }

    @Override
    public DataSetLoadPlan visitScript(@NotNull PredicateParser.ScriptContext ctx) {
        if (ctx.getChildCount() == 1) {
            // empty query is always true
            return new DataSetLoadPlan();
        } else {
            final PredicateParser.PredicateContext predicate = ctx.predicate();
            return visit(predicate);
        }
    }

    /*
     * predicate
     */

    @Override
    public DataSetLoadPlan visitNormal(@NotNull PredicateParser.NormalContext ctx) {
        final PredicateParser.PredicateContext leftPredicate = ctx.predicate(0);

        final String operator = ctx.logicalOperator().getText();
        final DataSetLoadPlan left = visit(leftPredicate);

        final PredicateParser.PredicateContext rightPredicate = ctx.predicate(1);
        final DataSetLoadPlan right = visit(rightPredicate);

        final DataSetLoadPlan[] dataSetLoadPlans = {left, right};
        return new DataSetLoadPlan(dataSetLoadPlans, operator);
    }

    /*
     * functionalPredicate
     */

    @Override
    public DataSetLoadPlan visitCountExpr(PredicateParser.CountExprContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        return visit(objectFieldContext);
    }

    @Override
    public DataSetLoadPlan visitExistExpr(PredicateParser.ExistExprContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        return visit(objectFieldContext);
    }

    @Override
    public DataSetLoadPlan visitExistListOr(PredicateParser.ExistListOrContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        return visit(objectFieldContext);
    }

    @Override
    public DataSetLoadPlan visitExistListAnd(PredicateParser.ExistListAndContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        return visit(objectFieldContext);
    }

    /*
     * value
     */
    @Override
    public DataSetLoadPlan visitLogicalOperator(@NotNull PredicateParser.LogicalOperatorContext ctx) {
        // handled in predicate#Normal
        return null;
    }

    @Override
    public DataSetLoadPlan visitObjectField(@NotNull PredicateParser.ObjectFieldContext ctx) {
        final String canonicalObjectPath = ctx.getText();
        final String[] fields = canonicalObjectPath.split(QueryUtils.DOT4R);

        final String rootField = fields[0];
        final StringBuilder pathBuilder = new StringBuilder(rootField);

        final DataSetLoadPlan.Comparator comparator = new DataSetLoadPlan.Comparator();
        final Set<RemoteSource> remoteSourceList = new TreeSet<RemoteSource>(comparator);

        final int length = fields.length;
        if (remoteSources.containsKey(rootField)) {
            final RemoteSource remoteSource = remoteSources.get(rootField);
            remoteSourceList.add(remoteSource);
        }

        for (int i = 1; i < length; i++) {
            pathBuilder.append('.');
            pathBuilder.append(fields[i]);

            final String path = pathBuilder.toString();
            if (remoteSources.containsKey(path)) {
                final RemoteSource remoteSource = remoteSources.get(path);
                remoteSourceList.add(remoteSource);
            }
        }

        return new DataSetLoadPlan(remoteSourceList);
    }

}