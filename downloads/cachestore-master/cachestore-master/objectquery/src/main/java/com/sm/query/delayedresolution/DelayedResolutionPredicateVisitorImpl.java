package com.sm.query.delayedresolution;

import com.sm.query.parser.PredicateBaseVisitor;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.QueryException;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;


/**
 * TODO since runPredicate can be expected to run several times, there's some memoization that can be done
 */
public class DelayedResolutionPredicateVisitorImpl extends PredicateBaseVisitor<DelayedResolvable> {
    private static final Log logger = LogFactory.getLog(DelayedResolutionPredicateVisitorImpl.class);
    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "!=";

    public static final String LT = "<";
    public static final String LTE = "<=";
    public static final String GT = ">";
    public static final String GTE = ">=";

    public final static String SOURCE_FIELD = "this";
    public final static String AND = "and";

    private final Object source;
    private final ObjectFieldContextVisitor objectFieldContextVisitor;

    public DelayedResolutionPredicateVisitorImpl(Object source, ObjectFieldContextVisitor objectFieldContextVisitor) {
        this.source = source;
        this.objectFieldContextVisitor = objectFieldContextVisitor;
    }

    public DelayedResolvable runPredicate(PredicateParser.ScriptContext tree) {
        try {
            final DelayedResolvable unresolved = visit(tree);
            return unresolved;
        } catch (Exception ex) {
            logger.error(ex);
            throw new QueryException(ex.getMessage(), ex);
        }
    }

    @Override
    public DelayedResolvable visitScript(@NotNull PredicateParser.ScriptContext ctx) {
        if (ctx.getChildCount() == 1) {
            // empty query is always true
            return new DelayedResolvable(true);
        } else {
            final PredicateParser.PredicateContext predicate = ctx.predicate();
            return visit(predicate);
        }
    }

    /*
     * predicate
     */

    @Override
    public DelayedResolvable visitNormal(@NotNull PredicateParser.NormalContext ctx) {
        final PredicateParser.PredicateContext leftPredicate = ctx.predicate(0);

        // @TODO see why there was a check for predicate size of 1

        final boolean comparisonIsAnd = ctx.logicalOperator().getText().equals(AND);

        final DelayedResolvable left = visit(leftPredicate);

        final PredicateParser.PredicateContext rightPredicate = ctx.predicate(1);

        final DelayedResolvable resolvable;

        if (left.isResolved()) {
            final Boolean leftResolution = left.getPredicateResolution();

            if (comparisonIsAnd && leftResolution) {
                // TRUE && B() == B()
                resolvable = visit(rightPredicate);
            } else if (!comparisonIsAnd && !leftResolution) {
                // FALSE || B() == B()
                resolvable = visit(rightPredicate);
            } else {
                // FALSE && B() == FALSE
                // TRUE || B() == TRUE
                resolvable = new DelayedResolvable(leftResolution);
            }
        } else {
            final DelayedResolvable right = visit(rightPredicate);

            if (right.isResolved()) {
                final Boolean rightResolution = right.getPredicateResolution();

                if (comparisonIsAnd && !rightResolution) {
                    // A() && FALSE == FALSE
                    resolvable = right;
                } else if (!comparisonIsAnd && rightResolution) {
                    // A() || TRUE == TRUE
                    resolvable = right;
                } else {
                    // A() && TRUE == A()
                    // A() || FALSE == A()
                    resolvable = left;
                }
            } else {
                left.mergeMissingRemoteSources(right);
                resolvable = left;
            }
        }

        return resolvable;
    }

    @Override
    public DelayedResolvable visitParenPredicate(@NotNull PredicateParser.ParenPredicateContext ctx) {
        final PredicateParser.PredicateContext predicate = ctx.predicate();
        return visit(predicate);
    }

    @Override
    public DelayedResolvable visitNotPredicate(@NotNull PredicateParser.NotPredicateContext ctx) {
        final PredicateParser.PredicateContext predicate = ctx.predicate();
        final DelayedResolvable visit = visit(predicate);
        visit.invert();
        return visit;
    }

    /*
     * objectPredicate
     */

