package fem.model.output.nodalFieldState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.Iterator;

import fem.model.output.Renderer;
import math.linalg.Vector;
import util.gui.ZoomPanel;

public class NodalFieldStateRenderer implements Renderer, Serializable {
	
	private static final long serialVersionUID = 1L;
	private NodalFieldStateRecorder m_recorder;
	private Rectangle2D m_bounds;
	
	public NodalFieldStateRenderer(NodalFieldStateRecorder hsr){
		this.m_recorder = hsr;
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
		Stroke _save = g2d.getStroke();
		Iterator<NodalFieldStateValue> iter = m_recorder.iterate();
		final double size = 10*zp.getScale();
		DecimalFormat df = new DecimalFormat("0.000");
		Font _font = g2d.getFont();
		Color _color = g2d.getColor();
		Stroke stroke = new BasicStroke(4f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		while(iter.hasNext()){
			NodalFieldStateValue ntv = iter.next();			
			Vector crds = ntv.getCrds();
//			Shape s = zp.transformShapeToPanel(new Ellipse2D.Double(crds.get(0)-size/2,crds.get(1)-size/2,size,size));
//			g2d.setColor(new Color(175,125,0,125));
//			g2d.fill(s);
//			g2d.setColor(Color.BLACK);
//			g2d.draw(s);	
			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
			zp.ucs2pixel(_p2d,_p2d);
			
			TextLayout layout = new TextLayout(new AttributedString(df.format(ntv.getFieldState())).getIterator(),g2d.getFontRenderContext());
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
		g2d.setColor(_color);
		g2d.setFont(_font);
		
	}
	
	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<NodalFieldStateValue> iter = m_recorder.iterate();
			NodalFieldStateValue ntv;
			if(iter.hasNext()){
				ntv = iter.next();
				Vector v = ntv.getCrds();
				m_bounds = new Rectangle2D.Double(v.get(0),v.get(1),0,0);
			}
			while(iter.hasNext()){
				ntv = iter.next();
				Vector v = ntv.getCrds();
				m_bounds.add(new Point2D.Double(v.get(0),v.get(1)));
			}
		}
		return m_bounds;
	}

}
