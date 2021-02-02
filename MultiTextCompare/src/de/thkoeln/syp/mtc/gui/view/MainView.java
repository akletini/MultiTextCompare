package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.gui.RowNumberTable;
import de.thkoeln.syp.mtc.gui.control.MainController;
import de.thkoeln.syp.mtc.gui.control.Management;

import java.awt.Font;

public class MainView extends JFrame {
	private Management management;
	private JPanel panel;
	private JTable tableMatrix;
	private JToolBar toolBar;
	private JButton btnDateiauswahl, btnKonfig, btnHilfe, btnAbout;
	private JScrollPane scrollPaneMatrix, scrollPaneFiles;
	private RowNumberTable rowTable;
	private JTextArea textArea;
	private Map<Double, IAehnlichkeitImpl> tempFiles;
	private MainController mainController;

	public MainView() {
		management = Management.getInstance();

		// Panel
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		panel.setLayout(new MigLayout("", "[grow]",
				"[30px:n:100px,top][grow,center][80px:n:160px,grow,bottom]"));

		// Toolbar inkl. Buttons
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		panel.add(toolBar, "flowx,cell 0 0,alignx center");
		btnDateiauswahl = new JButton("File Selection");
		toolBar.add(btnDateiauswahl);
		btnKonfig = new JButton("Configuration");
		toolBar.add(btnKonfig);
		btnHilfe = new JButton("Help");
		toolBar.add(btnHilfe);
		btnAbout = new JButton("About");
		toolBar.add(btnAbout);

		// TextArea (Ausgabe)
		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
		textArea.setEditable(false);
		scrollPaneFiles = new JScrollPane(textArea);
		panel.add(scrollPaneFiles, "flowx,cell 0 2,grow");

		// Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 960, 540);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("MultiTextCompare");
		this.setContentPane(panel);

		Management.getInstance().setMainController(new MainController(this));
	}

	// Erstellen der Matrix
	public void updateMatrix(IMatrix matrix, int anzahlDateien,
			String[] nameDateien) {

		DecimalFormat df = new DecimalFormat("0.000");
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		int index = 0;
		List<IAehnlichkeitImpl> listMatrix = management.getTextVergleicher()
				.getMatrix().getInhalt();
		String[][] data = new String[anzahlDateien][anzahlDateien];

		for (int i = 0; i < anzahlDateien; i++) {
			data[i][i] = "1.000";
			for (int j = i + 1; j < anzahlDateien; j++) {
				double wert = listMatrix.get(index).getWert();
				String wertString = df.format(wert);
				data[i][j] = wertString;
				data[j][i] = wertString;
				index++;
			}
			// tempFiles.put(cantorPairing(i, i), listMatrix.get(index));
		}

		tableMatrix = new JTable(data, nameDateien) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				Object value = getModel().getValueAt(row, col);
				double wert = Double.valueOf(value.toString());
				Color wertFarbe = getColor(wert);
				comp.setBackground(wertFarbe);
				return comp;
			}

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					public String getToolTipText(MouseEvent e) {
						String tip = null;
						int index = columnModel
								.getColumnIndexAtX(e.getPoint().x);
						int realIndex = columnModel.getColumn(index)
								.getModelIndex();

						return getPaths()[realIndex];
					}
				};
			}
		};
		tableMatrix.getTableHeader().setReorderingAllowed(false);
		tableMatrix.setRowHeight(60);
		tableMatrix.setDefaultEditor(Object.class, null);
		tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (scrollPaneMatrix != null)
			panel.remove(scrollPaneMatrix);
		scrollPaneMatrix = new JScrollPane(tableMatrix);
		panel.add(scrollPaneMatrix, "cell 0 1,grow");

		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tableMatrix
				.getDefaultRenderer(Object.class);
		renderer.setHorizontalAlignment(SwingConstants.CENTER);

		rowTable = new RowNumberTable(tableMatrix);
		rowTable.setFilenames(nameDateien);
		scrollPaneMatrix.setRowHeaderView(rowTable);
		scrollPaneMatrix.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		SwingUtilities.updateComponentTreeUI(this);
		MouseAdapterMatrix mam = new MouseAdapterMatrix();
		tableMatrix.addMouseListener(mam);
		tableMatrix.getTableHeader().addMouseListener(mam);
	}

	private class MouseAdapterMatrix extends MouseAdapter {
		private List<File> selected;

		public MouseAdapterMatrix() {
			selected = new ArrayList<File>();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.println("blibla");
			Set<Entry<File, File>> tempFiles;
			
//			if (management.getFileSelectionController().getMode() == 1) {
//				tempFiles = management.getFileImporter().getXmlTempFilesMap().entrySet();
//			} else
				tempFiles = management.getFileImporter().getTempFilesMap()
						.entrySet();

			if (tableMatrix.equals(e.getSource()) && selected.size() < 2) {
				JTable table = (JTable) e.getSource();
				int row = table.rowAtPoint(e.getPoint());
				int column = table.columnAtPoint(e.getPoint());

				for (Map.Entry<File, File> entry : tempFiles) {
					if (entry.getValue().getName().equals("temp_" + (row + 1))
							|| entry.getValue().getName()
									.equals("temp_" + (column + 1))) {
						selected.add(entry.getValue());
						if (row == column)
							selected.add(entry.getValue());
					}
				}
			} else if (tableMatrix.getTableHeader().equals(e.getSource())) {
				int columnIndex = tableMatrix.columnAtPoint(e.getPoint());
				for (Map.Entry<File, File> entry : tempFiles) {
					if (entry.getValue().getName()
							.equals("temp_" + (columnIndex + 1)))
						selected.add(entry.getValue());
				}
			}

			if (selected.size() == 3) {
				management.setComparisonView(new ComparisonView(selected, management.getFileSelectionController().getMode()));
				selected.clear();
			}
		}

	}

	// Generierung der Farbe passend zum �hnlichkeitswert
	private Color getColor(double value) {
		double h = value * 0.3; // Hue
		double s = 0.9; // Saturation
		double b = 0.9; // Brightness

		return Color.getHSBColor((float) h, (float) s, (float) b);
	}

	private String[] getPaths() {
		String[] pathArray = new String[management.getFileImporter()
				.getTextdateien().size()];
		for (int i = 0; i < management.getFileImporter().getTextdateien()
				.size(); i++) {
			pathArray[i] = management.getFileImporter().getTextdateien()
					.get(i).getAbsolutePath();
		}
		return pathArray;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void addDateiauswahlListener(ActionListener e) {
		btnDateiauswahl.addActionListener(e);
	}

	public void addKonfigurationListener(ActionListener e) {
		btnKonfig.addActionListener(e);
	}

	public void addHilfeListener(ActionListener e) {
		btnHilfe.addActionListener(e);
	}
}