    @Override
    public DelayedResolvable visitComparison(@NotNull PredicateParser.ComparisonContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField();
        DelayedResolvable left = visit(objectFieldContext);
        if (left.isResolved()) {
            String operator = ctx.comparisonOperator().getText();

            final PredicateParser.ExpressionContext expression = ctx.expression();
            // expressions always resolve
            DelayedResolvable right = visit(expression);

            final boolean resolution = left.resolveCompare(operator, right);
            return new DelayedResolvable(resolution);
        } else {
            return left;
        }

    }

    @Override
    public DelayedResolvable visitCheckNull(@NotNull PredicateParser.CheckNullContext ctx) {
        final PredicateParser.ObjectFieldContext objectField = ctx.objectField();
        final DelayedResolvable left = visit(objectField);

        if (left.isResolved()) {
            final boolean isNull = left.isNull();
            return new DelayedResolvable(isNull);
        } else {
            return left;
        }
    }

    /**
     * x in (a1, a2, ...) is the same as x = a1 AND x = a2 AND ...
     *
     * @param ctx
     * @return
     */
    @Override
    public DelayedResolvable visitInComp(@NotNull PredicateParser.InCompContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField();
        DelayedResolvable objectFieldValue = visit(objectFieldContext);

        if (objectFieldValue.isResolved()) {
            final List<PredicateParser.ExpressionContext> expressions = ctx.expression();
            for (PredicateParser.ExpressionContext inExpression : expressions) {
                // expressions always resolve
                final DelayedResolvable inExpressionElement = visit(inExpression);
                final boolean expressionResolution = objectFieldValue.resolveCompare(EQUALS, inExpressionElement);
                if (!expressionResolution) {
                    return new DelayedResolvable(false);
                }
            }

            return new DelayedResolvable(true);
        } else {
            return objectFieldValue;
        }
    }

    /*
     * functionalPredicate
     */

    @Override
    public DelayedResolvable visitCountExpr(PredicateParser.CountExprContext ctx) {
        // get the objectField resolution of the first objectField
        // if a second objectField is provided
        //   resolve the objectField to a Collection of object
        //   use second objectField on each object
        //     supports the subobject's field being a scalar or a Collection
        //
        // example:
        // Q.bar = List<Foo>
        // Foo.baz = List<P>
        // query: count (bar, baz, 3 + 2) = 8
        // means: of the field 'bar', which should be a list of objects with the field 'baz', if the value for 'baz'
        //      is equal to '3 + 2' or '5', then count, return if that number is equal to '8'
        //
        // Supportable types:
        // 1. Root.field is NestableCollection<SubObject>, SubObject.subField is NestableCollection<Value>
        // 1. Root.field is NestableCollection<Value>, no SubObject

        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        final DelayedResolvable rootResolvable = visit(objectFieldContext);
        if (rootResolvable.isResolved()) {
            final PredicateParser.ExpressionContext matchExpression = ctx.expression(0);
            final DelayedResolvable expressionResolution = visit(matchExpression);

            final PredicateParser.ObjectFieldContext objectFieldSub = ctx.objectField(1);

            int counted = 0;

            boolean subObjectHasUnresolved = false;
            final boolean hasSubObject = objectFieldSub != null;
            if (hasSubObject) {
                final Iterable asIterator = rootResolvable.getIterable();
                for (Object leftSubValue : asIterator) {
                    final DelayedResolvable leftSubComparison = objectFieldContextVisitor.visitObjectField(objectFieldSub, leftSubValue);
                    if (leftSubComparison.isResolved()) {
                        counted += leftSubComparison.resolveEqualsOrCount(expressionResolution);
                    } else {
                        subObjectHasUnresolved = true;
                        rootResolvable.mergeMissingRemoteSources(leftSubComparison);
                    }
                }

            } else {
                counted += rootResolvable.resolveCount(expressionResolution);
            }

            final DelayedResolvable substep;
            if (hasSubObject && subObjectHasUnresolved) {
                substep = rootResolvable;
            } else {
                substep = new DelayedResolvable(counted);
            }

            if (substep.isResolved()) {
                final PredicateParser.ComparisonOperatorContext comparisonOperatorContext = ctx.comparisonOperator();
                final String comparisonOperator = comparisonOperatorContext.getText();

                final PredicateParser.ExpressionContext filterExpression = ctx.expression(1);
                final DelayedResolvable filterResolvable = visit(filterExpression);

                boolean countFiltered = substep.resolveCompare(comparisonOperator, filterResolvable);
                return new DelayedResolvable(countFiltered);
            } else {
                return substep;
            }
        } else {
            return rootResolvable;
        }
    }

