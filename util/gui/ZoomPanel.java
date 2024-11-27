/*
 * Created on 2005/01/21
 * by AH Olivier
 */
package util.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Bertie
 *
 */
public class ZoomPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Zoomer zmr;
	private DragBoxSelecter dbs;
	
    private AffineTransform pixelToCrdsSystem;
    private AffineTransform worldToPixel;
	
    private boolean isLocked = false;
    
    protected boolean clip = true;
    
    protected boolean engineeringCrds = true;
    
    public int border = 40;
    
    public ZoomPanel(){
        zmr = new Zoomer(this);
        dbs = new DragBoxSelecter();
        enableZoom(true);
        enableDB(true);
        setDoubleBuffered(true);
    }
    
    public double getScale(){
    	return worldToPixel.getScaleX();
    }
    
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if(pixelToCrdsSystem == null){
        	if(engineeringCrds)
        		setTransform(new AffineTransform(0.25,0.,0.,-0.25, 50.,getHeight() - 50.));
        	else
        		setTransform(new AffineTransform(0.25,0.,0.,0.25, 0., 0.));
	        try {
				this.worldToPixel = pixelToCrdsSystem.createInverse();
			} catch (NoninvertibleTransformException e) {
				_recoverFromZoomException();
			}
        }
        zoomAll();
    }
    
    public void zoomAll(){
        Rectangle2D r2d = new Rectangle2D.Double();
        r2d.setFrame(calcBounds());
        zoomBox(r2d);
    }
    
    public Rectangle2D calcBounds(){
    	return new Rectangle2D.Double(0,0,100,75);
    }
   
    public AffineTransform transformCoordinateSystem(AffineTransform tx){
    	AffineTransform old = new AffineTransform(pixelToCrdsSystem);
    	pixelToCrdsSystem.preConcatenate(tx);
        try {
			this.worldToPixel = pixelToCrdsSystem.createInverse();
		} catch (NoninvertibleTransformException e) {
			_recoverFromZoomException();
		}
    	return old;
    }
    
    public AffineTransform getTransform() {
        return (AffineTransform)pixelToCrdsSystem.clone();
    }
    protected AffineTransform getInvTransform(){
    	return (AffineTransform)worldToPixel.clone();
    }
    public void setTransform(AffineTransform transform) {
        this.pixelToCrdsSystem = transform;
        try {
			this.worldToPixel = pixelToCrdsSystem.createInverse();
		} catch (NoninvertibleTransformException e) {
			_recoverFromZoomException();
		}
    }    
    
    public void paintComponent(Graphics g) {
    	try{
    		super.paintComponent(g);
    	}
    	catch(NullPointerException npe){}
    	if(clip)
    		g.clipRect(border-10, border-10, getWidth()-2*border+20, getHeight()-2*border+20);
    	g.setColor(Color.BLACK);
    }
    
    public Shape transformShapeToPanel(Shape shape){
		return getTransform().createTransformedShape(shape);
    }
    
    public void zoomBox(){
    	BoxSelecter boxSelecter = new BoxSelecter();
    	addMouseListener(boxSelecter);
    	addMouseMotionListener(boxSelecter);
    	enableZoom(false);
    }
    
    // box should be in panel coordinates
   public void zoomBox(Rectangle2D box){
		if (box == null)
			return;
		double scale = Math.min(
				(getWidth() * 1. - 2*border) / (box.getWidth()), 
				(getHeight() * 1. - 2*border)/ (box.getHeight()));
		AffineTransform _pixelToCrdsSystem = new AffineTransform();
		_pixelToCrdsSystem.translate(border,border);
		if(engineeringCrds){
			_pixelToCrdsSystem.scale(scale,-scale);
			_pixelToCrdsSystem.translate(-box.getX(),-box.getHeight()-box.getY());
		}
		else{
			_pixelToCrdsSystem.scale(scale,scale);
			_pixelToCrdsSystem.translate(-box.getX(),-box.getY());
		}
		
		// centre transform
		if((getHeight()-2*border)/box.getHeight() < (getWidth()-2*border)/box.getWidth())
			_pixelToCrdsSystem.translate((getWidth()/scale-2*border/scale-box.getWidth())/2.,0);// centre x
		else if(engineeringCrds)
			_pixelToCrdsSystem.translate(0,-(getHeight()/scale-2*border/scale-box.getHeight())/2.);// centre y
		else
			_pixelToCrdsSystem.translate(0,(getHeight()/scale-2*border/scale-box.getHeight())/2.);// centre y
		
		setTransform(_pixelToCrdsSystem);
		
		repaint();
   }
    
    /** This method tries to recover from a NoninvertibleTransformException.
	 * 
	 */
	private void _recoverFromZoomException() {
		pixelToCrdsSystem = new AffineTransform(1, 0., 0., -1, 50., getHeight() - 50.);
		try {
			worldToPixel = pixelToCrdsSystem.createInverse();
			System.err.println("Zoom reset!");
		} catch (NoninvertibleTransformException e) {
			System.err.println("COULD NOT RESET PANEL TRANSFORMATION");
			e.printStackTrace();
			System.exit(1);
		}
	}
   
    public void enableZoom(boolean enable) {
        if(enable) {
            addMouseListener(zmr);
            addMouseMotionListener(zmr);
            addMouseWheelListener(zmr); 
            isLocked = false;
        }
        else {
            removeMouseListener(zmr);
            removeMouseMotionListener(zmr);
            removeMouseWheelListener(zmr);
            isLocked = true;
        }
    }

    public void enableDB(boolean enable) {
        if(enable) {
            addMouseListener(dbs);
            addMouseMotionListener(dbs);
            addMouseWheelListener(dbs); 
        }
        else {
            removeMouseListener(dbs);
            removeMouseMotionListener(dbs);
            removeMouseWheelListener(dbs);
        }
    }
    
    public boolean isLocked(){
    	return isLocked;
    }
    
	public Point2D pixel2ucs(Point2D src,Point2D dst) {
        return dst = getInvTransform().transform(src,dst);
    }
    public Point2D ucs2pixel(Point2D src,Point2D dst) {
        return getTransform().transform(src,dst);
    }

    private class Zoomer extends MouseAdapter implements MouseMotionListener, MouseWheelListener {

    	private Point[] dragCrds = new Point[2];
    	private ZoomPanel zpanel;
    	
    	private Zoomer(ZoomPanel zp){
    	    this.zpanel = zp;
    	}
    	
    	public void zoom(double factor, Point2D p){
    	    AffineTransform vcs = zpanel.getTransform();
    		double[] mat = new double[6];
    		vcs.getMatrix(mat);
    		double dX = (-p.getX() + mat[4]) * factor + p.getX();
    		double dY = (-p.getY() + mat[5]) * factor + p.getY();
    		zpanel.setTransform(new AffineTransform(mat[0] * factor,0.,0.,mat[3] * factor,dX,dY));
    	}
    	public void pan(Point2D[] point){
    	    AffineTransform vcs = zpanel.getTransform();
    		double[] mat = new double[6];
    		vcs.getMatrix(mat);
    		double dX = mat[4] + point[1].getX() - point[0].getX();
    		double dY = mat[5] + point[1].getY() - point[0].getY();
    		zpanel.setTransform(new AffineTransform(mat[0], 0., 0., mat[3], dX, dY));
    		zpanel.repaint();
    	}
        public void mouseReleased(MouseEvent e) {
            dragCrds[0] = null;
        }
        public void mouseDragged(MouseEvent me) {
    		if (SwingUtilities.isMiddleMouseButton(me))	{
    			if (dragCrds[0] == null)
    			{
    				dragCrds[0] = me.getPoint();
    			}
    			else
    			{
    				dragCrds[1] = me.getPoint();
    				pan(dragCrds);
    				dragCrds[0] = dragCrds[1];
    			}
    			zpanel.repaint();
    		}
        }
        public void mouseMoved(MouseEvent e) {}
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            int count = mwe.getWheelRotation();
    		int direction = (count > 0) ? 1 : 2;
    		if (direction == 1)
    		{
    			zoom(0.9, mwe.getPoint());
    		}
    		else if (direction == 2)
    		{
    			zoom(1.1, mwe.getPoint());
    		}
    		zpanel.repaint();
        }
    }    
    
    private class BoxSelecter extends MouseAdapter implements MouseMotionListener{
		private Point2D start;
		private Point2D end;
		private Point2D current = new Point2D.Double();
		private Point2D _prev;
		private Rectangle2D r2d = new Rectangle2D.Double();
		
		public void mousePressed(MouseEvent e) {
			if(start == null)
				start = pixel2ucs(e.getPoint(),null);
			else if(end == null){
				end = pixel2ucs(e.getPoint(),end);				
				r2d.setFrameFromDiagonal(start, end);
				zoomBox(r2d);
				removeMouseListener(this);
				removeMouseMotionListener(this);
				enableZoom(true);
			}
		}

		public void mouseDragged(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) { 
			if(start!=null){
				current = pixel2ucs(e.getPoint(),current);
				Graphics2D g2d = (Graphics2D)getGraphics();
				g2d.setColor(Color.GREEN);
				g2d.setXORMode(new Color(255,255,243));
				if(_prev!= null){
					Rectangle2D r2d = new Rectangle2D.Double();
					r2d.setFrameFromDiagonal(start, _prev);
					g2d.draw(transformShapeToPanel(r2d));
				}
				else{
					_prev = new Point2D.Double();
				}
				Rectangle2D r2d = new Rectangle2D.Double();
				r2d.setFrameFromDiagonal(start, current);
				g2d.draw(transformShapeToPanel(r2d));
				_prev.setLocation(current);
				
			}
		}
    }
    private class DragBoxSelecter extends MouseAdapter implements MouseMotionListener{
		private Point2D start;
		private Point2D end;
		private Point2D current = new Point2D.Double();
		private Point2D _prev;
		private Rectangle2D r2d = new Rectangle2D.Double();
		
		public DragBoxSelecter(){ }
		
		public void mousePressed(MouseEvent e) {
			if(!SwingUtilities.isRightMouseButton(e))
				return;
			enableZoom(false);
			start = pixel2ucs(e.getPoint(),null);
		}

		public void mouseReleased(MouseEvent e){
			if(!SwingUtilities.isRightMouseButton(e))
				return;
			end = pixel2ucs(e.getPoint(),end);				
			r2d.setFrameFromDiagonal(start, end);
			start = null;
			_prev = null;
			enableZoom(true);
			if(r2d.getWidth()>0 && r2d.getHeight()>0)
				zoomBox(r2d);
			else
				zoomAll();
		}
		
		public void mouseDragged(MouseEvent e) {
			if(!SwingUtilities.isRightMouseButton(e))
				return;
			if(start != null){
				current = pixel2ucs(e.getPoint(),current);
				Graphics2D g2d = (Graphics2D)getGraphics();
				g2d.setColor(Color.GREEN);
				g2d.setXORMode(new Color(255,255,243));
				if(_prev!= null){
					Rectangle2D r2d = new Rectangle2D.Double();
					r2d.setFrameFromDiagonal(start, _prev);
					g2d.draw(transformShapeToPanel(r2d));
				}
				else{
					_prev = new Point2D.Double();
				}
				Rectangle2D r2d = new Rectangle2D.Double();
				r2d.setFrameFromDiagonal(start, current);
				g2d.draw(transformShapeToPanel(r2d));
				_prev.setLocation(current);
				
			}
		}

    }
}
