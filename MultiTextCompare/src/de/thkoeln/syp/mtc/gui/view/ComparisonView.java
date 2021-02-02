package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

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
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;

public class ComparisonView extends JFrame {
	private Management management;
	private JPanel panel;
	private List<File> selection;
	private List<File> temp;
	private Color backgroundColor;
	private JTextPane tPane1, tPane2, tPane3;

	// mode 0 = .txt, mode 1 = .xml
	public ComparisonView(List<File> selectedList, int mode) {
		management = Management.getInstance();
		selection = new ArrayList<File>();
		temp = new ArrayList<File>();
		if (management.getComparisonView() != null)
			management.getComparisonView().dispose();
		setLocationRelativeTo(management.getMainView().getRootPane());
		panel = new JPanel();
		backgroundColor = new Color(20, 20, 20);

		for (Entry<File, File> entry : management.getFileImporter()
				.getTempFilesMap().entrySet()) {
			if (selectedList.contains(entry.getValue())) {
				selection.add(entry.getKey());
			}
		}

		if (mode == 1) {
			for (File f : selection) {
				temp.add(management.getFileImporter().getXmlTempFilesMap()
						.get(f));
			}
			selection.clear();
			selection.addAll(temp);
			System.out.println("hallo ich bin die else if :-)");
		}

		if (selection.size() == 1)
			selection.add(selection.get(0));
		for (File f : selection) {
			System.out.println(f.getAbsolutePath());
		}
		System.out.println("--------");
		IDiffHelper diff = new IDiffHelperImpl();
		try {

			diff.computeDisplayDiff(selection.toArray(new File[selection.size()]));

			if (selection.size() == 2) {
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
			if (selection.size() == 3) {
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
			// Frame
			this.getContentPane().add(panel);
			this.setTitle("Dateivergleich");
			this.pack();
			this.setVisible(true);
			this.setLocationRelativeTo(null);
			this.setResizable(false);

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