    @Override
    public DelayedResolvable visitExistExpr(PredicateParser.ExistExprContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        final DelayedResolvable rootResolvable = visit(objectFieldContext);
        if (rootResolvable.isResolved()) {
            final PredicateParser.ExpressionContext matchExpression = ctx.expression();
            final DelayedResolvable matchResolution = visit(matchExpression);

            final PredicateParser.ObjectFieldContext objectFieldSub = ctx.objectField(1);

            boolean notExists = true;
            boolean subObjectHasUnresolved = false;
            final boolean hasSubObject = objectFieldSub != null;
            if (hasSubObject) {
                final Iterable asIterator = rootResolvable.getIterable();
                final Iterator iterator = asIterator.iterator();
                while (iterator.hasNext() && notExists) {
                    Object leftSubValue = iterator.next();
                    final DelayedResolvable leftSubComparison = objectFieldContextVisitor.visitObjectField(objectFieldSub, leftSubValue);
                    if (leftSubComparison.isResolved()) {
                        notExists = !leftSubComparison.resolveEqualsOrContains(matchResolution);
                    } else {
                        subObjectHasUnresolved = true;
                        rootResolvable.mergeMissingRemoteSources(leftSubComparison);
                    }
                }

            } else {
                notExists = rootResolvable.resolveContains(matchResolution);
            }

            final DelayedResolvable resolvable;
            if (hasSubObject && subObjectHasUnresolved) {
                resolvable = rootResolvable;
            } else {
                resolvable = new DelayedResolvable(!notExists);
            }

            return resolvable;
        } else {
            return rootResolvable;
        }
    }

    private DelayedResolvable stringModifiedExpr(DelayedResolvable leftObject, String comparisonOperator, DelayedResolvable rightObject, StringModifier modifier, PredicateParser.FunctionalPredicateContext originalContext) {
        if (leftObject.isResolved()) {
            final String string = leftObject.getString();
            final String modified = modifier.modify(string);
            leftObject.setResolution(modified);

            boolean compare = leftObject.resolveCompare(comparisonOperator, rightObject);
            return new DelayedResolvable(compare);
        } else {
            return leftObject;
        }
    }

    @Override
    public DelayedResolvable visitLowerExpr(PredicateParser.LowerExprContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField();
        final DelayedResolvable leftObject = visit(objectFieldContext);

        final PredicateParser.ExpressionContext resolvedExpression = ctx.expression();
        final DelayedResolvable expression = visit(resolvedExpression);

        final String text = ctx.comparisonOperator().getText();

        return stringModifiedExpr(leftObject, text, expression, StringModifier.LOWER, ctx);
    }

    @Override
    public DelayedResolvable visitUpperExpr(PredicateParser.UpperExprContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField();
        final DelayedResolvable leftObject = visit(objectFieldContext);

        final PredicateParser.ExpressionContext resolvedExpression = ctx.expression();
        final DelayedResolvable expression = visit(resolvedExpression);

        final String text = ctx.comparisonOperator().getText();

        return stringModifiedExpr(leftObject, text, expression, StringModifier.UPPER, ctx);
    }

    @Override
    public DelayedResolvable visitSubstrExpr(PredicateParser.SubstrExprContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField();
        final DelayedResolvable leftObject = visit(objectFieldContext);

        final PredicateParser.ExpressionContext substringArgExpression = ctx.expression(0);
        final DelayedResolvable substringArgument = visit(substringArgExpression);
        final String substringArgumentString = substringArgument.getString();
        final Integer substringArgumentInteger = Integer.valueOf(substringArgumentString);
        final StringModifier modifier = new StringModifier.Substring(substringArgumentInteger);

        final PredicateParser.ExpressionContext resolvedExpression = ctx.expression(1);
        final DelayedResolvable expression = visit(resolvedExpression);

        final String text = ctx.comparisonOperator().getText();

        return stringModifiedExpr(leftObject, text, expression, modifier, ctx);
    }

