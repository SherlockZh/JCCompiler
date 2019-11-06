package ThompsonConstruction;

import InputSystem.Input;

import java.util.ArrayList;
import java.util.List;

public class RegularExpressionHandler {
    private Input input;
    private MacroHandler macroHandler;
    private List<String> REArray = new ArrayList<>();
//    private boolean isQuoted = false;


    public RegularExpressionHandler(Input input, MacroHandler macroHandler) throws Exception {
        this.input = input;
        this.macroHandler = macroHandler;
        
        processRE();
    }
    
    public int getRECount(){
        return REArray.size();
    }
    
    public String getRE(int index){
        if(index < 0 || index >= REArray.size())
            return null;
        return REArray.get(index);
    }
    
    private void processRE() throws Exception {
        while (input.lookahead(1) != Input.EOF){
            preProcessRE();
        }
    }

    /*
     * 对正则表达式进行预处理，将表达式中的宏进行替换，例如
     * D*\.D 预处理后输出
     * [0-9]*\.[0-9]
     * 注意，宏是可以间套的，所以宏替换时要注意处理间套的情形
     */
    private void preProcessRE() throws Exception {
        while (Character.isSpaceChar(input.lookahead(1)) || input.lookahead(1) == '\n') {
            input.advance();
        }

        StringBuilder RE = new StringBuilder();
        char c = (char)input.advance();
        boolean isQuoted = false;
        while (!Character.isSpaceChar(c) && c != '\n'){
            if(c == '"'){
                isQuoted = !isQuoted;
            }
            if(!isQuoted && c == '{'){
                String name = extractMacroNameFromInput();
                RE.append(expandMacro(name));
            }else {
                RE.append(c);
            }
            c = (char)input.advance();
        }

        REArray.add(RE.toString());
    }

    private String expandMacro(String macroName) throws Exception {
        String macroContent = macroHandler.expandMacro(macroName);
        int begin = macroContent.indexOf('{');
        while (begin != -1){
            int end = macroContent.indexOf('}', begin);
            if(end == -1){
                ErrorHandler.parseError(ErrorHandler.Error.E_BAD_MACRO);
                return null;
            }

            boolean quoted = checkInQuoted(macroContent, begin, end);

            if (!quoted){
                macroName = macroContent.substring(begin + 1, end);
                macroContent = macroContent.substring(0, begin) +
                        macroHandler.expandMacro(macroName) +
                        macroContent.substring(end + 1);
                begin = macroContent.indexOf('{');
            }else {
                begin = macroContent.indexOf('{', end);
            }
        }
        return macroContent;
    }

    private boolean checkInQuoted(String macroContent, int begin, int end) throws Exception {
        boolean isquoted = false;
        int quoteBegin = macroContent.indexOf('"');
        int quoteEnd;

        while (quoteBegin != -1){
            quoteEnd = macroContent.indexOf('"', quoteBegin + 1);
            if(quoteEnd == -1){
                ErrorHandler.parseError(ErrorHandler.Error.E_BAD_MACRO);
            }
            if(quoteBegin < begin && quoteEnd > end){
                isquoted = true;
            }else if(quoteBegin < begin && quoteEnd < end){
                ErrorHandler.parseError(ErrorHandler.Error.E_BAD_MACRO);
            }else if(quoteBegin > begin && quoteEnd < end){
                ErrorHandler.parseError(ErrorHandler.Error.E_BAD_MACRO);
            }
            quoteBegin = macroContent.indexOf('"', quoteEnd + 1);
        }

        return isquoted;
    }

    private String extractMacroNameFromInput() throws Exception{
        StringBuilder name = new StringBuilder();
        char c = (char)input.advance();
        while (c != '}' && c != '\n'){
            name.append(c);
            c = (char)input.advance();
        }
        if(c == '}')
            return name.toString();
        else {
            ErrorHandler.parseError(ErrorHandler.Error.E_BAD_MACRO);
            return null;
        }
    }

    public int getRegularExpressionCount() {
        return REArray.size();
    }

    public String getRegularExpression(int index) {
        if(index < 0 || index >= REArray.size())
            return null;
        return REArray.get(index);
    }
}




















