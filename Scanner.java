package cop5556sp17;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//import cop5556sp17.Scanner.Kind;

public class Scanner {
	/**
	 * Kind enum
	 */

	public static enum Kind {
		IDENT(""), 
		INT_LIT(""), 
		 
		SEMI(";"), 
		COMMA(","), 
		LPAREN("("), 
		RPAREN(")"), 
		LBRACE("{"), 
		RBRACE("}"), 
		ARROW("->"), 
		BARARROW("|->"), 
		OR("|"), 
		AND("&"), 
		EQUAL("=="), 
		NOTEQUAL("!="), 
		LT("<"), 
		GT(">"), 
		LE("<="), 
		GE(">="), 
		PLUS("+"), 
		MINUS("-"), 
		TIMES("*"), 
		DIV("/"), 
		MOD("%"), 
		NOT("!"), 
		ASSIGN("<-"),
		//add to map
		OP_BLUR("blur"), 
		OP_GRAY("gray"), 
		OP_CONVOLVE("convolve"), 
		OP_WIDTH("width"), 
		OP_HEIGHT("height"),
		OP_SLEEP("sleep"),
		KW_SCREENHEIGHT("screenheight"), 
		KW_SCREENWIDTH("screenwidth"), 
		KW_INTEGER("integer"), 
		KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), 
		KW_URL("url"), 
		KW_FILE("file"), 
		KW_FRAME("frame"), 
		KW_WHILE("while"), 
		KW_IF("if"), 
		KW_TRUE("true"), 
		KW_FALSE("false"),
		KW_XLOC("xloc"), 
		KW_YLOC("yloc"), 
		KW_HIDE("hide"), 
		KW_SHOW("show"), 
		KW_MOVE("move"),  
		KW_SCALE("scale"),
		
		EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
	
