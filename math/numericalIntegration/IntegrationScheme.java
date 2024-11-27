package math.numericalIntegration;



public interface IntegrationScheme {

	// Number of Gausspoints in the integration scheme
	// E.g. for 2-point integration the number is:
	// in 1D: 2
	// in 2D: 2x2 = 4
	// in 3D: 2x2x2 = 8
	public int numberOfGaussPoints();
	
	// Get the Gausspoint with the given index.
	public GaussPoint getGaussPoint(int index);
	
}
