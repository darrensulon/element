package fem.components;

import fem.analysis.Analysis;
import math.linalg.Matrix;
import math.linalg.Vector;
import math.numericalIntegration.GaussPoint;
import math.numericalIntegration.IntegrationScheme;
import math.numericalIntegration.ThreeByThreeIntegrationScheme;
import math.numericalIntegration.TwoByTwoIntegrationScheme;
import math.shapeFunctions.FourNodeQuad;

/**
 * This class is abstract with super class AbstractElement. This element is a isoparametric quadrilateral finite element which contains
 * four nodes with 2 degrees of freedom at each node. The degrees of freedom represent displacement in vertical and 
 * horizontal directions.
 * 
 *
 */
public class PlaneQuad4Iso extends AbstractElement {

	private static final long serialVersionUID = 1L;
	
	/**
	 * When instantiated the shape functions and integration scheme of this element is set as well as the number of degrees of freedom
	 * which the element contains and the number of degrees of freedom per node.
	 * 
	 */
	public PlaneQuad4Iso(){
		setShapeFunction(new FourNodeQuad());
		setIntegrationScheme(new TwoByTwoIntegrationScheme());
		numberOfDoFs = 8;
		numberOfDoFsPerNode = 2;
	}
	
	/**
	 * When instantiated the name of the element and the name of the nodes which this element contains 
	 * is provided. The shape functions and integration scheme of this element is set.
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
	public PlaneQuad4Iso(String name, String[] nodeNames, String matName){
		super(name,nodeNames,matName);
		setShapeFunction(new FourNodeQuad());
		setIntegrationScheme(new TwoByTwoIntegrationScheme());
		numberOfDoFs = 8;
	}
	
	public int numNodes(){
		return 4;
	}
	
	public Vector computeGradientVector(Vector crds){
		return null;
	}
	
	public Matrix getElementConductivityMatrix() {
		return null;
	}

	public Vector getElementVector(Vector nodalW0) {
		return null;
	}
	
	public Vector zTox(Vector zcrds){
		Matrix x = computeX();
		Vector s = getShapeFunction().computeS(zcrds);
		Vector xcrds = x.multiply(s);
		x.release();
		s.release();
		return xcrds;
	}
	
	public Vector centroidZcoordinates(){
		Vector crds = Vector.getVector(2);
		crds.clear();
		return crds;
	}

	@Override
	public Matrix getElementPermeabilityMatrix() {
		return null;
	}

	
	public Matrix ComputeS(Vector crds) {

		Vector sfS = getShapeFunction().computeS(crds);
		Matrix s = Matrix.getMatrix(8, 2);
		s.clear();
		
		s.add(sfS.get(0), 0, 0);
		s.add(sfS.get(0), 1, 1);
		s.add(sfS.get(1), 2, 0);
		s.add(sfS.get(1), 3, 1);
		s.add(sfS.get(2), 4, 0);
		s.add(sfS.get(2), 5, 1);
		s.add(sfS.get(3), 6, 0);
		s.add(sfS.get(3), 7, 1);
		
		sfS.release();
		
		return s;
		
	}
	
	public double computeDetJacobian(Vector crds) {
		
		double s = crds.get(0);
		double t = crds.get(1);
		
		Matrix xT = computeX().transpose();
		
		Matrix XcT = Matrix.getMatrix(1, 4);
		XcT.copyRow(xT.col(0), 0);
		
		Vector Yc = xT.col(1);
		
		Matrix j = Matrix.getMatrix(4, 4);
		
		j.set(0, 0, 0);
		j.set(1-t, 0, 1);
		j.set(t-s, 0, 2);
		j.set(s-1, 0, 3);
		
		j.set(t-1, 1, 0);
		j.set(0, 1, 1);
		j.set(s+1, 1, 2);
		j.set(-s-t, 1, 3);
		
		j.set(s-t, 2, 0);
		j.set(-s-1, 2, 1);
		j.set(0, 2, 2);
		j.set(t+1, 2, 3);
		
		j.set(1-s, 3, 0);
		j.set(s+t, 3, 1);
		j.set(-t-1, 3, 2);
		j.set(0, 3, 3);
		
		double detJ = (1./8.)*((XcT.multiply(j).multiply(Yc)).get(0));
		
		
		xT.release();
		XcT.release();
		Yc.release();
		j.release();
		
		System.err.println(detJ);
		
		return detJ;
	}
	
	public Matrix computeB(Vector crds) {
		
		double s = crds.get(0);
		System.err.println("s = "+s);
		double t = crds.get(1);
		System.err.println("t = "+t);
		
		Matrix x = computeX();
		
		double x1 = x.get(0, 0);
		double x2 = x.get(0, 1);
		double x3 = x.get(0, 2);
		double x4 = x.get(0, 3);
		double y1 = x.get(1, 0);
		double y2 = x.get(1, 1);
		double y3 = x.get(1, 2);
		double y4 = x.get(1, 3);
		
		double a = (1./4.)*((y1*(s-1))+(y2*(-1-s))+(y3*(1+s))+(y4*(1-s)));;
		double b = (1./4.)*(y1*(t-1)+y2*(1-t)+y3*(1+t)+y4*(-1-t));
		double c = (1./4.)*(x1*(t-1)+x2*(1-t)+x3*(1+t)+x4*(-1-t));
		double d = (1./4.)*(x1*(s-1)+x2*(-1-s)+x3*(1+s)+x4*(1-s));
		
		Matrix sz = getShapeFunction().computeSz(crds);
		
		Matrix bMatrix = Matrix.getMatrix(3, 8);
		
		System.err.println(a*sz.get(0, 0)- b*sz.get(0, 1));
		double b11 = a*sz.get(0, 0) - b*sz.get(0, 1);
		System.err.println("b11 =" +b11);
		
		//first column
		bMatrix.set(b11, 0, 0);
		bMatrix.set(0, 1, 0);
		bMatrix.set(c*sz.get(0, 1)- d*sz.get(0, 0), 2, 0);
		//second column
		bMatrix.set(0, 0, 1);
		bMatrix.set(c*sz.get(0, 1)- d*sz.get(0, 0), 1, 1);
		bMatrix.set(a*sz.get(0, 0)- b*sz.get(0, 1), 2, 1);
		//third column
		bMatrix.set(a*sz.get(1, 0)- b*sz.get(1, 1), 0, 2);
		bMatrix.set(0, 1, 2);
		bMatrix.set(c*sz.get(1, 1)- d*sz.get(1, 0), 2, 2);
		//fourth column
		bMatrix.set(0, 0, 3);
		bMatrix.set(c*sz.get(1, 1)- d*sz.get(1, 0), 1, 3);
		bMatrix.set(a*sz.get(1, 0)- b*sz.get(1, 1), 2, 3);
		//fifth column
		bMatrix.set(a*sz.get(2, 0)- b*sz.get(2, 1), 0, 4);
		bMatrix.set(0, 1, 4);
		bMatrix.set(c*sz.get(2, 1)- d*sz.get(2, 0), 2, 4);
		//sixth column
		bMatrix.set(0, 0, 5);
		bMatrix.set(c*sz.get(2, 1)- d*sz.get(2, 0), 1, 5);
		bMatrix.set(a*sz.get(2, 0)- b*sz.get(2, 1), 2, 5);
		//seventh column
		bMatrix.set(a*sz.get(3, 0)- b*sz.get(3, 1), 0, 6);
		bMatrix.set(0, 1, 6);
		bMatrix.set(c*sz.get(3, 1)- d*sz.get(3, 0), 2, 6);
		//eighth column
		bMatrix.set(0, 0, 7);
		bMatrix.set(c*sz.get(3, 1)- d*sz.get(3, 0), 1, 7);
		bMatrix.set(a*sz.get(3, 0)- b*sz.get(3, 1), 2, 7);
		
		double detJ = computeDetJacobian(crds);
		bMatrix.print("B", System.err);
		bMatrix.scale(1./detJ);
		
		x.release();
		sz.release();
		bMatrix.print("B", System.err);
		return bMatrix;
	}


	@Override
	public Matrix getElementStiffnessMatrix() {
		Matrix ke = Matrix.getMatrix(8, 8);
		ke.clear();
		
		Matrix d = computeMaterialMatrix();
		
		IntegrationScheme intScheme = getIntegrationScheme();
		
		for (int i = 0; i < intScheme.numberOfGaussPoints(); i++) {
			GaussPoint gp = intScheme.getGaussPoint(i);
			Vector gp_crds = gp.coordinates();
			
			Matrix b = computeB(gp_crds);
			Matrix bT = b.transpose();
			Matrix bTdb = (bT.multiply(d)).multiply(b);
			
			
			double detJ = computeDetJacobian(gp_crds);
			double weight = gp.weight();
			
			bTdb.scale(detJ*weight);
			
			ke.add(bTdb);
			
			// release resources...
			gp_crds.release();
			b.release();
			bT.release();
			
		};
		d.release();
		return ke;
		
	}

	@Override
	public Matrix getElementPotentialMatrix() {
		return null;
	}

	@Override
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
	public Vector getStrains(Vector crds) {
		Matrix b = computeB(crds);
		Vector u = computeU();
		Vector strains = b.multiply(u);
		b.release();
		u.release();
		return strains;
	}

	@Override
	public Vector getStresses(Vector crds) {
		Matrix e = computeMaterialMatrix();
		Matrix b = computeB(crds);
		Vector u = computeU();
		Vector stresses = e.multiply(b).multiply(u);
		e.release();
		b.release();
		u.release();
		return stresses;
	}

	@Override
	public Vector getMaxInPlaneStresses(Vector crds, int mode) {
		Vector inPlaneStresses = Vector.getVector(3); //Max in plane, Min in plane, theta
		Vector pointStresses = getStresses(crds);
		double sigma11 = (pointStresses.get(0)+ pointStresses.get(1))/2 + Math.sqrt(((pointStresses.get(0)+ pointStresses.get(1))/2)+pointStresses.get(2));
		double sigma22 = (pointStresses.get(0)+ pointStresses.get(1))/2 - Math.sqrt(((pointStresses.get(0)+ pointStresses.get(1))/2)+pointStresses.get(2));
		double theta = (Math.atan(pointStresses.get(2)/((pointStresses.get(0)+ pointStresses.get(1))/2)))/2;
		inPlaneStresses.set(sigma11, 0);
		inPlaneStresses.set(sigma22, 1);
		inPlaneStresses.set(theta, 2);
		return inPlaneStresses;
	}
	
}
