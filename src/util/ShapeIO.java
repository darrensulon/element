package util;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;

enum Shp {
		Line2D_Double(1),
		Line2D_Float(2), 
		Path2D_Double(3), 
		Path2D_Float(4), 
		Rectangle(5), 
		Rectangle2D_Double(6), 
		Rectangle2D_Float(7), 
		Area(8), 
		Arc2D_Double(9), 
		Arc2D_Float(10), 		
		CubicCurve2D_Double(11), 
		CubicCurve2D_Float(12), 
		Ellipse2D_Double(13), 
		Ellipse2D_Float(14), 
		GeneralPath(15), 
		Polygon(16), 
		QuadCurve2D_Double(17), 
		QuadCurve2D_Float(18), 
		RoundRectangle2D_Double(19), 
		RoundRectangle2D_Float(20),
		PathIterator(21);
		
		public final byte type;
		
		Shp(int type){
			this.type = (byte)type;
		}
		
		static Shp getType(Shape shp){
			if(shp instanceof Arc2D.Double)
				return Arc2D_Double;
			if(shp instanceof Arc2D.Float)
				return Arc2D_Float;
			if(shp instanceof java.awt.geom.Area)
				return Area;
			if(shp instanceof CubicCurve2D.Double)
				return CubicCurve2D_Double;
			if(shp instanceof CubicCurve2D.Float)
				return CubicCurve2D_Float;
			if(shp instanceof Ellipse2D.Double)
				return Ellipse2D_Double;
			if(shp instanceof Ellipse2D.Float)
				return Ellipse2D_Float;
			if(shp instanceof GeneralPath)
				return GeneralPath;
			if(shp instanceof Line2D.Double)
				return Line2D_Double;
			if(shp instanceof Line2D.Float)
				return Line2D_Float;
			if(shp instanceof Path2D.Double)
				return Path2D_Double;
			if(shp instanceof Path2D.Float)
				return Path2D_Float;
			if(shp instanceof java.awt.Polygon)
				return Polygon;
			if(shp instanceof QuadCurve2D.Double)
				return QuadCurve2D_Double;
			if(shp instanceof QuadCurve2D.Float)
				return QuadCurve2D_Float;
			if(shp instanceof java.awt.Rectangle)
				return Rectangle;
			if(shp instanceof Rectangle2D.Double)
				return Rectangle2D_Double;
			if(shp instanceof Rectangle2D.Float)
				return Rectangle2D_Float;
			if(shp instanceof RoundRectangle2D.Double)
				return RoundRectangle2D_Double;
			if(shp instanceof RoundRectangle2D.Float)
				return RoundRectangle2D_Float;
			return PathIterator;
		}

		public static Shp get(byte b){
			Shp[] shp = Shp.values();
			for(int i = 0; i < shp.length; i++){
				if(shp[i].type == b)
					return shp[i];
			}	
			return PathIterator;
		}
		
	};

public class ShapeIO {

	private static PrintStream debug;
	
    public static void writeShape(Shape shape, ObjectOutput out) throws IOException {
    	Shp shp = Shp.getType(shape);
    	if(debug != null)
    		debug.println("writting ... "+shp);
    	byte type = shp.type; 
    	out.write(type);
    	if(shp == Shp.Line2D_Double)
    		writeLineD(shape, out);
    	else if(shp == Shp.Line2D_Float)
    		writeLineF(shape, out);
    	else if(shp == Shp.Path2D_Double)
    		writePath2D_D(shape, out);
    	else if(shp == Shp.Path2D_Float)
    		writePath2D_F(shape, out);
    	else if(shp == Shp.GeneralPath)
    		writeGeneralPath(shape, out);
    	else if(shp == Shp.Area)
    		writeArea(shape, out);
    	else if(shp == Shp.Rectangle)
    		writeRect(shape, out);
    	else if(shp == Shp.Rectangle2D_Double)
    		writeRectangleD(shape, out);
    	else if(shp == Shp.Rectangle2D_Float)
    		writeRectangleF(shape, out);
    	else if(shp == Shp.Arc2D_Double)
    		writeArcD(shape, out);
    	else if(shp == Shp.Arc2D_Float)
    		writeArcF(shape, out);
    	else if(shp == Shp.RoundRectangle2D_Double)
    		writeRndRectD(shape, out);
    	else if(shp == Shp.RoundRectangle2D_Float)
    		writeRndRectF(shape, out);
    	else if(shp == Shp.Ellipse2D_Double)
    		writeEllipseD(shape, out);
    	else if(shp == Shp.Ellipse2D_Float)
    		writeEllipseF(shape, out);
    	else if(shp == Shp.CubicCurve2D_Double)
    		writeCubicCurveD(shape, out);
    	else if(shp == Shp.CubicCurve2D_Float)
    		writeCubicCurveF(shape, out);
    	else if(shp == Shp.QuadCurve2D_Double)
    		writeQuadCurveD(shape, out);
    	else if(shp == Shp.QuadCurve2D_Float)
    		writeQuadCurveF(shape, out);
    	else if(shp == Shp.Polygon)
    		writePolygon(shape, out);
    	else // Shp.PathIterator
    		write(shape,out);
    }
	
