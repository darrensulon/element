package util.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class IsoEngine {

	// color matrix, contains the rgb values at specific intervals, used to interpolate step values
	private static final int[][] cmat = new int[][]{ 
			{78,0,178},
			{0,125,255},
			{0,255,0},
			{255,200,0},
			{255,0,0},
			{178,50,0},	
//			{240,240,240},
//			{140,140,140},
		};
	
	public static Shape[] isoLines(Set<TriaArea> set, int steps, double min, double max){
		return calculateIsoLines(set, steps, min, max);
	}
	
	public static Shape[] isoLines(Set<TriaArea> set, int steps){
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;	
		double u;
		// get value range
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			TriaArea tria = (TriaArea)iter.next();
			min = (min < (u = tria.min()))?min:u;
			max = (max > (u = tria.max()))?max:u;
		}
		return calculateIsoLines(set, steps, min, max);
	}
	
	public static IsoArea[] isoAreas(Set<TriaArea> set, int steps, double min, double max){
		return calculateIsoAreas(set, steps, min, max);
	}
	
	public static IsoArea[] isoAreas(Set<TriaArea> set, int steps){
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		
		// get value range
		Iterator iter = set.iterator();
		double u;
		while(iter.hasNext()){
			TriaArea tria = (TriaArea)iter.next();
			min = (min < (u = tria.min()))?min:u;
			max = (max > (u = tria.max()))?max:u;
		}
		return calculateIsoAreas(set, steps, min, max);
	}
	
	// calculates the isolines
	private static Shape[] calculateIsoLines(Set<TriaArea> triangles, int steps, double min, double max){
		long s = System.currentTimeMillis();
		Set _set = new HashSet();		
		double[] isoLineValues = new double[steps];
		GeneralPath[] isoLines = new GeneralPath[steps];		
		Point2D _pA = new Point2D.Double();
		Point2D _pB = new Point2D.Double();
		Point2D _pC = new Point2D.Double();
		
		for(int i=0; i < steps; i++){
			_set.clear();
			isoLineValues[i] = (i+1)*(max-min)/steps + min;
			Iterator iter = triangles.iterator();
			while(iter.hasNext()){
				TriaArea tria = (TriaArea)iter.next();
				
				_pA.setLocation(getInterpolatedPoint(tria.getX(0),tria.getX(1),isoLineValues[i],tria.getU(0),tria.getU(1)),
								getInterpolatedPoint(tria.getY(0),tria.getY(1),isoLineValues[i],tria.getU(0),tria.getU(1)));
				
				_pB.setLocation(getInterpolatedPoint(tria.getX(1),tria.getX(2),isoLineValues[i],tria.getU(1),tria.getU(2)),
								getInterpolatedPoint(tria.getY(1),tria.getY(2),isoLineValues[i],tria.getU(1),tria.getU(2)));
				
				_pC.setLocation(getInterpolatedPoint(tria.getX(2),tria.getX(0),isoLineValues[i],tria.getU(2),tria.getU(0)),
								getInterpolatedPoint(tria.getY(2),tria.getY(0),isoLineValues[i],tria.getU(2),tria.getU(0)));
				
				if(!Double.isNaN(_pA.getX())&&!Double.isNaN(_pB.getX())){
					_set.add(new Line2D.Double(_pA, _pB));
					continue;
				}
				if(!Double.isNaN(_pB.getX())&&!Double.isNaN(_pC.getX())){
					_set.add(new Line2D.Double(_pB, _pC));
					continue;
				}
				if(!Double.isNaN(_pC.getX())&&!Double.isNaN(_pA.getX())){
					_set.add(new Line2D.Double(_pC, _pA));
					continue;
				}
			}		
			isoLines[i] = createGeneralPathFormSetOfLines(_set);
		}
		System.err.println("isoline calc = "+(System.currentTimeMillis()-s)+" ms");
		return isoLines;
	}
	
	// calculates the isoAreas
	private static IsoArea[] calculateIsoAreas(Set<TriaArea> triangles, int steps, double min, double max){
		long s = System.currentTimeMillis();
		IsoArea[] isoAreas = new IsoArea[steps];
		Area area;
				
		Set _tmpBoundaryPoints = new HashSet();
		double[] isoAreaValues = new double[steps+1];
		
		isoAreaValues[0]=min;
		for(int i = 0; i < steps; i++)
			isoAreaValues[i+1] = (i+1)*(max-min)/steps + min;
		
		Point2D _n0  = new Point2D.Double();
		Point2D _n1  = new Point2D.Double();
		Point2D _n2  = new Point2D.Double();
		Point2D _pAl = new Point2D.Double();
		Point2D _pBl = new Point2D.Double();
		Point2D _pCl = new Point2D.Double();
		Point2D _pAh = new Point2D.Double();
		Point2D _pBh = new Point2D.Double();
		Point2D _pCh = new Point2D.Double();
		
		
		for(int i=0; i < steps; i++){
			
			area = new Area();
			Iterator iter = triangles.iterator();
			
			while(iter.hasNext()){
				_tmpBoundaryPoints.clear();
				
				TriaArea tria = (TriaArea)iter.next();
				
				_pAl.setLocation(getInterpolatedPoint(tria.getX(0),tria.getX(1),isoAreaValues[i],tria.getU(0),tria.getU(1)),
								getInterpolatedPoint(tria.getY(0),tria.getY(1),isoAreaValues[i],tria.getU(0),tria.getU(1)));
				
				_pBl.setLocation(getInterpolatedPoint(tria.getX(1),tria.getX(2),isoAreaValues[i],tria.getU(1),tria.getU(2)),
								getInterpolatedPoint(tria.getY(1),tria.getY(2),isoAreaValues[i],tria.getU(1),tria.getU(2)));
				
				_pCl.setLocation(getInterpolatedPoint(tria.getX(2),tria.getX(0),isoAreaValues[i],tria.getU(2),tria.getU(0)),
								getInterpolatedPoint(tria.getY(2),tria.getY(0),isoAreaValues[i],tria.getU(2),tria.getU(0)));
				
				_pAh.setLocation(getInterpolatedPoint(tria.getX(0),tria.getX(1),isoAreaValues[i+1],tria.getU(0),tria.getU(1)),
						getInterpolatedPoint(tria.getY(0),tria.getY(1),isoAreaValues[i+1],tria.getU(0),tria.getU(1)));
		
				_pBh.setLocation(getInterpolatedPoint(tria.getX(1),tria.getX(2),isoAreaValues[i+1],tria.getU(1),tria.getU(2)),
						getInterpolatedPoint(tria.getY(1),tria.getY(2),isoAreaValues[i+1],tria.getU(1),tria.getU(2)));
		
				_pCh.setLocation(getInterpolatedPoint(tria.getX(2),tria.getX(0),isoAreaValues[i+1],tria.getU(2),tria.getU(0)),
						getInterpolatedPoint(tria.getY(2),tria.getY(0),isoAreaValues[i+1],tria.getU(2),tria.getU(0)));				
		
				if(!Double.isNaN(_pAl.getX())) _tmpBoundaryPoints.add(_pAl);
				if(!Double.isNaN(_pBl.getX())) _tmpBoundaryPoints.add(_pBl);
				if(!Double.isNaN(_pCl.getX())) _tmpBoundaryPoints.add(_pCl);
				if(!Double.isNaN(_pAh.getX())) _tmpBoundaryPoints.add(_pAh);
				if(!Double.isNaN(_pBh.getX())) _tmpBoundaryPoints.add(_pBh);
				if(!Double.isNaN(_pCh.getX())) _tmpBoundaryPoints.add(_pCh);
				
				if(isoAreaValues[i]<=tria.getU(0) && tria.getU(0) <= isoAreaValues[i+1]){
					_n0.setLocation(tria.getX(0), tria.getY(0));
					_tmpBoundaryPoints.add(_n0);
				}
				if(isoAreaValues[i]<=tria.getU(1) && tria.getU(1) <= isoAreaValues[i+1]){
					_n1.setLocation(tria.getX(1), tria.getY(1));
					_tmpBoundaryPoints.add(_n1);
				}
				if(isoAreaValues[i]<=tria.getU(2) && tria.getU(2) <= isoAreaValues[i+1]){
					_n2.setLocation(tria.getX(2), tria.getY(2));
					_tmpBoundaryPoints.add(_n2);
				}

				if(_tmpBoundaryPoints.size()>2){
					area.add(new Area(computeConvexHull(_tmpBoundaryPoints)));	
				}
			}
			isoAreas[i] = new IsoArea(area, calcColor(i, steps),isoAreaValues[i],isoAreaValues[i+1]);
		}
//		System.err.println("isoline area = "+(System.currentTimeMillis()-s)+" ms");
		return isoAreas;
	}
	
	
	// performs linear interpolation
	private static double getInterpolatedPoint(double x0, double x1, double vd, double v0, double v1){
		double r0 = (v1-vd)/(v1-v0);
		double r1 = (vd-v0)/(v1-v0);
		if(0 <=r0 && r0 <= 1 && 0 <= r1 && r1 <= 1)
			return x0*r0+x1*r1;
		return Double.NaN;
	}
	// this method calculates the third coordinate of the crossproduct between p1-p0 and p2-p1,
	// if this is positive, line p0-p1-p2 turns right.
	private static boolean checkRightTurn(Point2D p0, Point2D p1, Point2D p2) {
		double cp2 = ((p1.getX() - p0.getX()) * (p2.getY() - p1.getY())) - 
				  	((p1.getY() - p0.getY()) * (p2.getX() - p1.getX()));
		if (cp2 > 0) 
			return false;
		return true;
	}
	private static GeneralPath computeConvexHull(Set set) {
		List list = new ArrayList(set); // set of points used to calculate the convex hull
		List lUpper = new ArrayList(); // contains the nodes that forms the upper part of the hull
		List lLower = new ArrayList(); // contains the nodes that forms the lower part of the hull
		Point2D p0;
		Point2D p1;
		// sorting the nodes by accending by using the x-coordinate
		for (int i = 0; i < list.size(); i++) {
			for (int j = i; j < list.size(); j++) {
				p0 = (Point2D) list.get(i);
				p1 = (Point2D) list.get(j);
				if (p0.getX() > p1.getX()) {
					list.set(i, p1);
					list.set(j, p0);
				}
			}
		}
		// calculates the upper part of the hull.
		lUpper.add(list.get(0));
		lUpper.add(list.get(1));
		for (int i = 2; i < list.size(); i++) {
			lUpper.add(list.get(i));
			while (lUpper.size() > 2
					&& checkRightTurn((Point2D) lUpper.get(lUpper.size() - 3),
							(Point2D) lUpper.get(lUpper.size() - 2),
							(Point2D) lUpper.get(lUpper.size() - 1))) {
				lUpper.remove(lUpper.size() - 2);
			}
		}
		// calculates the lower part of the hull.
		lLower.add(list.get(list.size() - 1));
		lLower.add(list.get(list.size() - 2));
		for (int i = list.size() - 3; i > -1; i--) {
			lLower.add(list.get(i));
			while (lLower.size() > 2
					&& checkRightTurn((Point2D) lLower.get(lLower.size() - 3),
							(Point2D) lLower.get(lLower.size() - 2),
							(Point2D) lLower.get(lLower.size() - 1))) {
				lLower.remove(lLower.size() - 2);
			}
		}
		// creates a general path from the upper and lower boundaries of the hull
		GeneralPath path = new GeneralPath();
		Point2D p2d = (Point2D) lUpper.get(0);
		path.moveTo((float) p2d.getX(), (float) p2d.getY());
		for(int i = 1; i < lUpper.size(); i++) {
			p2d = (Point2D) lUpper.get(i);
			path.lineTo((float) p2d.getX(), (float) p2d.getY());
		}
		for(int i = 1; i < lLower.size()-1; i++){
			p2d = (Point2D)lLower.get(i);
			path.lineTo((float) p2d.getX(), (float) p2d.getY());
		}
		path.closePath();
		return path;
	}
    public static GeneralPath createGeneralPathFormSetOfLines(Set line2D){
    	GeneralPath gp = new GeneralPath();
    	List list = new ArrayList();
    	list.addAll(line2D);
    	Point2D start, l1p2, l2p1, l2p2;
    	while(!list.isEmpty()){
	    	start = ((Line2D)list.get(0)).getP1();
	    	gp.moveTo((float)start.getX(),(float)start.getY());
	    	l1p2 = ((Line2D)list.remove(0)).getP2();
	    	gp.lineTo((float)l1p2.getX(),(float)l1p2.getY());	    	
    		for(int i = 0; i < list.size(); i++){
    			l2p1 = ((Line2D)list.get(i)).getP1();
    			l2p2 = ((Line2D)list.get(i)).getP2();
	    		if(l2p1.equals(l1p2)){ // found the next line segment
	    			gp.lineTo((float)l2p2.getX(),(float)l2p2.getY());
	    			l1p2.setLocation(l2p2);
	    			list.remove(i);
	    			i = -1;
	    		}
	    	}
    	}
    	return gp;
    }
	
	// maps a step to a color
	private static Color calcColor(int step,int steps){
		int alpha = 255;
		// determine the color boundary
		double x = (step*1.)/(steps*1.-1);
		if(x == 0)
			return new Color(cmat[0][0],cmat[0][1],cmat[0][2],alpha);
		
		double _increment = 1./(cmat.length-1);
		
		for(int i = 1; i < cmat.length; i++){
			if( x <= _increment*i){
				int r = (int)(getInterpolatedPoint(cmat[i-1][0],cmat[i][0],x,_increment*(i-1),_increment*i));
				int g = (int)(getInterpolatedPoint(cmat[i-1][1],cmat[i][1],x,_increment*(i-1),_increment*i));
				int b = (int)(getInterpolatedPoint(cmat[i-1][2],cmat[i][2],x,_increment*(i-1),_increment*i));
//				System.err.println(_increment*(i-1)+"\t"+_increment*i+"\t"+x+"\t"+cmat[i-1][0]*1.+"\t"+cmat[i][0]*1.);
//				System.err.println(i+"\t"+_increment*i+"\t"+r+"\t"+g+"\t"+b);
				return new Color(r,g,b,alpha);
			}
		}
		return new Color(cmat[cmat.length-1][0],cmat[cmat.length-1][1],cmat[cmat.length-1][2],alpha);
	}
    
	public static void reverseColors(IsoArea[] areas){
		List<Color> tmp = new ArrayList<Color>();
		for(int i = 0; i < areas.length;i++)
			tmp.add(areas[i].color);
		Collections.reverse(tmp);
		for(int i = 0; i < areas.length;i++)
			areas[i].color = tmp.get(i);
	}
	
}
