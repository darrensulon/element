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


public class Vector implements Externalizable {
	
	private static final long serialVersionUID = 1L;
	private static Set<Vector> s_vectorSet = new HashSet<Vector>();
	private static Map<String, Set<Vector>> s_vectorMap = new HashMap<String, Set<Vector>>();
	private static Map<String, Integer> s_couterMap = new HashMap<String, Integer>();
	
	private static boolean verbose = false;
	
	private static String getKey(int rows){
		return "["+rows+"]";
	}
	public synchronized static Vector getVector(int rows){
		String key = getKey(rows);
		if(!s_vectorMap.containsKey(key)){
			s_vectorMap.put(key, new HashSet<Vector>());
			s_couterMap.put(key, 0);
		}
		Set<Vector> set = s_vectorMap.get(key);
		if(set.isEmpty()){
			s_couterMap.put(key, s_couterMap.get(key)+1);
			Vector vec = new Vector(rows);
			vec.m_standalone = false;
			if(verbose)System.err.println("Vector ["+rows+"] created!");
			s_vectorSet.add(vec);
			return vec;
		}
		else{
			Iterator<Vector> iter = set.iterator();
			Vector vec = iter.next();
			iter.remove();
			vec.locked = false;
			vec.m_standalone = false;
			return vec;
		}
	}
	
	public synchronized static Vector getVector(int rows, boolean standalone){
		if(standalone)
			return new Vector(rows);
		else
			return getVector(rows);
	}
	
	public static void stats(PrintStream stream){
		Iterator<Map.Entry<String,Set<Vector>>> iter = s_vectorMap.entrySet().iterator();
		stream.println("\nVector service stats...");
		int available = 0;
		while(iter.hasNext()){
			Map.Entry<String, Set<Vector>> entry = iter.next();
			int _total = s_couterMap.get(entry.getKey());
			stream.println(entry.getKey() +"\t("+ entry.getValue().size() +
					" of "+ _total+") available");
			available += entry.getValue().size();
		}
		stream.println("Total number of vectors = "+s_vectorSet.size()+"\t["+Math.round(available*100./s_vectorSet.size())+"% available]\n");
	}
	
	private static final String VALUE = "\\-?\\d*\\.?\\d";
	private static final String OPR = "\\s*[\\+|\\*|\\-|\\/]\\s*";  
	private static final String EXPR = "[(|"+VALUE+"]+[[("+VALUE+OPR+"]|"+OPR+VALUE+"|)]*";
	private static final String VEC_STRING = "\\{["+EXPR+"|"+VALUE+"][\\s*,\\s*["+EXPR+"|"+VALUE+"]]*\\}";

	
	public static Vector getVector(String values, boolean standalone){
		if(values.matches(VEC_STRING)){
			StringTokenizer st = new StringTokenizer(values.substring(1,values.length()-1),",");
			Vector vec = getVector(st.countTokens());
			for(int i = 0; i < vec.size(); i++){
				String val = st.nextToken().trim().replace(" ", "");
				if(val.matches(VALUE))
					vec.m_values[i] = Double.parseDouble(val);
				else if(val.matches(EXPR)){
					try {
						vec.m_values[i] = InlineCalculator.calc("="+val);
					} catch (InvalidParameterException e) {
						throw new VectorDimensionException("Invalid vector format : " +val);
					}
				}
			}
			return vec;
		}
		else
			throw new VectorDimensionException("Invalid vector format : " +values);
	}
	
	public static Vector getVector(String values){
		return getVector(values,false);
	}
	
	public static void dump() {
		if(verbose)
			System.err.println("Dumping " + s_vectorSet.size() + " vectors.");
		int count = 0;
		Iterator<Vector> iter = s_vectorSet.iterator();
		while(iter.hasNext())
			if(!iter.next().locked)
				count++;
		// strict has not been released cannot be dumped policy
		if(count > 0)
			throw new VectorLockedException("Vectors being dumped " + s_vectorSet.size() +
					", of which " + count + " has not yet been released.");
		s_vectorSet.clear();
		s_couterMap.clear();
		s_vectorMap.clear();
	}
	