    @Override
    public DelayedResolvable visitExistListOr(PredicateParser.ExistListOrContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        final DelayedResolvable leftObjectRoot = visit(objectFieldContext);

        if (leftObjectRoot.isResolved()) {
            final PredicateParser.ObjectFieldContext objectFieldSub = ctx.objectField(1);
            final List<PredicateParser.ExpressionContext> expressions = ctx.expression();
            final Iterator<PredicateParser.ExpressionContext> expressionIterator = expressions.iterator();

            final boolean hasSubObjects = objectFieldSub != null;

            final Iterable leftValueIterator;
            if (hasSubObjects) {
                leftValueIterator = leftObjectRoot.getIterable();
            } else {
                leftValueIterator = null;
            }

            boolean anySubObjectUnresolved = false;
            boolean resolved = false;
            while (expressionIterator.hasNext() && !resolved) {
                final PredicateParser.ExpressionContext expressionList = expressionIterator.next();
                final DelayedResolvable expressionResolution = visit(expressionList);

                if (hasSubObjects) {
                    final Iterator leftIterator = leftValueIterator.iterator();

                    boolean subResolved = false;
                    while (leftIterator.hasNext() && !subResolved) {
                        final Object leftSubValue = leftIterator.next();
                        final DelayedResolvable leftSubComparison = objectFieldContextVisitor.visitObjectField(objectFieldSub, leftSubValue);
                        if (leftSubComparison.isResolved()) {
                            subResolved = leftSubComparison.resolveContains(expressionResolution);
                        } else {
                            anySubObjectUnresolved = true;
                            leftObjectRoot.mergeMissingRemoteSources(leftSubComparison);
                        }
                        // else rely on failure case to return unresolved
                    }

                    resolved = subResolved;

                } else {
                    resolved = leftObjectRoot.resolveContains(expressionResolution);
                }
            }

            if (hasSubObjects && anySubObjectUnresolved) {
                return leftObjectRoot;
            } else {
                return new DelayedResolvable(resolved);
            }
        } else {
            return leftObjectRoot;
        }
    }


    @Override
    public DelayedResolvable visitExistListAnd(PredicateParser.ExistListAndContext ctx) {
        final PredicateParser.ObjectFieldContext objectFieldContext = ctx.objectField(0);
        final DelayedResolvable leftObjectRoot = visit(objectFieldContext);

        if (leftObjectRoot.isResolved()) {
            final PredicateParser.ObjectFieldContext objectFieldSub = ctx.objectField(1);
            final List<PredicateParser.ExpressionContext> expressions = ctx.expression();
            final Iterator<PredicateParser.ExpressionContext> expressionIterator = expressions.iterator();

            final boolean hasSubObjects = objectFieldSub != null;

            final Iterable leftValueIterator;
            if (hasSubObjects) {
                leftValueIterator = leftObjectRoot.getIterable();
            } else {
                leftValueIterator = null;
            }

            boolean anySubObjectUnresolved = false;
            boolean resolved = true;
            while (expressionIterator.hasNext() && resolved) {
                final PredicateParser.ExpressionContext expressionList = expressionIterator.next();
                final DelayedResolvable expressionResolution = visit(expressionList);

                if (hasSubObjects) {
                    final Iterator leftIterator = leftValueIterator.iterator();

                    boolean subResolved = true;
                    while (leftIterator.hasNext() && subResolved) {
                        final Object leftSubValue = leftIterator.next();
                        final DelayedResolvable leftSubComparison = objectFieldContextVisitor.visitObjectField(objectFieldSub, leftSubValue);
                        if (leftSubComparison.isResolved()) {
                            subResolved = leftSubComparison.resolveContains(expressionResolution);
                        } else {
                            anySubObjectUnresolved = true;
                            leftObjectRoot.mergeMissingRemoteSources(leftSubComparison);
                        }
                        // else rely on failure case to return unresolved
                    }

                    resolved = subResolved;

                } else {
                    resolved = leftObjectRoot.resolveContains(expressionResolution);
                }
            }

            if (hasSubObjects && anySubObjectUnresolved) {
                return leftObjectRoot;
            } else {
                return new DelayedResolvable(resolved);
            }
        } else {
            return leftObjectRoot;
        }
    }

