package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;
import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;
import de.thkoeln.syp.mtc.gui.control.ComparisonController;
import de.thkoeln.syp.mtc.gui.control.Logger;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.resources.NoWrapJTextPane;
import de.thkoeln.syp.mtc.gui.resources.ScrollBarSynchronizer;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IMatchHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;
import de.thkoeln.syp.mtc.steuerung.services.IMatchHelper;

public class ComparisonView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4623247136191282712L;
	private Management management;
	private File[] selectedTempFiles, matchedDiffFiles;
	private String fileName1, fileName2, fileName3;
	private JTextPane tPaneLeft, tPaneMid, tPaneRight;
	private JSplitPane splitLeft, splitRight;
	private JScrollPane scrollPaneLeft, scrollPaneMid, scrollPaneRight,
			scrollPaneMain;
	private JPanel panel;
	private IDiffHelper diffHelper;
	private IMatchHelper matchHelper;
	private List<File> selection;
	private List<File> temp;
	private Logger logger;

	public ComparisonView(List<File> selectedList, List<Integer> fileIndices) {
		// Management
		management = Management.getInstance();
		logger = management.getLogger();
		if (management.getComparisonView() != null)
			management.getComparisonView().dispose();
		this.setSize(1000, 500);

		// Variablen fuer den Vergleich
		diffHelper = new IDiffHelperImpl();
		matchHelper = new IMatchHelperImpl();
		selection = new ArrayList<File>();
		temp = new ArrayList<File>();
		diffHelper.setFileImporter(management.getFileImporter());

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
		scrollPaneLeft.setWheelScrollingEnabled(false);

		scrollPaneMid
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPaneMid
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneMid.setWheelScrollingEnabled(false);

		scrollPaneRight
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneMain
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneMain
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		// Synced das vertikale Scrollen
		new ScrollBarSynchronizer(scrollPaneLeft.getVerticalScrollBar(),
				scrollPaneMid.getVerticalScrollBar(),
				scrollPaneRight.getVerticalScrollBar());
		new ScrollBarSynchronizer(scrollPaneLeft.getHorizontalScrollBar(),
				scrollPaneMid.getHorizontalScrollBar(),
				scrollPaneRight.getHorizontalScrollBar());

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

		if (selection.size() == 3) {
			fileName3 = " <-> "
					+ management.getCurrentFileSelection()
							.get(fileIndices.get(2)).split("\\|")[0];
		}

		class DiffThread extends SwingWorker<Void, Void> {

			@Override
			protected Void doInBackground() throws Exception {
				try {

					for (File f : selection) {
						temp.add(management.getFileImporter().getTempFilesMap()
								.get(f));
					}

					selection.clear();
					selection.addAll(temp);

					if (selection.size() == 1)
						selection.add(selection.get(0));

					selectedTempFiles = selection.toArray(new File[selection
							.size()]);
					matchHelper.setMATCH_AT(management.getFileImporter()
							.getConfig().getMatchAt());
					matchHelper.setLOOKAHEAD(management.getFileImporter()
							.getConfig().getMatchingLookahead());
					matchHelper.setSearchBestMatch(management.getFileImporter()
							.getConfig().getBestMatch());
					matchedDiffFiles = matchHelper
							.createMatchFiles(selectedTempFiles);

					// Falls Line Match aktiviert
					if (management.getFileImporter().getConfig().getLineMatch()) {
						if (selection.size() == 2) {
							matchHelper.matchLines(matchedDiffFiles[0],
									matchedDiffFiles[1]);
						} else if (selection.size() == 3) {
							matchUntilFilesUnchanged(matchHelper,
									matchedDiffFiles);
						}
					}

					diffHelper.computeDisplayDiff(matchedDiffFiles);
				} catch (IOException e) {
					logger.setMessage(e.toString(), logger.LEVEL_ERROR);
				}
				return null;
			}

			@Override
			public void done() {
				// -- Fuer 2 Dateien --
				if (selection.size() == 2) {
					splitLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
							scrollPaneLeft, scrollPaneRight);
					panel.setLayout(new GridLayout(0, 1));
					panel.add(splitLeft);
					diffWriter(diffHelper.getLeftLines(), tPaneLeft);
					diffWriter(diffHelper.getRightLines(), tPaneRight);

					splitLeft.setDividerLocation(getWidth() / 2);
					splitLeft.setResizeWeight(0.5);

					tPaneLeft.setCaretPosition(0);
					tPaneRight.setCaretPosition(0);

				}

				// -- Fuer 3 Dateien --
				if (selection.size() == 3) {

					splitLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
							scrollPaneLeft, scrollPaneMid);
					splitRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
							splitLeft, scrollPaneRight);

					splitLeft.setDividerLocation(getWidth() / 3);
					splitRight.setDividerLocation((getWidth() / 3) * 2);

					splitLeft.setResizeWeight(0.5);
					splitRight.setResizeWeight(0.66);

					panel.setLayout(new GridLayout(0, 1));
					panel.add(splitRight);

					diffWriter(diffHelper.getLeftLines(), tPaneLeft);
					diffWriter(diffHelper.getMiddleLines(), tPaneMid);
					diffWriter(diffHelper.getRightLines(), tPaneRight);

					tPaneLeft.setCaretPosition(0);
					tPaneMid.setCaretPosition(0);
					tPaneRight.setCaretPosition(0);
					management.getComparisonView().setTitle(
							"Selected:     " + fileName1 + " <-> " + fileName2
									+ fileName3);

				}

				management.getComparisonView().setVisible(true);
			}

		}
		DiffThread diffThread = new DiffThread();
		diffThread.execute();

		// Frame
		this.add(scrollPaneMain);
		this.setTitle("Selected:     " + fileName1 + " <-> " + fileName2
				+ fileName3);

		this.setVisible(true);
		this.setLocationRelativeTo(null);
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			logger.setMessage(e.toString(), logger.LEVEL_ERROR);
		}

		Management.getInstance().setComparisonController(
				new ComparisonController(this));

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
		match.matchLines(files[0], files[1]);
		match.matchLines(files[0], files[2]);
		match.matchLines(files[1], files[2]);
	}

	public void addMouseWheelListenerLeft(MouseWheelListener e) {
		scrollPaneLeft.addMouseWheelListener(e);
	}

	public void addMouseWheelListenerMiddle(MouseWheelListener e) {
		scrollPaneMid.addMouseWheelListener(e);
	}

	public JScrollPane getScrollPaneLeft() {
		return scrollPaneLeft;
	}

	public JScrollPane getScrollPaneMid() {
		return scrollPaneMid;
	}

	public JScrollPane getScrollPaneRight() {
		return scrollPaneRight;
	}

	public JScrollPane getScrollPaneMain() {
		return scrollPaneMain;
	}

}
