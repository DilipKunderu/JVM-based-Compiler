package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		Program program = program();
		matchEOF();
		return program;
	}

	Expression expression() throws SyntaxException {
		//TODO
		Token firstToken = t;
		Expression e0 = term();
		Expression e1 = null;
		while(isRelOp()) {
			Token op = t;
			consume();
			e1 = term();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		return e0;
	}

	Expression term() throws SyntaxException {
		//TODO
		Token firstToken = t;
		Expression e0 = elem();
		Expression e1 = null;
		while (isWeakOp()) {
			// PLUS | MINUS | OR
			Token op = t;
			consume();
			e1 = elem();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		return e0;
	}

	Expression elem() throws SyntaxException {
		//TODO
		Token firstToken = t;
		//todo: should the factor and all the sub routines have the same firstToken?
		Expression e0 = factor();
		Expression e1 = null;
		while(isStrongOp()) {
			//TIMES | DIV | AND | MOD
			Token op = t;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		return e0;
	}

	Expression factor() throws SyntaxException {
		Kind kind = t.kind;
		Token firstToken = t;
		Expression e = null;
		switch (kind) {
		case IDENT: {
			consume();
			e = new IdentExpression(firstToken);
		}
			break;
		case INT_LIT: {
			consume();
			e = new IntLitExpression(firstToken);
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
			e = new BooleanLitExpression(firstToken);
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
			e = new ConstantExpression(firstToken);
		}
			break;
		case LPAREN: {
			consume();
			e = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("Expected token kind is either "
					+ "IDENT, INT_LIT, KW_TRUE, KW_FALSE, "
					+ "KW_SCREENWIDTH, KW_SCREENHEIGHT received is " + t.kind);
		}
		return e;
	}

	Block block() throws SyntaxException {
		//TODO
		Token firstToken = match(LBRACE);
		ArrayList<Dec> decList = new ArrayList<>();
		ArrayList<Statement> stmtList = new ArrayList<>();
		while(!t.isKind(RBRACE)) {
			if (isDecStart()) {
				decList.add(dec());
			} else {
				stmtList.add(statement());
			}
		}
		match(RBRACE);
		return new Block(firstToken, decList, stmtList);
	}

	Program program() throws SyntaxException {
		//TODO
		if (t.isKind(IDENT)) {
			Token firstToken = consume();
			ArrayList<ParamDec> list = new ArrayList<>();
			if (!t.isKind(LBRACE)) {
				list.add(paramDec());
				while(t.isKind(COMMA)) {
					consume();
					list.add(paramDec());
				}
			}
			Block blockObj = block();
			return new Program(firstToken, list, blockObj);
		} else {
			throw new SyntaxException("Expected token kind is IDENT received is " + t.kind);
		}
	}

	boolean isParamDec() {
		if (t.isKind(KW_URL) || t.isKind(KW_FILE) 
				|| t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)) {
			return true;
		}
		return false;
	}
	
	ParamDec paramDec() throws SyntaxException {
		//TODO
		if (isParamDec()) {
			Token firstToken = consume();
			Token identToken = match(IDENT);
			ParamDec paramDec = new ParamDec(firstToken, identToken);
			return paramDec;
		}
		throw new SyntaxException("Expected token kinds KW_URL, KW_FILE, KW_INTEGER, "
				+ "KW_BOOLEAN but received is " + t.kind);
	}

	boolean isDecStart() {
		if (t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) 
				|| t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)) {
			return true;
		}
		return false;
	}
	
	Dec dec() throws SyntaxException {
		//TODO
		if (isDecStart()) {
			Token firstToken = consume();
			Token identToken = match(IDENT);
			Dec dec = new Dec(firstToken, identToken);
			return dec;
		}
		throw new SyntaxException("Exception in parsing dec.");
	}

	boolean stmtMayStart() {
		if (t.isKind(OP_SLEEP) || t.isKind(KW_WHILE) || t.isKind(Kind.KW_IF)) {
			return true;
		}
		return false;
	}
	
	boolean stmtMayAlsoStarts() {
		if (isFilterOp() || isFrameOp() || isImageOp()) {
			return true;
		}
		 return false;
	}
	
	Statement statement() throws SyntaxException {
		//TODO
		Token firstToken = t;
		if (t.isKind(IDENT)){
			consume();
			if (t.isKind(ASSIGN)) {
				consume();
				Expression e = expression();
				//todo: check if firstToken is what needs to save
				match(SEMI);
				return new AssignmentStatement(firstToken, new IdentLValue(firstToken), e);
			} else {
//				subChain();
				IdentChain e0 = new IdentChain(firstToken);
				Token tArrow = arrowOp();
				ChainElem e1 = chainElem();
				BinaryChain bChain = new BinaryChain(firstToken, e0, tArrow, e1);
				while(isArrowOp()) {
					tArrow = arrowOp();
					e1 = chainElem();
					bChain = new BinaryChain(firstToken, bChain, tArrow, e1);
				}
				match(SEMI);
				return bChain;
			}
		} else if (stmtMayStart()) {
			if (t.isKind(OP_SLEEP)) {
				consume();
				Expression e = expression();
				match(SEMI);
				return new SleepStatement(firstToken, e);
			} else if (t.isKind(KW_WHILE)) {
				return whileStatement();
			} else if (t.isKind(KW_IF)) {
				return ifStatement();
			}
			return null;
		} else if (stmtMayAlsoStarts()) {	
//			consume();
//			arg();
//			subChain();
			Chain chain = chain();
			match(SEMI);
			return chain;
		} else {
			throw new SyntaxException("Exception in parsing statement");
		}
	}

	void assign() throws SyntaxException {
		match(IDENT);
		match(ASSIGN);
		expression();
	}
	
	IfStatement ifStatement() throws SyntaxException {
		if(t.isKind(KW_IF)) {
			Token firstToken = t;
			consume();
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			Block b = block();
			return new IfStatement(firstToken, e, b);
		}
		 throw new SyntaxException("Token expected is kind KW_IF received is " + t.kind);
	}
	
	WhileStatement whileStatement() throws SyntaxException {
		if (t.isKind(KW_WHILE )) {	
			Token firstToken = t;
			consume();
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			Block b = block();
			return new WhileStatement(firstToken, e, b);
		}
		throw new SyntaxException("Token expected is kind KW_IF received is " + t.kind);
	}

	Chain chain() throws SyntaxException {
		//TODO
//		chainElem();
//		subChain();
		//new method
		Token firstToken = t;
		ChainElem e0 = chainElem();
		Token tArrow = arrowOp();
		ChainElem e1 = chainElem();
		BinaryChain bChain = new BinaryChain(firstToken, e0, tArrow, e1);
		while(isArrowOp()) {
			tArrow = arrowOp();
			e1 = chainElem();
			bChain = new BinaryChain(firstToken, bChain, tArrow, e1);
		}
		return bChain;
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		Kind k = t.kind;
		Token firstToken = t;
		switch (k) {
		case IDENT:
			consume();
			return new IdentChain(firstToken);
		default:
			if (isFilterOp()) {
				consume();
				Tuple tuple = arg();
				return new FilterOpChain(firstToken, tuple);
			} else if (isFrameOp()) {
				consume();
				Tuple tuple1 = arg();
				return new FrameOpChain(firstToken, tuple1);
			} else if (isImageOp()) {
				consume();
				Tuple tuple2 = arg();
				return new ImageOpChain(firstToken, tuple2);
			} else {
				 throw new SyntaxException("Token expected is of Op type "
					 		+ "Filter or Frame or Image but received token is " + t.kind);
			}
		}
	}
/*	
	void subChain(BinaryChain bChain, Token firstToken) throws SyntaxException {
		arrowOp();
		chainElem();
		while(isArrowOp()) {
			arrowOp();
			chainElem();
		}
	}
*/	
	boolean isFilterOp() {
		if (t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE)) {
			return true;
		}
		return false;
	}
	
	void filterOp() throws SyntaxException {
		if (isFilterOp()) {
			consume();
		} else {
			 throw new SyntaxException("Token expected is either of kind "
				 		+ "OP_BLUR, OP_GRAY, OP_CONVOLVE but received " + t.kind);
		}
	}
	
	boolean isFrameOp() {
		if (t.isKind(KW_SHOW) || t.isKind(KW_HIDE) 
				|| t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC) ) {
			return true;
		}
		return false;
	}
	
	void frameOp() throws SyntaxException {
		if (isFrameOp()) {
			consume();
		} else {
			 throw new SyntaxException("Token expected is either of kind "
				 		+ "KW_SHOW, KW_HIDE, KW_MOVE, KW_XLOC, KW_YLOC but received " + t.kind);
		}
	}
	
	boolean isImageOp() {
		if (t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)) {
			return true;
		}
		return false;
	}
	
	void imageOp() throws SyntaxException {
		if (isImageOp()) {
			consume();
		} else {
			 throw new SyntaxException("Token expected is either of kind "
				 		+ "OP_WIDTH, OP_HEIGHT, KW_SCALE but received " + t.kind);
		}
	}
	
	Tuple arg() throws SyntaxException {
		//TODO
		ArrayList<Expression> list = new ArrayList<>();
		Token firstToken = t;
		if (t.isKind(LPAREN)) {
			consume();
			list.add(expression());
			while(t.isKind(COMMA)) {
				consume();
				list.add(expression());
			}
			match(RPAREN);
		}
		return new Tuple(firstToken, list);
	}

	boolean isArrowOp() {
		if (t.isKind(ARROW) || t.isKind(BARARROW)) {
			return true;
		}
		return false;
	}
	
	Token arrowOp() throws SyntaxException {
		if (isArrowOp()) {	
			return consume();
		} else {
			throw new SyntaxException("Tokens expected are ARROW, BARARROW "
					+ "received is " + t.kind);
		}
	}
	
	boolean isRelOp() {
		if (t.isKind(LT) || t.isKind(LE) || t.isKind(GT) 
				|| t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) {
			return true;
		}
		return false;
	}
	
	void relOp() throws SyntaxException {
		if (isRelOp()) {
			consume();
		} else {
			 throw new SyntaxException("Token expected is either of kind "
				 		+ "LT, LE, GT, GE, EQUAL, NOTEQUAL but received " + t.kind);
		}
	}
	
	boolean isWeakOp() {
		if (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)) {
			return true;
		}
		return false;
	}
	
	void weakOp() throws SyntaxException {
		if (isWeakOp()) {
			consume();
		} else {
			 throw new SyntaxException("Token expected is either of kind "
				 		+ "PLUS, MINUS, OR but received " + t.kind);
		}
	}
	
	boolean isStrongOp() {
		if (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)) {
			return true;
		}
		return false;
	}
	
	void strongOp() throws SyntaxException {
		if (isStrongOp()) {
			consume();
		} else {
			 throw new SyntaxException("Token expected is either of kind "
			 		+ "TIMES, DIV, AND, MOD but received " + t.kind);
		}
	}
	
	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
