package fem.model.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import util.gui.ZoomPanel;
import fem.model.FemModel;
import fem.model.output.Renderer;
import fem.model.output.element.ElementRecorder;
import fem.model.output.element.ElementRenderer;
import fem.model.output.gradientVector.AbstractGradientVectorRecorder;
import fem.model.output.gradientVector.GradientVectorRenderer;
import fem.model.output.gradientVector.StandardGradientVectorRecorder;
import fem.model.output.isoArea.IsoAreaRenderer;
import fem.model.output.nodalFieldState.NodalFieldStateRecorder;
import fem.model.output.nodalFieldState.NodalFieldStateRenderer;
import fem.model.output.node.NodeRecorder;
import fem.model.output.node.NodeRenderer;

public class Viewer extends ZoomPanel implements ActionListener{
	
	protected JInternalFrame parent;
	protected File inputFile;
	private static final long serialVersionUID = 1L;
	protected List<Renderer> renderers;
	protected FemModel fem;
	
	private boolean displayNodeNames = false;
	private boolean displayNodePrimalValues = false;
	private boolean displayElementNames = false;
	private boolean displayGradientVectors = false;
	private boolean displayContours = false;
	
	public Viewer(List<Renderer> _renderers, JInternalFrame parent, FemModel fem, File inputFile){
		this.renderers = _renderers;
		this.parent = parent;
		this.inputFile = inputFile;
		this.fem = fem;
		enableZoom(true);
	}
	
	protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu addOptionsMenu = new JMenu("Add");
        menuBar.add(addOptionsMenu);
        
        JMenuItem addPrimalValuesDisplayItem = new JMenuItem("Primal Values at Nodes");
        addPrimalValuesDisplayItem.setActionCommand("add primal values");
        addPrimalValuesDisplayItem.setToolTipText("Displays the primal values at the nodes");
        addPrimalValuesDisplayItem.addActionListener(this);
        addOptionsMenu.add(addPrimalValuesDisplayItem);
        
        JMenuItem addContourDisplayItem = new JMenuItem("Primal Value contours");
        addContourDisplayItem.setActionCommand("add contours");
        addContourDisplayItem.setToolTipText("Displays the primal values as contours on the model");
        addContourDisplayItem.addActionListener(this);
        addOptionsMenu.add(addContourDisplayItem);
        
        JMenuItem addElementNameDisplayItem = new JMenuItem("Element Names");
        addElementNameDisplayItem.setActionCommand("add element names");
        addElementNameDisplayItem.setToolTipText("Displays the element names in the graphical output");
        addElementNameDisplayItem.addActionListener(this);
        addOptionsMenu.add(addElementNameDisplayItem);
        
        JMenuItem addNodeNameDisplayItem = new JMenuItem("Node Names");
        addNodeNameDisplayItem.setActionCommand("add node names");
        addNodeNameDisplayItem.setToolTipText("Displays the element names in the graphical output");
        addNodeNameDisplayItem.addActionListener(this);
        addOptionsMenu.add(addNodeNameDisplayItem);
        
        JMenuItem addGradientVectorDisplayItem = new JMenuItem("Gradient Vectors");
        addGradientVectorDisplayItem.setActionCommand("add gradient vectors");
        addGradientVectorDisplayItem.setToolTipText("Displays the gradient vector");
        addGradientVectorDisplayItem.addActionListener(this);
        addOptionsMenu.add(addGradientVectorDisplayItem);
        
        JMenu removeOptionsMenu = new JMenu("Remove");
        menuBar.add(removeOptionsMenu);
        
        JMenuItem removePrimalValuesDisplayItem = new JMenuItem("Primal Values at Nodes");
        removePrimalValuesDisplayItem.setActionCommand("remove primal values");
        removePrimalValuesDisplayItem.addActionListener(this);
        removeOptionsMenu.add(removePrimalValuesDisplayItem);
        
        JMenuItem removeContourDisplayItem = new JMenuItem("Primal Value contours");
        removeContourDisplayItem.setActionCommand("remove contours");
        removeContourDisplayItem.addActionListener(this);
        removeOptionsMenu.add(removeContourDisplayItem);
        
        JMenuItem removeElementNameDisplayItem = new JMenuItem("Element Names");
        removeElementNameDisplayItem.setActionCommand("remove element names");
        removeElementNameDisplayItem.addActionListener(this);
        removeOptionsMenu.add(removeElementNameDisplayItem);
        
        JMenuItem removeNodeNameDisplayItem = new JMenuItem("Node Names");
        removeNodeNameDisplayItem.setActionCommand("remove node names");
        removeNodeNameDisplayItem.addActionListener(this);
        removeOptionsMenu.add(removeNodeNameDisplayItem);
        
        JMenuItem removeGradientVectorDisplayItem = new JMenuItem("Gradient Vectors");
        removeGradientVectorDisplayItem.setActionCommand("remove gradient vectors");
        removeGradientVectorDisplayItem.addActionListener(this);
        removeOptionsMenu.add(removeGradientVectorDisplayItem);
        
