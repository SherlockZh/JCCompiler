package BottomUpParser;

import MyCompiler.SymbolDefine;

import java.util.ArrayList;

public class Production {
    private int dotPos = 0;
    private boolean printDot = false;
    private int left = 0;
    private ArrayList<Integer> right;

    public Production(int left, int dot, ArrayList<Integer> right){
        this.left = left;
        this.right = right;

        if(dot >= right.size())
            dot = right.size();
        this.dotPos = dot;
    }

    public Production dotForward(){
        return new Production(this.left, dotPos + 1, this.right);
    }

    public int getDotPos(){
        return dotPos;
    }

    public int getLeft() {
        return left;
    }

    public ArrayList<Integer> getRight() {
        return right;
    }

    public int getDotSymbol() {
        if(dotPos >= right.size())
            return SymbolDefine.UNKNOWN_SYMBOL;
        return right.get(dotPos);
    }

    @Override
    public boolean equals(Object obj) {
        Production production = (Production)obj;

        return this.left == production.getLeft()
                && this.right.equals(production.getRight())
                && this.dotPos == production.getDotPos();
    }

    public void print() {
        System.out.print(SymbolDefine.getSymbolStr(left) + " -> " );
        for (int i = 0; i < right.size(); i++) {
            if (i == dotPos) {
                printDot = true;
                System.out.print(".");
            }

            System.out.print(SymbolDefine.getSymbolStr(right.get(i)) + " ");
        }

        if (!printDot) {
            System.out.print(".");
        }

        System.out.print("\n");
    }
}























