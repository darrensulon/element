package fem.components;

import math.linalg.Vector;
import util.model.NamedObject;
import util.model.NamedObjectNotFoundException;
import fem.interFace.IElement;
import fem.interFace.IElementLoad;

/**
 * This class is abstract with super class IElementload.
 * Class contains generic get and set methods required for implementing a element load into a finite element.
 * 
 * @author Darren Sulon
 *
 */
public class ElementLoad extends NamedObject implements IElementLoad{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The name of the element to which this element load belongs.
	 */
	private String m_element_name;
	
	/**
	 * The values of the load at each node in the element.
	 */
	private Vector m_values;
	
	/**
	 * When instantiated the name of the element load, the name of the element to which this load belongs and the 
	 * values of the element load at each node is provided.
	 * 
	 * @param name
	 * name of the element load
	 * 
	 * @param elementName
	 * name of element to which this load belongs
	 * 
	 * @param values
	 * values of the element loads at its nodes
	 */
	public ElementLoad(String name, String elementName, double[] values){
		super(name);
		this.m_element_name = elementName;
		m_values = Vector.getVector(values.length, true);
		m_values.set(values);
	}
	
	public String getElementName() {
		try{
			return m_element_name;
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}

	public Vector getNodalIntensities() {
		return m_values.clone();
	}
	
	
	public IElement element(){
		return (IElement)getModel().get(m_element_name);
	}

}
