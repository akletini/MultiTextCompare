package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import de.thkoeln.syp.mtc.gui.control.Logger;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.resources.TextLineNumber;

public class FileView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1950432104929262839L;
	private JTextPane textPane;
	private JScrollPane scroll;
	private TextLineNumber tln;
	private Management management;
	private Logger logger;
	
	public FileView(){
		management = Management.getInstance();
		logger =  management.getLogger();
		textPane = new JTextPane();
		scroll = new JScrollPane(textPane);
		tln = new TextLineNumber(textPane);
		tln.setBackground(new Color(20,20,20));
		tln.setForeground(Color.WHITE);
		tln.setUpdateFont(true);
		tln.setDigitAlignment(RIGHT_ALIGNMENT);
		scroll.setRowHeaderView(tln);
		dispose();
		
		textPane.setBorder(new JTextField().getBorder());
		textPane.setSelectionStart(0);
		textPane.setSelectionEnd(0);
		textPane.setEditable(false);
		textPane.setBackground(new Color(20,20,20));
		textPane.setForeground(Color.WHITE);
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.getViewport().setViewPosition(new Point(0,0));
		
		
		
		this.add(scroll);
		this.setSize(1280, 720);
		setMinimumSize(new Dimension(500, 600));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			logger.setMessage(e.toString(), logger.LEVEL_ERROR);
		}
	}
	
	public JTextPane getTextPane() {
		return textPane;
	}

	public void setTextArea(JTextPane textArea) {
		this.textPane = textArea;
	}

	public JScrollPane getScroll() {
		return scroll;
	}

	public void setScroll(JScrollPane scroll) {
		this.scroll = scroll;
	}
}
