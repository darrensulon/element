package fem.interFace;

import math.linalg.Vector;
import util.model.INamedObject;

/**
 * Interface which contains specific methods that are required by all primal boundary conditions.
 * 
 * @author Darren Sulon
 *
 */
public interface IPrimalBoundaryCondition extends INamedObject{

	/**
	 * Gets the system index of this boundary condition.
	 * 
	 * @return
	 * system index
	 */
	public int getSystemIndex();
	
	/**
	 * Gets the node at which this boundary condition occurs.
	 * 
	 * @return
	 * node
	 */
	public INode node();
	
	/**
	 * Sets the dual value for this boundary condition.
	 * 
	 * @param dual
	 * dual value
	 */
	public void setDualValue(double dual);
	
	/**
	 * Gets the dual value of this boundary condition.
	 * 
	 * @return
	 * dual value
	 */
	public double getDualValue();
	
	/**
	 * Sets the primal value for this boundary condition.
	 * 
	 * @param primal
	 * primal value
	 */
	public void setPrimalValue(double primal);
	
	/**
	 * Gets the primal value for this boundary condition.
	 * 
	 * @return
	 * primal value
	 */
	public double getPrimalValue();
	
}
