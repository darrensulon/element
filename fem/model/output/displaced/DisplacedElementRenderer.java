package fem.model.output.displaced;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;

import fem.model.output.Renderer;
import util.gui.ZoomPanel;

public class DisplacedElementRenderer implements Renderer, Serializable {

	private static final Stroke STROKE = new BasicStroke(4f,
			BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	
	private DisplacedElementRecorder m_recorder;
	private Rectangle2D m_bounds;
	private boolean m_draw_names;
	private boolean m_fillElement = false;
	
	public DisplacedElementRenderer(DisplacedElementRecorder hsr, boolean drawNames, boolean fill){
		this.m_recorder = hsr;
		this.m_draw_names = drawNames;
		this.m_fillElement = fill;
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
		Iterator<DisplacedElement2D> iter = m_recorder.iterate();
		
		while(iter.hasNext()){
			DisplacedElement2D ntv = iter.next();			
			Shape s = zp.transformShapeToPanel(ntv.getShape());
			Area area = new Area(s);
			area.subtract(new Area(STROKE.createStrokedShape(s)));
			if(m_fillElement){
				g2d.setColor(Color.YELLOW.darker().darker().darker().darker());
				g2d.fill(area);
			}
			g2d.setColor(Color.WHITE);
			g2d.draw(area);		
			if(m_draw_names){
				Point2D _p2d = zp.ucs2pixel(ntv.midPoint(),null);
				g2d.drawString(ntv.getName(), (float)_p2d.getX(), (float)_p2d.getY());
			}
		}
	}

	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<DisplacedElement2D> iter = m_recorder.iterate();
			if(iter.hasNext()){
				m_bounds =  iter.next().getShape().getBounds2D();
			}
			while(iter.hasNext()){
				m_bounds.add(iter.next().getShape().getBounds2D());
			}
		}
		return m_bounds;
	}
	
}
