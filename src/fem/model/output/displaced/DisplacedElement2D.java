package fem.model.output.displaced;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class DisplacedElement2D implements Serializable {

	private String name;
	private double[] crds;
	private Shape shape;
	private Point2D midPoint;
	
	public DisplacedElement2D(String name, double[] crds){
		this.name = name;
		this.crds = crds;
	}
	
	public String getName(){
		return name;
	}
	public Point2D midPoint(){
		if(midPoint == null){
			double x = 0;
			double y = 0;
			for(int i = 0; i < crds.length; i = i+2){
				x += crds[i];
				y += crds[i+1];
			}
			x /= crds.length/2;
			y /= crds.length/2;
			midPoint = new Point2D.Double(x,y);
		}
		return midPoint;
	}
	
		
	public Shape getShape(){
		if(shape == null){
			Path2D path = new Path2D.Double();
			for(int i = 0; i < crds.length; i = i+2){
				if(i == 0){
					path.moveTo(crds[i], crds[i+1]);
				}
				else {
					path.lineTo(crds[i], crds[i+1]);
				}
			}
			path.closePath();
			shape = path;
		}
		return shape;
	}
	
}
