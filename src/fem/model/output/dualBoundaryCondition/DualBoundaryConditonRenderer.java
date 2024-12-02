package fem.model.output.dualBoundaryCondition;

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
import java.util.Iterator;

import math.linalg.Vector;
import util.gui.ZoomPanel;
import fem.model.output.Renderer;
import fem.model.output.nodeDisplacement.NodeDisplacementPoint;

public class DualBoundaryConditonRenderer implements Renderer {

	private AbstractDualBoundaryConditionRecorder m_recorder;
	
	private Rectangle2D m_bounds;
	
	private double max_length = 50; // pixels
	private double max;
	
	public DualBoundaryConditonRenderer(AbstractDualBoundaryConditionRecorder hsr){
		this.m_recorder = hsr;
		max = hsr.maxAbsHeatState();
	}
	
	private Shape arrow(DualBoundaryConditionPoint hsp, double zps){
		Vector hs = hsp.getHeatState();
		Vector crds = hsp.getCrds();
		double _a = Math.atan2(hs.get(1),hs.get(0));
		double _l = hs.abs();
		double _scale = max_length/max*zps;
		double unit = 3*_l/max_length;
		Path2D arrow = new Path2D.Double();
		arrow.moveTo(-_l, 0);
		arrow.lineTo(0, 0);
		arrow.lineTo(-2*unit, 2*unit);
		arrow.lineTo(-1.2*unit, 0.);
		arrow.lineTo(-2*unit, -2*unit);
		arrow.lineTo( 0, 0);
		AffineTransform af = AffineTransform.getTranslateInstance(crds.get(0), crds.get(1));			
		af.scale(_scale,_scale);
		af.rotate(_a);
		return af.createTransformedShape(arrow);
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
		Iterator<DualBoundaryConditionPoint> iter = m_recorder.iterate();
		Color _color = g2d.getColor();
		g2d.setColor(Color.MAGENTA);
		g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		while(iter.hasNext()){
			DualBoundaryConditionPoint hsp = iter.next();			
			Vector crds = hsp.getCrds();
			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
			zp.ucs2pixel(_p2d,_p2d);
			if(hsp.getHeatState().abs() != 0) {
				
//				String abs = Double.toString(hsp.getHeatState().abs());
//				g2d.drawString(hsp.getName()+" = "+abs, (float)_p2d.getX(), (float) ((float)_p2d.getY()-max_length/2));
				Shape arrow = arrow(hsp,zp.getScale());
				g2d.setStroke(new BasicStroke(2));
				g2d.draw(zp.transformShapeToPanel(arrow));
			}
		}
		
	}

	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<DualBoundaryConditionPoint> iter = m_recorder.iterate();
			DualBoundaryConditionPoint hsp;
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
