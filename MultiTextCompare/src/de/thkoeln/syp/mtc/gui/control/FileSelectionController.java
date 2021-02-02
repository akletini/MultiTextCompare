package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;

public class FileSelectionController extends JFrame {
	private Management management;
	private JPanel panel;
	private FileDialog fd;
	private JFileChooser fc;
	private File[] selection;
	private List<File> selectionList;
	private IMatrix matrix;
	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;
	private IXMLvergleicher xmlvergleicher;
	private int mode;

	public FileSelectionController(FileSelectionView fileSelectionView) {
		management = Management.getInstance();
		management.setFileSelectionController(this);
		fileImporter = management.getFileImporter();
		textvergleicher = management.getTextVergleicher();
		xmlvergleicher = management.getXmlvergleicher();
		panel = new JPanel();
		matrix = new IMatrixImpl();
		fileSelectionView.getTextFieldFileName().setText(
				fileImporter.getConfig().getDateiname());
		fileSelectionView.addSetRootListener(new SetRootListener());
		fileSelectionView.addSearchListener(new SearchListener());
		fileSelectionView.addAddFilesListener(new AddFilesListener());
		fileSelectionView.addDeleteListener(new DeleteListener());
		fileSelectionView.addResetListener(new ResetListener());
		fileSelectionView.addCompareListener(new CompareListener());

		// String labelRoot = fileImporter.getConfig().getRootDir();
		// int slashIndex = 0;
		// if (labelRoot.length() > 50) {
		// for (int i = labelRoot.length() - 50; i > labelRoot.length(); i--) {
		// if (labelRoot.charAt(i) == '/') {
		// slashIndex = i;
		// break;
		// }
		// }
		// labelRoot = labelRoot.substring(slashIndex, labelRoot.length());
		// labelRoot = "..." + labelRoot;
		// }
		fileSelectionView.getLblRootPath().setText(
				fileImporter.getConfig().getRootDir());
	}

	class SetRootListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			Action details = fc.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			fc.setCurrentDirectory(new File(fileImporter.getConfig()
					.getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(management.getFileSelectionView());
			fileImporter.setRootDir(fc.getSelectedFile());
			updateLblRootPath();
			// management.getDateiauswahlView().pack();
		}
	}

