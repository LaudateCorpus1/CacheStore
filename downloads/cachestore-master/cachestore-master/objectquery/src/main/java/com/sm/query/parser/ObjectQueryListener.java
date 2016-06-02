// Generated from /Users/mhsieh/java/dev/query/objectquery/src/main/resources/ObjectQuery.g4 by ANTLR 4.5.1
package com.sm.query.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectQueryParser}.
 */
public interface ObjectQueryListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ObjectQueryParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(ObjectQueryParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectQueryParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(ObjectQueryParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelectStats}
	 * labeled alternative in {@link ObjectQueryParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStats(ObjectQueryParser.SelectStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelectStats}
	 * labeled alternative in {@link ObjectQueryParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStats(ObjectQueryParser.SelectStatsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReplaceStats}
	 * labeled alternative in {@link ObjectQueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void enterReplaceStats(ObjectQueryParser.ReplaceStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReplaceStats}
	 * labeled alternative in {@link ObjectQueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void exitReplaceStats(ObjectQueryParser.ReplaceStatsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InsertStats}
	 * labeled alternative in {@link ObjectQueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void enterInsertStats(ObjectQueryParser.InsertStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InsertStats}
	 * labeled alternative in {@link ObjectQueryParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void exitInsertStats(ObjectQueryParser.InsertStatsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignObject}
	 * labeled alternative in {@link ObjectQueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void enterAssignObject(ObjectQueryParser.AssignObjectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignObject}
	 * labeled alternative in {@link ObjectQueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void exitAssignObject(ObjectQueryParser.AssignObjectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignStats}
	 * labeled alternative in {@link ObjectQueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void enterAssignStats(ObjectQueryParser.AssignStatsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignStats}
	 * labeled alternative in {@link ObjectQueryParser#assignments}.
	 * @param ctx the parse tree
	 */
	void exitAssignStats(ObjectQueryParser.AssignStatsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectQueryParser#whereStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhereStatement(ObjectQueryParser.WhereStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectQueryParser#whereStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhereStatement(ObjectQueryParser.WhereStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SearchObjectId}
	 * labeled alternative in {@link ObjectQueryParser#searchCondition}.
	 * @param ctx the parse tree
	 */
	void enterSearchObjectId(ObjectQueryParser.SearchObjectIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SearchObjectId}
	 * labeled alternative in {@link ObjectQueryParser#searchCondition}.
	 * @param ctx the parse tree
	 */
	void exitSearchObjectId(ObjectQueryParser.SearchObjectIdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SearchRecord}
	 * labeled alternative in {@link ObjectQueryParser#searchCondition}.
	 * @param ctx the parse tree
	 */
	void enterSearchRecord(ObjectQueryParser.SearchRecordContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SearchRecord}
	 * labeled alternative in {@link ObjectQueryParser#searchCondition}.
	 * @param ctx the parse tree
	 */
	void exitSearchRecord(ObjectQueryParser.SearchRecordContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectQueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void enterObjectField(ObjectQueryParser.ObjectFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectQueryParser#objectField}.
	 * @param ctx the parse tree
	 */
	void exitObjectField(ObjectQueryParser.ObjectFieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link ObjectQueryParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(ObjectQueryParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ObjectQueryParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(ObjectQueryParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjectAssigns}
	 * labeled alternative in {@link ObjectQueryParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObjectAssigns(ObjectQueryParser.ObjectAssignsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjectAssigns}
	 * labeled alternative in {@link ObjectQueryParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObjectAssigns(ObjectQueryParser.ObjectAssignsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjectEmpty}
	 * labeled alternative in {@link ObjectQueryParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObjectEmpty(ObjectQueryParser.ObjectEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjectEmpty}
	 * labeled alternative in {@link ObjectQueryParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObjectEmpty(ObjectQueryParser.ObjectEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayValue}
	 * labeled alternative in {@link ObjectQueryParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArrayValue(ObjectQueryParser.ArrayValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayValue}
	 * labeled alternative in {@link ObjectQueryParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArrayValue(ObjectQueryParser.ArrayValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayEmpty}
	 * labeled alternative in {@link ObjectQueryParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArrayEmpty(ObjectQueryParser.ArrayEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayEmpty}
	 * labeled alternative in {@link ObjectQueryParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArrayEmpty(ObjectQueryParser.ArrayEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterStrings(ObjectQueryParser.StringsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitStrings(ObjectQueryParser.StringsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNumbers(ObjectQueryParser.NumbersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNumbers(ObjectQueryParser.NumbersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Objects}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterObjects(ObjectQueryParser.ObjectsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Objects}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitObjects(ObjectQueryParser.ObjectsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Arrays}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterArrays(ObjectQueryParser.ArraysContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Arrays}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitArrays(ObjectQueryParser.ArraysContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterBooleans(ObjectQueryParser.BooleansContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitBooleans(ObjectQueryParser.BooleansContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNulls(ObjectQueryParser.NullsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNulls(ObjectQueryParser.NullsContext ctx);
}