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
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, BOOLEAN=34, STRING=35, ID=36, NUMBER=37, LPAREN=38, 
		RPAREN=39, WS=40, COMMA=41;
	public static final int
		RULE_script = 0, RULE_selectStatement = 1, RULE_updateStatement = 2, RULE_whereStatement = 3, 
		RULE_predicate = 4, RULE_objectPredicate = 5, RULE_expression = 6, RULE_functionalExpression = 7, 
		RULE_assignments = 8, RULE_objectField = 9, RULE_identifier = 10, RULE_value = 11, 
		RULE_object = 12, RULE_array = 13, RULE_binaryOperator = 14, RULE_unaryOperator = 15, 
		RULE_comparisonOperator = 16, RULE_logicalOperator = 17;
	public static final String[] ruleNames = {
		"script", "selectStatement", "updateStatement", "whereStatement", "predicate", 
		"objectPredicate", "expression", "functionalExpression", "assignments", 
		"objectField", "identifier", "value", "object", "array", "binaryOperator", 
		"unaryOperator", "comparisonOperator", "logicalOperator"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'select'", "'from'", "'replace'", "'set'", "'insert'", "'where'", 
		"'not'", "'in'", "'lower'", "'upper'", "'count'", "'substr'", "'exist'", 
		"'='", "'key#'", "'*'", "'.'", "'null'", "'{'", "'}'", "'['", "']'", "'+'", 
		"'-'", "'/'", "'%'", "'!='", "'<'", "'<='", "'>'", "'>='", "'and'", "'or'", 
		null, null, null, null, "'('", "')'", null, "','"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "BOOLEAN", 
		"STRING", "ID", "NUMBER", "LPAREN", "RPAREN", "WS", "COMMA"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Query.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QueryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(QueryParser.EOF, 0); }
		public UpdateStatementContext updateStatement() {
			return getRuleContext(UpdateStatementContext.class,0);
		}
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			switch (_input.LA(1)) {
			case T__2:
			case T__4:
				{
				setState(36);
				updateStatement();
				}
				break;
			case T__0:
				{
				setState(37);
				selectStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(40);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectStatementContext extends ParserRuleContext {
		public SelectStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStatement; }
	 
		public SelectStatementContext() { }
		public void copyFrom(SelectStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SelectStatsContext extends SelectStatementContext {
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public WhereStatementContext whereStatement() {
			return getRuleContext(WhereStatementContext.class,0);
		}
		public SelectStatsContext(SelectStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterSelectStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitSelectStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitSelectStats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectStatementContext selectStatement() throws RecognitionException {
		SelectStatementContext _localctx = new SelectStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectStatement);
		int _la;
		try {
			_localctx = new SelectStatsContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(T__0);
			setState(43);
			objectField();
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(44);
				match(COMMA);
				setState(45);
				objectField();
				}
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(51);
			match(T__1);
			setState(52);
			identifier();
			setState(54);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(53);
				whereStatement();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UpdateStatementContext extends ParserRuleContext {
		public UpdateStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_updateStatement; }
	 
		public UpdateStatementContext() { }
		public void copyFrom(UpdateStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ReplaceStatsContext extends UpdateStatementContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public AssignmentsContext assignments() {
			return getRuleContext(AssignmentsContext.class,0);
		}
		public WhereStatementContext whereStatement() {
			return getRuleContext(WhereStatementContext.class,0);
		}
		public ReplaceStatsContext(UpdateStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterReplaceStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitReplaceStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitReplaceStats(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InsertStatsContext extends UpdateStatementContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public AssignmentsContext assignments() {
			return getRuleContext(AssignmentsContext.class,0);
		}
		public WhereStatementContext whereStatement() {
			return getRuleContext(WhereStatementContext.class,0);
		}
		public InsertStatsContext(UpdateStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterInsertStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitInsertStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitInsertStats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpdateStatementContext updateStatement() throws RecognitionException {
		UpdateStatementContext _localctx = new UpdateStatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_updateStatement);
		int _la;
		try {
			setState(70);
			switch (_input.LA(1)) {
			case T__2:
				_localctx = new ReplaceStatsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				match(T__2);
				setState(57);
				objectField();
				setState(58);
				match(T__3);
				setState(59);
				assignments(0);
				setState(61);
				_la = _input.LA(1);
				if (_la==T__5) {
					{
					setState(60);
					whereStatement();
					}
				}

				}
				break;
			case T__4:
				_localctx = new InsertStatsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(63);
				match(T__4);
				setState(64);
				objectField();
				setState(65);
				match(T__3);
				setState(66);
				assignments(0);
				setState(68);
				_la = _input.LA(1);
				if (_la==T__5) {
					{
					setState(67);
					whereStatement();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereStatementContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public WhereStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterWhereStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitWhereStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitWhereStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereStatementContext whereStatement() throws RecognitionException {
		WhereStatementContext _localctx = new WhereStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_whereStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(T__5);
			setState(74);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(73);
				match(T__6);
				}
				break;
			}
			setState(76);
			predicate(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	 
		public PredicateContext() { }
		public void copyFrom(PredicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ParenPredicateContext extends PredicateContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public ParenPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterParenPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitParenPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitParenPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicPredicateContext extends PredicateContext {
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public LogicalOperatorContext logicalOperator() {
			return getRuleContext(LogicalOperatorContext.class,0);
		}
		public LogicPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterLogicPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitLogicPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitLogicPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ObjPredicateContext extends PredicateContext {
		public ObjectPredicateContext objectPredicate() {
			return getRuleContext(ObjectPredicateContext.class,0);
		}
		public ObjPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterObjPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitObjPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitObjPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		return predicate(0);
	}

	private PredicateContext predicate(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PredicateContext _localctx = new PredicateContext(_ctx, _parentState);
		PredicateContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_predicate, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				_localctx = new ParenPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(79);
				match(LPAREN);
				setState(80);
				predicate(0);
				setState(81);
				match(RPAREN);
				}
				break;
			case 2:
				{
				_localctx = new ObjPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(84);
				_la = _input.LA(1);
				if (_la==T__6) {
					{
					setState(83);
					match(T__6);
					}
				}

				setState(86);
				objectPredicate();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(95);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new LogicPredicateContext(new PredicateContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_predicate);
					setState(89);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(90);
					logicalOperator();
					setState(91);
					predicate(4);
					}
					} 
				}
				setState(97);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ObjectPredicateContext extends ParserRuleContext {
		public ObjectPredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectPredicate; }
	 
		public ObjectPredicateContext() { }
		public void copyFrom(ObjectPredicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExpressionPredicateContext extends ObjectPredicateContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public ExpressionPredicateContext(ObjectPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterExpressionPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitExpressionPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitExpressionPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InCompContext extends ObjectPredicateContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(QueryParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(QueryParser.COMMA, i);
		}
		public InCompContext(ObjectPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterInComp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitInComp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitInComp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ComparisonContext extends ObjectPredicateContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ComparisonContext(ObjectPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitComparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectPredicateContext objectPredicate() throws RecognitionException {
		ObjectPredicateContext _localctx = new ObjectPredicateContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_objectPredicate);
		int _la;
		try {
			setState(119);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new ComparisonContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				objectField();
				setState(99);
				comparisonOperator();
				setState(100);
				expression(0);
				}
				break;
			case 2:
				_localctx = new InCompContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
				objectField();
				setState(103);
				match(T__7);
				setState(104);
				match(LPAREN);
				setState(105);
				expression(0);
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(106);
					match(COMMA);
					setState(107);
					expression(0);
					}
					}
					setState(112);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(113);
				match(RPAREN);
				}
				break;
			case 3:
				_localctx = new ExpressionPredicateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(115);
				expression(0);
				setState(116);
				comparisonOperator();
				setState(117);
				expression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExpValuesContext extends ExpressionContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ExpValuesContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterExpValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitExpValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitExpValues(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpParenContext extends ExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public ExpParenContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterExpParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitExpParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitExpParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public BinaryOperatorContext binaryOperator() {
			return getRuleContext(BinaryOperatorContext.class,0);
		}
		public ExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionExpContext extends ExpressionContext {
		public FunctionalExpressionContext functionalExpression() {
			return getRuleContext(FunctionalExpressionContext.class,0);
		}
		public FunctionExpContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterFunctionExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitFunctionExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitFunctionExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new ExpParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(122);
				match(LPAREN);
				setState(123);
				expression(0);
				setState(124);
				match(RPAREN);
				}
				break;
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
				{
				_localctx = new FunctionExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(126);
				functionalExpression();
				}
				break;
			case T__17:
			case T__18:
			case T__20:
			case BOOLEAN:
			case STRING:
			case NUMBER:
				{
				_localctx = new ExpValuesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(127);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(136);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(new ExpressionContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expression);
					setState(130);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(131);
					binaryOperator();
					setState(132);
					expression(5);
					}
					} 
				}
				setState(138);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FunctionalExpressionContext extends ParserRuleContext {
		public FunctionalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionalExpression; }
	 
		public FunctionalExpressionContext() { }
		public void copyFrom(FunctionalExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class UpperExprContext extends FunctionalExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public UpperExprContext(FunctionalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterUpperExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitUpperExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitUpperExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LowerExprContext extends FunctionalExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public LowerExprContext(FunctionalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterLowerExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitLowerExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitLowerExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CountExprContext extends FunctionalExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public CountExprContext(FunctionalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterCountExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitCountExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitCountExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExistExprContext extends FunctionalExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public ExistExprContext(FunctionalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterExistExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitExistExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitExistExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SubstrExprContext extends FunctionalExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public SubstrExprContext(FunctionalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterSubstrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitSubstrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitSubstrExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionalExpressionContext functionalExpression() throws RecognitionException {
		FunctionalExpressionContext _localctx = new FunctionalExpressionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_functionalExpression);
		int _la;
		try {
			setState(180);
			switch (_input.LA(1)) {
			case T__8:
				_localctx = new LowerExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(139);
				match(T__8);
				setState(140);
				match(LPAREN);
				setState(141);
				objectField();
				setState(142);
				match(RPAREN);
				}
				break;
			case T__9:
				_localctx = new UpperExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				match(T__9);
				setState(145);
				match(LPAREN);
				setState(146);
				objectField();
				setState(147);
				match(RPAREN);
				}
				break;
			case T__10:
				_localctx = new CountExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(149);
				match(T__10);
				setState(150);
				match(LPAREN);
				setState(151);
				objectField();
				setState(152);
				match(COMMA);
				setState(156);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << ID))) != 0)) {
					{
					setState(153);
					objectField();
					setState(154);
					match(COMMA);
					}
				}

				setState(158);
				expression(0);
				setState(159);
				match(RPAREN);
				}
				break;
			case T__11:
				_localctx = new SubstrExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(161);
				match(T__11);
				setState(162);
				match(LPAREN);
				setState(163);
				objectField();
				setState(164);
				match(COMMA);
				setState(165);
				expression(0);
				setState(166);
				match(RPAREN);
				}
				break;
			case T__12:
				_localctx = new ExistExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(168);
				match(T__12);
				setState(169);
				match(LPAREN);
				setState(170);
				objectField();
				setState(171);
				match(COMMA);
				setState(175);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << ID))) != 0)) {
					{
					setState(172);
					objectField();
					setState(173);
					match(COMMA);
					}
				}

				setState(177);
				expression(0);
				setState(178);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentsContext extends ParserRuleContext {
		public AssignmentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignments; }
	 
		public AssignmentsContext() { }
		public void copyFrom(AssignmentsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AssignObjectContext extends AssignmentsContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public AssignObjectContext(AssignmentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterAssignObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitAssignObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitAssignObject(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignStatsContext extends AssignmentsContext {
		public List<AssignmentsContext> assignments() {
			return getRuleContexts(AssignmentsContext.class);
		}
		public AssignmentsContext assignments(int i) {
			return getRuleContext(AssignmentsContext.class,i);
		}
		public AssignStatsContext(AssignmentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterAssignStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitAssignStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitAssignStats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentsContext assignments() throws RecognitionException {
		return assignments(0);
	}

	private AssignmentsContext assignments(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AssignmentsContext _localctx = new AssignmentsContext(_ctx, _parentState);
		AssignmentsContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_assignments, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new AssignObjectContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(183);
			objectField();
			setState(184);
			match(T__13);
			setState(185);
			value();
			}
			_ctx.stop = _input.LT(-1);
			setState(192);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AssignStatsContext(new AssignmentsContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_assignments);
					setState(187);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(188);
					match(COMMA);
					setState(189);
					assignments(3);
					}
					} 
				}
				setState(194);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ObjectFieldContext extends ParserRuleContext {
		public ObjectFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectField; }
	 
		public ObjectFieldContext() { }
		public void copyFrom(ObjectFieldContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IDSContext extends ObjectFieldContext {
		public List<TerminalNode> ID() { return getTokens(QueryParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(QueryParser.ID, i);
		}
		public IDSContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterIDS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitIDS(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitIDS(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AllContext extends ObjectFieldContext {
		public AllContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitAll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitAll(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class KeysContext extends ObjectFieldContext {
		public KeysContext(ObjectFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterKeys(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitKeys(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitKeys(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectFieldContext objectField() throws RecognitionException {
		ObjectFieldContext _localctx = new ObjectFieldContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_objectField);
		int _la;
		try {
			setState(205);
			switch (_input.LA(1)) {
			case T__14:
				_localctx = new KeysContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(195);
				match(T__14);
				}
				break;
			case T__15:
				_localctx = new AllContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
				match(T__15);
				}
				break;
			case ID:
				_localctx = new IDSContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(197);
				match(ID);
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__16) {
					{
					{
					setState(198);
					match(T__16);
					setState(199);
					match(ID);
					}
					}
					setState(204);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(QueryParser.ID, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
	 
		public ValueContext() { }
		public void copyFrom(ValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StringsContext extends ValueContext {
		public TerminalNode STRING() { return getToken(QueryParser.STRING, 0); }
		public StringsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterStrings(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitStrings(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitStrings(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullsContext extends ValueContext {
		public NullsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterNulls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitNulls(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitNulls(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleansContext extends ValueContext {
		public TerminalNode BOOLEAN() { return getToken(QueryParser.BOOLEAN, 0); }
		public BooleansContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterBooleans(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitBooleans(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitBooleans(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ObjectsContext extends ValueContext {
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public ObjectsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterObjects(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitObjects(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitObjects(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumbersContext extends ValueContext {
		public TerminalNode NUMBER() { return getToken(QueryParser.NUMBER, 0); }
		public NumbersContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterNumbers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitNumbers(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitNumbers(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArraysContext extends ValueContext {
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public ArraysContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterArrays(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitArrays(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitArrays(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_value);
		try {
			setState(215);
			switch (_input.LA(1)) {
			case T__18:
				_localctx = new ObjectsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				object();
				}
				break;
			case T__20:
				_localctx = new ArraysContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(210);
				array();
				}
				break;
			case STRING:
				_localctx = new StringsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(211);
				match(STRING);
				}
				break;
			case NUMBER:
				_localctx = new NumbersContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(212);
				match(NUMBER);
				}
				break;
			case BOOLEAN:
				_localctx = new BooleansContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(213);
				match(BOOLEAN);
				}
				break;
			case T__17:
				_localctx = new NullsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(214);
				match(T__17);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjectContext extends ParserRuleContext {
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
	 
		public ObjectContext() { }
		public void copyFrom(ObjectContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ObjectEmptyContext extends ObjectContext {
		public ObjectEmptyContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterObjectEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitObjectEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitObjectEmpty(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ObjectAssignsContext extends ObjectContext {
		public AssignmentsContext assignments() {
			return getRuleContext(AssignmentsContext.class,0);
		}
		public ObjectAssignsContext(ObjectContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterObjectAssigns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitObjectAssigns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitObjectAssigns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_object);
		try {
			setState(223);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new ObjectAssignsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(217);
				match(T__18);
				setState(218);
				assignments(0);
				setState(219);
				match(T__19);
				}
				break;
			case 2:
				_localctx = new ObjectEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(221);
				match(T__18);
				setState(222);
				match(T__19);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayContext extends ParserRuleContext {
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
	 
		public ArrayContext() { }
		public void copyFrom(ArrayContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArrayValueContext extends ArrayContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public ArrayValueContext(ArrayContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterArrayValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitArrayValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitArrayValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArrayEmptyContext extends ArrayContext {
		public ArrayEmptyContext(ArrayContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterArrayEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitArrayEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitArrayEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_array);
		int _la;
		try {
			setState(238);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				_localctx = new ArrayValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				match(T__20);
				setState(226);
				value();
				setState(231);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(227);
					match(COMMA);
					setState(228);
					value();
					}
					}
					setState(233);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(234);
				match(T__21);
				}
				break;
			case 2:
				_localctx = new ArrayEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(236);
				match(T__20);
				setState(237);
				match(T__21);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BinaryOperatorContext extends ParserRuleContext {
		public BinaryOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binaryOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterBinaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitBinaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitBinaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinaryOperatorContext binaryOperator() throws RecognitionException {
		BinaryOperatorContext _localctx = new BinaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_binaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__15) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryOperatorContext extends ParserRuleContext {
		public UnaryOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterUnaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitUnaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitUnaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryOperatorContext unaryOperator() throws RecognitionException {
		UnaryOperatorContext _localctx = new UnaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			_la = _input.LA(1);
			if ( !(_la==T__22 || _la==T__23) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonOperatorContext extends ParserRuleContext {
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__26) | (1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LogicalOperatorContext extends ParserRuleContext {
		public LogicalOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterLogicalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitLogicalOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitLogicalOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalOperatorContext logicalOperator() throws RecognitionException {
		LogicalOperatorContext _localctx = new LogicalOperatorContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_logicalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			_la = _input.LA(1);
			if ( !(_la==T__31 || _la==T__32) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 4:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 6:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 8:
			return assignments_sempred((AssignmentsContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean assignments_sempred(AssignmentsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3+\u00fb\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\3\2\5\2)\n\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3\61\n\3\f\3\16"+
		"\3\64\13\3\3\3\3\3\3\3\5\39\n\3\3\4\3\4\3\4\3\4\3\4\5\4@\n\4\3\4\3\4\3"+
		"\4\3\4\3\4\5\4G\n\4\5\4I\n\4\3\5\3\5\5\5M\n\5\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\5\6W\n\6\3\6\5\6Z\n\6\3\6\3\6\3\6\3\6\7\6`\n\6\f\6\16\6c\13\6"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7o\n\7\f\7\16\7r\13\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\5\7z\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u0083\n\b"+
		"\3\b\3\b\3\b\3\b\7\b\u0089\n\b\f\b\16\b\u008c\13\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u009f\n\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u00b2"+
		"\n\t\3\t\3\t\3\t\5\t\u00b7\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u00c1"+
		"\n\n\f\n\16\n\u00c4\13\n\3\13\3\13\3\13\3\13\3\13\7\13\u00cb\n\13\f\13"+
		"\16\13\u00ce\13\13\5\13\u00d0\n\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\5\r"+
		"\u00da\n\r\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00e2\n\16\3\17\3\17\3\17"+
		"\3\17\7\17\u00e8\n\17\f\17\16\17\u00eb\13\17\3\17\3\17\3\17\3\17\5\17"+
		"\u00f1\n\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\23\2\5\n\16\22\24"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$\2\6\4\2\22\22\31\34\3\2\31"+
		"\32\4\2\20\20\35!\3\2\"#\u010a\2(\3\2\2\2\4,\3\2\2\2\6H\3\2\2\2\bJ\3\2"+
		"\2\2\nY\3\2\2\2\fy\3\2\2\2\16\u0082\3\2\2\2\20\u00b6\3\2\2\2\22\u00b8"+
		"\3\2\2\2\24\u00cf\3\2\2\2\26\u00d1\3\2\2\2\30\u00d9\3\2\2\2\32\u00e1\3"+
		"\2\2\2\34\u00f0\3\2\2\2\36\u00f2\3\2\2\2 \u00f4\3\2\2\2\"\u00f6\3\2\2"+
		"\2$\u00f8\3\2\2\2&)\5\6\4\2\')\5\4\3\2(&\3\2\2\2(\'\3\2\2\2)*\3\2\2\2"+
		"*+\7\2\2\3+\3\3\2\2\2,-\7\3\2\2-\62\5\24\13\2./\7+\2\2/\61\5\24\13\2\60"+
		".\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63\65\3\2\2\2\64\62"+
		"\3\2\2\2\65\66\7\4\2\2\668\5\26\f\2\679\5\b\5\28\67\3\2\2\289\3\2\2\2"+
		"9\5\3\2\2\2:;\7\5\2\2;<\5\24\13\2<=\7\6\2\2=?\5\22\n\2>@\5\b\5\2?>\3\2"+
		"\2\2?@\3\2\2\2@I\3\2\2\2AB\7\7\2\2BC\5\24\13\2CD\7\6\2\2DF\5\22\n\2EG"+
		"\5\b\5\2FE\3\2\2\2FG\3\2\2\2GI\3\2\2\2H:\3\2\2\2HA\3\2\2\2I\7\3\2\2\2"+
		"JL\7\b\2\2KM\7\t\2\2LK\3\2\2\2LM\3\2\2\2MN\3\2\2\2NO\5\n\6\2O\t\3\2\2"+
		"\2PQ\b\6\1\2QR\7(\2\2RS\5\n\6\2ST\7)\2\2TZ\3\2\2\2UW\7\t\2\2VU\3\2\2\2"+
		"VW\3\2\2\2WX\3\2\2\2XZ\5\f\7\2YP\3\2\2\2YV\3\2\2\2Za\3\2\2\2[\\\f\5\2"+
		"\2\\]\5$\23\2]^\5\n\6\6^`\3\2\2\2_[\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2"+
		"\2\2b\13\3\2\2\2ca\3\2\2\2de\5\24\13\2ef\5\"\22\2fg\5\16\b\2gz\3\2\2\2"+
		"hi\5\24\13\2ij\7\n\2\2jk\7(\2\2kp\5\16\b\2lm\7+\2\2mo\5\16\b\2nl\3\2\2"+
		"\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rp\3\2\2\2st\7)\2\2tz\3\2\2"+
		"\2uv\5\16\b\2vw\5\"\22\2wx\5\16\b\2xz\3\2\2\2yd\3\2\2\2yh\3\2\2\2yu\3"+
		"\2\2\2z\r\3\2\2\2{|\b\b\1\2|}\7(\2\2}~\5\16\b\2~\177\7)\2\2\177\u0083"+
		"\3\2\2\2\u0080\u0083\5\20\t\2\u0081\u0083\5\30\r\2\u0082{\3\2\2\2\u0082"+
		"\u0080\3\2\2\2\u0082\u0081\3\2\2\2\u0083\u008a\3\2\2\2\u0084\u0085\f\6"+
		"\2\2\u0085\u0086\5\36\20\2\u0086\u0087\5\16\b\7\u0087\u0089\3\2\2\2\u0088"+
		"\u0084\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2"+
		"\2\2\u008b\17\3\2\2\2\u008c\u008a\3\2\2\2\u008d\u008e\7\13\2\2\u008e\u008f"+
		"\7(\2\2\u008f\u0090\5\24\13\2\u0090\u0091\7)\2\2\u0091\u00b7\3\2\2\2\u0092"+
		"\u0093\7\f\2\2\u0093\u0094\7(\2\2\u0094\u0095\5\24\13\2\u0095\u0096\7"+
		")\2\2\u0096\u00b7\3\2\2\2\u0097\u0098\7\r\2\2\u0098\u0099\7(\2\2\u0099"+
		"\u009a\5\24\13\2\u009a\u009e\7+\2\2\u009b\u009c\5\24\13\2\u009c\u009d"+
		"\7+\2\2\u009d\u009f\3\2\2\2\u009e\u009b\3\2\2\2\u009e\u009f\3\2\2\2\u009f"+
		"\u00a0\3\2\2\2\u00a0\u00a1\5\16\b\2\u00a1\u00a2\7)\2\2\u00a2\u00b7\3\2"+
		"\2\2\u00a3\u00a4\7\16\2\2\u00a4\u00a5\7(\2\2\u00a5\u00a6\5\24\13\2\u00a6"+
		"\u00a7\7+\2\2\u00a7\u00a8\5\16\b\2\u00a8\u00a9\7)\2\2\u00a9\u00b7\3\2"+
		"\2\2\u00aa\u00ab\7\17\2\2\u00ab\u00ac\7(\2\2\u00ac\u00ad\5\24\13\2\u00ad"+
		"\u00b1\7+\2\2\u00ae\u00af\5\24\13\2\u00af\u00b0\7+\2\2\u00b0\u00b2\3\2"+
		"\2\2\u00b1\u00ae\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3"+
		"\u00b4\5\16\b\2\u00b4\u00b5\7)\2\2\u00b5\u00b7\3\2\2\2\u00b6\u008d\3\2"+
		"\2\2\u00b6\u0092\3\2\2\2\u00b6\u0097\3\2\2\2\u00b6\u00a3\3\2\2\2\u00b6"+
		"\u00aa\3\2\2\2\u00b7\21\3\2\2\2\u00b8\u00b9\b\n\1\2\u00b9\u00ba\5\24\13"+
		"\2\u00ba\u00bb\7\20\2\2\u00bb\u00bc\5\30\r\2\u00bc\u00c2\3\2\2\2\u00bd"+
		"\u00be\f\4\2\2\u00be\u00bf\7+\2\2\u00bf\u00c1\5\22\n\5\u00c0\u00bd\3\2"+
		"\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3"+
		"\23\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00d0\7\21\2\2\u00c6\u00d0\7\22"+
		"\2\2\u00c7\u00cc\7&\2\2\u00c8\u00c9\7\23\2\2\u00c9\u00cb\7&\2\2\u00ca"+
		"\u00c8\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2"+
		"\2\2\u00cd\u00d0\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00c5\3\2\2\2\u00cf"+
		"\u00c6\3\2\2\2\u00cf\u00c7\3\2\2\2\u00d0\25\3\2\2\2\u00d1\u00d2\7&\2\2"+
		"\u00d2\27\3\2\2\2\u00d3\u00da\5\32\16\2\u00d4\u00da\5\34\17\2\u00d5\u00da"+
		"\7%\2\2\u00d6\u00da\7\'\2\2\u00d7\u00da\7$\2\2\u00d8\u00da\7\24\2\2\u00d9"+
		"\u00d3\3\2\2\2\u00d9\u00d4\3\2\2\2\u00d9\u00d5\3\2\2\2\u00d9\u00d6\3\2"+
		"\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00d8\3\2\2\2\u00da\31\3\2\2\2\u00db\u00dc"+
		"\7\25\2\2\u00dc\u00dd\5\22\n\2\u00dd\u00de\7\26\2\2\u00de\u00e2\3\2\2"+
		"\2\u00df\u00e0\7\25\2\2\u00e0\u00e2\7\26\2\2\u00e1\u00db\3\2\2\2\u00e1"+
		"\u00df\3\2\2\2\u00e2\33\3\2\2\2\u00e3\u00e4\7\27\2\2\u00e4\u00e9\5\30"+
		"\r\2\u00e5\u00e6\7+\2\2\u00e6\u00e8\5\30\r\2\u00e7\u00e5\3\2\2\2\u00e8"+
		"\u00eb\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00ec\3\2"+
		"\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ed\7\30\2\2\u00ed\u00f1\3\2\2\2\u00ee"+
		"\u00ef\7\27\2\2\u00ef\u00f1\7\30\2\2\u00f0\u00e3\3\2\2\2\u00f0\u00ee\3"+
		"\2\2\2\u00f1\35\3\2\2\2\u00f2\u00f3\t\2\2\2\u00f3\37\3\2\2\2\u00f4\u00f5"+
		"\t\3\2\2\u00f5!\3\2\2\2\u00f6\u00f7\t\4\2\2\u00f7#\3\2\2\2\u00f8\u00f9"+
		"\t\5\2\2\u00f9%\3\2\2\2\32(\628?FHLVYapy\u0082\u008a\u009e\u00b1\u00b6"+
		"\u00c2\u00cc\u00cf\u00d9\u00e1\u00e9\u00f0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}