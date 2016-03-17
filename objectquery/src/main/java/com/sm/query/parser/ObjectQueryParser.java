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
public class ObjectQueryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		BOOLEAN=18, STRING=19, ID=20, NUMBER=21, WS=22, COMMA=23;
	public static final int
		RULE_script = 0, RULE_selectStatement = 1, RULE_updateStatement = 2, RULE_assignments = 3, 
		RULE_whereStatement = 4, RULE_searchCondition = 5, RULE_objectField = 6, 
		RULE_identifier = 7, RULE_object = 8, RULE_array = 9, RULE_value = 10;
	public static final String[] ruleNames = {
		"script", "selectStatement", "updateStatement", "assignments", "whereStatement", 
		"searchCondition", "objectField", "identifier", "object", "array", "value"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'select'", "'from'", "'replace'", "'update'", "'set'", "';'", "'insert'", 
		"'='", "'where'", "'not'", "'null'", "'record#'", "'.'", "'{'", "'}'", 
		"'['", "']'", null, null, null, null, null, "','"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, "BOOLEAN", "STRING", "ID", "NUMBER", 
		"WS", "COMMA"
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
	public String getGrammarFileName() { return "ObjectQuery.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ObjectQueryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ObjectQueryParser.EOF, 0); }
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public List<UpdateStatementContext> updateStatement() {
			return getRuleContexts(UpdateStatementContext.class);
		}
		public UpdateStatementContext updateStatement(int i) {
			return getRuleContext(UpdateStatementContext.class,i);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			switch (_input.LA(1)) {
			case T__2:
			case T__3:
			case T__6:
				{
				setState(23); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(22);
					updateStatement();
					}
					}
					setState(25); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__6))) != 0) );
				}
				break;
			case T__0:
				{
				setState(27);
				selectStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(30);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterSelectStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitSelectStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitSelectStats(this);
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
			setState(32);
			match(T__0);
			setState(33);
			objectField();
			setState(38);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(34);
				match(COMMA);
				setState(35);
				objectField();
				}
				}
				setState(40);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(41);
			match(T__1);
			setState(42);
			identifier();
			setState(44);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(43);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterReplaceStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitReplaceStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitReplaceStats(this);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterInsertStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitInsertStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitInsertStats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpdateStatementContext updateStatement() throws RecognitionException {
		UpdateStatementContext _localctx = new UpdateStatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_updateStatement);
		int _la;
		try {
			setState(66);
			switch (_input.LA(1)) {
			case T__2:
			case T__3:
				_localctx = new ReplaceStatsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(46);
				_la = _input.LA(1);
				if ( !(_la==T__2 || _la==T__3) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(47);
				objectField();
				setState(48);
				match(T__4);
				setState(49);
				assignments(0);
				setState(51);
				_la = _input.LA(1);
				if (_la==T__8) {
					{
					setState(50);
					whereStatement();
					}
				}

				setState(54);
				_la = _input.LA(1);
				if (_la==T__5) {
					{
					setState(53);
					match(T__5);
					}
				}

				}
				break;
			case T__6:
				_localctx = new InsertStatsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(56);
				match(T__6);
				setState(57);
				objectField();
				setState(58);
				match(T__4);
				setState(59);
				assignments(0);
				setState(61);
				_la = _input.LA(1);
				if (_la==T__8) {
					{
					setState(60);
					whereStatement();
					}
				}

				setState(64);
				_la = _input.LA(1);
				if (_la==T__5) {
					{
					setState(63);
					match(T__5);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterAssignObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitAssignObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitAssignObject(this);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterAssignStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitAssignStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitAssignStats(this);
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
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_assignments, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new AssignObjectContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(69);
			objectField();
			setState(70);
			match(T__7);
			setState(71);
			value();
			}
			_ctx.stop = _input.LT(-1);
			setState(78);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AssignStatsContext(new AssignmentsContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_assignments);
					setState(73);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(74);
					match(COMMA);
					setState(75);
					assignments(3);
					}
					} 
				}
				setState(80);
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

	public static class WhereStatementContext extends ParserRuleContext {
		public SearchConditionContext searchCondition() {
			return getRuleContext(SearchConditionContext.class,0);
		}
		public WhereStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterWhereStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitWhereStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitWhereStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereStatementContext whereStatement() throws RecognitionException {
		WhereStatementContext _localctx = new WhereStatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_whereStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			match(T__8);
			setState(83);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(82);
				match(T__9);
				}
			}

			setState(85);
			searchCondition();
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

	public static class SearchConditionContext extends ParserRuleContext {
		public SearchConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searchCondition; }
	 
		public SearchConditionContext() { }
		public void copyFrom(SearchConditionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SearchObjectIdContext extends SearchConditionContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode STRING() { return getToken(ObjectQueryParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(ObjectQueryParser.NUMBER, 0); }
		public TerminalNode BOOLEAN() { return getToken(ObjectQueryParser.BOOLEAN, 0); }
		public SearchObjectIdContext(SearchConditionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterSearchObjectId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitSearchObjectId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitSearchObjectId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SearchRecordContext extends SearchConditionContext {
		public TerminalNode NUMBER() { return getToken(ObjectQueryParser.NUMBER, 0); }
		public SearchRecordContext(SearchConditionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterSearchRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitSearchRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitSearchRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchConditionContext searchCondition() throws RecognitionException {
		SearchConditionContext _localctx = new SearchConditionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_searchCondition);
		int _la;
		try {
			setState(94);
			switch (_input.LA(1)) {
			case ID:
				_localctx = new SearchObjectIdContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(87);
				objectField();
				setState(88);
				match(T__7);
				setState(89);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << BOOLEAN) | (1L << STRING) | (1L << NUMBER))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case T__11:
				_localctx = new SearchRecordContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				match(T__11);
				setState(92);
				match(T__7);
				setState(93);
				match(NUMBER);
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

	public static class ObjectFieldContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(ObjectQueryParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(ObjectQueryParser.ID, i);
		}
		public ObjectFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectField; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterObjectField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitObjectField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitObjectField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectFieldContext objectField() throws RecognitionException {
		ObjectFieldContext _localctx = new ObjectFieldContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_objectField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(ID);
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__12) {
				{
				{
				setState(97);
				match(T__12);
				setState(98);
				match(ID);
				}
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(ObjectQueryParser.ID, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterObjectEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitObjectEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitObjectEmpty(this);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterObjectAssigns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitObjectAssigns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitObjectAssigns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_object);
		try {
			setState(112);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new ObjectAssignsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(106);
				match(T__13);
				setState(107);
				assignments(0);
				setState(108);
				match(T__14);
				}
				break;
			case 2:
				_localctx = new ObjectEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(110);
				match(T__13);
				setState(111);
				match(T__14);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterArrayValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitArrayValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitArrayValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArrayEmptyContext extends ArrayContext {
		public ArrayEmptyContext(ArrayContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterArrayEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitArrayEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitArrayEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_array);
		int _la;
		try {
			setState(127);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new ArrayValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(114);
				match(T__15);
				setState(115);
				value();
				setState(120);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(116);
					match(COMMA);
					setState(117);
					value();
					}
					}
					setState(122);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(123);
				match(T__16);
				}
				break;
			case 2:
				_localctx = new ArrayEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(125);
				match(T__15);
				setState(126);
				match(T__16);
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
		public TerminalNode STRING() { return getToken(ObjectQueryParser.STRING, 0); }
		public StringsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterStrings(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitStrings(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitStrings(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullsContext extends ValueContext {
		public NullsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterNulls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitNulls(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitNulls(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleansContext extends ValueContext {
		public TerminalNode BOOLEAN() { return getToken(ObjectQueryParser.BOOLEAN, 0); }
		public BooleansContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterBooleans(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitBooleans(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitBooleans(this);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterObjects(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitObjects(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitObjects(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumbersContext extends ValueContext {
		public TerminalNode NUMBER() { return getToken(ObjectQueryParser.NUMBER, 0); }
		public NumbersContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterNumbers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitNumbers(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitNumbers(this);
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
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).enterArrays(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectQueryListener ) ((ObjectQueryListener)listener).exitArrays(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectQueryVisitor ) return ((ObjectQueryVisitor<? extends T>)visitor).visitArrays(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_value);
		try {
			setState(135);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				match(STRING);
				}
				break;
			case NUMBER:
				_localctx = new NumbersContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				match(NUMBER);
				}
				break;
			case T__13:
				_localctx = new ObjectsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(131);
				object();
				}
				break;
			case T__15:
				_localctx = new ArraysContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(132);
				array();
				}
				break;
			case BOOLEAN:
				_localctx = new BooleansContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(133);
				match(BOOLEAN);
				}
				break;
			case T__10:
				_localctx = new NullsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(134);
				match(T__10);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return assignments_sempred((AssignmentsContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean assignments_sempred(AssignmentsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\31\u008c\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\3\2\6\2\32\n\2\r\2\16\2\33\3\2\5\2\37\n\2\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\7\3\'\n\3\f\3\16\3*\13\3\3\3\3\3\3\3\5\3/\n\3\3\4\3\4\3\4\3"+
		"\4\3\4\5\4\66\n\4\3\4\5\49\n\4\3\4\3\4\3\4\3\4\3\4\5\4@\n\4\3\4\5\4C\n"+
		"\4\5\4E\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5O\n\5\f\5\16\5R\13\5\3"+
		"\6\3\6\5\6V\n\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7a\n\7\3\b\3\b\3"+
		"\b\7\bf\n\b\f\b\16\bi\13\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\5\ns\n\n\3"+
		"\13\3\13\3\13\3\13\7\13y\n\13\f\13\16\13|\13\13\3\13\3\13\3\13\3\13\5"+
		"\13\u0082\n\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u008a\n\f\3\f\2\3\b\r\2\4\6"+
		"\b\n\f\16\20\22\24\26\2\4\3\2\5\6\5\2\r\r\24\25\27\27\u0095\2\36\3\2\2"+
		"\2\4\"\3\2\2\2\6D\3\2\2\2\bF\3\2\2\2\nS\3\2\2\2\f`\3\2\2\2\16b\3\2\2\2"+
		"\20j\3\2\2\2\22r\3\2\2\2\24\u0081\3\2\2\2\26\u0089\3\2\2\2\30\32\5\6\4"+
		"\2\31\30\3\2\2\2\32\33\3\2\2\2\33\31\3\2\2\2\33\34\3\2\2\2\34\37\3\2\2"+
		"\2\35\37\5\4\3\2\36\31\3\2\2\2\36\35\3\2\2\2\37 \3\2\2\2 !\7\2\2\3!\3"+
		"\3\2\2\2\"#\7\3\2\2#(\5\16\b\2$%\7\31\2\2%\'\5\16\b\2&$\3\2\2\2\'*\3\2"+
		"\2\2(&\3\2\2\2()\3\2\2\2)+\3\2\2\2*(\3\2\2\2+,\7\4\2\2,.\5\20\t\2-/\5"+
		"\n\6\2.-\3\2\2\2./\3\2\2\2/\5\3\2\2\2\60\61\t\2\2\2\61\62\5\16\b\2\62"+
		"\63\7\7\2\2\63\65\5\b\5\2\64\66\5\n\6\2\65\64\3\2\2\2\65\66\3\2\2\2\66"+
		"8\3\2\2\2\679\7\b\2\28\67\3\2\2\289\3\2\2\29E\3\2\2\2:;\7\t\2\2;<\5\16"+
		"\b\2<=\7\7\2\2=?\5\b\5\2>@\5\n\6\2?>\3\2\2\2?@\3\2\2\2@B\3\2\2\2AC\7\b"+
		"\2\2BA\3\2\2\2BC\3\2\2\2CE\3\2\2\2D\60\3\2\2\2D:\3\2\2\2E\7\3\2\2\2FG"+
		"\b\5\1\2GH\5\16\b\2HI\7\n\2\2IJ\5\26\f\2JP\3\2\2\2KL\f\4\2\2LM\7\31\2"+
		"\2MO\5\b\5\5NK\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\t\3\2\2\2RP\3\2"+
		"\2\2SU\7\13\2\2TV\7\f\2\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\5\f\7\2X\13"+
		"\3\2\2\2YZ\5\16\b\2Z[\7\n\2\2[\\\t\3\2\2\\a\3\2\2\2]^\7\16\2\2^_\7\n\2"+
		"\2_a\7\27\2\2`Y\3\2\2\2`]\3\2\2\2a\r\3\2\2\2bg\7\26\2\2cd\7\17\2\2df\7"+
		"\26\2\2ec\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2h\17\3\2\2\2ig\3\2\2\2"+
		"jk\7\26\2\2k\21\3\2\2\2lm\7\20\2\2mn\5\b\5\2no\7\21\2\2os\3\2\2\2pq\7"+
		"\20\2\2qs\7\21\2\2rl\3\2\2\2rp\3\2\2\2s\23\3\2\2\2tu\7\22\2\2uz\5\26\f"+
		"\2vw\7\31\2\2wy\5\26\f\2xv\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{}\3\2"+
		"\2\2|z\3\2\2\2}~\7\23\2\2~\u0082\3\2\2\2\177\u0080\7\22\2\2\u0080\u0082"+
		"\7\23\2\2\u0081t\3\2\2\2\u0081\177\3\2\2\2\u0082\25\3\2\2\2\u0083\u008a"+
		"\7\25\2\2\u0084\u008a\7\27\2\2\u0085\u008a\5\22\n\2\u0086\u008a\5\24\13"+
		"\2\u0087\u008a\7\24\2\2\u0088\u008a\7\r\2\2\u0089\u0083\3\2\2\2\u0089"+
		"\u0084\3\2\2\2\u0089\u0085\3\2\2\2\u0089\u0086\3\2\2\2\u0089\u0087\3\2"+
		"\2\2\u0089\u0088\3\2\2\2\u008a\27\3\2\2\2\23\33\36(.\658?BDPU`grz\u0081"+
		"\u0089";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}