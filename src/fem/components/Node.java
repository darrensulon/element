package fem.components;

import math.linalg.Vector;
import util.model.NamedObject;
import fem.analysis.Analysis;
import fem.interFace.INode;

/**
 * This class is abstract with super class INode.
 * Class contains generic get and set methods required for implementing a node into the FemModel.
 * 
 * @author Darren Sulon
 *
 */
public class Node extends NamedObject implements INode{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Coordinates of the node.
	 */
	private Vector m_crds;
	
	/**
	 * System index of the node.
	 */
	private int[] m_index;
	
//	private int numberOfDoFs = 1;
	/**
	 * Number of degrees of freedom at this node.
	 */
	private int numberOfDoFs;
	
	/**
	 * Primal values at the node.
	 */
	private double[] m_primalValue;
	
	/**
	 * Whether a node has a primal boundary condition or not
	 */
	private boolean m_hasPrimalBoundaryCondition;
	
	/**
	 * This is the constructor for a 1 dimensional node. When instantiated the name of the node and the 
	 * x1 coordinate is provided.
	 * 
	 * @param name
	 * name of the node
	 * 
	 * @param x1
	 * coordinate of the node
	 */
	public Node(String name, double x1){
		super(name);
		m_crds = Vector.getVector(1, true);
		m_crds.set(x1, 0);
	}

	/**
	 * This is the constructor for a 2 dimensional node. When instantiated the name of the node as well as the 
	 * x1 and x2 coordinates are provided.
	 * 
	 * @param name
	 * name of the node
	 * 
	 * @param x1
	 * x1 coordinates
	 * 
	 * @param x2
	 * x2 coordinates
	 */
	public Node(String name, double x1, double x2){
		super(name);
		m_crds = Vector.getVector(2, true);
		m_crds.set(x1, 0);
		m_crds.set(x2, 1);
		
	}
	
	/**
	 * This is the constructor for a 3 dimensional node. When instantiated the name of the node as well as the 
	 * x1, x2 and x3 coordinates are provided.
	 * 
	 * @param name
	 * name of the node
	 * 
	 * @param x1
	 * x1 coordinates
	 * 
	 * @param x2
	 * x2 coordinates
	 * 
	 * @param x3
	 * x3 coordinates
	 */
	public Node(String name, double x1, double x2, double x3){
		super(name);
		m_crds = Vector.getVector(3, true);
		m_crds.set(x1, 0);
		m_crds.set(x2, 1);
		m_crds.set(x3, 2);
	}
	
	/**
	 * This is the constructor for any dimensional node. When instantiated the name of the node as well as 
	 * an double array of the coordinates are provided.
	 * 
	 * @param name
	 * name of the node
	 * 
	 * @param crds
	 * coordinates
	 * 
	 * @param chosenAnalysis
	 * the user's selected analysis
	 */
	public Node(String name, double[] crds, String chosenAnalysis){
		super(name);
		m_crds = Vector.getVector(crds.length, true);
		m_crds.set(crds);
		if(chosenAnalysis.equals(Analysis.HEAT_TRANSFER)||
				chosenAnalysis.equals(Analysis.SEEPAGE)||
				chosenAnalysis.equals(Analysis.POTENTIAL_FLOW)||
				chosenAnalysis.equals(Analysis.ELECTROSTATIC)) {
			numberOfDoFs = 1;
		}else if(chosenAnalysis.equals(Analysis.ELASTICITY)) {
			numberOfDoFs = 2;
		}
		m_primalValue = new double[numberOfDoFs];
		m_index = new int[numberOfDoFs];
	}
	
	public Vector getCoordinates() {
		return m_crds.clone();
	}

	
	public void setPrimal(double primalValue, int dof){
		m_primalValue[dof] = primalValue;
		
	}
	
	public double[] getPrimal() {		
		return m_primalValue;
	}

	
	public int[] getSystemIndex() {	
		return m_index;
	}
	
	public int setSystemIndex(int index){
		m_index = new int[numberOfDoFs];
		for(int i = 0; i < numberOfDoFs; i++) {
			this.m_index[i] = index;
			index = index + 1;
		}
		return index;
	}
	
	public void setPrimalBoundaryCondition(boolean hasPrimalBoundaryCondition){
		this.m_hasPrimalBoundaryCondition = hasPrimalBoundaryCondition;
	}
	
	public boolean hasPrimalBoundaryCondition(){
		return m_hasPrimalBoundaryCondition;
	}
	
	public void clearPrimal() {
		for(int i = 0; i < m_primalValue.length; i++) {
			m_primalValue[i] = 0;
		}
	}

	@Override
	public void setNumberofDofs(int numberOfDoFs) {
		this.numberOfDoFs = numberOfDoFs;
	}
	
}
