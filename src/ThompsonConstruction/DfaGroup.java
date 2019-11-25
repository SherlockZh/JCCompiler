package ThompsonConstruction;

import java.util.*;

public class DfaGroup {
    private static int GROUP_COUNT = 0;

    private int group_num = 0;

    List<DFA> dfaGroups = new ArrayList<>();
    List<DFA> tobeRemove = new ArrayList<>();

    private DfaGroup() {
        group_num = GROUP_COUNT;
    }

    public static DfaGroup createDfaGroup() {
        DfaGroup group = new DfaGroup();
        GROUP_COUNT++;
        return group;
    }

    public void add(DFA dfa){
        dfaGroups.add(dfa);
    }

    public void tobeRemove(DFA dfa) {
        tobeRemove.add(dfa);
    }

    public void commitRemove() {
        Iterator it = tobeRemove.iterator();
        while (it.hasNext()) {
            dfaGroups.remove(it.next());
        }

        tobeRemove.clear();
    }

    public boolean contains(DFA dfa) {
        return dfaGroups.contains(dfa);
    }

    public int size() {
        return dfaGroups.size();
    }

    public DFA get(int n) {
        if (n < dfaGroups.size()) {
            return dfaGroups.get(n);
        }

        return null;
    }

    public int groupNum() {
        return group_num;
    }

    public void printGroup() {
        /*
         * 排序是为了调试演示方便，可以去掉，不影响逻辑
         */
        Collections.sort(dfaGroups, (Comparator) (o1, o2) -> {
            DFA dfa1 = (DFA)o1;
            DFA dfa2 = (DFA)o2;

            if (dfa1.stateNum > dfa2.stateNum) {
                return 1;
            }

            return 0;
        });

        System.out.println("Dfa Group num: " + group_num + " it has dfa: ");
        for (DFA dfa : dfaGroups) {
            System.out.print(dfa.stateNum + " ");
        }

        System.out.print("\n");
    }
}

















