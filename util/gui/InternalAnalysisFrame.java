package util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import fem.analysis.Analysis;
import fem.analysis.FirstOrderLinearAnalysis;
import fem.model.FemModel;
import fem.model.StationaryModelParser;

/**
 * An object which provides features of a native frame, including dragging,
 * closing, becoming an icon, resizing, title display, and support for a menu
 * bar. This object extends JInternalFrame with further additional objects
 * stored for the contents that need to be displayed in the frame as well as the
 * objects required to perform a FEM analysis.
 * 
 * @author Darren
 *
 */
public class InternalAnalysisFrame extends JInternalFrame implements ActionListener {

	/**
	 * A JPanel in which the the components of this frame are added.
	 */
	private JPanel startPanel;

	/**
	 * A GridBagLayout which is the layout manager for the panel of this class.
	 */
	private GridBagLayout g;

	/**
	 * Constraints for the layout of this class.
	 */
	GridBagConstraints c;

	/**
	 * A JLabel which instructs the user what action to take in the
	 * InternalStartAnalysisFrame.
	 */
	private JLabel instruction;

	/**
	 * A String array with all the possible fem analysis that can be performed in
	 * this program.
	 */
	private String[] types;

	/**
	 * A JComboBox which contains all the possible fem analysis that can be
	 * performed in this program.
	 */
	private JComboBox<String> analysisTypes;

	/**
	 * A String array with all the possible fem analysis descriptions that can be
	 * performed in this program.
	 */
	String[] descriptions;

	/**
	 * A JLabel which displays the analysis description of the selected item in the
	 * JComboBox analyisTypes.
	 */
	private JLabel analysisDescription;

	/**
	 * A JButton which when pressed the openInputFileDialog() method is called.
	 */
	private JButton start;

	/**
	 * A FemModel object which is required for an analysis to be performed.
	 */
	private FemModel fem;

	/**
	 * A StationaryModelParser required to read input from the file selected by the
	 * controller.
	 */
	private StationaryModelParser p;

	/**
	 * The input file on which the analysis is performed.
	 */
	private File inputFile;

	/**
	 * The status of the analysis. True if completed, false if not yet performed.
	 * Default is false.
	 */
	private boolean analysisCompleted = false;

	/**
	 * Creates an InternalStartAnalysis with the specified title, resizability,
	 * closability, maximizability, and iconifiability.
	 * 
	 * @param title       title
	 * @param resizable   resizability
	 * @param closable    closability
	 * @param maximizable maximizability
	 * @param iconifiable iconifiability
	 */
	public InternalAnalysisFrame(String title, boolean resizable, boolean closable, boolean maximizable,
			boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		initializeStartFrame();
	}

	/**
	 * This object and all its components are instantiated.
	 */
	private void initializeStartFrame() {
		p = new StationaryModelParser();
		fem = new FemModel();
		startPanel = new JPanel();
		startPanel.setBackground(Color.BLACK);
		g = new GridBagLayout();
		startPanel.setLayout(g);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(20, 10, 20, 10);

		// start frame components
		// create description label
		instruction = new JLabel("<html>Select finite element analysis which you would like to perform:<html>");
		instruction.setForeground(Color.WHITE);
		instruction.setMinimumSize(new Dimension(350, 40));
		instruction.setPreferredSize(new Dimension(350, 40));
		instruction.setMaximumSize(new Dimension(350, 40));
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		startPanel.add(instruction, c);

		// create jcombobox with multiple analysis types
		types = new String[4];
		types[0] = Analysis.HEAT_TRANSFER;
		types[1] = Analysis.ELASTICITY;
		types[2] = Analysis.SEEPAGE;
		types[3] = Analysis.POTENTIAL_FLOW;
		analysisTypes = new JComboBox<>();
		analysisTypes.setBackground(Color.DARK_GRAY);
		analysisTypes.setForeground(Color.WHITE);
		for (int i = 0; i < types.length; i++) {
			analysisTypes.addItem(types[i]);
		}
		analysisTypes.addActionListener(this);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		startPanel.add(analysisTypes, c);

		// create start button
		start = new JButton("Start Analysis");
		start.setBackground(Color.DARK_GRAY);
		start.setForeground(Color.WHITE);
		start.setActionCommand("start");
		start.setMinimumSize(new Dimension(100, 20));
		start.setPreferredSize(new Dimension(100, 20));
		start.setMaximumSize(new Dimension(100, 20));
		start.addActionListener(this);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		startPanel.add(start, c);

		// create analysis description label
		descriptions = new String[5];
		descriptions[0] = "<html>Description:<br>Analyse two-dimensional problems which involve determining the temperature distribution within a body and the heat moving into and out of the body.<html>";
		descriptions[1] = "<html>Description:<br>Analyse two-dimensional problems which involve determining the displacements within a body due to a given load for the plane stress case.<html>";
		descriptions[2] = "<html>Description:<br>Analyse two-dimensional problems which involve the flow of fluid through a porous media and/or around solid objects.<html>";
		descriptions[3] = "<html>Description:<br>Analyse two-dimensional case of idealized fluid flow that occurs in the case of incompressible, inviscid, and irrotational flow.<html>";
		descriptions[4] = "<html>Description:<br>Analyse two-dimensional electrostatic problems where electrical potentials and electric fields are of concern.<html>";
		analysisDescription = new JLabel();
		analysisDescription.setForeground(Color.WHITE);
		analysisDescription.setText(descriptions[0]);
		analysisDescription.setVerticalAlignment(JLabel.TOP);
		analysisDescription.setMinimumSize(new Dimension(300, 70));
		analysisDescription.setPreferredSize(new Dimension(300, 70));
		analysisDescription.setMaximumSize(new Dimension(300, 70));
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.gridheight = 1;
		startPanel.add(analysisDescription, c);

		// initialize start frame
		this.add(startPanel);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.width *= .3;
		dim.height *= .4;
		this.setSize(dim.width, dim.height);
		int xOffset = 20, yOffset = 20;
		this.setLocation(xOffset, yOffset);
		this.setVisible(true);
	}

