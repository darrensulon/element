package fem.components;

import fem.model.view.ElasticGraphicalOutputOptions;
import math.linalg.Matrix;
import math.linalg.Vector;
import math.shapeFunctions.ThreeNodeTriangle;

/**
 * This class is abstract with super class AbstractElement. This element is a triangular finite element which contains
 * three nodes with 2 degrees of freedom at each node. The degrees of freedom represent displacement in vertical and 
 * horizontal directions. It is assumed in the derivation of this element that strains remain constant over the entire 
 * surface of the element.
 * 
 *
 */
public class ConstantStrainTriangle extends AbstractElement {

	private static final long serialVersionUID = 1L;
	
	
	public ConstantStrainTriangle(){
		numberOfDoFs = 6;
		numberOfDoFsPerNode = 2;
		setShapeFunction(new ThreeNodeTriangle());
	}
	
	/**
	 * When instantiated the name of the element and the name of the nodes which this element contains 
	 * is provided as well as the material's name.
	 * 
	 * @param name
	 * name of primal boundary condition
	 * 
	 * @param nodeNames
	 * name of the nodes this element contains
	 * 
	 * @param matName
	 * material name
	 */
	public ConstantStrainTriangle(String name, String[] nodeNames, String matName){
		super(name,nodeNames,matName);
		numberOfDoFs = 3;
		setShapeFunction(new ThreeNodeTriangle());
	}
	
	public int numNodes(){
		return 3;
	}

	public Vector computeGradientVector(Vector crds){
		return null;
	}
	
	public Matrix getElementConductivityMatrix() {
		return null;
	}
	
	public Matrix getElementPermeabilityMatrix() {
		return null;
	}

	public Vector getElementVector(Vector nodalW0) {
		return null;
	}
	
	public Vector zTox(Vector crds){
		Matrix x = super.computeX();
		Vector s = Vector.getVector(3);
		s.set(crds.get(0), 0);
		s.set(crds.get(1), 1);
		s.set(1 -crds.get(0)-crds.get(1),2);
		Vector xcrds = x.multiply(s);
		x.release();
		s.release();
		return xcrds;
	}
	
	public Vector centroidZcoordinates(){
		Vector crds = Vector.getVector(3);
		crds.ones();
		crds.scale(1./3.);
		return crds;
	}
	
	public Matrix computeSx() {
		
		Matrix x = computeX();
		Matrix sx = Matrix.getMatrix(3, 6);
		
		double bi = x.get(1, 1)- x.get(1, 2), bj = x.get(1, 2)- x.get(1, 0), bm = x.get(1, 0)- x.get(1, 1);
		double gi = x.get(0, 2)- x.get(0, 1), gj = x.get(0, 0)- x.get(0, 2), gm = x.get(0, 1)- x.get(0, 0);
		
		sx.set(bi, 0, 0);
		sx.set(0, 0, 1);
		sx.set(bj, 0, 2);
		sx.set(0, 0, 3);
		sx.set(bm, 0, 4);
		sx.set(0, 0, 5);
		sx.set(0, 1, 0);
		sx.set(gi, 1, 1);
		sx.set(0, 1, 2);
		sx.set(gj, 1, 3);
		sx.set(0, 1, 4);
		sx.set(gm, 1, 5);
		sx.set(gi, 2, 0);
		sx.set(bi, 2, 1);
		sx.set(gj, 2, 2);
		sx.set(bj, 2, 3);
		sx.set(gm, 2, 4);
		sx.set(bm, 2, 5);		
	
		sx.scale(1/(2*area()));
		return sx.transpose();
	}
	
	private Matrix computeC(){
		Matrix x = super.computeX();
		Matrix c = Matrix.getMatrix(2, 2);
		c.set(x.get(0,0) - x.get(0,2), 0, 0);
		c.set(x.get(0,1) - x.get(0,2), 0, 1);
		c.set(x.get(1,0) - x.get(1,2), 1, 0);
		c.set(x.get(1,1) - x.get(1,2), 1, 1);
		x.release();
		return c;
	}
	
	public double area(){
		Matrix c = computeC();
		double area = c.determinant()/2.;
		c.release();
		return area;
	}

	@Override
	public Matrix getElementStiffnessMatrix() {
		Matrix sx = computeSx();
		Matrix c = computeMaterialMatrix();
		Matrix ke = sx.multiply(c).multiply(sx.transpose());
		ke.scale(area());
		return ke;
	}
	
	public Matrix computeMaterialMatrix() {
		Matrix c = Matrix.getMatrix(3, 3);
		double e_mod = getElasticMaterial().geteMod();
		double v = getElasticMaterial().getPosRatio();
		double c_coef = (e_mod)/(1-v*v);
		c.clear();
		c.set(1, 0, 0);
		c.set(v, 0, 1);
		c.set(v, 1, 0);
		c.set(1, 1, 1);
		c.set((1-v)/2, 2, 2);
		c.scale(c_coef);
		return c;
	}

	@Override
	public Matrix getElementPotentialMatrix() {
		return null;
	}

	@Override
	public Vector getStrains(Vector crds) {
		Matrix sx = computeSx();
		Vector u = computeU();
		Vector strains = sx.transpose().multiply(u);
		sx.release();
		u.release();
		return strains;
	}

	@Override
	public Vector getStresses(Vector crds) {
		Matrix sxT = computeSx().transpose();
		Matrix e = computeMaterialMatrix();
		Vector u = computeU();
		Vector stresses = e.multiply(sxT).multiply(u);
		sxT.release();
		e.release();
		u.release();
		return stresses;
	}

	@Override
	public Vector getMaxInPlaneStresses(Vector crds, int mode) {
		Vector inPlaneStresses = Vector.getVector(3); //Max in plane, Min in plane, theta
		Vector pointStresses = getStresses(crds);
		double sigma11 = ((pointStresses.get(0)+ pointStresses.get(1))/2.) + Math.sqrt(Math.pow(((pointStresses.get(0)- pointStresses.get(1))/2), 2) +  Math.pow(pointStresses.get(2), 2));
		double sigma22 = (pointStresses.get(0)+ pointStresses.get(1))/2. - Math.sqrt(Math.pow(((pointStresses.get(0)- pointStresses.get(1))/2), 2) +  Math.pow(pointStresses.get(2), 2));
		double theta = (Math.atan(pointStresses.get(2)/((pointStresses.get(0)+ pointStresses.get(1))/2)))/2;
		if(mode == ElasticGraphicalOutputOptions.S_INPLANE2) {
			theta = theta + Math.PI/2.;
		}
		inPlaneStresses.set(sigma11, 0);
		inPlaneStresses.set(sigma22, 1);
		inPlaneStresses.set(theta, 2);
		return inPlaneStresses;
	}	
}
