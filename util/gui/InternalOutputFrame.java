package util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.CloseAction;

public class InternalOutputFrame extends JInternalFrame {
	
	private JScrollPane pane;
	private SystemOutStream outputStream;

	public InternalOutputFrame(String title, boolean resizable, boolean closable, boolean maximizable,
			boolean iconifiable, SystemOutStream outputStream) {
		super(title, resizable, closable, maximizable, iconifiable);
		this.outputStream = outputStream;
		initializeOutputFrame();
	}

	
	public void initializeOutputFrame() {
		outputStream.setBackground(Color.BLACK);
		outputStream.setForeground(Color.WHITE);
		pane = new JScrollPane(outputStream);
		this.add(pane);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.width = (int) ((dim.width*.7) - 60);
		dim.height = (int) (dim.height*.9 - 40);
		this.setSize(dim.width, dim.height);
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xOffset = (int) (dim.width*.3) + 40, yOffset = 20;
		this.setLocation(xOffset, yOffset);
		this.setVisible(true);
	}

}
