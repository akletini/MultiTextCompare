package de.thkoeln.syp.mtc.gui.resources;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import de.thkoeln.syp.mtc.gui.view.FileView;

// Extra Klasse fuer die Klickbarkeit der Matrix
public class MouseAdapterMatrix extends MouseAdapter {
	private Management management;
	private List<File> selectedFiles, referenceFiles;
	private List<Integer> fileIndices;
	private Set<Entry<File, File>> tempFiles;
	boolean kreuzKlick;
	private boolean controlMode;
	private int selectedRow = -1, selectedColumn = -1;
	private Logger logger;
	private ReferenceCell referenceCell;

	public MouseAdapterMatrix() {
		management = Management.getInstance();
		logger = management.getLogger();
		tempFiles = management.getFileImporter().getTempFilesMap().entrySet();
		selectedFiles = new ArrayList<File>();
		fileIndices = new ArrayList<Integer>();
		referenceFiles = new ArrayList<File>();
		controlMode = false;
		referenceCell = new ReferenceCell(-1, -1);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		kreuzKlick = false;
		int rowIndex = 0, columnIndex = 0;
		// Abbruch falls die FileSelection geaendert wurde
		if (management.isNewSelection())
			logger.setMessage(
					"It is not possible to show side-by-side comparisons after altering the file selection. Please generate a new matrix.",
					logger.LEVEL_WARNING);
		else {

			// Klick in der Matrix (Kreuzung)
			if (management.getMainView().getTableMatrix().equals(e.getSource())) {
				JTable table = (JTable) e.getSource();
				rowIndex = table.rowAtPoint(e.getPoint());
				columnIndex = table.columnAtPoint(e.getPoint());
				kreuzKlick = true;

				// wieder farbig machen
				if (controlMode && rowIndex == referenceCell.row
						&& columnIndex == referenceCell.col
						&& e.isControlDown()) {
					controlMode = false;
					greyOutMatrix(false);
					selectedFiles.clear();
					fileIndices.clear();
					referenceCell.row = -1;
					referenceCell.col = -1;
					return;
				}

				// Einzelner Klick ohne STRG => mache gar nichts
				if (isSingleClick(e) && !e.isControlDown() && !controlMode) {
					// Tu nichts
					e.consume();
				}
				// Doppelklick ohne STRG => öffnet 2er Diff
				else if (isDoubleClick(e) && !e.isControlDown() && !controlMode) {
					fetchFilesFromCellClick(rowIndex, columnIndex);
					logClickInCell(rowIndex, columnIndex);
					if (selectedFiles.size() == 2) {
						if (rowIndex < columnIndex) {
							management.setComparisonView(new ComparisonView(
									selectedFiles, fileIndices));
						} else {
							Collections.reverse(selectedFiles);
							Collections.reverse(fileIndices);
							management.setComparisonView(new ComparisonView(
									selectedFiles, fileIndices));
						}
						logger.setMessage("Comparison is now visible",
								logger.LEVEL_INFO);
						selectedFiles.clear();
						fileIndices.clear();
						kreuzKlick = false;
					}
				}

				// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// gehe in STRG-Mode
				else if (isSingleClick(e)
						&& e.isControlDown()
						&& !(rowIndex == referenceCell.row && columnIndex == referenceCell.col)) {
					controlMode = true;
					greyOutMatrix(true);
					// Add reference cell to selectedFiles list
					fetchFilesFromCellClick(rowIndex, columnIndex);
					referenceCell = new ReferenceCell(rowIndex, columnIndex);
					management.setReferenceRow(rowIndex);
					management.setReferenceCol(columnIndex);

				}
				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if (controlMode) {
					if (rowIndex == referenceCell.row) {

						// Tempfiles werden durchsucht
						for (Map.Entry<File, File> entry : tempFiles) {
							if (entry.getValue().getName()
									.equals("temp_" + (columnIndex + 1))) {
								if (!fileIndices.contains(columnIndex)) {
									selectedFiles.add(entry.getValue());
									fileIndices.add(columnIndex);
								}
								// Logausgabe
								logger.setMessage(
										management.getCurrentFileSelection()
												.get(rowIndex).split("\\|")[0]
												.trim()
												+ " and "
												+ management
														.getCurrentFileSelection()
														.get(columnIndex)
														.split("\\|")[0].trim()
												+ " have been selected as reference.",
										logger.LEVEL_INFO);
							}
						}

					} else if (columnIndex == referenceCell.col) {

						// Tempfiles werden durchsucht
						for (Map.Entry<File, File> entry : tempFiles) {
							if (entry.getValue().getName()
									.equals("temp_" + (rowIndex + 1))) {
								if (!fileIndices.contains(rowIndex)) {
									selectedFiles.add(entry.getValue());
									fileIndices.add(rowIndex);
								}
								// Logausgabe
//								logger.setMessage(
//										management.getCurrentFileSelection()
//												.get(columnIndex).split("\\|")[0]
//												.trim()
//												+ " has been selected.",
//										logger.LEVEL_INFO);
							}
						}
					}

					if (selectedFiles.size() == 3) {
						if (rowIndex < columnIndex) {
							management.setComparisonView(new ComparisonView(
									selectedFiles, fileIndices));
							selectedFiles.remove(selectedFiles.size() - 1);
							fileIndices.remove(fileIndices.size() - 1);
						} else {
							Collections.reverse(selectedFiles);
							Collections.reverse(fileIndices);
							management.setComparisonView(new ComparisonView(
									selectedFiles, fileIndices));
							selectedFiles.remove(0);
							fileIndices.remove(0);
						}
						logger.setMessage("Comparison is now visible",
								logger.LEVEL_INFO);

						kreuzKlick = false;
					}

				}
				// Control mode end

			}
			// click on table header
			else if (management.getMainView().getTableMatrix().getTableHeader()
					.equals(e.getSource())) {
				columnIndex = management.getMainView().getTableMatrix()
						.columnAtPoint(e.getPoint());

				// Tempfiles werden durchsucht
				for (Map.Entry<File, File> entry : tempFiles) {
					if (entry.getValue().getName()
							.equals("temp_" + (columnIndex + 1))) {
						FileView fileView = new FileView();
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(
									new FileInputStream(entry.getValue()), "UTF-8"));
							fileView.getTextPane().setText(null);
							fileView.getTextPane().read(reader, "Reading file...");
							fileView.getTextPane().setCaretPosition(0);
							fileView.setTitle(entry.getValue().getAbsolutePath());
							reader.close();
							fileView.setVisible(true);

						} catch (IOException ex) {
							logger.setMessage(ex.toString(), logger.LEVEL_ERROR);
						}
					}
				}
			}

		}
	}

	public void greyOutMatrix(boolean doIt) {
		JTable matrix = management.getMainView().getTableMatrix();

		if (doIt) {
			management.setIsMatrixGreyedOut(doIt);
			matrix.repaint();
		} else {
			management.setIsMatrixGreyedOut(doIt);
			matrix.getSelectionModel().clearSelection();
			matrix.repaint();
		}
	}

	public void fetchFilesFromCellClick(int rowIndex, int columnIndex) {
		selectedFiles.clear();
		fileIndices.clear();
		// Tempfiles werden durchsucht
		for (Map.Entry<File, File> entry : tempFiles) {
			// Spalte
			if (entry.getValue().getName().equals("temp_" + (columnIndex + 1))) {
				selectedFiles.add(entry.getValue());
				fileIndices.add(columnIndex);
			}
			// Zeile
			if (entry.getValue().getName().equals("temp_" + (rowIndex + 1))) {
				selectedFiles.add(entry.getValue());
				fileIndices.add(rowIndex);
			}
		}
	}

	public void logClickInCell(int rowIndex, int columnIndex) {
		// Logausgabe bei Klick auf Diagonale
		if (columnIndex == rowIndex) {
			logger.setMessage(management.getCurrentFileSelection()
					.get(rowIndex).split("\\|")[0].trim()
					+ " has been selected.", logger.LEVEL_INFO);
			// Sonstige Logausgabe
		} else {
			logger.setMessage(
					management.getCurrentFileSelection().get(rowIndex)
							.split("\\|")[0].trim()
							+ " and "
							+ management.getCurrentFileSelection()
									.get(columnIndex).split("\\|")[0]
							+ " have been selected.", logger.LEVEL_INFO);
		}
	}

	public boolean isSingleClick(MouseEvent e) {
		return e.getClickCount() == 1;
	}

	public boolean isDoubleClick(MouseEvent e) {
		return e.getClickCount() == 2;
	}

}

class ReferenceCell {
	File fileRow, fileCol;
	int row, col;

	public ReferenceCell(int row, int col) {
		this.row = row;
		this.col = col;
	}

}