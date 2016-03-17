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
package com.sm.query.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QueryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QueryVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QueryParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(QueryParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SelectStats}
	 * labeled alternative in {@link QueryParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectStats(QueryParser.SelectStatsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReplaceStats}
	 * labeled alternative in {@link QueryParser#updateStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReplaceStats(QueryParser.ReplaceStatsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InsertStats}
	 * labeled alternative in {@link QueryParser#updateStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsertStats(QueryParser.InsertStatsContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#whereStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereStatement(QueryParser.WhereStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicPredicate(QueryParser.LogicPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenPredicate(QueryParser.ParenPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjPredicate}
	 * labeled alternative in {@link QueryParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjPredicate(QueryParser.ObjPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(QueryParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InComp}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInComp(QueryParser.InCompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionPredicate}
	 * labeled alternative in {@link QueryParser#objectPredicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionPredicate(QueryParser.ExpressionPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpValues}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpValues(QueryParser.ExpValuesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpParen}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpParen(QueryParser.ExpParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Expr}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(QueryParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionExp}
	 * labeled alternative in {@link QueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionExp(QueryParser.FunctionExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LowerExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLowerExpr(QueryParser.LowerExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UpperExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpperExpr(QueryParser.UpperExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CountExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountExpr(QueryParser.CountExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SubstrExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstrExpr(QueryParser.SubstrExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link QueryParser#functionalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistExpr(QueryParser.ExistExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignObject}
	 * labeled alternative in {@link QueryParser#assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignObject(QueryParser.AssignObjectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignStats}
	 * labeled alternative in {@link QueryParser#assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStats(QueryParser.AssignStatsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Keys}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeys(QueryParser.KeysContext ctx);
	/**
	 * Visit a parse tree produced by the {@code All}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAll(QueryParser.AllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IDS}
	 * labeled alternative in {@link QueryParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIDS(QueryParser.IDSContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(QueryParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Objects}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjects(QueryParser.ObjectsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Arrays}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrays(QueryParser.ArraysContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrings(QueryParser.StringsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumbers(QueryParser.NumbersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleans(QueryParser.BooleansContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link QueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNulls(QueryParser.NullsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjectAssigns}
	 * labeled alternative in {@link QueryParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectAssigns(QueryParser.ObjectAssignsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjectEmpty}
	 * labeled alternative in {@link QueryParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectEmpty(QueryParser.ObjectEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayValue}
	 * labeled alternative in {@link QueryParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayValue(QueryParser.ArrayValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayEmpty}
	 * labeled alternative in {@link QueryParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayEmpty(QueryParser.ArrayEmptyContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#binaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryOperator(QueryParser.BinaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#unaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(QueryParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(QueryParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryParser#logicalOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOperator(QueryParser.LogicalOperatorContext ctx);
}