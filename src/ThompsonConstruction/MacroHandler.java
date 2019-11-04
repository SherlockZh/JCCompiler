package ThompsonConstruction;

import InputSystem.Input;

import java.util.HashMap;
import java.util.Map;

public class MacroHandler {

    private HashMap<String, String> macroMap = new HashMap<>();
    private Input inputSystem;

    public MacroHandler(Input inputSystem){
        this.inputSystem = inputSystem;
        while (inputSystem.lookahead(1) != Input.EOF){
            newMacro();
        }
    }

    private void newMacro() {
        while (Character.isSpaceChar(inputSystem.lookahead(1)) || inputSystem.lookahead(1) == '\n'){
            inputSystem.advance();
        }

        StringBuilder macroName = new StringBuilder();
        char c = (char)inputSystem.lookahead(1);
        while (!Character.isSpaceChar(c) && c != '\n'){
            macroName.append(c);
            inputSystem.advance();
            c = (char)inputSystem.lookahead(1);
        }

        while (Character.isSpaceChar(inputSystem.lookahead(1))){
            inputSystem.advance();
        }

        c = (char)inputSystem.lookahead(1);
        StringBuilder macroContent = new StringBuilder();
        while (!Character.isSpaceChar(c) && c != '\n'){
            macroContent.append(c);
            inputSystem.advance();
            c = (char)inputSystem.lookahead(1);
        }

        inputSystem.advance();

        macroMap.put(macroName.toString(), macroContent.toString());
    }

    public String expandMacro(String macroName) throws Exception {
        if(!macroMap.containsKey(macroName)){
            ErrorHandler.parseError(ErrorHandler.Error.E_NO_MACRO);
        }else {
            return "(" + macroMap.get(macroName) + ")";
        }

        return "ERROR";
    }

    public void printMacros(){
        if (macroMap.isEmpty()) {
            System.out.println("There are no macros");
        }
        else {
            for (Map.Entry<String, String> entry : macroMap.entrySet()) {
                System.out.println("Macro name: " + entry.getKey() + " Macro content: " + entry.getValue());
            }
        }
    }
}

























