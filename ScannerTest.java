package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
		
	@Test
	public void testClass() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "class abc { \n integer b <- 0;\n integer i <- 10 - b + 10;\n boolean harshita <- i == 0;\n\n while (i <= 5) {\n integer b <- i | 1;\n /* sreeja ears are dumb \n * this is basically that thing right?\n */\n integer c <- i >= 3;\n integer d <- false;\n print(|->)\n }\n}";

		//create and initialize the scanner
		Scanner sc = new Scanner(input);
		sc.scan();
		
		//get first token
		Scanner.Token token = sc.nextToken();
		Scanner.LinePos lp = token.getLinePos();
		
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "class");
		assertEquals(token.length, 5);
		assertEquals(token.pos, 0);
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 0);
		
		//next token
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "abc");
		assertEquals(token.length, 3);
		assertEquals(token.pos, 6);
		lp = token.getLinePos();
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 6);
		
		//next token
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.LBRACE, token.kind);
		assertEquals(token.getText(), "{");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 10);
		lp = token.getLinePos();
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 10);
		
		//next token
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(token.getText(), "integer");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 14);
		lp = token.getLinePos();
		assertEquals(lp.line, 1);
		assertEquals(lp.posInLine, 1);		
		
		//next token
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "b");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 22);
		lp = token.getLinePos();
		assertEquals(lp.line, 1);
		assertEquals(lp.posInLine, 9);	
		
		//next token <-
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(token.getText(), "<-");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 24);
		lp = token.getLinePos();
		assertEquals(lp.line, 1);
		assertEquals(lp.posInLine, 11);	
		
		//next token 0
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "0");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 27);
		lp = token.getLinePos();
		assertEquals(lp.line, 1);
		assertEquals(lp.posInLine, 14);
		
		//next token ;
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 28);
		lp = token.getLinePos();
		assertEquals(lp.line, 1);
		assertEquals(lp.posInLine, 15);		

		//next token integer
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(token.getText(), "integer");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 31);
		lp = token.getLinePos();
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 1);		
		
		//next token i
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "i");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 39);
		lp = token.getLinePos();
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 9);
		
		//next token <-
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(token.getText(), "<-");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 41);
		lp = token.getLinePos();
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 11);
		

		//next token 10
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "10");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 44);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 14);

		//next token -
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.MINUS, token.kind);
		assertEquals(token.getText(), "-");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 47);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 17);
		

		//next token b
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "b");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 49);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 19);
		
		//next token +
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.PLUS, token.kind);
		assertEquals(token.getText(), "+");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 51);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 21);
		
		//next token 10
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "10");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 53);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 23);
				
		//next token ;
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 55);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 25);
		
		//next token boolean
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_BOOLEAN, token.kind);
		assertEquals(token.getText(), "boolean");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 58);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 1);
		
		//next token harshita
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "harshita");
		assertEquals(token.length, 8);
		assertEquals(token.pos, 66);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 9);
		
		//next token <-
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(token.getText(), "<-");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 75);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 18);
		
		//next token i
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "i");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 78);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 21);
		
		//next token ==
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.EQUAL, token.kind);
		assertEquals(token.getText(), "==");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 80);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 23);
		
		//next token 0
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "0");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 83);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 26);
		
		//next token 0
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 84);
		assertEquals(lp.line, 3);
		assertEquals(lp.posInLine, 27);
		
		//next token while
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_WHILE, token.kind);
		assertEquals(token.getText(), "while");
		assertEquals(token.length, 5);
		assertEquals(token.pos, 88);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 1);
		
		//next token (
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.LPAREN, token.kind);
		assertEquals(token.getText(), "(");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 94);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 7);
		
		//next token i
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "i");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 95);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 8);
		
		//next token <=
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.LE, token.kind);
		assertEquals(token.getText(), "<=");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 97);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 10);
		
		//next token 5
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "5");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 100);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 13);
		
		//next token )
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.RPAREN, token.kind);
		assertEquals(token.getText(), ")");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 101);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 14);
		
		//next token {
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.LBRACE, token.kind);
		assertEquals(token.getText(), "{");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 103);
		assertEquals(lp.line, 5);
		assertEquals(lp.posInLine, 16);
		
		//next token integer
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(token.getText(), "integer");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 106);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 1);
		
		
		//next token b
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "b");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 114);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 9);

		//next token <-
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(token.getText(), "<-");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 116);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 11);
		
		//next token i
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "i");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 119);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 14);
		
		//next token |
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.OR, token.kind);
		assertEquals(token.getText(), "|");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 121);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 16);

		//next token 1
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "1");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 123);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 18);
		
		//next token ;
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 124);
		assertEquals(lp.line, 6);
		assertEquals(lp.posInLine, 19);

		//next token integer
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(token.getText(), "integer");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 196);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 1);
		
		//next token c
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "c");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 204);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 9);
		
		//next token <-
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(token.getText(), "<-");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 206);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 11);
		
		//next token i
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "i");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 209);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 14);
		
		//next token >=
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.GE, token.kind);
		assertEquals(token.getText(), ">=");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 211);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 16);
		
		//next token 3
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(token.getText(), "3");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 214);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 19);
		
		
		
		//next token ;
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 215);
		assertEquals(lp.line, 10);
		assertEquals(lp.posInLine, 20);
		
		//next token integer
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(token.getText(), "integer");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 218);
		assertEquals(lp.line, 11);
		assertEquals(lp.posInLine, 1);
		
		//next token d
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "d");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 226);
		assertEquals(lp.line, 11);
		assertEquals(lp.posInLine, 9);
		
		//next token <-
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(token.getText(), "<-");
		assertEquals(token.length, 2);
		assertEquals(token.pos, 228);
		assertEquals(lp.line, 11);
		assertEquals(lp.posInLine, 11);
		
		
		//next token false
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_FALSE, token.kind);
		assertEquals(token.getText(), "false");
		assertEquals(token.length, 5);
		assertEquals(token.pos, 231);
		assertEquals(lp.line, 11);
		assertEquals(lp.posInLine, 14);
		
		//next token ;
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 236);
		assertEquals(lp.line, 11);
		assertEquals(lp.posInLine, 19);		
		
		//next token print
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "print");
		assertEquals(token.length, 5);
		assertEquals(token.pos, 239);
		assertEquals(lp.line, 12);
		assertEquals(lp.posInLine, 1);		
		
		//next token (
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.LPAREN, token.kind);
		assertEquals(token.getText(), "(");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 244);
		assertEquals(lp.line, 12);
		assertEquals(lp.posInLine, 6);
		
		//next token |->
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.BARARROW, token.kind);
		assertEquals(token.getText(), "|->");
		assertEquals(token.length, 3);
		assertEquals(token.pos, 245);
		assertEquals(lp.line, 12);
		assertEquals(lp.posInLine, 7);
		
		//next token )
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.RPAREN, token.kind);
		assertEquals(token.getText(), ")");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 248);
		assertEquals(lp.line, 12);
		assertEquals(lp.posInLine, 10);
		
		//next token }
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.RBRACE, token.kind);
		assertEquals(token.getText(), "}");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 251);
		assertEquals(lp.line, 13);
		assertEquals(lp.posInLine, 1);
		
		//next token }
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.RBRACE, token.kind);
		assertEquals(token.getText(), "}");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 253);
		assertEquals(lp.line, 14);
		assertEquals(lp.posInLine, 0);
		
	}
	
	/**
	 * this test case checks all the the keyword
	 */
	@Test
	public void testKeywords() throws IllegalCharException, IllegalNumberException {
		
		String input = "ylocation yloc image$;== image while tru == true\n sleep "
				+ "screenwidth screenheight scaler scale\n hide_ hide blured blur gray "
				+ "convolve width height sleep file false if  ";
		
		Scanner sc = new Scanner(input);
		sc.scan();
		//get first token
		Scanner.Token token = sc.nextToken();
		Scanner.LinePos lp = token.getLinePos();
		
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "ylocation");
		assertEquals(token.length, 9);
		assertEquals(token.pos, 0);
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 0);
		
		//next token loc
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.KW_YLOC, token.kind);
		assertEquals(token.getText(), "yloc");
		assertEquals(token.length, 4);
		assertEquals(token.pos, 10);
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 10);
		
		//next token image
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "image$");
		assertEquals(token.length, 6);
		assertEquals(token.pos, 15);
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 15);
		
		//next token ;
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(token.getText(), ";");
		assertEquals(token.length, 1);
		assertEquals(token.pos, 21);
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 21);
		
		token = sc.nextToken();
		assertEquals(Kind.EQUAL, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_IMAGE, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_WHILE, token.kind);		
		token = sc.nextToken();
		assertEquals(Kind.IDENT, token.kind);		
		token = sc.nextToken();
		assertEquals(Kind.EQUAL, token.kind);		
		token = sc.nextToken();
		assertEquals(Kind.KW_TRUE, token.kind);		
		token = sc.nextToken();
		assertEquals(Kind.OP_SLEEP, token.kind);		
		token = sc.nextToken();
		assertEquals(Kind.KW_SCREENWIDTH, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_SCREENHEIGHT, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_SCALE, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_HIDE, token.kind);
		
		token = sc.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.OP_BLUR, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.OP_GRAY, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.OP_CONVOLVE, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.OP_WIDTH, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.OP_HEIGHT, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.OP_SLEEP, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_FILE, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_FALSE, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.KW_IF, token.kind);
		token = sc.nextToken();
		assertEquals(Kind.EOF, token.kind);
		
		assertEquals(null, sc.nextToken());
		
		
	}
	
	/**
	 * Test the corner cases involving comments, also checked new line updating in comments
	 */
	@Test
	public void testCorner() throws IllegalCharException, IllegalNumberException {
		
		String input = "newfile/*this \nis a \ncomment*/here /*checking eof case";
		Scanner sc = new Scanner(input);
		sc.scan();
		//get first token
		Scanner.Token token = sc.nextToken();
		Scanner.LinePos lp = token.getLinePos();
		
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "newfile");
		assertEquals(token.length, 7);
		assertEquals(token.pos, 0);
		assertEquals(lp.line, 0);
		assertEquals(lp.posInLine, 0);
		
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(token.getText(), "here");
		assertEquals(token.length, 4);
		assertEquals(token.pos, 30);
		assertEquals(lp.line, 2);
		assertEquals(lp.posInLine, 9);
	
		token = sc.nextToken();
		lp = token.getLinePos();
		assertEquals(Kind.EOF, token.kind);
		
	}
	
	
	/*
	 * checking the illegalchar, illegalnumber and numberformat exceptions
	 */
	@Test
	public void testExceptions() throws IllegalNumberException, IllegalCharException, NumberFormatException {

		String input1 = "!=>>={}\n()*||<> |- > /== -/%>=";
		Scanner scanner = new Scanner(input1);
		scanner.scan();
		Scanner.Token t = scanner.nextToken();
		thrown.expect(NumberFormatException.class);
		t.intVal();
	}
	
	@Test
	public void testExceptions1() throws IllegalNumberException, IllegalCharException, NumberFormatException {
		String input = "121212345332321";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();
	}
	
	@Test
	public void testExceptions2() throws IllegalNumberException, IllegalCharException, NumberFormatException {
		String input1 = "!=>>={}\n()*||<> |- > /= -/%>=";
		Scanner scanner = new Scanner(input1);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	/**
	 * test on idents and int literals
	 */
	@Test
	public void testIdentsIntLiterals() throws IllegalCharException, IllegalNumberException {
		
		String input = "image1 == 123121f, 01234 == ,/* dskj\ndks*/&*/";
		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token t = scanner.nextToken();
		
		assertEquals(Kind.IDENT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.EQUAL, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.INT_LIT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.IDENT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.COMMA, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.INT_LIT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.INT_LIT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.EQUAL, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.COMMA, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.AND, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.TIMES, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.DIV, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.EOF, t.kind);
		
	}
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();
	}

	@Test
	public void test12() throws IllegalCharException, IllegalNumberException {
		String input = "123\rasd";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token t = scanner.nextToken();
		assertEquals(Kind.INT_LIT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.IDENT, t.kind);
		t = scanner.nextToken();
		assertEquals(Kind.EOF, t.kind);
		
	}
	
	@Test
	public void testsingleChars() throws IllegalCharException, IllegalNumberException{
		String input = "<><!\n"
				+ "({)}\n"
				+ "+!=,\n"
				+ "/;%&\n"
				+ "&*";
		
		Scanner sc = new Scanner(input);
		sc.scan();
		Scanner.Token t = sc.nextToken();
		Scanner.LinePos lp = t.getLinePos();
		
		assertEquals(Kind.LT, t.kind);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.GT, t.kind);
		assertEquals(0, lp.line);
		assertEquals(1, lp.posInLine);

		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.LT, t.kind);
		assertEquals(0, lp.line);
		assertEquals(2, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.NOT, t.kind);
		assertEquals(0, lp.line);
		assertEquals(3, lp.posInLine);

		

		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.LPAREN, t.kind);
		assertEquals(1, lp.line);
		assertEquals(0, lp.posInLine);

		
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.LBRACE, t.kind);
		assertEquals(1, lp.line);
		assertEquals(1, lp.posInLine);

		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.RPAREN, t.kind);
		assertEquals(1, lp.line);
		assertEquals(2, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.RBRACE, t.kind);
		assertEquals(1, lp.line);
		assertEquals(3, lp.posInLine);
		t = sc.nextToken();
		
		
		
		lp = t.getLinePos();
		assertEquals(Kind.PLUS, t.kind);
		assertEquals(2, lp.line);
		assertEquals(0, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.NOTEQUAL, t.kind);
		assertEquals(2, lp.line);
		assertEquals(1, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.COMMA, t.kind);
		assertEquals(2, lp.line);
		assertEquals(3, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.DIV, t.kind);
		assertEquals(3, lp.line);
		assertEquals(0, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.SEMI, t.kind);
		assertEquals(3, lp.line);
		assertEquals(1, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.MOD, t.kind);
		assertEquals(3, lp.line);
		assertEquals(2, lp.posInLine);
		
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.AND, t.kind);
		assertEquals(3, lp.line);
		assertEquals(3, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.AND, t.kind);
		assertEquals(4, lp.line);
		assertEquals(0, lp.posInLine);
		
		t = sc.nextToken();
		lp = t.getLinePos();
		assertEquals(Kind.TIMES, t.kind);
		assertEquals(4, lp.line);
		assertEquals(1, lp.posInLine);
		
		
		
	}
	
	
	/**
	 * Token peek and corner cases test
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testMoreCornerCases() throws IllegalCharException, IllegalNumberException {
		String str = "\r\n\n\r\n\r";
		Scanner sc = new Scanner(str);
		
		sc.scan();
		assertEquals(0, sc.tokenNum);
		assertEquals(1, sc.tokens.size());
		Scanner.Token tok = sc.peek();
		assertEquals(Kind.EOF, tok.kind);
		tok = sc.nextToken();
		assertEquals(Kind.EOF, tok.kind);
		tok = sc.peek();
		assertEquals(null, tok);
		tok = sc.nextToken();
		assertEquals(null, tok);
	}

	/**
	 * white spaces checking
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testWhiteSpaces() throws IllegalCharException, IllegalNumberException {
		
		String input = "\tint pow\n"
				+ "(integer a) \n{\t\trta*;eof}";
		
		Scanner sc = new Scanner(input);
		sc.scan();
		Scanner.Token t = sc.nextToken();
		
		Scanner.LinePos l = t.getLinePos();
		assertEquals(Kind.IDENT, t.kind);
		assertEquals(0, l.line);
		assertEquals(1, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.IDENT, t.kind);
		assertEquals(0, l.line);
		assertEquals(5, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.LPAREN, t.kind);
		assertEquals(1, l.line);
		assertEquals(0, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.KW_INTEGER, t.kind);
		assertEquals(1, l.line);
		assertEquals(1, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.IDENT, t.kind);
		assertEquals(1, l.line);
		assertEquals(9, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.RPAREN, t.kind);
		assertEquals(1, l.line);
		assertEquals(10, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.LBRACE, t.kind);
		assertEquals(2, l.line);
		assertEquals(0, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.IDENT, t.kind);
		assertEquals(2, l.line);
		assertEquals(3, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.TIMES, t.kind);
		assertEquals(2, l.line);
		assertEquals(6, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.SEMI, t.kind);
		assertEquals(2, l.line);
		assertEquals(7, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.IDENT, t.kind);
		assertEquals(2, l.line);
		assertEquals(8, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.RBRACE, t.kind);
		assertEquals(2, l.line);
		assertEquals(11, l.posInLine);
		
		t = sc.nextToken();
		l = t.getLinePos();
		assertEquals(Kind.EOF, t.kind);
		assertEquals(2, l.line);
		assertEquals(12, l.posInLine);
		
		t = sc.nextToken();
		assertEquals(null, t);
		
		
	}

	/**
	 * testing comments
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testComment() throws IllegalCharException, IllegalNumberException {
		
		String input = "as /******";
		Scanner sc = new Scanner(input);
		sc.scan();
		Scanner.Token t = sc.nextToken();
		assertEquals(Kind.IDENT, t.kind);
		t = sc.nextToken();
		assertEquals(Kind.EOF, t.kind);
		
		t = sc.nextToken();
		assertEquals(null, t);
	}
	
	/**
	 * testing comments
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testComment1() throws IllegalCharException, IllegalNumberException {
		
		String input = "as /**sdsdsd/ssd/****/";
		Scanner sc = new Scanner(input);
		sc.scan();
		Scanner.Token t = sc.nextToken();
		
		Scanner.LinePos l = t.getLinePos();
		
		assertEquals(Kind.IDENT, t.kind);
		t = sc.nextToken();
		assertEquals(Kind.EOF, t.kind);
		
		t = sc.nextToken();
		assertEquals(null, t);
	}
	
	/**
	 * testing comments
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testComment2() throws IllegalCharException, IllegalNumberException {
		
		String input = "as /**aa/a/////////?????a/a/*/";
		Scanner sc = new Scanner(input);
		sc.scan();
		Scanner.Token t = sc.nextToken();
		
		assertEquals(Kind.IDENT, t.kind);
		t = sc.nextToken();
		assertEquals(Kind.EOF, t.kind);
		
		t = sc.nextToken();
		assertEquals(null, t);
	}
	
	/**
	 * checking token's getText method
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testgetText() throws IllegalCharException, IllegalNumberException {
		String input = "sdas-eof";
		Scanner sc = new Scanner(input);
		sc.scan();
		Scanner.Token t = sc.nextToken();
		assertEquals(Kind.IDENT, t.kind);
		assertEquals("sdas", t.getText());
		t = sc.nextToken();
		assertEquals(Kind.MINUS, t.kind);
		assertEquals("-", t.getText());
		t = sc.nextToken();
		
		assertEquals(Kind.IDENT, t.kind);
		assertEquals("eof", t.getText());
		t = sc.nextToken();
		assertEquals(Kind.EOF, t.kind);
		assertEquals("eof", t.getText());
		
		
	}
	
	
	//TODO  more tests
	
}