    @Override
    public DelayedResolvable visitBitsOr(PredicateParser.BitsOrContext ctx) {
        final PredicateParser.ObjectFieldContext objectField = ctx.objectField();
        final DelayedResolvable evaluatedObject = visit(objectField);

        if (evaluatedObject.isResolved()) {
            byte[] bytes = evaluatedObject.getByteArray();

            final List<PredicateParser.ExpressionContext> expressions = ctx.expression();
            final int expressionsSize = expressions.size();
            for (int i = 0; i < expressionsSize; i++) {
                final PredicateParser.ExpressionContext expression = ctx.expression(i);
                final DelayedResolvable expressionResolution = visit(expression);

                int v = expressionResolution.getInteger();

                if (checkFlag(bytes, v)) {
                    return new DelayedResolvable(true);
                }
            }

            return new DelayedResolvable(false);
        } else {
            return evaluatedObject;
        }
    }

    @Override
    public DelayedResolvable visitBitsAnd(PredicateParser.BitsAndContext ctx) {
        final PredicateParser.ObjectFieldContext objectField = ctx.objectField();
        final DelayedResolvable evaluatedObject = visit(objectField);

        if (evaluatedObject.isResolved()) {
            byte[] bytes = evaluatedObject.getByteArray();

            final List<PredicateParser.ExpressionContext> expressions = ctx.expression();
            final int expressionsSize = expressions.size();
            for (int i = 0; i < expressionsSize; i++) {
                final PredicateParser.ExpressionContext expression = ctx.expression(i);
                final DelayedResolvable expressionResolution = visit(expression);

                int v = expressionResolution.getInteger();

                if (!checkFlag(bytes, v)) {
                    return new DelayedResolvable(false);
                }
            }

            return new DelayedResolvable(true);
        } else {
            return evaluatedObject;
        }
    }

    /**
     * @param bytes is 0 base array
     * @param pos   is 1 base, start with 1 ...
     * @return
     */
    boolean checkFlag(byte[] bytes, int pos) {
        // index position
        int i = pos / 8;
        // offset bit location
        int off = pos % 8;

        if (i >= bytes.length)
            throw new QueryException("source index out of bound len " + bytes.length + " index " + i + " pos " + pos);
        byte b;
        if (off == 0)
            b = bytes[i - 1];
        else
            b = bytes[i];

        return (b & (0x01 << off)) > 0;
    }

    /*
     * expression
     */

    @Override
    public DelayedResolvable visitExpr(@NotNull PredicateParser.ExprContext ctx) {
        final PredicateParser.ExpressionContext leftExpression = ctx.expression(0);
        DelayedResolvable left = visit(leftExpression);
        final PredicateParser.ExpressionContext rightExpression = ctx.expression(1);
        DelayedResolvable right = visit(rightExpression);

        String binaryOperator = ctx.binaryOperator().getText();
        return left.applyOperator(binaryOperator, right);
    }

    @Override
    public DelayedResolvable visitExpParen(@NotNull PredicateParser.ExpParenContext ctx) {
        final PredicateParser.ExpressionContext insideParenthesis = ctx.expression();
        return visit(insideParenthesis);
    }

    /*
     * value
     */

    @Override
    public DelayedResolvable visitNumbers(@NotNull PredicateParser.NumbersContext ctx) {
        final String numberAsText = ctx.getText();

        final Object value;
        if (!numberAsText.contains(".")) {
            value = Long.parseLong(numberAsText);
        } else {
            value = Double.parseDouble(numberAsText);
        }

        return new DelayedResolvable(value);
    }

    @Override
    public DelayedResolvable visitStrings(@NotNull PredicateParser.StringsContext ctx) {
        final String textValue = ctx.getText();
        String str = textValue.substring(1, textValue.length() - 1);
        return new DelayedResolvable(str);
    }

    @Override
    public DelayedResolvable visitBooleans(@NotNull PredicateParser.BooleansContext ctx) {
        final String resolvedBooleanContext = ctx.getText();
        boolean value = Boolean.valueOf(resolvedBooleanContext);
        return new DelayedResolvable(value);
    }

    @Override
    public DelayedResolvable visitLogicalOperator(@NotNull PredicateParser.LogicalOperatorContext ctx) {
        // handled in predicate#Normal
        return null;
    }

    @Override
    public DelayedResolvable visitObjectField(@NotNull PredicateParser.ObjectFieldContext ctx) {
        final String canonicalObjectPath = ctx.getText();
        if (canonicalObjectPath.equals(SOURCE_FIELD)) {
            return new DelayedResolvable(source);
        } else {
            return objectFieldContextVisitor.visitObjectField(ctx, source);
        }
    }
}