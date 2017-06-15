package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	
	TypeName type;

	public TypeName gettype() {
		return type;
	}

	public void settype(TypeName type) {
		this.type = type;
	}
	
	public Chain(Token firstToken) {
		super(firstToken);
	}

}
