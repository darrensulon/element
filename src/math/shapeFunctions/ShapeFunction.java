package math.shapeFunctions;

import math.linalg.Matrix;
import math.linalg.Vector;

public interface ShapeFunction {

	public int number();	
	public Vector computeS(Vector vec);	
	public Matrix computeSz(Vector vec);
	
}
