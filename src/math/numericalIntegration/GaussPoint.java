package math.numericalIntegration;

import math.linalg.Vector;


public class GaussPoint {
	private Vector coordinates;
	private double weight;
	
	/**
	 * Create a GaussPoint with the given coordinates and weight
	 * @param coordinates, the Vector containing the GaussPoint's position vector
	 * @param weight, the associated weight of the GaussPoint
	 */
	public GaussPoint(Vector coordinates, double weight){
		this.coordinates = coordinates;
		this.weight = weight;
	}

	// Return the coordinate vector
	public Vector coordinates(){ return coordinates; }	
	
	// Return weight
	public double weight(){ return weight; }	
}
