package cop5556sp17;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cop5556sp17.AST.Dec;


public class SymbolTable {
	
	//TODO  add fields
	private int maxScope;
	Stack<Integer> scopestack;
	
	//key1 is scope, key2 is ident
	Map<String, Map<Integer, Dec>> symbolmap;
	
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		maxScope++;
		this.scopestack.push(maxScope);
	}
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		scopestack.pop();
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		Map<Integer, Dec> map;
		if (symbolmap.containsKey(ident)) {
			map = symbolmap.get(ident);
		} else {
			map = new HashMap<>();
		}
		int curr_scope = currScope();
		if (map.containsKey(curr_scope)) {
			return false;
		}
		map.put(curr_scope, dec);
		symbolmap.put(ident, map);
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		if (!symbolmap.containsKey(ident)) {
			return null;
		}
		Map<Integer, Dec> map = symbolmap.get(ident);
        for(int i1 = scopestack.size()-1; i1>=0; i1-- ) {
			if (map.containsKey(scopestack.get(i1))) {
				return map.get(scopestack.get(i1));
			}
		}
		return null;
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		this.maxScope = 0;
		scopestack = new Stack<Integer>();
		scopestack.push(0);
		symbolmap = new HashMap<>();
	}

	private int currScope() {
		return scopestack.peek();
	}

	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return symbolmap.toString();
	}
	
}
