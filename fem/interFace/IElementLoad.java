package fem.interFace;

import math.linalg.Vector;
import util.model.INamedObject;

/**
 * Interface which contains specific methods that are required by all element loads.
 * 
 *
 */
public interface IElementLoad extends INamedObject{

	/**
	 * Gets the name of the element to which this load belongs.
	 * @return
	 * element name
	 */
	public String getElementName();	
	
	/**
	 * Gets the intensities of the load at each node and returns the intensities in a Vector.
	 * 
	 * @return
	 * nodal intensities
	 */
	public Vector getNodalIntensities();	
	
	/**
	 * Gets the element to which this load belongs
	 * 
	 * @return
	 * element
	 */
	public IElement element();
	
}
