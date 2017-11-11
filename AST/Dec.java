package cop5556sp17.AST;

import org.objectweb.asm.Label;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public class Dec extends ASTNode {
	
	final Token ident;
	public TypeName type;
	int slotNumber;
	Label startLabel;
	Label endLabel;
	
	public Label getStartLabel() {
		return startLabel;
	}

	public void setStartLabel(Label startLabel) {
		this.startLabel = startLabel;
	}

	public Label getEndLabel() {
		return endLabel;
	}

	public void setEndLabel(Label endLabel) {
		this.endLabel = endLabel;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}

	public Dec(Token firstToken, Token ident) {
		super(firstToken);
		this.slotNumber = -1;
		this.ident = ident;
	}

	public TypeName getType() {
		return type;
	}
	public void setType(TypeName type) {
		this.type = type;
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
