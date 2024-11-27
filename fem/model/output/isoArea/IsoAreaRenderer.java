package fem.model.output.isoArea;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import math.linalg.Matrix;
import math.linalg.Vector;
import math.shapeFunctions.FourNodeQuad;
import math.shapeFunctions.ShapeFunction;
//import math.shapeFunctions.TwoDQuadraticTriangleShapeFunction_Isoparametric;
import util.gui.ZoomPanel;
import util.view.IsoArea;
import util.view.IsoEngine;
import util.view.TriaArea;
import fem.components.ConstantStrainTriangle;
import fem.components.Quad4Iso;
//import fem.components.Triangle6Iso;
import fem.components.Triangle;
import fem.interFace.IElement;
import fem.interFace.INode;
import fem.model.FemModel;
import fem.model.output.Recorder;
import fem.model.output.Renderer;
import fem.model.output.stresses.StressPoint;

public class IsoAreaRenderer implements Recorder, Renderer, Serializable {

	private static final long serialVersionUID = 1L;

	private IsoArea[] m_areas;
	
	private double m_lower;
	private double m_upper;
	
//	private final static ShapeFunction iso6 = new TwoDQuadraticTriangleShapeFunction_Isoparameteric();
	private final static ShapeFunction iso4 = new FourNodeQuad();
	
	public IsoAreaRenderer(){
		m_lower = Double.POSITIVE_INFINITY;
		m_upper = Double.NEGATIVE_INFINITY;
	}
	
	public IsoAreaRenderer(double lower, double upper){
		this.m_lower = lower;
		this.m_upper = upper;
	}
	
	public void record(FemModel model) {
		if(m_lower > m_upper){
			Vector primal = model.getPrimalVector();
			m_lower = primal.minEntry();
			m_upper = primal.maxEntry();
//			System.out.println("Lower, upper = "+m_lower+", "+m_upper);
		}
		// build triangles
		Iterator<IElement> elements = model.iterator(IElement.class);
		Set<TriaArea> trias = new HashSet<TriaArea>();
		while(elements.hasNext()){
			IElement elem = elements.next();
			if(elem instanceof Triangle){
				Matrix x = elem.computeX();
				Vector u = elem.computeU();
				Matrix tri = Matrix.getMatrix(3, 3);
				tri.copyRow(x, 0);
				tri.copyRow(x, 1);
				tri.copyRow(u, 2);	
				trias.add(new TriaArea(tri));
				x.release();
				u.release();
				tri.release();
			}else if(elem instanceof ConstantStrainTriangle){
				Matrix x = elem.computeX();
				Vector u = elem.computeU();
				
				// det max disp at each node
				Vector uResultant = Vector.getVector(3);
				uResultant.set(u.getSubVector(new int[] {0,1}).abs(), 0);
				uResultant.set(u.getSubVector(new int[] {2,3}).abs(), 1);
				uResultant.set(u.getSubVector(new int[] {4,5}).abs(), 2);
				Matrix tri = Matrix.getMatrix(3, 3);
				tri.copyRow(x, 0);
				tri.copyRow(x, 1);
				tri.copyRow(uResultant, 2);	
				trias.add(new TriaArea(tri));
				x.release();
				u.release();
				tri.release();
			}
//			else if(elem instanceof Triangle6Iso){
//				triangulate(trias,(Triangle6Iso)elem,5);
//			}
			else if(elem instanceof Quad4Iso){
				triangulate(trias, (Quad4Iso)elem,8);
			}
			else {
				throw new RuntimeException("Element of type "+elem.getClass().getName()+" not supported!");
			}			
		}		
		m_areas = IsoEngine.isoAreas(trias, 6, m_lower, m_upper);
		Iterator<TriaArea> iter = trias.iterator();
		while(iter.hasNext())
			iter.next().release();
	}

	public Rectangle2D bounds() {
		Rectangle2D _bounds = m_areas[0].outline.getBounds2D();
		for(int i = 1; i < m_areas.length; i++)
			_bounds.add(m_areas[i].outline.getBounds2D());		
		return _bounds;
	}