	/**
	 * Invoked when an action occurs.
	 * 
	 * If action is performed on the JComboBox within this object, the description
	 * of the analysis is updated to show the description applicable to the object
	 * selected in the JComboBox.
	 * 
	 * If "start" - the controller's chosen analysis is stored in the FemModel and
	 * the methods openInputFileDialog() and performAnalysis() are called.
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == analysisTypes) {
			JComboBox<String> cb = (JComboBox) e.getSource();
			String selected = (String) cb.getSelectedItem();
			for (int i = 0; i < types.length; i++) {
				if (selected == types[i]) {
					analysisDescription.setText(descriptions[i]);
				}
			}
		} else if ("start".equals(e.getActionCommand())) {
			String s = (String) analysisTypes.getSelectedItem();
			fem.setChosenAnalysis(s);
			openInputFileDialog();
			if (inputFile != null) {
				try {
					p.fromStream(fem, new FileInputStream(inputFile));

				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				performAnalysis();
				startPanel.removeAll();
				JLabel finished = new JLabel("Analysis performed on: " + inputFile.getName());
				finished.setForeground(Color.WHITE);
				startPanel.add(finished);
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				dim.width *= .3;
				dim.height *= .1;
				this.setSize(dim.width, dim.height);
				int xOffset = 20, yOffset = 20;
				this.setLocation(xOffset, yOffset);
				this.repaint();
				this.getParent().setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	/**
	 * Method performs a FirstOrderLinearAnalysis on the FemModel and File stored
	 * within this class.
	 */
	private void performAnalysis() {
		
		FirstOrderLinearAnalysis fola = new FirstOrderLinearAnalysis(System.out, true);
		fola.perform(fem, inputFile);
		analysisCompleted = true;

	}

	/**
	 * Method opens a JFileChooser where the controller must select the input file
	 * on which the analysis must be performed. The JFileChooser is provided
	 * extension filters within this method which determines which file types my be
	 * accepted in order to perform the analysis. Extension not prescribed within
	 * this method will not be accessible by the JFileChooser. JFileChooser is
	 * modified to show an instruction "Select a file to analyze:" to help the
	 * controller make a decision. If the if the JFileChooser approves the chosen
	 * file, the file is successfully stored in this class. Warnings are fired if
	 * the file does not exist or if a file has not be selected by the controller.
	 */
	public void openInputFileDialog() {
		Set<String> extensions = new HashSet<String>();

		if (fem.getChosenAnalysis() == Analysis.HEAT_TRANSFER) {
			extensions.add("shf");
		} else if (fem.getChosenAnalysis() == Analysis.ELASTICITY) {
			extensions.add("elas");
		} else if (fem.getChosenAnalysis() == Analysis.SEEPAGE) {
			extensions.add("seep");
		} else if (fem.getChosenAnalysis() == Analysis.POTENTIAL_FLOW) {
			extensions.add("ptflw");
		} else if (fem.getChosenAnalysis() == Analysis.ELECTROSTATIC) {
			extensions.add("elc");
		}

		FileFilter filter = new ExtensionFilter(extensions);
		JFileChooser fc = new JFileChooser(".");
		fc.setForeground(Color.WHITE);
		fc.setBackground(Color.BLACK);
		fc.setFileFilter(filter);
		JPanel panel1 = new JPanel();
		JLabel label = new JLabel("Select a file to perform analysis:");
		label.setPreferredSize(new Dimension(480, 20));
		panel1.add(label);
		JPanel panel2 = (JPanel) fc.getComponent(2);
		Component[] components = new Component[panel2.getComponentCount()];
		for (int i = 0; i < panel2.getComponentCount(); i++) {
			components[i] = panel2.getComponent(i);
		}
		panel2.removeAll();
		BoxLayout layout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
		panel2.setLayout(layout);
		panel2.add(panel1);
		for (int i = 0; i < components.length; i++) {
			panel2.add(components[i]);
		}
		int returnVal = fc.showOpenDialog(null);
		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "File does not exist", "Warning!", JOptionPane.ERROR_MESSAGE);
			} else {
				this.inputFile = file;
			}
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			JOptionPane.showMessageDialog(null, "File not Selected!", "Warning!", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Method returns the FemModel for the open analysis
	 * 
	 * @return fem
	 */
	public FemModel getFemModel() {
		return fem;
	}

	/**
	 * Method returns the the completion status of the analysis, returns true if
	 * analysis is complete and false if the analysis has not yet been performed.
	 * 
	 * @return analysisCompleted
	 */
	public boolean getStatus() {
		return analysisCompleted;
	}

	/**
	 * Method returns the File on which the controller would like to perform the
	 * analysis
	 * 
	 * @return inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * Class used to determine the extension filter for getInputFile method.
	 *
	 */
	private static class ExtensionFilter extends FileFilter {
		Collection<String> extensions;

		public ExtensionFilter(Collection<String> extensions) {
			super();
			this.extensions = extensions;
		}

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

}
