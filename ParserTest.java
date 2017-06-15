package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog0 {god <- 687; }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog1 { if (23 + abc >  56 - dwedwd) {print -> Hi;}}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog2 url google";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blah {if (a) integer one}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog04 url cnn , file f35 {if (x | y) { while (y - x) { convolve |-> fdff -> show ; abc <- 91*6+71 ; } } }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	@Test
	public void testProgram5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "false (I am the Donald)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	public void testProgram6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "235 % 25 *100 & 11 / 7";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	public void testProgram7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}
	
	@Test
	public void testProgram9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2667 % screenheight + true & false != screenwidth & blahblah | you * (I % 1000))";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}
	
	@Test
	public void testProgram8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur (2667 % screenheight + true & false != screenwidth & blahblah | you * (I % 1000))";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
	}
	
	@Test
	public void testProgram10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blah {integer AGE}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testProgram11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep sugar;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testProgram12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " LIES <- 21 < Twenty;";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	public void testProgram13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if( a < b) { while (a > b) {do a%b;} do b % a})";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testProgram14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if( a < b) { while (a > b) {c <- a % b;} d <- b % a;})";
		Parser parser = new Parser(new Scanner(input).scan());
		//thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testProgram15() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "willthisparse file XYZ url salesforce integer 112 boolean b {image thisImage frame thatFrame sleep PS114; while 0x0022FFCA * 0xAACCBBDD % 1000 | 551 /4 & 9 <= screenwidth * screenheight % 100 | 21 /3 & 7); hide (a > b) |-> 2blah -> show (a < b);}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
}

