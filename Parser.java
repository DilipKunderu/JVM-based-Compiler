package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;

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
	void parse() throws SyntaxException {
		program();
		matchEOF();
		return;
	}

	void expression() throws SyntaxException {
		//TODO
		term();
		while(isRelOp()) {
			consume();
			term();
		}
	}

	void term() throws SyntaxException {
		//TODO
		elem();
		while (isWeakOp()) {
			consume();
			elem();
		}
	}

	void elem() throws SyntaxException {
		//TODO
		factor();
		while(isStrongOp()) {
			consume();
			factor();
		}
	}

	void factor() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
		}
			break;
		case INT_LIT: {
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("Expected token kind is either "
					+ "IDENT, INT_LIT, KW_TRUE, KW_FALSE, "
					+ "KW_SCREENWIDTH, KW_SCREENHEIGHT received is " + t.kind);
		}
	}

	void block() throws SyntaxException {
		//TODO
		match(LBRACE);
		while(!t.isKind(RBRACE)) {
			if (isDecStart()) {
				dec();
			} else {
				statement();
			}
		}
		match(RBRACE);
	}

	void program() throws SyntaxException {
		//TODO
		if (t.isKind(IDENT)) {
			consume();
			if (!t.isKind(LBRACE)) {
				paramDec();
				while(t.isKind(COMMA)) {
					consume();
					paramDec();
				}
			}
			block();
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
	
	void paramDec() throws SyntaxException {
		//TODO
		if (isParamDec()) {
			consume();
			match(IDENT);
			return;
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
	
	void dec() throws SyntaxException {
		//TODO
		if (isDecStart()) {
			consume();
			match(IDENT);
			return;
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
	
	void statement() throws SyntaxException {
		//TODO
		if (t.isKind(IDENT)){
			consume();
			if (t.isKind(ASSIGN)) {
				consume();
				expression();
			} else {
				subChain();
			}
			match(SEMI);
		} else if (stmtMayStart()) {
			if (t.isKind(OP_SLEEP)) {
				consume();
				expression();
				match(SEMI);
			} else if (t.isKind(KW_WHILE)) {
				whileStatement();
			} else if (t.isKind(KW_IF)) {
				ifStatement();
			}
		} else if (stmtMayAlsoStarts()) {	
			consume();
			arg();
			subChain();
			match(SEMI);
		} else {
			throw new SyntaxException("Exception in parsing statement");
		}
	}

	void assign() throws SyntaxException {
		match(IDENT);
		match(ASSIGN);
		expression();
	}
	
	void ifStatement() throws SyntaxException {
		if(t.isKind(KW_IF)) {
			controlStatements();
			return;
		}
		 throw new SyntaxException("Token expected is kind KW_IF received is " + t.kind);
	}
	
	void whileStatement() throws SyntaxException {
		if (t.isKind(KW_WHILE )) {
			controlStatements();
			return;
		}
		throw new SyntaxException("Token expected is kind KW_IF received is " + t.kind);
	}
	
	void controlStatements() throws SyntaxException {
		consume();
		match(LPAREN);
		expression();
		match(RPAREN);
		block();
	}

	void chain() throws SyntaxException {
		//TODO
		chainElem();
		subChain();
	}

	void chainElem() throws SyntaxException {
		//TODO
		Kind k = t.kind;
		switch (k) {
		case IDENT:
			consume();
			break;
		default:
			if (isFilterOp() || isFrameOp() || isImageOp()) {
				consume();
				arg();
			} else {
				 throw new SyntaxException("Token expected is of Op type "
					 		+ "Filter or Frame or Image but received token is " + t.kind);
			}
		}
	}
	
	void subChain() throws SyntaxException {
		arrowOp();
		chainElem();
		while(isArrowOp()) {
			arrowOp();
			chainElem();
		}
	}
	
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
	
	void arg() throws SyntaxException {
		//TODO
		if (t.isKind(LPAREN)) {
			consume();
			expression();
			while(t.isKind(COMMA)) {
				consume();
				expression();
			}
			match(RPAREN);
		}
	}

	boolean isArrowOp() {
		if (t.isKind(ARROW) || t.isKind(BARARROW)) {
			return true;
		}
		return false;
	}
	
	void arrowOp() throws SyntaxException {
		if (isArrowOp()) {	
			consume();
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
