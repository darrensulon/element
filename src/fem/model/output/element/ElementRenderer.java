package fem.model.output.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;

import fem.model.output.Renderer;
import util.gui.ZoomPanel;

public class ElementRenderer implements Renderer, Serializable {

	private static final Stroke STROKE = new BasicStroke(4f,
			BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	
	private ElementRecorder m_recorder;
	private Rectangle2D m_bounds;
	private boolean m_draw_names;
	private boolean m_fillElement = false;
	
	public ElementRenderer(ElementRecorder hsr, boolean drawNames, boolean fill){
		this.m_recorder = hsr;
		this.m_draw_names = drawNames;
		this.m_fillElement = fill;
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
		Iterator<Element2D> iter = m_recorder.iterate();
		Stroke _save = g2d.getStroke();
		Stroke stroke = new BasicStroke(4f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		
		while(iter.hasNext()){
			Element2D ntv = iter.next();			
			Shape s = zp.transformShapeToPanel(ntv.getShape());
			Area area = new Area(s);
			area.subtract(new Area(STROKE.createStrokedShape(s)));
			if(m_fillElement){
				g2d.setColor(Color.CYAN.darker().darker().darker().darker());
				g2d.fill(area);
			}
			g2d.setColor(Color.WHITE);
			g2d.draw(area);		
			if(m_draw_names){
				Point2D _p2d = zp.ucs2pixel(ntv.midPoint(),null);
				TextLayout layout = new TextLayout(ntv.getName(), new Font("Arial", Font.PLAIN, 12),g2d.getFontRenderContext());
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
	}

	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<Element2D> iter = m_recorder.iterate();
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
