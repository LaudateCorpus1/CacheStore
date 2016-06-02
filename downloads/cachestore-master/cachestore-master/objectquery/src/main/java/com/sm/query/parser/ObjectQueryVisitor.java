// Generated from /Users/mhsieh/java/dev/query/objectquery/src/main/resources/ObjectQuery.g4 by ANTLR 4.5.1
package com.sm.query.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ObjectQueryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ObjectQueryVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ObjectQueryParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(ObjectQueryParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SelectStats}
	 * labeled alternative in {@link ObjectQueryParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectStats(ObjectQueryParser.SelectStatsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReplaceStats}
	 * labeled alternative in {@link ObjectQueryParser#updateStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReplaceStats(ObjectQueryParser.ReplaceStatsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InsertStats}
	 * labeled alternative in {@link ObjectQueryParser#updateStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsertStats(ObjectQueryParser.InsertStatsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignObject}
	 * labeled alternative in {@link ObjectQueryParser#assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignObject(ObjectQueryParser.AssignObjectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignStats}
	 * labeled alternative in {@link ObjectQueryParser#assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStats(ObjectQueryParser.AssignStatsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectQueryParser#whereStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereStatement(ObjectQueryParser.WhereStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SearchObjectId}
	 * labeled alternative in {@link ObjectQueryParser#searchCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchObjectId(ObjectQueryParser.SearchObjectIdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SearchRecord}
	 * labeled alternative in {@link ObjectQueryParser#searchCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchRecord(ObjectQueryParser.SearchRecordContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectQueryParser#objectField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectField(ObjectQueryParser.ObjectFieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectQueryParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(ObjectQueryParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjectAssigns}
	 * labeled alternative in {@link ObjectQueryParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectAssigns(ObjectQueryParser.ObjectAssignsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjectEmpty}
	 * labeled alternative in {@link ObjectQueryParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectEmpty(ObjectQueryParser.ObjectEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayValue}
	 * labeled alternative in {@link ObjectQueryParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayValue(ObjectQueryParser.ArrayValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayEmpty}
	 * labeled alternative in {@link ObjectQueryParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayEmpty(ObjectQueryParser.ArrayEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Strings}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrings(ObjectQueryParser.StringsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Numbers}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumbers(ObjectQueryParser.NumbersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Objects}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjects(ObjectQueryParser.ObjectsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Arrays}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrays(ObjectQueryParser.ArraysContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Booleans}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleans(ObjectQueryParser.BooleansContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Nulls}
	 * labeled alternative in {@link ObjectQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNulls(ObjectQueryParser.NullsContext ctx);
}