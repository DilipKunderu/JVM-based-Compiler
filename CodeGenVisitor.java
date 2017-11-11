package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
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
//			String arg1 = "";
//			if (dec.getType() == TypeName.INTEGER) {
//				arg1 = "I";
//			} else if (dec.getType() == TypeName.BOOLEAN) {
//				arg1 = "Z";
//			}
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
					dec.getType().getJVMTypeDesc(), null, startLabel, endLabel, dec.getSlotNumber());
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
//		assert false : "not yet implemented";
		
		Chain c1 = binaryChain.getE0();
		Chain c2 = binaryChain.getE1();
		if (c1 instanceof IdentChain) {
			c1.visit(this, true);
		} else if (c1 instanceof FilterOpChain) {
			c1.visit(this, binaryChain.getArrow());
		} else if (c1 instanceof FrameOpChain) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", 
					PLPRuntimeFrame.createOrSetFrameSig, false);
		} else {
			c1.visit(this, null);
		}
		
		if (c2 instanceof IdentChain) {
			c2.visit(this, false);
		} else if (c1 instanceof FilterOpChain) {
			c2.visit(this, binaryChain.getArrow());
		} else if (c1 instanceof FrameOpChain) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", 
					PLPRuntimeFrame.createOrSetFrameSig, false);
		} else {
			c2.visit(this, null);
		}
		
		return binaryChain;
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
			} else if (t0 == TypeName.IMAGE){
				switch(opKind) { 
					case PLUS:
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", 
								PLPRuntimeImageOps.addSig, false);
						break;
					case MINUS:
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", 
								PLPRuntimeImageOps.subSig, false);
						break;
				}
			}
		} else if (t0 == TypeName.IMAGE && t1 == TypeName.INTEGER) {
			switch(opKind) {
				case TIMES:
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", 
							PLPRuntimeImageOps.mulSig, false);
					break;
				case DIV:
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", 
						PLPRuntimeImageOps.divSig, false);
					break;
				case MOD:
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", 
							PLPRuntimeImageOps.modSig, false);
					break;
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
			if (stmt instanceof BinaryChain) {
				mv.visitInsn(POP);
			}
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
//		assert false : "not yet implemented";
		if (constantExpression.getFirstToken().kind == Kind.KW_SCREENWIDTH) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);		
		} else if (constantExpression.getFirstToken().kind == Kind.KW_SCREENHEIGHT){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		return constantExpression;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		declaration.setSlotNumber(decCntr);
		if (declaration.getType() == TypeName.FRAME 
				|| declaration.getType() == TypeName.IMAGE) {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlotNumber());
		}
		decCntr++;
		return declaration;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
//		assert false : "not yet implemented";
		Token arrowOp = (Token) arg;
		if (arrowOp.kind == Kind.ARROW) {
			mv.visitInsn(ACONST_NULL);
		} else if (arrowOp.kind == Kind.BARARROW) {
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", 
					PLPRuntimeImageOps.copyImageSig, false);
			mv.visitInsn(SWAP);
		}
		if (filterOpChain.firstToken.kind == Kind.OP_GRAY) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", 
				PLPRuntimeFilterOps.opSig, false);
		} else if (filterOpChain.firstToken.kind == Kind.OP_BLUR) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", 
					PLPRuntimeFilterOps.opSig, false);
		} else if (filterOpChain.firstToken.kind == Kind.OP_CONVOLVE) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", 
					PLPRuntimeFilterOps.opSig, false);
		}
//		mv.visitInsn(POP);
		return filterOpChain;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
//		assert false : "not yet implemented";
		
		//TODO^ make sure image and file are loaded on to stack
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);
		Kind kind = frameOpChain.getFirstToken().kind;
		
//		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", 
//				PLPRuntimeFrame.createOrSetFrameSig, false);
		if (kind == Kind.KW_SHOW) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", 
					PLPRuntimeFrame.showImageDesc, false);
		} else if (kind == Kind.KW_HIDE) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", 
					PLPRuntimeFrame.hideImageDesc, false);
		} else if (kind == Kind.KW_MOVE) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", 
					PLPRuntimeFrame.moveFrameDesc, false);
		} else if (kind == Kind.KW_XLOC) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", 
					PLPRuntimeFrame.getXValDesc, false);
		} else if (kind == Kind.KW_YLOC) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", 
					PLPRuntimeFrame.getYValDesc, false);
		}
		
		return frameOpChain;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
