package fem.model.output.primalBoundaryCondition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import fem.model.output.Renderer;
import util.gui.ZoomPanel;

public class PrimalBoundaryConditionRenderer implements Renderer {

	private PrimalBoundaryConditionRecorder m_recorder;
	private Rectangle2D m_bounds;
	
	public PrimalBoundaryConditionRenderer(PrimalBoundaryConditionRecorder hsr){
		this.m_recorder = hsr;
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
		Iterator<Point2D> iter = m_recorder.iterate();
		final double size = 10*zp.getScale();
		while(iter.hasNext()){
			Point2D pnt = iter.next();			
			Shape s = zp.transformShapeToPanel(new Rectangle2D.Double(pnt.getX()-size/2,pnt.getY()-size/2,size,size));
//			g2d.setColor(new Color(175,125,0,125));
			g2d.setColor(Color.RED);
			g2d.fill(s);
			g2d.setColor(Color.BLACK);
			g2d.draw(s);		
//			Point2D p = zp.ucs2pixel(pnt, null);
//			g2d.setColor(new Color(255,255,255,200));
////			g2d.setColor(Color.GREEN);
//			g2d.fillRect((int)p.getX()+7, (int)p.getY()+4,20,12);
//			g2d.setColor(Color.RED);
//			g2d.drawString(m_recorder.getName(pnt), (float)p.getX()-9, (float)p.getY()-14);
		}
	}
	
	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<Point2D> iter = m_recorder.iterate();
			if(iter.hasNext()){
				Point2D p2d = iter.next();
				m_bounds = new Rectangle2D.Double(p2d.getX(),p2d.getY(),0,0);
			}
			while(iter.hasNext()){
				Point2D p2d = iter.next();
				m_bounds.add(new Rectangle2D.Double(p2d.getX(),p2d.getY(),0,0));
			}
		}
		return m_bounds;
	}

}
