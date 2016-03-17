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
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PredicateParser}.
 */
public interface PredicateListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PredicateParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(PredicateParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link PredicateParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(PredicateParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterNotPredicate(PredicateParser.NotPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitNotPredicate(PredicateParser.NotPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Normal}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterNormal(PredicateParser.NormalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Normal}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitNormal(PredicateParser.NormalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterFunctionPredicate(PredicateParser.FunctionPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitFunctionPredicate(PredicateParser.FunctionPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterParenPredicate(PredicateParser.ParenPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitParenPredicate(PredicateParser.ParenPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterObjPredicate(PredicateParser.ObjPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjPredicate}
	 * labeled alternative in {@link PredicateParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitObjPredicate(PredicateParser.ObjPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void enterComparison(PredicateParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Comparison}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void exitComparison(PredicateParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CheckNull}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void enterCheckNull(PredicateParser.CheckNullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CheckNull}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void exitCheckNull(PredicateParser.CheckNullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InComp}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void enterInComp(PredicateParser.InCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InComp}
	 * labeled alternative in {@link PredicateParser#objectPredicate}.
	 * @param ctx the parse tree
	 */
	void exitInComp(PredicateParser.InCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CountExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterCountExpr(PredicateParser.CountExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CountExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitCountExpr(PredicateParser.CountExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterExistExpr(PredicateParser.ExistExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitExistExpr(PredicateParser.ExistExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LowerExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterLowerExpr(PredicateParser.LowerExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LowerExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitLowerExpr(PredicateParser.LowerExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UpperExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterUpperExpr(PredicateParser.UpperExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UpperExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitUpperExpr(PredicateParser.UpperExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SubstrExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterSubstrExpr(PredicateParser.SubstrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SubstrExpr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitSubstrExpr(PredicateParser.SubstrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExistListOr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterExistListOr(PredicateParser.ExistListOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExistListOr}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitExistListOr(PredicateParser.ExistListOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExistListAnd}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void enterExistListAnd(PredicateParser.ExistListAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExistListAnd}
	 * labeled alternative in {@link PredicateParser#functionalPredicate}.
	 * @param ctx the parse tree
	 */
	void exitExistListAnd(PredicateParser.ExistListAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpValue}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpValue(PredicateParser.ExpValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpValue}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpValue(PredicateParser.ExpValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpParen}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpParen(PredicateParser.ExpParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpParen}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpParen(PredicateParser.ExpParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Expr}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpr(PredicateParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Expr}
	 * labeled alternative in {@link PredicateParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpr(PredicateParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void enterStrings(PredicateParser.StringsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void exitStrings(PredicateParser.StringsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNumbers(PredicateParser.NumbersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNumbers(PredicateParser.NumbersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void enterBooleans(PredicateParser.BooleansContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void exitBooleans(PredicateParser.BooleansContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNulls(PredicateParser.NullsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link PredicateParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNulls(PredicateParser.NullsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PredicateParser#objectField}.
	 * @param ctx the parse tree
	 */
	void enterObjectField(PredicateParser.ObjectFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link PredicateParser#objectField}.
	 * @param ctx the parse tree
	 */
	void exitObjectField(PredicateParser.ObjectFieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link PredicateParser#binaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperator(PredicateParser.BinaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PredicateParser#binaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperator(PredicateParser.BinaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link PredicateParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(PredicateParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PredicateParser#unaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(PredicateParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link PredicateParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(PredicateParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PredicateParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(PredicateParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link PredicateParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOperator(PredicateParser.LogicalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PredicateParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOperator(PredicateParser.LogicalOperatorContext ctx);
}