//		assert false : "not yet implemented";
		Boolean isRight = (Boolean) arg;
		TypeName type = identChain.getType();
		Dec chdec = identChain.getDec();
		if (isRight) {
			if (type == TypeName.INTEGER) {
				if (chdec.getSlotNumber() < 0) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, chdec.getIdent().getText(), type.getJVMTypeDesc());
				} else {
					mv.visitVarInsn(ISTORE, chdec.getSlotNumber());
				}
			} else if (type == TypeName.IMAGE) {
				//TODO6 check if dup and pop are required
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", 
						PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, chdec.getSlotNumber());
			} else if (type == TypeName.FILE) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(PUTFIELD, className, chdec.getIdent().getText(), type.getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", 
						PLPRuntimeImageIO.writeImageDesc, false);
			} else if (type == TypeName.FRAME) {
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, chdec.getSlotNumber());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", 
						PLPRuntimeFrame.createOrSetFrameSig, false);
			} else if (type == TypeName.URL) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, chdec.getIdent().getText(), 
						chdec.getType().getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", 
						PLPRuntimeImageIO.readFromURLSig, false);
			} else {
				//TODO6: check if we need to throw exception
			}
		} else {
			if (chdec.getSlotNumber() < 0) {
				if (chdec.getType() == TypeName.URL) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, chdec.getIdent().getText(), 
							chdec.getType().getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", 
							PLPRuntimeImageIO.readFromURLSig, false);
				} else if (chdec.getType() == TypeName.FILE) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, chdec.getIdent().getText(), 
							chdec.getType().getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", 
							PLPRuntimeImageIO.readFromFileDesc, false);
				} else {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, chdec.getIdent().getText(), 
							chdec.getType().getJVMTypeDesc());
				}
			} else {
				if (type == TypeName.INTEGER || type == TypeName.BOOLEAN) {
					mv.visitVarInsn(ILOAD, chdec.getSlotNumber());
				} else {
					mv.visitVarInsn(ALOAD, chdec.getSlotNumber());
				}
			}
		}
		return identChain;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		Dec dec = identExpression.getDec();
		if (dec.getSlotNumber() == -1) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), dec.getType().getJVMTypeDesc());
		} else {
			if (dec.getType() == TypeName.INTEGER || dec.getType() == TypeName.BOOLEAN) {
				mv.visitVarInsn(ILOAD, dec.getSlotNumber());
			} else {
				mv.visitVarInsn(ALOAD, dec.getSlotNumber());
			}
		}
		return identExpression;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		Dec dec = identX.getDec();
		if (dec.getSlotNumber() < 0) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			if (dec.getType().equals(TypeName.INTEGER)) {
				mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "I");
			} else if (dec.getType().equals(TypeName.BOOLEAN)) {
				mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "Z");
			}
		} else {
			if (dec.getType() == TypeName.IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", 
						PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, dec.getSlotNumber());
			} else if (dec.getType() == TypeName.INTEGER || dec.getType() == TypeName.BOOLEAN){
				mv.visitVarInsn(ISTORE, dec.getSlotNumber());
			}
		}
		return identX;
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
//		assert false : "not yet implemented";
		Tuple tuple = imageOpChain.getArg();
		tuple.visit(this, arg);
		Kind k = imageOpChain.firstToken.kind;
		if (k == Kind.KW_SCALE) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", 
					PLPRuntimeImageOps.scaleSig, false);
		} else if (k == Kind.OP_WIDTH) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, 
					"getWidth", PLPRuntimeImageOps.getWidthSig, false);
		} else if (k == Kind.OP_HEIGHT) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, 
					"getHeight", PLPRuntimeImageOps.getHeightSig, false);
		}
		return imageOpChain;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		int val = intLitExpression.getVal();
		mv.visitLdcInsn(val);
		return intLitExpression;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		//TODO:6 check if L needs to be removed for URL and FILE
//		if (paramDec.getType().equals(TypeName.BOOLEAN)) {
//			fv = cw.visitField(0, paramDec.getIdent().getText(), paramDec.getType().getJVMTypeDesc(), null, null);
//		} else if (paramDec.getType().equals(TypeName.INTEGER)){
//			fv = cw.visitField(0, paramDec.getIdent().getText(), "I", null, null);
//		}
		if (paramDec.getType() == TypeName.URL) {
			fv = cw.visitField(0, paramDec.getIdent().getText(), paramDec.getType().getJVMTypeDesc(), null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(argIndex);
			mv.visitMethodInsn(INVOKESTATIC, "PLPRuntimeImageIO", "getURL", "([Ljava/lang/String;I)Ljava/net/URL;", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Ljava/net/URL;");
			
		} else if (paramDec.getType() == TypeName.FILE) {
			fv = cw.visitField(0, paramDec.getIdent().getText(), paramDec.getType().getJVMTypeDesc(), null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(argIndex);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Ljava/io/File;");
			
		} else {
			fv = cw.visitField(0, paramDec.getIdent().getText(), paramDec.getType().getJVMTypeDesc(), null, null);
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
			fv.visitEnd();
		}
		argIndex++;
		return paramDec;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
//		assert false : "not yet implemented";
		
//		mv.visitTypeInsn(NEW, "java/lang/Long");
//		mv.visitInsn(DUP);
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
//		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V", false);
//		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		
		return sleepStatement;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
//		assert false : "not yet implemented";
		List<Expression> exps = tuple.getExprList();
		for (int i = 0; i < exps.size(); i++) {
			exps.get(i).visit(this, arg);
		}
		return tuple;
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
				
		return whileStatement;
	}

}
