package InputSystem;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class StdInHandler implements FileHandler{
	
	private StringBuilder inputBuffer = new StringBuilder();

	public void Open() {

	Scanner s = new Scanner(System.in);
		while (true) {
			String line = s.nextLine();
			if (line.equals("end")) {
				break;
			}
			inputBuffer.append(line).append('\n');
		}
	 // s.close();
	}
	
	public int Close() {
		return 0;
	}
	
	public int Read(byte[] buf, int begin, int len) {

		int curPos = 0;
		if (curPos >= inputBuffer.length()) {
		    return 0;
		}
		
		int readCnt = 0;
		byte[] inputBuf = inputBuffer.toString().getBytes(StandardCharsets.UTF_8);
		while (curPos + readCnt < inputBuffer.length() && readCnt < len) {
			buf[begin + readCnt] = inputBuf[curPos + readCnt];
			readCnt++;
		}

		return readCnt;
		
	}

}
