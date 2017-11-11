package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	
	public TypeName type;
	public int val;
	
	protected Expression(Token firstToken) {
		super(firstToken);
	}
	
	public TypeName getType() {
		return type;
	}

	public void setType(TypeName type) {
		this.type = type;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
