// Generated from /Users/mhsieh/java/dev/query/objectquery/src/main/resources/Query.g4 by ANTLR 4.5.1
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
		T__31=32, T__32=33, T__33=34, T__34=35, BOOLEAN=36, STRING=37, ID=38, 
		NUMBER=39, LPAREN=40, RPAREN=41, WS=42, COMMA=43;
	public static final int
		RULE_script = 0, RULE_selectStatement = 1, RULE_updateStatement = 2, RULE_whereStatement = 3, 
		RULE_limitCause = 4, RULE_predicate = 5, RULE_objectPredicate = 6, RULE_expression = 7, 
		RULE_functionalExpression = 8, RULE_assignments = 9, RULE_objectField = 10, 
		RULE_identifier = 11, RULE_value = 12, RULE_object = 13, RULE_array = 14, 
		RULE_binaryOperator = 15, RULE_unaryOperator = 16, RULE_comparisonOperator = 17, 
		RULE_logicalOperator = 18;
	public static final String[] ruleNames = {
		"script", "selectStatement", "updateStatement", "whereStatement", "limitCause", 
		"predicate", "objectPredicate", "expression", "functionalExpression", 
		"assignments", "objectField", "identifier", "value", "object", "array", 
		"binaryOperator", "unaryOperator", "comparisonOperator", "logicalOperator"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'select'", "'from'", "'replace'", "'set'", "'insert'", "'where'", 
		"'not'", "'limit'", "'in'", "'lower'", "'upper'", "'count'", "'substr'", 
		"'exist'", "'strToBytes'", "'='", "'key#'", "'*'", "'.'", "'null'", "'{'", 
		"'}'", "'['", "']'", "'+'", "'-'", "'/'", "'%'", "'!='", "'<'", "'<='", 
		"'>'", "'>='", "'and'", "'or'", null, null, null, null, "'('", "')'", 
		null, "','"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"BOOLEAN", "STRING", "ID", "NUMBER", "LPAREN", "RPAREN", "WS", "COMMA"
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
			setState(40);
			switch (_input.LA(1)) {
			case T__2:
			case T__4:
				{
				setState(38);
				updateStatement();
				}
				break;
			case T__0:
				{
				setState(39);
				selectStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(42);
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
		public LimitCauseContext limitCause() {
			return getRuleContext(LimitCauseContext.class,0);
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
			setState(44);
			match(T__0);
			setState(45);
			objectField();
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(46);
				match(COMMA);
				setState(47);
				objectField();
				}
				}
				setState(52);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(53);
			match(T__1);
			setState(54);
			identifier();
			setState(56);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(55);
				whereStatement();
				}
			}

			setState(59);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(58);
				limitCause();
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
			setState(75);
			switch (_input.LA(1)) {
			case T__2:
				_localctx = new ReplaceStatsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(61);
				match(T__2);
				setState(62);
				objectField();
				setState(63);
				match(T__3);
				setState(64);
				assignments(0);
				setState(66);
				_la = _input.LA(1);
				if (_la==T__5) {
					{
					setState(65);
					whereStatement();
					}
				}

				}
				break;
			case T__4:
				_localctx = new InsertStatsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				match(T__4);
				setState(69);
				objectField();
				setState(70);
				match(T__3);
				setState(71);
				assignments(0);
				setState(73);
				_la = _input.LA(1);
				if (_la==T__5) {
					{
					setState(72);
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
			setState(77);
			match(T__5);
			setState(79);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(78);
				match(T__6);
				}
				break;
			}
			setState(81);
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

	public static class LimitCauseContext extends ParserRuleContext {
		public LimitCauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limitCause; }
	 
		public LimitCauseContext() { }
		public void copyFrom(LimitCauseContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LimitPhraseContext extends LimitCauseContext {
		public TerminalNode NUMBER() { return getToken(QueryParser.NUMBER, 0); }
		public LimitPhraseContext(LimitCauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterLimitPhrase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitLimitPhrase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitLimitPhrase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitCauseContext limitCause() throws RecognitionException {
		LimitCauseContext _localctx = new LimitCauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_limitCause);
		try {
			_localctx = new LimitPhraseContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(T__7);
			setState(84);
			match(NUMBER);
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
	public static class NotPredicateContext extends PredicateContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public NotPredicateContext(PredicateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterNotPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitNotPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitNotPredicate(this);
			else return visitor.visitChildren(this);
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

	public final PredicateContext predicate() throws RecognitionException {
		return predicate(0);
	}

	private PredicateContext predicate(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PredicateContext _localctx = new PredicateContext(_ctx, _parentState);
		PredicateContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_predicate, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				_localctx = new NotPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(87);
				match(T__6);
				setState(88);
				predicate(2);
				}
				break;
			case 2:
				{
				_localctx = new ParenPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(89);
				match(LPAREN);
				setState(90);
				predicate(0);
				setState(91);
				match(RPAREN);
				}
				break;
			case 3:
				{
				_localctx = new ObjPredicateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(93);
				objectPredicate();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(102);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new LogicPredicateContext(new PredicateContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_predicate);
					setState(96);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(97);
					logicalOperator();
					setState(98);
					predicate(5);
					}
					} 
				}
				setState(104);
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

	public final ObjectPredicateContext objectPredicate() throws RecognitionException {
		ObjectPredicateContext _localctx = new ObjectPredicateContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_objectPredicate);
		int _la;
		try {
			setState(126);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new ComparisonContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				objectField();
				setState(106);
				comparisonOperator();
				setState(107);
				expression(0);
				}
				break;
			case 2:
				_localctx = new InCompContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(109);
				objectField();
				setState(110);
				match(T__8);
				setState(111);
				match(LPAREN);
				setState(112);
				expression(0);
				setState(117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(113);
					match(COMMA);
					setState(114);
					expression(0);
					}
					}
					setState(119);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(120);
				match(RPAREN);
				}
				break;
			case 3:
				_localctx = new ExpressionPredicateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(122);
				expression(0);
				setState(123);
				comparisonOperator();
				setState(124);
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

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new ExpParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(129);
				match(LPAREN);
				setState(130);
				expression(0);
				setState(131);
				match(RPAREN);
				}
				break;
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
				{
				_localctx = new FunctionExpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(133);
				functionalExpression();
				}
				break;
			case T__19:
			case T__20:
			case T__22:
			case BOOLEAN:
			case STRING:
			case NUMBER:
				{
				_localctx = new ExpValuesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(134);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(143);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(new ExpressionContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expression);
					setState(137);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(138);
					binaryOperator();
					setState(139);
					expression(5);
					}
					} 
				}
				setState(145);
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
	public static class StrToBytesFuncExprContext extends FunctionalExpressionContext {
		public TerminalNode LPAREN() { return getToken(QueryParser.LPAREN, 0); }
		public TerminalNode STRING() { return getToken(QueryParser.STRING, 0); }
		public TerminalNode RPAREN() { return getToken(QueryParser.RPAREN, 0); }
		public StrToBytesFuncExprContext(FunctionalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).enterStrToBytesFuncExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryListener ) ((QueryListener)listener).exitStrToBytesFuncExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryVisitor ) return ((QueryVisitor<? extends T>)visitor).visitStrToBytesFuncExpr(this);
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

	public final FunctionalExpressionContext functionalExpression() throws RecognitionException {
		FunctionalExpressionContext _localctx = new FunctionalExpressionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_functionalExpression);
		int _la;
		try {
			setState(191);
			switch (_input.LA(1)) {
			case T__9:
				_localctx = new LowerExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				match(T__9);
				setState(147);
				match(LPAREN);
				setState(148);
				objectField();
				setState(149);
				match(RPAREN);
				}
				break;
			case T__10:
				_localctx = new UpperExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				match(T__10);
				setState(152);
				match(LPAREN);
				setState(153);
				objectField();
				setState(154);
				match(RPAREN);
				}
				break;
			case T__11:
				_localctx = new CountExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(156);
				match(T__11);
				setState(157);
				match(LPAREN);
				setState(158);
				objectField();
				setState(159);
				match(COMMA);
				setState(163);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << ID))) != 0)) {
					{
					setState(160);
					objectField();
					setState(161);
					match(COMMA);
					}
				}

				setState(165);
				expression(0);
				setState(166);
				match(RPAREN);
				}
				break;
			case T__12:
				_localctx = new SubstrExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(168);
				match(T__12);
				setState(169);
				match(LPAREN);
				setState(170);
				objectField();
				setState(171);
				match(COMMA);
				setState(172);
				expression(0);
				setState(173);
				match(RPAREN);
				}
				break;
			case T__13:
				_localctx = new ExistExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(175);
				match(T__13);
				setState(176);
				match(LPAREN);
				setState(177);
				objectField();
				setState(178);
				match(COMMA);
				setState(182);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << ID))) != 0)) {
					{
					setState(179);
					objectField();
					setState(180);
					match(COMMA);
					}
				}

				setState(184);
				expression(0);
				setState(185);
				match(RPAREN);
				}
				break;
			case T__14:
				_localctx = new StrToBytesFuncExprContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(187);
				match(T__14);
				setState(188);
				match(LPAREN);
				setState(189);
				match(STRING);
				setState(190);
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
	public static class AssignObjectContext extends AssignmentsContext {
		public ObjectFieldContext objectField() {
			return getRuleContext(ObjectFieldContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
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

	public final AssignmentsContext assignments() throws RecognitionException {
		return assignments(0);
	}

	private AssignmentsContext assignments(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AssignmentsContext _localctx = new AssignmentsContext(_ctx, _parentState);
		AssignmentsContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_assignments, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new AssignObjectContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(194);
			objectField();
			setState(195);
			match(T__15);
			setState(196);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(203);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AssignStatsContext(new AssignmentsContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_assignments);
					setState(198);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(199);
					match(COMMA);
					setState(200);
					assignments(3);
					}
					} 
				}
				setState(205);
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

	public final ObjectFieldContext objectField() throws RecognitionException {
		ObjectFieldContext _localctx = new ObjectFieldContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_objectField);
		int _la;
		try {
			setState(216);
			switch (_input.LA(1)) {
			case T__16:
				_localctx = new KeysContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(206);
				match(T__16);
				}
				break;
			case T__17:
				_localctx = new AllContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(207);
				match(T__17);
				}
				break;
			case ID:
				_localctx = new IDSContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(208);
				match(ID);
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__18) {
					{
					{
					setState(209);
					match(T__18);
					setState(210);
					match(ID);
					}
					}
					setState(215);
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
		enterRule(_localctx, 22, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
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

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_value);
		try {
			setState(226);
			switch (_input.LA(1)) {
			case T__20:
				_localctx = new ObjectsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(220);
				object();
				}
				break;
			case T__22:
				_localctx = new ArraysContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(221);
				array();
				}
				break;
			case STRING:
				_localctx = new StringsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(222);
				match(STRING);
				}
				break;
			case NUMBER:
				_localctx = new NumbersContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(223);
				match(NUMBER);
				}
				break;
			case BOOLEAN:
				_localctx = new BooleansContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(224);
				match(BOOLEAN);
				}
				break;
			case T__19:
				_localctx = new NullsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(225);
				match(T__19);
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

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_object);
		try {
			setState(234);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new ObjectAssignsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(228);
				match(T__20);
				setState(229);
				assignments(0);
				setState(230);
				match(T__21);
				}
				break;
			case 2:
				_localctx = new ObjectEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(232);
				match(T__20);
				setState(233);
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
		enterRule(_localctx, 28, RULE_array);
		int _la;
		try {
			setState(249);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				_localctx = new ArrayValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(236);
				match(T__22);
				setState(237);
				value();
				setState(242);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(238);
					match(COMMA);
					setState(239);
					value();
					}
					}
					setState(244);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(245);
				match(T__23);
				}
				break;
			case 2:
				_localctx = new ArrayEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(247);
				match(T__22);
				setState(248);
				match(T__23);
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
		enterRule(_localctx, 30, RULE_binaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) ) {
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
		enterRule(_localctx, 32, RULE_unaryOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			_la = _input.LA(1);
			if ( !(_la==T__24 || _la==T__25) ) {
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
		enterRule(_localctx, 34, RULE_comparisonOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__15) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32))) != 0)) ) {
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
		enterRule(_localctx, 36, RULE_logicalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			_la = _input.LA(1);
			if ( !(_la==T__33 || _la==T__34) ) {
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
		case 5:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		case 7:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 9:
			return assignments_sempred((AssignmentsContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3-\u0106\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\5\2+\n\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3\63\n"+
		"\3\f\3\16\3\66\13\3\3\3\3\3\3\3\5\3;\n\3\3\3\5\3>\n\3\3\4\3\4\3\4\3\4"+
		"\3\4\5\4E\n\4\3\4\3\4\3\4\3\4\3\4\5\4L\n\4\5\4N\n\4\3\5\3\5\5\5R\n\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7a\n\7\3\7\3\7\3"+
		"\7\3\7\7\7g\n\7\f\7\16\7j\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\7\bv\n\b\f\b\16\by\13\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u0081\n\b\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\5\t\u008a\n\t\3\t\3\t\3\t\3\t\7\t\u0090\n\t\f\t\16"+
		"\t\u0093\13\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\5\n\u00a6\n\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00b9\n\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n"+
		"\u00c2\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00cc\n\13\f\13"+
		"\16\13\u00cf\13\13\3\f\3\f\3\f\3\f\3\f\7\f\u00d6\n\f\f\f\16\f\u00d9\13"+
		"\f\5\f\u00db\n\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00e5\n\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00ed\n\17\3\20\3\20\3\20\3\20\7\20"+
		"\u00f3\n\20\f\20\16\20\u00f6\13\20\3\20\3\20\3\20\3\20\5\20\u00fc\n\20"+
		"\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\2\5\f\20\24\25\2\4\6\b\n"+
		"\f\16\20\22\24\26\30\32\34\36 \"$&\2\6\4\2\24\24\33\36\3\2\33\34\4\2\22"+
		"\22\37#\3\2$%\u0116\2*\3\2\2\2\4.\3\2\2\2\6M\3\2\2\2\bO\3\2\2\2\nU\3\2"+
		"\2\2\f`\3\2\2\2\16\u0080\3\2\2\2\20\u0089\3\2\2\2\22\u00c1\3\2\2\2\24"+
		"\u00c3\3\2\2\2\26\u00da\3\2\2\2\30\u00dc\3\2\2\2\32\u00e4\3\2\2\2\34\u00ec"+
		"\3\2\2\2\36\u00fb\3\2\2\2 \u00fd\3\2\2\2\"\u00ff\3\2\2\2$\u0101\3\2\2"+
		"\2&\u0103\3\2\2\2(+\5\6\4\2)+\5\4\3\2*(\3\2\2\2*)\3\2\2\2+,\3\2\2\2,-"+
		"\7\2\2\3-\3\3\2\2\2./\7\3\2\2/\64\5\26\f\2\60\61\7-\2\2\61\63\5\26\f\2"+
		"\62\60\3\2\2\2\63\66\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\67\3\2\2\2"+
		"\66\64\3\2\2\2\678\7\4\2\28:\5\30\r\29;\5\b\5\2:9\3\2\2\2:;\3\2\2\2;="+
		"\3\2\2\2<>\5\n\6\2=<\3\2\2\2=>\3\2\2\2>\5\3\2\2\2?@\7\5\2\2@A\5\26\f\2"+
		"AB\7\6\2\2BD\5\24\13\2CE\5\b\5\2DC\3\2\2\2DE\3\2\2\2EN\3\2\2\2FG\7\7\2"+
		"\2GH\5\26\f\2HI\7\6\2\2IK\5\24\13\2JL\5\b\5\2KJ\3\2\2\2KL\3\2\2\2LN\3"+
		"\2\2\2M?\3\2\2\2MF\3\2\2\2N\7\3\2\2\2OQ\7\b\2\2PR\7\t\2\2QP\3\2\2\2QR"+
		"\3\2\2\2RS\3\2\2\2ST\5\f\7\2T\t\3\2\2\2UV\7\n\2\2VW\7)\2\2W\13\3\2\2\2"+
		"XY\b\7\1\2YZ\7\t\2\2Za\5\f\7\4[\\\7*\2\2\\]\5\f\7\2]^\7+\2\2^a\3\2\2\2"+
		"_a\5\16\b\2`X\3\2\2\2`[\3\2\2\2`_\3\2\2\2ah\3\2\2\2bc\f\6\2\2cd\5&\24"+
		"\2de\5\f\7\7eg\3\2\2\2fb\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2i\r\3\2"+
		"\2\2jh\3\2\2\2kl\5\26\f\2lm\5$\23\2mn\5\20\t\2n\u0081\3\2\2\2op\5\26\f"+
		"\2pq\7\13\2\2qr\7*\2\2rw\5\20\t\2st\7-\2\2tv\5\20\t\2us\3\2\2\2vy\3\2"+
		"\2\2wu\3\2\2\2wx\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\7+\2\2{\u0081\3\2\2\2|"+
		"}\5\20\t\2}~\5$\23\2~\177\5\20\t\2\177\u0081\3\2\2\2\u0080k\3\2\2\2\u0080"+
		"o\3\2\2\2\u0080|\3\2\2\2\u0081\17\3\2\2\2\u0082\u0083\b\t\1\2\u0083\u0084"+
		"\7*\2\2\u0084\u0085\5\20\t\2\u0085\u0086\7+\2\2\u0086\u008a\3\2\2\2\u0087"+
		"\u008a\5\22\n\2\u0088\u008a\5\32\16\2\u0089\u0082\3\2\2\2\u0089\u0087"+
		"\3\2\2\2\u0089\u0088\3\2\2\2\u008a\u0091\3\2\2\2\u008b\u008c\f\6\2\2\u008c"+
		"\u008d\5 \21\2\u008d\u008e\5\20\t\7\u008e\u0090\3\2\2\2\u008f\u008b\3"+
		"\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092"+
		"\21\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7\f\2\2\u0095\u0096\7*\2\2"+
		"\u0096\u0097\5\26\f\2\u0097\u0098\7+\2\2\u0098\u00c2\3\2\2\2\u0099\u009a"+
		"\7\r\2\2\u009a\u009b\7*\2\2\u009b\u009c\5\26\f\2\u009c\u009d\7+\2\2\u009d"+
		"\u00c2\3\2\2\2\u009e\u009f\7\16\2\2\u009f\u00a0\7*\2\2\u00a0\u00a1\5\26"+
		"\f\2\u00a1\u00a5\7-\2\2\u00a2\u00a3\5\26\f\2\u00a3\u00a4\7-\2\2\u00a4"+
		"\u00a6\3\2\2\2\u00a5\u00a2\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\3\2"+
		"\2\2\u00a7\u00a8\5\20\t\2\u00a8\u00a9\7+\2\2\u00a9\u00c2\3\2\2\2\u00aa"+
		"\u00ab\7\17\2\2\u00ab\u00ac\7*\2\2\u00ac\u00ad\5\26\f\2\u00ad\u00ae\7"+
		"-\2\2\u00ae\u00af\5\20\t\2\u00af\u00b0\7+\2\2\u00b0\u00c2\3\2\2\2\u00b1"+
		"\u00b2\7\20\2\2\u00b2\u00b3\7*\2\2\u00b3\u00b4\5\26\f\2\u00b4\u00b8\7"+
		"-\2\2\u00b5\u00b6\5\26\f\2\u00b6\u00b7\7-\2\2\u00b7\u00b9\3\2\2\2\u00b8"+
		"\u00b5\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bb\5\20"+
		"\t\2\u00bb\u00bc\7+\2\2\u00bc\u00c2\3\2\2\2\u00bd\u00be\7\21\2\2\u00be"+
		"\u00bf\7*\2\2\u00bf\u00c0\7\'\2\2\u00c0\u00c2\7+\2\2\u00c1\u0094\3\2\2"+
		"\2\u00c1\u0099\3\2\2\2\u00c1\u009e\3\2\2\2\u00c1\u00aa\3\2\2\2\u00c1\u00b1"+
		"\3\2\2\2\u00c1\u00bd\3\2\2\2\u00c2\23\3\2\2\2\u00c3\u00c4\b\13\1\2\u00c4"+
		"\u00c5\5\26\f\2\u00c5\u00c6\7\22\2\2\u00c6\u00c7\5\20\t\2\u00c7\u00cd"+
		"\3\2\2\2\u00c8\u00c9\f\4\2\2\u00c9\u00ca\7-\2\2\u00ca\u00cc\5\24\13\5"+
		"\u00cb\u00c8\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce"+
		"\3\2\2\2\u00ce\25\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00db\7\23\2\2\u00d1"+
		"\u00db\7\24\2\2\u00d2\u00d7\7(\2\2\u00d3\u00d4\7\25\2\2\u00d4\u00d6\7"+
		"(\2\2\u00d5\u00d3\3\2\2\2\u00d6\u00d9\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7"+
		"\u00d8\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00da\u00d0\3\2"+
		"\2\2\u00da\u00d1\3\2\2\2\u00da\u00d2\3\2\2\2\u00db\27\3\2\2\2\u00dc\u00dd"+
		"\7(\2\2\u00dd\31\3\2\2\2\u00de\u00e5\5\34\17\2\u00df\u00e5\5\36\20\2\u00e0"+
		"\u00e5\7\'\2\2\u00e1\u00e5\7)\2\2\u00e2\u00e5\7&\2\2\u00e3\u00e5\7\26"+
		"\2\2\u00e4\u00de\3\2\2\2\u00e4\u00df\3\2\2\2\u00e4\u00e0\3\2\2\2\u00e4"+
		"\u00e1\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e3\3\2\2\2\u00e5\33\3\2\2"+
		"\2\u00e6\u00e7\7\27\2\2\u00e7\u00e8\5\24\13\2\u00e8\u00e9\7\30\2\2\u00e9"+
		"\u00ed\3\2\2\2\u00ea\u00eb\7\27\2\2\u00eb\u00ed\7\30\2\2\u00ec\u00e6\3"+
		"\2\2\2\u00ec\u00ea\3\2\2\2\u00ed\35\3\2\2\2\u00ee\u00ef\7\31\2\2\u00ef"+
		"\u00f4\5\32\16\2\u00f0\u00f1\7-\2\2\u00f1\u00f3\5\32\16\2\u00f2\u00f0"+
		"\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5"+
		"\u00f7\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00f8\7\32\2\2\u00f8\u00fc\3"+
		"\2\2\2\u00f9\u00fa\7\31\2\2\u00fa\u00fc\7\32\2\2\u00fb\u00ee\3\2\2\2\u00fb"+
		"\u00f9\3\2\2\2\u00fc\37\3\2\2\2\u00fd\u00fe\t\2\2\2\u00fe!\3\2\2\2\u00ff"+
		"\u0100\t\3\2\2\u0100#\3\2\2\2\u0101\u0102\t\4\2\2\u0102%\3\2\2\2\u0103"+
		"\u0104\t\5\2\2\u0104\'\3\2\2\2\32*\64:=DKMQ`hw\u0080\u0089\u0091\u00a5"+
		"\u00b8\u00c1\u00cd\u00d7\u00da\u00e4\u00ec\u00f4\u00fb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}