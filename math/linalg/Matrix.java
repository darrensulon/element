package math.linalg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import math.InlineCalculator;
import math.InvalidParameterException;


public class Matrix implements Externalizable {

	private static final long serialVersionUID = 1L;

	private static Set<Matrix> s_matrixSet = new HashSet<Matrix>();
	private static Map<String, Set<Matrix>> s_matrixMap = new HashMap<String, Set<Matrix>>();
	private static Map<String, Integer> s_couterMap = new HashMap<String, Integer>();
	
	private static boolean verbose = false;
	
	private static String getKey(int rows, int cols){
		return "["+rows+"x"+cols+"]";
	}
	public synchronized static Matrix getMatrix(int rows, int cols){
		String key = getKey(rows, cols);
		if(!s_matrixMap.containsKey(key)){
			s_matrixMap.put(key, new HashSet<Matrix>());
			s_couterMap.put(key, 0);
		}
		Set<Matrix> set = s_matrixMap.get(key);
		if(set.isEmpty()){
			s_couterMap.put(key, s_couterMap.get(key)+1);
			Matrix mat = new Matrix(rows,cols);
			if(verbose)System.err.println("Matrix ["+rows+","+cols+"] created!");
			s_matrixSet.add(mat);
			mat.m_standalone = false;
			return mat;
		}
		else{
			Iterator<Matrix> iter = set.iterator();
			Matrix mat = iter.next();
			iter.remove();
			mat.locked = false;
			mat.m_standalone = false;
			return mat;
		}
	}
	
	public synchronized static Matrix getMatrix(int rows, int cols, boolean standalone){
		if(standalone)
			return new Matrix(rows,cols);
		else
			return getMatrix(rows,cols);
	}
	
	public static void stats(PrintStream stream){
		Iterator<Map.Entry<String,Set<Matrix>>> iter = s_matrixMap.entrySet().iterator();
		stream.println("\nMatrix service stats...");
		int available = 0;
		while(iter.hasNext()){
			Map.Entry<String, Set<Matrix>> entry = iter.next();
			int _total = s_couterMap.get(entry.getKey());
			stream.println(entry.getKey() +"\t("+ entry.getValue().size() +
					" of "+ _total+") available");
			available += entry.getValue().size();
		}
		stream.println("Total number of matrices = "+s_matrixSet.size()+"\t["+Math.round(available*100./s_matrixSet.size())+"% available]\n");
	}
	
	private static final String VALUE = "[\\-|\\+]?\\d*\\.?\\d*";
	private static final String OPR = "\\s*[\\+|\\*|\\-|\\/]\\s*";  
	private static final String EXPR = "[(|"+VALUE+"]+[[("+VALUE+OPR+"]|"+OPR+VALUE+"|)]*";
	private static final String VEC_STRING = "\\{["+EXPR+"|"+VALUE+"][\\s*,\\s*["+EXPR+"|"+VALUE+"]]*\\}";
	private static final String MAT_STRING = "\\{\\s*"+VEC_STRING+"[\\s*,\\s*"+VEC_STRING+"]*\\}";
	
