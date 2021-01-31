package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class DateiauswahlController extends JFrame {
	private JPanel panel;
	private FileDialog fd;
	private JFileChooser fc;
	private File[] auswahl;
	private List<File> auswahlGesamt;
	private IFileImporter fileImporter;
	private ITextvergleicher textVergleicher;
	private IMatrix matrix;
	private MainView mainView;
	private DateiauswahlView dateiauswahlView;

	public DateiauswahlController(MainView mainView,
			DateiauswahlView dateiauswahlView) {
		panel = new JPanel();
		fileImporter = new IFileImporterImpl();
		textVergleicher = new ITextvergleicherImpl();
		matrix = new IMatrixImpl();
		this.mainView = mainView;
		this.dateiauswahlView = dateiauswahlView;
		dateiauswahlView.getTextFieldDateiname().setText(
				fileImporter.getConfig().getDateiname());
		dateiauswahlView.addSetRootListener(new SetRootListener());
		dateiauswahlView.addSuchenListener(new SuchenListener());
		dateiauswahlView.addEinfacheSucheListener(new EinfacheSucheListener());
		dateiauswahlView.addResetListener(new ResetListener());
		dateiauswahlView.addVergleichenListener(new VergleichenListener());
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
			fc.showOpenDialog(dateiauswahlView);
			fileImporter.setRootDir(fc.getSelectedFile());
			updateLblRootPath(dateiauswahlView);
			// dateiauswahlView.pack();
		}
	}

	class SuchenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<File> ref = new ArrayList<File>(fileImporter.getTextdateien());
			fileImporter.importTextRoot(dateiauswahlView
					.getTextFieldDateiname().getText() + getFileExt());
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
					dateiauswahlView.getTextFieldDateiname().getText());
			fileImporter.exportConfigdatei();
		}
	}

	class EinfacheSucheListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
			panel.setLayout(new GridLayout(0, 1));
			fd = new FileDialog(dateiauswahlView, "Dateiauswahl",
					FileDialog.LOAD);
			fd.setMultipleMode(true);
			fd.setDirectory(".");
			fd.setFile("*" + getFileExt());
			fd.setVisible(true);
			auswahl = fd.getFiles();
			auswahlGesamt = new ArrayList<File>();
			for (File f : auswahl) {
				auswahlGesamt.add(f);
			}
			fileImporter.importTextdateien(auswahlGesamt);
			dateiauswahlView.setLocationRelativeTo(null);
			setRdbtn(fileImporter.getTextdateien().isEmpty());
			updateListFilePath();
		}
	}

	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fileImporter.loescheImports();
			fileImporter.deleteTempFiles();
			dateiauswahlView.getModel().clear();
			setRdbtn(true);
		}
	}

	class VergleichenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int anzDateien = fileImporter.getTextdateien().size();
			if (anzDateien > 1) {
				fileImporter.createTempFiles();
				fileImporter.normTempFiles();
				textVergleicher.getTempfilesFromHashMap(fileImporter
						.getTempFilesMap());
				textVergleicher.getVergleiche(textVergleicher.getTempFiles());
				if (fileImporter.getConfig().getLineMatch() == false) {
					textVergleicher.vergleicheUeberGanzesDokument();
				} else {
					textVergleicher.vergleicheZeilenweise();
				}
				matrix = textVergleicher.getMatrix();
				mainView.updateMatrix(matrix, anzDateien,
						getNameDateien(anzDateien));
				mainView.getTextArea().setText(
						mainView.getTextArea().getText()
								+ "\n Eine Matrix mit " + anzDateien
								+ " Dateien wurde erfolgreich erstellt!");
			} else
				new PopupView("Error",
						"Bitte wählen Sie mindestens 2 Dateien aus");
		}
	}

	public File[] getAuswahl() {
		return auswahl;
	}

	public IMatrix getMatrix() {
		return matrix;
	}

	public String getFileExt() {
		if (dateiauswahlView.getRadioButton() == 1)
			return ".txt";
		if (dateiauswahlView.getRadioButton() == 2)
			return ".xml";
		if (dateiauswahlView.getRadioButton() == 3)
			return ".json";
		else
			return "";
	}

	public void setRdbtn(boolean b) {
		dateiauswahlView.getRdbtnTxt().setEnabled(b);
		dateiauswahlView.getRdbtnXml().setEnabled(b);
		dateiauswahlView.getRdbtnJson().setEnabled(b);
		dateiauswahlView.getRdbtnAll().setEnabled(b);
	}

	// private List<File> fileArrayToList(File[] array) {
	// List<File> fileListe = new ArrayList<>();
	// for (int i = 0; i < array.length; i++) {
	// fileListe.add(array[i]);
	// }
	// return fileListe;
	// }

	public void updateListFilePath() {
		int importSize = fileImporter.getTextdateien().size();
		String[] nameDateien = getNameDateien(importSize);
		for (int i = 0; i < importSize; i++) {
			if (!dateiauswahlView.getModel().contains(
					fileImporter.getTextdateien().get(i).getAbsolutePath()))
				dateiauswahlView.getModel().addElement(
						nameDateien[i]
								+ ":  "
								+ fileImporter.getTextdateien().get(i)
										.getAbsolutePath());
		}
	}

	public void updateLblRootPath(DateiauswahlView d) {
		d.getLblRootPath().setText(fileImporter.getConfig().getRootDir());
	}

	public String[] getNameDateien(int anzahl) {
		String[] nameDateien = new String[anzahl];
		for (int i = 0; i < anzahl; i++) {
			nameDateien[i] = Character.toString((char) (('A' + i)));
		}
		return nameDateien;
	}

}