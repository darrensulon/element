package fem.interFace;

import math.linalg.Vector;
import util.model.INamedObject;

/**
 * Interface which contains specific methods that are required by all types of nodes.
 * 
 * @author Darren Sulon
 *
 */
public interface INode extends INamedObject{

	/**
	 * Gets the coordinates of the node.
	 * 
	 * @return
	 * coordinates
	 */
	public Vector getCoordinates();
	
	public void setNumberofDofs(int numberOfDoFs);

	/**
	 * Gets the system index of this node.
	 * 
	 * @return
	 * system index
	 */
	public int[] getSystemIndex();
	
	/**
	 * Sets the system index of this node
	 * 
	 * @param index
	 * local index
	 * @return
	 * system index
	 */
	public int setSystemIndex(int index);
		
	/**
	 * Sets the primal value of this node.
	 * 
	 * @param primal
	 * primal value
	 * @param dof
	 * degree of freedom
	 */
	public void setPrimal(double primal, int dof);
	
	public void clearPrimal();
	
	/**
	 * Sets the primal value of this node.
	 * 
	 * @return
	 * primal value
	 */
	public double[] getPrimal();
	
	/**
	 * Method determines if the node has a primal boundary condition.
	 * 
	 * @return
	 * true or false
	 */
	public boolean hasPrimalBoundaryCondition();
	
	/**
	 * Method sets the state primal boundary condition state of this node. Method sets true if node 
	 * has boundary condition and false if there is no boundary condition.
	 * 
	 * @param hasPrimalBoundaryCondition
	 * has a boundary condition
	 */
	public void setPrimalBoundaryCondition(boolean hasPrimalBoundaryCondition);
}
