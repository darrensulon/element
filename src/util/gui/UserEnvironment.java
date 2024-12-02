package util.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import fem.analysis.Analysis;
import fem.model.FemModel;
import fem.model.output.Renderer;
import fem.model.output.displaced.DisplacedElementRecorder;
import fem.model.output.displaced.DisplacedElementRenderer;
import fem.model.output.dualBoundaryCondition.AbstractDualBoundaryConditionRecorder;
import fem.model.output.dualBoundaryCondition.DualBoundaryConditonRenderer;
import fem.model.output.dualBoundaryCondition.StandardDualBoundaryConditionRecorder;
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
import fem.model.output.nodeDisplacement.AbstractNodeDisplacementRecorder;
import fem.model.output.nodeDisplacement.NodeDisplacementRenderer;
import fem.model.output.nodeDisplacement.StandardNodeDisplacementRecorder;
import fem.model.output.primalBoundaryCondition.PrimalBoundaryConditionRecorder;
import fem.model.output.primalBoundaryCondition.PrimalBoundaryConditionRenderer;
import fem.model.output.strain.AbstractStrainRecorder;
import fem.model.output.strain.StandardStrainRecorder;
import fem.model.output.strain.StrainRenderer;
import fem.model.output.stresses.AbstractStressRecorder;
import fem.model.output.stresses.StandardStressRecorder;
import fem.model.output.stresses.StressRenderer;
import fem.model.view.ElasticGraphicalOutputOptions;
import fem.model.view.ElasticViewer;
import fem.model.view.Viewer;


