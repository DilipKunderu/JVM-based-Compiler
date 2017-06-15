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
import java.util.Iterator;
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
		TypeName t0 = ((Chain) binaryChain.getE0().visit(this, null)).gettype();
		TypeName t1 = ((ChainElem) binaryChain.getE1().visit(this, null)).gettype();

		Token t = binaryChain.getArrow();

		if (t.kind == ARROW) {
			if ((t0 == URL || t0 == FILE) && t1 == IMAGE)

				binaryChain.settype(IMAGE);

			else if (t0 == FRAME) {
				if (binaryChain.getE1() instanceof FrameOpChain) {
					if (binaryChain.getE1().firstToken.kind == KW_XLOC
							|| binaryChain.getE1().firstToken.kind == KW_YLOC)

						binaryChain.settype(INTEGER);

					else if (binaryChain.getE1().firstToken.kind == KW_SHOW
							|| binaryChain.getE1().firstToken.kind == KW_HIDE
							|| binaryChain.getE1().firstToken.kind == KW_MOVE)

						binaryChain.settype(FRAME);

					else
						throw new TypeCheckException(
								"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
				} else
					throw new TypeCheckException("Illegal Type");
			} else if (t0 == INTEGER) {
				if (binaryChain.getE1() instanceof IdentChain && binaryChain.getE1().gettype() == INTEGER)

					binaryChain.settype(INTEGER);

				else
					throw new TypeCheckException(
							"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
			} else if (t0 == IMAGE) {
				if (t1 == FRAME)

					binaryChain.settype(FRAME);

				else if (t1 == FILE)

					binaryChain.settype(NONE);

				else if (binaryChain.getE1() instanceof IdentChain)

					binaryChain.settype(IMAGE);

				else if (binaryChain.getE1() instanceof IdentChain && binaryChain.getE1().gettype() == IMAGE)

					binaryChain.settype(IMAGE);

				else if (binaryChain.getE1() instanceof FilterOpChain) {
					if (binaryChain.getE1().firstToken.kind == OP_GRAY
							|| binaryChain.getE1().firstToken.kind == OP_CONVOLVE
							|| binaryChain.getE1().firstToken.kind == OP_BLUR)

						binaryChain.settype(IMAGE);

					else
						throw new TypeCheckException(
								"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
				} else if (binaryChain.getE1() instanceof ImageOpChain) {
					if (binaryChain.getE1().firstToken.kind == OP_WIDTH
							|| binaryChain.getE1().firstToken.kind == OP_HEIGHT)

						binaryChain.settype(INTEGER);

					else if (binaryChain.getE1().firstToken.kind == KW_SCALE)

						binaryChain.settype(IMAGE);

					else
						throw new TypeCheckException(
								"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
				} else
					throw new TypeCheckException(
							"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
			} else
				throw new TypeCheckException(
						"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
		} else if (t.kind == BARARROW) {
			if (t1 == IMAGE && ((binaryChain.getE1() instanceof FilterOpChain)
					&& (binaryChain.getE1().firstToken.kind == OP_GRAY
							|| binaryChain.getE1().firstToken.kind == OP_CONVOLVE
							|| binaryChain.getE1().firstToken.kind == OP_BLUR)))

				binaryChain.settype(IMAGE);

			else
				throw new TypeCheckException(
						"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
		} else
			throw new TypeCheckException(
					"Illegal combination of " + t0.toString() + " and " + t1.toString() + " for " + t.kind);
		return binaryChain;
	}

	/*
	 * @Override public Object visitBinaryChain(BinaryChain binaryChain, Object
	 * arg) throws Exception { // TODO Auto-generated method stub TypeName t0 =
	 * ((Chain) binaryChain.getE0().visit(this, null)).gettype(); TypeName t1 =
	 * ((ChainElem) binaryChain.getE1().visit(this, null)).gettype();
	 * 
	 * Token t = binaryChain.getArrow();
	 * 
	 * if (t.kind == ARROW) {
	 * 
	 * if (t1 == IMAGE && (t0 == URL || t0 == FILE))
	 * 
	 * binaryChain.settype(IMAGE);
	 * 
	 * else if (t0 == FRAME) {
	 * 
	 * if (binaryChain.getE1() instanceof FrameOpChain) {
	 * 
	 * if (binaryChain.getE1().firstToken.kind == KW_SHOW ||
	 * binaryChain.getE1().firstToken.kind == KW_HIDE ||
	 * binaryChain.getE1().firstToken.kind == KW_MOVE) {
	 * 
	 * binaryChain.settype(FRAME); } else if
	 * (binaryChain.getE1().firstToken.kind == KW_XLOC ||
	 * binaryChain.getE1().firstToken.kind == KW_YLOC) {
	 * 
	 * binaryChain.settype(INTEGER); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind);
	 * 
	 * } else if (t0 == IMAGE) { if (binaryChain.getE1() instanceof
	 * ImageOpChain) { if (binaryChain.getE1().firstToken.kind == OP_WIDTH ||
	 * binaryChain.getE1().firstToken.kind == OP_HEIGHT) {
	 * binaryChain.settype(INTEGER); } else if
	 * (binaryChain.getE1().firstToken.kind == KW_SCALE) {
	 * binaryChain.settype(IMAGE); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind); } else if (t1 == FRAME) { binaryChain.settype(FRAME);
	 * } else if (t1 == FILE) { binaryChain.settype(NONE); } else if
	 * (binaryChain.getE1() instanceof FilterOpChain) { if
	 * (binaryChain.getE1().firstToken.kind == OP_GRAY ||
	 * binaryChain.getE1().firstToken.kind == OP_CONVOLVE ||
	 * binaryChain.getE1().firstToken.kind == OP_BLUR) {
	 * binaryChain.settype(IMAGE); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind); } else if (binaryChain.getE1() instanceof IdentChain)
	 * { binaryChain.settype(IMAGE); } else if ((binaryChain.getE1() instanceof
	 * IdentChain) && (binaryChain.getE1().gettype() == IMAGE)) {
	 * binaryChain.settype(IMAGE); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind);
	 * 
	 * } else if (t0 == INTEGER) { if ((binaryChain.getE1() instanceof
	 * IdentChain) && binaryChain.getE1().gettype() == INTEGER) {
	 * binaryChain.settype(INTEGER); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind); } else if (t.kind == BARARROW) { if (t0 == IMAGE &&
	 * ((binaryChain.getE1() instanceof FilterOpChain) &&
	 * (binaryChain.getE1().firstToken.kind == OP_GRAY ||
	 * binaryChain.getE1().firstToken.kind == OP_BLUR ||
	 * binaryChain.getE1().firstToken.kind == OP_CONVOLVE))) {
	 * binaryChain.getE1().settype(IMAGE); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind); } else throw new TypeCheckException(
	 * "Illegal combination of " + t0.toString() + " and " + t1.toString() +
	 * " for " + t.kind);
	 * 
	 * return binaryChain; }
	 */

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName t0 = ((Expression) binaryExpression.getE0().visit(this, null)).gettype();
		TypeName t1 = ((Expression) binaryExpression.getE1().visit(this, null)).gettype();

		if (t0 == t1) {
			if (binaryExpression.getOp().kind == EQUAL || binaryExpression.getOp().kind == NOTEQUAL) {
				binaryExpression.settype(BOOLEAN);
				return binaryExpression;
			}
			if (t0 == INTEGER) {
				switch (binaryExpression.getOp().kind) {
				case PLUS:
				case MINUS:
				case TIMES:
				case DIV:
				case MOD:
				case AND:
				case OR:
					binaryExpression.settype(t0);
					break;
				case EQUAL:
				case NOTEQUAL:
				case LT:
				case LE:
				case GT:
				case GE:
					binaryExpression.settype(BOOLEAN);
					break;
				default:
					throw new TypeCheckException("Illegal combination of " + t0.toString() + " and " + t1.toString()
							+ " for " + binaryExpression.getOp().kind);
				}
			} else if (t0 == IMAGE) {
				switch (binaryExpression.getOp().kind) {
				case EQUAL:
				case NOTEQUAL:
					binaryExpression.settype(BOOLEAN);
					break;
				case PLUS:
				case MINUS:
					binaryExpression.settype(t0);
					break;
				default:
					throw new TypeCheckException("Illegal combination of " + t0.toString() + " and " + t1.toString()
							+ " for " + binaryExpression.getOp().kind);
				}
			} else if (t0 == BOOLEAN) {
				switch (binaryExpression.getOp().kind) {
				case EQUAL:
				case NOTEQUAL:
				case LT:
				case LE:
				case GT:
				case GE:
				case AND:
				case OR:
				case MOD:
					binaryExpression.settype(t0);
					break;
				default:
					throw new TypeCheckException("Illegal combination of " + t0.toString() + " and " + t1.toString()
							+ " for " + binaryExpression.getOp().kind);
				}
			} else
				throw new TypeCheckException("Illegal combination of " + t0.toString() + " and " + t1.toString()
						+ " for " + binaryExpression.getOp().kind);
		} else {
			if ((t0 == INTEGER && t1 == IMAGE) || (t1 == INTEGER && t0 == IMAGE)) {
				switch (binaryExpression.getOp().kind) {
				case TIMES:
				case DIV:
					binaryExpression.settype(IMAGE);
					break;
				default:
					throw new TypeCheckException("Illegal combination of " + t0.toString() + " and " + t1.toString()
							+ " for " + binaryExpression.getOp().kind);
				}
			} else
				throw new TypeCheckException("Illegal combination of " + t0.toString() + " and " + t1.toString()
						+ " for " + binaryExpression.getOp().kind);
		}
		return binaryExpression;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();

		List<Dec> decList = block.getDecs();
		List<Statement> stmList = block.getStatements();

		Iterator<Dec> decit = decList.iterator();
		Iterator<Statement> stmit = stmList.iterator();

		Dec dec = null;
		Statement stmt = null;

		while (decit.hasNext() || stmit.hasNext() || dec != null || stmt != null) {
			if (decit.hasNext() && dec == null) {
				dec = (Dec) decit.next();
			}

			if (stmit.hasNext() && stmt == null) {
				stmt = (Statement) stmit.next();
			}
			if (dec == null) {
				stmt.visit(this, null);
				stmt = null;
			} else if (stmt == null) {
				dec.visit(this, null);
				dec = null;
			} else {
				if (dec.firstToken.getLinePos().line < stmt.firstToken.getLinePos().line) {
					dec.visit(this, null);
					dec = null;
				} else if (dec.firstToken.getLinePos().line > stmt.firstToken.getLinePos().line) {
					stmt.visit(this, null);
					stmt = null;
				} else {
					if (dec.firstToken.getLinePos().posInLine < stmt.firstToken.getLinePos().posInLine) {
						dec.visit(this, null);
						dec = null;
					} else {
						stmt.visit(this, null);
						stmt = null;
					}
				}
			}
		}
		symtab.leaveScope();
		return block;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.settype(TypeName.BOOLEAN);
		return booleanLitExpression;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		filterOpChain.getArg().visit(this, arg);
		if (filterOpChain.getArg().getExprList().size() == 0) {
			filterOpChain.settype(IMAGE);
		} else
			throw new TypeCheckException("Tuple length should be zero");
		return filterOpChain;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		frameOpChain.getArg().visit(this, null);
		if (frameOpChain.getFirstToken().isKind(KW_SHOW) || frameOpChain.getFirstToken().isKind(KW_HIDE)) {
			if (frameOpChain.getArg().getExprList().size() == 0) {
				frameOpChain.settype(NONE);
			} else
				throw new TypeCheckException("frameOpChain error");
		} else if (frameOpChain.getFirstToken().isKind(KW_XLOC) || frameOpChain.getFirstToken().isKind(KW_YLOC)) {
			if (frameOpChain.getArg().getExprList().size() == 0) {
				frameOpChain.settype(INTEGER);
			} else
				throw new TypeCheckException("frameOpChain error");
		} else if (frameOpChain.getFirstToken().isKind(KW_MOVE)) {
			if (frameOpChain.getArg().getExprList().size() == 2) {
				frameOpChain.settype(NONE);
			} else
				throw new TypeCheckException("frameOpChain error");
		} else
			throw new TypeCheckException("Bug in Parser");
		return frameOpChain;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec dec = symtab.lookup(identChain.getFirstToken().getText());
		if (dec != null) {
			identChain.setDec(dec);
			identChain.settype(dec.gettype());
			return identChain;
		}
		throw new TypeCheckException("identChain error");
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec dec = symtab.lookup(identExpression.getFirstToken().getText());

		if (dec == null) {
			throw new TypeCheckException("identExpression error");
		} else {
			identExpression.setDec(dec);
			identExpression.settype(dec.gettype());
		}
		return identExpression;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = (Expression) ifStatement.getE().visit(this, null);
		if (e.gettype() == BOOLEAN) {
			ifStatement.getB().visit(this, null);
			return ifStatement;
		}
		throw new TypeCheckException("ifStatement error");
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.settype(TypeName.INTEGER);
		return intLitExpression;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = (Expression) sleepStatement.getE().visit(this, null);
		if (e.gettype() == INTEGER) {
			return sleepStatement;
		}
		throw new TypeCheckException("sleep statement error");
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// TypeName
		Expression e = (Expression) whileStatement.getE().visit(this, null);
		if (e.gettype() == BOOLEAN) {
			whileStatement.getB().visit(this, null);
			return whileStatement;
		}
		throw new TypeCheckException("whileStatement error");
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName t0 = Type.getTypeName(declaration.getFirstToken());
		declaration.settype(t0);

		if (symtab.insert(declaration.getIdent().getText(), declaration)) {
			return declaration;
		}
		throw new TypeCheckException("visitDec error");
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<ParamDec> paramDecList = program.getParams();

		for (ParamDec pd : paramDecList) {
			pd.visit(this, null);
		}
		program.getB().visit(this, null);
		return program;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		IdentLValue iv = assignStatement.getVar();
		iv.visit(this, null);
		assignStatement.getE().visit(this, null);

		if (iv.getType() == assignStatement.getE().gettype()) {
			return assignStatement;
		}
		throw new TypeCheckException("assignmentStatement error");
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec dec = symtab.lookup(identX.getText());

		if (dec == null) {
			throw new TypeCheckException("identX error");
		} else {
			identX.setDec(dec);
			identX.setType(dec.gettype());
		}
		return identX;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName t0 = Type.getTypeName(paramDec.getFirstToken());
		paramDec.settype(t0);

		if (symtab.insert(paramDec.getIdent().getText(), paramDec)) {
			return paramDec;
		}
		throw new TypeCheckException("visitParamDec error");
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.settype(TypeName.INTEGER);
		return constantExpression;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		imageOpChain.getArg().visit(this, null);
		if (imageOpChain.getFirstToken().isKind(OP_WIDTH) || imageOpChain.getFirstToken().isKind(OP_HEIGHT)) {
			if (imageOpChain.getArg().getExprList().size() == 0) {
				imageOpChain.settype(INTEGER);
			} else
				throw new TypeCheckException("imageOpChain error");
		} else if (imageOpChain.getFirstToken().isKind(KW_SCALE)) {
			if (imageOpChain.getArg().getExprList().size() == 1) {
				imageOpChain.settype(IMAGE);
			} else
				throw new TypeCheckException("imageOpChain error");
		} else
			throw new TypeCheckException("imageOpChain error");
		return imageOpChain;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<Expression> expList = tuple.getExprList();

		for (Expression e : expList) {
			e.visit(this, null);

			if (!(e.gettype() == INTEGER)) {
				throw new TypeCheckException("Illegal Tuple parameter types");
			}
		}
		return tuple;
	}

}
