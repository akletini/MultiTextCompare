package de.thkoeln.syp.mtc.gui.resources;

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

import de.thkoeln.syp.mtc.gui.control.Logger;
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
	private Logger logger;

	public MouseAdapterMatrix() {
		management = Management.getInstance();
		logger = management.getLogger();
		tempFiles = management.getFileImporter().getTempFilesMap().entrySet();
		selectedFiles = new ArrayList<File>();
		fileIndices = new ArrayList<Integer>();
		cachedFiles = new ArrayList<File>();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		kreuzKlick = false;
		int rowIndex = 0, columnIndex = 0;
		// Abbruch falls die FileSelection geaendert wurde
		if (management.getFileSelectionController().getNewSelection())
			logger.setMessage("It is not possible to show side-by-side comparisons after altering the file selection. Please generate a new matrix.", logger.LEVEL_WARNING);
		else {

			// Klick in der Matrix (Kreuzung)
			if (management.getMainView().getTableMatrix().equals(e.getSource())) {
				JTable table = (JTable) e.getSource();
				rowIndex = table.rowAtPoint(e.getPoint());
				columnIndex = table.columnAtPoint(e.getPoint());
				System.out.println("row " + rowIndex + " col " + columnIndex);
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
					logger.setMessage(management.getCurrentFileSelection()
							.get(rowIndex)
							.split("\\|")[0].trim()
							+ " has been selected. Total: "
							+ (selectedFiles.size()), logger.LEVEL_INFO);

					// Sonstige Logausgabe
				} else {
					logger.setMessage(management.getCurrentFileSelection()
									.get(rowIndex)
									.split("\\|")[0].trim()
									+ " & "
									+ management.getCurrentFileSelection()
											.get(columnIndex).split("\\|")[0]
									+ " have been selected. Total: "
									+ (selectedFiles.size()), logger.LEVEL_INFO);
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
						logger.setMessage(management.getCurrentFileSelection()
								.get(columnIndex)
								.split("\\|")[0].trim()
								+ " has been selected. Total: "
								+ (selectedFiles.size()), logger.LEVEL_INFO);
					}
				}
			}

			// Falls 4 Dateien ausgewaehlt wurden
			if (selectedFiles.size() > 3) {
				logger.setMessage("Error! Only 2 or 3 files can be compared at once.", logger.LEVEL_WARNING);
				selectedFiles.clear();
				fileIndices.clear();
				kreuzKlick = false;

				// ComparisonView wird geoeffnet
			} else if ((selectedFiles.size() == 2 && kreuzKlick == true)
					|| selectedFiles.size() == 3) {
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
				logger.setMessage("Comparison is now visible", logger.LEVEL_INFO);
				selectedFiles.clear();
				fileIndices.clear();
				kreuzKlick = false;
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
			matrix.repaint(); //war ausgegraut
		}
	}

}