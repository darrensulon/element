package math.numericalIntegration;

import math.linalg.Vector;

public class TwoByTwoIntegrationScheme implements IntegrationScheme {

	private static final double gp = 1./Math.sqrt(3.);
	private static final double[] z1g = {-gp, gp,  gp, -gp};
	private static final double[] z2g = {-gp,-gp,  gp,  gp};
	private static final double[]  wg = {1.0,1.0, 1.0, 1.0};

	@Override
	public int numberOfGaussPoints() {
		return 2*2;
	}
	
	@Override
	public GaussPoint getGaussPoint(int index) {
		if(index < 0 || index > 3){
			System.err.println("Gausspoint index out of bounds");
			return null;
		} else {
			Vector coords = Vector.getVector(2);
			coords.set(z1g[index], 0);
			coords.set(z2g[index], 1);
			return new GaussPoint(coords, wg[index]);
		}
	}		
}
