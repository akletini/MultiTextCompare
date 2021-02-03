package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;
import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;

public class HelpView extends JFrame {

	private JPanel panel;
	private File[] auswahl;
	private Color backgroundColor;
	private JTextPane tPane1;
	private JTextPane tPane2;
	private JTextPane tPane3;
	
	public HelpView() throws IOException {
		panel = new JPanel();
		backgroundColor = new Color(20,20,20);
		
		File file1 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IDiffHelperTestFiles"
				+ File.separator + "DiffHelperA.txt");
		File file2 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IDiffHelperTestFiles"
				+ File.separator + "DiffHelperB.txt");
		File file3 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IDiffHelperTestFiles"
				+ File.separator + "DiffHelperC.txt");
		
		File file11 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortElements.xml");
		
		File file12 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/XMLTestFiles/ExpectedOutcome/ExpectedOutcomeSortElements.xml");
		auswahl = new File[]{file1, file2, file3};
		

		IDiffHelper diff = new IDiffHelperImpl();
		try {

			diff.computeDisplayDiff(auswahl);

			if (auswahl.length == 2) {
				EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));

				tPane1 = new JTextPane();
				tPane3 = new JTextPane();

				tPane1.setBorder(eb);
				tPane3.setBorder(eb);

				tPane1.setMargin(new Insets(5, 5, 5, 5));
				tPane1.setBackground(backgroundColor);

				tPane3.setMargin(new Insets(5, 5, 5, 5));
				tPane3.setBackground(backgroundColor);

				panel.add(tPane1);
				panel.add(tPane3);

				for (IDiffLine diffLine : diff.getLeftLines()) {
					for (IDiffChar diffChar : diffLine.getDiffedLine()) {
						appendToPane(tPane1, diffChar.getCurrentChar()
								.toString(),
								stringToColor(diffChar.getCharColor()));
					}
				}

				for (IDiffLine diffLine : diff.getRightLines()) {
					for (IDiffChar diffChar : diffLine.getDiffedLine()) {
						appendToPane(tPane3, diffChar.getCurrentChar()
								.toString(),
								stringToColor(diffChar.getCharColor()));
					}
				}
			}
			if (auswahl.length == 3) {
				EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));

				tPane1 = new JTextPane();
				tPane2 = new JTextPane();
				tPane3 = new JTextPane();

				tPane1.setBorder(eb);
				tPane2.setBorder(eb);
				tPane3.setBorder(eb);

				tPane1.setMargin(new Insets(5, 5, 5, 5));
				tPane1.setBackground(backgroundColor);

				tPane2.setMargin(new Insets(5, 5, 5, 5));
				tPane2.setBackground(backgroundColor);

				tPane3.setMargin(new Insets(5, 5, 5, 5));
				tPane3.setBackground(backgroundColor);

				panel.add(tPane1);
				panel.add(tPane2);
				panel.add(tPane3);

				for (IDiffLine diffLine : diff.getLeftLines()) {
					for (IDiffChar diffChar : diffLine.getDiffedLine()) {
						appendToPane(tPane1, diffChar.getCurrentChar()
								.toString(),
								stringToColor(diffChar.getCharColor()));
					}
				}
				for (IDiffLine diffLine : diff.getMiddleLines()) {
					for (IDiffChar diffChar : diffLine.getDiffedLine()) {
						appendToPane(tPane2, diffChar.getCurrentChar()
								.toString(),
								stringToColor(diffChar.getCharColor()));
					}
				}

				for (IDiffLine diffLine : diff.getRightLines()) {
					for (IDiffChar diffChar : diffLine.getDiffedLine()) {
						appendToPane(tPane3, diffChar.getCurrentChar()
								.toString(),
								stringToColor(diffChar.getCharColor()));
					}
				}

			}
			
//			JScrollPane scrollPane = new JScrollPane();
//			panel.add(scrollPane);
			
			getContentPane().add(panel);
			
			

			pack();
			setVisible(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily,
				"Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment,
				StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	private Color stringToColor(String string) {
		if (string.equals("WHITE")) {
			return Color.WHITE;
		} else if (string.equals("RED")) {
			return Color.RED;
		} else if (string.equals("GREEN")) {
			return Color.GREEN;
		} else if (string.equals("PINK")) {
			return Color.MAGENTA;
		} else if (string.equals("ORANGE")) {
			return Color.ORANGE;
		} else if (string.equals("BLUE")) {
			return Color.BLUE;
		} else if (string.equals("CYAN")) {
			return Color.CYAN;
		} else if (string.equals("YELLOW")) {
			return Color.YELLOW;
		}

		return null;
	}

}
