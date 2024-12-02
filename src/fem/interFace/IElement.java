package fem.interFace;

import math.linalg.Matrix;
import math.linalg.Vector;
import math.numericalIntegration.IntegrationScheme;
import util.model.INamedObject;

/**
 * Interface which contains specific methods that are required by all finite elements.
 * 
 * @author Darren Sulon
 *
 */
public interface IElement extends INamedObject{

	/**
	 * Sets the new node names of this element.
	 * 
	 * @param nodeNames
	 * the names of the nodes of this element
	 */
	public void setNodeNames(String[] nodeNames);
	
	/**
	 * Computes the material matrix [D] for elastic analysis.
	 * @return
	 * material matrix - [D]
	 */
	public Matrix computeMaterialMatrix();
	
	/**
	 * Computes the strains as a 3x1 vector at a coordinate in the element for elastic analysis. 
	 * Strain is computed by the following: {strains} = [Sx]^T*{u}
	 * 
	 * @param crds
	 * coordinates where strain is required
	 * 
	 * @return
	 * strains
	 */
	public Vector getStrains(Vector crds);
	
	/**
	 * Computes the stresses as a 3x1 vector at a coordinate in the element for elastic analysis. 
	 * Strain is computed by the following: {strains} = [D]*[Sx]^T*{u}
	 * 
	 * @param crds
	 * coordinates where strain is required
	 * 
	 * @return
	 * strains
	 */
	public Vector getStresses(Vector crds);
	
	/**
	 * Computes the max in plane stresses of an element at a particular coordinate.
	 * 
	 * @param crds
	 * coordinates of stress point
	 * @param mode
	 * max in plane 1 or max in plane 2
	 * @return
	 * max in plane stresses
	 */
	public Vector getMaxInPlaneStresses(Vector crds, int mode);
	
	/**
	 * Sets the new material name of this element.
	 * 
	 * @param matName
	 * the name of the material
	 */
	public void setMaterialName(String matName);	
		
	/**
	 * Gets the number of nodes in this element.
	 * 
	 * @return
	 * number of nodes
	 */
	public int numNodes();
	
	/**
	 * Gets the system indices of this element.
	 * 
	 * @return
	 * system indices of this element
	 */
	public int[] getSystemIndices();
	
	/**
	 * Computes the [X] matrix of this element. The matrix consists all the x1 and x2 coordinates of this element.
	 * 
	 * @return
	 * [X] matrix of this element
	 */
	public Matrix computeX();
	
	/**
	 * Computes the {Ue} vector for this element. This vector consists the field state at the nodes.
	 * 
	 * @return
	 * {Ue} vector of this element
	 */
	public Vector computeU();
	
	/**
	 * Gets the element vector of an element given a constant source W0.
	 * {W0} = integral( [S]^T * W0 )
	 * 
	 * @param nodalW0
	 * constant source
	 * 
	 * @return
	 * element vector
	 */
	public Vector getElementVector(Vector nodalW0);

	/**
	 * Gets the element conductivity matrix required for performing a stationary heat flow FEM analysis.
	 * In 2D [ke] = integral over body ( [S] * [c] * [S]^T) 
	 * 
	 * @return
	 * conductivity matrix
	 */
	public Matrix getElementConductivityMatrix();
	
	/**
	 * Gets the element permeability matrix required for performing a seepage FEM analysis. In 2D [ke] = integral over body ( [S] * [c] * [S]^T) 
	 * 
	 * @return
	 * permeability matrix
	 */
	public Matrix getElementPermeabilityMatrix();
	
	/**
	 * Gets the element potential matrix required for performing a potential flow FEM analysis. In 2D [ke] = integral over body ( [S] * [c] * [S]^T) 
	 * 
	 * @return
	 * potential matrix
	 */
	public Matrix getElementPotentialMatrix();
	
	/**
	 * Gets the element stiffness matrix required for performing a eleastic FEM analysis. In 2D [ke] = integral over body ( [S] * [c] * [S]^T) 
	 * 
	 * @return
	 * stiffness matrix
	 */
	public Matrix getElementStiffnessMatrix();
	
	/**
	 * Computes the gradient vector {g} at a point in the element  given normalized coordinates. {g} = {d}^T * {u}
	 * 
	 * @param zcrds
	 * coordinates
	 * 
	 * @return
	 * {g} gradient vector
	 */
	public Vector computeGradientVector(Vector zcrds);
	
	/**
	 * Gets the integration scheme specific to this element.
	 * 
	 * @return
	 * integration scheme of this element
	 */
	public IntegrationScheme getIntegrationScheme(); // G
	
	/**
	 * Gets the normalized centroid coordinates as a vector.
	 * 
	 * @return
	 * z coordinates
	 */
	public Vector centroidZcoordinates(); // G
	
	/**
	 * Method converts the {z} coordinates to {X} coordinates.
	 * 
	 * @param zcrds
	 * z coordinates
	 * @return
	 * x coordinates
	 */
	public Vector zTox(Vector zcrds);
}
