package util.model;

import java.io.BufferedReader;
import java.io.PrintStream;

public interface IStreamable extends INamedObject {	
   
	public void fromStream(BufferedReader br) throws Exception;
    
	public void toStream(PrintStream ps);

}
