package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Chain c1 = binaryChain.getE0();
		ChainElem c2 = binaryChain.getE1();
		c1 = (Chain) c1.visit(this, null);
		c2 = (ChainElem) c2.visit(this, null);
		Token op = binaryChain.getArrow();
		
		if (c1.getType() == TypeName.URL && c2.getType() == TypeName.IMAGE && op.kind == Kind.ARROW) {
			binaryChain.setType(TypeName.IMAGE);
		} else if (c1.getType() == TypeName.FILE && c2.getType() == TypeName.IMAGE && op.kind == Kind.ARROW) {
			binaryChain.setType(TypeName.IMAGE);
		} else if (c1.getType() == TypeName.FRAME && op.kind == Kind.ARROW && c2 instanceof FrameOpChain) {
			if (c2.getFirstToken().kind == Kind.KW_XLOC 
					|| c2.getFirstToken().kind == Kind.KW_YLOC) {
				binaryChain.setType(TypeName.INTEGER);
			} else {
				binaryChain.setType(TypeName.FRAME);
			}
		} else if (c1.getType() == TypeName.IMAGE) {
			Kind kd = c2.getFirstToken().kind;
			if ((op.kind == Kind.ARROW) 
					&& (kd == Kind.OP_WIDTH || kd == Kind.OP_HEIGHT) 
					&& (c2 instanceof ImageOpChain)) {
				binaryChain.setType(TypeName.INTEGER);
			} else if ((c2 instanceof FilterOpChain)
					&& (kd == Kind.OP_GRAY || kd == Kind.OP_BLUR || kd == Kind.OP_CONVOLVE)) {
				binaryChain.setType(TypeName.IMAGE);
			} else if (kd == Kind.KW_SCALE && op.kind == Kind.ARROW && c2 instanceof  ImageOpChain) {
				binaryChain.setType(TypeName.IMAGE);
			} else if (c2.getType() == TypeName.FRAME && op.kind == Kind.ARROW) {
				binaryChain.setType(TypeName.FRAME);
			} else if (c2.getType() == TypeName.FILE && op.kind == Kind.ARROW) {
				binaryChain.setType(TypeName.NONE);
			} else if (c2 instanceof IdentChain && op.kind == Kind.ARROW 
					&& c2.type == TypeName.IMAGE) {
				binaryChain.setType(TypeName.IMAGE);
			} else {
				throw new TypeCheckException("Not a valid chain statement");
			}
		} else if (c1.getType() == TypeName.INTEGER 
				&& c2 instanceof IdentChain && c2.getType() == TypeName.INTEGER) {
			binaryChain.setType(TypeName.INTEGER);
		} else {
			throw new TypeCheckException("Not a valid chain statement");
		}
		return binaryChain;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();
		
		Expression val1Obj = (Expression) e0.visit(this, null);
		Expression val2Obj = (Expression) e1.visit(this, null);
		Token op = binaryExpression.getOp();
		
		if (val1Obj.getType() == TypeName.BOOLEAN && val2Obj.getType() == TypeName.BOOLEAN) {
//			boolean val1 = (Boolean) val1Obj;
//			boolean val2 = (Boolean) val2Obj;
//			boolean val0;
			switch (op.kind) {
				case LT: case GT: case LE: case GE: 
					case EQUAL: case NOTEQUAL: case AND: case OR: {
					binaryExpression.setType(TypeName.BOOLEAN);
					return binaryExpression;
				}
				default: throw new TypeCheckException("Operation " 
						+ op + " cannot be performed on " + e0 + " and " + e1);
			}
		} else if (val1Obj.getType() == TypeName.INTEGER && val2Obj.getType() == TypeName.INTEGER) {
//			int val1 = (Integer) val1Obj;
//			int val2 = (Integer) val2Obj;
//			int val0;
//			boolean valb;
			switch(op.kind) {
				case PLUS: case MINUS: case TIMES: case DIV: case MOD: {
					binaryExpression.setType(TypeName.INTEGER);
					return binaryExpression;
				}
				case LT: case LE: case GT: case GE: case EQUAL: case NOTEQUAL: {
					binaryExpression.setType(TypeName.BOOLEAN);
					return binaryExpression;
				}
				default:
					throw new TypeCheckException("Operation " 
							+ op + " cannot be performed between " + e0 + " and " + e1);
/*					switch (op.kind) {
//						case AND: val0 = val1 && val2; break;
//						case OR: val0 = val1 || val2; break;
						case LT: valb = val1 < val2; break;
						case LE: valb = val1 <= val2; break;
						case GT: valb = val1 > val2; break;
						case GE: valb = val1 >= val2; break;
						case EQUAL: valb = val1 == val2; break;
						case NOTEQUAL: valb = val1 != val2; break;
						default: throw new TypeCheckException("Operation " 
								+ op + " cannot be performed between " + e0 + " and " + e1);
					}
					return valb;*/
			}
//			return val0;
		} else if (((val1Obj.getType() == TypeName.IMAGE
					&& val2Obj.getType() == TypeName.INTEGER) 
				|| (val1Obj.getType() == TypeName.INTEGER
					&& val2Obj.getType() == TypeName.IMAGE)) && (op.kind == Kind.TIMES)){
			binaryExpression.setType(TypeName.IMAGE);
		} else if (val1Obj.getType() == TypeName.IMAGE 
				&& val2Obj.getType() == TypeName.IMAGE 
				&& (op.kind == Kind.PLUS || op.kind == Kind.MINUS)) {
			binaryExpression.setType(TypeName.IMAGE);
			
			
			
			
			
			
			
			
			
			//check if frame + frame scenario comes
		} else if (val1Obj.getType() == val2Obj.getType()){
			binaryExpression.setType(TypeName.BOOLEAN);
		} else {
			throw new TypeCheckException("Incompatible types " + e0 + " and " + e1);
		}
		return binaryExpression;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		class A implements Comparable<A>{
			Dec dec;
			Statement stmt;
			LinePos linepos;
			void initLinePos(Dec dec) {
				this.dec = dec;
				this.stmt = null;
				this.linepos = dec.getFirstToken().getLinePos();
			}
			void initLinePos(Statement stmt) {
				this.dec = null;
				this.stmt = stmt;
				this.linepos = stmt.getFirstToken().getLinePos();
			}
			@Override
			public int compareTo(A arg0) {
				// TODO Auto-generated method stub
				int l1 = this.linepos.line;
				int p1 = this.linepos.posInLine;
				int l2 = arg0.linepos.line;
				int p2 = arg0.linepos.posInLine;
				if (l1 < l2) {
					return -1;
				} else if (l1 > l2) {
					return 1;
				} else {
					if (p1 < p2) {
						return -1;
					} else if (p1 > p2) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		}
		symtab.enterScope();
		List<A> list = new ArrayList<>();
		for (Dec dec: block.getDecs()) {
			A a = new A();
			a.initLinePos(dec);
			list.add(a);
		}
		for (Statement stmt: block.getStatements()) {
			A a = new A();
			a.initLinePos(stmt);
			list.add(a);
		}
		Collections.sort(list);
		for (A a: list) {
			if (a.dec != null) {
				a.dec = (Dec) a.dec.visit(this, null);
			} else {
				a.stmt = (Statement) a.stmt.visit(this, null);
			}
		}
		symtab.leaveScope();
		return block;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.setType(TypeName.BOOLEAN);
		return booleanLitExpression;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple = filterOpChain.getArg();
		if (tuple.getExprList().size() == 0) {
			filterOpChain.setType(TypeName.IMAGE);
			return filterOpChain;
		}
		throw new TypeCheckException("Filter operator Chain doesnt take any arguments.");
	}
	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token token = frameOpChain.getFirstToken();
		Tuple tuple = (Tuple) frameOpChain.getArg().visit(this, null);
		if (token.kind == Kind.KW_SHOW || token.kind == Kind.KW_HIDE) {
			if (tuple.getExprList().size() != 0) {
				throw new TypeCheckException("With show and hide arguments are not expected.");
			}
			frameOpChain.setType(TypeName.NONE);
		} else if (token.kind == Kind.KW_XLOC || token.kind == Kind.KW_YLOC) {
			if (tuple.getExprList().size() != 0) {
				throw new TypeCheckException("With xloc and yloc arguments are not expected.");
			}
			frameOpChain.setType(TypeName.INTEGER);
		} else if (token.kind == Kind.KW_MOVE) {
			if (tuple.getExprList().size() != 2) {
				throw new TypeCheckException("With move 2 arguments are expeceted, and passed are " 
						+ tuple.getExprList().size());
			}
			frameOpChain.setType(TypeName.NONE);
		} else {
			throw new TypeCheckException("Some problem in the parser.");
		}
		return frameOpChain;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec dec = symtab.lookup(identChain.getFirstToken().getText());
		if (dec == null) {
			throw new TypeCheckException("Ident: " + identChain.getFirstToken().getText() 
					+ " is not in scope");
		}
		identChain.setType(dec.getType());
		identChain.setDec(dec);
		return identChain;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token token = identExpression.getFirstToken();
		Dec dec = symtab.lookup(token.getText());
		if (null == dec) {
			throw new TypeCheckException("Ident value " + token.getText() + " is out of scope.");
		}
		identExpression.setDec(dec);
		identExpression.setType(dec.getType());
		return identExpression;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression exp = ifStatement.getE();
		exp = (Expression) exp.visit(this, null);
		if (exp.getType() == TypeName.BOOLEAN) {
			Block block = ifStatement.getB();
			block.visit(this, null);
			return ifStatement;
		}
		throw new TypeCheckException("While Expression is not of type boolean");
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setType(TypeName.INTEGER);
		return intLitExpression;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = sleepStatement.getE();
		e = (Expression) e.visit(this, null);
		if (e.getType() == TypeName.INTEGER) {
			return sleepStatement;
		}
		throw new TypeCheckException("Sleep statement's expression is not of type Integer");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression exp = whileStatement.getE();
		exp = (Expression) exp.visit(this, null);
		if (exp.getType() == TypeName.BOOLEAN) {
			Block block = whileStatement.getB();
			block.visit(this, null);
			return whileStatement;
		}
		throw new TypeCheckException("While Expression is not of type boolean");
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName type = Type.getTypeName(declaration.getFirstToken());
		declaration.setType(type);
		boolean flag = symtab.insert(declaration.getIdent().getText(), declaration);
		if (!flag) {
			throw new TypeCheckException("Ident " + declaration.getIdent().getText() 
					+ " declaration is already exists in the scope");
		}
		return declaration;
	}

	//todo: save prog name
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		String progName = program.getName();
		List<ParamDec> paramList = program.getParams();
		Block progBlock = program.getB();
		for (ParamDec p: paramList) {
			p.visit(this, null);
//			symtab.insert(p.getIdent().getText(), p);
		}
		progBlock.visit(this, null);
		return program;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		IdentLValue ival = assignStatement.getVar();
		Expression e = assignStatement.getE();
		ival = (IdentLValue) ival.visit(this, null);
	
		e = (Expression) e.visit(this, null);
		if (e.getType() != ival.getType()) {
			throw new TypeCheckException("Type mismatch: Expression of type " 
					+ e.type + " cannot be assigned to ident " + ival.getText());
		}
		return assignStatement;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec dec = symtab.lookup(identX.getText());
		if (dec == null) {
			throw new TypeCheckException("Ident " + identX.getText() +  " is not in the scope");
		}
		identX.setType(dec.getType());
		identX.setDec(dec);
		return identX;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName type = Type.getTypeName(paramDec.getFirstToken());
		paramDec.setType(type);
		boolean flag = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if (!flag) {
			throw new TypeCheckException("Ident " + paramDec.getIdent().getText() 
					+ " already exists in the scope.");
		}
		return paramDec;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setType(TypeName.INTEGER);
		return constantExpression;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple = imageOpChain.getArg();
		Kind kind = imageOpChain.getFirstToken().kind;
		if (kind == Kind.OP_WIDTH || kind == Kind.OP_HEIGHT) {
			if (tuple.getExprList().size() != 0) {
				throw new TypeCheckException("Width and height doesnt take arguments");
			}
			imageOpChain.setType(TypeName.INTEGER);
		} else if (kind == Kind.KW_SCALE) {
			if (tuple.getExprList().size() != 1) {
				throw new TypeCheckException("scale takes only one argument");
			}
			imageOpChain.setType(TypeName.IMAGE);
		} else {
			throw new TypeCheckException("Parsing problem");
		}
		return imageOpChain;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<Expression> elist = tuple.getExprList();
		for (Expression e: elist) {
			e = (Expression) e.visit(this, null);
			if (e.getType() != TypeName.INTEGER) {
				throw new TypeCheckException("Argument has to be of type int.");
			}
		}
		return tuple;
	}


}
