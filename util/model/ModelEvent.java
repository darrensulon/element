package util.model;

public class ModelEvent {

	private INamedObject m_namedObj;
	private NamedModel m_model;
	
	public ModelEvent(INamedObject nobj, NamedModel model){
		this.m_namedObj = nobj;
		this.m_model = model;
	}
	
	public INamedObject getNamedObject(){
		return m_namedObj;
	}
	
	public NamedModel getModel(){
		return m_model;
	}

}
