package math.shapeFunctions;

import math.linalg.Matrix;
import math.linalg.Vector;

public class FourNodeQuad implements ShapeFunction{
	
	
    public Vector computeS(Vector crds){
    	double z0 = crds.get(0);
    	double z1 = crds.get(1);
    	Vector s = Vector.getVector(4);
		s.set(0.25*(1-z0)*(1-z1),0);
		s.set(0.25*(1+z0)*(1-z1),1);
		s.set(0.25*(1+z0)*(1+z1),2);
		s.set(0.25*(1-z0)*(1+z1),3);
		return s;
    }
    
    public Matrix computeSz(Vector crds) {
    	double z0 = crds.get(0);
    	double z1 = crds.get(1);
    	Matrix sz = Matrix.getMatrix(4, 2);
		sz.set(-(1.0 - z1) / 4.0, 0, 0);
		sz.set( (1.0 - z1) / 4.0, 1, 0);
		sz.set( (1.0 + z1) / 4.0, 2, 0);
		sz.set(-(1.0 + z1) / 4.0, 3, 0);
		sz.set(-(1.0 - z0) / 4.0, 0, 1);
		sz.set(-(1.0 + z0) / 4.0, 1, 1);
		sz.set( (1.0 + z0) / 4.0, 2, 1);
		sz.set( (1.0 - z0) / 4.0, 3, 1);
        return sz;
    }
    
	public int number(){
		return 4;
	}
	
}
