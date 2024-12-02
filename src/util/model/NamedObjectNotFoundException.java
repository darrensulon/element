package util.model;

public class NamedObjectNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String m_calling_object;
	private String m_wanted_object;
	
	public NamedObjectNotFoundException(String wantedObject){
		this.m_wanted_object = wantedObject;
	}
	
	public NamedObjectNotFoundException setCallingObject(INamedObject nobj){
		m_calling_object = "'"+nobj.getName()+"'";
		return this;
	}
	
	public String toString() {
		if(m_calling_object == null)
			return "NamedObject not found : '"+m_wanted_object+"'...";
		return "NamedObject not found : '"+m_wanted_object+"' called from "+ m_calling_object+"...";
	}

}
