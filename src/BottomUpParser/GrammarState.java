package BottomUpParser;

import MyCompiler.Lexer;
import MyCompiler.SymbolDefine;

import java.util.*;


public class GrammarState {
    private static int stateNumCount = 0;
    private boolean printInfo = false;
    private boolean transitionDone = false;
    private int stateNum = -1;
    private GrammarStateManager stateManager = GrammarStateManager.getGrammarManager();
    private ArrayList<Production> productions;
    private HashMap<Integer, GrammarState> transition = new HashMap<>();
    private ArrayList<Production> closureSet = new ArrayList<>();
    private ProductionManager productionManager = ProductionManager.getProductionManager();
    private HashMap<Integer, ArrayList<Production>> partitionMap = new HashMap<>();

    public static void increaseStateNum(){
        stateNumCount++;
    }

    public boolean isTransitionMade() {
        return transitionDone;
    }

    public GrammarState(ArrayList<Production> productions) {
        this.stateNum = stateNumCount;
        this.productions = productions;
        this.closureSet.addAll(this.productions);
    }

    public void print() {
        System.out.println("State Number: " + stateNum);
        for (Production production : productions) {
            production.print();
        }
    }

    public void printTransition() {
        for (Map.Entry<Integer, GrammarState> entry: transition.entrySet()) {
            System.out.println("transfer on " + SymbolDefine.getSymbolStr(entry.getKey()) + " to state ");
            entry.getValue().print();
            System.out.print("\n");
        }
    }

    public void createTransition(){
        if(transitionDone)
            return;

        transitionDone = true;
        System.out.println("\n====make transition=====\n");
        print();

        makeClosure();
        partition();
        makeTransition();

        System.out.println();

        printInfo = true;
    }

    private void makeClosure(){
        Deque<Production> productionStack = new ArrayDeque<>();
        for (Production production : productions) {
            productionStack.push(production);
        }

        while (!productionStack.isEmpty()){
            Production production = productionStack.pop();
            int symbol = production.getDotSymbol();
            ArrayList<Production> closures = productionManager.getProduction(symbol);

            for(int i = 0; closures != null && i < closures.size(); i++){
                if(!closureSet.contains(closures.get(i))){
                    closureSet.add(closures.get(i));
                    productionStack.push(closures.get(i));
                }
            }
        }

        printClosure();
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

    private void partition(){
        for (Production production : closureSet) {
            int symbol = production.getDotSymbol();
            if (symbol == SymbolDefine.UNKNOWN_SYMBOL)
                continue;

            ArrayList<Production> productionList = partitionMap.get(symbol);
            if (productionList == null) {
                productionList = new ArrayList<>();
                partitionMap.put(production.getDotSymbol(), productionList);
            }

            if (!productionList.contains(production)) {
                productionList.add(production);
            }
        }
        printPartition();
    }

    private void printPartition() {
        if (printInfo) {
            return;
        }

        for(Map.Entry<Integer, ArrayList<Production>> entry : partitionMap.entrySet()) {

            System.out.println("partition for symbol: " + SymbolDefine.getSymbolStr(entry.getKey()));

            ArrayList<Production> productionList = entry.getValue();
            for (Production production : productionList) {
                production.print();
            }
        }
    }

    private GrammarState makeNextGrammarState(int left){
        List<Production> productionList = partitionMap.get(left);
        List<Production> newStateProductionList = new ArrayList<>();

        for (Production production : productionList) {
            newStateProductionList.add(production.dotForward());
        }

        return stateManager.getGrammarState(newStateProductionList);
    }

    private void makeTransition() {
        for (Map.Entry<Integer, ArrayList<Production>> entry : partitionMap.entrySet()) {
            System.out.println("\n====begin print transition info ===");
            GrammarState nextState = makeNextGrammarState(entry.getKey());
            transition.put(entry.getKey(), nextState);
            System.out.println("from state " + stateNum + " to State " + nextState.stateNum + " on " + SymbolDefine.getSymbolStr(entry.getKey()));
            System.out.println("----State " + nextState.stateNum + "------");
            nextState.print();
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
        GrammarState state = (GrammarState)obj;
        return state.productions.equals(this.productions);
    }
}





























