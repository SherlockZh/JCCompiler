import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;


public class GrammarState {
	public static int stateNumCount = 0;
	private boolean printInfo = false;
	private boolean transitionDone = false;
    public  int stateNum = -1;
    private GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    private ArrayList<Production> productions;
    private HashMap<Integer, GrammarState> transition = new HashMap<>();
    private ArrayList<Production> closureSet = new ArrayList<>();
    private ProductionManager productionManager = ProductionManager.getProductionManager();
    private HashMap<Integer, ArrayList<Production>> partition = new HashMap<>();
    private ArrayList<Production> mergedProduction = new ArrayList<>();
    
    public static void  increaseStateNum() {
    	stateNumCount++;
    }
    
    public boolean isTransitionMade() {
    	return transitionDone;
    }
    
    public GrammarState(ArrayList<Production> productions) {
    	this.stateNum = stateNumCount;
    	
    	this.productions = productions;
    	
    	if (stateNum == 7) {
    	    System.out.println("closure set of state 7:" + productions.size());
    	}

    	this.closureSet.addAll(this.productions);
    }
    
    public void stateMerge(GrammarState state) {
    	if (!this.productions.contains(state.productions)) {
    		for (int i = 0; i < state.productions.size(); i++) {
    			if (!this.productions.contains(state.productions.get(i))
						&& !mergedProduction.contains(state.productions.get(i))) {
    				mergedProduction.add(state.productions.get(i));
    			}
    		}
    	}
    	
    }
    
    public void print() {
    	System.out.println("State Number: " + stateNum);
		for (Production production : productions) {
			production.print();
		}

		for (Production production : mergedProduction) {
			production.print();
		}
    }
    
    public void printTransition() {
    	for (Map.Entry<Integer, GrammarState> entry: transition.entrySet()) {
    		System.out.println("transfer on " + CTokenType.getSymbolStr(entry.getKey()) + " to state ");
    		entry.getValue().print();
    		System.out.print("\n");
    	}
    }
    
    public void createTransition() {
    	if (transitionDone) {
    		return;
    	}
    	
    	transitionDone = true;
  
    	
    	makeClosure();
    	
    	partition();
  
    	makeTransition();
   
    	printInfo = true;
    	
    }
    
    private void makeClosure() {
    	int debug = 0;
    	if (stateNum == 1) {
    		debug = 1;
    	}
    	
    	Stack<Production> productionStack = new Stack<Production>();
		for (Production value : productions) {
			productionStack.push(value);
		}
    	
    //	System.out.println("---begin make closure----");
    	
    	while (!productionStack.isEmpty()) {
    		Production production = productionStack.pop();
    		System.out.println("\nproduction on top of stack is : ");
    		production.print();
    		production.printBeta();
    		
    		
    		if (CTokenType.isTerminal(production.getDotSymbol())) {
    			    System.out.println("symbol after dot is not non-terminal, ignore and prcess next item");
    			    continue;	
    			}
    		
    		int symbol = production.getDotSymbol();
    		ArrayList<Production> closures = productionManager.getProduction(symbol);
    		ArrayList<Integer> lookAhead = production.computeFirstSetOfBetaAndC();

			for (Production oldProduct : closures) {
				Production newProduct = (oldProduct).cloneSelf();


				newProduct.addLookAheadSet(lookAhead);


				if (!closureSet.contains(newProduct)) {
					System.out.println("push and add new production to stack and closureSet");

					closureSet.add(newProduct);
					productionStack.push(newProduct);
					System.out.println("Add new production:");
					newProduct.print();
					removeRedundantProduction(newProduct);
				} else {
					System.out.println("the production is already exist!");
				}

			}
    	
    		
    	}
    	
    	//printClosure();
    	//System.out.println("----end make closure----");
    	
    }
    
