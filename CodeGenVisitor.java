package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	
	FieldVisitor fv;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	int dec_counter = 1;
	int paramdec_counter = 0;

	List<Dec> decList = new ArrayList<>();

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		// TODO visit the local variables
		
		for (Dec t : decList) {
			mv.visitLocalVariable(t.getIdent().getText(), classDesc, null, t.getStart_label(), t.getEnd_label(), t.getSlot());
		}
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method

		cw.visitEnd();// end of class

		// generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().gettype());
		assignStatement.getVar().visit(this, arg);
		return assignStatement;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Implement this
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);

		if (binaryExpression.getE0().gettype() == TypeName.INTEGER) {
			switch (binaryExpression.getOp().kind) {
			case PLUS:
				mv.visitInsn(IADD);
				break;

			case MINUS:
				mv.visitInsn(ISUB);
				break;

			case TIMES:
				mv.visitInsn(IMUL);
				break;

			case DIV:
				mv.visitInsn(IDIV);
				break;

			case MOD:
				mv.visitInsn(IREM);
				break;

			case AND:
				mv.visitInsn(IAND);
				break;

			case OR:
				mv.visitInsn(IOR);
				break;

			case EQUAL: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}

			case NOTEQUAL: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}

			case LT: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPGE, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}

			case LE: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPGT, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}

			case GT: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPLE, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}

			case GE: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPLT, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}
			default:
				break;
			}	
		}
		else if (binaryExpression.getE0().gettype() == TypeName.BOOLEAN) {
			switch (binaryExpression.getOp().kind) {
			case EQUAL: {
				Label l3 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, l3);
				mv.visitInsn(ICONST_1);
				Label l4 = new Label();
				mv.visitJumpInsn(GOTO, l4);
				mv.visitLabel(l3);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l4);
				break;
			}
			case NOTEQUAL: {
				Label l3 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, l3);
				mv.visitInsn(ICONST_1);
				Label l4 = new Label();
				mv.visitJumpInsn(GOTO, l4);
				mv.visitLabel(l3);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l4);
				break;
			}
			case LT: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPGE, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}
			case LE: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPGT, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}
			case GT: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPLE, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}
			case GE: {
				Label l4 = new Label();
				mv.visitJumpInsn(IF_ICMPLT, l4);
				mv.visitInsn(ICONST_1);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				mv.visitLabel(l4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l5);
				break;
			}
			default:
				break;
			}
		}
		return binaryExpression;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Implement this
		List<Dec> tempDecList = block.getDecs();
		List<Statement> tempStmtList = block.getStatements();
		
		Label l_start = new Label();
		Label l_end = new Label();
		
		mv.visitLabel(l_start);
		
		for (Dec t : tempDecList ) {
			t.setStart_label(l_start);
			t.visit(this, arg);
			decList.add(t);
			t.setEnd_label(l_end);
		}
		
		for (Statement s : tempStmtList) {
			s.visit(this, arg);
		}
		
		mv.visitLabel(l_end);
		return block;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Implement this
		if (booleanLitExpression.getValue() == true) {
			mv.visitInsn(ICONST_1);
		} else
			mv.visitInsn(ICONST_0);
		return booleanLitExpression;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Implement this
		declaration.setSlot(dec_counter);
		dec_counter++;
		return declaration;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Implement this
		Dec dec = identExpression.getDec();
		if (dec.getSlot() == -1) { // paramDec
			// fields need getfield and put field
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.firstToken.getText(),
					dec.gettype().getJVMTypeDesc());
		} else
			mv.visitVarInsn(ILOAD, identExpression.getDec().getSlot()); // dec
		return identExpression;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Implement this
		Dec dec = identX.getDec();

		if (dec.getSlot() == -1) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, identX.getFirstToken().getText(), dec.gettype().getJVMTypeDesc());
		} else {
			mv.visitVarInsn(ISTORE, dec.getSlot());
		}
		return identX;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label l2 = new Label();
		mv.visitJumpInsn(IFEQ, l2);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(l2);

		return ifStatement;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Implement this
		mv.visitLdcInsn(intLitExpression.value);
		return intLitExpression;
	}

	
	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Implement this
		// For assignment 5, only needs to handle integers and booleans
		fv = cw.visitField(0, paramDec.getIdent().getText(), paramDec.gettype().getJVMTypeDesc(), null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(paramdec_counter);
		paramdec_counter++;
		mv.visitInsn(AALOAD);

		if (paramDec.gettype() == TypeName.INTEGER) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I",
					false);
//			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
		}

		else if (paramDec.gettype() == TypeName.BOOLEAN) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z",
					false);
//			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
		}
		fv.visitEnd();
		return paramDec;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Implement this
		Label GUARD = new Label(); 
		Label BODY = new Label(); 
		
		mv.visitJumpInsn(GOTO, GUARD);
		mv.visitLabel(BODY);
		whileStatement.getB().visit(this, arg);
		mv.visitLabel(GUARD);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, BODY);

		return whileStatement;
	}

}