	public static Matrix getMatrix(String values, boolean standalone){
		if(values.matches(MAT_STRING)){
			String[] _parts = values.substring(1,values.length()-1).split("\\}\\s*,\\s*\\{");
			StringTokenizer st = new StringTokenizer(_parts[0].replace(" ", "").substring(1),",");
			int rows = _parts.length;
			int cols = st.countTokens();
			Matrix mat = Matrix.getMatrix(rows,cols, standalone);
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					String val = st.nextToken().trim().replace(" ", "");
					if(val.matches(VALUE))
						mat.m_values[i][j] = Double.parseDouble(val);
					else if(val.matches(EXPR)){
						try {
							mat.m_values[i][j] = InlineCalculator.calc("="+val);
						} catch (InvalidParameterException e) {
							throw new MatrixDimensionException("Invalid matrix format : " +val);
						}
					}					
				}	
				if(i < rows - 2)
					st = new StringTokenizer(_parts[i+1].replace(" ", ""),",");
				else if(i < rows - 1)
					st = new StringTokenizer(_parts[i+1].replace(" ", "").replace("}",""),",");
				if(st.countTokens() != cols && i < rows - 1)
					throw new MatrixDimensionException("Incorrect entry count of "+st.countTokens()+" (should be "+cols+")");
			}
			return mat;
		}
		else
			throw new VectorDimensionException("Invalid vector format : " +values);
	}
	
	public static Matrix getMatrix(String values){
		return getMatrix(values,false);
	}
	
	public static void dump() {
		if(verbose)
			System.err.println("Dumping " + s_matrixSet.size() + " matrices.");
		int count = 0;
		Iterator<Matrix> iter = s_matrixSet.iterator();
		while(iter.hasNext())
			if(!iter.next().locked)
				count++;
		// strict has not been released cannot be dumped policy
		if(count > 0)
			throw new MatrixLockedException("Matrices being dumped " + s_matrixSet.size() +
					", of which " + count + " has not yet been released.");
		s_matrixSet.clear();
		s_couterMap.clear();
		s_matrixMap.clear();
	}
	
	// end of static stuff.	
	private double[][] m_values;
	private boolean locked = false;
	private boolean m_standalone = false;
	
	public Matrix(){ }
	
	private Matrix(int rows, int cols){
		m_values = new double[rows][cols];
		m_standalone = true;
	}
		
	protected void _check(){
		if(locked)
			throw new MatrixLockedException();
	}
	
	private boolean isZero(double d){
		if(Math.abs(d) < 1e-9)
			return true;
		return false;
	}
	
	public void set(double value, int row, int col){
		_check();
		m_values[row][col] = value;
	}
	
	public void add(double value, int row, int col){
		_check();
		m_values[row][col] = value;
	}
	
	public void setRow(Vector vec, int row){
		_check(); vec._check();
		if(m_values[row].length != vec.size() || row >= rows())
			throw new MatrixDimensionException();
		System.arraycopy(vec.get(), 0, m_values[row], 0, cols());		
	}
	
	public void setCol(Vector vec, int col){
		_check(); vec._check();
		if(m_values.length !=  vec.size() || col >= cols())
			throw new MatrixDimensionException();
		for(int i = 0; i < vec.size(); i++)
			m_values[i][col] = vec.get(i);
	}
	
	public void setRow(double[] values, int row){
		_check();
		if(m_values[row].length != values.length || row >= rows())
			throw new MatrixDimensionException();
		System.arraycopy(values, 0, m_values[row], 0, cols());
	}
	
	public void setCol(double[] values, int col){
		_check();
		if(m_values.length != values.length || col >= cols())
			throw new MatrixDimensionException();
		for(int i = 0; i < values.length; i++)
			m_values[i][col] = values[i];
	}
	
	public void set(double[][] values){
		_check();
		if(values.length != rows() || values[0].length != cols())
			throw new MatrixDimensionException();
		for(int i = 0; i < m_values.length; i++)
			System.arraycopy(values[i], 0, m_values[i], 0, cols());
	}
	
	public double get(int row, int col){
		_check();
		if(row >= rows() || col >= cols())
			throw new MatrixDimensionException();
		return m_values[row][col];
	}
	
	public int rows(){
		_check();
		return m_values.length;
	}
	
	public int cols(){
		_check();
		return m_values[0].length;
	}
	
	public Matrix expand(){
		_check();
		Matrix expnd = Matrix.getMatrix(rows()*cols(),cols()*cols());
		expnd.clear();
		int c = cols();
		int[] _r = new int[rows()];
		int[] _c = new int[cols()];
		for(int i = 0; i < c; i++){
			for(int j = 0; j < _r.length; j++)
				_r[j] = i+c*j;
			for(int j = 0; j < _c.length; j++)
				_c[j] = i*c+j;
			expnd.addSubmatrix(this, _r, _c);
		}
		return expnd;
	}
	
	/**
	 * Clones the contents of this matrix, expensive to call.
	 * 
	 * @return
	 */
	public double[][] get(){
		_check();
		double[][] _val = new double[rows()][];
		for(int i = 0; i < _val.length; i++)
			_val[i] = m_values[i].clone();
		return _val;
	}
	
	public Vector col(int col){
		_check();
		Vector vec = Vector.getVector(rows());
		for(int i = 0; i < rows(); i++)
			vec.set(m_values[i][col], i);
		return vec;
	}
	
	public Vector row(int row){
		_check();
		Vector vec = Vector.getVector(cols());
		vec.set(m_values[row]);
		return vec;
	}
	
	public void swopRow(int r1, int r2){
		_check();
		for(int j = 0; j < m_values[0].length; j++){
			double _tmp = m_values[r1][j];
			m_values[r1][j] = m_values[r2][j];
			m_values[r2][j] = _tmp;
		}
	}
	
	public void swopCol(int c1, int c2){
		_check();
		for(int i = 0; i < m_values.length; i++){
			double _tmp = m_values[i][c1];
			m_values[i][c1] = m_values[i][c2];
			m_values[i][c2] = _tmp;
		}
	}
	
	public void copyRow(Matrix src, int row){
		_check(); src._check();
		if(cols() != src.cols())
			throw new MatrixDimensionException();
		System.arraycopy(src.m_values[row], 0, m_values[row], 0, cols());		
	}
	
	public void copyRow(Vector src, int row){
		_check();
		if(cols() != src.rows())
			throw new MatrixDimensionException();
		System.arraycopy(src.get(), 0, m_values[row], 0, cols());		
	}
	
	public double sumRow(int rowIndex){
		_check();
		double sum = 0.0;
		for(int j = 0; j < m_values[rowIndex].length; j++)
			sum += m_values[rowIndex][j];
		return sum;
	}
	
	public double sumColumn(int colIndex){
		_check();
		double sum = 0.0;
		for(int i = 0; i < m_values.length; i++)
			sum += m_values[i][colIndex];
		return sum;
	}
	
	public double maxEntry(){
		double max = -Double.MAX_VALUE;
		for(int i = 0; i < m_values.length; i++){
			for(int j = 0; j < m_values[i].length; j++){
				max = (max < m_values[i][j])?m_values[i][j]:max;
			}
		}
		return max;
	}
	
	public double minEntry(){
		double min = Double.MAX_VALUE;
		for(int i = 0; i < m_values.length; i++){
			for(int j = 0; j < m_values[i].length; j++){
				min = (min > m_values[i][j])?m_values[i][j]:min;
			}
		}
		return min;
	}
	
	public double minRowEntry(int row){
		_check();
		double _min = m_values[row][0];
		for(int i = 1; i < rows(); i++)
			_min = (_min > m_values[row][i])?m_values[row][i]:_min;
		return _min;
	}
	
	public double minColEntry(int col){
		_check();
		double _min = m_values[0][col];
		for(int i = 1; i < cols(); i++)
			_min = (_min > m_values[i][col])?m_values[i][col]:_min;
		return _min;
	}
	
	public double maxRowEntry(int row){
		_check();
		double _max = m_values[row][0];
		for(int i = 1; i < rows(); i++)
			_max = (_max < m_values[row][i])?m_values[row][i]:_max;
		return _max;
	}
	
	public double maxColEntry(int col){
		_check();
		double _max = m_values[0][col];
		for(int i = 1; i < cols(); i++)
			_max = (_max < m_values[i][col])?m_values[i][col]:_max;
		return _max;
	}
	
	public Matrix release(){
		if(m_standalone)
			throw new MatrixLockedException("May not release a standalone Matrix");
		clear();
		locked = true;
		s_matrixMap.get(getKey(m_values.length, m_values[0].length)).add(this);
		return null;
	}
	
	public void clear(){
		_check();
		for(int i = 0; i < m_values.length; i++)
			for(int j = 0; j < m_values[i].length; j++)
				m_values[i][j] = 0.0;
	}
	
	public void identity(){
		_check();
		if(rows()!=cols())
			throw new MatrixDimensionException();
		clear();
		for(int i = 0; i < rows(); i++)
			m_values[i][i] = 1.0;
	}
	
	/**
	 * 
	 *  [return new] = [this]x[matrix]
	 */
	public Matrix multiply(Matrix matrix){
		_check(); matrix._check();
		Matrix result = getMatrix(rows(), matrix.cols());
		double[][] a = m_values;
		double[][] b = matrix.m_values;
		double _r = 0.0;
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < b[0].length; j++){
				for(int k = 0; k < a[0].length; k++){
					_r += a[i][k]*b[k][j];
				}
				result.set(_r, i, j);
				_r = 0.0;
			}
		}
		return result;
	}
	
	public Matrix prePostTMultiply(Matrix matrix){
		_check(); matrix._check();
		Matrix tmp = matrix.multiply(this);
		Matrix transpose = matrix.transpose();
		Matrix result = tmp.multiply(transpose);
		tmp.release();
		transpose.release();
		return result;
	}
	
	/**
	 * 
	 *  [return new] = [this]x[vector]
	 */
	public Vector multiply(Vector vector){
		_check(); vector._check();
		if(cols() != vector.rows())
			throw new VectorDimensionException("Length of vector = " +vector.rows()+ " should be "+cols());
		Vector result = Vector.getVector(rows());
		double[][] a = m_values;
		double[] b = vector.get();
		double _r = 0.0;
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				_r += a[i][j]*b[j];
			}
			result.set(_r, i);
			_r = 0.0;
		}
		return result;
	}
	
	
	public Matrix inverse2x2(){
		_check();
		if(rows() != 2 || cols() != 2)
			throw new MatrixDimensionException("only support 2x2 inverse calculations at the moment");
		Matrix inv = getMatrix(2, 2);
		inv.set( m_values[1][1], 0, 0);
		inv.set(-m_values[0][1], 0, 1);
		inv.set(-m_values[1][0], 1, 0);
		inv.set( m_values[0][0], 1, 1);
		inv.scale(1./determinant());
		return inv;
	}
	
	
	// the first row contains the eigen values, 
	// the second row the eigen vector that corresponds to the first eigen value
	// and the third row the eigen vector that corresponds to the second eigen value
	public Vector getEigenValues2x2(){
		_check();
		Vector ev = Vector.getVector(2);
		
		// http://en.wikipedia.org/wiki/Eigenvalue_algorithm
		// http://www.math.harvard.edu/archive/21b_fall_04/exhibits/2dmatrices/index.html
		// lamda = (a+d)/2 +- sqrt(4*b+(a-d)²)/2
		// | a  b |
		// | c  d |
		
		double a = m_values[0][0];  double b = m_values[0][1];
		double c = m_values[1][0];  double d = m_values[1][1];

		double _trace = a + d;
		double _deter = a*d - b*c;
		double sqrt = Math.sqrt(_trace*_trace /4. - _deter);
		double l1 = _trace/2 + sqrt;
		double l2 = _trace/2 - sqrt;

		ev.set(l1,0);
		ev.set(l2,1);
		return ev;
	}
	
	public Vector[] getEigenVectors2x2(){	
		double a = m_values[0][0];  double b = m_values[0][1];
		double c = m_values[1][0];  double d = m_values[1][1];

		double _trace = a + d;
		double _deter = a*d - b*c;
		double sqrt = Math.sqrt(_trace*_trace /4. - _deter);
		double l1 = _trace/2 + sqrt;
		double l2 = _trace/2 - sqrt;
		
		Vector[] _ev = new Vector[2];
		_ev[0] = Vector.getVector(2);
		_ev[1] = Vector.getVector(2);
		
		if(c != 0){
			_ev[0].set(l1 - d,0);
			_ev[0].set(c,1);
			_ev[1].set(l2 - d,0);
			_ev[1].set(c,1);
		}
		else if(m_values[0][1] != 0){
			_ev[0].set(b,0);
			_ev[0].set(l1-a,1);
			_ev[1].set(b,0);
			_ev[1].set(l2-a,1);			
		} else {
			_ev[0].set(1.0, 0);
			_ev[1].set(1.0, 1);
		}
		_ev[0].normalize();
		_ev[1].normalize();
		_ev[0].scale(l1);
		_ev[1].scale(l2);
		return _ev;
	}
	
	
	public EigenvalueDecomposition getEigenValueDecomposition(){
		return new EigenvalueDecomposition(this);
	}
	
	/**
	 * @return The eigenvalues of the matrix as the elements of a Vector
	 */
	public Vector eigenValues(){
		EigenvalueDecomposition ed = new EigenvalueDecomposition(this);
		return ed.getRealEigenvalues();
	}
	
	/**
	 * @return The eigenvalues of the matrix in diagonal-matrix form
	 */
	public Matrix eigenMatrix(){
		EigenvalueDecomposition ed = new EigenvalueDecomposition(this);
		return ed.getD();
	}
	
	/**
	 * @return The eigenvectors of the matrix as the columns of the returned Matrix
	 */
	public Matrix getEigenVectors(){
		EigenvalueDecomposition ed = new EigenvalueDecomposition(this);
		return ed.getEigenVectors();		
	}
	
	
	
    public double determinant(){    
		_check();
       if (rows() > 3)
			throw new MatrixDimensionException("Only support up to 3x3 determinant calculations.");
       if ((rows() != cols()))
			throw new MatrixDimensionException("Determinant only for square matrices up to 3x3.");
        if (rows() == 1){
            return m_values[0][0];
        }
        else if (rows() == 2){
    		return m_values[0][0]*m_values[1][1]-m_values[0][1]*m_values[1][0];
        }
        else {   // 3x3 matrix
            double t1 = m_values[0][0] * (m_values[1][1] * m_values[2][2] - m_values[1][2] * m_values[2][1]);
            double t2 = m_values[0][1] * (m_values[1][0] * m_values[2][2] - m_values[1][2] * m_values[2][0]);
            double t3 = m_values[0][2] * (m_values[1][0] * m_values[2][1] - m_values[1][1] * m_values[2][0]);
            return (t1 - t2 + t3);
         }
      }
	
	public Matrix transpose(){
		_check();
		Matrix transpose = getMatrix(cols(), rows());
		for(int i = 0; i < m_values.length; i++)
			for(int j = 0; j < m_values[i].length; j++)
				transpose.m_values[j][i] = m_values[i][j];
		return transpose;
	}
	
	public void scale(double value){
		_check();
		for(int i = 0; i < m_values.length; i++)
			for(int j = 0; j < m_values[i].length; j++)
				m_values[i][j] *= value;
	}
	
	public void add(Matrix matrix){
		_check(); matrix._check();
		if(rows() != matrix.rows() || cols() != matrix.cols())
			throw new MatrixDimensionException();
		
		for(int i = 0; i < m_values.length; i++)
			for(int j = 0; j < m_values[i].length; j++)
				m_values[i][j] += matrix.m_values[i][j];
	}
	
	
	
	public void addSubmatrix(Matrix mat, int[] indices){
		_check(); mat._check();
		if(mat.rows() != indices.length || mat.cols() != indices.length)
			throw new MatrixDimensionException();
		for(int i = 0; i < mat.rows(); i++){
			for(int j = 0; j < mat.cols(); j++){
				m_values[indices[i]][indices[j]] += mat.m_values[i][j];
			}
		}		
	}
	
	public void addSubmatrix(Matrix mat, int[] rows, int[] cols){
		_check(); mat._check();
		if(mat.rows() != rows.length || mat.cols() != cols.length)
			throw new MatrixDimensionException();
		for(int i = 0; i < mat.rows(); i++){
			for(int j = 0; j < mat.cols(); j++){
				m_values[rows[i]][cols[j]] += mat.m_values[i][j];
			}
		}		
	}
	
	public Matrix getSubMatrix(boolean[] indices){		
		_check();
		if(rows() != indices.length || cols() != indices.length)
			throw new MatrixDimensionException();
		int dim = 0;
		for(int i = 0; i < indices.length; i++){
			if(indices[i])
				dim++;
		}
		Matrix _sub = Matrix.getMatrix(dim, dim);
		int row = 0;
		for(int i = 0; i < indices.length; i++){
			if(!indices[i])
				continue;
			int col = 0;
			for(int j = 0; j < indices.length; j++){
				if(!indices[j])
					continue;
				_sub.set(m_values[i][j], row, col);
				col++;
			}
			row++;
		}
		return _sub;		
	}
	
	public Matrix getSubMatrix(boolean[] rows, boolean[] cols){		
		_check();
		if(rows() != rows.length || cols() != cols.length)
			throw new MatrixDimensionException();
		int _rows = 0;
		for(int i = 0; i < rows.length; i++){
			if(rows[i])
				_rows++;
		}
		int _cols = 0;
		for(int j = 0; j < cols.length; j++){
			if(cols[j])
				_cols++;
		}
		Matrix _sub = Matrix.getMatrix(_rows, _cols);
		int row = 0;
		for(int i = 0; i < rows(); i++){
			if(!rows[i])
				continue;
			int col = 0;
			for(int j = 0; j < cols(); j++){
				if(!cols[j])
					continue;
				_sub.set(m_values[i][j], row, col);
				col++;
			}
			row++;
		}
		return _sub;		
	}
	
	public Matrix getSubMatrix(int[] indices){		
		_check();
		if(rows() < indices.length || cols() < indices.length)
			throw new MatrixDimensionException();
		for(int i = 0; i < indices.length; i++){
			if(indices[i] >= rows())
				throw new MatrixDimensionException("Row index > number of rows : ("+indices[i] + " > "+rows()+")");
		}
		for(int j = 0; j < indices.length; j++){
			if(indices[j] >= cols())
				throw new MatrixDimensionException("Col index > number of cols : ("+indices[j] + " > "+cols()+")");
		}
		Matrix _sub = Matrix.getMatrix(indices.length, indices.length);
		for(int i = 0; i < indices.length; i++){			
			for(int j = 0; j < indices.length; j++){				
				_sub.set(m_values[indices[i]][indices[j]],i,j);
			}
		}
		return _sub;		
	}
	
	public Matrix getSubMatrix(int[] rows, int[] cols){		
		_check();
		if(rows() < rows.length || cols() < cols.length)
			throw new MatrixDimensionException();
		for(int i = 0; i < rows.length; i++){
			if(rows[i] >= rows())
				throw new MatrixDimensionException("Row index > number of rows : ("+rows[i] + " > "+rows()+")");
		}
		for(int j = 0; j < cols.length; j++){
			if(cols[j] >= cols())
				throw new MatrixDimensionException("Col index > number of cols : ("+cols[j] + " > "+cols()+")");
		}
		Matrix _sub = Matrix.getMatrix(rows.length, cols.length);
		for(int i = 0; i < rows.length; i++){
			for(int j = 0; j < cols.length; j++){
				_sub.set(m_values[rows[i]][cols[j]],i ,j );
			}
		}
		return _sub;
	}
	
	public void replaceSubmatrix(Matrix mat, int[] indices){
		_check(); mat._check();
		if(mat.rows() != indices.length || mat.cols() != indices.length)
			throw new MatrixDimensionException();
		for(int i = 0; i < mat.rows(); i++){
			for(int j = 0; j < mat.cols(); j++){
				m_values[indices[i]][indices[j]] = mat.m_values[i][j];
			}
		}		
	}
	
	public void replaceSubmatrix(Matrix mat, int[] rows, int[] cols){
		_check(); mat._check();
		if(mat.rows() != rows.length || mat.cols() != cols.length)
			throw new MatrixDimensionException();
		for(int i = 0; i < mat.rows(); i++){
			for(int j = 0; j < mat.cols(); j++){
				m_values[rows[i]][cols[j]] = mat.m_values[i][j];
			}
		}		
	}
	
	public boolean isSymmetric(){
		_check();
		if(rows()!=cols())
			throw new MatrixDimensionException("Matrix not square!");
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < i; j++){
				if(m_values[i][j] != m_values[j][i])
					return false;
			}
		}
		return true;
	}
	
	public boolean isSquare(){
		_check();
		if(rows() != cols())
			return false;
		return true;
	}
	
    public Matrix inverse() {
		// by gcvr.
		//
		// SYMBOLS:
		// -------
		//
		// matrix ...... square matrix
		// dimension ...... size (rows/columns) of matrix matrix
		// inverseMatrix ...... inverse of matrix
		//
		// Isym ... flag for symmetry of matrix matrix (1 = symmetric)
		// Iwlpvt.. 0 for no pivoting, 1 for pivoting
		//
		// eps..... tolerance to identify a singular matrix
		// tol..... tolerance for the residuals
		//
		// l ...... lower triangular matrix
		// u ...... upper triangular matrix
		// det .... determinant (det(matrix)=det(l)*det(u))
		// Istop... flag: Istop = 1 if something is wrong
		//
		// pivot .. absolute value of pivot candidates
		// ipv..... location of pivotal element
		// icount . counter for number of row interchanges
		// ---------------------------------------------------------------
		_check();
		if (!isSquare())
			throw new MatrixDimensionException("invalid matrix dimensions");

		Matrix inverse = getMatrix(rows(), cols());

		boolean doPivoting = true;
		int dimension = rows();

		double[][] c = new double[dimension][2 * dimension]; // (500,1000),
		double[][] u = new double[dimension][dimension]; // (500,500)
		double[][] l = new double[dimension][dimension]; // (500,500)

		// c----------
		// c initialize
		// c-----------
		int icount = 0; // ! counts row interchanges
		// c--------
		// c prepare
		// c--------
		int na = dimension - 1;
		int nn = 2 * dimension;
		// c----------------------------------------------
		// c Initialize l and define the extended matrix c
		// c----------------------------------------------
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				l[i][j] = 0.0;
				c[i][j] = m_values[i][j];
			}
			for (int j = 0; j < dimension; j++) {
				c[i][dimension + j] = 0.0;
			}
			c[i][dimension + i] = 1.0;
		}
		// ----------------------
		// begin row reductions
		// ----------------------
		for (int m = 0; m < na; m++) {// outer loop for working row
			int ma = m - 1;
			int m1 = m + 1;
			if (doPivoting) {// Go to 97 to skip pivoting  ( ;-) good old basic?) 
				// -----------------------------
				// Pivoting:
				// begin by searching column i
				// for largest element
				// -----------------------------
				int ipv = m;
				double pivot = Math.abs(c[m][m]);
				for (int j = m1; j < dimension; j++) {
					if ((Math.abs(c[j][m])) > pivot) {
						ipv = j;
						pivot = Math.abs(c[j][m]);
					}
				}
				if (isZero(pivot))
					throw new MatrixException(
							"Singular matrix cannot be inverted");
				// ---
				// switch the working row with the row containing the
				// pivot element (also switch rows in l)
				// ---
				if (ipv != m) {
					for (int j = m; j < nn; j++) {
						double save = c[m][j];
						c[m][j] = c[ipv][j];
						c[ipv][j] = save;
					}
					for (int j = 0; j < ma; j++) {
						double save = l[m][j];
						l[m][j] = l[ipv][j];
						l[ipv][j] = save;
					}
					icount = icount + 1;
				}
			}
			// ---------------------------------------
			// reduce column i beneath element c(m,m)
			// ---------------------------------------
			for (int i = m1; i < dimension; i++) {
				l[i][m] = c[i][m] / c[m][m];
				c[i][m] = 0.0;
				for (int j = m1; j < nn; j++) {
					c[i][j] = c[i][j] - l[i][m] * c[m][j];
				}
			}
		}
		// --------------------------------
		// check the last diagonal element
		// for singularity
		// -------------------------------
		if (isZero(c[dimension - 1][dimension - 1]))
			throw new MatrixException("Singular matrix cannot be inverted");
		// ----------------------
		// complete the matrix l
		// ----------------------
		for (int i = 0; i < dimension; i++) {
			l[i][i] = 1.0;
		}
		// --------------------
		// define the matrix u
		// --------------------x
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				u[i][j] = c[i][j];
			}
		}
		// ------------------------------------
		// perform back-substitution to solve
		// the reduced system
		// using the upper triangular matrix c
		// ------------------------------------
		for (int ll = 0; ll < dimension; ll++) {
			inverse.m_values[dimension - 1][ll] = c[dimension - 1][dimension + ll]
					/ c[dimension - 1][dimension - 1];

			for (int i = na - 1; i >= 0; i--) {
				double sum = c[i][dimension + ll];
				for (int j = i + 1; j < dimension; j++) {
					sum = sum - c[i][j] * inverse.m_values[j][ll];
				}
				inverse.m_values[i][ll] = sum / c[i][i];
			}
		}
		return inverse;
	} 
	
    
	public Matrix choleski(){
		_check();
		if(!isSymmetric()){
			throw new RuntimeException("Matrix is not symmetrical");
		}
		Matrix w = Matrix.getMatrix(rows(), cols());
		for(int i = 0; i < rows(); i++){
			for(int m = 0; m <= i; m++){
				if(i == m){ // diagonal term
					double sum = 0.0;
					for(int k = 0; k < i; k++) { // dot product
						sum += w.m_values[i][k]*w.m_values[i][k];
					}
					w.m_values[i][i] = Math.sqrt(m_values[i][i] - sum); 
				}
				else { 
					double sum = 0.0;
					for(int k = 0; k < i; k++){
						sum += w.m_values[i][k]*w.m_values[m][k];
					}
					w.m_values[i][m] = (m_values[i][m] - sum)/w.m_values[m][m];
				}			
			}
		}
		return w;
	}
	
	public Matrix clone(){
		Matrix clone = getMatrix(rows(), cols());
		for(int i = 0; i < m_values.length; i++)
			for(int j = 0; j < m_values[i].length; j++)
				clone.m_values[i][j] = m_values[i][j];
		return clone;
	}
	
	public String toString(){
		_check();
		StringBuilder sb = new StringBuilder("{");
		for(int i = 0; i < m_values.length; i++){
			if(i > 0)
				sb.append(" ");
			sb.append("{");
			for(int j = 0; j < m_values[i].length; j++){
				sb.append(m_values[i][j]);
				if(j < m_values[i].length-1)
					sb.append(", ");
			}
			if(i < m_values.length-1)
				sb.append("},\n");
			else
				sb.append("}");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public void print(String name, PrintStream stream){
		_check();
		stream.println(name);
		for(int i = 0; i < m_values.length; i++){
			for(int j = 0; j < m_values[i].length; j++)
				stream.print(m_values[i][j]+"\t");
			stream.println();
		}
		stream.println();
	}
		
	public void print(String name){
		print(name,System.out);
	}
	
	private String format(double d, NumberFormat f, int length){
		String s = f.format(d)+" ";
		while(s.length() < length){
				s = " "+s;
		}
		return s;
	}
	
	private void print(PrintStream stream, int offset, int length, int num){
		if(stream == null)
			return;
		for(int i = 0; i < offset; i++)
			stream.print(" ");
		stream.print("+");
		for(int i = 0; i < num; i++){
			for(int j = 0; j < length; j++)
				stream.print("-");
			stream.print("+");
		}
		stream.print("\n");
	}
	
	public void printf(String name, PrintStream stream){
		if(stream == null)
			return;
		_check();		
		double max = maxEntry();
		double min = minEntry();
		NumberFormat f;
		if(max > 1e8)
			f = new DecimalFormat("0.000E0");
		else if(max > 1000)
			f = new DecimalFormat("0");
		else if(max > 100)
			f = new DecimalFormat("0.0");
		else if(max > 10)
			f = new DecimalFormat("0.00");
		else if(max > 1)
			f = new DecimalFormat("0.000");
		else if(max > 1e-2)
			f = new DecimalFormat("0.0000");
		else
			f = new DecimalFormat("0.000E0");
		
		int length = Math.max(f.format(max).length(), f.format(min).length())+2;
		print(stream,name.length()+3,length,cols());
		int counter = 0;
		for(int i = 0; i < m_values.length; i++){
			if(counter == rows()-1){
				stream.print(name+" = |");
			}			
			else{
				for(int k = 0; k < name.length() + 3; k++)
					stream.print(" ");
				stream.print("|");
			}
			counter++;
			for(int j = 0; j < m_values[i].length; j++)
				stream.print(format(m_values[i][j],f,length)+"|");
			stream.println();
			if(counter == rows()-1){
				stream.print(name+" = ");
				print(stream,0,length,cols());
			}
			else{
				print(stream,name.length()+3,length,cols());
			}
			counter++;
		}
		stream.println();
	}
	
	public void printf(String name){
		printf(name,System.out);
	}
	
	//............Externalizable...........................
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(rows());
		out.writeInt(cols());
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < cols(); j++){
				out.writeDouble(m_values[i][j]);
			}
		}
	}
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
		int rows = in.readInt();
		int cols = in.readInt();
		m_values = new double[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				m_values[i][j] = in.readDouble();
			}
		}
	}	
	
	public static void main(String[] args) {
		
		// Submatrix
		Matrix sz = Matrix.getMatrix(8, 3);
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 8; j++) {
				sz.set(i, j, i-1);
			}
		}
		sz.printf("Sz");
		
		Matrix szFull = Matrix.getMatrix(24, 9);
		szFull.clear();
		
		int[] r1 = new int[]{0,3,6, 9,12,15,18,21};
		int[] r2 = new int[]{1,4,7,10,13,16,19,22};
		int[] r3 = new int[]{2,5,8,11,14,17,20,23};
		int[] c1 = new int[]{0,1,2};
		int[] c2 = new int[]{3,4,5};
		int[] c3 = new int[]{6,7,8};
		
		szFull.addSubmatrix(sz, r1, c1);
		szFull.addSubmatrix(sz, r2, c2);
		szFull.addSubmatrix(sz, r3, c3);
		
		szFull.printf("szFull");
		
		Matrix.stats(System.err);
		sz.release();
		szFull.release();
		Matrix.stats(System.err);
		Matrix.dump();
		Matrix.stats(System.err);
		
