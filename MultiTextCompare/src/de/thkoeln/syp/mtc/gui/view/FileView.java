package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class FileView {
	private JFrame frame;
	private JTextArea textArea;
	private JScrollPane scroll;
	
	public FileView(){
		frame = new JFrame();
		textArea = new JTextArea();
		scroll = new JScrollPane(textArea);
		frame.dispose();
		
		textArea.setBorder(new JTextField().getBorder());
		textArea.setSelectionStart(0);
		textArea.setSelectionEnd(0);
		textArea.setEditable(false);
		textArea.setBackground(new Color(20,20,20));
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.getViewport().setViewPosition(new Point(0,0));
		
		
		frame.add(scroll);
	    frame.setSize(1280, 720);
	    
	    frame.setLocationRelativeTo(null);
	    
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public JScrollPane getScroll() {
		return scroll;
	}

	public void setScroll(JScrollPane scroll) {
		this.scroll = scroll;
	}
}