	// end of static stuff.
	
	private double[] m_values;
	private boolean locked = false;
	private boolean m_standalone = false;
	
	public Vector(){}
	
	private Vector(int rows){
		m_values = new double[rows];
		m_standalone = true;
	}
	
	public int size(){
		return m_values.length;
	}
		
	protected void _check(){
		if(locked)
			throw new VectorLockedException();
	}
	
	public double maxEntry(){
		_check();
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < m_values.length; i++)
			max = (max < m_values[i])?m_values[i]:max;
		return max;
	}
	
	public double minEntry(){
		_check();
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i < m_values.length; i++)
			min = (min > m_values[i])?m_values[i]:min;
		return min;
	}

	public double aveEntry(){
		double ave = 0.0;
		for(int i = 0; i < m_values.length; i++)
			ave += m_values[i];
		return ave/m_values.length;
	}

	public int maxValidEntryIndex(){
		_check();
		double max = maxEntry();
		for(int i = 0; i < m_values.length; i++){
			if(max == m_values[i])
				return i;
		}
		System.err.println("max = "+max);
		printf("values ", System.err);
		throw new RuntimeException("This should not happen, sorry!");
	}
	
	public int minValidEntryIndex(){
		_check();
		double min = minEntry();
		for(int i = 0; i < m_values.length; i++){
			if(min == m_values[i])
				return i;
		}
		throw new RuntimeException("This should not happen, sorry!");
	}
	
	public void add(double value, int row){
		_check();
		m_values[row] += value;
	}
	
	public void set(double value, int row){
		_check();
		m_values[row] = value;
	}
	
	public void set(double[] values){
		_check();
		if(values.length != rows())
			throw new VectorDimensionException();
		System.arraycopy(values, 0, m_values, 0, rows());
	}
	
	
	public static Vector add(Vector v1, Vector v2){
		v1._check();v2._check();
		Vector sum = v1.clone();
		sum.add(v2);	
		return sum;
	}
	
	public static Vector subtract(Vector v1, Vector v2){
		v1._check();v2._check();
		Vector sum = v1.clone();
		sum.subtract(v2);	
		return sum;
	}
	
	public double get(int row){
		_check();
		if(row >= rows())
			throw new VectorDimensionException("invalid index "+row);
		return m_values[row];
	}
	
	public int rows(){
		_check();
		return m_values.length;
	}
	
	/**
	 * Clones the contents of this vector, expensive to call.
	 * 
	 * @return
	 */
	public double[] get(){
		_check();
		return m_values;//.clone();
	}
	
	public Vector release(){
		if(m_standalone)
			throw new VectorLockedException("May not release a standalone Vector");
		clear();
		locked = true;
		s_vectorMap.get(getKey(m_values.length)).add(this);
		return null;
	}
	
	public void clear(){
		_check();
		for(int i = 0; i < m_values.length; i++)
			m_values[i] = 0.0;
	}
	
	
	/**
	 * 
	 */
	public double dot(Vector vec){
		_check(); vec._check();
		double dot = 0.0;
		for(int i = 0; i < rows(); i++){
			dot +=  m_values[i] * vec.m_values[i];
		}		
		return dot;
	}
	
	public void multiply(Vector vec){
		_check(); vec._check();
		if(rows() != vec.rows())
			throw new VectorDimensionException();
		for(int i = 0; i < rows(); i++){
			m_values[i] *= vec.m_values[i];
		}		
	}
	
	public Vector multiply(Matrix mat){
		_check(); mat._check();
		if(rows() != mat.cols())
			throw new VectorDimensionException();
		Vector result = Vector.getVector(size());
		result.clear();
		for(int i = 0; i < mat.cols(); i++){
			for(int j = 0; j < mat.rows(); j++){				
				result.m_values[i] += m_values[j]*mat.get(j,i);
			}	
		}
		return result;
	}
	
	public double sum(){
		_check();
		double sum = 0.0;
		for(int i = 0; i < m_values.length; i++)
			sum += m_values[i];
		return sum;
	}
	
	public void scale(double value){
		_check();
		for(int i = 0; i < m_values.length; i++)			
			m_values[i] *= value;
	}
	
	public double abs(){
		_check();
		double abs = 0.0;
		for(int i = 0; i < rows(); i++)
			abs +=  m_values[i] * m_values[i];
		return Math.sqrt(abs);
	}
	
	public void normalize(){
		_check();
		double abs = abs();
		for(int i = 0; i < rows(); i++)
			m_values[i] /= abs;
	}
	
	public void ones(){
		_check();
		for(int i = 0; i < rows(); i++)
			m_values[i] = 1.0;
	}
	
	public Matrix product(Vector vec){
		_check(); vec._check();
		if(rows() != vec.rows())
			throw new VectorDimensionException();
		Matrix mat = Matrix.getMatrix(rows(), rows());
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < rows(); j++){
				mat.set(m_values[i]*vec.m_values[j], i, j);
			}
		}
		return mat;
	}
	
	public Vector cross(Vector vec){
		_check();
		if(vec.rows() != 3 || rows() != 3)
			throw new VectorDimensionException();		
		Vector cross = getVector(3);
		cross.set( m_values[1]*vec.m_values[2] - m_values[2]*vec.m_values[1] , 0);
		cross.set(-m_values[0]*vec.m_values[2] + m_values[2]*vec.m_values[0] , 1);
		cross.set( m_values[0]*vec.m_values[1] - m_values[1]*vec.m_values[0] , 2);
		return cross;
	}
	
	public void add(double value){
		_check();
		
		for(int i = 0; i < m_values.length; i++)			
			m_values[i] += value;
	}
	
	public void add(Vector vector){
		_check(); vector._check();
		if(rows() != vector.rows())
			throw new VectorDimensionException();
		
		for(int i = 0; i < m_values.length; i++)			
			m_values[i] += vector.m_values[i];
	}
	
	public void add(Vector vector, int[] indices){
		_check(); vector._check();
		if(indices.length != vector.rows())
			throw new VectorDimensionException();		
		for(int i = 0; i < vector.rows(); i++)			
			m_values[indices[i]] += vector.m_values[i];
	}
	
	public Vector getSubVector(int[] indices){		
		_check();
		if(rows() < indices.length )
			throw new MatrixDimensionException();
		for(int i = 0; i < indices.length; i++){
			if(indices[i] >= rows())
				throw new MatrixDimensionException("Row index > number of rows : ("+indices[i] + " > "+rows()+")");
		}
		Vector _sub = Vector.getVector(indices.length);
		for(int i = 0; i < indices.length; i++){					
			_sub.set(m_values[indices[i]],i);
		}
		return _sub;		
	}
	
	public Vector getSubVector(boolean[] indices){		
		_check();
		if(rows() < indices.length )
			throw new MatrixDimensionException();
		int counter = 0;
		for(int i = 0; i < indices.length; i++){
			if(indices[i]){
				counter++;
			}
		}
		Vector _sub = Vector.getVector(counter);	
		counter = 0;
		for(int i = 0; i < size(); i++) {
			if(indices[i]){
				_sub.m_values[counter++] = m_values[i];
			}
		}
		return _sub;		
	}
	
	public void subtract(Vector vector){
		_check(); vector._check();
		if(rows() != vector.rows())
			throw new VectorDimensionException();
		
		for(int i = 0; i < m_values.length; i++)			
			m_values[i] -= vector.m_values[i];
	}
	
	public void replace(Vector vector, boolean[] indices){
		_check(); vector._check();
		if(rows() != vector.rows() || indices.length != rows())
			throw new VectorDimensionException();
		
		for(int i = 0; i < m_values.length; i++)
			if(indices[i])
				m_values[i] = vector.m_values[i];		
	}
	
	public Vector clone(){
		_check();
		Vector clone = getVector(size());
		for(int i = 0; i < m_values.length; i++)
			clone.m_values[i] = m_values[i];
		return clone;
	}
	
	public Vector clone(boolean standalone){
		Vector clone = getVector(size(),standalone);
		for(int i = 0; i < m_values.length; i++)
			clone.m_values[i] = m_values[i];
		return clone;
	}
	
	public String toString(){
		_check();
		StringBuilder sb = new StringBuilder("{");
		for(int i = 0; i < m_values.length; i++){
			sb.append(m_values[i]);
			if(i < m_values.length-1)
				sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public void print(String name, PrintStream stream){
		_check();
		stream.print(name+"\t = [");
		for(int i = 0; i < m_values.length; i++){
			stream.print(m_values[i]);
			if(i < m_values.length -1)
				stream.print(", ");
		}
		stream.println("]");
	}
	
	
	private String format(double d, NumberFormat f, int length){
		String s = f.format(d)+" ";
		while(s.length() < length){
				s = " "+s;
		}
		return s;
	}
	
	private void print(PrintStream stream, int offset, int length){
		if(stream == null)
			return;
		for(int i = 0; i < offset; i++)
			stream.print(" ");
		stream.print("+");
		for(int j = 0; j < length; j++)
			stream.print("-");
		stream.print("+");
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
		else if(max > 1e-4)
			f = new DecimalFormat("0.0000");
		else
			f = new DecimalFormat("0.000E0");
		
		int length = Math.max(f.format(max).length(), f.format(min).length())+2;
		
		print(stream,name.length()+3,length);
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
				stream.print(format(m_values[i],f,length)+"|");
			stream.println();
			if(counter == rows()-1){
				stream.print(name+" = ");
				print(stream,0,length);
			}
			else{
				print(stream,name.length()+3,length);
			}
			counter++;
		}
		stream.println();
	}
	
	public void printf(String name){
		printf(name, System.out);
	}
	
	public static void print(String name, boolean[] fix, PrintStream ps){
		ps.print(name + " [");
		for(int i = 0; i < fix.length; i++){
			ps.print(fix[i]);
			if(i < fix.length-1)
				ps.print(", ");
		}
		ps.println("]");
	}
	
	public void print(String name){
		print(name, System.out);
	}
	
	public static void print(String name, int[] array, PrintStream ps){
		ps.print(name + " [");
		for(int i = 0; i < array.length; i++){
			ps.print(array[i]);
			if(i < array.length-1)
				ps.print(", ");
		}
		ps.println("]");
	}
	
	//............Externalizable...........................
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(rows());
		for(int i = 0; i < rows(); i++){
			out.writeDouble(m_values[i]);
		}
	}
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
		int rows = in.readInt();
		m_values = new double[rows];
		m_standalone = true;
		locked = false; 
		for(int i = 0; i < rows; i++){
			m_values[i] = in.readDouble();
		}
	}
	
	public static void main(String[] args) {
//		Vector a = Vector.getVector(4);
//		Vector b = Vector.getVector(4);
//		
//		a.set(30, 0);		
//		a.printf("A", System.out);
//		
//		a = a.release();
//		
//		b.set(30, 0);
//				
//		Vector c = Vector.getVector(4);
//		c.set(20, 0);
//		c.printf("C", System.out);
//		
//		b = b.release();
//		
//		stats(System.err);
//		
//		Vector v = Vector.getVector(2);
//		v.set(new double[]{1.23,-2.3});
//		Matrix m = Matrix.getMatrix(2, 2);
//		m.set(new double[][]{{1.2,0.54},{.32,1.654}});
//		v.printf("v", System.out);
//		m.printf("m", System.out);
//		v.multiply(m).printf("v×m", System.out);
		
		Vector v1 = Vector.getVector("{10/3, (1+2) / 3 +   5 * (3 -1)/10 ,1  ,54}");
		v1.printf("Toets");
		Vector.stats(System.err);
		v1.release();
		Vector.stats(System.err);
		Vector.dump();
		Vector.stats(System.err);
	}
	
}
