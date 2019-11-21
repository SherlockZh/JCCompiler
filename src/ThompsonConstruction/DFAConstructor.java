package ThompsonConstruction;

import java.util.*;

public class DFAConstructor {
    private NFAPair nfaMachine;
    private NFAInterpreter nfaInterpreter;
    private List<DFA> dfaList = new ArrayList<>();

    private static final int MAX_DFA_STATE_COUNT = 254;
    private static final int ASCII_COUNT = 128;
    private static final int STATE_FAILURE = -1;

    private int[][] dfaStateTransformTable = new int[MAX_DFA_STATE_COUNT][ASCII_COUNT + 1];

    public DFAConstructor(NFAPair pair, NFAInterpreter nfaInterpreter){
        this.nfaInterpreter = nfaInterpreter;
        this.nfaInterpreter.debug = false;
        this.nfaMachine = pair;
        initTransformTable();
    }

    private void initTransformTable() {
        for(int i = 0; i < MAX_DFA_STATE_COUNT; i++){
            for(int j = 0; j <= ASCII_COUNT; j++){
                dfaStateTransformTable[i][j] = STATE_FAILURE;
            }
        }
    }

    public int[][] convertNfa2Dfa() {
        Set<NFA> input = new HashSet<>();
        input.add(nfaMachine.startNode);
        Set<NFA> nfaStartClosure = nfaInterpreter.e_closure(input);
        DFA start = DFA.getDfaFromNfaSet(nfaStartClosure);
        dfaList.add(start);

        System.out.println("Create DFA start node: ");
        printDfa(start);

        int nextState = STATE_FAILURE, currentDfaIndex = 0;

        while (currentDfaIndex < dfaList.size()){
            DFA currentDfa = dfaList.get(currentDfaIndex);

            //debug start
            boolean debug = false;
            if(currentDfa.stateNum == 5 || currentDfa.stateNum == 7){
                debug = true;
            }
            //debug end

            for(char c = 0; c <= ASCII_COUNT; c++){
                Set<NFA> movedSet = nfaInterpreter.move(currentDfa.nfaStates, c);

                if(movedSet.isEmpty()){
                    nextState = STATE_FAILURE;
                }else {
                    Set<NFA> closure = nfaInterpreter.e_closure(movedSet);
                    DFA dfa = isNfaStatesExistInDfa(closure);

                    if(dfa == null){
                        System.out.println("Create DFA node: ");
                        DFA newDfa = DFA.getDfaFromNfaSet(closure);
                        printDfa(newDfa);

                        dfaList.add(newDfa);
                        nextState = newDfa.stateNum;
                    }else {
                        System.out.println("Get a existed dfa node: ");
                        printDfa(dfa);
                        nextState = dfa.stateNum;
                    }
                }
                if(nextState != STATE_FAILURE){
                    System.out.println("DFA from state: " + currentDfa.stateNum + " to state: " + nextState + " on char: " + c);
                }
                dfaStateTransformTable[currentDfa.stateNum][c] = nextState;
            }
            System.out.println();
            currentDfaIndex++;
        }
        return dfaStateTransformTable;
    }

    private DFA isNfaStatesExistInDfa(Set<NFA> closure) {
        for(DFA dfa : dfaList){
            if(dfa.hasNfaStates(closure))
                return dfa;
        }
        return null;
    }

    private void printDfa(DFA dfa){
        System.out.print("DFA state: " + dfa.stateNum + " its nfa states are: ");
        Iterator<NFA> it = dfa.nfaStates.iterator();
        while (it.hasNext()) {
            System.out.print(it.next().getStateNum());
            if (it.hasNext()) {
                System.out.print(",");
            }
        }
        System.out.println();
    }

    public void printDFA() {
        int dfaNum = dfaList.size();
        for(int i = 0; i < dfaNum; i++){
            for(int j = 0; j < dfaNum; j++){
                if(isOnNumberClass(i, j))
                    System.out.println("From state " + i + " to state " + j + "on D");
                if(isOnDot(i, j)){
                    System.out.println("From state " + i + " to state " + j + "on dot");
                }
            }
        }
    }

    private boolean isOnNumberClass(int from, int to) {
        for(char c = '0'; c <= '9'; c++){
            if(dfaStateTransformTable[from][c] != to)
                return false;
        }
        return true;
    }

    private boolean isOnDot(int from, int to) {
        return dfaStateTransformTable[from]['.'] == to;
    }
}





















