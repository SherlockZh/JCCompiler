package ThompsonConstruction;

import java.util.ArrayList;
import java.util.List;

public class DfaGroupManager {
    private List<DfaGroup> groupList = new ArrayList<>();

    public DfaGroupManager(){

    }

    public DfaGroup createNewGroup(){
        DfaGroup group = DfaGroup.createDfaGroup();
        groupList.add(group);
        return group;
    }

    public DfaGroup getContainingGroup(int dfaStateNum) {
        for(DfaGroup group : groupList){
            if(groupContainsDfa(group, dfaStateNum))
                return group;
        }
        return null;
    }

    private boolean groupContainsDfa(DfaGroup group, int dfaStateNum){
        for(int i = 0; i < group.size(); i++){
            if(group.get(i).stateNum == dfaStateNum)
                return true;
        }
        return false;
    }

    public int size() {
        return groupList.size();
    }

    public DfaGroup get(int n) {
        if (n < groupList.size()) {
            return groupList.get(n);
        }

        return null;
    }
}

























