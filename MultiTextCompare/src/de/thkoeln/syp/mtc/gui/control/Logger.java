package de.thkoeln.syp.mtc.gui.control;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.gui.view.MainView;

public class Logger {
	private Management management;
	private int level;
	public final int LEVEL_INFO = 0;
	public final int LEVEL_WARNING = 1;
	public final int LEVEL_ERROR = 2;

	public Logger() {
		management = Management.getInstance();
		createLogFile();
	}

	public void setMessage(String message, int level) {
		this.level = level;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat timestamp = new SimpleDateFormat("HH:mm:ss");
		String fullEntry = timestamp.format(cal.getTime()) + " | " + message
				+ "\n";
		appendToLog(fullEntry, level);
		writeToLogFile(fullEntry, false);
	}

	private void createLogFile() {
		try {
			File logFile = new File("log.txt");
			logFile.createNewFile();
		} catch (IOException e) {
			setMessage(exceptionToString(e), LEVEL_ERROR);
		}

	}
	
	public void writeToLogFile(String message, boolean addDate){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true));
			if(addDate){
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat timestamp = new SimpleDateFormat("HH:mm:ss");
				message = timestamp.format(cal.getTime()) + " | " + message + "\n";
			}
		    writer.append(message);
		    writer.close();
		    } catch (IOException e) {
		    	setMessage(exceptionToString(e), LEVEL_ERROR);
		    }
	}
	
	public String exceptionToString(Throwable e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	// Schreibt eine Zeile in den Log
	public void appendToLog(String s, int level) {
		MainView mainView = management.getMainView();
		String text = s;
		switch (level) {
			case 0:
				if(mainView.getInfo().isSelected()){
					appendToPane(mainView.getTextArea(), s, Color.black, Color.white);
				}
				break;
			case 1:
				if(mainView.getWarning().isSelected()){
					appendToPane(mainView.getTextArea(), s, Color.red, Color.yellow);
				}
				break;
			case 2:
				if(mainView.getError().isSelected()){
					appendToPane(mainView.getTextArea(), s, Color.white, Color.red);
				}
				break;
			default:
				appendToPane(mainView.getTextArea(), s + " | internal error", Color.white, Color.red);
				break;
		}
	}
	
	private void appendToPane(JTextPane textPane, String msg, Color foreground, Color background) {
		textPane.setEditable(true);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, foreground);

		aset = sc.addAttribute(aset, StyleConstants.Background,
				background);


		int len = textPane.getDocument().getLength();
		textPane.setCaretPosition(len);
		textPane.setCharacterAttributes(aset, false);
		textPane.replaceSelection(msg);
		textPane.setEditable(false);
	}
}
