package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;
import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.resources.NoWrapJTextPane;
import de.thkoeln.syp.mtc.gui.resources.ScrollBarSynchronizer;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IMatchHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;
import de.thkoeln.syp.mtc.steuerung.services.IMatchHelper;

public class ComparisonView extends JFrame {
	private Management management;
	private File[] selectedTempFiles, matchedDiffFiles;
	private String fileName1, fileName2, fileName3;
	private JTextPane tPaneLeft, tPaneMid, tPaneRight;
	private JScrollPane scrollPaneLeft, scrollPaneMid, scrollPaneRight,
			scrollPaneMain;
	private JPanel panel;
	private IDiffHelper diffHelper;
	private IMatchHelper matchHelper;
	private List<File> selection;
	private List<File> temp;

	public ComparisonView(List<File> selectedList, List<Integer> fileIndices) {
		// Management
		management = Management.getInstance();
		if (management.getComparisonView() != null)
			management.getComparisonView().dispose();

		// Variablen fuer den Vergleich
		diffHelper = new IDiffHelperImpl();
		matchHelper = new IMatchHelperImpl();
		selection = new ArrayList<File>();
		temp = new ArrayList<File>();

		// Dateinamen werden ermittelt fuer die Anzeige im Frame Titel
		fileName1 = management.getFileSelectionView().getModel()
				.get(fileIndices.get(0)).split("\\|")[0];
		fileName2 = management.getFileSelectionView().getModel()
				.get(fileIndices.get(1)).split("\\|")[0];
		fileName3 = "";

		// -- Alle Container --

		tPaneLeft = new NoWrapJTextPane();
		tPaneMid = new NoWrapJTextPane();
		tPaneRight = new NoWrapJTextPane();
		scrollPaneLeft = new JScrollPane(tPaneLeft);
		scrollPaneMid = new JScrollPane(tPaneMid);
		scrollPaneRight = new JScrollPane(tPaneRight);
		panel = new JPanel();
		scrollPaneMain = new JScrollPane(panel);

		// Textpane Parameter
		setupTextPane(tPaneLeft);
		setupTextPane(tPaneMid);
		setupTextPane(tPaneRight);

		// Nur horizontale Scollbars + vertikal rechts
		scrollPaneLeft
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPaneLeft
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPaneMid
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPaneMid
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPaneRight
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneMain
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Synced das vertikale Scrollen
		new ScrollBarSynchronizer(scrollPaneLeft.getVerticalScrollBar(),
				scrollPaneMid.getVerticalScrollBar(),
				scrollPaneRight.getVerticalScrollBar());

		// Panel Parameter
		panel.setPreferredSize(new Dimension(200, 200));

		// Anzeige wird vorbereitet
		for (File f : selectedList) {
			for (Entry<File, File> entry : management.getFileImporter()
					.getTempFilesMap().entrySet()) {
				if (entry.getValue().equals(f))
					selection.add(entry.getKey());
			}
		}

		for (File f : selection) {
			temp.add(management.getFileImporter().getDiffTempFilesMap().get(f));
		}

		selection.clear();
		selection.addAll(temp);

		if (selection.size() == 1)
			selection.add(selection.get(0));

		try {
			selectedTempFiles = selection.toArray(new File[selection.size()]);
			matchedDiffFiles = matchHelper.createMatchFiles(selectedTempFiles);
			
			// Falls Line Match aktiviert
			if (management.getFileImporter().getConfig().getLineMatch()) {
				if (selection.size() == 2) {
					matchHelper.matchEqualLines(matchedDiffFiles[0],
							matchedDiffFiles[1]);
				} else if (selection.size() == 3) {
					matchUntilFilesUnchanged(matchHelper, matchedDiffFiles);
				}
			}

			diffHelper.computeDisplayDiff(matchedDiffFiles);

			// -- Fuer 2 Dateien --
			if (selection.size() == 2) {
				panel.setLayout(new GridLayout(0, 2));
				panel.add(scrollPaneLeft);
				panel.add(scrollPaneRight);

				diffWriter(diffHelper.getLeftLines(), tPaneLeft);
				diffWriter(diffHelper.getRightLines(), tPaneRight);

				tPaneLeft.setCaretPosition(0);
				tPaneRight.setCaretPosition(0);
			}

			// -- Fuer 3 Dateien --
			if (selection.size() == 3) {
				fileName3 = " <-> "
						+ management.getFileSelectionView().getModel()
								.get(fileIndices.get(2)).split("\\|")[0];

				panel.setLayout(new GridLayout(0, 3));
				panel.add(scrollPaneLeft);
				panel.add(scrollPaneMid);
				panel.add(scrollPaneRight);

				diffWriter(diffHelper.getLeftLines(), tPaneLeft);
				diffWriter(diffHelper.getMiddleLines(), tPaneMid);
				diffWriter(diffHelper.getRightLines(), tPaneRight);

				tPaneLeft.setCaretPosition(0);
				tPaneMid.setCaretPosition(0);
				tPaneRight.setCaretPosition(0);
			}

			// Frame
			this.add(scrollPaneMain);
			this.setTitle("Selected:     " + fileName1 + " <-> " + fileName2
					+ fileName3);
			this.setSize(1000, 500);
			this.setVisible(true);
			this.setLocationRelativeTo(null);
			try {
				this.setIconImage(ImageIO.read(new File("res/icon.png")));
			} catch (IOException e) {
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Setup der TextPanes
	private void setupTextPane(JTextPane textPane) {
		textPane.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		textPane.setMargin(new Insets(5, 5, 5, 5));
		textPane.setBackground(new Color(20, 20, 20));
		textPane.setPreferredSize((new Dimension(500, 200)));
	}

	// Schreibt eine Zeile in die TextPane
	private void appendToPane(JTextPane textPane, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily,
				"Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment,
				StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, 12);

		int len = textPane.getDocument().getLength();
		textPane.setCaretPosition(len);
		textPane.setCharacterAttributes(aset, false);
		textPane.replaceSelection(msg);
	}

	// Befuellt eine TextPane
	private void diffWriter(List<IDiffLine> lineList, JTextPane textPane) {
		for (IDiffLine diffLine : lineList) {
			for (IDiffChar diffChar : diffLine.getDiffedLine()) {
				appendToPane(textPane, diffChar.getCurrentChar().toString(),
						stringToColor(diffChar.getCharColor()));
			}
		}
	}

	// Wandelt String mit Farbbezeichnung in Color um
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

	// Fuer das Matchen der Lines
	private void matchUntilFilesUnchanged(IMatchHelper match, File[] files)
			throws IOException {
		match.matchEqualLines(files[0], files[1]);
		match.matchEqualLines(files[0], files[2]);
		match.matchEqualLines(files[1], files[2]);

	}
}
