package ThompsonConstruction;

import java.util.Set;

public class NFAPrinter {
    private static final int ASCII_NUM = 128;
    private boolean start = true;

    private void printCCL(Set<Byte> set){
        System.out.print("[ ");
        for(int i = 0; i < ASCII_NUM; i++){
            if(set.contains((byte)i)){
                if(i < ' ')
                    System.out.print("^" + (char)(i + '@'));
                else
                    System.out.print((char)i);
            }
        }
        System.out.print(" ]");
    }

    public void printNFA(NFA startNFA){
        if(startNFA == null || startNFA.isVisited())
            return;
        if(start)
            System.out.println("--------NFA--------");
        startNFA.setVisited();
        printNfaNode(startNFA);

        if (start) {
            System.out.print("  (START STATE)");
            start = false;
        }

        System.out.print("\n");

        printNFA(startNFA.next);
        printNFA(startNFA.next2);
    }

    private void printNfaNode(NFA node) {
        if (node.next == null) {
            System.out.print("TERMINAL");
        }
        else {
            System.out.print("NFA state: " + node.getStateNum());
            System.out.print("--> " + node.next.getStateNum());
            if (node.next2 != null) {
                System.out.print(" " + node.next2.getStateNum() );
            }

            System.out.print(" on:");
            switch (node.getEdge()) {
                case NFA.CCL:
                    printCCL(node.inputSet);
                    break;
                case NFA.EPSILON:
                    System.out.print("EPSILON ");
                    break;
                default:
                    System.out.print((char)node.getEdge());
                    break;
            }
        }
    }
}

























