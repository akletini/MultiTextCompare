package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
	private List<File> lastComparisonFiles;
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
		lastComparisonFiles = new ArrayList<File>();
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
			management.getFileSelectionView().pack();
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
			fileImporter.getConfig().setDateityp(getFileExt());
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
			fileImporter.getConfig().setDateityp(getFileExt());
			updateListFilePath();
			mode = management.getFileSelectionView().getRadioButton();
		}
	}

	class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (File f : getListSelection()) {
				fileImporter.deleteImport(f);
			}
			updateListFilePath();
			if (management.getFileSelectionView().getModel().isEmpty())
				management.getFileSelectionView().getLblFileCount()
						.setText("0");
		}
	}

	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fileImporter.deleteImports();
			fileImporter.deleteTempFiles();
			management.getFileSelectionView().getModel().clear();
			management.getFileSelectionView().getLblFileCount().setText("0");
			setRdbtn(true);
		}
	}

	class CompareListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int anzDateien = fileImporter.getTextdateien().size();
			if (anzDateien < 2) {
				new PopupView("Error",
						"Please select at least two files for comparison");
				return;
			}
			// if (!lastComparisonFiles.equals(fileImporter.getTextdateien())) {
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
					.getFileImporter().getTempFilesMap(), textvergleicher.getTempFiles());
			textvergleicher.getVergleiche(textvergleicher.getTempFiles());
			if (fileImporter.getConfig().getLineMatch() == false) {
				textvergleicher.vergleicheUeberGanzesDokument();
			} else {
				textvergleicher.vergleicheZeilenweise();
			}

			management.getMainView().updateMatrix(textvergleicher.getMatrix(),
					anzDateien, getFileNames(anzDateien));

			lastComparisonFiles.clear();
			lastComparisonFiles.addAll(fileImporter.getTextdateien());

			if (!xmlvergleicher.getErrorList().isEmpty()) {
				appendToTextArea("A matrix with "
						+ anzDateien
						+ " files has been created, but the file selection contained "
						+ xmlvergleicher.getErrorList().size() + " XML errors.");
			}

			else {
				appendToTextArea("A matrix with " + anzDateien
						+ " files has been created successfully!");
			}
		}
		// }
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
		String[] fileNames = getFileNames(importSize);
		management.getFileSelectionView().getModel().clear();
		for (int i = 0; i < importSize; i++) {
			management
					.getFileSelectionView()
					.getModel()
					.addElement(
							fileNames[i]
									+ ":  "
									+ fileImporter.getTextdateien().get(i)
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

	public List<String> convertToPaths(List<String> list) {
		List<String> pathList = new ArrayList<String>();
		int temp = 4;
		int index = 0;
		if (list.size() <= 26)
			for (int i = 0; i < management.getFileSelectionView()
					.getListFilePath().getSelectedValuesList().size(); i++) {
				pathList.add(list.get(i).substring(temp));
				index++;
				if (index == 26) {
					temp = 5;
				}
			}
		return pathList;
	}

	public List<File> getListSelection() {
		List<String> selectedPaths = convertToPaths(management
				.getFileSelectionView().getListFilePath()
				.getSelectedValuesList());
		List<File> selectedFiles = new ArrayList<File>();
		for (String s : selectedPaths) {
			Path path = Paths.get(s);
			selectedFiles.add(path.toFile());
		}
		return selectedFiles;
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
								+ sdf.format(cal.getTime()) + " | " + s + "\n");
	}
}