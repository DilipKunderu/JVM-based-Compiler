package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class CodeGenVisitorTest {

    static final boolean doPrint = true;
    boolean devel = false;
    boolean grade = true;
    private PrintStream stdout;
    private ByteArrayOutputStream outputStream;

    static void show(Object s) {
        if (doPrint) {
            System.out.println(s);
        }
    }

    private void assertProgramValidity(String inputCode, String[] args, String expectedOutput) throws Exception {
        Scanner scanner = new Scanner(inputCode);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);

        CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
        byte[] bytecode = (byte[]) program.visit(cv, null);

        String programName = program.getName();

//        String classFileName = "bin/" + programName + ".class";
//        OutputStream output = new FileOutputStream(classFileName);
//        output.write(bytecode);
//        output.close();

        Runnable instance = CodeGenUtils.getInstance(programName, bytecode, args);
        instance.run();

        String actualOutput = outputStream.toString().trim();

        assertEquals("Invalid Output\n -------------------- \n" + inputCode + "\n --------------------", expectedOutput, actualOutput);
    }

    @Before
    public void setUp() throws Exception {
        stdout = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(stdout);
    }

    @Test
    public void emptyProg() throws Exception {
        //scan, parse, and type check the program
        String progname = "emptyProg";
        String input = progname + "  {}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        show(program);

        //generate code
        CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
        byte[] bytecode = (byte[]) program.visit(cv, null);

        //output the generated bytecode
        CodeGenUtils.dumpBytecode(bytecode);

        //write byte code to file 
        String name = ((Program) program).getName();
        String classFileName = "bin/" + name + ".class";
        OutputStream output = new FileOutputStream(classFileName);
        output.write(bytecode);
        output.close();
        System.out.println("wrote classfile to " + classFileName);

        // directly execute bytecode
        String[] args = new String[0]; //create command line argument array to initialize params, none in this case
        Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
        instance.run();
    }

    @Test
    public void testEmptyProgWithAssertions() throws Exception {
        String inputCode = "emptyProg {}";
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "");
    }
    
    
    @Test
    public void testParam0() throws Exception {
        String[] input = new String[]{
                "param0 integer x, boolean y {",
                "y <- y;",
                "x <- x;",
                "x <- 9;",
                "y <- false;",
                "y <- true;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{"7", "true"};

        assertProgramValidity(inputCode, args, "true79falsetrue");
    }
}