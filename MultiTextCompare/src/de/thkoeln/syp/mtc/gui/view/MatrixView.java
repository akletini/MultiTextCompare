package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;

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
			data[i][i] = "1";
			for (int j = i + 1; j < anzahlDateien; j++) {
				double wert = list.get(index).getWert();
				String wertString = df.format(wert);
				data[i][j] = wertString;
				data[j][i] = wertString;
				index++;
			}
		}

 		table = new JTable(data, nameDateien);
// 		{
//			@Override
//			public Component prepareRenderer(TableCellRenderer renderer,
//					int row, int col) {
//				Component comp = super.prepareRenderer(renderer, row, col);
//				Object value = getModel().getValueAt(row, col);
//				double wert = Double.valueOf(value.toString());
//				int r = (int) ((255 * (100 - (wert * 100))) / 100);
//				int g = (int) ((255 * (wert * 100) / 100));
//				Color wertFarbe = new Color(r, g, 0);
//
//				comp.setBackground(wertFarbe);
//				return comp;
//			}
//		};
//		JTextArea text1 = new JTextArea();
//		JTextArea text2 = new JTextArea();
//		try {
//			File file = matrix.getInhalt().get(0).getVon();
//			BufferedReader in = new BufferedReader(new FileReader(file));
//			String line = in.readLine();
//			while(line != null){
//			  text1.append(line + "\n");
//			  line = in.readLine();
//			}
//			file = matrix.getInhalt().get(0).getZu();
//			in = new BufferedReader(new FileReader(file));
//			line = in.readLine();
//			while(line != null){
//			  text2.append(line + "\n");
//			  line = in.readLine();
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
 		
// 		JEditorPane edit = new JEditorPane();
// 		edit.setEditable(false);
// 		JScrollPane scroll = new JScrollPane(edit);
// 		HTMLEditorKit kit = new HTMLEditorKit();
// 		edit.setEditorKit(kit);
// 		
// 		Document doc = kit.createDefaultDocument();
// 		edit.setDocument(doc);
// 		try {
//			edit.setText(new IDiffHelperImpl().fillDiffList(new File("F:\\FileA.txt"), new File("F:\\FileB.txt")));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		JScrollPane scrollPane = new JScrollPane(table);
		RowNumberTable rowTable = new RowNumberTable(table);
		rowTable.setFilenames(nameDateien);
		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		panel.add(scrollPane);
//		panel.add(scroll);


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