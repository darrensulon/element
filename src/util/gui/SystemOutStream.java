package util.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SystemOutStream extends JTextArea {

	private static final long serialVersionUID = 1L;
	
	private MyOutStream out;
	
	
	
	public SystemOutStream(String title, PrintStream ps){
		this(title,ps,false, false);
	}
	
	public SystemOutStream(String title, PrintStream ps, boolean exit_on_close){
		this(title,ps,exit_on_close,false);
	}
	
	public SystemOutStream(String title, PrintStream ps, boolean exit_on_close, boolean always_on_top){
		out = new MyOutStream(ps);
		setFont(new Font("courier",Font.PLAIN,14));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	public PrintStream getOutStream() {
		return out;
	}	
	
	class MyOutStream extends PrintStream {

		PrintStream m_ps;

		private MyOutStream(PrintStream ps) {
			super(new ByteArrayOutputStream());
			this.m_ps = ps;
		}
		
		public boolean checkError() {
			return false;
		}

		public void close() {
			// Nothing to do
		}

		public void flush() {
			// Nothing to do
		}

		public void print(boolean b) {
			append(b ? "true" : "false", true);
			if(m_ps!=null)
				m_ps.print(b);
		}

		public void print(char c) {
			append(String.valueOf(c), true);
			if(m_ps!=null)
				m_ps.print(c);
		}

		public void print(char[] s) {
			append(String.valueOf(s), true);
			if(m_ps!=null)
				m_ps.print(s);
		}

		public void print(double d) {
			append(String.valueOf(d), true);
			if(m_ps!=null)
				m_ps.print(d);
		}

		public void print(float f) {
			append(String.valueOf(f), true);
			if(m_ps!=null)
				m_ps.print(f);
		}

		public void print(int i) {
			append(String.valueOf(i), true);
			if(m_ps!=null)
				m_ps.print(i);
		}

		public void print(long l) {
			append(String.valueOf(l), true);
			if(m_ps!=null)
				m_ps.print(l);
		}

		public void print(Object obj) {
			append(String.valueOf(obj), true);
			if(m_ps!=null)
				m_ps.print(obj);
		}

		public void print(String s) {
			append(s, true);
			if(m_ps!=null)
				m_ps.print(s);
		}

		public void println() {
			append("\n", true);
			if(m_ps!=null)
				m_ps.print("\n");
		}

		public void println(boolean x) {
			print(x+"\n");
		}

		public void println(char x) {
			print(x+"\n");
		}

		public void println(char[] x) {
			print(x.toString()+"\n");
		}

		public void println(double x) {
			print(x+"\n");
		}

		public void println(float x) {
			print(x+"\n");
		}

		public void println(int x) {
			print(x+"\n");
		}

		public void println(long x) {
			print(x+"\n");
		}

		public void println(Object x) {
			print(x+"\n");
		}

		public void println(String x) {
			print(x+"\n");
		}

		public void write(byte[] buf, int off, int len) {
			print(new String(buf, off, len));
		}

		public void write(int b /* as byte */) {
			print(String.valueOf((char) b));
		}

		public void write(byte[] b) throws IOException {
			print(new String(b));
		}
	
		public void dump(String s){
			append(s, false);
		}
		
		// Helpers
		private void append(String text, boolean show) {
			SystemOutStream.this.append(text);
			if (show)
				showBottom();
		}
		
		private void showBottom() {
			 SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	JViewport vp = (JViewport)SystemOutStream.this.getParent();
//	                	vp.setViewPosition(new Point(0,PopupOutputStream.this.getHeight()));
	                }
			 });
		}

	}

}