	public static Shape readShape(ObjectInput in) throws IOException {
		Shp shp = Shp.get(in.readByte());
		if(debug != null)
    		debug.println("reading ... "+shp);;
		if(shp == Shp.Line2D_Double)
			return readLineD(in);
		if(shp == Shp.Line2D_Float)
			return readLineF(in);
		if(shp == Shp.Path2D_Double)
			return readPath2D_D(in);
		if(shp == Shp.Path2D_Float)
			return readPath2D_F(in);
		if(shp == Shp.GeneralPath)
			return readGeneralPath(in);
		if(shp == Shp.Area)
			return readArea(in);
		if(shp == Shp.Rectangle)
			return readRect(in);
		if(shp == Shp.Rectangle2D_Double)
			return readRectangleD(in);
		if(shp == Shp.Rectangle2D_Float)
			return readRectangleF(in);
		if(shp == Shp.Arc2D_Double)
			return readArcD(in);
		if(shp == Shp.Arc2D_Float)
			return readArcF(in);
		if(shp == Shp.RoundRectangle2D_Double)
			return readRndRectD(in);
		if(shp == Shp.RoundRectangle2D_Float)
			return readRndRectF(in);
		if(shp == Shp.Ellipse2D_Double)
			return readEllipseD(in);
		if(shp == Shp.Ellipse2D_Float)
			return readEllipseF(in);
		if(shp == Shp.CubicCurve2D_Double)
			return readCubicCurveD(in);
		if(shp == Shp.CubicCurve2D_Float)
			return readCubicCurveF(in);
		if(shp == Shp.QuadCurve2D_Double)
			return readQuadCurveD(in);
		if(shp == Shp.QuadCurve2D_Float)
			return readQuadCurveF(in);
		if(shp == Shp.Polygon)
			return readPolygon(in);
		// Shp.PathIterator
		return read(in);
	}
		
	private static Shape readLineD(ObjectInput in) throws IOException {
		return new Line2D.Double(in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble());
	}
	
	private static void writeLineD(Shape shape, ObjectOutput out) throws IOException {
		Line2D l2d = (Line2D)shape;
		out.writeDouble(l2d.getX1());
		out.writeDouble(l2d.getY1());
		out.writeDouble(l2d.getX2());
		out.writeDouble(l2d.getY2());
	}
	
	private static Shape readLineF(ObjectInput in) throws IOException {
		return new Line2D.Float(in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat());
	}
	
	private static void writeLineF(Shape shape, ObjectOutput out) throws IOException {
		Line2D l2d = (Line2D)shape;
		out.writeFloat((float)l2d.getX1());
		out.writeFloat((float)l2d.getY1());
		out.writeFloat((float)l2d.getX2());
		out.writeFloat((float)l2d.getY2());
	}
	
