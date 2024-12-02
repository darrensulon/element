package util.view;

import math.linalg.Matrix;
import math.linalg.MatrixDimensionException;

public class TriaArea {

	private Matrix m_values;
	
	// stores the triangle information in a matrix.
	// first row contains the x coordinates,
	// second row contains the y coordinates,
	// third row contains the u values
	public TriaArea(Matrix crds){
		if(crds.rows() != 3 || crds.cols() != 3)
			throw new MatrixDimensionException();
		this.m_values = crds.clone();
	}

	public double getX(int index){
		return m_values.get(0, index);
	}
	
	public double getY(int index){
		return m_values.get(1, index);
	}
	
	public double getU(int index){
		return m_values.get(2, index);
	}
	
	public void setX(double value, int index){
		m_values.set(value, 0, index);
	}
	
	public void setY(double value, int index){
		m_values.set(value, 1, index);
	}
	
	public void setU(double value, int index){
		m_values.set(value, 2, index);
	}	
	
	public double max(){
		return m_values.maxRowEntry(2);
	}
	
	public double min(){
		return  m_values.minRowEntry(2);
	}
	
	public void release(){
		m_values.release();
	}
	
}
