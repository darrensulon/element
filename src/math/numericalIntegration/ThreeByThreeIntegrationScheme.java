package math.numericalIntegration;

import math.linalg.Vector;

public class ThreeByThreeIntegrationScheme implements IntegrationScheme{

	// Gauss integration (3x3 point)
	private static double c1 = -0.774596669241483;
	private static double c2 = 0.0;
	private static double c3 = 0.774596669241483;
	private static double w1 = 0.555555555555555;
	private static double w2 = 0.888888888888889;
	private static double w3 = 0.555555555555555;
	
	private static double[] z1g = {c1, c2, c3, c1, c2, c3, c1, c2, c3};
	private static double[] z2g = {c1, c1, c1, c2, c2, c2, c3, c3, c3};
	private static double[]  wg = {w1*w1, w2*w1, w3*w1, w1*w2, w2*w2, w3*w2, w1*w3, w2*w3, w3*w3};

	@Override
	public int numberOfGaussPoints() {
		return 3*3;
	}
	
	@Override
	public GaussPoint getGaussPoint(int index) {
		if(index < 0 || index > 8){
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
