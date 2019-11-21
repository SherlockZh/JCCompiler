package ThompsonConstruction;

import java.util.HashSet;
import java.util.Set;

public class DFA {

    private static int STATE_NUM = 0;

    int stateNum = 0;

    Set<NFA> nfaStates = new HashSet<>();
    boolean accepted = false;

    public static DFA getDfaFromNfaSet(Set<NFA> input){
        DFA dfa = new DFA();

        for(NFA nfa : input){
            dfa.nfaStates.add(nfa);
            if(nfa.next == null && nfa.next2 == null)
                dfa.accepted = true;
        }
        dfa.stateNum = STATE_NUM;
        STATE_NUM++;
        return dfa;
    }

    public boolean hasNfaStates(Set<NFA> set){
        return this.nfaStates.equals(set);
    }
}
