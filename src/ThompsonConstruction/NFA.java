package ThompsonConstruction;

import java.util.*;

public class NFA {

    public enum ANCHOR {
        NONE,
        START,
        END,
        BOTH
    }
    public static final int EPSILON = -1;
    public static final int CCL = -2;
    public static final int EMPTY = -3;
    private static final int ASCII_COUNT = 127;

    private int edge;

    public Set<Byte> inputSet;
    public NFA next;
    public NFA next2;
    private ANCHOR anchor;
    private int stateNum;
    private boolean visited = false;

    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public void setStateNum(int num) {
        stateNum = num;
    }
    public int getStateNum() {
        return stateNum;
    }
    public int getEdge() {
        return edge;
    }
    public void setEdge(int edge) {
        this.edge = edge;
    }
    public void setAnchor(ANCHOR anchor) {
        this.anchor = anchor;
    }
    public NFA.ANCHOR getAnchor() {
        return this.anchor;
    }

    public NFA() {
        inputSet = new HashSet<>();
        clearState();
    }

    public void addToSet(Byte b) {
        inputSet.add(b);
    }

    public void setComplement(){
        Set<Byte> newSet = new HashSet<>();

        for(byte b = 0; b < ASCII_COUNT; b++){
            if(!inputSet.contains(b))
                newSet.add(b);
        }
        inputSet = newSet;
    }

    public void clearState() {
        inputSet.clear();
        next = next2 = null;
        anchor = ANCHOR.NONE;
        stateNum = -1;
    }

    public void CloneNFA(NFA nfa){
        inputSet.clear();
        inputSet.addAll(nfa.inputSet);
        anchor = nfa.getAnchor();
        this.next = nfa.next;
        this.next2 = nfa.next2;
        this.edge = nfa.getEdge();
    }
}





















