package fem.model.output;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import util.gui.ZoomPanel;

public interface Renderer {

	public Rectangle2D bounds();
	
	public void render(Graphics2D g2d, ZoomPanel zp);
	
}
