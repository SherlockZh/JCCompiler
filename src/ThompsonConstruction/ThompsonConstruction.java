package ThompsonConstruction;

import InputSystem.Input;

public class ThompsonConstruction {

    private Input input = new Input();
    private MacroHandler macroHandler = null;

    private void runMacroExample() {
        System.out.println("Please enter macro definition");

        renewInputBuffer();
        macroHandler = new MacroHandler(input);
        macroHandler.printMacros();
    }

    private void runMacroExpandExample() throws Exception {
        System.out.println("Enter regular expression");
        renewInputBuffer();

        RegularExpressionHandler regularExpr = new RegularExpressionHandler(input, macroHandler);
        System.out.println("regular expression after expanded: ");
        for(int i = 0; i < regularExpr.getRECount(); i++){
            System.out.println(regularExpr.getRE(i));
        }
    }

    private void renewInputBuffer() {
        input.newFile(null);
        input.advance();
        input.pushback(1);
    }

    public static void main(String[] args) throws Exception {
        ThompsonConstruction construction = new ThompsonConstruction();
        construction.runMacroExample();
        construction.runMacroExpandExample();
    }
}
