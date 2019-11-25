package ThompsonConstruction;

import InputSystem.Input;

public class ThompsonConstruction {
    private Input input = new Input();
    private MacroHandler macroHandler = null;
    private RegularExpressionHandler regularExpr = null;
    private Lexer lexer = null;

    private NFAPrinter nfaPrinter = new NFAPrinter();

    NFAPair pair = new NFAPair();

    NFAInterpreter nfaInterpreter = null;

    DFAConstructor dfaConstructor = null;

    MinimizeDFA miniDfa;

    private void runMacroExample() {
        System.out.println("Please enter macro definition");

        renewInputBuffer();
        macroHandler = new MacroHandler(input);
        macroHandler.printMacros();
    }

    private void runMacroExpandExample() throws Exception {
        System.out.println("Enter regular expression");
        renewInputBuffer();

        regularExpr = new RegularExpressionHandler(input, macroHandler);
        System.out.println("regular expression after expanded: ");
        for(int i = 0; i < regularExpr.getRECount(); i++){
            System.out.println(regularExpr.getRE(i));
        }
    }

    private void runLexerExample(){
        lexer = new Lexer(regularExpr);
        int cnt = 0;
        System.out.println("当前正则解析的正则表达式: " + regularExpr.getRegularExpression(cnt));
        lexer.advance();

        while (!lexer.MatchToken(Lexer.Token.END_OF_INPUT)){
            if(lexer.MatchToken(Lexer.Token.EOS)){
                System.out.println("解析下一个正则表达式");
                cnt++;
                System.out.println("当前正在解析的正则表达式： " + regularExpr.getRegularExpression(cnt));
                lexer.advance();
            }
            else {
                printLexResult();
            }
        }
    }

    private void printLexResult(){
        while (!lexer.MatchToken(Lexer.Token.EOS)){
            System.out.println("当前识别的字符是：" + (char)lexer.getLexeme());

            if(!lexer.MatchToken(Lexer.Token.L)){
                System.out.println("当前字符具有特殊含义");
                printMetaCharMeaning(lexer);
            }
            else{
                System.out.println("当前字符是普通字符常量");
            }
            lexer.advance();
        }
    }

    private void printMetaCharMeaning(Lexer lexer) {
        String s = "";
        if (lexer.MatchToken(Lexer.Token.ANY)) {
            s = "当前字符是点通配符";
        }

        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            s = "当前字符是开头匹配符";
        }

        if (lexer.MatchToken(Lexer.Token.AT_EOL)) {
            s = "当前字符是末尾匹配符";
        }

        if (lexer.MatchToken(Lexer.Token.CCL_END)) {
            s = "当前字符是字符集类结尾括号";
        }

        if (lexer.MatchToken(Lexer.Token.CCL_START)) {
            s = "当前字符是字符集类的开始括号";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSE_CURLY)) {
            s = "当前字符是结尾大括号";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
            s = "当前字符是结尾圆括号";
        }

        if (lexer.MatchToken(Lexer.Token.DASH)) {
            s = "当前字符是横杆";
        }

        if (lexer.MatchToken(Lexer.Token.OPEN_CURLY)) {
            s = "当前字符是起始大括号";
        }

        if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
            s = "当前字符是起始圆括号";
        }

        if (lexer.MatchToken(Lexer.Token.OPTIONAL)) {
            s = "当前字符是单字符匹配符?";
        }

        if (lexer.MatchToken(Lexer.Token.OR)) {
            s = "当前字符是或操作符";
        }

        if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
            s = "当前字符是正闭包操作符";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSURE)) {
            s = "当前字符是闭包操作符";
        }

        System.out.println(s);
    }

    private void runNfaInterpretorExample() {
        nfaInterpreter = new NFAInterpreter(pair.startNode, input);
        nfaInterpreter.interpretNFA();
    }

    public void runDfaConstructorExample() {
        dfaConstructor = new DFAConstructor(pair, nfaInterpreter);
        dfaConstructor.convertNfa2Dfa();
        dfaConstructor.printDFA();
    }

    private void runNfaMachineConstructorExample() throws Exception {
        lexer = new Lexer(regularExpr);
        NFAMachineConstructor nfaMachineConstructor = new NFAMachineConstructor(lexer);

        //nfaMachineConstructor.constructNfaForSingleCharacter(pair);
        //nfaMachineConstructor.constructNfaForDot(pair);
        //nfaMachineConstructor.constructNFAForCharacterSetWithoutNegative(pair);
        //nfaMachineConstructor.constructNFAForCharacterSet(pair);
        //nfaMachineConstructor.term(pair);
        //nfaMachineConstructor.constructStarClosure(pair);
        //nfaMachineConstructor.constructPlusClosure(pair);
        //nfaMachineConstructor.constructOptionsClosure(pair);
        //nfaMachineConstructor.factor(pair);
        //nfaMachineConstructor.cat_expr(pair);
        nfaMachineConstructor.expr(pair);
        nfaPrinter.printNFA(pair.startNode);
    }

    private void renewInputBuffer() {
        input.newFile(null);
        input.advance();
        input.pushback(1);
    }

    private void runMinimizeDFAExample() {
        miniDfa = new MinimizeDFA(dfaConstructor);
        miniDfa.minimize();
    }

    public static void main(String[] args) throws Exception {
        ThompsonConstruction construction = new ThompsonConstruction();
        construction.runMacroExample();
        construction.runMacroExpandExample();
        construction.runLexerExample();
        
        construction.runNfaMachineConstructorExample();

        construction.runNfaInterpretorExample();

        construction.runDfaConstructorExample();

        construction.runMinimizeDFAExample();
    }


}
/*
D   [0-9]
end

{D}*\.{D}|{D}\.{D}*
end

1.2
end
 */