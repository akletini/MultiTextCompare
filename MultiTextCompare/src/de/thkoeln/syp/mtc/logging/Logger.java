package de.thkoeln.syp.mtc.logging;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class Logger {
	private Management management;
	public static final int LEVEL_INFO = 0;
	public static final int LEVEL_WARNING = 1;
	public static final int LEVEL_ERROR = 2;

	public Logger() {
		management = Management.getInstance();
		createLogFile();
	}

	public void setMessage(String message, int level) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat timestamp = new SimpleDateFormat("HH:mm:ss");
		String fullEntry = timestamp.format(cal.getTime()) + " | " + message
				+ "\n";
		appendToLog(fullEntry, level);
		writeToLogFile(fullEntry, false);
	}

	private void createLogFile() {
		try {
			File logDir = new File(System.getProperty("user.dir")
					+ File.separator + "logs");
			logDir.mkdir();
			File logFile = new File(System.getProperty("user.dir")
					+ File.separator + "logs" + File.separator + "log " + getCurrentDate() + ".txt");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
		} catch (IOException e) {
			setMessage(e.toString(), LEVEL_ERROR);
		}

	}

	private String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		return formatter.format(date);
	}

	public void writeToLogFile(String message, boolean addDate) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(getCurrentLogFile(), true));
			if (addDate) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat timestamp = new SimpleDateFormat("HH:mm:ss");
				message = timestamp.format(cal.getTime()) + " | " + message
						+ "\n";
			}
			writer.append(message);
			writer.close();
		} catch (IOException e) {
			setMessage(e.toString(), LEVEL_ERROR);
		}
	}


	// Schreibt eine Zeile in den Log
	public void appendToLog(String s, int level) {
		MainView mainView = management.getMainView();
		switch (level) {
		case 0:
			if (mainView.getInfo().isSelected()) {
				appendToPane(mainView.getTextArea(), s, Color.black,
						Color.white);
			}
			break;
		case 1:
			if (mainView.getWarning().isSelected()) {
				appendToPane(mainView.getTextArea(), s, Color.red, Color.yellow);
			}
			break;
		case 2:
			if (mainView.getError().isSelected()) {
				appendToPane(mainView.getTextArea(), s, Color.white, Color.red);
			}
			break;
		default:
			appendToPane(mainView.getTextArea(), s + " | internal error",
					Color.white, Color.red);
			break;
		}
	}
	
	public File getCurrentLogFile(){
		String date = getCurrentDate();
		File currentFile = new File(System.getProperty("user.dir")
				+ File.separator + "logs" + File.separator + "log " + date + ".txt");
		if(!currentFile.exists()){
			createLogFile();
		}
		return currentFile;
	}

	private void appendToPane(JTextPane textPane, String msg, Color foreground,
			Color background) {
		textPane.setEditable(true);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, foreground);

		aset = sc.addAttribute(aset, StyleConstants.Background, background);

		int len = textPane.getDocument().getLength();
		textPane.setCaretPosition(len);
		textPane.setCharacterAttributes(aset, false);
		textPane.replaceSelection(msg);
		textPane.setEditable(false);
	}
}
