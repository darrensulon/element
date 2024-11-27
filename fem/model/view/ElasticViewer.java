package fem.model.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fem.model.FemModel;
import fem.model.output.Renderer;
import fem.model.output.displaced.DisplacedElementRecorder;
import fem.model.output.displaced.DisplacedElementRenderer;
import fem.model.output.dualBoundaryCondition.AbstractDualBoundaryConditionRecorder;
import fem.model.output.dualBoundaryCondition.DualBoundaryConditonRenderer;
import fem.model.output.dualBoundaryCondition.StandardDualBoundaryConditionRecorder;
import fem.model.output.element.ElementRecorder;
import fem.model.output.element.ElementRenderer;
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

public class ElasticViewer extends Viewer implements ActionListener{
	
	private static final long serialVersionUID = 1L;

	public ElasticViewer(List<Renderer> _renderers, JInternalFrame parent, FemModel fem, File inputFile) {
		super(_renderers, parent, fem, inputFile);
	}
	
	public void resetRenderers() {
		renderers.clear();
		ElementRecorder er = (ElementRecorder) fem
				.recordState(new ElementRecorder());
		renderers.add(new ElementRenderer(er, false, true));
		NodeRecorder nr = (NodeRecorder) fem
				.recordState(new NodeRecorder());
		renderers.add(new NodeRenderer(nr, true));
		PrimalBoundaryConditionRecorder pbcr = (PrimalBoundaryConditionRecorder) fem
				.recordState(new PrimalBoundaryConditionRecorder());
		renderers.add(new PrimalBoundaryConditionRenderer(pbcr));
		AbstractDualBoundaryConditionRecorder dbc = (AbstractDualBoundaryConditionRecorder) fem
				.recordState(new StandardDualBoundaryConditionRecorder());
		renderers.add(new DualBoundaryConditonRenderer(dbc));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		this.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if (ElasticGraphicalOutputOptions.DISP11.equals(e.getActionCommand())) {
				resetRenderers();
				AbstractNodeDisplacementRecorder nd = (AbstractNodeDisplacementRecorder) fem
						.recordState(new StandardNodeDisplacementRecorder(ElasticGraphicalOutputOptions.U_11));
				renderers.add(new NodeDisplacementRenderer(nd));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.DISP11);
		}else if (ElasticGraphicalOutputOptions.DISP22.equals(e.getActionCommand())) {
				resetRenderers();
				AbstractNodeDisplacementRecorder nd = (AbstractNodeDisplacementRecorder) fem
						.recordState(new StandardNodeDisplacementRecorder(ElasticGraphicalOutputOptions.U_22));
				renderers.add(new NodeDisplacementRenderer(nd));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.DISP22);
		}else if (ElasticGraphicalOutputOptions.DISP_RES.equals(e.getActionCommand())) {
				resetRenderers();
				AbstractNodeDisplacementRecorder nd = (AbstractNodeDisplacementRecorder) fem
						.recordState(new StandardNodeDisplacementRecorder(ElasticGraphicalOutputOptions.U_RES));
				renderers.add(new NodeDisplacementRenderer(nd));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.DISP_RES);
		} else if (ElasticGraphicalOutputOptions.DISP_SHAPE.equals(e.getActionCommand())) {
				renderers.clear();
				ElementRecorder er = (ElementRecorder) fem
						.recordState(new ElementRecorder());
				renderers.add(new ElementRenderer(er, false, true));
				DisplacedElementRecorder der = (DisplacedElementRecorder) fem
						.recordState(new DisplacedElementRecorder());
				renderers.add(new DisplacedElementRenderer(der, false, true));
				PrimalBoundaryConditionRecorder pbcr = (PrimalBoundaryConditionRecorder) fem
						.recordState(new PrimalBoundaryConditionRecorder());
				renderers.add(new PrimalBoundaryConditionRenderer(pbcr));
				AbstractDualBoundaryConditionRecorder dbc = (AbstractDualBoundaryConditionRecorder) fem
						.recordState(new StandardDualBoundaryConditionRecorder());
				renderers.add(new DualBoundaryConditonRenderer(dbc));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.DISP_SHAPE);
		}else if (ElasticGraphicalOutputOptions.STRESS11.equals(e.getActionCommand())) {
				String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
				int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				if (selection != JOptionPane.CLOSED_OPTION) {
					resetRenderers();
					AbstractStressRecorder ips = (AbstractStressRecorder) fem
							.recordState(new StandardStressRecorder(ElasticGraphicalOutputOptions.S_11, selection));
					renderers.add(new StressRenderer(ips));
					parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRESS11);
				}
		}else if (ElasticGraphicalOutputOptions.STRESS22.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStressRecorder ips = (AbstractStressRecorder) fem
						.recordState(new StandardStressRecorder(ElasticGraphicalOutputOptions.S_22, selection));
				renderers.add(new StressRenderer(ips));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRESS22);
			}
		}else if (ElasticGraphicalOutputOptions.STRESS12.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStressRecorder ips = (AbstractStressRecorder) fem
						.recordState(new StandardStressRecorder(ElasticGraphicalOutputOptions.S_12, selection));
				renderers.add(new StressRenderer(ips));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRESS12);
			}
		}else if (ElasticGraphicalOutputOptions.STRESS_INPLANE1.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStressRecorder ips = (AbstractStressRecorder) fem
						.recordState(new StandardStressRecorder(ElasticGraphicalOutputOptions.S_INPLANE1, selection));
				renderers.add(new StressRenderer(ips));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRESS_INPLANE1);
			}
		}else if (ElasticGraphicalOutputOptions.STRESS_INPLANE2.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStressRecorder ips = (AbstractStressRecorder) fem
						.recordState(new StandardStressRecorder(ElasticGraphicalOutputOptions.S_INPLANE2, selection));
				renderers.add(new StressRenderer(ips));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRESS_INPLANE2);
			}
		}else if (ElasticGraphicalOutputOptions.STRAIN11.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStrainRecorder isr = (AbstractStrainRecorder) fem
						.recordState(new StandardStrainRecorder(ElasticGraphicalOutputOptions.E_11, selection));
				renderers.add(new StrainRenderer(isr));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRAIN11);
			}
		}else if (ElasticGraphicalOutputOptions.STRAIN22.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStrainRecorder isr = (AbstractStrainRecorder) fem
						.recordState(new StandardStrainRecorder(ElasticGraphicalOutputOptions.E_22, selection));
				renderers.add(new StrainRenderer(isr));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRAIN22);
			}
		}else if (ElasticGraphicalOutputOptions.STRAIN12.equals(e.getActionCommand())) {
			String[] options  = new String[] {ElasticGraphicalOutputOptions.AT_GP, ElasticGraphicalOutputOptions.AT_CENTRE};
			int selection = JOptionPane.showOptionDialog(this.getParent(), "Select point to display '"+e.getActionCommand()+"'", "Stress/Strain Options Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (selection != JOptionPane.CLOSED_OPTION) {
				resetRenderers();
				AbstractStrainRecorder isr = (AbstractStrainRecorder) fem
						.recordState(new StandardStrainRecorder(ElasticGraphicalOutputOptions.E_12, selection));
				renderers.add(new StrainRenderer(isr));
				parent.setTitle("Graphical Display Showing: "+ ElasticGraphicalOutputOptions.STRAIN12);
			}
		}
		
		this.repaint();
		this.getParent().setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void initializeParent() {
		this.setBackground(Color.BLACK);
		parent.setBackground(Color.BLACK);
		BorderLayout layout = new BorderLayout();
		parent.setLayout(layout);
		parent.getInsets().set(10, 10, 10, 10);
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
	
	protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu displacementsOptionsMenu = new JMenu("Displacements");
        menuBar.add(displacementsOptionsMenu);
        
        JMenuItem u11DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.DISP11);
        u11DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.DISP11);
        u11DisplayItem.setToolTipText("Displays the displacement in X1 direction at the nodes as a vector");
        u11DisplayItem.addActionListener(this);
        displacementsOptionsMenu.add(u11DisplayItem);
        
        JMenuItem u22DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.DISP22);
        u22DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.DISP22);
        u22DisplayItem.setToolTipText("Displays the displacement in X2 direction at the nodes as a vector");
        u22DisplayItem.addActionListener(this);
        displacementsOptionsMenu.add(u22DisplayItem);
        
        JMenuItem uResultDisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.DISP_RES);
        uResultDisplayItem.setActionCommand(ElasticGraphicalOutputOptions.DISP_RES);
        uResultDisplayItem.setToolTipText("Displays the resultant displacement at the nodes as a vector");
        uResultDisplayItem.addActionListener(this);
        displacementsOptionsMenu.add(uResultDisplayItem);
        
        JMenuItem uShapeDisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.DISP_SHAPE);
        uShapeDisplayItem.setActionCommand(ElasticGraphicalOutputOptions.DISP_SHAPE);
        uShapeDisplayItem.setToolTipText("Displays the displaced model");
        uShapeDisplayItem.addActionListener(this);
        displacementsOptionsMenu.add(uShapeDisplayItem);
        
        JMenu StressesOptionsMenu = new JMenu("Stresses");
        menuBar.add(StressesOptionsMenu);
        
        JMenuItem s11DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRESS11);
        s11DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRESS11);
        s11DisplayItem.setToolTipText("Displays the normal stresses in X1 direction at the gauss points or centre of element as a vector");
        s11DisplayItem.addActionListener(this);
        StressesOptionsMenu.add(s11DisplayItem);
        
        JMenuItem s22DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRESS22);
        s22DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRESS22);
        s22DisplayItem.setToolTipText("Displays the normal stresses in X2 direction at the gauss points or centre of element as a vector");
        s22DisplayItem.addActionListener(this);
        StressesOptionsMenu.add(s22DisplayItem);
        
        JMenuItem s12DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRESS12);
        s12DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRESS12);
        s12DisplayItem.setToolTipText("Displays the shear stress in X1-X2 plane at the gauss points or centre of element");
        s12DisplayItem.addActionListener(this);
        StressesOptionsMenu.add(s12DisplayItem);
        
        JMenuItem sMax1DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRESS_INPLANE1);
        sMax1DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRESS_INPLANE1);
        sMax1DisplayItem.addActionListener(this);
        StressesOptionsMenu.add(sMax1DisplayItem);
        
        JMenuItem sMax2DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRESS_INPLANE2);
        sMax2DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRESS_INPLANE2);
        sMax2DisplayItem.addActionListener(this);
        StressesOptionsMenu.add(sMax2DisplayItem);
        
        JMenu StrainsOptionsMenu = new JMenu("Strains");
        menuBar.add(StrainsOptionsMenu);
        
        JMenuItem strain11DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRAIN11);
        strain11DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRAIN11);
        strain11DisplayItem.setToolTipText("Displays the normal strains in X1 direction at the gauss points or centre of element as a vector");
        strain11DisplayItem.addActionListener(this);
        StrainsOptionsMenu.add(strain11DisplayItem);
        
        JMenuItem strain22DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRAIN22);
        strain22DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRAIN22);
        strain22DisplayItem.setToolTipText("Displays the normal strains in X2 direction at the gauss points or centre of element as a vector");
        strain22DisplayItem.addActionListener(this);
        StrainsOptionsMenu.add(strain22DisplayItem);
        
        JMenuItem strains12DisplayItem = new JMenuItem(ElasticGraphicalOutputOptions.STRAIN12);
        strains12DisplayItem.setActionCommand(ElasticGraphicalOutputOptions.STRAIN12);
        strains12DisplayItem.setToolTipText("Displays the shear strains in X1-X2 plane at the gauss points or centre of element");
        strains12DisplayItem.addActionListener(this);
        StrainsOptionsMenu.add(strains12DisplayItem);
        
        return menuBar;
    }
	
	

}
