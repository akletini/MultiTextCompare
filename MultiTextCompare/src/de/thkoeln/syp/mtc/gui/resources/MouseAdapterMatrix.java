package de.thkoeln.syp.mtc.gui.resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.ComparisonView;

// Extra Klasse fuer die Klickbarkeit der Matrix
public class MouseAdapterMatrix extends MouseAdapter {
	private Management management;
	private List<File> selectedFiles, cachedFiles;
	private List<Integer> fileIndices;
	private Set<Entry<File, File>> tempFiles;
	boolean kreuzKlick;
	private boolean greyOut = false;
	private int selectedRow = -1, selectedColumn = -1;

	public MouseAdapterMatrix() {
		management = Management.getInstance();
		tempFiles = management.getFileImporter().getTempFilesMap().entrySet();
		selectedFiles = new ArrayList<File>();
		fileIndices = new ArrayList<Integer>();
		cachedFiles = new ArrayList<File>();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		kreuzKlick = false;
		int rowIndex = 0, columnIndex = 0;
		JTable matrix = management.getMainView().getTableMatrix();

		// Abbruch falls die FileSelection geaendert wurde
		if (management.getFileSelectionController().getNewSelection())
			management
					.appendToLog("It is not possible to show side-by-side comparisons after altering the file selection. Please generate a new matrix.");
		else {

			// Klick in der Matrix (Kreuzung)
			if (management.getMainView().getTableMatrix().equals(e.getSource())) {
				JTable table = (JTable) e.getSource();
				rowIndex = table.rowAtPoint(e.getPoint());
				columnIndex = table.columnAtPoint(e.getPoint());
				kreuzKlick = true;

				

				// Tempfiles werden durchsucht
				for (Map.Entry<File, File> entry : tempFiles) {
					// Spalte
					if (entry.getValue().getName()
							.equals("temp_" + (columnIndex + 1))
							&& !fileIndices.contains(columnIndex)) {
						selectedFiles.add(entry.getValue());
						fileIndices.add(columnIndex);
					}
					// Zeile
					if (entry.getValue().getName()
							.equals("temp_" + (rowIndex + 1))
							&& !fileIndices.contains(rowIndex)) {
						selectedFiles.add(entry.getValue());
						fileIndices.add(rowIndex);
					}
				}
				
				// Logik fürs Ausgrauen
				if (e.getClickCount() == 1) {
					// Zelle zum ersten Mal geklickt
					if (rowIndex != selectedRow
							|| columnIndex != selectedColumn) {
						greyOutMatrix(true);
						selectedRow = rowIndex;
						selectedColumn = columnIndex;
					}
					// beim zweiten Klick wieder alle Farben anzeigen
					else {
						greyOutMatrix(false);
						selectedRow = -1;
						selectedColumn = -1;
						
					}
				}

				// Logausgabe bei Klick auf Diagonale
				if (columnIndex == rowIndex) {
					management.appendToLog(management.getFileSelectionView()
							.getModel()
							.get(fileIndices.get(selectedFiles.size() - 1))
							.split("\\|")[0].trim()
							+ " has been selected. Total: "
							+ (selectedFiles.size()));

					// Sonstige Logausgabe
				} else {
					management
							.appendToLog(management
									.getFileSelectionView()
									.getModel()
									.get(fileIndices.get(selectedFiles.size() - 2))
									.split("\\|")[0].trim()
									+ " & "
									+ management
											.getFileSelectionView()
											.getModel()
											.get(fileIndices.get(selectedFiles
													.size() - 1)).split("\\|")[0]
									+ " have been selected. Total: "
									+ (selectedFiles.size()));
				}

			}

			// Klick auf Spaltenkopf
			else if (management.getMainView().getTableMatrix().getTableHeader()
					.equals(e.getSource())) {
				columnIndex = management.getMainView().getTableMatrix()
						.columnAtPoint(e.getPoint());

				// Tempfiles werden durchsucht
				for (Map.Entry<File, File> entry : tempFiles) {
					if (entry.getValue().getName()
							.equals("temp_" + (columnIndex + 1))) {
						if (!fileIndices.contains(columnIndex)) {
							selectedFiles.add(entry.getValue());
							fileIndices.add(columnIndex);
						}

						// Logausgabe
						management.appendToLog(management
								.getFileSelectionView().getModel()
								.get(fileIndices.get(selectedFiles.size() - 1))
								.split("\\|")[0].trim()
								+ " has been selected. Total: "
								+ (selectedFiles.size()));
					}
				}
			}

			// Falls 4 Dateien ausgewaehlt wurden
			if (selectedFiles.size() > 3) {
				management
						.appendToLog("Error! Only 2 or 3 files can be compared at once.");
				selectedFiles.clear();
				fileIndices.clear();
				kreuzKlick = false;

				// ComparisonView wird geoeffnet
			}
			if (selectedFiles.size() == 2 && kreuzKlick == true
					&& e.getClickCount() == 2) {

				greyOutMatrix(true);
				
				//Klick ueber und unter der Hauptdiagonale spiegeln
				if(rowIndex < columnIndex){
				management.setComparisonView(new ComparisonView(selectedFiles,
						fileIndices));
				}
				else {
					Collections.reverse(selectedFiles);
					Collections.reverse(fileIndices);
					management.setComparisonView(new ComparisonView(selectedFiles,
							fileIndices));
				}
				
				management.appendToLog("Comparison is now visible");
				selectedFiles.clear();
				fileIndices.clear();
				kreuzKlick = false;

			}
			if (selectedFiles.size() == 2 && kreuzKlick == true
					&& e.getClickCount() == 1) {
				cachedFiles.addAll(selectedFiles);

				// Matrix ausgrauen
			}
			if (selectedFiles.size() == 3 && e.isControlDown()) {
				System.out.println("3er Auswahl");
				management.setComparisonView(new ComparisonView(selectedFiles,
						fileIndices));
				
				selectedFiles.clear();
				fileIndices.clear();
				greyOutMatrix(true);
			}

		}
	}

	public void greyOutMatrix(boolean doIt) {
		JTable matrix = management.getMainView().getTableMatrix();
		if (doIt) {
			greyOut = true;
			matrix.repaint();
		} else {
			greyOut = false;
			matrix.getSelectionModel().clearSelection();
//			matrix.repaint();
		}
	}

}