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
 * This class is abstract with super class AbstractElement. This element is an isoparametric element which contains
 * four nodes.
 * 
 * @author Darren Sulon
 *
 */
public class Quad4Iso extends AbstractElement {

	private static final long serialVersionUID = 1L;
	
	/**
	 * When instantiated the shape functions and integration scheme of this element is set.
	 * 
	 */
	public Quad4Iso(){
		setShapeFunction(new FourNodeQuad());
		setIntegrationScheme(new TwoByTwoIntegrationScheme());
		numberOfDoFs = 4;
		numberOfDoFsPerNode = 1;
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
	public Quad4Iso(String name, String[] nodeNames, String matName){
		super(name,nodeNames,matName);
		setShapeFunction(new FourNodeQuad());
		setIntegrationScheme(new TwoByTwoIntegrationScheme());
		numberOfDoFs = 4;
	}
	
	public int numNodes(){
		return 4;
	}
	
	public Vector computeGradientVector(Vector crds){
		Matrix x = computeX();
		Vector u = computeU();
		
		double c = 0.0;
		if(this.getModel().getChosenAnalysis().equals(Analysis.HEAT_TRANSFER)) {
			c = (-getStationaryHeatFlowMaterial().getConductivity());
		}else if(this.getModel().getChosenAnalysis().equals(Analysis.SEEPAGE)) {
			c = (-getSeepageMaterial().getK());
		}else if(this.getModel().getChosenAnalysis().equals(Analysis.POTENTIAL_FLOW)) {
			c = -1.0;
		}

		// sz = [dS/dz]
		Matrix sz = getShapeFunction().computeSz(crds);
		
		// j = [[x]*[sz]]^T
		Matrix jacobianT = x.multiply(sz);
		Matrix jacobian = jacobianT.transpose();
		Matrix jacobianInverse = jacobian.inverse();
		Matrix zx = jacobianInverse.transpose();
		
		// [dS/dx] = [sx] = [sz][zx]
		Matrix sx = sz.multiply(zx);
		Matrix sxT = sx.transpose();
		
		Vector gradVector = sxT.multiply(u);
		gradVector.scale(c);
		
		// release matrices
		x.release();
		u.release();
		sz.release();
		jacobianT.release();
		jacobian.release();
		jacobianInverse.release();
		zx.release();
		sx.release();
		sxT.release();
		
		return gradVector;
	}
	
	public Matrix getElementConductivityMatrix() {
		Matrix x = computeX();
		Matrix ke = Matrix.getMatrix(4, 4);
		ke.clear();
		
		double c = getStationaryHeatFlowMaterial().getConductivity();
		
		// Gauss loop
//		IntegrationScheme intScheme = new TwoByTwoIntegrationScheme();
		IntegrationScheme intScheme = getIntegrationScheme();
		for (int i = 0; i < intScheme.numberOfGaussPoints(); i++) {
			GaussPoint gp = intScheme.getGaussPoint(i);
			Vector gp_crds = gp.coordinates();
			
			// sz = [dS/dz]
			Matrix sz = getShapeFunction().computeSz(gp_crds);
						
			// j = [[x]*[sz]]^T
			Matrix jacobianT = x.multiply(sz);
			Matrix jacobian = jacobianT.transpose();
			Matrix jacobianInverse = jacobian.inverse();
			Matrix zx = jacobianInverse.transpose();
			
			// [dS/dx] = [sx] = [sz][zx]
			Matrix sx = sz.multiply(zx);
			
			Matrix sxT = sx.transpose();
			
			Matrix sxsxT = sx.multiply(sxT);
			
			double jDet = jacobian.determinant();
			sxsxT.scale(jDet*gp.weight());
			ke.add(sxsxT);
		
			// release resources...
			gp_crds.release();
			sz.release();
			jacobianT.release();
			jacobian.release();
			jacobianInverse.release();
			zx.release();
			sx.release();
			sxT.release();
			sxsxT.release();
		}
		ke.scale(c);
		x.release();
		return ke;
	}

	public Vector getElementVector(Vector nodalW0) {
		if(nodalW0.size() == 1){ // constant area load
			Vector _tmp = Vector.getVector(4);
			_tmp.ones();
			_tmp.scale(nodalW0.get(0));
			nodalW0.release();
			nodalW0 = _tmp;
		}
		if(nodalW0.size() == 4){
			Vector loadVec = Vector.getVector(4);
			loadVec.clear();
			Matrix x = computeX();
			// Gauss loop
			IntegrationScheme intScheme = new ThreeByThreeIntegrationScheme();
			for (int i = 0; i < intScheme.numberOfGaussPoints(); i++) {
				GaussPoint gp = intScheme.getGaussPoint(i);
				Vector gp_crds = gp.coordinates();
				
				// sz = [dS/dz]
				Matrix sz = getShapeFunction().computeSz(gp_crds);
				
				// j = [[x]*[sz]]^T
				Matrix jacobianT = x.multiply(sz);
				Matrix jacobian = jacobianT.transpose();
				
				Vector s = getShapeFunction().computeS(gp_crds);
				double w_atGP = s.dot(nodalW0);
				s.scale(w_atGP * jacobian.determinant() * gp.weight());
				loadVec.add(s);
								
				sz.release();
				jacobianT.release();
				jacobian.release();
				s.release();				
				gp_crds.release();
			}
			nodalW0.release();
			x.release();
			return loadVec;
		}	
		throw new RuntimeException();
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
		Matrix x = computeX();
		Matrix ke = Matrix.getMatrix(4, 4);
		ke.clear();
		
		double c = getSeepageMaterial().getK();
		
		// Gauss loop
//		IntegrationScheme intScheme = new TwoByTwoIntegrationScheme();
		IntegrationScheme intScheme = getIntegrationScheme();
		for (int i = 0; i < intScheme.numberOfGaussPoints(); i++) {
			GaussPoint gp = intScheme.getGaussPoint(i);
			Vector gp_crds = gp.coordinates();
			
			// sz = [dS/dz]
			Matrix sz = getShapeFunction().computeSz(gp_crds);
						
			// j = [[x]*[sz]]^T
			Matrix jacobianT = x.multiply(sz);
			Matrix jacobian = jacobianT.transpose();
			Matrix jacobianInverse = jacobian.inverse();
			Matrix zx = jacobianInverse.transpose();
			
			// [dS/dx] = [sx] = [sz][zx]
			Matrix sx = sz.multiply(zx);
			
			Matrix sxT = sx.transpose();
			
			Matrix sxcsxT = sx.multiply(sxT);
			
			double jDet = jacobian.determinant();
			sxcsxT.scale(jDet*gp.weight()*c);
			ke.add(sxcsxT);
		
			// release resources...
			gp_crds.release();
			sz.release();
			jacobianT.release();
			jacobian.release();
			jacobianInverse.release();
			zx.release();
			sx.release();
			sxT.release();
			sxcsxT.release();
		}
		x.release();
		return ke;
	}


	@Override
	public Matrix getElementStiffnessMatrix() {
		Matrix x = computeX();
		Matrix ke = Matrix.getMatrix(4, 4);
		ke.clear();
		
		Matrix c = Matrix.getMatrix(3, 3);
		double e_mod = getElasticMaterial().geteMod();
		double v = getElasticMaterial().getPosRatio();
		double c_coef = (e_mod)/((1+v)*(1-2*v));
		c.clear();
		c.set(1-v, 0, 0);
		c.set(v, 0, 1);
		c.set(v, 1, 0);
		c.set(1-v, 1, 1);
		c.set((1-2*v)/2, 2, 2);
		c.scale(c_coef);
		
		// Gauss loop
//		IntegrationScheme intScheme = new TwoByTwoIntegrationScheme();
		IntegrationScheme intScheme = getIntegrationScheme();
		for (int i = 0; i < intScheme.numberOfGaussPoints(); i++) {
			GaussPoint gp = intScheme.getGaussPoint(i);
			Vector gp_crds = gp.coordinates();
			
			// sz = [dS/dz]
			Matrix sz = getShapeFunction().computeSz(gp_crds);
						
			// j = [[x]*[sz]]^T
			Matrix jacobianT = x.multiply(sz);
			Matrix jacobian = jacobianT.transpose();
			Matrix jacobianInverse = jacobian.inverse();
			Matrix zx = jacobianInverse.transpose();
			
			// [dS/dx] = [sx] = [sz][zx]
			Matrix sx = sz.multiply(zx);
			
			Matrix sxT = sx.transpose();
			
			Matrix sxcsxT = sx.multiply(c).multiply(sxT);
			
			double jDet = jacobian.determinant();
			sxcsxT.scale(jDet*gp.weight());
			ke.add(sxcsxT);
		
			// release resources...
			gp_crds.release();
			sz.release();
			jacobianT.release();
			jacobian.release();
			jacobianInverse.release();
			zx.release();
			sx.release();
			sxT.release();
			sxcsxT.release();
		}
		x.release();
		c.release();
		return ke;
	}

	@Override
	public Matrix getElementPotentialMatrix() {
		Matrix x = computeX();
		Matrix ke = Matrix.getMatrix(4, 4);
		ke.clear();
		
		// Gauss loop
//		IntegrationScheme intScheme = new TwoByTwoIntegrationScheme();
		IntegrationScheme intScheme = getIntegrationScheme();
		for (int i = 0; i < intScheme.numberOfGaussPoints(); i++) {
			GaussPoint gp = intScheme.getGaussPoint(i);
			Vector gp_crds = gp.coordinates();
			
			// sz = [dS/dz]
			Matrix sz = getShapeFunction().computeSz(gp_crds);
						
			// j = [[x]*[sz]]^T
			Matrix jacobianT = x.multiply(sz);
			Matrix jacobian = jacobianT.transpose();
			Matrix jacobianInverse = jacobian.inverse();
			Matrix zx = jacobianInverse.transpose();
			
			// [dS/dx] = [sx] = [sz][zx]
			Matrix sx = sz.multiply(zx);
			
			Matrix sxT = sx.transpose();
			
			Matrix sxsxT = sx.multiply(sxT);
			
			double jDet = jacobian.determinant();
			sxsxT.scale(jDet*gp.weight());
			ke.add(sxsxT);
		
			// release resources...
			gp_crds.release();
			sz.release();
			jacobianT.release();
			jacobian.release();
			jacobianInverse.release();
			zx.release();
			sx.release();
			sxT.release();
			sxsxT.release();
		}
		x.release();
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
