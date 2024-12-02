package math.shapeFunctions;

import math.linalg.Matrix;
import math.linalg.Vector;

public class ThreeNodeTriangle implements ShapeFunction{

	/** numbering of nodes :
	 * 
	 *          (3)
	 *         /   \
	 *        /     \
	 *       /       \
	 *      /         \
	 *     /           \
	 *   (1)-----------(2)
	 */
	public Vector computeS(Vector vec) {
		return vec.clone();
	}
	
	public Matrix computeSz(Vector vec) {
		Matrix sz = Matrix.getMatrix(3,3);
		sz.identity();
		return sz;
	}
	
	public int number(){
		return 3;
	}
	
}
