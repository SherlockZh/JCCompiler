package BottomUpParser;

import MyCompiler.SymbolDefine;
import MyCompiler.Symbols;

import java.util.ArrayList;
import java.util.HashMap;

public class FirstSetBuilder {
    private HashMap<Integer, Symbols> symbolMap = new HashMap<>();
    private ArrayList<Symbols> symbolArray = new ArrayList<>();
    private boolean runFirstSetPass = true;

    int productionCount = 0;

    public FirstSetBuilder() {
        initProductions();
    }

    private void initProductions() {
        ArrayList<int[]> productions = new ArrayList<>();
        productions.add(new int[]{SymbolDefine.EXPR});
        Symbols stmt = new Symbols(SymbolDefine.STMT, false, productions);
        symbolMap.put(SymbolDefine.STMT, stmt);
        symbolArray.add(stmt);


        productions = new ArrayList<>();
        productions.add(new int[] {SymbolDefine.EXPR, SymbolDefine.PLUS, SymbolDefine.TERM});
        productions.add(new int[] {SymbolDefine.TERM});
        Symbols expr = new Symbols(SymbolDefine.EXPR, false, productions);
        symbolMap.put(SymbolDefine.EXPR, expr);
        symbolArray.add(expr);



        productions = new ArrayList<>();
        productions.add(new int[]{SymbolDefine.TERM, SymbolDefine.TIMES, SymbolDefine.FACTOR});
        productions.add(new int[]{SymbolDefine.FACTOR});
        Symbols term = new Symbols(SymbolDefine.TERM, false, productions);
        symbolMap.put(SymbolDefine.TERM, term);
        symbolArray.add(term);



        productions = new ArrayList<>();
        productions.add(new int[] {SymbolDefine.LP, SymbolDefine.EXPR, SymbolDefine.RP});
        productions.add(new int[] {SymbolDefine.NUM_OR_ID});
        Symbols factor = new Symbols(SymbolDefine.FACTOR, false, productions);
        symbolMap.put(SymbolDefine.FACTOR, factor);
        symbolArray.add(factor);

        Symbols plus = new Symbols(SymbolDefine.PLUS, false, null);
        symbolMap.put(SymbolDefine.PLUS, plus);
        symbolArray.add(plus);

        Symbols times = new Symbols(SymbolDefine.TIMES, false, null);
        symbolMap.put(SymbolDefine.TIMES, times);
        symbolArray.add(times);

        Symbols lp = new Symbols(SymbolDefine.LP, false, null);
        symbolMap.put(SymbolDefine.LP, lp);
        symbolArray.add(lp);

        Symbols rp = new Symbols(SymbolDefine.RP, false, null);
        symbolMap.put(SymbolDefine.RP, rp);
        symbolArray.add(rp);

        Symbols num_or_id = new Symbols(SymbolDefine.NUM_OR_ID, false, null);
        symbolMap.put(SymbolDefine.NUM_OR_ID, num_or_id);
        symbolArray.add(num_or_id);
    }

    public boolean isSymbolNullable(int sym) {
        Symbols symbol= symbolMap.get(sym);
        if (symbol == null) {
            return false;
        }

        return symbol.isNullable;
    }

    public void runFirstSets(){
        while (runFirstSetPass){
            runFirstSetPass = false;
            for(Symbols symbol : symbolArray){
                addSymbolFirstSet(symbol);
            }
        }
        printAllFirstSet();
        System.out.println("============");
    }

    private void addSymbolFirstSet(Symbols symbol) {
        if(isSymbolTerminals(symbol.value)){
            if(symbol.firstSet.contains(symbol.value)){
                symbol.firstSet.add(symbol.value);
            }
            return;
        }

        for (int i = 0; i < symbol.productions.size(); i++){
            int[] rightSize = symbol.productions.get(i);
            if(rightSize.length == 0)
                continue;

            if(isSymbolTerminals(rightSize[0]) && !symbol.firstSet.contains(rightSize[0])){
                runFirstSetPass = true;
                symbol.firstSet.add(rightSize[0]);
            }
            else if(!isSymbolTerminals(rightSize[0])){
                int pos = 0;
                Symbols curSymbol;
                do{
                    curSymbol = symbolMap.get(rightSize[pos]);
                    if(!symbol.firstSet.containsAll(curSymbol.firstSet)){
                        runFirstSetPass = true;

                        for(int j = 0; j < curSymbol.firstSet.size(); j++){
                            if(!symbol.firstSet.contains(curSymbol.firstSet.get(j))){
                                symbol.firstSet.add(curSymbol.firstSet.get(j));
                            }
                        }
                    }
                    pos++;
                }while (pos < rightSize.length && curSymbol.isNullable);
            }
        }
    }

    private boolean isSymbolTerminals(int symbol) {
        return symbol < 256;
    }

    public void printAllFirstSet() {
        for (Symbols sym : symbolArray) {
            printFirstSet(sym);
        }
    }

    private void printFirstSet(Symbols symbol) {
        StringBuilder sb = new StringBuilder(SymbolDefine.getSymbolStr(symbol.value));
        sb.append("{ ");
        for (int i = 0; i < symbol.firstSet.size(); i++) {
            sb.append(SymbolDefine.getSymbolStr(symbol.firstSet.get(i))).append(" ");
        }
        sb.append(" }");

        System.out.println(sb.toString());
        System.out.println("============");
    }

    public ArrayList<Integer> getFirstSet(int symbol) {
        for (Symbols sym : symbolArray) {
            if (sym.value == symbol) {
                return sym.firstSet;
            }
        }
        return null;
    }
}






















