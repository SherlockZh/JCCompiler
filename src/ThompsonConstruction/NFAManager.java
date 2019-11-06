package ThompsonConstruction;

import java.util.ArrayDeque;
import java.util.Deque;

public class NFAManager {
    private final int NFA_MAX = 256;
    private NFA[] nfaStatesArr = null;
    private Deque<NFA> nfaStack = null;
    private int nextAlloc = 0;
    private int nfaStates = 0;

    public NFAManager() throws Exception {
        nfaStatesArr = new NFA[NFA_MAX];
        for(int i = 0; i < NFA_MAX; i++){
            nfaStatesArr[i] = new NFA();
        }
        nfaStack = new ArrayDeque<>();

        if(nfaStatesArr == null || nfaStack == null){
            ErrorHandler.parseError(ErrorHandler.Error.E_MEMORY);
        }
    }

    public NFA newNFA() throws Exception {
        if(++nfaStates >= NFA_MAX){
            ErrorHandler.parseError(ErrorHandler.Error.E_LENGTH);
        }

        NFA nfa = null;
        if(!nfaStack.isEmpty()){
            nfa = nfaStack.pop();
        }else{
            nfa = nfaStatesArr[nextAlloc];
            nextAlloc++;
        }

        nfa.clearState();
        nfa.setStateNum(nfaStates);
        nfa.setEdge(NFA.EPSILON);

        return nfa;
    }

    public void discardNFA(NFA nfa) {
        --nfaStates;
        nfa.clearState();
        nfaStack.push(nfa);
    }
}























