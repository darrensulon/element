package fem.interFace;

import math.linalg.Vector;
import util.model.INamedObject;

/**
 * Interface which contains specific methods that are required by all dual boundary conditions.
 * 
 * @author Darren Sulon
 *
 */
public interface IDualBoundaryCondition extends INamedObject{

	/**
	 * returns the system index at which the boundary condition occurs as an array.
	 * 
	 * @return
	 * system index
	 */
	public int[] getSystemIndex();
	
	/**
	 * Gets the dual value and returns the value in a Vector.
	 * 
	 * @return
	 * the dual value as a vector
	 */
	public Vector getDualValue();
	
	/**
	 * Gets the degree of freedom to which the boundary condition belongs.
	 * 
	 * @return
	 * degree of freedom
	 */
	public int getDoF();
	
	/**
	 * Returns the node to which the primal boundary condition belongs.
	 * 
	 * @return
	 * node
	 */
	public INode node();
}