        return menuBar;
    }
	
	public void initializeParent() {
		this.setBackground(Color.BLACK);
		parent.setBackground(Color.BLACK);
		BorderLayout layout = new BorderLayout();
		parent.setLayout(layout);
		JLabel fileName = new JLabel("Analysis performed on: " + inputFile.getName());
		fileName.setForeground(Color.WHITE);
		parent.setJMenuBar(createMenuBar());
		parent.add(fileName, BorderLayout.SOUTH);
		parent.add(this, BorderLayout.CENTER);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.width *= .6;
		dim.height = (int) (dim.height*.8 - 60);
		parent.setSize(dim.width, dim.height);
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xOffset = 20, yOffset = (int) (dim.height * .1) + 40;
		parent.setLocation(xOffset, yOffset);
		parent.setVisible(true);
	}
	
	 public Rectangle2D calcBounds(){
		 Rectangle2D _bounds = new Rectangle2D.Double(0,0,1,1);
		 Iterator<Renderer> iter = renderers.iterator();
		 if(iter.hasNext()){
			 _bounds = iter.next().bounds();
		 }
		 while(iter.hasNext()){
			 _bounds.add(iter.next().bounds());
		 }
		 // Add border of 5% of diagonal
		 double dia = Math.sqrt(_bounds.getWidth()*_bounds.getWidth()+_bounds.getHeight()*_bounds.getHeight());
		 double x = _bounds.getX() - .05*dia;
		 double y = _bounds.getY() - .05*dia;
		 double w =_bounds.getWidth() + 0.10*dia;
		 double h = _bounds.getHeight() + 0.10*dia;
		 return new Rectangle2D.Double(x,y,w,h);
	 }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		for(int i = 0; i < renderers.size(); i++)
			renderers.get(i).render(g2d, this);
	}
	
	public void dispose(){
		parent.remove(this);
	}
	
	public void _layout(){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run() {
					initializeParent();
				};
			});			
	}
	
	public JInternalFrame getViewer(){
		return parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		this.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if("add primal values".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof NodalFieldStateRenderer) {
					renderers.remove(index);
				}
				index++;
			}
			displayNodePrimalValues = true;
			NodalFieldStateRecorder tr = (NodalFieldStateRecorder) fem
					.recordState(new NodalFieldStateRecorder());
				renderers.add(new NodalFieldStateRenderer(tr));
		} else if("add node names".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof NodeRenderer) {
					renderers.remove(index);
				}
			}
			displayNodeNames = true;
			NodeRecorder nr = (NodeRecorder) fem
    				.recordState(new NodeRecorder());
    		renderers.add(new NodeRenderer(nr, displayNodeNames));
		}else if("add contours".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof IsoAreaRenderer) {
					renderers.remove(index);
				}
			}
			displayContours = true;
			IsoAreaRenderer iar = new IsoAreaRenderer();
    		iar.record(fem);
    		renderers.add(0, iar);
		} else if("add element names".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof ElementRenderer||r instanceof NodeRenderer) {
					renderers.remove(index);
				}
			}
			displayElementNames = true;
			ElementRecorder er = (ElementRecorder) fem
    				.recordState(new ElementRecorder());
    		renderers.add(new ElementRenderer(er, displayElementNames, false));
    		NodeRecorder nr = (NodeRecorder) fem
    				.recordState(new NodeRecorder());
    		renderers.add(new NodeRenderer(nr, displayNodeNames));
		} else if("add gradient vectors".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof GradientVectorRenderer) {
					renderers.remove(index);
				}
			}
			displayGradientVectors = true;
			AbstractGradientVectorRecorder hs = (AbstractGradientVectorRecorder) fem
					.recordState(new StandardGradientVectorRecorder(2));
			renderers.add(new GradientVectorRenderer(hs));
		} else if("remove primal values".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof NodalFieldStateRenderer) {
					renderers.remove(index);
				}
			}
			displayNodePrimalValues = false;
		} else if("remove contours".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof IsoAreaRenderer) {
					renderers.remove(index);
				}
			}
			displayContours = false;
		} else if("remove element names".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof ElementRenderer) {
					renderers.remove(index);
				}
			}
			displayElementNames = false;
			ElementRecorder er = (ElementRecorder) fem
    				.recordState(new ElementRecorder());
    		renderers.add(new ElementRenderer(er, displayElementNames, false));
		} else if("remove node names".equals(e.getActionCommand())) {
			for(int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof NodeRenderer) {
					renderers.remove(index);
				}
			}
			displayNodeNames = false;
			NodeRecorder nr = (NodeRecorder) fem
    				.recordState(new NodeRecorder());
    		renderers.add(new NodeRenderer(nr, displayNodeNames));
		} else if("remove gradient vectors".equals(e.getActionCommand())) {
			for (int index = 0; index < renderers.size(); index++) {
				Renderer r = renderers.get(index);
				if(r instanceof GradientVectorRenderer) {
					renderers.remove(index);
				}
			}
			displayGradientVectors = false;
		}
		
		this.repaint();
		this.getParent().setCursor(Cursor.getDefaultCursor());
	}
	
}
