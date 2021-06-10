package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;
import de.thkoeln.syp.mtc.gui.control.Logger;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.resources.TextLineNumber;

public class ParseErrorView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1453797887570422728L;
	private JTextPane textPane;
	private JScrollPane scroll;
	private TextLineNumber tln;

	private Management management;
	private Logger logger;

	public ParseErrorView(File file, IParseError error) {
		management = Management.getInstance();
		logger = management.getLogger();
		textPane = new JTextPane();
		scroll = new JScrollPane(textPane);
		tln = new TextLineNumber(textPane);
		tln.setBackground(new Color(20,20,20));
		tln.setForeground(Color.white);
		tln.setUpdateFont(true);
		tln.setDigitAlignment(RIGHT_ALIGNMENT);
		scroll.setRowHeaderView(tln);
		dispose();

		try {
			List<String> fileLines = new ArrayList<String>();
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String currentLine = "";
			while ((currentLine = input.readLine()) != null) {
				fileLines.add(currentLine + "\n");
			}
			
			if(error.isXMLErrorList()){
				writeXMLError(fileLines, error);
			}
			else if(error.isJSONErrorList()){
				writeJSONError(fileLines, error);
			}

			textPane.setCaretPosition(0);
			textPane.setBackground(new Color(20, 20, 20));
			textPane.setForeground(Color.WHITE);
			textPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
			
			input.close();
			add(scroll);
			this.setSize(1280, 720);
			setMinimumSize(new Dimension(500, 600));
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			try {
				this.setIconImage(ImageIO.read(new File("res/icon.png")));
			} catch (IOException e) {
				logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
			}
			setTitle(file.getName());
			setVisible(true);
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
	}
	
	// Schreibt eine Zeile in die TextPane
		private void appendToPane(JTextPane textPane, String msg, Color c) {
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
					StyleConstants.Foreground, c);

			aset = sc.addAttribute(aset, StyleConstants.FontFamily,
					"Tahoma");
			aset = sc.addAttribute(aset, StyleConstants.Alignment,
					StyleConstants.ALIGN_JUSTIFIED);
			aset = sc.addAttribute(aset, StyleConstants.FontSize, 14);

			int len = textPane.getDocument().getLength();
			textPane.setCaretPosition(len);
			textPane.setCharacterAttributes(aset, false);
			textPane.replaceSelection(msg);
		}
		
		private void writeXMLError(List<String> fileLines, IParseError error){
			int errorRow = error.getLine() - 1;
			textPane.setText(null);
			String errorMessage = error.getMessage().split(".xml:")[1];
			
			appendToPane(textPane, errorMessage + "\n", Color.RED);
			for(int i = 0; i < fileLines.size(); i++){
				if(i == errorRow){
					appendToPane(textPane, fileLines.get(i), Color.RED);
					continue;
				}
				appendToPane(textPane, fileLines.get(i), Color.WHITE);
			}
		}
		
		private void writeJSONError(List<String> fileLines, IParseError error){
			int errorRow = error.getLine() - 1;
			textPane.setText(null);
			String errorMessage = error.getMessage().split(" at ")[0] + " at line: " + (error.getLine() + 1) + ", column: " + error.getCol();
			
			appendToPane(textPane, errorMessage + "\n", Color.RED);
			for(int i = 0; i < fileLines.size(); i++){
				if(i == errorRow){
					appendToPane(textPane, fileLines.get(i), Color.RED);
					continue;
				}
				appendToPane(textPane, fileLines.get(i), Color.WHITE);
			}
		}
}