	private static Shape readPath2D_D(ObjectInput in) throws IOException {
		Path2D p2d = new Path2D.Double(in.readInt());
		byte type = -1;
		do {
			type = in.readByte();
			switch(type){
				case 0 : 
					p2d.moveTo(in.readDouble(), in.readDouble());
					break;
				case 1 : 
					p2d.lineTo(in.readDouble(), in.readDouble());
					break;
				case 2 : 
					p2d.quadTo(in.readDouble(), in.readDouble(),
							in.readDouble(),in.readDouble());
					break;
				case 3 : 
					p2d.curveTo(in.readDouble(), in.readDouble(),
							in.readDouble(), in.readDouble(),
							in.readDouble(), in.readDouble());
					break;
				case 4 :
					p2d.closePath();
			}
		} while (type != -1);
		return p2d;
	}
	
	private static void writePath2D_D(Shape shape, ObjectOutput out) throws IOException{
		Path2D p2d = (Path2D)shape;
		out.writeInt(p2d.getWindingRule());
		PathIterator pi = p2d.getPathIterator(null);
		double[] crds = new double[6];
		int type;
		while(!pi.isDone()){
			type = pi.currentSegment(crds);
			switch(type){
				case PathIterator.SEG_MOVETO :
					out.writeByte(0);
					for(int i = 0; i < 2; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_LINETO :
					out.writeByte(1);
					for(int i = 0; i < 2; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_QUADTO :
					out.writeByte(2);
					for(int i = 0; i < 4; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_CUBICTO :
					out.writeByte(3);
					for(int i = 0; i < 6; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_CLOSE :
					out.writeByte(4);
					break;					
			}
			pi.next();
		}
		out.writeByte(-1);
	}
	
	private static Shape readPath2D_F(ObjectInput in) throws IOException {
		Path2D p2d = new Path2D.Float(in.readInt());
		byte type = -1;
		do {
			type = in.readByte();
			switch(type){
				case 0 : 
					p2d.moveTo(in.readFloat(), in.readFloat());
					break;
				case 1 : 
					p2d.lineTo(in.readFloat(), in.readFloat());
					break;
				case 2 : 
					p2d.quadTo(in.readFloat(), in.readFloat(),
							in.readFloat(),in.readFloat());
					break;
				case 3 : 
					p2d.curveTo(in.readFloat(), in.readFloat(),
							in.readFloat(), in.readFloat(),
							in.readFloat(), in.readFloat());
					break;
				case 4 :
					p2d.closePath();
			}
		} while (type != -1);
		return p2d;
	}
	
	private static void writePath2D_F(Shape shape, ObjectOutput out) throws IOException{
		Path2D p2d = (Path2D)shape;
		out.writeInt(p2d.getWindingRule());
		PathIterator pi = p2d.getPathIterator(null);
		float[] crds = new float[6];
		int type;
		while(!pi.isDone()){
			type = pi.currentSegment(crds);
			switch(type){
				case PathIterator.SEG_MOVETO :
					out.writeByte(0);
					for(int i = 0; i < 2; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_LINETO :
					out.writeByte(1);
					for(int i = 0; i < 2; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_QUADTO :
					out.writeByte(2);
					for(int i = 0; i < 4; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_CUBICTO :
					out.writeByte(3);
					for(int i = 0; i < 6; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_CLOSE :
					out.writeByte(4);
					break;					
			}
			pi.next();
		}
		out.writeByte(-1);
	}	
	
	private static Shape readGeneralPath(ObjectInput in) throws IOException {
		GeneralPath p2d = new GeneralPath(in.readInt());
		byte type = -1;
		do {
			type = in.readByte();
			switch(type){
				case 0 : 
					p2d.moveTo(in.readFloat(), in.readFloat());
					break;
				case 1 : 
					p2d.lineTo(in.readFloat(), in.readFloat());
					break;
				case 2 : 
					p2d.quadTo(in.readFloat(), in.readFloat(),
							in.readFloat(),in.readFloat());
					break;
				case 3 : 
					p2d.curveTo(in.readFloat(), in.readFloat(),
							in.readFloat(), in.readFloat(),
							in.readFloat(), in.readFloat());
					break;
				case 4 :
					p2d.closePath();
			}
		} while (type != -1);
		return p2d;
	}
	
	private static void writeGeneralPath(Shape shape, ObjectOutput out) throws IOException{
		GeneralPath p2d = (GeneralPath)shape;
		out.writeInt(p2d.getWindingRule());
		PathIterator pi = p2d.getPathIterator(null);
		float[] crds = new float[6];
		int type;
		while(!pi.isDone()){
			type = pi.currentSegment(crds);
			switch(type){
				case PathIterator.SEG_MOVETO :
					out.writeByte(0);
					for(int i = 0; i < 2; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_LINETO :
					out.writeByte(1);
					for(int i = 0; i < 2; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_QUADTO :
					out.writeByte(2);
					for(int i = 0; i < 4; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_CUBICTO :
					out.writeByte(3);
					for(int i = 0; i < 6; i++)
						out.writeFloat(crds[i]);
					break;
				case PathIterator.SEG_CLOSE :
					out.writeByte(4);
					break;					
			}
			pi.next();
		}
		out.writeByte(-1);
	}
	
	private static Shape readArea(ObjectInput in) throws IOException {
		Path2D p2d = new Path2D.Double();
		byte type = -1;
		do {
			type = in.readByte();
			switch(type){
				case 0 : 
					p2d.moveTo(in.readDouble(), in.readDouble());
					break;
				case 1 : 
					p2d.lineTo(in.readDouble(), in.readDouble());
					break;
				case 2 : 
					p2d.quadTo(in.readDouble(), in.readDouble(),
							in.readDouble(),in.readDouble());
					break;
				case 3 : 
					p2d.curveTo(in.readDouble(), in.readDouble(),
							in.readDouble(), in.readDouble(),
							in.readDouble(), in.readDouble());
					break;
				case 4 :
					p2d.closePath();
			}
		} while (type != -1);
		return new Area(p2d);
	}
	
	private static void writeArea(Shape shape, ObjectOutput out) throws IOException{
		Area p2d = (Area)shape;	
		PathIterator pi = p2d.getPathIterator(null);
		double[] crds = new double[6];
		int type;
		while(!pi.isDone()){
			type = pi.currentSegment(crds);
			switch(type){
				case PathIterator.SEG_MOVETO :
					out.writeByte(0);
					for(int i = 0; i < 2; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_LINETO :
					out.writeByte(1);
					for(int i = 0; i < 2; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_QUADTO :
					out.writeByte(2);
					for(int i = 0; i < 4; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_CUBICTO :
					out.writeByte(3);
					for(int i = 0; i < 6; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_CLOSE :
					out.writeByte(4);
					break;					
			}
			pi.next();
		}
		out.writeByte(-1);
	}	
	
	private static Shape readRect(ObjectInput in) throws IOException {
		return new Rectangle(in.readInt(),in.readInt(),in.readInt(),in.readInt());
	}
	
	private static void writeRect(Shape shape, ObjectOutput out) throws IOException{
		Rectangle r = (Rectangle)shape;
		out.writeInt(r.x);
		out.writeInt(r.y);
		out.writeInt(r.width);
		out.write(r.height);
	}
	
	private static Shape readRectangleD(ObjectInput in) throws IOException {
		return new Rectangle2D.Double(in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble());
	}
	
	private static void writeRectangleD(Shape shape, ObjectOutput out) throws IOException{
		Rectangle2D r = (Rectangle2D)shape;
		out.writeDouble(r.getX());
		out.writeDouble(r.getY());
		out.writeDouble(r.getWidth());
		out.writeDouble(r.getHeight());
	}
	
	private static Shape readRectangleF(ObjectInput in) throws IOException {
		return new Rectangle2D.Float(in.readFloat(),in.readFloat(),in.readFloat(),in.readFloat());
	}
	
	private static void writeRectangleF(Shape shape, ObjectOutput out) throws IOException{
		Rectangle2D r = (Rectangle2D)shape;
		out.writeFloat((float)r.getX());
		out.writeFloat((float)r.getY());
		out.writeFloat((float)r.getWidth());
		out.writeFloat((float)r.getHeight());
	}
	
	private static Shape readArcD(ObjectInput in) throws IOException {
		return new Arc2D.Double(in.readDouble(),in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble(),in.readDouble(),in.readInt());
	}
	
	private static void writeArcD(Shape shape, ObjectOutput out) throws IOException{
		Arc2D arc = (Arc2D.Double)shape;
		out.writeDouble(arc.getX());
		out.writeDouble(arc.getY());
		out.writeDouble(arc.getWidth());
		out.writeDouble(arc.getHeight());
		out.writeDouble(arc.getAngleStart());
		out.writeDouble(arc.getAngleExtent());
		out.writeInt(arc.getArcType());
	}
	
	private static Shape readArcF(ObjectInput in) throws IOException {
		return new Arc2D.Float(in.readFloat(),in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat(),in.readFloat(),in.readInt());
	}
	
	private static void writeArcF(Shape shape, ObjectOutput out) throws IOException{
		Arc2D arc = (Arc2D.Float)shape;
		out.writeFloat((float)arc.getX());
		out.writeFloat((float)arc.getY());
		out.writeFloat((float)arc.getWidth());
		out.writeFloat((float)arc.getHeight());
		out.writeFloat((float)arc.getAngleStart());
		out.writeFloat((float)arc.getAngleExtent());
		out.writeInt(arc.getArcType());
	}
	
	private static Shape readRndRectD(ObjectInput in) throws IOException {
		return new RoundRectangle2D.Double(in.readDouble(),in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble(),in.readDouble());
	}
	
	private static void writeRndRectD(Shape shape, ObjectOutput out) throws IOException{
		RoundRectangle2D rr = (RoundRectangle2D.Double)shape;
		out.writeDouble(rr.getX());
		out.writeDouble(rr.getY());
		out.writeDouble(rr.getWidth());
		out.writeDouble(rr.getHeight());
		out.writeDouble(rr.getArcWidth());
		out.writeDouble(rr.getArcHeight());
	}
	
	private static Shape readRndRectF(ObjectInput in) throws IOException {
		return new RoundRectangle2D.Float(in.readFloat(),in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat(),in.readFloat());
	}
	
	private static void writeRndRectF(Shape shape, ObjectOutput out) throws IOException{
		RoundRectangle2D rr = (RoundRectangle2D.Float)shape;
		out.writeFloat((float)rr.getX());
		out.writeFloat((float)rr.getY());
		out.writeFloat((float)rr.getWidth());
		out.writeFloat((float)rr.getHeight());
		out.writeFloat((float)rr.getArcWidth());
		out.writeFloat((float)rr.getArcHeight());
	}
	
	private static Shape readEllipseD(ObjectInput in) throws IOException {
		return new Ellipse2D.Double(in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble());
	}
	
	private static void writeEllipseD(Shape shape, ObjectOutput out) throws IOException{
		Ellipse2D ellipse = (Ellipse2D.Double)shape;
		out.writeDouble(ellipse.getX());
		out.writeDouble(ellipse.getY());
		out.writeDouble(ellipse.getWidth());
		out.writeDouble(ellipse.getHeight());
	}
	
	private static Shape readEllipseF(ObjectInput in) throws IOException {
		return new Ellipse2D.Float(in.readFloat(),in.readFloat(),in.readFloat(),in.readFloat());
	}
	
	private static void writeEllipseF(Shape shape, ObjectOutput out) throws IOException{
		Ellipse2D ellipse = (Ellipse2D.Float)shape;
		out.writeFloat((float)ellipse.getX());
		out.writeFloat((float)ellipse.getY());
		out.writeFloat((float)ellipse.getWidth());
		out.writeFloat((float)ellipse.getHeight());
	}
	
	private static Shape readCubicCurveD(ObjectInput in) throws IOException {
		return new CubicCurve2D.Double(in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble());
	}
	
	private static void writeCubicCurveD(Shape shape, ObjectOutput out) throws IOException{
		CubicCurve2D curve = (CubicCurve2D.Double)shape;
		out.writeDouble(curve.getX1());
		out.writeDouble(curve.getY1());
		out.writeDouble(curve.getCtrlX1());
		out.writeDouble(curve.getCtrlY1());
		out.writeDouble(curve.getCtrlX2());
		out.writeDouble(curve.getCtrlY2());
		out.writeDouble(curve.getX2());
		out.writeDouble(curve.getY2());
	}
	
	private static Shape readCubicCurveF(ObjectInput in) throws IOException {
		return new CubicCurve2D.Float(in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat());
	}
	
	private static void writeCubicCurveF(Shape shape, ObjectOutput out) throws IOException{
		CubicCurve2D curve = (CubicCurve2D.Float)shape;
		out.writeFloat((float)curve.getX1());
		out.writeFloat((float)curve.getY1());
		out.writeFloat((float)curve.getCtrlX1());
		out.writeFloat((float)curve.getCtrlY1());
		out.writeFloat((float)curve.getCtrlX2());
		out.writeFloat((float)curve.getCtrlY2());
		out.writeFloat((float)curve.getX2());
		out.writeFloat((float)curve.getY2());
	}
	
	private static Shape readQuadCurveD(ObjectInput in) throws IOException {
		return new QuadCurve2D.Double(in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble(),
				in.readDouble(),in.readDouble());
	}
	
	private static void writeQuadCurveD(Shape shape, ObjectOutput out) throws IOException{
		QuadCurve2D curve = (QuadCurve2D.Double)shape;
		out.writeDouble(curve.getX1());
		out.writeDouble(curve.getY1());
		out.writeDouble(curve.getCtrlX());
		out.writeDouble(curve.getCtrlY());
		out.writeDouble(curve.getX2());
		out.writeDouble(curve.getY2());
	}
	
	private static Shape readPolygon(ObjectInput in) throws IOException {
		try {
			return (Polygon)in.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}
	
	private static void writePolygon(Shape shape, ObjectOutput out) throws IOException{
		out.writeObject((Polygon)shape);
	}
	
	private static Shape readQuadCurveF(ObjectInput in) throws IOException {
		return new QuadCurve2D.Float(in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat(),
				in.readFloat(),in.readFloat());
	}
	
	private static void writeQuadCurveF(Shape shape, ObjectOutput out) throws IOException{
		QuadCurve2D curve = (QuadCurve2D.Float)shape;
		out.writeFloat((float)curve.getX1());
		out.writeFloat((float)curve.getY1());
		out.writeFloat((float)curve.getCtrlX());
		out.writeFloat((float)curve.getCtrlY());
		out.writeFloat((float)curve.getX2());
		out.writeFloat((float)curve.getY2());
	}
	
	private static Shape read(ObjectInput in) throws IOException {
		Path2D p2d = new Path2D.Double();
		byte type = -1;
		do {
			type = in.readByte();
			switch(type){
				case 0 : 
					p2d.moveTo(in.readDouble(), in.readDouble());
					break;
				case 1 : 
					p2d.lineTo(in.readDouble(), in.readDouble());
					break;
				case 2 : 
					p2d.quadTo(in.readDouble(), in.readDouble(),
							in.readDouble(),in.readDouble());
					break;
				case 3 : 
					p2d.curveTo(in.readDouble(), in.readDouble(),
							in.readDouble(), in.readDouble(),
							in.readDouble(), in.readDouble());
					break;
				case 4 :
					p2d.closePath();
			}
		} while (type != -1);
		return p2d;
	}
	
	private static void write(Shape shape, ObjectOutput out) throws IOException{
		PathIterator pi = shape.getPathIterator(null);
		double[] crds = new double[6];
		int type;
		while(!pi.isDone()){
			type = pi.currentSegment(crds);
			switch(type){
				case PathIterator.SEG_MOVETO :
					out.writeByte(0);
					for(int i = 0; i < 2; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_LINETO :
					out.writeByte(1);
					for(int i = 0; i < 2; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_QUADTO :
					out.writeByte(2);
					for(int i = 0; i < 4; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_CUBICTO :
					out.writeByte(3);
					for(int i = 0; i < 6; i++)
						out.writeDouble(crds[i]);
					break;
				case PathIterator.SEG_CLOSE :
					out.writeByte(4);
					break;					
			}
			pi.next();
		}
		out.writeByte(-1);
	}
}
