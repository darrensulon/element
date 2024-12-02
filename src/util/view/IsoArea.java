package util.view;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import util.ShapeIO;

public class IsoArea implements Externalizable {

	private static final long serialVersionUID = 1L;
	public Path2D outline;
	public Color color; 
	public double lowerBound;
	public double upperBound;
	
	public IsoArea(){ }
	
	public IsoArea(Area _outline, Color color, double lowerBound, double upperBound){
		this.outline = new Path2D.Double();
		this.outline.append(_outline, false);
		this.color = color;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		outline = (Path2D)ShapeIO.readShape(in);
		color = new Color(in.readInt(),in.readInt(),in.readInt(),in.readInt());
		lowerBound = in.readDouble();
		upperBound = in.readDouble();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		ShapeIO.writeShape(outline, out);
		out.writeInt(color.getRed());
		out.writeInt(color.getGreen());
		out.writeInt(color.getBlue());
		out.writeInt(color.getAlpha());
		out.writeDouble(lowerBound);
		out.writeDouble(upperBound);
	}
	
}
