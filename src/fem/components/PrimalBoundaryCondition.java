package fem.components;

import util.model.NamedModel;
import util.model.NamedObject;
import util.model.NamedObjectNotFoundException;
import fem.interFace.INode;
import fem.interFace.IPrimalBoundaryCondition;

/**
 * This class is for prescribing a boundary condition in the system primal vector. In stationary heat flow this is
 * analogous to the temperature condition at a node (i.e. if the temperature is prescribed or not). 
 * This class is abstract with super class IPrimalBoundaryCondition.
 * 
 * @author Darren Sulon
 *
 */
public class PrimalBoundaryCondition extends NamedObject implements IPrimalBoundaryCondition{

	private static final long serialVersionUID = 1L;

	/**
	 * Name of the node to which this boundary condition belongs.
	 */
	private String m_nodeName;
	
	/**
	 * The dual boundary intensity at the node.
	 */
	private double m_dual;
	
	private int dof;
	
	/**
	 * Temporary storage of the primal boundary condition intensity.
	 */
	private transient double _primal = Double.NaN; // used as temporary storage of primal value
	
	/**
	 * When instantiated the name of the boundary condition and the name of the node to which this boundary condition 
	 * belongs is provided.
	 * 
	 * @param name
	 * name of primal boundary condition
	 * 
	 * @param nodeName
	 * name of node at which boundary condition occurs
	 */
	public PrimalBoundaryCondition(String name, String nodeName){
		super(name);
		this.m_nodeName = nodeName;
	}
	
	/**
	 * When instantiated the name of the boundary condition, the name of the node to which this boundary condition 
	 * belongs and the primal boundary value is provided.
	 * 
	 * @param name
	 * name of primal boundary condition
	 * 
	 * @param nodeName
	 * name of node at which boundary condition occurs
	 * 
	 * @param dof
	 * degree of freedom
	 * 
	 * @param primal
	 * primal boundary condition value
	 */
	public PrimalBoundaryCondition(String name, String nodeName, int dof, double primal){
		this(name,nodeName);
		setPrimalValue(primal);
		this.dof = dof;
	}
	
    public void setModel(NamedModel model){
    	super.setModel(model);
    	if(!Double.isNaN(_primal)){
    		node().setPrimal(_primal, dof);
    		_primal = 0.0;
    	}
    }
	
	public INode node(){
		try{
			return (INode)getModel().get(m_nodeName);
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}
	
	
	public int getSystemIndex(){
		return node().getSystemIndex()[dof];
	}
	
	
	public void setPrimalValue(double value){
		if(getModel() != null)
			node().setPrimal(value, dof);
		else
			_primal = value;
	}
	
	public double getPrimalValue() {
		return node().getPrimal()[dof];
	}

	public void setDualValue(double reaction){
		this.m_dual = reaction;
	}
	
	public double getDualValue() {
		return m_dual;
	}
	
}
