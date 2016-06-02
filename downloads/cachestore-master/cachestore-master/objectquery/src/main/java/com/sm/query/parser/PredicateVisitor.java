// Generated from /Users/mhsieh/java/dev/query/objectquery/src/main/resources/Predicate.g4 by ANTLR 4.5.1
package com.sm.query.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PredicateParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PredicateVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PredicateParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(PredicateParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotPredicate(PredicateParser.NotPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Normal}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormal(PredicateParser.NormalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionPredicate(PredicateParser.FunctionPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenPredicate(PredicateParser.ParenPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjPredicate(PredicateParser.ObjPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(PredicateParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CheckNull}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckNull(PredicateParser.CheckNullContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InComp}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInComp(PredicateParser.InCompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CountExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountExpr(PredicateParser.CountExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistExpr(PredicateParser.ExistExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LowerExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLowerExpr(PredicateParser.LowerExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UpperExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpperExpr(PredicateParser.UpperExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SubstrExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstrExpr(PredicateParser.SubstrExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExistListOr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistListOr(PredicateParser.ExistListOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExistListAnd}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistListAnd(PredicateParser.ExistListAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BitsOr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitsOr(PredicateParser.BitsOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BitsAnd}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitsAnd(PredicateParser.BitsAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpValue}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpValue(PredicateParser.ExpValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpParen}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpParen(PredicateParser.ExpParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Expr}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(PredicateParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrings(PredicateParser.StringsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumbers(PredicateParser.NumbersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleans(PredicateParser.BooleansContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNulls(PredicateParser.NullsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PredicateParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectField(PredicateParser.ObjectFieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link PredicateParser#binaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryOperator(PredicateParser.BinaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PredicateParser#unaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(PredicateParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PredicateParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(PredicateParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PredicateParser#logicalOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOperator(PredicateParser.LogicalOperatorContext ctx);
}