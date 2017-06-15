package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;

import org.objectweb.asm.Label;

import cop5556sp17.Scanner.Token;

public class Dec extends ASTNode {
	
	TypeName type;
	Label start_label;
	Label end_label;
	int slot;
	
	public Label getStart_label() {
		return start_label;
	}

	public void setStart_label(Label start_label) {
		this.start_label = start_label;
	}

	public Label getEnd_label() {
		return end_label;
	}

	public void setEnd_label(Label end_label) {
		this.end_label = end_label;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public TypeName gettype() {
		return type;
	}

	public void settype(TypeName type) {
		this.type = type;
	}
	
	final Token ident;

	public Dec(Token firstToken, Token ident) {
		super(firstToken);

		this.ident = ident;
		this.slot = -1;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}

}
