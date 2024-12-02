package fem.components;

import math.linalg.Vector;
import util.model.NamedObject;
import util.model.NamedObjectNotFoundException;
import fem.interFace.IDualBoundaryCondition;
import fem.interFace.INode;

/**
 * This class is used for creating dual boundary conditions to be implemented into the model.
 * Boundary conditions are specified as 'zero dimensional' which implies that the boundary condition
 * is specified only at a point (of zero dimension) on the boundary. Any dual boundary conditions specified on an
 * edge must first be converted to a dimensionless stream by hand calculations and then specified as 
 * a boundary condition in the model. For example, in stationary heat flow, heat flux on the edge of 
 * the body must be converted to a heat stream at several points on the body before it can be implemented at a
 * zero-dimensional boundary condition.
 *
 */
public class DualZeroDBoundaryCondition extends NamedObject implements IDualBoundaryCondition {

	private static final long serialVersionUID = 1L;
	private String m_nodeName;
	private double m_intensity;
	private int dof;
	
	/**
	 * When instantiated the name, node name, nodal degree of freedom and intensity of the stream is
	 * provided.
	 * 
	 * @param name
	 * name of the condition
	 * 
	 * @param nodeName
	 * name of the node
	 * 
	 * @param dof
	 * nodal degree of freedom
	 * 
	 * @param intensity
	 * intensity of the boundary condition
	 */
	public DualZeroDBoundaryCondition(String name, String nodeName, int dof, double intensity){
		super(name);
		this.m_nodeName = nodeName;
		this.m_intensity = intensity;
		this.dof = dof;
	}
	
	public Vector getDualValue() {
		Vector lv = Vector.getVector(1);
		lv.set(m_intensity, 0);
		return lv;
	}

	public INode node(){
		try{
			return (INode)getModel().get(m_nodeName);
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}
	
	public int[] getSystemIndex() {
		return new int[]{node().getSystemIndex()[dof]};
	}

	@Override
	public int getDoF() {
		return dof;
	}

}