    private void removeRedundantProduction(Production product) {
    	boolean removeHappened = true;
    	
    	while (removeHappened) {
			removeHappened = false;
    		
    		Iterator it = closureSet.iterator();
    		while (it.hasNext()) {
    			Production item = (Production) it.next();
    			if (product.coverUp(item)) {
					removeHappened = true;
    				closureSet.remove(item);
    				if (stateNum == 1) {
    				 //   System.out.print("remove redundant production: ");
        				//item.print();
    				}
    		
    				break;
    			}
    		}
    	}
    }
    
  
    private void printClosure() {
    	if (printInfo) {
    		return;
    	}
    	
    	System.out.println("ClosureSet is: ");
		for (Production production : closureSet) {
			production.print();
		}
    }
    
    private void partition() {
		for (Production production : closureSet) {
			int symbol = production.getDotSymbol();
			if (symbol == CTokenType.UNKNOWN_TOKEN.ordinal()) {
				continue;
			}

			ArrayList<Production> productionList = partition.get(symbol);
			if (productionList == null) {
				productionList = new ArrayList<Production>();
				partition.put(production.getDotSymbol(), productionList);
			}

			if (!productionList.contains(production)) {
				productionList.add(production);
			}
		}
    	
    	
    	
    //	printPartition();
    }
    
    private void printPartition() {
    	if (printInfo) {
    		return;
    	}
    	
    	for(Map.Entry<Integer, ArrayList<Production>> entry : partition.entrySet()) {
    		
    		System.out.println("partition for symbol: " + CTokenType.getSymbolStr(entry.getKey()));
    		
    		ArrayList<Production> productionList = entry.getValue();
    		for (int i = 0; i < productionList.size(); i++) {
    			productionList.get(i).print();
    		}
    	}
    }
    
    private GrammarState makeNextGrammarState(int left) {
    	ArrayList<Production> productionList = partition.get(left);
    	ArrayList<Production> newStateProductionList = new ArrayList<>();

		for (Production production : productionList) {
			newStateProductionList.add(production.dotForward());
		}
    	
    	return  stateManager.getGrammarState(newStateProductionList);
    }
    
    private void makeTransition() {
    	
    	for (Map.Entry<Integer, ArrayList<Production>> entry : partition.entrySet()) {
    //		System.out.println("\n====begin print transition info ===");
    		GrammarState nextState = makeNextGrammarState(entry.getKey());
    		transition.put(entry.getKey(), nextState);
    	//	System.out.println("from state " + stateNum + " to State " + nextState.stateNum + " on " + 
    	//	CTokenType.getSymbolStr(entry.getKey()));
    		//System.out.println("----State " + nextState.stateNum + "------");
    	//	nextState.print();
    		
    		stateManager.addTransition(this, nextState, entry.getKey());
    	}
    	
    	extendFollowingTransition();
    }
    
    private void extendFollowingTransition() {
    	for (Map.Entry<Integer, GrammarState> entry : transition.entrySet()) {
    		GrammarState state = entry.getValue();
    		if (!state.isTransitionMade()) {
    			state.createTransition();
    		}
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	return checkProductionEqual(obj, false);
    }
    
    public boolean checkProductionEqual(Object obj, boolean isPartial) {
    	GrammarState state = (GrammarState)obj;
    	 
    	if (state.productions.size() != this.productions.size()) {
    		return false;
    	}
    	
    	int equalCount = 0;
    	
    	for (int i = 0; i < state.productions.size(); i++) {
             for (int j = 0; j < this.productions.size(); j++) {
            	 if (!isPartial) {
               	  if (state.productions.get(i).equals(this.productions.get(j))) {
         				equalCount++;
         				break;
         			 }
                 }
                 else {
               	    if (state.productions.get(i).productionEequals(this.productions.get(j))) {
               	    	equalCount++;
               	    	break;
               	    }
                 }
             }
    			
    		}
    	
    		
    	return equalCount == state.productions.size();
    }
    
    public HashMap<Integer, Integer> makeReduce() {
    	HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    	reduce(map, this.productions);
    	reduce(map, this.mergedProduction);
    	
    	return map;
    }
    
    private void  reduce(HashMap<Integer, Integer> map, ArrayList<Production> productions) {
		for (Production production : productions) {
			if (production.canBeReduce()) {
				ArrayList<Integer> lookAhead = production.getLookAheadSet();
				for (Integer integer : lookAhead) {
					map.put(integer, (production.getProductionNum()));
				}
			}
		}
    }
    
    
}
