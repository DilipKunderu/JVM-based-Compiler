package cop5556sp17;

import static cop5556sp17.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.AST.ASTNode;
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
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;

public class ASTTest {

	static final boolean doPrint = true;

	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	 @Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	 @Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}

	 @Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}

	 @Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if( a < b) { while (a > b) {do a%b;} do b % a})";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
		IfStatement st = (IfStatement) ast;
		assertEquals(BinaryExpression.class, st.getE());
		BinaryExpression be = (BinaryExpression) st.getE();
		assertEquals(IdentExpression.class, be.getE0());
		assertEquals(Kind.LT, be.getOp().kind);
		assertEquals(IdentExpression.class, be.getE1());
		Block bk = (Block) st.getB();
		assertEquals(Dec.class, bk.getDecs().get(0).getClass());
		assertEquals(Statement.class, bk.getStatements());
	}

	 @Test
	public void test06() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(29 % 222)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
	}

	 @Test
	public void test07() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(a + 4) % drgdrfg";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
	}

	 @Test
	public void test08() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog0 {god <- 687; }";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());

	}

	 @Test
	public void test09() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url abcsfwefaekfne";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
	}

	 @Test
	public void test00() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(289 % 77)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression b = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, b.getE0().getClass());
		assertEquals(IntLitExpression.class, b.getE1().getClass());
	}

	 @Test
	public void test01() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog0 {god <- 687; }";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
	}

	 @Test
	public void test02() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "false (I am the Donald)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.factor();
		assertEquals(BooleanLitExpression.class, ast.getClass());
	}

	 @Test
	public void test03() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "235 % 25 *100 & 11 / 7";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.elem();
		assertEquals(BinaryExpression.class, ast.getClass());

	}

	@Test
	public void test05() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur (2667 % screenheight + true & false != screenwidth & blahblah | you * (I % 1000))";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chainElem();
		assertEquals(FilterOpChain.class, ast.getClass());
	}
	
	@Test
	public void test10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "((hundred/100), 836)";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.arg();
		assertEquals(ast.getClass(), Tuple.class);
		Tuple t = (Tuple) ast;
		assertEquals(t.getExprList().size(), 2);
		assertEquals(Kind.LPAREN,t.getFirstToken().kind);
		assertEquals(t.getExprList().get(1).getClass(), IntLitExpression.class);
		BinaryExpression be = (BinaryExpression) t.getExprList().get(0);
		assertEquals( Kind.IDENT, be.firstToken.kind);
	}
	
	@Test
	public void test11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "height (11, b/6 <= 88)";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.chainElem();
		assertEquals(ImageOpChain.class, ast.getClass());

	}
	
	@Test
	public void test12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(a < b){x<- 117;}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
	}

	
	
}
