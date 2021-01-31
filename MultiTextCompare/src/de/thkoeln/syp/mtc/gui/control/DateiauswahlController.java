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
	private Management management;
	private JPanel panel;
	private FileDialog fd;
	private JFileChooser fc;
	private File[] auswahl;
	private List<File> auswahlGesamt;
	private IMatrix matrix;

	public DateiauswahlController(DateiauswahlView dateiauswahlView) {
		management = Management.getInstance();
		panel = new JPanel();
		matrix = new IMatrixImpl();
		dateiauswahlView.getTextFieldDateiname().setText(
				management.getFileImporter().getConfig().getDateiname());
		dateiauswahlView.addSetRootListener(new SetRootListener());
		dateiauswahlView.addSuchenListener(new SuchenListener());
		dateiauswahlView.addEinfacheSucheListener(new EinfacheSucheListener());
		dateiauswahlView.addResetListener(new ResetListener());
		dateiauswahlView.addVergleichenListener(new VergleichenListener());
		dateiauswahlView.getLblRootPath()
		.setText(management.getFileImporter().getConfig().getRootDir());
	}

	class SetRootListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			Action details = fc.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			fc.setCurrentDirectory(new File(management.getFileImporter()
					.getConfig().getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(management.getDateiauswahlView());
			management.getFileImporter().setRootDir(fc.getSelectedFile());
			updateLblRootPath();
			// management.getDateiauswahlView().pack();
		}
	}

	class SuchenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<File> ref = new ArrayList<File>(management.getFileImporter()
					.getTextdateien());
			management.getFileImporter().importTextRoot(
					management.getDateiauswahlView().getTextFieldDateiname()
							.getText()
							+ getFileExt());
			management.getFileImporter().getRootImporter().start();
			try {
				management.getFileImporter().getRootImporter().join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (management.getFileImporter().getTextdateien().equals(ref))
				new PopupView("Hinweis",
						"Bei dieser Suche wurden keine weiteren Dateien gefunden");

			setRdbtn(management.getFileImporter().getTextdateien().isEmpty());
			updateListFilePath();
			management
					.getFileImporter()
					.getConfig()
					.setDateiname(
							management.getDateiauswahlView()
									.getTextFieldDateiname().getText());
			management.getFileImporter().exportConfigdatei();
		}
	}

	class EinfacheSucheListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
			panel.setLayout(new GridLayout(0, 1));
			fd = new FileDialog(management.getDateiauswahlView(),
					"Dateiauswahl", FileDialog.LOAD);
			fd.setMultipleMode(true);
			fd.setDirectory(".");
			fd.setFile("*" + getFileExt());
			fd.setVisible(true);
			auswahl = fd.getFiles();
			auswahlGesamt = new ArrayList<File>();
			for (File f : auswahl) {
				auswahlGesamt.add(f);
			}
			management.getFileImporter().importTextdateien(auswahlGesamt);
			management.getDateiauswahlView().setLocationRelativeTo(null);
			setRdbtn(management.getFileImporter().getTextdateien().isEmpty());
			updateListFilePath();
		}
	}

	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.getFileImporter().deleteImports();
			management.getFileImporter().deleteTempFiles();
			management.getDateiauswahlView().getModel().clear();
			setRdbtn(true);
		}
	}

	class VergleichenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int anzDateien = management.getFileImporter().getTextdateien()
					.size();
			if (anzDateien > 1) {
				management.getFileImporter().deleteTempFiles();
				management.getFileImporter().createTempFiles();
				management.getFileImporter().normTempFiles();
				management.getTextVergleicher().getTempfilesFromHashMap(
						management.getFileImporter().getTempFilesMap());
				management.getTextVergleicher().getVergleiche(
						management.getTextVergleicher().getTempFiles());
				if (management.getFileImporter().getConfig().getLineMatch() == false) {
					management.getTextVergleicher()
							.vergleicheUeberGanzesDokument();
				} else {
					management.getTextVergleicher().vergleicheZeilenweise();
				}
				matrix = management.getTextVergleicher().getMatrix();
				management.getMainView().updateMatrix(matrix, anzDateien,
						getNameDateien(anzDateien));
				management
						.getMainView()
						.getTextArea()
						.setText(
								management.getMainView().getTextArea()
										.getText()
										+ "\n Eine Matrix mit "
										+ anzDateien
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
		if (management.getDateiauswahlView().getRadioButton() == 1)
			return ".txt";
		if (management.getDateiauswahlView().getRadioButton() == 2)
			return ".xml";
		if (management.getDateiauswahlView().getRadioButton() == 3)
			return ".json";
		else
			return "";
	}

	public void setRdbtn(boolean b) {
		management.getDateiauswahlView().getRdbtnTxt().setEnabled(b);
		management.getDateiauswahlView().getRdbtnXml().setEnabled(b);
		management.getDateiauswahlView().getRdbtnJson().setEnabled(b);
		management.getDateiauswahlView().getRdbtnAll().setEnabled(b);
	}

	// private List<File> fileArrayToList(File[] array) {
	// List<File> fileListe = new ArrayList<>();
	// for (int i = 0; i < array.length; i++) {
	// fileListe.add(array[i]);
	// }
	// return fileListe;
	// }

	public void updateListFilePath() {
		int importSize = management.getFileImporter().getTextdateien().size();
		String[] nameDateien = getNameDateien(importSize);
		for (int i = 0; i < importSize; i++) {
			if (!management
					.getDateiauswahlView()
					.getModel()
					.contains(
							management.getFileImporter().getTextdateien()
									.get(i).getAbsolutePath()))
				management
						.getDateiauswahlView()
						.getModel()
						.addElement(
								nameDateien[i]
										+ ":  "
										+ management.getFileImporter()
												.getTextdateien().get(i)
												.getAbsolutePath());
		}
	}

	public void updateLblRootPath() {
		management.getDateiauswahlView().getLblRootPath()
				.setText(management.getFileImporter().getConfig().getRootDir());
	}

	public String[] getNameDateien(int anzahl) {
		String[] nameDateien = new String[anzahl];
		for (int i = 0; i < anzahl; i++) {
			nameDateien[i] = Character.toString((char) (('A' + i)));
		}
		return nameDateien;
	}

}