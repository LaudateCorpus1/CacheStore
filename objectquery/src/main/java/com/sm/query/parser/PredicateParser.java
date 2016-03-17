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
public class PredicateParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, BOOLEAN=26, STRING=27, ID=28, NUMBER=29, LPAREN=30, RPAREN=31, 
		WS=32, COMMA=33;
	public static final int
		RULE_script = 0, RULE_predicate = 1, RULE_objectPredicate = 2, RULE_functionalPredicate = 3, 
		RULE_expression = 4, RULE_value = 5, RULE_objectField = 6, RULE_binaryOperator = 7, 
		RULE_unaryOperator = 8, RULE_comparisonOperator = 9, RULE_logicalOperator = 10;
	public static final String[] ruleNames = {
		"script", "predicate", "objectPredicate", "functionalPredicate", "expression", 
		"value", "objectField", "binaryOperator", "unaryOperator", "comparisonOperator", 
		"logicalOperator"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'not'", "'is'", "'null'", "'in'", "'count'", "'exist'", "'lower'", 
		"'upper'", "'substr'", "'existListOr'", "'existListAnd'", "'.'", "'+'", 
		"'-'", "'*'", "'/'", "'%'", "'='", "'!='", "'<'", "'<='", "'>'", "'>='", 
		"'and'", "'or'", null, null, null, null, "'('", "')'", null, "','"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "BOOLEAN", "STRING", "ID", "NUMBER", "LPAREN", "RPAREN", "WS", 
		"COMMA"
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
	public String getGrammarFileName() { return "Predicate.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PredicateParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(PredicateParser.EOF, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitScript(this);
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
			setState(23);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << ID) | (1L << LPAREN))) != 0)) {
				{
				setState(22);
				predicate(0);
				}
			}

			setState(25);
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
	public static class NotPredicateContext extends PredicateContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public NotPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterNotPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitNotPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitNotPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NormalContext extends PredicateContext {
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public LogicalOperatorContext logicalOperator() {
			return getRuleContext(LogicalOperatorContext.class,0);
		}
		public NormalContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitNormal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenPredicateContext extends PredicateContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ParenPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterParenPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitParenPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitParenPredicate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionPredicateContext extends PredicateContext {
		public FunctionalPredicateContext functionalPredicate() {
			return getRuleContext(FunctionalPredicateContext.class,0);
		}
		public FunctionPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterFunctionPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitFunctionPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitFunctionPredicate(this);
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterObjPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitObjPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitObjPredicate(this);
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
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_predicate, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			switch (_input.LA(1)) {
			case T__0:
				{
				_localctx = new NotPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(28);
				match(T__0);
				setState(29);
				predicate(3);
				}
				break;
			case LPAREN:
				{
				_localctx = new ParenPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30);
				match(LPAREN);
				setState(31);
				predicate(0);
				setState(32);
				match(RPAREN);
				}
				break;
			case ID:
				{
				_localctx = new ObjPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(34);
				objectPredicate();
				}
				break;
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case T__10:
				{
				_localctx = new FunctionPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(35);
				functionalPredicate();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(44);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new NormalContext(new PredicateContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_predicate);
					setState(38);
					if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
					setState(39);
					logicalOperator();
					setState(40);
					predicate(6);
					}
					} 
				}
				setState(46);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
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
	public static class InCompContext extends ObjectPredicateContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PredicateParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PredicateParser.COMMA, i);
		}
		public InCompContext(ObjectPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterInComp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitInComp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitInComp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CheckNullContext extends ObjectPredicateContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public CheckNullContext(ObjectPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterCheckNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitCheckNull(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitCheckNull(this);
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitComparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectPredicateContext objectPredicate() throws RecognitionException {
		ObjectPredicateContext _localctx = new ObjectPredicateContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_objectPredicate);
		int _la;
		try {
			setState(68);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new ComparisonContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(47);
				objectField();
				setState(48);
				comparisonOperator();
				setState(49);
				expression(0);
				}
				break;
			case 2:
				_localctx = new CheckNullContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(51);
				objectField();
				setState(52);
				match(T__1);
				setState(53);
				match(T__2);
				}
				break;
			case 3:
				_localctx = new InCompContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(55);
				objectField();
				setState(56);
				match(T__3);
				setState(57);
				match(LPAREN);
				setState(58);
				expression(0);
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(59);
					match(COMMA);
					setState(60);
					expression(0);
					}
					}
					setState(65);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(66);
				match(RPAREN);
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

	public static class FunctionalPredicateContext extends ParserRuleContext {
		public FunctionalPredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionalPredicate; }
	 
		public FunctionalPredicateContext() { }
		public void copyFrom(FunctionalPredicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class UpperExprContext extends FunctionalPredicateContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public UpperExprContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterUpperExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitUpperExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitUpperExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LowerExprContext extends FunctionalPredicateContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LowerExprContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterLowerExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitLowerExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitLowerExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExistListAndContext extends FunctionalPredicateContext {
		public List<TerminalNode> LPAREN() { return getTokens(PredicateParser.LPAREN); }
		public TerminalNode LPAREN(int i) {
			return getToken(PredicateParser.LPAREN, i);
		}
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RPAREN() { return getTokens(PredicateParser.RPAREN); }
		public TerminalNode RPAREN(int i) {
			return getToken(PredicateParser.RPAREN, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PredicateParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PredicateParser.COMMA, i);
		}
		public ExistListAndContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterExistListAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitExistListAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitExistListAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CountExprContext extends FunctionalPredicateContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public CountExprContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterCountExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitCountExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitCountExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExistListOrContext extends FunctionalPredicateContext {
		public List<TerminalNode> LPAREN() { return getTokens(PredicateParser.LPAREN); }
		public TerminalNode LPAREN(int i) {
			return getToken(PredicateParser.LPAREN, i);
		}
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RPAREN() { return getTokens(PredicateParser.RPAREN); }
		public TerminalNode RPAREN(int i) {
			return getToken(PredicateParser.RPAREN, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PredicateParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PredicateParser.COMMA, i);
		}
		public ExistListOrContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterExistListOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitExistListOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitExistListOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SubstrExprContext extends FunctionalPredicateContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public SubstrExprContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterSubstrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitSubstrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitSubstrExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExistExprContext extends FunctionalPredicateContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public List<ObjectFieldContext> objectField() {
			return getRuleContexts(ObjectFieldContext.class);
		}
		public ObjectFieldContext objectField(int i) {
			return getRuleContext(ObjectFieldContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ExistExprContext(FunctionalPredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterExistExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitExistExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitExistExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionalPredicateContext functionalPredicate() throws RecognitionException {
		FunctionalPredicateContext _localctx = new FunctionalPredicateContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_functionalPredicate);
		int _la;
		try {
			setState(161);
			switch (_input.LA(1)) {
			case T__4:
				_localctx = new CountExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				match(T__4);
				setState(71);
				match(LPAREN);
				setState(72);
				objectField();
				setState(73);
				match(COMMA);
				setState(77);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(74);
					objectField();
					setState(75);
					match(COMMA);
					}
				}

				setState(79);
				expression(0);
				setState(80);
				match(RPAREN);
				setState(81);
				comparisonOperator();
				setState(82);
				expression(0);
				}
				break;
			case T__5:
				_localctx = new ExistExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(84);
				match(T__5);
				setState(85);
				match(LPAREN);
				setState(86);
				objectField();
				setState(87);
				match(COMMA);
				setState(91);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(88);
					objectField();
					setState(89);
					match(COMMA);
					}
				}

				setState(93);
				expression(0);
				setState(94);
				match(RPAREN);
				}
				break;
			case T__6:
				_localctx = new LowerExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(96);
				match(T__6);
				setState(97);
				match(LPAREN);
				setState(98);
				objectField();
				setState(99);
				match(RPAREN);
				setState(100);
				comparisonOperator();
				setState(101);
				expression(0);
				}
				break;
			case T__7:
				_localctx = new UpperExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(103);
				match(T__7);
				setState(104);
				match(LPAREN);
				setState(105);
				objectField();
				setState(106);
				match(RPAREN);
				setState(107);
				comparisonOperator();
				setState(108);
				expression(0);
				}
				break;
			case T__8:
				_localctx = new SubstrExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(110);
				match(T__8);
				setState(111);
				match(LPAREN);
				setState(112);
				objectField();
				setState(113);
				match(COMMA);
				setState(114);
				expression(0);
				setState(115);
				match(RPAREN);
				setState(116);
				comparisonOperator();
				setState(117);
				expression(0);
				}
				break;
			case T__9:
				_localctx = new ExistListOrContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(119);
				match(T__9);
				setState(120);
				match(LPAREN);
				setState(121);
				objectField();
				setState(122);
				match(COMMA);
				setState(126);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(123);
					objectField();
					setState(124);
					match(COMMA);
					}
				}

				setState(128);
				match(LPAREN);
				setState(129);
				expression(0);
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(130);
					match(COMMA);
					setState(131);
					expression(0);
					}
					}
					setState(136);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(137);
				match(RPAREN);
				setState(138);
				match(RPAREN);
				}
				break;
			case T__10:
				_localctx = new ExistListAndContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(140);
				match(T__10);
				setState(141);
				match(LPAREN);
				setState(142);
				objectField();
				setState(143);
				match(COMMA);
				setState(147);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(144);
					objectField();
					setState(145);
					match(COMMA);
					}
				}

				setState(149);
				match(LPAREN);
				setState(150);
				expression(0);
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(151);
					match(COMMA);
					setState(152);
					expression(0);
					}
					}
					setState(157);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(158);
				match(RPAREN);
				setState(159);
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
	public static class ExpValueContext extends ExpressionContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ExpValueContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterExpValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitExpValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitExpValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpParenContext extends ExpressionContext {
		public TerminalNode LPAREN() { return getToken(PredicateParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PredicateParser.RPAREN, 0); }
		public ExpParenContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterExpParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitExpParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitExpParen(this);
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitExpr(this);
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
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new ExpParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(164);
				match(LPAREN);
				setState(165);
				expression(0);
				setState(166);
				match(RPAREN);
				}
				break;
			case T__2:
			case BOOLEAN:
			case STRING:
			case NUMBER:
				{
				_localctx = new ExpValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(168);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(177);
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
					setState(171);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(172);
					binaryOperator();
					setState(173);
					expression(4);
					}
					} 
				}
				setState(179);
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
		public TerminalNode STRING() { return getToken(PredicateParser.STRING, 0); }
		public StringsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterStrings(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitStrings(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitStrings(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullsContext extends ValueContext {
		public NullsContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterNulls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitNulls(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitNulls(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleansContext extends ValueContext {
		public TerminalNode BOOLEAN() { return getToken(PredicateParser.BOOLEAN, 0); }
		public BooleansContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterBooleans(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitBooleans(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitBooleans(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumbersContext extends ValueContext {
		public TerminalNode NUMBER() { return getToken(PredicateParser.NUMBER, 0); }
		public NumbersContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterNumbers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitNumbers(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitNumbers(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_value);
		try {
			setState(184);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(180);
				match(STRING);
				}
				break;
			case NUMBER:
				_localctx = new NumbersContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(181);
				match(NUMBER);
				}
				break;
			case BOOLEAN:
				_localctx = new BooleansContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(182);
				match(BOOLEAN);
				}
				break;
			case T__2:
				_localctx = new NullsContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(183);
				match(T__2);
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
		public List<TerminalNode> ID() { return getTokens(PredicateParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(PredicateParser.ID, i);
		}
		public ObjectFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectField; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterObjectField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitObjectField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitObjectField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectFieldContext objectField() throws RecognitionException {
		ObjectFieldContext _localctx = new ObjectFieldContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_objectField);
		int _la;
		try {
			setState(195);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				match(ID);
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(187);
					match(T__11);
					setState(188);
					match(ID);
					}
					}
					setState(193);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(194);
				match(ID);
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterBinaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitBinaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitBinaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinaryOperatorContext binaryOperator() throws RecognitionException {
		BinaryOperatorContext _localctx = new BinaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_binaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16))) != 0)) ) {
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterUnaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitUnaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitUnaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryOperatorContext unaryOperator() throws RecognitionException {
		UnaryOperatorContext _localctx = new UnaryOperatorContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_la = _input.LA(1);
			if ( !(_la==T__12 || _la==T__13) ) {
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22))) != 0)) ) {
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
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).enterLogicalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PredicateListener ) ((PredicateListener)listener).exitLogicalOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PredicateVisitor ) return ((PredicateVisitor<? extends T>)visitor).visitLogicalOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalOperatorContext logicalOperator() throws RecognitionException {
		LogicalOperatorContext _localctx = new LogicalOperatorContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_logicalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			_la = _input.LA(1);
			if ( !(_la==T__23 || _la==T__24) ) {
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
		case 1:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 4:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3#\u00d0\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\5\2\32\n\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\5\3\'\n\3\3\3\3\3\3\3\3\3\7\3-\n\3\f\3\16\3\60\13\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4@\n\4\f\4\16\4C\13\4\3\4\3\4"+
		"\5\4G\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5P\n\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\5\5^\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0081\n\5\3\5\3\5\3\5\3\5\7\5\u0087\n\5"+
		"\f\5\16\5\u008a\13\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0096"+
		"\n\5\3\5\3\5\3\5\3\5\7\5\u009c\n\5\f\5\16\5\u009f\13\5\3\5\3\5\3\5\5\5"+
		"\u00a4\n\5\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00ac\n\6\3\6\3\6\3\6\3\6\7\6\u00b2"+
		"\n\6\f\6\16\6\u00b5\13\6\3\7\3\7\3\7\3\7\5\7\u00bb\n\7\3\b\3\b\3\b\7\b"+
		"\u00c0\n\b\f\b\16\b\u00c3\13\b\3\b\5\b\u00c6\n\b\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\f\2\4\4\n\r\2\4\6\b\n\f\16\20\22\24\26\2\6\3\2\17\23\3"+
		"\2\17\20\3\2\24\31\3\2\32\33\u00df\2\31\3\2\2\2\4&\3\2\2\2\6F\3\2\2\2"+
		"\b\u00a3\3\2\2\2\n\u00ab\3\2\2\2\f\u00ba\3\2\2\2\16\u00c5\3\2\2\2\20\u00c7"+
		"\3\2\2\2\22\u00c9\3\2\2\2\24\u00cb\3\2\2\2\26\u00cd\3\2\2\2\30\32\5\4"+
		"\3\2\31\30\3\2\2\2\31\32\3\2\2\2\32\33\3\2\2\2\33\34\7\2\2\3\34\3\3\2"+
		"\2\2\35\36\b\3\1\2\36\37\7\3\2\2\37\'\5\4\3\5 !\7 \2\2!\"\5\4\3\2\"#\7"+
		"!\2\2#\'\3\2\2\2$\'\5\6\4\2%\'\5\b\5\2&\35\3\2\2\2& \3\2\2\2&$\3\2\2\2"+
		"&%\3\2\2\2\'.\3\2\2\2()\f\7\2\2)*\5\26\f\2*+\5\4\3\b+-\3\2\2\2,(\3\2\2"+
		"\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2/\5\3\2\2\2\60.\3\2\2\2\61\62\5\16\b"+
		"\2\62\63\5\24\13\2\63\64\5\n\6\2\64G\3\2\2\2\65\66\5\16\b\2\66\67\7\4"+
		"\2\2\678\7\5\2\28G\3\2\2\29:\5\16\b\2:;\7\6\2\2;<\7 \2\2<A\5\n\6\2=>\7"+
		"#\2\2>@\5\n\6\2?=\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2BD\3\2\2\2CA\3"+
		"\2\2\2DE\7!\2\2EG\3\2\2\2F\61\3\2\2\2F\65\3\2\2\2F9\3\2\2\2G\7\3\2\2\2"+
		"HI\7\7\2\2IJ\7 \2\2JK\5\16\b\2KO\7#\2\2LM\5\16\b\2MN\7#\2\2NP\3\2\2\2"+
		"OL\3\2\2\2OP\3\2\2\2PQ\3\2\2\2QR\5\n\6\2RS\7!\2\2ST\5\24\13\2TU\5\n\6"+
		"\2U\u00a4\3\2\2\2VW\7\b\2\2WX\7 \2\2XY\5\16\b\2Y]\7#\2\2Z[\5\16\b\2[\\"+
		"\7#\2\2\\^\3\2\2\2]Z\3\2\2\2]^\3\2\2\2^_\3\2\2\2_`\5\n\6\2`a\7!\2\2a\u00a4"+
		"\3\2\2\2bc\7\t\2\2cd\7 \2\2de\5\16\b\2ef\7!\2\2fg\5\24\13\2gh\5\n\6\2"+
		"h\u00a4\3\2\2\2ij\7\n\2\2jk\7 \2\2kl\5\16\b\2lm\7!\2\2mn\5\24\13\2no\5"+
		"\n\6\2o\u00a4\3\2\2\2pq\7\13\2\2qr\7 \2\2rs\5\16\b\2st\7#\2\2tu\5\n\6"+
		"\2uv\7!\2\2vw\5\24\13\2wx\5\n\6\2x\u00a4\3\2\2\2yz\7\f\2\2z{\7 \2\2{|"+
		"\5\16\b\2|\u0080\7#\2\2}~\5\16\b\2~\177\7#\2\2\177\u0081\3\2\2\2\u0080"+
		"}\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\7 \2\2\u0083"+
		"\u0088\5\n\6\2\u0084\u0085\7#\2\2\u0085\u0087\5\n\6\2\u0086\u0084\3\2"+
		"\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u008b\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c\7!\2\2\u008c\u008d\7!\2"+
		"\2\u008d\u00a4\3\2\2\2\u008e\u008f\7\r\2\2\u008f\u0090\7 \2\2\u0090\u0091"+
		"\5\16\b\2\u0091\u0095\7#\2\2\u0092\u0093\5\16\b\2\u0093\u0094\7#\2\2\u0094"+
		"\u0096\3\2\2\2\u0095\u0092\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0097\3\2"+
		"\2\2\u0097\u0098\7 \2\2\u0098\u009d\5\n\6\2\u0099\u009a\7#\2\2\u009a\u009c"+
		"\5\n\6\2\u009b\u0099\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009d"+
		"\u009e\3\2\2\2\u009e\u00a0\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a1\7!"+
		"\2\2\u00a1\u00a2\7!\2\2\u00a2\u00a4\3\2\2\2\u00a3H\3\2\2\2\u00a3V\3\2"+
		"\2\2\u00a3b\3\2\2\2\u00a3i\3\2\2\2\u00a3p\3\2\2\2\u00a3y\3\2\2\2\u00a3"+
		"\u008e\3\2\2\2\u00a4\t\3\2\2\2\u00a5\u00a6\b\6\1\2\u00a6\u00a7\7 \2\2"+
		"\u00a7\u00a8\5\n\6\2\u00a8\u00a9\7!\2\2\u00a9\u00ac\3\2\2\2\u00aa\u00ac"+
		"\5\f\7\2\u00ab\u00a5\3\2\2\2\u00ab\u00aa\3\2\2\2\u00ac\u00b3\3\2\2\2\u00ad"+
		"\u00ae\f\5\2\2\u00ae\u00af\5\20\t\2\u00af\u00b0\5\n\6\6\u00b0\u00b2\3"+
		"\2\2\2\u00b1\u00ad\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3"+
		"\u00b4\3\2\2\2\u00b4\13\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6\u00bb\7\35\2"+
		"\2\u00b7\u00bb\7\37\2\2\u00b8\u00bb\7\34\2\2\u00b9\u00bb\7\5\2\2\u00ba"+
		"\u00b6\3\2\2\2\u00ba\u00b7\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00b9\3\2"+
		"\2\2\u00bb\r\3\2\2\2\u00bc\u00c1\7\36\2\2\u00bd\u00be\7\16\2\2\u00be\u00c0"+
		"\7\36\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2"+
		"\u00c1\u00c2\3\2\2\2\u00c2\u00c6\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c4\u00c6"+
		"\7\36\2\2\u00c5\u00bc\3\2\2\2\u00c5\u00c4\3\2\2\2\u00c6\17\3\2\2\2\u00c7"+
		"\u00c8\t\2\2\2\u00c8\21\3\2\2\2\u00c9\u00ca\t\3\2\2\u00ca\23\3\2\2\2\u00cb"+
		"\u00cc\t\4\2\2\u00cc\25\3\2\2\2\u00cd\u00ce\t\5\2\2\u00ce\27\3\2\2\2\23"+
		"\31&.AFO]\u0080\u0088\u0095\u009d\u00a3\u00ab\u00b3\u00ba\u00c1\u00c5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}