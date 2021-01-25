package de.thkoeln.syp.mtc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import javax.swing.JToolBar;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.HilfeView;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;
import de.thkoeln.syp.mtc.gui.view.RowNumberTable;

public class MainMenu extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JToolBar toolBar;
	private JButton btnDateiauswahl;
	private JButton btnKonfig;
	private JButton btnHilfe;
	private JButton btnAbout;
	private JScrollPane scrollPaneMatrix;
	private RowNumberTable rowTable;
	private JScrollPane scrollPaneLog;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainMenu() {
		final MainMenu mainMenu = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 960, 540);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane
				.setLayout(new MigLayout("", "[grow]",
						"[50px:n:100px,grow,top][grow,center][80px:n:160px,grow,bottom]"));

		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		contentPane.add(toolBar, "flowx,cell 0 0");

		btnDateiauswahl = new JButton("Dateiauswahl");
		btnDateiauswahl.setVerticalAlignment(SwingConstants.TOP);
		btnDateiauswahl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DateiauswahlView(mainMenu);
			}
		});
		toolBar.add(btnDateiauswahl);

		btnKonfig = new JButton("Konfiguration");
		btnKonfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new KonfigurationView();
			}
		});
		toolBar.add(btnKonfig);

		btnHilfe = new JButton("Hilfe");
		btnHilfe.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					new HilfeView();
				} catch (IOException e1) {

					e1.printStackTrace();
				}

			}
		});
		toolBar.add(btnHilfe);

		btnAbout = new JButton("About");
		toolBar.add(btnAbout);

		// scrollPaneMatrix = new JScrollPane();

		scrollPaneLog = new JScrollPane();
		contentPane.add(scrollPaneLog, "cell 0 2,grow");
	}

	public void updateMatrix(IMatrix matrix, int anzahlDateien,
			String[] nameDateien) {
		System.out.println("bljat");
		DecimalFormat df = new DecimalFormat("0.000");
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		int index = 0;
		List<IAehnlichkeitImpl> list = matrix.getInhalt();

		String[][] data = new String[anzahlDateien][anzahlDateien];

		for (int i = 0; i < anzahlDateien; i++) {
			data[i][i] = "1.000";
			for (int j = i + 1; j < anzahlDateien; j++) {
				double wert = list.get(index).getWert();
				String wertString = df.format(wert);
				data[i][j] = wertString;
				data[j][i] = wertString;
				index++;
			}
		}
		System.out.println(data.length);

		table = new JTable(data, nameDateien) {
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
		};

		scrollPaneMatrix = new JScrollPane(table);
		contentPane.add(scrollPaneMatrix, "cell 0 1,grow");
		rowTable = new RowNumberTable(table);
		rowTable.setFilenames(nameDateien);
		scrollPaneMatrix.setRowHeaderView(rowTable);
		scrollPaneMatrix.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());

		SwingUtilities.updateComponentTreeUI(this);
	}

	public Color getColor(double value) {
		double h = value * 0.3; // Hue
		double s = 0.9; // Saturation
		double b = 0.9; // Brightness

		return Color.getHSBColor((float) h, (float) s, (float) b);
	}
}
