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
import javax.swing.SwingUtilities;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.gui.view.OLD_MatrixView;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class DateiauswahlController extends JFrame {
	private JPanel panel;
	private FileDialog fd;
	private File[] auswahl;
	private List<File> auswahlGesamt;

	private IFileImporter fileImporter;
	private ITextvergleicher textVergleicher;
	private IMatrix matrix;

	private JFileChooser fc;

	private MainView mainView;
	private DateiauswahlView dateiauswahlView;

	public DateiauswahlController(MainView mainView,
			DateiauswahlView dateiauswahlView) {
		panel = new JPanel();
		auswahlGesamt = new ArrayList<File>();
		fileImporter = new IFileImporterImpl();
		textVergleicher = new ITextvergleicherImpl();
		matrix = new IMatrixImpl();
		this.mainView = mainView;
		this.dateiauswahlView = dateiauswahlView;
		dateiauswahlView.addSetRootListener(new SetRootListener());
		dateiauswahlView.addSuchenListener(new SuchenListener());
		dateiauswahlView.addEinfacheSucheListener(new EinfacheSucheListener());
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
			fileImporter.importTextRoot(dateiauswahlView
					.getTextFieldDateiname().getText());
			for (File f : fileImporter.getTextdateien()) {
				updateListFilePath(f);
			}
			// fileImporter.createTempFiles();
			// textVergleicher.getTempfilesFromHashMap(fileImporter
			// .getTempFilesMap());
			// textVergleicher.getVergleiche(textVergleicher.getTempFiles());
			// if (fileImporter.getConfig().getLineMatch() == false) {
			// textVergleicher.vergleicheUeberGanzesDokument();
			// } else {
			// textVergleicher.vergleicheZeilenweise();
			// }
			// matrix = dateiauswahlView.getTextvergleicher().getMatrix();
			// new MatrixView(matrix, textVergleicher.getTempFiles().size());
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
			fd.setFile("*.txt");
			fd.setVisible(true);
			auswahl = fd.getFiles();
			for (File f : auswahl) {
				updateListFilePath(f);
			}
			fileImporter.importTextdateien(auswahlGesamt);
			dateiauswahlView.setLocationRelativeTo(null);
		}
	}

	class VergleichenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			fileImporter.createTempFiles();
			textVergleicher.getTempfilesFromHashMap(fileImporter
					.getTempFilesMap());
			textVergleicher.getVergleiche(textVergleicher.getTempFiles());
			if (fileImporter.getConfig().getLineMatch() == false) {
				textVergleicher.vergleicheUeberGanzesDokument();
			} else {
				textVergleicher.vergleicheZeilenweise();
			}
			matrix = textVergleicher.getMatrix();
			String[] nameDateien = new String[auswahlGesamt.size()];
			for (int i = 0; i < auswahlGesamt.size(); i++) {
				nameDateien[i] = Character.toString((char) (('A' + i)));
			}
			System.out.println(auswahlGesamt.size());
			mainView.updateMatrix(matrix, auswahlGesamt.size(), nameDateien);

			// if (auswahl.length > 1) {
			// fileImport.importTextdateien(fileArrayToList(getAuswahl()));
			// fileImport.createTempFiles();
			// textVergleicher.getTempfilesFromHashMap(fileImport
			// .getTempFilesMap());
			// textVergleicher.getVergleiche(textVergleicher.getTempFiles());
			// if (!fileImport.getConfig().getLineMatch()) {
			// } else {
			// textVergleicher.vergleicheZeilenweise();
			// }
			// matrix = textVergleicher.getMatrix();
			// // new MatrixView((IMatrixImpl) matrix, anzahlDateien,
			// // nameDateien);
			// mainView.updateMatrix(matrix, anzahlDateien, nameDateien);
		}
	}

	public File[] getAuswahl() {
		return auswahl;
	}

	private List<File> fileArrayToList(File[] array) {
		List<File> fileListe = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			fileListe.add(array[i]);
		}
		return fileListe;
	}

	public IMatrix getMatrix() {
		return matrix;
	}

	public void updateListFilePath(File f) {
		if (!auswahlGesamt.contains(f)) {
			auswahlGesamt.add(f);
			dateiauswahlView.getModel().addElement(f.getAbsolutePath());
		}
	}

	public void updateLblRootPath(DateiauswahlView d) {
		d.getLblRootPath().setText(fileImporter.getConfig().getRootDir());
	}
}