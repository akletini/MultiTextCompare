package de.thkoeln.syp.mtc.gui.view;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import de.thkoeln.syp.mtc.datenhaltung.api.IJSONParseError;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;
import de.thkoeln.syp.mtc.gui.control.Logger;
import de.thkoeln.syp.mtc.gui.control.Management;

public class ParseErrorView extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1453797887570422728L;
	private JTextPane textPane;
	private JScrollPane scroll;

	private Management management;
	private Logger logger;
	
	public ParseErrorView(File file, IJSONParseError jsonError) {
		setupPanel();
		
	}
	
	public ParseErrorView(File file, IXMLParseError xmlError) {
		setupPanel();
	}
	
	private void setupPanel(){
		management = Management.getInstance();
		logger =  management.getLogger();
		textPane = new JTextPane();
		scroll = new JScrollPane(textPane);
	}
}