	public void render(Graphics2D g2d, ZoomPanel zp) {
		Color save = g2d.getColor();
		for(int i = 0; i < m_areas.length; i++){
			g2d.setColor(m_areas[i].color);
			g2d.fill(zp.transformShapeToPanel(m_areas[i].outline));			
		}
		g2d.setColor(save);
	}
	
	
//	private void triangulate(Set<TriaArea> set, Triangle6Iso elem, int step) {		
//		Matrix x = elem.computeX();
//		Vector u = elem.computeU();
//		Vector z = Vector.getVector(2);
//		Matrix _mat = Matrix.getMatrix(3, 3);
//		for(int i = 0; i < step; i++){
//			for(int j = step-1;j>=i; j--){		
//				z.set((i*1.)/(step), 0);
//				z.set((j*1.)/(step)-z.get(0), 1);				
//				Vector s = iso6.computeS(z);
//				
//				Vector x0 = x.row(0);
//				Vector x1 = x.row(1);
//				
//				_mat.set(s.dot(x0), 0, 0);
//				_mat.set(s.dot(x1), 1, 0);
//				_mat.set(s.dot(u), 2, 0);
//				
//				s.release();
//				
//				z.set((i+1.)/(step), 0);	
//				s = iso6.computeS(z);
//				_mat.set(s.dot(x0), 0, 1);
//				_mat.set(s.dot(x1), 1, 1);
//				_mat.set(s.dot(u), 2, 1);
//				
//				z.set((i*1.)/(step), 0);
//				z.set((j+1.)/(step)-z.get(0), 1);	
//				s = iso6.computeS(z);
//				_mat.set(s.dot(x0), 0, 2);
//				_mat.set(s.dot(x1), 1, 2);
//				_mat.set(s.dot(u), 2, 2);
//				set.add(new TriaArea(_mat));				
//			}
//		}
//		
//		for(int i = 0; i < step-1; i++){
//			for(int j = step-1;j>=i+1; j--){
//				z.set((i*1.)/(step), 0);
//				z.set((j*1.)/(step)-z.get(0), 1);				
//				Vector s = iso6.computeS(z);
//				
//				Vector x0 = x.row(0);
//				Vector x1 = x.row(1);
//				
//				_mat.set(s.dot(x0), 0, 0);
//				_mat.set(s.dot(x1), 1, 0);
//				_mat.set(s.dot(u), 2, 0);
//				
//				s.release();
//				
//				z.set((i+1.)/(step), 0);	
//				s = iso6.computeS(z);
//				_mat.set(s.dot(x0), 0, 1);
//				_mat.set(s.dot(x1), 1, 1);
//				_mat.set(s.dot(u), 2, 1);
//				
//				z.set((i+1.)/(step), 0);
//				z.set((j*1.)/(step)-z.get(0), 1);	
//				s = iso6.computeS(z);
//				_mat.set(s.dot(x0), 0, 2);
//				_mat.set(s.dot(x1), 1, 2);
//				_mat.set(s.dot(u), 2, 2);
//				set.add(new TriaArea(_mat));
//			}
//		}
//	}
		
	private void triangulate(Set<TriaArea> set, Quad4Iso elem, int n) {
		Matrix x = elem.computeX(); // x coordinates of the element
		Vector u = elem.computeU(); // nodal values
		Matrix z = Matrix.getMatrix(2, 4);
		Matrix tmp = Matrix.getMatrix(3, 4);
		int[] _r = {0,1,2};
		int[] _l1 = {0,1,3};
		int[] _u1 = {1,2,3};
		int[] _l2 = {0,2,3};
		int[] _u2 = {0,1,2};
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				Vector _x0 = x.row(0);
				Vector _x1 = x.row(1);
				
				z.set(2.*i/n-1, 0, 0);
				z.set(2.*j/n-1, 1, 0);

				z.set(2.*(i+1)/n-1, 0 , 1);
				z.set(2.*(j)/n-1, 1 , 1);
				
				z.set(2.*(i+1)/n-1, 0 , 2);
				z.set(2.*(j+1)/n-1, 1 , 2);
				
				z.set(2.*(i)/n-1, 0 , 3);
				z.set(2.*(j+1)/n-1, 1 , 3);

				for(int k = 0; k < 4; k++){
					Vector s = iso4.computeS(z.col(k));
					tmp.set(s.dot(_x0), 0, k);
					tmp.set(s.dot(_x1), 1, k);
					tmp.set(s.dot(u),   2, k);
				}
				Matrix m1,m2;
				if((i+j)%2 == 0){					
					set.add(new TriaArea(m1 = tmp.getSubMatrix(_r, _u1)));
					set.add(new TriaArea(m2 = tmp.getSubMatrix(_r, _l1)));
				}
				else {
					set.add(new TriaArea(m1 = tmp.getSubMatrix(_r, _u2)));
					set.add(new TriaArea(m2 = tmp.getSubMatrix(_r, _l2)));
				}
				m1.release(); m2.release();
			}
		}
	}

}
