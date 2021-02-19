package de.thkoeln.syp.mtc.gui.resources;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JTable;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.ComparisonView;

// Extra Klasse fuer die Klickbarkeit der Matrix
public class MouseAdapterMatrix extends MouseAdapter {
	private Management management;
	private List<File> selectedFiles;
	private List<Integer> fileIndices;
	private Set<Entry<File, File>> tempFiles;
	boolean kreuzKlick;

	public MouseAdapterMatrix() {
		management = Management.getInstance();
		tempFiles = management.getFileImporter().getTempFilesMap().entrySet();
		selectedFiles = new ArrayList<File>();
		fileIndices = new ArrayList<Integer>();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		kreuzKlick = false;

		// Abbruch falls die FileSelection geaendert wurde
		if (management.getFileSelectionController().getNewSelection())
			management
					.appendToLog("It is not possible to show side-by-side comparisons after altering the file selection. Please generate a new matrix.");
		else {

			// Klick in der Matrix (Kreuzung)
			if (management.getMainView().getTableMatrix().equals(e.getSource())) {
				JTable table = (JTable) e.getSource();
				int rowIndex = table.rowAtPoint(e.getPoint());
				int columnIndex = table.columnAtPoint(e.getPoint());
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
				int columnIndex = management.getMainView().getTableMatrix()
						.columnAtPoint(e.getPoint());
				
				// Tempfiles werden durchsucht
				for (Map.Entry<File, File> entry : tempFiles) {
					if (entry.getValue().getName()
							.equals("temp_" + (columnIndex + 1))) {
						selectedFiles.add(entry.getValue());
						fileIndices.add(columnIndex);
						
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
			} else if ((selectedFiles.size() == 2 && kreuzKlick == true)
					|| selectedFiles.size() == 3) {
				management.setComparisonView(new ComparisonView(selectedFiles,
						fileIndices));
				management.appendToLog("Comparison is now visible");
				selectedFiles.clear();
				fileIndices.clear();
				kreuzKlick = false;
			}
		}
	}
}