public class UserEnvironment extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private boolean outputOpen = false;
	private boolean startOpen = false;
	private boolean graphicalOutputOpen = false;
	private SystemOutStream outputStream;
	private InternalAnalysisFrame startFrame;
	private InternalOutputFrame outputFrame;
	private JInternalFrame viewFrame;

	public UserEnvironment(String title, SystemOutStream outputStream) throws IOException {
		super(title);
		this.outputStream = outputStream;
		initializeFrame();
	}
	
    
	
	private JDesktopPane desktop;
	private void initializeFrame() throws IOException {
//		Image img = new ImageIcon(System.getProperty("user.dir")+"/display/background.jpg").getImage();
		desktop = new JDesktopPane() {
			public void paintComponent(Graphics g) {
//			        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
//			        super.paintComponent(g);
			        Graphics2D g2d = (Graphics2D) g;
			        int w = getWidth();
			        int h = getHeight();
			        Color color1 = Color.DARK_GRAY;
			        Color color2 = Color.BLACK;
			        GradientPaint gp = new GradientPaint(0, 0, color1, w/4, h, color2);
			        g2d.setPaint(gp);
			        g2d.fillRect(0, 0, w, h);
			        g2d.setColor(Color.WHITE);
			        g2d.setFont(new Font("Arial", Font.BOLD, 16));
			        g2d.drawString("ELEMENT - Stellenbosch University Department of Civil Engineering", w - 550, h - 20);
			}
		};
		setContentPane(desktop);
		setJMenuBar(createMenuBar());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setVisible(true);
	}
	
	protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        //Set up the lone menu.
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        //Set up the first menu item in file menu.
        JMenuItem newAnalysisItem = new JMenuItem("New Analysis");
        newAnalysisItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        newAnalysisItem.setActionCommand("new");
        newAnalysisItem.addActionListener(this);
        fileMenu.add(newAnalysisItem);

        //Set up the second menu item in file menu.
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        quitItem.setActionCommand("quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
        
        JMenu outputMenu = new JMenu("Display");
        outputMenu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(outputMenu);
        
        //Set up the first menu item in display menu.
        JMenuItem analysisOutputDisplayItem = new JMenuItem("Show Output");
        analysisOutputDisplayItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        analysisOutputDisplayItem.setActionCommand("output");
        analysisOutputDisplayItem.addActionListener(this);
        outputMenu.add(analysisOutputDisplayItem);
        
        //Set up the first menu item in display menu.
        JMenuItem analysisGraphicalDisplayItem = new JMenuItem("Show Graphical Output");
        analysisGraphicalDisplayItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_G, ActionEvent.ALT_MASK));
        analysisGraphicalDisplayItem.setActionCommand("graphical");
        analysisGraphicalDisplayItem.addActionListener(this);
        outputMenu.add(analysisGraphicalDisplayItem);
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        
        JMenuItem thisHelpItem = new JMenuItem("App Documentation");
        thisHelpItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, ActionEvent.ALT_MASK));
        thisHelpItem.setActionCommand("javadocs");
        thisHelpItem.addActionListener(this);
        helpMenu.add(thisHelpItem);
        
        JMenuItem javaAPIItem = new JMenuItem("Java API Documentation");
        javaAPIItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_J, ActionEvent.ALT_MASK));
        javaAPIItem.setActionCommand("api");
        javaAPIItem.addActionListener(this);
        helpMenu.add(javaAPIItem);
        
        JMenuItem usermanualItem = new JMenuItem("Skripsie Document");
        usermanualItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.ALT_MASK));
        usermanualItem.setActionCommand("user manual");
        usermanualItem.addActionListener(this);
        helpMenu.add(usermanualItem);
        
        return menuBar;
    }
	
	//React to menu selections.
    public void actionPerformed(ActionEvent e) {
    	
    	
        if ("new".equals(e.getActionCommand())) { //new
            if(startOpen == false) {
            	startFrame = new InternalAnalysisFrame("Analysis", true, true, true, true) {
        			public void dispose() {
        				startOpen = false;
        				if (outputOpen) {
        					try {
								outputFrame.setClosed(true);
							} catch (PropertyVetoException e) {
								e.printStackTrace();
							}
        				}
        				if (graphicalOutputOpen) {
        					try {
								viewFrame.setClosed(true);
							} catch (PropertyVetoException e) {
								e.printStackTrace();
							}
        				}
        			}
        		};
        		this.add(startFrame);
        		outputStream.setText(null);
        		startOpen = true;
            } else {
            	JOptionPane.showMessageDialog(this, "In order to start new analysis you must close the current one!", "Warning!", JOptionPane.ERROR_MESSAGE);
            }
            
            
        }else if ("quit".equals(e.getActionCommand())) { //quit
            quit();
            
            
        }else if ("output".equals(e.getActionCommand())) { 
        	if(startFrame == null ||startOpen == false) {
        		//fire warning
        		JOptionPane.showMessageDialog(this, "No analysis open!", "Warning!", JOptionPane.ERROR_MESSAGE);
        	}else if (startFrame.getStatus() == false ) {
        		//fire warning
        		JOptionPane.showMessageDialog(this, "Analysis not yet performed!", "Warning!", JOptionPane.ERROR_MESSAGE);
        	}else {
        		if(outputOpen == false) {
            		outputFrame = new InternalOutputFrame("Output", true, true, true, true, outputStream) {
            			public void dispose() {
            				outputOpen = false;
            			}
            		};
            		this.add(outputFrame);
                	outputOpen = true;
        		}else {
        			JOptionPane.showMessageDialog(this, "Output is already open!", "Warning!", JOptionPane.ERROR_MESSAGE);
        		}
        	}
        	
        	
       	}else if ("graphical".equals(e.getActionCommand())) { 
	       		if(startFrame == null ||startOpen == false) {
	       			//fire warning
	       			JOptionPane.showMessageDialog(this, "No analysis open!", "Warning!", JOptionPane.ERROR_MESSAGE);
		       	}else if (startFrame.getStatus() == false ) {
		       		//fire warning
		       		JOptionPane.showMessageDialog(null, "Analysis not yet performed!", "Warning!", JOptionPane.ERROR_MESSAGE);
		       	}else {
		       		if(graphicalOutputOpen == false) {
		       			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		       			getGraphicalDisplay();
		       			setCursor(Cursor.getDefaultCursor());
		       		}else {
		       			JOptionPane.showMessageDialog(this, "Graphical Display is already open!", "Warning!", JOptionPane.ERROR_MESSAGE);
		       		}
		       	}
        }else if ("javadocs".equals(e.getActionCommand())) {
			try {
				Desktop.getDesktop().open(new File(System.getProperty("user.dir")+"/javadocs/index.html"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        }else if ("api".equals(e.getActionCommand())) {
        	URI uri;
			try {
				uri = new URI("https://docs.oracle.com/javase/7/docs/api/");
				Desktop.getDesktop().browse(uri);
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }else if ("user manual".equals(e.getActionCommand())) {
        	try {
				Desktop.getDesktop().open(new File(System.getProperty("user.dir")+"/resource/skripsie.pdf"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
    }
    
    public void getGraphicalDisplay() {
    	
    	FemModel fem = startFrame.getFemModel();
    	List<Renderer> _mlist = new ArrayList<Renderer>();
		
    	if(fem.getChosenAnalysis().equals(Analysis.HEAT_TRANSFER)||
				fem.getChosenAnalysis().equals(Analysis.SEEPAGE)||
				fem.getChosenAnalysis().equals(Analysis.ELECTROSTATIC)||
				fem.getChosenAnalysis().equals(Analysis.POTENTIAL_FLOW)) {  
    		
//    		IsoAreaRenderer iar = new IsoAreaRenderer();
//    		iar.record(fem);
//    		_mlist.add(iar);
    		ElementRecorder er = (ElementRecorder) fem
    				.recordState(new ElementRecorder());
    		_mlist.add(new ElementRenderer(er, false, false)); // show/not show element names
    		NodeRecorder nr = (NodeRecorder) fem
    				.recordState(new NodeRecorder());
    		_mlist.add(new NodeRenderer(nr, false));
//			AbstractGradientVectorRecorder hs = (AbstractGradientVectorRecorder) fem
//					.recordState(new StandardGradientVectorRecorder(2));
//			_mlist.add(new GradientVectorRenderer(hs));
//			NodalFieldStateRecorder tr = (NodalFieldStateRecorder) fem
//					.recordState(new NodalFieldStateRecorder());
//				_mlist.add(new NodalFieldStateRenderer(tr));
			
				viewFrame = new JInternalFrame("Graphical Display", true, true, true, true) {
					public void dispose() {
						graphicalOutputOpen = false;
					}
					public void paintComponent(Graphics g) {
						Image img = new ImageIcon(System.getProperty("user.dir")+"/display/background.jpg").getImage();
				        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
					}
				};
				graphicalOutputOpen = true;
				Viewer viewer = new Viewer(_mlist, viewFrame, fem, startFrame.getInputFile());
				viewer._layout();
				this.add(viewer.getViewer());
				
		}else if(fem.getChosenAnalysis().equals(Analysis.ELASTICITY)) {

				ElementRecorder er = (ElementRecorder) fem
						.recordState(new ElementRecorder());
				_mlist.add(new ElementRenderer(er, false, true)); // show/not show element names
				NodeRecorder nr = (NodeRecorder) fem
						.recordState(new NodeRecorder());
				_mlist.add(new NodeRenderer(nr, true));
				PrimalBoundaryConditionRecorder pbcr = (PrimalBoundaryConditionRecorder) fem
						.recordState(new PrimalBoundaryConditionRecorder());
				_mlist.add(new PrimalBoundaryConditionRenderer(pbcr));
				AbstractDualBoundaryConditionRecorder dbc = (AbstractDualBoundaryConditionRecorder) fem
						.recordState(new StandardDualBoundaryConditionRecorder());
				_mlist.add(new DualBoundaryConditonRenderer(dbc));

				
				viewFrame = new JInternalFrame("Graphical Display Showing: General Layout", true, true, true, true) {
					public void dispose() {
						graphicalOutputOpen = false;
					}
					public void paintComponent(Graphics g) {
						Image img = new ImageIcon(System.getProperty("user.dir")+"/display/background.jpg").getImage();
				        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
					}
				};
				graphicalOutputOpen = true;
				ElasticViewer viewer = new ElasticViewer(_mlist, viewFrame, fem, startFrame.getInputFile());
				viewer._layout();
				this.add(viewer.getViewer());
			
		}
    }
    
    
    private static class ExtensionFilter extends FileFilter {
		Collection<String> extensions;

		public ExtensionFilter(Collection<String> extensions) {
			super();
			this.extensions = extensions;
		}

		// Accept all directories and inp files.
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}

			if (ext != null) {
				if (extensions.contains(ext)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		public String getDescription() {
			StringBuffer sb = new StringBuffer();
			for (String ext : extensions) {
				sb.append("*." + ext + " ");
			}
			return sb.toString();
		}
	}
	
	
	 //Quit the application.
    protected void quit() {
        System.exit(0);
    }
    

}
