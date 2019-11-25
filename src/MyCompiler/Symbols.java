package MyCompiler;

import java.util.ArrayList;


public class Symbols {
    public  int value;
    public  ArrayList<int[]> productions;
    public  ArrayList<Integer> firstSet = new ArrayList<>();
    public  ArrayList<Integer> followSet = new ArrayList<>();
    public  ArrayList<ArrayList<Integer>> selectionSet = new ArrayList<>();
    
    public  boolean isNullable;
    
    public Symbols(int symVal, boolean nullable, ArrayList<int[]> productions) {
    	value = symVal;
    	this.productions =  productions; 
    	isNullable = nullable;
    	
    	if (symVal < 256) {
    		//terminal's first set is itself
    		firstSet.add(symVal);
    	}
    }
    
    
}
