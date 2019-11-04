package InputSystem;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Input {
	public static final int EOF = 0; //输入流中没有可以读取的信息
	private final int   MAXLOOK = 16; //look ahead 最多字符数
	private final int   MAXLEX = 1024; //分词后字符串的最大长度
	private final int   BUFSIZE =  (MAXLEX * 3 ) + (2 * MAXLOOK); //缓冲区大小
	private int         END_BUF = BUFSIZE; //缓冲区的逻辑结束地址
	private final int   DANGER = (END_BUF - MAXLOOK);
	private final int   END = BUFSIZE;
	private final byte[]START_BUF = new byte[BUFSIZE]; //缓冲区
	private int         Next = END; //指向当前要读入的字符位置
	private int         startMark = END; //当前被词法分析器分析的字符串位置
	private int         endMark = END; //当前被词法分析器分析的字符串结束位置
	private int         preMark = END; //上一个被词法分析器分析的字符串起始位置
	private int         preLineNum = 0; //上一个被词法分析器分析的字符串所在的行号
	private int         preLength = 0; //上一个被词法分析器分析的字符串长度
	
	private FileHandler fileHandler = null;
	
	private int         LineNum = 1;  //当前被词法分析器分析的字符串的行号
	
	private int         Mline  = 1; 

	
	
	private boolean Eof_read = false; //输入流中是否还有可读信息
	private boolean noMoreChars() {
		/*
		 * 缓冲区中是否还有可读的字符
		 */
		return (Eof_read && Next >= END_BUF);
	}
	

	private FileHandler getFileHandler(String fileName) {
		if (fileName != null) {
			return new DiskFileHandler(fileName);
		}
		else {
			return new StdInHandler();
		}
	}
	
	public void newFile(String fileName) {
		
		if (fileHandler != null) {
			fileHandler.Close();
		}
		
		fileHandler = getFileHandler(fileName);
		fileHandler.Open();
		
		Eof_read  = false;
		Next      = END;
		preMark   = END;
		startMark = END;
		endMark   = END;
		END_BUF   = END;
		LineNum   = 1;
		Mline     = 1;
	}
	
	public String preText() {
		byte[] str = Arrays.copyOfRange(START_BUF, startMark, startMark + length());
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int length() {
		return endMark - startMark;
	}
	
	public int getLineNum() {
		return LineNum;
	}
	
	public String text() {
		byte[] str = Arrays.copyOfRange(START_BUF, preMark, preMark + preLength);
		return new String(str, StandardCharsets.UTF_8);
	}
	
	public int getPreLength() {
		return preLength;
	}
	
	public int getPreLineNum() {
		return preLineNum;
	}
	
	public int mark_start() {
		Mline = LineNum;
		endMark = startMark = Next;
		return startMark;
	}
	
	public int mark_end() {
		Mline = LineNum;
		endMark = Next;
		return endMark;
	}
	
	public int move_start() {
		if (startMark >= endMark) {
			return -1;
		}
		else {
			startMark++;
			return startMark;
		}
	}
	
	public int to_mark() {
		LineNum = Mline;
		Next = endMark;
		return Next;
	}
	
	public int mark_prev() {
		/*
		 * 执行这个函数后，上一个被词法解析器解析的字符串将无法在缓冲区中找到
		 */
		preMark = startMark;
		preLineNum = LineNum;
		preLength = endMark - startMark;
		return preMark;
	}
	

	public byte advance() {
		/*
		 * advance() 是真正的获取输入函数，他将数据从输入流中读入缓冲区，并从缓冲区中返回要读取的字符
		 * 并将Next加一，从而指向下一个要读取的字符, 如果Next的位置距离缓冲区的逻辑末尾(END_BUF)不到
		 * MAXLOOK 时， 将会对缓冲区进行一次flush 操作
		 */
		
		if (noMoreChars()) {
			return 0;
		}
		
		if (!Eof_read && flush(false) < 0) {
			/*
			 * 从输入流读入数据到缓冲区时出错
			 */
			return -1;
		}
		
		if (START_BUF[Next] == '\n') {
			LineNum++;
		}
		
		return START_BUF[Next++];
	}
	
	public static int NO_MORE_CHARS_TO_READ = 0;
	public static int FLUSH_OK = 1;
	public static int FLUSH_FAIL = -1;
	
	
	private int flush(boolean force) {
		/*
		 * flush 缓冲区，如果Next 没有越过Danger的话，那就什么都不做
		 * 要不然像上一节所说的一样将数据进行平移，并从输入流中读入数据，写入平移后
		 * 所产生的空间
		 *                            preMark                     DANGER
		 *                              |                          |
		 *     START_BUF              startMark         endMark          | Next  END_BUF
		 *         |                    | |           |            |  |      |
		 *         V                    V V           V            V  V      V
		 *         +---------------------------------------------------------+---------+
		 *         | 已经读取的区域        |          未读取的区域                 | 浪费的区域|
		 *         +--------------------------------------------------------------------
		 *         |<---shift_amt------>|<-----------copy_amt--------------->|
		 *         |<-------------------------BUFSIZE---------------------------------->|
		 * 
		 *  未读取区域的左边界是pMark或sMark(两者较小的那个),把 未读取区域平移到最左边覆盖已经读取区域，返回1
		 *  如果flush操作成功，-1如果操作失败，0 如果输入流中已经没有可以读取的多余字符。如果force 为 true
		 *  那么不管Next有没有越过Danger,都会引发Flush操作
		 */
		
		int copy_amt, shift_amt, left_edge;
		if (noMoreChars()) {
			return NO_MORE_CHARS_TO_READ;
		}
		
		if (Eof_read) {
			//输入流已经没有多余信息了
			return FLUSH_OK;
		}
		
		if (Next > DANGER || force) {
			left_edge = Math.min(preMark, startMark);
			shift_amt = left_edge;
			if (shift_amt < MAXLEX) {
				if (!force) {
					return FLUSH_FAIL;
				}
				
				left_edge = mark_start();
				mark_prev();
				shift_amt = left_edge;
			}
			
			copy_amt = END_BUF - left_edge;
			System.arraycopy(START_BUF, 0, START_BUF, left_edge, copy_amt);
			
			if (fillBuffer(copy_amt) == 0) {
				System.err.println("Internal Error, flush: Buffer full, can't read");
			}
			
			if (preMark != 0) {
				preMark -= shift_amt;
			}
			
			startMark -= shift_amt;
			endMark -= shift_amt;
			Next  -= shift_amt;
		}
		
		return FLUSH_OK;
	}
	
	private int fillBuffer(int starting_at) {
		/*
		 * 从输入流中读取信息，填充缓冲区平移后的可用空间，可用空间的长度是从starting_at一直到End_buf
		 * 每次从输入流中读取的数据长度是MAXLEX写整数倍
		 * 
		 */
		
		int need; //需要从输入流中读入的数据长度
		int got = 0; //实际上从输入流中读到的数据长度
		need = ((END - starting_at) / MAXLEX) * MAXLEX;
		if (need < 0) {
			System.err.println("Internal Error (fillbuffer): Bad read-request starting addr.");
		}
		
		if (need == 0) {
			return 0;
		}
		
		if ((got = fileHandler.Read(START_BUF, starting_at, need)) == -1) {
			System.err.println("Can't read input file");
		}
		
		END_BUF = starting_at + got;
		if (got < need) {
			//输入流已经到末尾
			Eof_read = true;
		}
		
		return got;
	}

	public boolean pushback(int n) {
		/*
		 * 把预读取的若干个字符退回缓冲区
		 */
		while (--n >= 0 && Next > startMark) {
			if (START_BUF[--Next] == '\n' || START_BUF[Next] == '\0') {
				--LineNum;
			}
		}
		
		if (Next < endMark) {
			endMark = Next;
			Mline = LineNum;
		}
		
		return (Next > startMark);
	}
	
	public byte lookahead(int n) {
		/*
		 * 预读取若干个字符
		 */
		byte p = START_BUF[Next + n - 1];
		if (Eof_read && Next + n - 1 >= END_BUF) {
			return EOF;
		}
		
		return (Next + n - 1 < 0 || Next + n - 1 >= END_BUF) ? 0 : p;
	}
}
