package cop5556sp17;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import cop5556sp17.AST.Dec;

public class SymbolTable {

	class SymbolTableEntry {

	}

	// TODO add fields
	int current_scope, next_scope;

	Stack<Integer> scope_stack;
	Map<String, Map<Integer, Dec>> symbol_table;
	Map<Integer, Dec> temp;
	Iterator<Integer> it; 
	/**
	 * to be called when block entered
	 */
	public void enterScope() {
		// TODO: IMPLEMENT THIS
		current_scope = next_scope++;
		scope_stack.push(current_scope);
	}

	/**
	 * leaves scope
	 */
	public void leaveScope() {
		// TODO: IMPLEMENT THIS
		scope_stack.pop();
		if (scope_stack.size() > 0) {
			current_scope = scope_stack.peek();
		}
	}

	public boolean insert(String ident, Dec dec) {
		// TODO: IMPLEMENT THIS
		if (symbol_table.containsKey(ident)) {
			symbol_table.get(ident).put(current_scope, dec);
		} else {
			temp = new HashMap<>();
			temp.put(current_scope, dec);
			symbol_table.put(ident, temp);
		}
		return true;
	}

	public Dec lookup(String ident) {
		// TODO: IMPLEMENT THIS
		temp =  symbol_table.get(ident);
		int s = 0;
		if (null == temp) return null;
		
		for (int i = scope_stack.size() - 1; !(i < 0); i--) {
			s = scope_stack.get(i);
			
			if (temp.containsKey(s)){
				return temp.get(s); 
			}
		}
		return null;
	}

	public SymbolTable() {
		// TODO: IMPLEMENT THIS
		scope_stack = new Stack<>();
		symbol_table = new HashMap<>();
		current_scope = 0;
		next_scope = current_scope + 1;
	}

	@Override
	public String toString() {
		// TODO: IMPLEMENT THIS
		return symbol_table.toString();
	}

}
