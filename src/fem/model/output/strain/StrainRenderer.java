package fem.model.output.strain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.Iterator;

import math.linalg.Vector;
import util.gui.ZoomPanel;
import fem.model.output.Renderer;
import fem.model.output.gradientVector.GradientVectorPoint;
import fem.model.view.ElasticGraphicalOutputOptions;

public class StrainRenderer implements Renderer {

	private AbstractStrainRecorder m_recorder;
	
	private Rectangle2D m_bounds;
	
	private double max_length = 50; // pixels
	private double max;
	
	public StrainRenderer(AbstractStrainRecorder hsr){
		this.m_recorder = hsr;
		max = hsr.maxAbsHeatState();
	}
	
	private Shape arrow(StrainPoint hsp, double zps){
		Vector hs = hsp.getStress();
		Vector crds = hsp.getCrds();
		double _a = Math.atan2(hs.get(1),hs.get(0));
		double _l = hs.abs();
		double _scale = max_length/max*zps;
		double unit = 3*_l/max_length;
		Path2D arrow = new Path2D.Double();
		arrow.moveTo(-_l/2, 0);
		arrow.lineTo( _l/2, 0);
		arrow.lineTo( _l/2-2*unit, 2*unit);
		arrow.lineTo( _l/2-1.2*unit, 0.);
		arrow.lineTo( _l/2-2*unit, -2*unit);
		arrow.lineTo( _l/2, 0);
		AffineTransform af = AffineTransform.getTranslateInstance(crds.get(0), crds.get(1));			
		af.scale(_scale,_scale);
		af.rotate(_a);
		return af.createTransformedShape(arrow);
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
//		Iterator<StressPoint> iter = m_recorder.iterate();
//		DecimalFormat df = new DecimalFormat("0.00000");
//		Color _color = g2d.getColor();
//		g2d.setColor(Color.GREEN.darker());
//		g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		while(iter.hasNext()){
//			StressPoint hsp = iter.next();			
//			Vector crds = hsp.getCrds();
//			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
//			zp.ucs2pixel(_p2d,_p2d);
//			g2d.drawString(df.format(hsp.getStress().abs()), (float)_p2d.getX(), (float)_p2d.getY());
//			
//			if(((StandardStressRecorder)m_recorder).getMode() == 6) {
//				g2d.setStroke(new BasicStroke(2));
//				g2d.draw(zp.transformShapeToPanel(arrow(hsp,zp.getScale())));
//			}
//		}
//		g2d.setColor(_color);
		Stroke _save = g2d.getStroke();
		Iterator<StrainPoint> iter = m_recorder.iterate();
		final double size = 10*zp.getScale();
		DecimalFormat df = new DecimalFormat("0.000E0");
		Font _font = g2d.getFont();
		Color _color = g2d.getColor();
		Stroke stroke = new BasicStroke(4f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		while(iter.hasNext()){
			StrainPoint ntv = iter.next();			
			Vector crds = ntv.getCrds();
			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
			zp.ucs2pixel(_p2d,_p2d);
			
			if(((StandardStrainRecorder)m_recorder).getMode() == ElasticGraphicalOutputOptions.E_12) {
				Shape s = zp.transformShapeToPanel(new Ellipse2D.Double(crds.get(0)-size/2,crds.get(1)-size/2,size,size));
				g2d.setColor(Color.ORANGE);
				g2d.fill(s);
				g2d.setColor(Color.BLACK);
				g2d.draw(s);	
				
			}else if(((StandardStrainRecorder)m_recorder).getMode() == ElasticGraphicalOutputOptions.E_11||
					((StandardStrainRecorder)m_recorder).getMode() == ElasticGraphicalOutputOptions.E_22) {
				Shape arrow = arrow(ntv,zp.getScale());
				g2d.setStroke(new BasicStroke(2));
				g2d.setColor(Color.ORANGE);
				g2d.draw(zp.transformShapeToPanel(arrow));		
			}
			
			
			TextLayout layout = new TextLayout(new AttributedString(df.format(ntv.getStress().abs())).getIterator(),g2d.getFontRenderContext());
			Shape text = layout.getOutline(null);
			AffineTransform af = AffineTransform.getTranslateInstance(_p2d.getX()+2, _p2d.getY()+14);
			
			g2d.setStroke(stroke);
			text = af.createTransformedShape(text);
			g2d.setColor(Color.WHITE);
			g2d.draw(text);
			g2d.setColor(Color.BLACK);
			g2d.fill(text);
			g2d.setStroke(_save);
		}
		g2d.setColor(_color);
		g2d.setFont(_font);
	}

	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<StrainPoint> iter = m_recorder.iterate();
			StrainPoint hsp;
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
