package cop5556sp17;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			if (kind == Kind.EOF) return "eof";
			return chars.substring(pos, pos + length);
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			return getLinePosObj(pos);
		}
		
		boolean isKind(Kind k) {
			return k.equals(kind);
		}
		
		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			try {
				return Integer.parseInt(chars.substring(pos, pos + length));
			} catch (Exception e) {
				throw new NumberFormatException("Received token is not integer type. Received kind is " 
						+ kind);
			}
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}
		
		private Scanner getOuterType() {
			return Scanner.this;
		}
		
	}

	LinePos getLinePosObj(int pos) {
		
		int lineNumber = Collections.binarySearch(lines, pos);
		if (lineNumber < 0) {
			lineNumber = -(lineNumber + 2);
		}
		int posInLine = pos - lines.get(lineNumber);
		return new LinePos(lineNumber, posInLine);
	}	 


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		this.lines = new ArrayList<>();
		lines.add(0);
		createKeyWordMap();
	}

	private void createKeyWordMap() {
		kwMap = new HashMap<>();
		for (Kind k: Kind.values()) {
			if (Kind.EOF != k
					&& k.getText().length() > 0 
					&& Character.isJavaIdentifierStart(k.getText().charAt(0))) {
				kwMap.put(k.getText(), k);
			}
		}
	}
	
	private enum State {
		START("start"),
		IN_DIGIT("inDigit"),
		IN_IDENT("inIdent"),
		AFTER_EQ("afterEq"),
		AFTER_NOT("afterNot"),
		AFTER_MINUS("afterMinus"),
		AFTER_LT("afterLt"),
		AFTER_GT("afterGt"),
		AFTER_OR("afterOr"),
		AFTER_DIV("afterDiv"),
		IN_MULTIPLE_LINE_COMMENT("InMultipleLineComment"),
		IS_DIV_SIGN("isDivSign");
		
		State (String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
	
	public int skipWhiteSpaces(int pos) {
		while(pos < chars.length() && Character.isWhitespace(chars.charAt(pos))) {
			if (chars.charAt(pos) == '\n') {
				lines.add(pos+1);
			}
			pos++;
		}
		return pos;
	}

	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//TODO IMPLEMENT THIS!!!!
		
		int length = chars.length();
		State state = State.START;
		int startPos = 0;
		int ch;
		while(pos <= length) {
			ch = pos < length ? chars.charAt(pos) : -1;
			switch (state) {
			case START:
				pos = skipWhiteSpaces(pos);
				ch = pos < length ? chars.charAt(pos) : -1;
				startPos = pos;
				switch(ch) {
					
					//single length tokens
					case -1:
						tokens.add(new Token(Kind.EOF, pos, 0)); 
						pos++;
						break;
					case '0':
						tokens.add(new Token(Kind.INT_LIT, startPos, 1));
						pos++;
						break;
					case ';':
						tokens.add(new Token(Kind.SEMI, startPos, 1));
						pos++;
						break;
					case ',':
						tokens.add(new Token(Kind.COMMA, startPos, 1));
						pos++;
						break;
					case '&':
						tokens.add(new Token(Kind.AND, startPos, 1));
						pos++;
						break;
					case '+':
						tokens.add(new Token(Kind.PLUS, startPos, 1));
						pos++;
						break;
					case '*':
						tokens.add(new Token(Kind.TIMES, pos, 1));
						pos++;
						break;
					case '%':
						tokens.add(new Token(Kind.MOD, startPos, 1));
						pos++;
						break;
					case '(':
						tokens.add(new Token(Kind.LPAREN, startPos, 1));
						pos++;
						break;
					case ')':
						tokens.add(new Token(Kind.RPAREN, startPos, 1));
						pos++;
						break;
					case '{':
						tokens.add(new Token(Kind.LBRACE, startPos, 1));
						pos++;
						break;
					case '}':
						tokens.add(new Token(Kind.RBRACE, startPos, 1));
						pos++;
						break;
						
					//complex tokens
					case '!':
						state = State.AFTER_NOT;
						pos++;
						break;
					case '=':
						state = State.AFTER_EQ;
						pos++;
						break;
					case '-':
						state = State.AFTER_MINUS;
						pos++;
						break;
					case '<':
						state = State.AFTER_LT;
						pos++;
						break;
					case '>':
						state = State.AFTER_GT;
						pos++;
						break;
					case '|':
						state = State.AFTER_OR;
						pos++;
						break;
					case '/':
						state = State.AFTER_DIV;
						pos++;
						break;
						
					default: 
						if (Character.isDigit(ch)) {
							state = State.IN_DIGIT;
							pos++;
							break;
						} else if (Character.isJavaIdentifierStart(ch)) {
							state = State.IN_IDENT;
							pos++;
						} else {
							LinePos lp = getLinePosObj(startPos);
							throw new IllegalCharException("Illegal Character encountered at line no " + lp.line
									+ " and column no " + lp.posInLine + ". Received char is " + ch);
						}
					}
					break;
			case IN_DIGIT:
				if (Character.isDigit(ch)) {
					pos++;
				} else {
					try {
						int i = Integer.parseInt(chars.substring(startPos, pos));
					} catch(Exception e) {
						LinePos lp = getLinePosObj(startPos);
						String str = "Received value at line number " + lp.line + " and column no " 
								+ lp.posInLine + ", cannot be represented using int";
						throw new IllegalNumberException(str);
					}
					tokens.add(new Token(Kind.INT_LIT, startPos, pos- startPos));
					state = State.START;
				}
				break;
			case IN_IDENT:
				if (Character.isJavaIdentifierPart(ch)) {
					pos++;
				} else {
					Kind k = Kind.IDENT;
					if (kwMap.containsKey(chars.substring(startPos, pos))) {
						k = kwMap.get(chars.substring(startPos, pos));
					}
					tokens.add(new Token(k, startPos, pos -startPos));
					state = State.START;
				}
				break;
			case AFTER_EQ:
				if (ch == '=') {
					tokens.add(new Token(Kind.EQUAL, startPos, 2));
					state = State.START;
					pos++;
				} else {
					LinePos lp = getLinePosObj(pos);
					String str = "Received char i.e. " + ch + " at line number " + lp.line + " and column no " 
							+ lp.posInLine + ", is illegal.";
					throw new IllegalCharException(str);
				}
				break;
			case AFTER_NOT:
				if (ch == '=') {
					tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
					pos++;
				} else {
					tokens.add(new Token(Kind.NOT, startPos, 1));
				}
				state = State.START;
				break;
			case AFTER_MINUS:
				if (ch == '>') {
					tokens.add(new Token(Kind.ARROW, startPos, 2));
					pos++;
				} else {
					tokens.add(new Token(Kind.MINUS, startPos, 1));
				}
				state = State.START;
				break;
				
			case AFTER_LT:				
				if (ch == '-') {
					tokens.add(new Token(Kind.ASSIGN, startPos, 2));
					pos++;
				} else if (ch == '=') {
					tokens.add(new Token(Kind.LE, startPos, 2));
					pos++;
				} else {
					tokens.add(new Token(Kind.LT, startPos, 1));
				}
				state = State.START;
				break;
			case AFTER_GT:
				if (ch == '=') {
					tokens.add(new Token(Kind.GE, startPos, 2));
					pos++;
				} else {
					tokens.add(new Token(Kind.GT, startPos, 1));
				}
				state = State.START;
				break;
			case AFTER_OR:
				if (ch == '-' && (pos+1) < length && chars.charAt(pos+1) == '>') {
					tokens.add(new Token(Kind.BARARROW, startPos, 3));
					pos = pos + 2;
				} else {
					tokens.add(new Token(Kind.OR, startPos, 1));
				}
				state = State.START;
				break;
			
			case AFTER_DIV:
				if (ch == '*') {
					state = State.IN_MULTIPLE_LINE_COMMENT;
					pos++;
				} else {
					tokens.add(new Token(Kind.DIV, startPos, 1));
					state = State.START;
				}
				break;

			case IN_MULTIPLE_LINE_COMMENT:
				
				switch(ch) {
				case '*':
					state = State.IS_DIV_SIGN;
					break;
				case '\n':
					lines.add(pos+1);
					break;
				case -1:
					tokens.add(new Token(Kind.EOF, pos,0));
					break;
				default: 
					break;
				}
				
				pos++;
				break;

			case IS_DIV_SIGN:
				
				switch(ch) {
				case '/':
					state = State.START;
					break;
				case '\n':
					lines.add(pos+1);
					break;
				case -1:
					tokens.add(new Token(Kind.EOF,pos,0));
					break;
				case '*':
					break;
				default:
					state = State.IN_MULTIPLE_LINE_COMMENT;
				}	
				pos++;
				break;
			default: assert false;
			}	
		}	
		return this;  
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	
	List<Integer> lines; 			//used to save the starting postion of lines.
	Map<String, Kind> kwMap;		//used to store all the keywords in a hashmap.

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
//		return tokens.get(tokenNum+1);
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
//		return null;
		return t.getLinePos();
	}


}
