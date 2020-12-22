package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;

public class MatrixView extends JFrame {
	private JPanel panel;
	private JTable table;
	private JButton testButton;
	private static DecimalFormat df = new DecimalFormat("0.000");
	private int index;
	private TableColumn tColumn;

	public MatrixView(IMatrix matrix, int anzahlDateien, String[] nameDateien) {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		index = 0;
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

 		table = new JTable(data, nameDateien){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				Object value = getModel().getValueAt(row, col);
				double wert = Double.valueOf(value.toString());
				int r = (int) ((255 * (100 - (wert * 100))) / 100);
				int g = (int) ((255 * (wert * 100) / 100));
				Color wertFarbe = new Color(r, g, 0);

				comp.setBackground(wertFarbe);
				return comp;
			}
		};
		JScrollPane scrollPane = new JScrollPane(table);
		RowNumberTable rowTable = new RowNumberTable(table);
		rowTable.setFilenames(nameDateien);
		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panel.add(scrollPane);

		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Matrix");
		this.setSize(400, 400);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.pack();
		//
		// int r = (int) ((255* (100 - (wert*100))) / 100);
		// int g = (int) ((255*(wert*100) / 100));
		// Color wertFarbe = new Color(r, g, 0);
		//
	}

	public MatrixView(IMatrix matrix, int anzahlDateien) {
		String[] nameDateien = new String[anzahlDateien];
		for (int i = 0; i < anzahlDateien; i++) {
			nameDateien[i] = Character.toString((char) (('A' + i)));
		}
		new MatrixView(matrix, anzahlDateien, nameDateien);
	}

}