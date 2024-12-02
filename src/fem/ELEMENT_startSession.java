package fem;


import java.io.IOException;
import util.gui.UserEnvironment;
import util.gui.SystemOutStream;


public class ELEMENT_startSession {
	
	public static void main(String[] args) throws IOException {
        
    	SystemOutStream outputStream = new SystemOutStream("Output", null);
		System.setOut(outputStream.getOutStream());
    	new UserEnvironment("ELEMENT", outputStream);
    }

}