package cop5556sp17;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;


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
//		System.out.println(scanner);
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
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	@Test
	public void testCustomProgram() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "classs {integer abc def <- 98989;}";
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.parse();
	}
	
	public void printTokens(Scanner sc) {
		List<Scanner.Token> t = sc.tokens;
		for (Scanner.Token t1: t) {
			System.out.print(t1.kind + " ");
		}
	}
	
	/**
	 * testing relOp here
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testRelOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = ">=<=><!=====";
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.relOp();
		parser.relOp();
		parser.relOp();
		parser.relOp();
		parser.relOp();
		parser.relOp();
		parser.relOp();
		thrown.expect(SyntaxException.class);
		parser.relOp();
	}
	
	/**
	 * testing strongOp
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testStrongOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "/%*&/%&*%&*/";
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		parser.strongOp();
		thrown.expect(SyntaxException.class);
		parser.strongOp();
	}
	
	/**
	 * testing weakOp
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testweakOp() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "+-|+++-+||";
		Parser parser = new Parser(new Scanner(input).scan());
		
		parser.weakOp();
		parser.weakOp();
		parser.weakOp();
		parser.weakOp();
		parser.weakOp();
		parser.weakOp();
		assertEquals(parser.t.kind, Kind.MINUS);
		parser.weakOp();
		parser.weakOp();
		parser.weakOp();
		parser.weakOp();
		thrown.expect(SyntaxException.class);
		assertEquals(parser.t.kind, Kind.EOF);
		parser.weakOp();
		
	}
	
	/**
	 * test chainElemOps
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testChainElemOps() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur gray convolve show hide move xloc yloc width height scale";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.filterOp();
		parser.filterOp();
		parser.filterOp();
		try {
			parser.filterOp();
		} catch (Exception e) {
//			System.out.println("caught here");
		}
		parser.frameOp();
		parser.frameOp();
		parser.frameOp();
		parser.frameOp();
		parser.frameOp();
		
		parser.imageOp();
		parser.imageOp();
		parser.imageOp();
		assertEquals(parser.t.kind, Kind.EOF);
		
	}
	
	/**
	 * testing factors
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "test123 123 true false screenwidth screenheight(1)";
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.factor();
		parser.factor();
		parser.factor();
		parser.factor();
		parser.factor();
		parser.factor();
		parser.factor();
		thrown.expect(SyntaxException.class);
		parser.factor();
		
	}
	
	/**
	 * test element
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "12 * 12 / 9089 % bj & true screenwidth * screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.elem();
		assertEquals(parser.t.kind, Kind.KW_SCREENWIDTH);
		parser.elem();
		
	}
	
	/**
	 * test term
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testTerm() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "12 + 323 - 323 | (12 * 12 / 9089 % bj & true) screenwidth * screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.term();
		assertEquals(parser.t.kind, Kind.KW_SCREENWIDTH);
		parser.term();
	}
	
	/**
	 * test expression
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "iijid > ewe < sdjh | eref * sdsd +ssdsd -sdsds > sds / "
				+ "wefjwb & false | true + sdsdd > sdksjdks<sdjskd + sdsfd-"
				+ "(12 + 323 - 323 | (12 * 12 / 9089 % bj & true)) screenwidth * screenheight";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.expression();
		assertEquals(parser.t.kind, Kind.KW_SCREENWIDTH);
		parser.expression();
	}
	
	/**
	 * testing arg
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(iijid > ewe < sdjh | eref * sdsd +ssdsd -sdsds > sds / "
				+ "wefjwb & false | true + sdsdd > sdksjdks<sdjskd + sdsfd-"
				+ "(x12 + 323 - 323 | (12 * 12 / 9089 % bj & true)), "
				+ "screenwidth * screenheight, inp1 < undfd > kjndf + 3 * 0 - hjj%ojio/3334)";
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.arg();
		assertEquals(parser.t.kind, Kind.EOF);
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testChainElements() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String arg = "(iijid > ewe < sdjh | eref * sdsd +ssdsd -sdsds > sds / "
				+ "wefjwb & false | true + sdsdd > sdksjdks<sdjskd + sdsfd-"
				+ "(x12 + 323 - 323 | (12 * 12 / 9089 % bj & true)), "
				+ "screenwidth * screenheight, inp1 < undfd > kjndf + 3 * 0 - hjj%ojio/3334)";
		String input = "asads" + arg;
		Parser parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.chainElem();
		
		input = "blur" + arg;
		parser = new Parser(new Scanner(input).scan());
//		printTokens(parser.scanner);
		parser.chainElem();
		
		input = "gray" + arg;
		parser = new Parser(new Scanner(input).scan());
//		System.out.println();
//		printTokens(parser.scanner);
		parser.chainElem();
		
		input = "show" + arg;
		parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
		
		input = "hide" + arg;
		parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
		
		input = "xloc" + arg;
		parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
		
		input = "width" + arg;
		parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
		
		input = "height" + arg;
		parser = new Parser(new Scanner(input).scan());
		parser.chainElem();

		input = 12 + arg;
		parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.chainElem();
		
	}
	
	/**
	 * test arrowOp
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testArrowOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "-> |-> ident";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arrowOp();
		parser.arrowOp();
		thrown.expect(SyntaxException.class);
		parser.arrowOp();
	}
	
	/**
	 * test assign
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "i <- 32 * 232 + 323 % hj & kajka | 34";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.assign();
		assertEquals(parser.t.kind, Kind.EOF);
	}
	
	/**
	 * test chain
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "nkj -> njnj -> blur -> scale(1) -> width (67) -> height (27) -> show -> move(1) ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
		assertEquals(parser.t.kind, Kind.EOF);
	}
	
	/**
	 * test dec
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image iubijn";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
		
		input = "frame iubijn";
		parser = new Parser(new Scanner(input).scan());
		parser.dec();
		
		input = "integer iubijn";
		parser = new Parser(new Scanner(input).scan());
		parser.dec();
		
		input = "boolean iubijn";
		parser = new Parser(new Scanner(input).scan());
		parser.dec();
		
		input = "url iubijn";
		parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.dec();
		
	}
	
	/**
	 * test paramDec
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer iubijn";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		
		input = "boolean iubijn";
		parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		
		input = "url iubijn";
		parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		
		input = "file iubijn";
		parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
		
		input = "image iubijn";
		parser = new Parser(new Scanner(input).scan());
		thrown.expect(SyntaxException.class);
		parser.paramDec();
	}
	
	/**
	 * test stmt
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testStmt() throws IllegalCharException, IllegalNumberException, SyntaxException {
		
		String in = "sleep 1 * 34 * 45 / 324 ;";
		Parser p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);
		
		in = "i <- 10*12/23;";
		p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);
		
		in = "blur(12) -> sds -> blur;";
		p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);
		
		in = "bulb -> bulbasaur;";
		p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);
		
		in = "blub <- bulbasaur;";
		p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);

		in = "bulb -> bulbasaur;";
		p = new Parser(new Scanner(in).scan());
		p.chain();
		assertEquals(p.t.kind, Kind.SEMI);
		
		in = "blub <- bulbasaur;";
		p = new Parser(new Scanner(in).scan());
		p.assign();
		assertEquals(p.t.kind, Kind.SEMI);
		
		in = "if (12*1212/12&jsds*njs) {image ident1 i <- 100; j -> i12;"
				+ "while (12 & 2323) {integer i convolve -> blur(90) -> height(20); pikachu <- 10;}"
				+ " gray(12) -> blur -> xloc;}";
		p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);
		
		in = "while(sel_pokemon * 10) "
				+ "{ bulbasaur <- 10; squirtle <- 10; integer i boolean b "
				+ "if (a < b) {swap -> aParam -> bParam; ass <- 10;}"
				+ "charmander -> scale(10) -> charmiliion -> charizard -> mega_charizard; "
				+ "sleep 10;}";
		p = new Parser(new Scanner(in).scan());
		p.statement();
		assertEquals(p.t.kind, Kind.EOF);
		
	}
	
	/**
	 * test block
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		
		String in = "{}";
		Parser p = new Parser(new Scanner(in).scan());
		p.block();
		
		in = "{<}";
		p = new Parser(new Scanner(in).scan());
		thrown.expect(SyntaxException.class);
		p.block();
		
		in = "{			image a "
				+ "		boolean b "
				+ "		integer i "
				+ "		while(i < n) {"
				+ "			j <- 1; "
				+ "			j -> k -> l -> scale(10, 100);"
				+ "			if (j < m) {"
				+ "				print -> all -> elements;"
				+ "			}"
				+ "		}"
				+ "	}";
		p = new Parser(new Scanner(in).scan());
		p.block();
		
		
		
		
	}
	
	/**
	 * test program
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		
		String in = "solve"
				+ "		integer a,"
				+ "		boolean b,"
				+ "		url u,"
				+ "		file f,"
				+ "		integer a1,"
				+ "		boolean b1"
				+ "		{"
				+ "			integer i"
				+ "			boolean j"
				+ "			while (i == 0) {"
				+ "				i <- 1;"
				+ "				j -> blur(10,10) -> scale(1,10) -> xloc(10,0) -> yloc(0,10);"
				+ "				show(0,0) -> width(10) -> height(10);"
				+ "				if (screenwidth < 20) { newWidth <- 20;}"
				+ "				if (screenheight < 20) { newHheight <- 20;}"
				+ "			}"
				+ "		}";
		
		Parser p = new Parser(new Scanner(in).scan());
		p.program();
	}
	
	/**
	 * testing corner cases in args
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testargs1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "ident2 ";
		Parser p = new Parser(new Scanner(i).scan());
		p.arg();
		assertEquals(Kind.IDENT, p.t.kind);
		
		i = "(if1";
		p = new Parser(new Scanner(i).scan());
		thrown.expect(SyntaxException.class);
		p.parse();
		
	}
	

	/**
	 * testing corner cases in args
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testargs2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "()";
		Parser p = new Parser(new Scanner(i).scan());
		thrown.expect(SyntaxException.class);
		p.arg();
		
	}
	
	/**
	 * testing corner cases in args
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testargs3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "(12)";
		Parser p = new Parser(new Scanner(i).scan());
		p.arg();
		assertEquals(Kind.EOF, p.t.kind);
	}
	
	/**
	 * testing corner cases in args
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testargs4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "(divya == sad + sreeja == cryingBatch * harshita == kid * (sai > laja*ty))";
		Parser p = new Parser(new Scanner(i).scan());
//		thrown.expect(SyntaxException.class);
		p.arg();
		assertEquals(Kind.EOF, p.t.kind);
	}
	
}
