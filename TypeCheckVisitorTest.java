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

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}

	@Test
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

	@Test
	public void test06() throws Exception{
		String input = "p {\ninteger a a <- b;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}
	
	@Test
	public void test05() throws Exception{
		String input = "q {image x frame y \n x -> y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
		
		@Test
		public void test04() throws Exception{
			String input = "q { }";
			Scanner scanner = new Scanner(input);
			scanner.scan();
			Parser parser = new Parser(scanner);
			ASTNode program = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			program.visit(v, null);
		}
		
		@Test 
		public void testBlock0() throws Exception{
			String input = "s {\ninteger y \nboolean b}";
			Scanner scanner = new Scanner(input);
			scanner.scan();
			Parser parser = new Parser(scanner);
			ASTNode program = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			program.visit(v, null);
		}
		
		@Test
		public void test02() throws Exception{
			String input = "q {\n integer a \nif(true){a <- 221 / 4;} a <- 3;}";
			Scanner scanner = new Scanner(input);
			scanner.scan();
			Parser parser = new Parser(scanner);
			ASTNode program = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			program.visit(v, null);
		}
		
		@Test
		public void test03() throws Exception{
			String input = "r {\nframe j \n j->black;}";
			Scanner scanner = new Scanner(input);
			scanner.scan();
			Parser parser = new Parser(scanner);
			ASTNode program = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			program.visit(v, null);
		}
}
