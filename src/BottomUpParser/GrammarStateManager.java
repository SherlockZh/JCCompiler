package BottomUpParser;

import MyCompiler.SymbolDefine;

import java.util.ArrayList;
import java.util.List;

public class GrammarStateManager {
    private ArrayList<GrammarState> stateList = new ArrayList<>();
    private static GrammarStateManager self;

    private GrammarStateManager() {

    }

    public static GrammarStateManager getGrammarManager(){
        if(self == null)
            self = new GrammarStateManager();
        return self;
    }

    public void buildTransitionStateMachine() {
        ProductionManager productionManager = ProductionManager.getProductionManager();
        GrammarState state = getGrammarState(productionManager.getProduction(SymbolDefine.STMT));
        state.createTransition();
    }

    public GrammarState getGrammarState(List<Production> productionList) {
        GrammarState state = new GrammarState(productionList);

        if(!stateList.contains(state)){
            stateList.add(state);
            GrammarState.increaseStateNum();
            return state;
        }

        for (GrammarState grammarState : stateList) {
            if (grammarState.equals(state)) {
                state = grammarState;
            }
        }

        return state;
    }


}























