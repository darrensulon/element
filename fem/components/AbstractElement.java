package fem.components;

import math.linalg.Matrix;
import math.linalg.Vector;
import math.numericalIntegration.IntegrationScheme;
import math.shapeFunctions.ShapeFunction;
import util.model.NamedObject;
import util.model.NamedObjectNotFoundException;
import fem.interFace.IElement;
import fem.interFace.INode;
import fem.model.FemException;

/**
 * This class is abstract with super class IElement and is the parent class of all finite elements.
 * Class contains generic get and set methods required for implementing a finite element into the FemModel.
 * 
 *
 */
public abstract class AbstractElement extends NamedObject implements IElement{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The number of degrees of freedom in the element.
	 */
	protected int numberOfDoFs;
	
	/**
	 * The number of degrees of freedom found at each node in the element.
	 */
	protected int numberOfDoFsPerNode;
	
	/**
	 * A String array of the names of nodes which belong to this finite element. Node names are stored in 
	 * in counter clockwise direction.
	 */
	private String[] m_nodes;
	
	/**
	 * The name of the material of this finite element. Can be one of the following: StationaryHeatFlowMaterial, 
	 * ElasticMaterial, SeepageMaterial, PotentialFlowMaterial or ElectrostaticMaterial.
	 */
	private String m_matName;

	/**
	 * The shape functions particular to this type of finite element.
	 */
	private ShapeFunction m_shapeFunction;
	
	/**
	 * The Gaussian integration of this particular element.
	 */
	private IntegrationScheme m_integrationScheme;
	
	/**
	 * An empty element is created.
	 */
	public AbstractElement(){}
	
	/**
	 * An element is created and given a name as well as the names of its nodes and its material
	 * 
	 * @param name 
	 * name of element
	 * 
	 * @param nodeNames 
	 * names of Nodes
	 * 
	 * @param matName 
	 * material Name
	 */
	public AbstractElement(String name, String[] nodeNames, String matName){
		super(name);
		m_nodes = new String[nodeNames.length];
		System.arraycopy(nodeNames, 0, m_nodes, 0, nodeNames.length);
		this.m_matName = matName;
	}

	/**
	 * Gets the shape functions of this element.
	 * 
	 * @return
	 * shape functions of this element
	 */
	protected ShapeFunction getShapeFunction(){
		return m_shapeFunction;
	}
	
	/**
	 * Sets the shape functions for this element.
	 * 
	 * @param sf
	 * the new shape functions of this element
	 */
	protected void setShapeFunction(ShapeFunction sf){
		this.m_shapeFunction = sf;
	}
		
	/**
	 * Sets the integration scheme of this element.
	 * 
	 * @param intscheme
	 * the new integration scheme of this element
	 */
	protected void setIntegrationScheme(IntegrationScheme intscheme){
		this.m_integrationScheme = intscheme;
	}
	
	public IntegrationScheme getIntegrationScheme(){
		return m_integrationScheme;
	}
	
	
	
	public void setNodeNames(String[] nodeNames){
		if(numNodes()!=nodeNames.length)
			throw new FemException("Element '"+getName()+"' of type "+this.getClass().getName()+ ":\n\tIncorrect number of nodes, "+nodeNames.length+", assigned to element,\n" +
					"\telement requires "+numNodes()+" nodes.");
		this.m_nodes = nodeNames;
	}
	
	
	public void setMaterialName(String matName){
		this.m_matName = matName;
	}
	

	public int[] getSystemIndices() {
		int[] _indices = new int[numberOfDoFs];
		int position = 0;
		int[] nodeIndices = null;
		for(int i = 0; i < m_nodes.length; i++) {
			nodeIndices = node(i).getSystemIndex(); 
			for(int j = 0; j < nodeIndices.length; j++) {
				_indices[position] = nodeIndices[j];
				position++;
			}
		}
		return _indices;
	}
	
	/**
	 * Method returns the node requested at a specific index.
	 * 
	 * @param index
	 * index of the node
	 * 
	 * @return
	 * the node which corresponds to the index
	 */
	public INode node(int index){
		try {
			return (INode)getModel().get(m_nodes[index]);
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}
	
	/**
	 * Gets the stationary heat flow material of this element.
	 * 
	 * @return
	 * stationary heat flow material
	 */
	public StationaryHeatFlowMaterial getStationaryHeatFlowMaterial(){
		try {
			return (StationaryHeatFlowMaterial)getModel().get(m_matName);
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}
	
	/**
	 * Gets the seepage material of this element.
	 * 
	 * @return
	 * seepage material
	 */
	public SeepageMaterial getSeepageMaterial(){
		try {
			return (SeepageMaterial)getModel().get(m_matName);
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}
	
	/**
	 * Gets the elastic material of this element.
	 * 
	 * @return
	 * elastic material
	 */
	public ElasticMaterial getElasticMaterial() {
		try {
			return (ElasticMaterial)getModel().get(m_matName);
		}
		catch(NamedObjectNotFoundException nonfe){
			throw nonfe.setCallingObject(this);
		}
	}
	
	
	public Matrix computeX(){
		Vector crds0 = node(0).getCoordinates();
		int nodeDim = crds0.rows();
		crds0.release();
		Matrix x = Matrix.getMatrix(nodeDim, m_nodes.length);
		for(int i = 0; i < m_nodes.length; i++){
			Vector crds = node(i).getCoordinates();
			x.setCol(crds.get(), i);
			crds.release();
		}
		return x;
	}
	
	
	public Vector computeU(){
		
		Vector u = Vector.getVector(numberOfDoFs);
		double[] uValues = new double[numberOfDoFs];
		double[] nodeU_Values = null;
		int position = 0;
		for(int i = 0; i <  m_nodes.length; i++){
//			u.set(node(i).getPrimal(),i);
			nodeU_Values = node(i).getPrimal();
			for(int j = 0; j < nodeU_Values.length; j++) {
				uValues[position] = nodeU_Values[j];
				position++;
			}
		}
		u.set(uValues);
		return u;		

		
	}
	
}