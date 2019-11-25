package ThompsonConstruction;

import java.util.List;

public class MinimizeDFA {
    private DFAConstructor dfaConstructor;
    private DfaGroupManager groupManager = new DfaGroupManager();
    private static final int ASCII_NUM = 128;
    private int[][] dfaTransTable = null;
    private int[][] minDfa;
    private List<DFA> dfaList;
    private DfaGroup newGroup;

    private boolean addNewGroup = false;
    private static final int STATE_FAILURE = -1;


    public MinimizeDFA(DFAConstructor dfaConstructor) {
        this.dfaConstructor = dfaConstructor;
        dfaList = dfaConstructor.getDfaList();
        dfaTransTable = dfaConstructor.getDfaTransTable();
    }

    public int[][] minimize() {
        addNoAcceptingDfaToGroup();
        addAcceptingDfaToGroup();

        do{
            addNewGroup = false;
            doGroupSeparationOnNumber();
            doGroupSeparationOnCharacter();
        }while (addNewGroup);

        createMiniDfaTransTable();
        printMiniDfaTable();

        return minDfa;
    }

    private void doGroupSeparationOnCharacter() {
        for (int i = 0; i < groupManager.size(); i++) {
            int dfaCount = 1;
            newGroup = null;
            DfaGroup group = groupManager.get(i);

            System.out.println("Handle seperation on group: ");
            group.printGroup();

            DFA first = group.get(0);
            DFA next = group.get(dfaCount);
            //当一个分区包含不止一个节点时，next不会是空
            while (next != null) {
                for (char c = 0; c < ASCII_NUM; c++) {
                    if (!Character.isDigit(c) && doGroupSeparationOnInput(group, first, next, c)) {
                        addNewGroup = true;
                        break;
                    }
                }

                dfaCount++;
                next = group.get(dfaCount);
            }

            group.commitRemove();
        }
    }

    private void doGroupSeparationOnNumber() {
        for(int i = 0; i < groupManager.size(); i++){
            int dfaCount = 1;
            newGroup = null;
            DfaGroup group = groupManager.get(i);

            System.out.println("Handle separation on group: ");
            group.printGroup();

            DFA first = group.get(0);
            DFA next = group.get(dfaCount);

            while (next != null){
                for(char c = '0'; c < '0' + 9; c++){
                    if(doGroupSeparationOnInput(group, first, next, c)){
                        addNewGroup = true;
                        break;
                    }
                }
                dfaCount++;
                next = group.get(dfaCount);
            }
            group.commitRemove();
        }
    }

    private boolean doGroupSeparationOnInput(DfaGroup group, DFA first, DFA next, char c) {
        int goto_first = dfaTransTable[first.stateNum][c];
        int goto_next = dfaTransTable[next.stateNum][c];

        if(groupManager.getContainingGroup(goto_first) != groupManager.getContainingGroup(goto_next)){
            if(newGroup == null){
                newGroup = groupManager.createNewGroup();
            }
            group.tobeRemove(next);
            newGroup.add(next);

            System.out.println("Dfa:" + first.stateNum + " and Dfa:" + next.stateNum + " jump to different group on input char " + c);

            System.out.println("remove Dfa:" + next.stateNum + " from group:" + group.groupNum() + " and add it to group:" + newGroup.groupNum());

            return true;
        }
        return false;
    }

    private void addNoAcceptingDfaToGroup() {
        DfaGroup group = groupManager.createNewGroup();
        for (DFA dfa : dfaList){
            if(!dfa.accepted)
                group.add(dfa);
        }
        group.printGroup();
    }

    private void addAcceptingDfaToGroup(){
        DfaGroup group = groupManager.createNewGroup();
        for (DFA dfa : dfaList){
            if(dfa.accepted)
                group.add(dfa);
        }
        group.printGroup();

        /*
         * 把集合 3,4,6,7 排列成 4,6,7,3
         * 这是为了调试演示需要，删除完全不会影响程序逻辑
         * 阅读代码时忽略下面这几句
         */
        DFA dfa = group.dfaGroups.get(0);
        group.dfaGroups.remove(0);
        group.dfaGroups.add(dfa);
    }

    private void createMiniDfaTransTable() {

        initMiniDfaTransTable();
        for(DFA dfa : dfaList){
            int from = dfa.stateNum;
            for(int i = 0; i < ASCII_NUM; i++){
                if(dfaTransTable[from][i]  != STATE_FAILURE){
                    int to = dfaTransTable[from][i];
                    DfaGroup fromGroup = groupManager.getContainingGroup(from);
                    DfaGroup toGroup = groupManager.getContainingGroup(to);

                    minDfa[fromGroup.groupNum()][i] = toGroup.groupNum();
                }
            }
        }
    }

    private void initMiniDfaTransTable() {
        minDfa = new int[groupManager.size()][ASCII_NUM];
        for(int i = 0; i < groupManager.size(); i++){
            for(int j = 0; j < ASCII_NUM; j++){
                minDfa[i][j] = STATE_FAILURE;
            }
        }
    }

    private void printMiniDfaTable() {
        for (int i = 0; i < groupManager.size(); i++)
            for (int j = 0; j < groupManager.size(); j++) {
                if (isOnNumberClass(i,j)) {
                    System.out.println("from " + i + " to " + j + " on D");
                }
                if (isOnDot(i,j)) {
                    System.out.println("from " + i + " to " + j + " on .");
                }
            }
    }

    private boolean isOnNumberClass(int from, int to) {
        for (char c = '0'; c <= '9'; c++) {
            if (minDfa[from][c] != to) {
                return false;
            }
        }

        return true;
    }

    private boolean isOnDot(int from, int to) {
        return minDfa[from]['.'] == to;
    }
}



























