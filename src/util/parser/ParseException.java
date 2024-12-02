package util.parser;

public class ParseException extends Exception{

	private static final long serialVersionUID = 1L;

	private int m_lineNr;
	private String m_reason;
	private String m_line;
	
	public ParseException(int lineNr, String reason, String line){
		this.m_line = line;
		this.m_reason = reason;
		this.m_lineNr = lineNr;
	}
	
	public void printStackTrace() {
		System.err.println("ParseException: "+m_reason+"\n\t@LINE "+m_lineNr+": '"+m_line+"'\n");
	}
	
}
