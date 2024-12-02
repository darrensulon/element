package fem.model.output.node;

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
import java.text.DecimalFormat;
import java.util.Iterator;

import fem.model.output.Renderer;
import math.linalg.Vector;
import util.gui.ZoomPanel;

public class NodeRenderer implements Renderer, Serializable {
	
	private static final long serialVersionUID = 1L;
	private NodeRecorder m_recorder;
	private Rectangle2D m_bounds;
	private boolean displayNodeNames;
	
	public NodeRenderer(NodeRecorder hsr, boolean displayNodeNames){
		this.m_recorder = hsr;
		this.displayNodeNames = displayNodeNames;
	}
	
	public void render(Graphics2D g2d, ZoomPanel zp) {
		Stroke _save = g2d.getStroke();
		Iterator<NodeValue> iter = m_recorder.iterate();
		final double size = 10*zp.getScale();
		DecimalFormat df = new DecimalFormat("0.000");
		Font _font = g2d.getFont();
		Color _color = g2d.getColor();
		Stroke stroke = new BasicStroke(4f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		while(iter.hasNext()){
			NodeValue ntv = iter.next();			
			Vector crds = ntv.getCrds();
			Shape s = zp.transformShapeToPanel(new Ellipse2D.Double(crds.get(0)-size/2,crds.get(1)-size/2,size,size));
			g2d.setColor(Color.WHITE);
			g2d.fill(s);
			g2d.setColor(Color.BLACK);
			g2d.draw(s);	
			Point2D _p2d = new Point2D.Double(crds.get(0),crds.get(1));
			zp.ucs2pixel(_p2d,_p2d);
			
			if(displayNodeNames) {
				TextLayout layout = new TextLayout(ntv.getName(), new Font("Arial", Font.PLAIN, 12),g2d.getFontRenderContext());
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
		}
		g2d.setColor(_color);
		g2d.setFont(_font);
		
	}
	
	public Rectangle2D bounds() {
		if(m_bounds == null){
			Iterator<NodeValue> iter = m_recorder.iterate();
			NodeValue ntv;
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