	static HashMap<String, Kind> keywordMap = new HashMap<>();
	static ArrayList<Integer> row;

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
	 * Thrown by Scanner when an int literal is not a value that can be
	 * represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
		public IllegalNumberException(String message) {
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
		public final int pos; // position in input array
		public final int length;

		// returns the text of this Token
		public String getText() {
			// TODO IMPLEMENT THIS
			
			return chars.substring(pos, pos + length); 
			//return null;
		}

		// returns a LinePos object representing the line and column of this
		// Token
		LinePos getLinePos() {
			// TODO IMPLEMENT THIS
			int temp = Collections.binarySearch(row, pos);//-x -2;
			if (temp < 0)
				temp = -temp-2;
			
			LinePos linePos = new LinePos(temp, pos - row.get(temp));
			return linePos;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/**
		 * Precondition: kind = Kind.INT_LIT, the text can be represented with a
		 * Java int. Note that the validity of the input should have been
		 * checked when the Token was created. So the exception should never be
		 * thrown.
		 * 
		 * @return int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException {
			// TODO IMPLEMENT THIS
			return Integer.parseInt(chars.substring(pos, pos + length));
		}

		public boolean isKind(Kind kind) {
			// TODO Auto-generated method stub
			return this.kind.equals(kind);
		}

	}

	private static enum State {
		START, IN_DIGIT, IN_IDENT, AFTER_EQ, AFTER_NOT, AFTER_DASH, AFTER_OR, AFTER_LT, AFTER_GT, AFTER_DIV
	}
	
	public static int skipWhiteSpace(int pos, String chars) {
		//boolean containsWhitespace = false;
		while (pos < chars.length() && Character.isWhitespace(chars.charAt(pos))) {
			if (chars.charAt(pos) == '\n')
				row.add(pos+1);
			pos++;
		}	
		return pos;
	}
	
	
	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		row = new ArrayList<>();
		row.add(0);
	}
	
	

	/**
	 * Initializes Scanner object by traversing chars and adding tokens to
	 * tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		keywordMap.put("screenheight", Kind.KW_SCREENHEIGHT);
		keywordMap.put("screenwidth", Kind.KW_SCREENWIDTH);
		keywordMap.put("integer", Kind.KW_INTEGER);
		keywordMap.put("boolean", Kind.KW_BOOLEAN);
		keywordMap.put("image", Kind.KW_IMAGE);
		keywordMap.put("url",Kind.KW_URL);
		keywordMap.put("file", Kind.KW_FILE);
		keywordMap.put("frame", Kind.KW_FRAME);
		keywordMap.put("while", Kind.KW_WHILE);
		keywordMap.put("if", Kind.KW_IF);
		keywordMap.put("true", Kind.KW_TRUE);
		keywordMap.put("false", Kind.KW_FALSE);
		keywordMap.put("xloc", Kind.KW_XLOC);
		keywordMap.put("yloc", Kind.KW_YLOC);
		keywordMap.put("hide", Kind.KW_HIDE);
		keywordMap.put("show", Kind.KW_SHOW);
		keywordMap.put("move", Kind.KW_MOVE);
		keywordMap.put("scale", Kind.KW_SCALE);
		keywordMap.put("blur", Kind.OP_BLUR);
		keywordMap.put("gray", Kind.OP_GRAY);
		keywordMap.put("convolve", Kind.OP_CONVOLVE);
		keywordMap.put("width", Kind.OP_WIDTH);
		keywordMap.put("height", Kind.OP_HEIGHT);
		keywordMap.put("sleep", Kind.OP_SLEEP);
		
		int pos = 0;
		int length = chars.length();
		//System.out.println(chars);
		State state = State.START;
		int startPos = 0;
		int ch;

		while (pos <= length) {
			//System.out.println(pos);
			ch = pos < length ? chars.charAt(pos) : -1;

			switch (state) {
			case START: {
				pos = skipWhiteSpace(pos, chars);
				ch = pos < length ? chars.charAt(pos) : -1;
				startPos = pos;
				switch (ch) {
				
					//simple cases
					case -1: {
						tokens.add(new Token(Kind.EOF, pos, 0));
						pos++;
					}
						break;
						
					case '+': {
						tokens.add(new Token(Kind.PLUS, startPos, 1));
						pos++;
					}
						break;
						
					case '*': {
						tokens.add(new Token(Kind.TIMES, startPos, 1));
						pos++;
					}
						break;
						
					case '0': {
						tokens.add(new Token(Kind.INT_LIT, startPos, 1));
						pos++;
					}
						break;
						
					case ';' : {
						tokens.add(new Token(Kind.SEMI, startPos, 1));
						//System.out.println("hi");
						pos++;
					}
						break;
						
					case ',' : {
						tokens.add(new Token(Kind.COMMA, startPos, 1));
						pos++;
					}
						break;
						
					case '{' : {
						tokens.add(new Token(Kind.LBRACE, startPos, 1));
						pos++;
					}
						break;
						
					case '}' : {
						tokens.add(new Token(Kind.RBRACE, startPos, 1));
						pos++;
					}
						break;
						
					case '(' : {
						tokens.add(new Token(Kind.LPAREN, startPos, 1));
						pos++;
					}
						break;
						
					case ')' : {
						tokens.add(new Token(Kind.RPAREN, startPos, 1));
						pos++;
					}
						break;
						
					case '&' : {
						tokens.add(new Token(Kind.AND, startPos, 1));
						pos++;
					}
						break;
						
					case '%' : {
						tokens.add(new Token(Kind.MOD, startPos, 1));
						pos++;
					}
						break;
						
					case '=': {
						state = State.AFTER_EQ;
						pos++;
					}
						break;
						
					case '!' : {
						state = State.AFTER_NOT;
						pos++;
					}
						break;
					
					case '-' : {
						state = State.AFTER_DASH;
						pos++;
					}
						break;
						
					case '|' : {
						state = State.AFTER_OR;
						pos++;
					}
						break;
						
					case '<' : {
						state = State.AFTER_LT;
						pos++;
					}
						break;
						
					case '>' : {
						state = State.AFTER_GT;
						pos++;
					}
						break;
						
					case '/' : {
						state = State.AFTER_DIV;
						pos++;
					}
						break;
					//comment case
					default: {
						if (Character.isDigit(ch)) {
							state = State.IN_DIGIT;
							pos++;
						} else if (Character.isJavaIdentifierStart(ch)) {
							state = State.IN_IDENT;
							pos++;
						} else {
							throw new IllegalCharException("illegal char " + ch + " at pos " + pos);
						}
					}
				}
			}
				break;
			case IN_DIGIT: {
				if (Character.isDigit(ch))
					pos++;
				else {
					try {
						@SuppressWarnings("unused")
						int i = Integer.parseInt(chars.substring(startPos, pos));
					}
					catch (Exception e) {
						throw new IllegalNumberException("Integer out of boundaries");
					}
					tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
					state = State.START;
				}
			}
				break;
				
			case IN_IDENT: {
				if (Character.isJavaIdentifierPart(ch)) {
					pos++;
				} else {
					if (keywordMap.containsKey(chars.substring(startPos, pos))) {
						tokens.add(new Token(keywordMap.get(chars.substring(startPos, pos)),startPos,pos - startPos));
					} else {
						tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
					}
						state = State.START;
				}
			}
				break;
				
			case AFTER_EQ: {
				if (ch == '=') {
					tokens.add(new Token(Kind.EQUAL, startPos, 2));
					pos++;
					state = State.START;
					
				}
				else {
					throw new IllegalCharException("illegal char " + ch + " at pos " + pos); 
				}
				//state = State.START;
			}
				break;
				
			case AFTER_NOT : {
				if (ch == '=') {
					tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
					pos++;
				}
				else 
					tokens.add(new Token(Kind.NOT, startPos, 1));
				state = State.START;
			}
				break;
				
			case AFTER_DASH : {
				if (ch == '>') { 
					tokens.add(new Token(Kind.ARROW, startPos, 2));
					pos++;
				}
				else
					tokens.add(new Token (Kind.MINUS, startPos,1));
				state = State.START;
			}
				break;
			
			case AFTER_OR : {
				if (ch == '-') {
					if ((pos + 1) < length && chars.charAt(pos + 1) == '>') {
						tokens.add(new Token(Kind.BARARROW, startPos, 3));
						pos += 2;
					}
					else {
						tokens.add(new Token(Kind.OR, startPos, 1));
					}
					//else throw new IllegalCharException("illegal char " + ch + " at pos " + pos); 
				}
				else {
					tokens.add(new Token(Kind.OR, startPos, 1));
				}
				state = State.START;
			}
				break;
			
			case AFTER_LT  : {
				if (ch == '=') {
					tokens.add(new Token(Kind.LE, startPos, 2));
					pos++;
				}else if (ch == '-'){
					tokens.add(new Token(Kind.ASSIGN, startPos, 2));
					pos++;
				}else  {
					tokens.add(new Token(Kind.LT, startPos, 1));
				}
				state = State.START;
			}
				break;
				
			case AFTER_GT : {
				if (ch == '='){ 
					tokens.add(new Token(Kind.GE, startPos, 2));
					pos++;
				}else
					tokens.add(new Token(Kind.GT, startPos, 1));
				state = State.START;
			}
				break;
				
			case AFTER_DIV : {
				if(ch == '*'){
					pos++;
					while(pos < length) {
						if (pos < length)
							ch = chars.charAt(pos);
						else
							ch = -1;
						if(ch == '*'){
							pos++;
							
							if(pos < length){
								ch = chars.charAt(pos);
								if (ch == '/'){
									pos++;
									break;
								}
							}
						} 
						else if(ch == '\n'){
							row.add(pos+1);
							pos++;
						} else {
							pos++;
						}
					}
					if(pos == length){
						tokens.add(new Token(Kind.EOF, pos, 0));
						pos++;
					}	
				}else{
					tokens.add(new Token(Kind.DIV, startPos, 1));
				}
				
				state = State.START;
			}
				break;
			default:
				assert false;
			}
		}
		// TODO IMPLEMENT THIS!!!!
		tokens.add(new Token(Kind.EOF, pos, 0));
		return this;
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that the
	 * next call will return the Token..
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}

	 /*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
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
		// TODO IMPLEMENT THIS
		return t.getLinePos();
	}

}