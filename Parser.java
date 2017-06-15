package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;

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
		Program p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException {
		//TODO
		Token _init = t;
		Expression e0 = null;
		Expression e1 = null;
		
		e0 = term();
		while (t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) {
			Token op = t;
			consume();
			e1 = term();
			e0 = new BinaryExpression(_init, e0, op, e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression term() throws SyntaxException {
		//TODO
		Token _init = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = elem();
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)) {
			Token op = t;
			consume();
			e1 = elem();
			e0 = new BinaryExpression(_init, e0, op, e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression elem() throws SyntaxException {
		//TODO
		Token _init = t;
		Expression e0 = null;
		Expression e1 = null;
		
		e0 = factor();
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)) {
			Token op = t;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(_init, e0, op, e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression factor() throws SyntaxException {
		Expression e = null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			e = new IdentExpression(t);
			consume();
		}
			break;
		case INT_LIT: {
			e = new IntLitExpression(t);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			e = new BooleanLitExpression(t);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			e = new ConstantExpression(t);
			consume();
		}
			break;
		case LPAREN: {
			//Token temp = t;
			consume();
			//Expression tempExp = expression();
			//if (tempExp instanceof BinaryExpression) {
			//	e = new BinaryExpression(temp, ((BinaryExpression) tempExp).getE0(), t, ((BinaryExpression) tempExp).getE1());
			//}
			//else {
				e = expression();
			//}
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		return e;
	}

	Block block() throws SyntaxException {
		//TODO
		ArrayList<Dec> decList = new ArrayList<>();
		ArrayList<Statement> statementList = new ArrayList<>();
		
		Token temp = t;
		
		if (t.isKind(LBRACE)) {
			consume();
			while (!t.isKind(RBRACE)) {
				if (t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) ||t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)) {	
					decList.add(new Dec (consume(), match(IDENT)));
				}
				else {
					statementList.add(statement());
				}
			}
			match(RBRACE);
		}
		
		else throw new SyntaxException("Exception thrown by block()");
		//throw new UnimplementedFeatureException();
		return new Block(temp, decList, statementList);
	}

	Program program() throws SyntaxException {
		//TODO
		Program p = null;
		ArrayList<ParamDec> paramList = new ArrayList<>();
		Token op = t;
		Block b;
		
		if (t.isKind(IDENT)) {
			consume();
			
			if (t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
				paramList.add(new ParamDec(consume(), match(IDENT)));
				
				while (t.isKind(COMMA)) {
					consume();
					paramList.add(paramDec());
				}
				b = block();
			}
			
			else if(t.isKind(LBRACE)){
				b = block();
			}else throw new SyntaxException("program() threw exception when second IDENT not detected");
			
			p = new Program(op, paramList, b);
		}
		
		else throw new SyntaxException("thrown by program() when first IDENT is not detected");
		//throw new UnimplementedFeatureException();
		return p;
	}

	ParamDec paramDec() throws SyntaxException {
		//TODO
		ParamDec e = null;
		
		if (t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) ) {
			e = new ParamDec(consume(), match(IDENT));
		}
		else throw new SyntaxException("paramDec() threw exception when appropriate IDENT not detected");
		//throw new UnimplementedFeatureException();
		return e;
	}

	Dec dec() throws SyntaxException {
		//TODO
		Dec e = null;
		
		if (t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) ||t.isKind(KW_IMAGE) || t.isKind(KW_FRAME) || t.isKind(KW_FILE) || t.isKind(KW_URL)) {
			e = new Dec(consume(), match(IDENT));
		}
		else {
			throw new SyntaxException("Exception in dec()");
		}
		return e;
		//throw new UnimplementedFeatureException();
	}

	Statement statement() throws SyntaxException {
		//TODO
		Statement s = null;
		Token tempToken = t;
		Expression expr = null;
		
		if (t.isKind(OP_SLEEP)) {
			//tempToken = t;
			consume();
			expr = expression();
			match(SEMI);
			s = new SleepStatement(tempToken, expr);
		}
		
		else if (t.isKind(KW_WHILE) || t.isKind(KW_IF)) {
			//tempToken = t;
			Token currToken = t;
			consume();
			match(LPAREN);
			expr = expression();
			match(RPAREN);
			if (currToken.isKind(KW_IF))
				s = new IfStatement(tempToken,expr,block());
			else if (currToken.isKind(KW_WHILE))
				s = new WhileStatement(tempToken, expr, block());
		}
		
		else if (t.isKind(IDENT)) {
			Token temp = scanner.peek();
			
			if (temp.isKind(ASSIGN)) {
				consume();
				consume();
				expr = expression();
				s = new AssignmentStatement(tempToken, new IdentLValue(tempToken), expr);
			}
			else  {
				s = chain();
			}
			match(SEMI);
			
		} else {
			s = chain();
			match(SEMI);
		}
		//throw new UnimplementedFeatureException();
		return s;
	}

	Statement assign() throws SyntaxException {
		IdentLValue i = null;
		Expression e = null;
		
		Token op = t;
		if (t.isKind(IDENT)){
			match(IDENT);
			i = new IdentLValue(t);
			match(ASSIGN);
			e = expression();
		}else throw new SyntaxException("assign() threw exception for lack of IDENT");
		return new AssignmentStatement(op, i, e);
	}
	
	//Chain ∷= ChainElem | BinaryChain
	//BinaryChain ∷= Chain (arrow | bararrow)  ChainElem

	Chain chain() throws SyntaxException {
		//TODO
		Chain c0 = null;
		Chain c1 = null;
		ChainElem c2 = null;
		
		Token _init = t;
		c1 = chainElem();
		Token temp = t;
		arrowOp();
		c2 = chainElem();
		c0 = new BinaryChain(_init, c1, temp, c2);
		
		while (t.isKind(ARROW) || t.isKind(BARARROW)) {
			Token temp2 = t;
			consume();
			c2 = chainElem();
			c0 = new BinaryChain(_init, c0, temp2, c2);
		}
		//throw new UnimplementedFeatureException();
		return c0;
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		ChainElem c = null;
		if (t.isKind(IDENT)){
			c = new IdentChain(t);
			match(IDENT);
		}
		else if (t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE)) {
			Token op = t;
			consume();
			c = new FilterOpChain(op,arg());
		}
		else if (t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC)){
			Token op = t;
			consume();
			c = new FrameOpChain(op,arg());
		}
		else if (t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)) {
			Token op = t;
			consume();
			c = new ImageOpChain(op,arg());
		} else {
			throw new SyntaxException("chainElem is called, but did not receive valid token kind");
		}
		//throw new UnimplementedFeatureException();
		return c;
	}

	Tuple arg() throws SyntaxException {
		// TODO
		Token op = t;
		List<Expression> l = new ArrayList<>();
		
		if (t.isKind(LPAREN)) {
			consume();
			l.add(expression());
			while (t.isKind(COMMA)) {
				consume();
				l.add(expression());
			}
			match(RPAREN);
		} 
		return new Tuple(op, l);
		
		// throw new UnimplementedFeatureException();
	}
	
	void arrowOp() throws SyntaxException {
		if (t.isKind(ARROW)) {
			match(ARROW);
		}
		else if (t.isKind(BARARROW)) {
			match(BARARROW); 
		} else {
			throw new SyntaxException("Inside arrowOp but didn't receive valid token kind");
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
		throw new SyntaxException("saw " + t.kind + " expected " + kind);
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
	@SuppressWarnings("unused")
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
