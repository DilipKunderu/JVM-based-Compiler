package cop5556sp17;

import static cop5556sp17.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.management.StandardEmitterMBean;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
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
	
	/**
	 * testing paramdec
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file value";
		Scanner s = new Scanner(input).scan();
		Parser p = new Parser(s);
		ASTNode ast = p.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
		assertEquals(ast instanceof ParamDec, true);
		ParamDec pd = (ParamDec) ast;
		assertEquals(pd.firstToken.kind, Kind.KW_FILE);
		assertEquals(pd.getIdent().kind, Kind.IDENT);
	}
	
	/**
	 * testing dec
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * @throws SyntaxException
	 */
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image value";
		Scanner s = new Scanner(input).scan();
		Parser p = new Parser(s);
		ASTNode ast = p.dec();
		assertEquals(Dec.class, ast.getClass());
		assertEquals(ast instanceof Dec, true);
		Dec pd = (Dec) ast;
		assertEquals(pd.firstToken.kind, Kind.KW_IMAGE);
		assertEquals(pd.getIdent().kind, Kind.IDENT);
	}
	
	@Test
	public void testExpress1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "2*3";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression e = (BinaryExpression) ast;
		assertEquals(e.firstToken.kind, Kind.INT_LIT);
		assertEquals(e.getE0().getClass(), IntLitExpression.class);
		assertEquals(e.getOp().kind, Kind.TIMES);
		assertEquals(e.getE1().getClass(), IntLitExpression.class);
	}
	
	@Test
	public void testExpress2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "(2&3)";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.expression();
		assertEquals(ast.getClass(), BinaryExpression.class);
		BinaryExpression e = (BinaryExpression) ast;
		assertEquals(e.getOp().kind, Kind.AND);
		assertEquals(e.getE0().getClass(), IntLitExpression.class);
		assertEquals(e.getE0().getFirstToken().kind, Kind.INT_LIT);
		assertEquals(e.getFirstToken().kind, Kind.INT_LIT);
	}
		
	@Test
	public void testExpress3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "((1*2) + (screenwidth*true))";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.expression();
		assertEquals(ast.getClass(), BinaryExpression.class);
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(be.getE0().getClass(), BinaryExpression.class);
		assertEquals(be.getE1().getClass(), BinaryExpression.class);
		BinaryExpression be1 = (BinaryExpression) be.getE0();
		BinaryExpression be2 = (BinaryExpression) be.getE1();
		assertEquals(be.getFirstToken().kind, Kind.LPAREN);
		assertEquals(be1.firstToken.kind, Kind.INT_LIT);
		assertEquals(be2.firstToken.kind, Kind.KW_SCREENWIDTH);
		assertEquals(be1.getE0().getClass(), IntLitExpression.class);
		assertEquals(be1.getE1().getClass(), IntLitExpression.class);
		assertEquals(be2.getE0().getClass(), ConstantExpression.class);
		assertEquals(be2.getE1().getClass(), BooleanLitExpression.class);
		
	}
	
	@Test
	public void testExpress4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "1";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.expression();
		assertEquals(ast.getClass(), IntLitExpression.class);
		IntLitExpression ie = (IntLitExpression) ast;
		assertEquals(ie.getFirstToken().kind, Kind.INT_LIT);
		
		i = "true";
		p = new Parser(new Scanner(i).scan());
		ast = p.factor();
		assertEquals(ast.getClass(), BooleanLitExpression.class);
		BooleanLitExpression ble = (BooleanLitExpression) ast;
		assertEquals(ble.firstToken.kind, Kind.KW_TRUE);
		
		i = "screenwidth";
		p = new Parser(new Scanner(i).scan());
		ast = p.factor();
		assertEquals(ast.getClass(), ConstantExpression.class);
		ConstantExpression ce = (ConstantExpression) ast;
		assertEquals(ce.firstToken.kind, Kind.KW_SCREENWIDTH);
		
		i = "width1";
		p = new Parser(new Scanner(i).scan());
		ast = p.factor();
		assertEquals(ast.getClass(), IdentExpression.class);
		IdentExpression ide = (IdentExpression) ast;
		assertEquals(ide.firstToken.kind, Kind.IDENT);
	}
	
	@Test
	public void testarg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.arg();
		assertEquals(ast.getClass(), Tuple.class);
		Tuple tup = (Tuple)	ast;
		assertEquals(tup.getExprList().size(), 0);
		 
		i = "(1,2)";
		p = new Parser(new Scanner(i).scan());
		ast = p.arg();
		assertEquals(ast.getClass(), Tuple.class);
		Tuple t = (Tuple) ast;
		assertEquals(t.getExprList().size(), 2);
		assertEquals(t.getFirstToken().kind, Kind.LPAREN);
		assertEquals(t.getExprList().get(0).getClass(), IntLitExpression.class);
	}
	
	@Test
	public void testarg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "((i*5), 32)";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.arg();
		assertEquals(ast.getClass(), Tuple.class);
		Tuple t = (Tuple) ast;
		assertEquals(t.getExprList().size(), 2);
		assertEquals(t.getFirstToken().kind, Kind.LPAREN);
		assertEquals(t.getExprList().get(0).getClass(), BinaryExpression.class);
		assertEquals(t.getExprList().get(1).getClass(), IntLitExpression.class);
		BinaryExpression be = (BinaryExpression) t.getExprList().get(0);
		assertEquals(be.firstToken.kind, Kind.IDENT);
		assertEquals(be.getFirstToken().equals(t.getFirstToken()), false);
	}
	
	@Test
	public void testchain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "ide -> blur(1, idei);";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.statement();
		assertEquals(ast.getClass(), BinaryChain.class);
		BinaryChain bc = (BinaryChain) ast;
		assertEquals(bc.getE0().getClass(), IdentChain.class);
		assertEquals(bc.getE1().getClass(), FilterOpChain.class);
		IdentChain iv = (IdentChain) bc.getE0();
		assertEquals(iv.getFirstToken().kind, Kind.IDENT);
		assertEquals(bc.getFirstToken().kind, Kind.IDENT);
		assertEquals(bc.getArrow().kind, Kind.ARROW);
		FilterOpChain foc = (FilterOpChain) bc.getE1();
		assertEquals(foc.getFirstToken().kind, Kind.OP_BLUR);
		assertEquals(foc.getArg().getClass(), Tuple.class);
		Tuple tup = foc.getArg();
		assertEquals(tup.getExprList().size(), 2);
		assertEquals(tup.getFirstToken().kind, Kind.LPAREN);
		assertEquals(tup.getExprList().get(0).getClass(), IntLitExpression.class);
		assertEquals(tup.getExprList().get(1).getClass(), IdentExpression.class);
		
	}
	
	@Test
	public void testchain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "ide -> blur((1 + idei)) |-> hide -> gray;";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.statement();
		assertEquals(ast.getClass(), BinaryChain.class);
		BinaryChain bc = (BinaryChain) ast;
		assertEquals(bc.firstToken.kind, Kind.IDENT);
		assertEquals(bc.getE1().getFirstToken().kind, Kind.OP_GRAY);
		assertEquals(bc.getE0().getClass(), BinaryChain.class);
		BinaryChain bc1 = (BinaryChain) bc.getE0();
		assertEquals(bc.firstToken.hashCode(), bc1.getFirstToken().hashCode());
		assertEquals(bc1.getArrow().kind, Kind.BARARROW);
		assertEquals(bc1.getE0().getClass(), BinaryChain.class);
		BinaryChain bc2 = (BinaryChain) bc1.getE0();
		assertEquals(bc.firstToken.hashCode(), bc2.getFirstToken().hashCode());
		assertEquals(bc2.getArrow().kind, Kind.ARROW);
		assertEquals(bc2.getE0().getClass(), IdentChain.class);
	}
	
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "asd <- 2;";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.statement();
		assertEquals(ast.getClass(), AssignmentStatement.class);
		AssignmentStatement as = (AssignmentStatement) ast;
		assertEquals(as.getFirstToken().kind, Kind.IDENT);
		assertEquals(as.getVar().getText().equals(as.getFirstToken().getText()), true);
		assertEquals(as.getFirstToken().hashCode(), as.getVar().getFirstToken().hashCode());
	}
	
	@Test
	public void testsleep() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "sleep (2*sleepTime);";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		SleepStatement ss = (SleepStatement) ast;
		assertEquals(ss.getFirstToken().kind, Kind.OP_SLEEP);
		assertEquals(ss.getE().getClass(), BinaryExpression.class);
		BinaryExpression be = (BinaryExpression) ss.getE();
		assertEquals(be.getFirstToken().kind, Kind.INT_LIT);
		assertEquals(be.getE1().getClass(), IdentExpression.class);
	}
	
	@Test
	public void testif() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "if (2*3==6!=false) {}";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.statement();
		assertEquals(IfStatement.class, ast.getClass());
		IfStatement is = (IfStatement) ast;
		assertEquals(is.firstToken.kind, Kind.KW_IF);
		assertEquals(is.getE().getClass(), BinaryExpression.class);
		
		BinaryExpression sb = (BinaryExpression) is.getE();
		assertEquals(sb.firstToken.kind, Kind.INT_LIT);
		assertEquals(sb.getOp().kind, Kind.NOTEQUAL);
		assertEquals(sb.getE1().getClass(), BooleanLitExpression.class);
		assertEquals(sb.getE0().getClass(), BinaryExpression.class);
		
		BinaryExpression be = (BinaryExpression) sb.getE0();
		assertEquals(be.getFirstToken(), sb.getFirstToken());
		assertEquals(be.getOp().kind, Kind.EQUAL);
		assertEquals(be.getE0().getClass(), BinaryExpression.class);

		BinaryExpression be1 = (BinaryExpression) be.getE0();
		assertEquals(be1.getFirstToken(), sb.getFirstToken());
		assertEquals(be1.getOp().kind, Kind.TIMES);
		
		Block b = is.getB();
		assertEquals(b.getFirstToken().kind, Kind.LBRACE);
		assertEquals(b.getDecs().size(), 0);
		assertEquals(b.getStatements().size(), 0);
	}
	
	@Test
	public void testwhile() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "while (id%2==3) { integer i if (true) {i <-3;}}";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		WhileStatement ws = (WhileStatement) ast;
		assertEquals(ws.getFirstToken().kind, Kind.KW_WHILE);
		assertEquals(ws.getE().getFirstToken().kind, Kind.IDENT);
		Block b = ws.getB();
		assertEquals(b.getFirstToken().kind, Kind.LBRACE);
		assertEquals(b.getStatements().size(), 1);
		assertEquals(b.getDecs().size(), 1);
		assertEquals(b.getDecs().get(0).firstToken.kind, Kind.KW_INTEGER);
		IfStatement is = (IfStatement) b.getStatements().get(0);
		Block b2 = is.getB();
		assertEquals(b2.getFirstToken().kind, Kind.LBRACE);
		assertEquals(b2.getStatements().get(0).getClass(), AssignmentStatement.class);
		AssignmentStatement ass = (AssignmentStatement) b2.getStatements().get(0);
		assertEquals(ass.getFirstToken().kind, Kind.IDENT);	
	}
	
	@Test
	public void testblock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "{integer i image j if (3==2*3) { i <- 2; while(i > 1){ if (i == 4) {i <- 0; } i <- i + 1; }}}";
		Parser p = new Parser(new Scanner(i).scan());
		ASTNode ast = p.block();
		assertEquals(Block.class, ast.getClass());
		Block b = (Block) ast;
		ArrayList<Dec> decs =  b.getDecs();
		ArrayList<Statement> stmts =  b.getStatements();
		assertEquals(decs.size(), 2);
		assertEquals(stmts.size(), 1);
		assertEquals(decs.get(0).firstToken.kind, Kind.KW_INTEGER);
		assertEquals(decs.get(1).firstToken.kind, Kind.KW_IMAGE);
		assertEquals(stmts.get(0).firstToken.kind, Kind.KW_IF);
		IfStatement ifst = (IfStatement) stmts.get(0);
		Block b2 = ifst.getB();
		decs = b2.getDecs();
		stmts = b2.getStatements();
		assertEquals(decs.size(), 0);
		assertEquals(stmts.size(), 2);
		WhileStatement ws = (WhileStatement) stmts.get(1);
		Block b3 = ws.getB();
		assertEquals(b3.getDecs().size(), 0);
		assertEquals(b3.getStatements().size(), 2);
		stmts = b3.getStatements();
		assertEquals(stmts.size(), 2);
		assertEquals(stmts.get(1).getClass(), AssignmentStatement.class);
		assertEquals(stmts.get(0).getClass(), IfStatement.class);
		IfStatement ifs = (IfStatement) stmts.get(0);
		assertEquals(ifs.getE().getFirstToken().kind, Kind.IDENT);
		Block b4 = ifs.getB();
		assertEquals(b4.getDecs().size(), 0);
		assertEquals(b4.getStatements().size(), 1);
		assertEquals(b4.getStatements().get(0).getClass(), AssignmentStatement.class);
		
	}
	
	@Test
	public void testprogram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String i = "class url urlName, file fileName {integer i boolean b i <- i+1;}";
		Parser p = new Parser(new Scanner(i).scan());
//		ASTNode ast = p.program();
		ASTNode ast = p.parse();
		assertEquals(Program.class, ast.getClass());
		Program pr = (Program) ast;
		assertEquals(pr.getFirstToken().kind, Kind.IDENT);
		assertEquals(pr.getName().equals("class"), true);
		assertEquals(pr.getParams().size(), 2);
		assertEquals(pr.getParams().get(1).getFirstToken().kind, Kind.KW_FILE);
		Block b = pr.getB();
		assertEquals(b.getDecs().size(), 2);
		assertEquals(b.getStatements().size(), 1);
		
	}

}
