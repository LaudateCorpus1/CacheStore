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
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ObjectQueryLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		BOOLEAN=18, STRING=19, ID=20, NUMBER=21, WS=22, COMMA=23;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"BOOLEAN", "STRING", "ESC", "UNICODE", "HEX", "ID", "NUMBER", "INT", "WS", 
		"COMMA"
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


	public ObjectQueryLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ObjectQuery.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\31\u00d0\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u008e\n\23\3\24\3\24"+
		"\3\24\7\24\u0093\n\24\f\24\16\24\u0096\13\24\3\24\3\24\3\25\3\25\3\25"+
		"\5\25\u009d\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\30\3\30\7\30"+
		"\u00a9\n\30\f\30\16\30\u00ac\13\30\3\31\5\31\u00af\n\31\3\31\3\31\3\31"+
		"\6\31\u00b4\n\31\r\31\16\31\u00b5\3\31\5\31\u00b9\n\31\3\31\5\31\u00bc"+
		"\n\31\3\32\3\32\3\32\7\32\u00c1\n\32\f\32\16\32\u00c4\13\32\5\32\u00c6"+
		"\n\32\3\33\6\33\u00c9\n\33\r\33\16\33\u00ca\3\33\3\33\3\34\3\34\2\2\35"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\2+\2-\2/\26\61\27\63\2\65\30\67\31\3\2\n\4\2"+
		"$$^^\n\2$$\61\61^^ddhhppttvv\5\2\62;CHch\4\2C\\c|\6\2\62;C\\aac|\3\2\62"+
		";\3\2\63;\5\2\13\f\17\17\"\"\u00d7\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\39\3\2\2\2\5@\3\2\2\2\7E\3\2"+
		"\2\2\tM\3\2\2\2\13T\3\2\2\2\rX\3\2\2\2\17Z\3\2\2\2\21a\3\2\2\2\23c\3\2"+
		"\2\2\25i\3\2\2\2\27m\3\2\2\2\31r\3\2\2\2\33z\3\2\2\2\35|\3\2\2\2\37~\3"+
		"\2\2\2!\u0080\3\2\2\2#\u0082\3\2\2\2%\u008d\3\2\2\2\'\u008f\3\2\2\2)\u0099"+
		"\3\2\2\2+\u009e\3\2\2\2-\u00a4\3\2\2\2/\u00a6\3\2\2\2\61\u00bb\3\2\2\2"+
		"\63\u00c5\3\2\2\2\65\u00c8\3\2\2\2\67\u00ce\3\2\2\29:\7u\2\2:;\7g\2\2"+
		";<\7n\2\2<=\7g\2\2=>\7e\2\2>?\7v\2\2?\4\3\2\2\2@A\7h\2\2AB\7t\2\2BC\7"+
		"q\2\2CD\7o\2\2D\6\3\2\2\2EF\7t\2\2FG\7g\2\2GH\7r\2\2HI\7n\2\2IJ\7c\2\2"+
		"JK\7e\2\2KL\7g\2\2L\b\3\2\2\2MN\7w\2\2NO\7r\2\2OP\7f\2\2PQ\7c\2\2QR\7"+
		"v\2\2RS\7g\2\2S\n\3\2\2\2TU\7u\2\2UV\7g\2\2VW\7v\2\2W\f\3\2\2\2XY\7=\2"+
		"\2Y\16\3\2\2\2Z[\7k\2\2[\\\7p\2\2\\]\7u\2\2]^\7g\2\2^_\7t\2\2_`\7v\2\2"+
		"`\20\3\2\2\2ab\7?\2\2b\22\3\2\2\2cd\7y\2\2de\7j\2\2ef\7g\2\2fg\7t\2\2"+
		"gh\7g\2\2h\24\3\2\2\2ij\7p\2\2jk\7q\2\2kl\7v\2\2l\26\3\2\2\2mn\7p\2\2"+
		"no\7w\2\2op\7n\2\2pq\7n\2\2q\30\3\2\2\2rs\7t\2\2st\7g\2\2tu\7e\2\2uv\7"+
		"q\2\2vw\7t\2\2wx\7f\2\2xy\7%\2\2y\32\3\2\2\2z{\7\60\2\2{\34\3\2\2\2|}"+
		"\7}\2\2}\36\3\2\2\2~\177\7\177\2\2\177 \3\2\2\2\u0080\u0081\7]\2\2\u0081"+
		"\"\3\2\2\2\u0082\u0083\7_\2\2\u0083$\3\2\2\2\u0084\u0085\7v\2\2\u0085"+
		"\u0086\7t\2\2\u0086\u0087\7w\2\2\u0087\u008e\7g\2\2\u0088\u0089\7h\2\2"+
		"\u0089\u008a\7c\2\2\u008a\u008b\7n\2\2\u008b\u008c\7u\2\2\u008c\u008e"+
		"\7g\2\2\u008d\u0084\3\2\2\2\u008d\u0088\3\2\2\2\u008e&\3\2\2\2\u008f\u0094"+
		"\7$\2\2\u0090\u0093\5)\25\2\u0091\u0093\n\2\2\2\u0092\u0090\3\2\2\2\u0092"+
		"\u0091\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2"+
		"\2\2\u0095\u0097\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u0098\7$\2\2\u0098"+
		"(\3\2\2\2\u0099\u009c\7^\2\2\u009a\u009d\t\3\2\2\u009b\u009d\5+\26\2\u009c"+
		"\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d*\3\2\2\2\u009e\u009f\7w\2\2\u009f"+
		"\u00a0\5-\27\2\u00a0\u00a1\5-\27\2\u00a1\u00a2\5-\27\2\u00a2\u00a3\5-"+
		"\27\2\u00a3,\3\2\2\2\u00a4\u00a5\t\4\2\2\u00a5.\3\2\2\2\u00a6\u00aa\t"+
		"\5\2\2\u00a7\u00a9\t\6\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa"+
		"\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\60\3\2\2\2\u00ac\u00aa\3\2\2"+
		"\2\u00ad\u00af\7/\2\2\u00ae\u00ad\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b0"+
		"\3\2\2\2\u00b0\u00b1\5\63\32\2\u00b1\u00b3\7\60\2\2\u00b2\u00b4\t\7\2"+
		"\2\u00b3\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b5\u00b6"+
		"\3\2\2\2\u00b6\u00bc\3\2\2\2\u00b7\u00b9\7/\2\2\u00b8\u00b7\3\2\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bc\5\63\32\2\u00bb\u00ae\3"+
		"\2\2\2\u00bb\u00b8\3\2\2\2\u00bc\62\3\2\2\2\u00bd\u00c6\7\62\2\2\u00be"+
		"\u00c2\t\b\2\2\u00bf\u00c1\t\7\2\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2"+
		"\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c5\u00bd\3\2\2\2\u00c5\u00be\3\2\2\2\u00c6\64\3\2\2"+
		"\2\u00c7\u00c9\t\t\2\2\u00c8\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00c8"+
		"\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00cd\b\33\2\2"+
		"\u00cd\66\3\2\2\2\u00ce\u00cf\7.\2\2\u00cf8\3\2\2\2\17\2\u008d\u0092\u0094"+
		"\u009c\u00aa\u00ae\u00b5\u00b8\u00bb\u00c2\u00c5\u00ca\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}