	class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<File> ref = new ArrayList<File>(fileImporter.getTextdateien());
			fileImporter.importTextRoot(management.getFileSelectionView()
					.getTextFieldFileName().getText()
					+ getFileExt());
			fileImporter.getRootImporter().start();
			try {
				fileImporter.getRootImporter().join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (fileImporter.getTextdateien().equals(ref))
				new PopupView("Hinweis",
						"Bei dieser Suche wurden keine weiteren Dateien gefunden");

			setRdbtn(fileImporter.getTextdateien().isEmpty());
			updateListFilePath();
			fileImporter.getConfig().setDateiname(
					management.getFileSelectionView().getTextFieldFileName()
							.getText());
			fileImporter.exportConfigdatei();
			mode = management.getFileSelectionView().getRadioButton();
		}
	}

	class AddFilesListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
			panel.setLayout(new GridLayout(0, 1));
			fd = new FileDialog(management.getFileSelectionView(),
					"Dateiauswahl", FileDialog.LOAD);
			fd.setLocationRelativeTo(null);
			fd.setMultipleMode(true);
			fd.setDirectory(fileImporter.getConfig().getRootDir());
			fd.setFile("*" + getFileExt());
			fd.setVisible(true);
			selection = fd.getFiles();
			selectionList = new ArrayList<File>();
			for (File f : selection) {
				selectionList.add(f);
			}
			fileImporter.importTextdateien(selectionList);
			management.getFileSelectionView().setLocationRelativeTo(null);
			setRdbtn(fileImporter.getTextdateien().isEmpty());
			updateListFilePath();
			mode = management.getFileSelectionView().getRadioButton();
		}
	}

	class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (File f : management.getFileSelectionView().getListSelection()) {
				fileImporter.deleteImport(f);
			}
			updateListFilePath();
		}
	}

	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fileImporter.deleteImports();
			fileImporter.deleteTempFiles();
			management.getFileSelectionView().getModel().clear();
			setRdbtn(true);
		}
	}

	class CompareListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int anzDateien = fileImporter.getTextdateien().size();
			if (anzDateien > 1) {
				fileImporter.deleteTempFiles();
				fileImporter.createTempFiles();
				xmlvergleicher.clearErrorList();

				// XML Vergleich
				if (mode == 1) {
					fileImporter.createXmlTempFiles(xmlvergleicher
							.xmlPrepare(fileImporter.getTempFilesMap()));
					for (IXMLParseError error : xmlvergleicher.getErrorList())
						appendToTextArea(error.getMessage());
				}

				fileImporter.normTempFiles();
				textvergleicher.getTempfilesFromHashMap(management
						.getFileImporter().getTempFilesMap());
				textvergleicher.getVergleiche(textvergleicher.getTempFiles());
				if (fileImporter.getConfig().getLineMatch() == false) {
					textvergleicher.vergleicheUeberGanzesDokument();
				} else {
					textvergleicher.vergleicheZeilenweise();
				}

				management.getMainView().updateMatrix(
						textvergleicher.getMatrix(), anzDateien,
						getFileNames(anzDateien));

				if (!xmlvergleicher.getErrorList().isEmpty()) {
					appendToTextArea("A matrix with "
							+ anzDateien
							+ " files has been created, but the file selection contained. "
							+ xmlvergleicher.getErrorList().size()
							+ " XML errors.");
				}

				else
					appendToTextArea("A matrix with " + anzDateien
							+ " files has been created successfully!");
			} else
				new PopupView("Error",
						"Please select at least two files for comparison");
		}
	}

	public File[] getAuswahl() {
		return selection;
	}

	public IMatrix getMatrix() {
		return matrix;
	}

	public String getFileExt() {
		if (management.getFileSelectionView().getRadioButton() == 0)
			return ".txt";
		if (management.getFileSelectionView().getRadioButton() == 1)
			return ".xml";
		if (management.getFileSelectionView().getRadioButton() == 2)
			return ".json";
		else
			return "";
	}

	public void setRdbtn(boolean b) {
		management.getFileSelectionView().getRdbtnTxt().setEnabled(b);
		management.getFileSelectionView().getRdbtnXml().setEnabled(b);
		management.getFileSelectionView().getRdbtnJson().setEnabled(b);
		management.getFileSelectionView().getRdbtnAll().setEnabled(b);
	}

	public void updateListFilePath() {
		int importSize = fileImporter.getTextdateien().size();
		// String[] nameDateien = getNameDateien(importSize);

		management.getFileSelectionView().getModel().clear();
		for (int i = 0; i < importSize; i++) {
			if (!management
					.getFileSelectionView()
					.getModel()
					.contains(
							fileImporter.getTextdateien().get(i)
									.getAbsolutePath()))
				management
						.getFileSelectionView()
						.getModel()
						.addElement(
								fileImporter.getTextdateien().get(i)
										.getAbsolutePath());

			management.getFileSelectionView().getLblFileCount()
					.setText(String.valueOf(importSize));
		}
	}

	public void updateLblRootPath() {
		// String labelRoot = fileImporter.getConfig().getRootDir();
		// if (labelRoot.length() > 30) {
		// labelRoot = labelRoot.substring(labelRoot.length() - 10,
		// labelRoot.length());
		// labelRoot = "..." + labelRoot;
		// }
		management.getFileSelectionView().getLblRootPath()
				.setText(fileImporter.getConfig().getRootDir());
	}

	public String[] getFileNames(int length) {
		String[] fileNames = new String[length];
		String alphabet = "A";
		char[] charAlphabet;
		for (int i = 0; i < length; i++) {
			fileNames[i] = alphabet;
			charAlphabet = alphabet.toCharArray();
			charAlphabet[charAlphabet.length - 1] += 1;
			alphabet = String.valueOf(charAlphabet);

			if (alphabet.contains("[")) {
				alphabet = "A" + alphabet;
				alphabet = alphabet.replace("[", "A");
			}
		}
		return fileNames;
	}

	public int getMode() {
		return mode;
	}

	public void appendToTextArea(String s) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		management
				.getMainView()
				.getTextArea()
				.setText(
						management.getMainView().getTextArea().getText()
								+ sdf.format(cal.getTime()) + "  " + s + "\n");
	}
}