package InputSystem;

import java.nio.charset.StandardCharsets;


public class InputSystem {

	private Input input = new Input();

	public void runStdinExample() {
    	input.newFile(null); //控制台输入
    	
    	input.mark_start();
    	printWord();
    	input.mark_end();
    	input.mark_prev();
    	/*
    	 *   执行上面语句后，缓冲区及相关指针情况如下图
    	 *       sMark
    	 *         |
    	 *       pMark     eMark                   
		 *         |        |                                     
    	 *       Start_buf Next                                   Danger   End_buf
		 *         |        |                                        |       |  
		 *         V        V                                        V       V  
		 *         +---------------------------------------------------------+---------+
		 *         | typedef|          未读取的区域                     |       | 浪费的区域|
		 *         +--------------------------------------------------------------------
		 *         |<-------------------------BUFSIZE---------------------------------->|
    	 * 
    	 */

    	input.lookahead(2);
    	
    	input.mark_start();
    	printWord();
    	input.mark_end();
    	
    	/*
    	 *   执行上面语句后，缓冲区及相关指针情况如下图
    	 *                 sMark
    	 *                  |
    	 *       pMark      |   eMark                   
		 *         |        |    |                                     
    	 *       Start_buf  |   Next                               Danger   End_buf
		 *         |        |    |                                   |       |  
		 *         V        V    V                                   V       V  
		 *         +---------------------------------------------------------+---------+
		 *         | typedef|int|      未读取区域                      |       | 浪费的区域|
		 *         +--------------------------------------------------------------------
		 *         |<-------------------------BUFSIZE---------------------------------->|
    	 * 
    	 */
    	
    	System.out.println("prev word: " + input.preText()); //打印出typedef
    	System.out.println("current word: " + input.text()); //打印出int
		
	}
	
	 private void printWord() {
	    	byte c;
	    	while ((c = input.advance()) != ' ') {
	    		byte[] buf = new byte[1];
	    		buf[0] = c;
				String s = new String(buf, StandardCharsets.UTF_8);
				System.out.print(s);
			}
	    	System.out.println();
	    }
	
    public static void main(String[] args) {
    	InputSystem input = new InputSystem();
    	input.runStdinExample();
    }
}
