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

	String [] args;
	int argIndex = 0;
	int decCntr = 1;
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
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
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
		for (ParamDec dec : params) {
			dec.visit(this, mv);
		}
		
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
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
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
//TODO  visit the local variables

		for (Dec dec : decsInfoList) {
			String arg1 = "";
			if (dec.getType() == TypeName.INTEGER) {
				arg1 = "I";
			} else if (dec.getType() == TypeName.BOOLEAN) {
				arg1 = "Z";
			}
			Label startLabel;
			Label endLabel;
			if (dec.getStartLabel() == null) {
				startLabel = startRun;
				endLabel = endRun;
			} else {
				startLabel = dec.getStartLabel();
				endLabel = dec.getEndLabel();
			}
			mv.visitLocalVariable(dec.getIdent().getText(), 
					arg1, null, startLabel, endLabel, dec.getSlotNumber());
		}
		
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		Expression e0 = (Expression) binaryExpression.getE0().visit(this, arg);
		Expression e1 = (Expression) binaryExpression.getE1().visit(this, arg);
		TypeName t0 = e0.getType();
		TypeName t1 = e1.getType();
		Kind opKind = binaryExpression.getOp().kind;
		if (t0 == t1) {
			if (t0 == TypeName.INTEGER) {
				switch (opKind) {
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
					case EQUAL:
						Label flablee = new Label();
						mv.visitInsn(ISUB);
						mv.visitJumpInsn(IFEQ, flablee);
						mv.visitInsn(ICONST_0);
						Label tlablee = new Label();
						mv.visitJumpInsn(GOTO, tlablee);
						mv.visitLabel(flablee);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(tlablee);
						break;
					case NOTEQUAL:
						Label flablee1 = new Label();
						mv.visitInsn(ISUB);
						mv.visitJumpInsn(IFEQ, flablee1);
						mv.visitInsn(ICONST_1);
						Label tlablee1 = new Label();
						mv.visitJumpInsn(GOTO, tlablee1);
						mv.visitLabel(flablee1);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(tlablee1);						
						break;
					default:
						checkBoolean(opKind);					
				}
			} else if (t0 == TypeName.BOOLEAN) {
				switch(opKind) {
					case AND: 
						Label flable = new Label();
						mv.visitJumpInsn(IFEQ, flable);
						Label tlabel = new Label();
						mv.visitJumpInsn(IFEQ, flable);
						mv.visitInsn(ICONST_1);
						mv.visitJumpInsn(GOTO, tlabel);
						mv.visitLabel(flable);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(tlabel);
						break;
					case OR:
						Label flable1 = new Label();
						mv.visitJumpInsn(IFNE, flable1);
						Label tlabel1 = new Label();
						mv.visitJumpInsn(IFNE, flable1);
						mv.visitInsn(ICONST_0);
						mv.visitJumpInsn(GOTO, tlabel1);
						mv.visitLabel(flable1);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(tlabel1);
						break;
					default:
						checkBoolean(opKind);
				}
			}
		}
		return binaryExpression;
	}
	
	public void checkBoolean(Kind opKind) {
		Label flable = new Label();
		if (opKind == Kind.LT) {
			mv.visitJumpInsn(IF_ICMPGE, flable);
		} else if (opKind == Kind.LE){
			mv.visitJumpInsn(IF_ICMPGT, flable);
		} else if (opKind == Kind.GE) {
			mv.visitJumpInsn(IF_ICMPLT, flable);
		} else if (opKind == Kind.GT) {
			mv.visitJumpInsn(IF_ICMPLE, flable);
		} else if (opKind == Kind.EQUAL) {
			mv.visitJumpInsn(IF_ICMPNE, flable);
		} else if (opKind == Kind.NOTEQUAL) {
			mv.visitJumpInsn(IF_ICMPEQ, flable);
		}
		Label tlabel = new Label();
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(GOTO, tlabel);
		mv.visitLabel(flable);
		mv.visitInsn(ICONST_0);
		mv.visitLabel(tlabel);
	}
	
	List<Dec> decsInfoList = new ArrayList<>();
	boolean firstFlag = true;
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		Label enterBlock;
		Label exitBlock;
		if (firstFlag) {
			enterBlock = null;
			exitBlock = null;
			firstFlag = false;
		} else {
			enterBlock = new Label();
			exitBlock = new Label();
			mv.visitLabel(enterBlock);
		}
		
		List<Dec> decs = block.getDecs();
		for (Dec dec: decs) {
			dec.setStartLabel(enterBlock);
			dec.setEndLabel(exitBlock);
			dec.visit(this, arg);
			decsInfoList.add(dec);
		}
		for (Statement stmt: block.getStatements()) {
			stmt.visit(this, arg);
		}
		if (exitBlock != null) {
			mv.visitLabel(exitBlock);
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//TODO Implement this
		if (booleanLitExpression.getValue() == true) {
			mv.visitInsn(ICONST_1);
		} else {
			mv.visitInsn(ICONST_0);
		}
		return booleanLitExpression;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		declaration.setSlotNumber(decCntr);
		decCntr++;
		return null;
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
		//TODO Implement this
		Dec dec = identExpression.getDec();
		if (dec.getSlotNumber() == -1) {
			mv.visitVarInsn(ALOAD, 0);
			if (dec.getType().equals(TypeName.INTEGER)) {
				mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "I");
			} else if (dec.getType().equals(TypeName.BOOLEAN)) {
				mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Z");
			}
		} else {
			mv.visitVarInsn(ILOAD, dec.getSlotNumber());
		}
		return identExpression;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		Dec dec = identX.getDec();
		if (dec.getSlotNumber() < 0) {
//			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			if (dec.getType().equals(TypeName.INTEGER)) {
				mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "I");
			} else if (dec.getType().equals(TypeName.BOOLEAN)) {
				mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "Z");
			}
		} else {
			mv.visitVarInsn(ISTORE, dec.getSlotNumber());
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label ifNotLabel = new Label();
		mv.visitJumpInsn(IFEQ, ifNotLabel);
		Label insideIfLabel = new Label();
		mv.visitLabel(insideIfLabel);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(ifNotLabel);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		int val = intLitExpression.getVal();
//		if (val == 0) {
//			mv.visitInsn(ICONST_0);
//		} else if (val == 1) {
//			mv.visitInsn(ICONST_1);
//		} else if (val == 2) {
//			mv.visitInsn(ICONST_2);
//		} else if (val == 3) {
//			mv.visitInsn(ICONST_3);
//		} else if (val == 4) {
//			mv.visitInsn(ICONST_4);
//		} else if (val == 5) {
//			mv.visitInsn(ICONST_5);
//		} else if (val == -1) {
//			mv.visitInsn(ICONST_M1);
//		} else if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) {
//			mv.visitIntInsn(BIPUSH, val);
//		} else if (val >= Short.MIN_VALUE && val <= Short.MAX_VALUE) {
//			mv.visitIntInsn(SIPUSH, val);
//		} else {
//			mv.visitLdcInsn(new Integer(val));
//		}
		mv.visitLdcInsn(val);
		return intLitExpression;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		
		if (paramDec.getType().equals(TypeName.BOOLEAN)) {
			fv = cw.visitField(0, paramDec.getIdent().getText(), "Z", null, null);
		} else if (paramDec.getType().equals(TypeName.INTEGER)){
			fv = cw.visitField(0, paramDec.getIdent().getText(), "I", null, null);
		}
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(argIndex);
		mv.visitInsn(AALOAD);
		if (paramDec.getType().equals(TypeName.INTEGER)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
		} else {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
		}
		argIndex++;
		fv.visitEnd();
		return null;
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
		//TODO Implement this

		Label l4 = new Label();
		mv.visitJumpInsn(GOTO, l4);
		Label l5 = new Label();
		mv.visitLabel(l5);
		
		whileStatement.getB().visit(this, arg);
		
		mv.visitLabel(l4);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, l5);
				
		return null;
	}

}
