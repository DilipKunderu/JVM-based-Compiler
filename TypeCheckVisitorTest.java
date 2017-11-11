/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {
	
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

//	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

//	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	
//	@Test
	public void testProgram() throws Exception{
		String input = "p {\ninteger i i <- j;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
//	@Test
	public void testProgram1() throws Exception{
		String input = "p { boolean b while (b == true) {integer i integer j if (i < j) {  i <- j; i <- i *j;}}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

//	@Test
	public void testProgram2() throws Exception{
		String input = "p url u { image a image b c -> id |-> gray; b <- a; while (a<b) {integer l}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
//	@Test
	public void testProgram3() throws Exception{
		String input = "p {frame xyz image cow \n cow -> xyz;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	public void testCasesFailed() throws Exception {
		String input = "p integer a, integer b "
				+ "{image img1 image img2 "
				+ "		if(img1 != img2) {image a a <- img1; } "
				+ "		if(a != b) {boolean a a <- img1 != img2; }}";
		input = "testFrame url u1, url u2, file file1 "
				+ "{frame fra1 frame fra2 image img fra1 -> move (screenheight, screenwidth) -> xloc; img -> fra2; img -> file1;}";
		input = "prog  boolean y , file x {\n integer z \n scale(100) -> width; blur -> y; convolve -> blur -> gray |-> gray -> width;}";
		input = "abc integer x, integer x {} ";
		input = "p \nurl y {\n  image i\n  y->i;\n}";
		
				input = "prog1  file file1, integer itx, boolean b1"
						+ "{ integer ii1 boolean bi1 \n image IMAGE1 frame fram1 sleep itx+ii1; "
						+ "while (b1){if(bi1)\n{sleep ii1+itx*2;}}\n"
						+ "file1->blur |->gray;fram1 ->yloc;\n IMAGE1->blur->scale (ii1+1)"
						+ "|-> gray;\nii1 <- 12345+54321;}";

						input = "tos url u,\n integer x\n{integer y image i u -> i; i -> height; frame f i -> scale (x) -> f;}";
		Scanner sc = new Scanner(input);
		sc.scan();
		Parser p = new Parser(sc);
		ASTNode prog = p.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		prog.visit(v, null);
	}
	
}