//		Matrix mat = Matrix.getMatrix("{{2,1/2,-.2} , {(3+1)/2.5, 5*10/3,4}  }").transpose();
//		mat.printf("mat");
//		mat.expand().printf("expanded mat");
		
		
//		Matrix a = getMatrix(2 , 2);
//		a.set(  1.0, 0, 0);
//		a.set( -1.0, 1, 0);
//		a.set( -1.0, 0, 1);
//		a.set( 1.01, 1, 1);
//		
//		a.getEigenValues2x2().print("eigen Values", System.out);
//		
//		Vector[] ev = a.getEigenVectors2x2();
//		ev[0].print("ev1", System.out);
//		ev[1].print("ev2", System.out);
//		
//		System.out.println(ev[0].abs());
//		System.out.println(ev[1].abs());
//		
//		if(true)
//			System.exit(0);
//		
//		Matrix cholesky = Matrix.getMatrix(3, 3);
//		
//		double[][] v = { {25,-5,10}, {-5,17,10.}, {10,10,62}};
//		cholesky.set(v);
//		cholesky.printf("Cholesky", System.out);		
//		cholesky.choleski().printf("Decomposition", System.out);
//		
//		Matrix inv = cholesky.inverse();
//		
//		inv.printf("inv", System.out);
//		
//		inv.multiply(cholesky).printf("check", System.out);
//		
//		inv.getSubMatrix(new boolean[]{true,false,true}).printf("sub", System.out);
//		
//		inv.getSubMatrix(new boolean[]{true,false,true},new boolean[]{true,false,true}).printf("sub", System.out);
//		
//		inv.getSubMatrix(new int[]{2,1,0}).printf("ja", System.out);
//		
//		inv.getSubMatrix(new int[]{1,1,1},new int[]{0,1,2}).printf("nee", System.out);
//		
//		if(true)
//			System.exit(0);
//		
//		 a = Matrix.getMatrix(4, 4);
//		Matrix b = Matrix.getMatrix(4, 2);
//		a.set(30, 0, 0);
//		
//		a.printf("A", System.out);
//		
//		a = a.release();
//		
//		b.set(30, 0, 0);
//				
//		Matrix c = Matrix.getMatrix(4, 4);
//		c.set(20, 0, 0);
//		c.printf("C", System.out);
//		
//		Vector vec = Vector.getVector(4);
//		vec.set(2, 0);
//		
//		Vector d = c.multiply(vec);
//		c.release();
//		
//		vec.printf("vec", System.out);
//		d.printf("[d]x{v}", System.out);
//				
//		b = b.release();
//
//		d.scale(.1);
//		d.printf("D", System.out);
//		
//		Matrix i = Matrix.getMatrix(3, 3);
//		i.identity();
//		i.printf("identity", System.out);
//		
//		stats(System.err);
//		Vector.stats(System.err);
//		vec.release();
//		Vector.stats(System.err);
		
	}
	
}
