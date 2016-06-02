// Generated from /Users/mhsieh/java/dev/query/objectquery/src/main/resources/Query.g4 by ANTLR 4.5.1
package com.sm.query.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryParser}.
 */
public interface QueryListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QueryParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(QueryParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(QueryParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelectStats}
	 * labeled alternative in {@link QueryParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStats(QueryParser.SelectStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelectStats}
	 * labeled alternative in {@link QueryParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStats(QueryParser.SelectStatsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReplaceStats}
	 * labeled alternative in {@link QueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void enterReplaceStats(QueryParser.ReplaceStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReplaceStats}
	 * labeled alternative in {@link QueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void exitReplaceStats(QueryParser.ReplaceStatsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InsertStats}
	 * labeled alternative in {@link QueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void enterInsertStats(QueryParser.InsertStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InsertStats}
	 * labeled alternative in {@link QueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void exitInsertStats(QueryParser.InsertStatsContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#whereStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhereStatement(QueryParser.WhereStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#whereStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhereStatement(QueryParser.WhereStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LimitPhrase}
	 * labeled alternative in {@link QueryParser#limitCause}.
	 * @param ctx the parse tree
	 */
	void enterLimitPhrase(QueryParser.LimitPhraseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LimitPhrase}
	 * labeled alternative in {@link QueryParser#limitCause}.
	 * @param ctx the parse tree
	 */
	void exitLimitPhrase(QueryParser.LimitPhraseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterObjPredicate(QueryParser.ObjPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitObjPredicate(QueryParser.ObjPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterLogicPredicate(QueryParser.LogicPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitLogicPredicate(QueryParser.LogicPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterNotPredicate(QueryParser.NotPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitNotPredicate(QueryParser.NotPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterParenPredicate(QueryParser.ParenPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitParenPredicate(QueryParser.ParenPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void enterComparison(QueryParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void exitComparison(QueryParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InComp}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void enterInComp(QueryParser.InCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InComp}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void exitInComp(QueryParser.InCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionPredicate}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void enterExpressionPredicate(QueryParser.ExpressionPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionPredicate}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void exitExpressionPredicate(QueryParser.ExpressionPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionExp}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionExp(QueryParser.FunctionExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionExp}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionExp(QueryParser.FunctionExpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Expr}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpr(QueryParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Expr}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpr(QueryParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpValues}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpValues(QueryParser.ExpValuesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpValues}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpValues(QueryParser.ExpValuesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpParen}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpParen(QueryParser.ExpParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpParen}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpParen(QueryParser.ExpParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LowerExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterLowerExpr(QueryParser.LowerExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LowerExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitLowerExpr(QueryParser.LowerExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UpperExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterUpperExpr(QueryParser.UpperExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UpperExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitUpperExpr(QueryParser.UpperExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CountExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterCountExpr(QueryParser.CountExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CountExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitCountExpr(QueryParser.CountExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SubstrExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterSubstrExpr(QueryParser.SubstrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SubstrExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitSubstrExpr(QueryParser.SubstrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterExistExpr(QueryParser.ExistExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitExistExpr(QueryParser.ExistExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StrToBytesFuncExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterStrToBytesFuncExpr(QueryParser.StrToBytesFuncExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StrToBytesFuncExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitStrToBytesFuncExpr(QueryParser.StrToBytesFuncExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignStats}
	 * labeled alternative in {@link QueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void enterAssignStats(QueryParser.AssignStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignStats}
	 * labeled alternative in {@link QueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void exitAssignStats(QueryParser.AssignStatsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignObject}
	 * labeled alternative in {@link QueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void enterAssignObject(QueryParser.AssignObjectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignObject}
	 * labeled alternative in {@link QueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void exitAssignObject(QueryParser.AssignObjectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Keys}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void enterKeys(QueryParser.KeysContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Keys}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void exitKeys(QueryParser.KeysContext ctx);
	/**
	 * Enter a parse tree produced by the {@code All}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void enterAll(QueryParser.AllContext ctx);
	/**
	 * Exit a parse tree produced by the {@code All}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void exitAll(QueryParser.AllContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IDS}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void enterIDS(QueryParser.IDSContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IDS}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void exitIDS(QueryParser.IDSContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(QueryParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(QueryParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Objects}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterObjects(QueryParser.ObjectsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Objects}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitObjects(QueryParser.ObjectsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Arrays}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterArrays(QueryParser.ArraysContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Arrays}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitArrays(QueryParser.ArraysContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterStrings(QueryParser.StringsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitStrings(QueryParser.StringsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNumbers(QueryParser.NumbersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNumbers(QueryParser.NumbersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterBooleans(QueryParser.BooleansContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitBooleans(QueryParser.BooleansContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNulls(QueryParser.NullsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNulls(QueryParser.NullsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjectAssigns}
	 * labeled alternative in {@link QueryParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObjectAssigns(QueryParser.ObjectAssignsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjectAssigns}
	 * labeled alternative in {@link QueryParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObjectAssigns(QueryParser.ObjectAssignsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjectEmpty}
	 * labeled alternative in {@link QueryParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObjectEmpty(QueryParser.ObjectEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjectEmpty}
	 * labeled alternative in {@link QueryParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObjectEmpty(QueryParser.ObjectEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayValue}
	 * labeled alternative in {@link QueryParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArrayValue(QueryParser.ArrayValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayValue}
	 * labeled alternative in {@link QueryParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArrayValue(QueryParser.ArrayValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayEmpty}
	 * labeled alternative in {@link QueryParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArrayEmpty(QueryParser.ArrayEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayEmpty}
	 * labeled alternative in {@link QueryParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArrayEmpty(QueryParser.ArrayEmptyContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#binaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperator(QueryParser.BinaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#binaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperator(QueryParser.BinaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(QueryParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(QueryParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(QueryParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(QueryParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOperator(QueryParser.LogicalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOperator(QueryParser.LogicalOperatorContext ctx);
}