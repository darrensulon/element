package fem.components;

import fem.analysis.Analysis;
import math.linalg.Matrix;
import math.linalg.Vector;

/**
 * This class is abstract with super class AbstractElement. This element is an triangular finite element which contains
 * three nodes.
 * 
 * @author Darren
 *
 */
public class Triangle extends AbstractElement {

	private static final long serialVersionUID = 1L;
	
	
	public Triangle(){
		numberOfDoFs = 3;
		numberOfDoFsPerNode = 1;
	}
	
	/**
	 * When instantiated the name of the element and the name of the nodes which this element contains 
	 * is provided.
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
	public Triangle(String name, String[] nodeNames, String matName){
		super(name,nodeNames,matName);
		numberOfDoFs = 3;
	}
	
	public int numNodes(){
		return 3;
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
	
	/**
	 * Computes the the [Sx] matrix of the element. In the case of triangular matrix the [Sx] matrix is also considered as the [Zx] matrix.
	 * @return
	 * [Sx] matrix
	 */
	private Matrix computeSx(){
		Matrix c = computeC();
		Matrix sx = Matrix.getMatrix(3, 2);
		sx.set(c.get(1, 1),0,0);
		sx.set(-c.get(1,0),1,0);
		sx.set(c.get(1,0)-c.get(1, 1),2,0);
		sx.set(-c.get(0,1),0,1);
		sx.set(c.get(0,0),1,1);
		sx.set(c.get(0,1)-c.get(0,0),2,1);
		sx.scale(1./c.determinant());
		c.release();
		return sx;
	}

	public Vector computeGradientVector(Vector crds){
		Vector u = computeU();	
		Matrix sx = computeSx();
		Matrix sxT = sx.transpose();
		Vector gradVector = sxT.multiply(u);
//		heatState.scale(-getStationaryHeatFlowMaterial().getConductivity());
		if(this.getModel().getChosenAnalysis().equals(Analysis.HEAT_TRANSFER)) {
			gradVector.scale(-getStationaryHeatFlowMaterial().getConductivity());
		}else if(this.getModel().getChosenAnalysis().equals(Analysis.SEEPAGE)) {
			gradVector.scale(-getSeepageMaterial().getK());
		}else if(this.getModel().getChosenAnalysis().equals(Analysis.POTENTIAL_FLOW)) {
			gradVector.scale(-1.0);
		}
		u.release();
		sx.release();
		sxT.release();
		return gradVector;
	}
	
	public Matrix getElementConductivityMatrix() {
		Matrix sx = computeSx();
		Matrix sxT = sx.transpose();		
		Matrix ke = sx.multiply(sxT);
		ke.scale(area() * getStationaryHeatFlowMaterial().getConductivity());
		// release temporary matrices
		sx.release();
		sxT.release();
		return ke;
	}
	
	public Matrix getElementPermeabilityMatrix() {
		Matrix sx = computeSx();
		Matrix sxT = sx.transpose();	
		double c = getSeepageMaterial().getK();
		Matrix ke = sx.multiply(sxT);
		ke.scale(area()*c);
		// release temporary matrices
		sx.release();
		sxT.release();
		return ke;
		
	}

	public Vector getElementVector(Vector nodalW0) {
		if(nodalW0.size() == 1){ // constant area load
			Vector _tmp = Vector.getVector(3);
			_tmp.ones();
			_tmp.scale(nodalW0.get(0));
			nodalW0.release();
			nodalW0 = _tmp.clone();
			_tmp.release();
			nodalW0.scale(area()/3.);
			return nodalW0;
		}
		else if(nodalW0.size() == 3){
			double area = area();
			double a6 = area/6.;
			double a12 = area/12.;
			Vector _tmp = Vector.getVector(3);
			_tmp.set(nodalW0.get(0)*a6 + nodalW0.get(1)*a12 + nodalW0.get(2)*a12, 0);
			_tmp.set(nodalW0.get(0)*a12 + nodalW0.get(1)*a6 + nodalW0.get(2)*a12, 1);
			_tmp.set(nodalW0.get(0)*a12 + nodalW0.get(1)*a12 + nodalW0.get(2)*a6, 2);
			nodalW0.release();
			nodalW0 = _tmp.clone();
			_tmp.release();		
			return nodalW0;
		}		
		throw new RuntimeException();
	}

//	public Matrix getMassMatrix() {
//		Matrix x = computeC();
//		Matrix mass = Matrix.getMatrix(3, 3);
//		mass.identity();		
//		// rho × c 
//		IMaterial material = material();
//		double rhoC = material.getDensity()*material.getSpecificHeat();				
//		mass.scale(rhoC*x.determinant()/6.);
//		x.release();		
//		return mass;
//	}
	
	public double area(){
		Matrix c = computeC();
		double area = c.determinant()/2.;
		c.release();
		return area;
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


	@Override
	public Matrix getElementStiffnessMatrix() {
		Matrix sx = computeSx();
		Matrix sxT = sx.transpose();	
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
		Matrix ke = sx.multiply(c).multiply(sxT);
		ke.scale(area()*c_coef);
		// release temporary matrices
		sx.release();
		sxT.release();
		c.release();
		return ke;
	}

	@Override
	public Matrix getElementPotentialMatrix() {
		Matrix sx = computeSx();
		Matrix sxT = sx.transpose();		
		Matrix ke = sx.multiply(sxT);
		ke.scale(area());
		// release temporary matrices
		sx.release();
		sxT.release();
		return ke;
	}

	@Override
	public Matrix computeMaterialMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getStrains(Vector crds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getStresses(Vector crds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getMaxInPlaneStresses(Vector crds, int mode) {
		// TODO Auto-generated method stub
		return null;
	}	
}
