
package fem.model.output.nodeDisplacement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import math.linalg.Vector;
import util.gui.ZoomPanel;
import fem.model.output.Renderer;
import fem.model.output.nodalFieldState.NodalFieldStateValue;

public class NodeDisplacementRenderer implements Renderer {

	private AbstractNodeDisplacementRecorder m_recorder;
	
	private Rectangle2D m_bounds;
	
	private double max_length = 50; // pixels
	private double max;
	
	public NodeDisplacementRenderer(AbstractNodeDisplacementRecorder hsr){
		this.m_recorder = hsr;
		max = hsr.maxAbsDisplacement();
	}
	
	private Shape arrow(NodeDisplacementPoint hsp, double zps){
		Vector hs = hsp.getDisplacements();
		Vector crds = hsp.getCrds();
		double _a = Math.atan2(hs.get(1),hs.get(0));
		double _l = hs.abs();
		double _scale = max_length/max*zps;
		double unit = 3*_l/max_length;
		Path2D arrow = new Path2D.Double();
		arrow.moveTo(0, 0);
		arrow.lineTo( _l, 0);
		arrow.lineTo( _l-2*unit, unit);
		arrow.lineTo( _l-1.2*unit, 0.);
		arrow.lineTo( _l-2*unit, -unit);
		arrow.lineTo( _l, 0);
		AffineTransform af = AffineTransform.getTranslateInstance(crds.get(0), crds.get(1));			
		af.scale(_scale,_scale);
		af.rotate(_a);
		return af.createTransformedShape(arrow);
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
//		Iterator<NodeDisplacementPoint> iter = m_recorder.iterate();
//		DecimalFormat df = new DecimalFormat("0.00000");
//		Color _color = g2d.getColor();
//		g2d.setColor(Color.GREEN);
//		g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		while(iter.hasNext()){
//			NodeDisplacementPoint hsp = iter.next();			
//			Vector crds = hsp.getCrds();
//			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
//			zp.ucs2pixel(_p2d,_p2d);
//			g2d.drawString(df.format(hsp.getDisplacements().abs()), (float)_p2d.getX(), (float)_p2d.getY());
//			g2d.setStroke(new BasicStroke(2));
//			g2d.draw(zp.transformShapeToPanel(arrow(hsp,zp.getScale())));
//		}
//		g2d.setColor(_color);
		Stroke _save = g2d.getStroke();
		Iterator<NodeDisplacementPoint> iter = m_recorder.iterate();
		final double size = 10*zp.getScale();
		DecimalFormat df = new DecimalFormat("0.000E0");
		Font _font = g2d.getFont();
		Color _color = g2d.getColor();
		Stroke stroke = new BasicStroke(4f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		while(iter.hasNext()){
			NodeDisplacementPoint ntv = iter.next();			
			Vector crds = ntv.getCrds();
//			Shape s = zp.transformShapeToPanel(new Ellipse2D.Double(crds.get(0)-size/2,crds.get(1)-size/2,size,size));
//			g2d.setColor(new Color(175,125,0,125));
//			g2d.fill(s);
//			g2d.setColor(Color.BLACK);
//			g2d.draw(s);	
			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
			zp.ucs2pixel(_p2d,_p2d);
			
			g2d.setColor(Color.GREEN);
			g2d.setStroke(new BasicStroke(2));
			g2d.draw(zp.transformShapeToPanel(arrow(ntv,zp.getScale())));
			
			TextLayout layout = new TextLayout(new AttributedString(df.format(ntv.getDisplacements().abs())).getIterator(),g2d.getFontRenderContext());
			Shape text = layout.getOutline(null);
			AffineTransform af = AffineTransform.getTranslateInstance(_p2d.getX(), _p2d.getY());
			
			g2d.setStroke(stroke);
			text = af.createTransformedShape(text);
			g2d.setColor(Color.WHITE);
			g2d.draw(text);
			g2d.setColor(Color.BLACK);
			g2d.fill(text);
			g2d.setStroke(_save);
		}
	}

	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<NodeDisplacementPoint> iter = m_recorder.iterate();
			NodeDisplacementPoint hsp;
			if(iter.hasNext()){
				hsp = iter.next();
				Vector v = hsp.getCrds();
				m_bounds = new Rectangle2D.Double(v.get(0),v.get(1),0,0);
			}
			while(iter.hasNext()){
				hsp = iter.next();
				Vector v = hsp.getCrds();
				m_bounds.add(new Point2D.Double(v.get(0),v.get(1)));
			}
		}
		return m_bounds;
	}

}
