package fem.model;

/**
 * Class used to inform the controller of errors that have occurred during execution.
 * 
 *
 */
public class FemException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Reason for error during execution.
	 */
	private String m_reason;
	
	/**
	 * When instantiated the reason for the error's occurrence is provided as a String.
	 * @param reason
	 * reason for exception
	 */
	public FemException(String reason){
		this.m_reason = reason;
	}
	
	/**
	 * Prints the error to the System.err console.
	 */
	public void printStackTrace() {
		System.err.println("FemException: "+m_reason+"\n");
	}
	
}
