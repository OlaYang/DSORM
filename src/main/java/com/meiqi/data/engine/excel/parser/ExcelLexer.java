package com.meiqi.data.engine.excel.parser;

// Generated from Excel.g4 by ANTLR 4.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExcelLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__8=1, T__7=2, T__6=3, T__5=4, T__4=5, T__3=6, T__2=7, T__1=8, T__0=9, 
		TRUE=10, FALSE=11, NAME=12, WS=13, STRING=14, ALPHA=15, SIGN=16, LONG=17, 
		DIGIT=18, FLOAT=19, EXPONENT=20, POW=21, MUL=22, DIV=23, JOIN=24, GE=25, 
		LE=26, NE=27, GT=28, LT=29, EQ=30;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'%'", "']'", "')'", "','", "'['", "'('", "'$ROW$'", "':'", "';'", "TRUE", 
		"FALSE", "NAME", "WS", "STRING", "ALPHA", "SIGN", "LONG", "DIGIT", "FLOAT", 
		"EXPONENT", "'^'", "'*'", "'/'", "'&'", "'>='", "'<='", "'<>'", "'>'", 
		"'<'", "'='"
	};
	public static final String[] ruleNames = {
		"T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", 
		"TRUE", "FALSE", "NAME", "WS", "STRING", "ALPHA", "SIGN", "LONG", "DIGIT", 
		"FLOAT", "EXPONENT", "POW", "MUL", "DIV", "JOIN", "GE", "LE", "NE", "GT", 
		"LT", "EQ"
	};


	    @Override
	    public void notifyListeners(LexerNoViableAltException e) {
	        String text = _input.getText(Interval.of(_tokenStartCharIndex, _input.index()));
	        String msg = "\u65e0\u6cd5\u8bc6\u522b\u7684\u8f93\u5165 '" + getErrorDisplay(text) + "'";
	        throw new RuntimeException(msg);
	    }


	public ExcelLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Excel.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 12: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: _channel = HIDDEN;  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2 \u00d3\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\3\2\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t"+
		"\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13^\n\13\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\fj\n\f\3\r\3\r\6\rn\n\r\r\r\16\ro\3\r"+
		"\3\r\3\r\3\r\7\rv\n\r\f\r\16\ry\13\r\3\r\3\r\3\r\6\r~\n\r\r\r\16\r\177"+
		"\5\r\u0082\n\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\6\17\u008c\n\17"+
		"\r\17\16\17\u008d\7\17\u0090\n\17\f\17\16\17\u0093\13\17\3\17\3\17\3\20"+
		"\3\20\3\21\6\21\u009a\n\21\r\21\16\21\u009b\3\22\6\22\u009f\n\22\r\22"+
		"\16\22\u00a0\3\23\3\23\3\24\5\24\u00a6\n\24\3\24\3\24\6\24\u00aa\n\24"+
		"\r\24\16\24\u00ab\3\24\5\24\u00af\n\24\3\24\5\24\u00b2\n\24\3\25\3\25"+
		"\5\25\u00b6\n\25\3\25\6\25\u00b9\n\25\r\25\16\25\u00ba\3\26\3\26\3\27"+
		"\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34"+
		"\3\35\3\35\3\36\3\36\3\37\3\37\2 \3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1"+
		"\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\2\35\20\1\37\21\1!"+
		"\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1\63\33\1\65\34"+
		"\1\67\35\19\36\1;\37\1= \1\3\2\7\5\2\13\f\17\17\"\"\3\2$$\4\2C\\c|\4\2"+
		"--//\4\2GGgg\u00e9\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\3?\3\2\2\2\5A\3\2\2\2\7C\3\2\2\2\tE"+
		"\3\2\2\2\13G\3\2\2\2\rI\3\2\2\2\17K\3\2\2\2\21Q\3\2\2\2\23S\3\2\2\2\25"+
		"]\3\2\2\2\27i\3\2\2\2\31m\3\2\2\2\33\u0083\3\2\2\2\35\u0087\3\2\2\2\37"+
		"\u0096\3\2\2\2!\u0099\3\2\2\2#\u009e\3\2\2\2%\u00a2\3\2\2\2\'\u00a5\3"+
		"\2\2\2)\u00b3\3\2\2\2+\u00bc\3\2\2\2-\u00be\3\2\2\2/\u00c0\3\2\2\2\61"+
		"\u00c2\3\2\2\2\63\u00c4\3\2\2\2\65\u00c7\3\2\2\2\67\u00ca\3\2\2\29\u00cd"+
		"\3\2\2\2;\u00cf\3\2\2\2=\u00d1\3\2\2\2?@\7\'\2\2@\4\3\2\2\2AB\7_\2\2B"+
		"\6\3\2\2\2CD\7+\2\2D\b\3\2\2\2EF\7.\2\2F\n\3\2\2\2GH\7]\2\2H\f\3\2\2\2"+
		"IJ\7*\2\2J\16\3\2\2\2KL\7&\2\2LM\7T\2\2MN\7Q\2\2NO\7Y\2\2OP\7&\2\2P\20"+
		"\3\2\2\2QR\7<\2\2R\22\3\2\2\2ST\7=\2\2T\24\3\2\2\2UV\7V\2\2VW\7T\2\2W"+
		"X\7W\2\2X^\7G\2\2YZ\7v\2\2Z[\7t\2\2[\\\7w\2\2\\^\7g\2\2]U\3\2\2\2]Y\3"+
		"\2\2\2^\26\3\2\2\2_`\7H\2\2`a\7C\2\2ab\7N\2\2bc\7U\2\2cj\7G\2\2de\7h\2"+
		"\2ef\7c\2\2fg\7n\2\2gh\7u\2\2hj\7g\2\2i_\3\2\2\2id\3\2\2\2j\30\3\2\2\2"+
		"kn\7a\2\2ln\5\37\20\2mk\3\2\2\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2"+
		"\2p\u0081\3\2\2\2qv\7a\2\2rv\5\37\20\2sv\5%\23\2tv\7\60\2\2uq\3\2\2\2"+
		"ur\3\2\2\2us\3\2\2\2ut\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x}\3\2\2\2"+
		"yw\3\2\2\2z~\5\37\20\2{~\5%\23\2|~\7a\2\2}z\3\2\2\2}{\3\2\2\2}|\3\2\2"+
		"\2~\177\3\2\2\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080\u0082\3\2\2\2\u0081"+
		"w\3\2\2\2\u0081\u0082\3\2\2\2\u0082\32\3\2\2\2\u0083\u0084\t\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0085\u0086\b\16\2\2\u0086\34\3\2\2\2\u0087\u0091\7$\2"+
		"\2\u0088\u0089\7$\2\2\u0089\u0090\7$\2\2\u008a\u008c\n\3\2\2\u008b\u008a"+
		"\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e"+
		"\u0090\3\2\2\2\u008f\u0088\3\2\2\2\u008f\u008b\3\2\2\2\u0090\u0093\3\2"+
		"\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0094\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0094\u0095\7$\2\2\u0095\36\3\2\2\2\u0096\u0097\t\4\2\2"+
		"\u0097 \3\2\2\2\u0098\u009a\t\5\2\2\u0099\u0098\3\2\2\2\u009a\u009b\3"+
		"\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\"\3\2\2\2\u009d\u009f"+
		"\5%\23\2\u009e\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0"+
		"\u00a1\3\2\2\2\u00a1$\3\2\2\2\u00a2\u00a3\4\62;\2\u00a3&\3\2\2\2\u00a4"+
		"\u00a6\5#\22\2\u00a5\u00a4\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00b1\3\2"+
		"\2\2\u00a7\u00a9\7\60\2\2\u00a8\u00aa\5%\23\2\u00a9\u00a8\3\2\2\2\u00aa"+
		"\u00ab\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ae\3\2"+
		"\2\2\u00ad\u00af\5)\25\2\u00ae\u00ad\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b2\3\2\2\2\u00b0\u00b2\5)\25\2\u00b1\u00a7\3\2\2\2\u00b1\u00b0\3\2"+
		"\2\2\u00b2(\3\2\2\2\u00b3\u00b5\t\6\2\2\u00b4\u00b6\t\5\2\2\u00b5\u00b4"+
		"\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b8\3\2\2\2\u00b7\u00b9\5%\23\2\u00b8"+
		"\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2"+
		"\2\2\u00bb*\3\2\2\2\u00bc\u00bd\7`\2\2\u00bd,\3\2\2\2\u00be\u00bf\7,\2"+
		"\2\u00bf.\3\2\2\2\u00c0\u00c1\7\61\2\2\u00c1\60\3\2\2\2\u00c2\u00c3\7"+
		"(\2\2\u00c3\62\3\2\2\2\u00c4\u00c5\7@\2\2\u00c5\u00c6\7?\2\2\u00c6\64"+
		"\3\2\2\2\u00c7\u00c8\7>\2\2\u00c8\u00c9\7?\2\2\u00c9\66\3\2\2\2\u00ca"+
		"\u00cb\7>\2\2\u00cb\u00cc\7@\2\2\u00cc8\3\2\2\2\u00cd\u00ce\7@\2\2\u00ce"+
		":\3\2\2\2\u00cf\u00d0\7>\2\2\u00d0<\3\2\2\2\u00d1\u00d2\7?\2\2\u00d2>"+
		"\3\2\2\2\27\2]imouw}\177\u0081\u008d\u008f\u0091\u009b\u00a0\u00a5\u00ab"+
		"\u00ae\u00b1\u00b5\u00ba";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}