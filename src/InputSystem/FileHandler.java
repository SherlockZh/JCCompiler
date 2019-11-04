package InputSystem;

public interface FileHandler {

	/*
	 * 提供接口用于从输入流中获取信息，输入对象可以是磁盘文件，也可以是控制台标准输入
	 */
	
	void Open();
	
	int Close();
	
	/*
	 * 返回实际读取的字符长度
	 */
	int Read(byte[] buf, int begin, int len);
	
}
