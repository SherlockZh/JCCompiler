package ThompsonConstruction;

import InputSystem.Input;

import java.util.*;

public class NFAInterpretor {
    private NFA start;
    private Input input;

    public boolean debug = true;

    public NFAInterpretor(NFA start, Input input) {
        this.start = start;
        this.input = input;
    }

    public Set<NFA> e_closure(Set<NFA> input){

        if(debug)
            System.out.print("ε-Closure( " + strFromNFASet(input) + " ) = ");

        if(input == null || input.isEmpty()) return null;

        Deque<NFA> nfaStack = new ArrayDeque<>(input);

        while (!nfaStack.isEmpty()){
            NFA nfa = nfaStack.pop();


            //TODO: extract individual function
            if(nfa.next != null && nfa.getEdge() == NFA.EPSILON){
                if(!input.contains(nfa.next)){
                    nfaStack.push(nfa.next);
                    input.add(nfa.next);
                }
            }

            if(nfa.next2 != null && nfa.getEdge() == NFA.EPSILON){
                if(!input.contains(nfa.next2)){
                    nfaStack.push(nfa.next2);
                    input.add(nfa.next2);
                }
            }
        }
        if(debug)
            System.out.println("{ " + strFromNFASet(input) + " }");

        return input;
    }

    private String strFromNFASet(Set<NFA> input) {
        StringBuilder s = new StringBuilder();
        Iterator it = input.iterator();
        while (it.hasNext()) {
            s.append(((NFA)it.next()).getStateNum());
            if (it.hasNext()) {
                s.append(",");
            }
        }

        return s.toString();
    }


    public Set<NFA> move(Set<NFA> input, char c){
        Set<NFA> outSet = new HashSet<>();

        for (NFA nfa : input) {
            Set<Byte> s = nfa.inputSet;
            Byte cb = (byte) c;

            if (nfa.getEdge() == c || (nfa.getEdge() == NFA.CCL && nfa.inputSet.contains(cb))) {
                outSet.add(nfa.next);
            }
        }
        if(debug){
            System.out.print("move({ " + strFromNFASet(input) + " }");
            System.out.println("{ " + strFromNFASet(outSet) + " }");
        }
        return outSet;
    }

    public void interpretNFA(){
        System.out.println("Input string: ");
        input.newFile(null);
        input.advance();
        input.pushback(1);

        Set<NFA> next = new HashSet<>();
        next.add(start);
        e_closure(next);

        Set<NFA> current = null;
        char c;
        StringBuilder inputStr = new StringBuilder();
        boolean lastAccepted = false;

        while ((c = (char)input.advance()) != Input.EOF){
            current = move(next, c);
            next = e_closure(current);

            if(next != null){
                if(hasAcceptState(next)){
                    lastAccepted = true;
                }
            }else {
                break;
            }
            inputStr.append(c);
        }
        if(lastAccepted)
            System.out.println("The NFA Machine can recognize string: " + inputStr.toString());
    }

    private boolean hasAcceptState(Set<NFA> input) {
        if(input == null || input.isEmpty())
            return false;

        boolean isAccepted = false;
        StringBuilder acceptedStatement = new StringBuilder("Accept State: ");
        for(NFA nfa : input){
            if(nfa.next == null && nfa.next2 == null){
                isAccepted = true;
                acceptedStatement.append(nfa.getStateNum()).append(" ");
            }
        }
        if(isAccepted)
            System.out.println(acceptedStatement);

        return isAccepted;
    }
}





























