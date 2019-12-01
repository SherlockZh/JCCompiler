import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class FirstSetBuilder {
	
    private HashMap<Integer, Symbols> symbolMap = new HashMap<Integer, Symbols>();
    private ArrayList<Symbols> symbolArray = new ArrayList<Symbols>();
    private boolean runFirstSetPass = true;
  
	int productionCount = 0;
	
    public FirstSetBuilder() {
    	initProductions();
    }
    
    public boolean isSymbolNullable(int sym) {
    	Symbols symbol= symbolMap.get(sym);
    	if (symbol == null) {
    		return false;
    	}
    	
    	return symbol.isNullable;
    }
    
    private void initProductions() {
    	symbolMap = CGrammarInitializer.getInstance().getSymbolMap();
    	symbolArray = CGrammarInitializer.getInstance().getSymbolArray();
    }
    
    public void runFirstSets() {
    	while (runFirstSetPass) {
    		runFirstSetPass = false;
    		
    		Iterator<Symbols> it = symbolArray.iterator();
    		while (it.hasNext()) {
    			Symbols symbol = it.next();
    			addSymbolFirstSet(symbol);
    		}
    		
    	}
    	
    	printAllFirstSet();
		System.out.println("============");
    }
    
    private void addSymbolFirstSet(Symbols symbol) {
    	if (isSymbolTerminals(symbol.value)) {
    		if (!symbol.firstSet.contains(symbol.value)) {
    			symbol.firstSet.add(symbol.value);
    		}
    		return;
    	}
    	
    	for (int i = 0;  i < symbol.productions.size(); i++) {
    		int[] rightSize = symbol.productions.get(i);
    		if (rightSize.length == 0) {
    			continue;
    		}
    		
    		if (isSymbolTerminals(rightSize[0]) && !symbol.firstSet.contains(rightSize[0])) {
    			runFirstSetPass = true;
    			symbol.firstSet.add(rightSize[0]);
    		}
    		else if (!isSymbolTerminals(rightSize[0])) {
    			//add first set of nullable
    			int pos = 0;
    			Symbols curSymbol = null;
    			do {
    				curSymbol = symbolMap.get(rightSize[pos]);
    				if (!symbol.firstSet.containsAll(curSymbol.firstSet)) {
    					runFirstSetPass = true;
    					
    					for (int j = 0; j < curSymbol.firstSet.size(); j++) {
    						if (!symbol.firstSet.contains(curSymbol.firstSet.get(j))) {
    							symbol.firstSet.add(curSymbol.firstSet.get(j));
    						}
    					}//for (int j = 0; j < curSymbol.firstSet.size(); j++)
    					
    				}//if (symbol.firstSet.containsAll(curSymbol.firstSet) == false)
    				
    				pos++;
    			}while(pos < rightSize.length && curSymbol.isNullable);
    		} // else
    		
    	}//for (int i = 0; i < symbol.productions.size(); i++)
    }
    
    private boolean isSymbolTerminals(int symbol) {
    	return CTokenType.isTerminal(symbol);
    }
    
    public void printAllFirstSet() {
		for (Symbols sym : symbolArray) {
			printFirstSet(sym);
		}
    }
    
    private void printFirstSet(Symbols symbol) {
    	
    	StringBuilder s = new StringBuilder(CTokenType.getSymbolStr(symbol.value));
    	s.append("{ ");
    	for (int i = 0; i < symbol.firstSet.size(); i++) {
    		s.append(CTokenType.getSymbolStr(symbol.firstSet.get(i))).append(" ");
    	}
    	s.append(" }");
    	
    	System.out.println(s.toString());
    	System.out.println("============");
    }
    
    public ArrayList<Integer> getFirstSet(int symbol) {
		for (Symbols sym : symbolArray) {
			if (sym.value == symbol) {
				return sym.firstSet;
			}
		}
		
		return null;
	
    }
    
 
}
