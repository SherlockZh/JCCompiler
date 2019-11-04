package ThompsonConstruction;

public class ErrorHandler {
    public enum Error{
    	E_MEMORY, //内存不足
    	E_BAD_EXPR, //正则表达式错误
    	E_PAREN,  //括号不匹配
    	E_LENGTH, //要解析的正则表达式过多
    	E_BRACKET, //字符集类没有以 [ 开头
    	E_BOL, //^必须在表达式的开头
    	E_CLOSE, //* ? + 后面必须跟着表达式
    	E_NEW_LINE, //双引号中不能保护换行符
    	E_BAD_MACRO, //没有匹配的 }
    	E_NO_MACRO, //给定的宏表达式不存在
    	E_MACRO_TOO_DEEP //宏表达式的间套太深
    }
    
    private static String[] errMsgs = new String[]{
    	 "Not enough memory for NFA",
    	 "Malformed regular expression",
    	 "Missing close parenthesis",
    	 "Too many regular expression or expression too long",
    	 "Missing [ in character class",
    	 "^ must be at the start of expression or after [",
    	 "+ ? or * must follow an expression or subexpression",
    	 "Newline in quoted string, use \\n to get newline into expression",
    	 "Missing ) in macro expansion",
    	 "Macro doesn't exist",
    	 "Macro expansions nested too deeply"
    };
               
    public static void parseError(Error type) throws Exception {
    	throw new Exception(errMsgs[type.ordinal()]);
    }
}
