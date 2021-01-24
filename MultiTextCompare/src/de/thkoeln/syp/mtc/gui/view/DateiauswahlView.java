package de.thkoeln.syp.mtc.gui.view;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class DateiauswahlView extends JFrame {
	private JPanel panel;
	private FileDialog fd;
	private File[] auswahl;

	private IFileImporter fileImport;
	private ITextvergleicher textVergleicher;
	private IMatrix matrix;

	public DateiauswahlView() {
		panel = new JPanel();
		fileImport = new IFileImporterImpl();
		textVergleicher = new ITextvergleicherImpl();
		matrix = new IMatrixImpl();

		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 1));

		fd = new FileDialog(this, "Dateiauswahl", FileDialog.LOAD);
		fd.setMultipleMode(true);
		fd.setDirectory(".");
		fd.setFile("*.txt");
		fd.setVisible(true);

		int anzahlDateien = fd.getFiles().length;
		String[] nameDateien = new String[anzahlDateien];
		for (int i = 0; i < anzahlDateien; i++) {
			nameDateien[i] = fd.getFiles()[i].getName();
		}

		// dateiSystem.add(panel);
		// Action details = dateiSystem.getActionMap().get("viewTypeDetails");
		// details.actionPerformed(null);
		// dateiSystem.setMultiSelectionEnabled(true);
		// dateiSystem.setCurrentDirectory(new File("."));
		// dateiSystem.setDialogTitle("Dateiauswahl");
		// dateiSystem.setMultiSelectionEnabled(true);
		// dateiSystem.showOpenDialog(panel);

		// ----------------------------------------------------------------
		// Code der so in etwa in den Controller ausgelagert werden m�sste
		auswahl = fd.getFiles();
		if(auswahl.length>1){	
		fileImport.importTextdateien(fileArrayToList(getAuswahl()));
		fileImport.createTempFiles();
		textVergleicher.getTempfilesFromHashMap(fileImport.getTempFilesMap());
		textVergleicher.getVergleiche(textVergleicher.getTempFiles());
		if (!fileImport.getConfig().getLineMatch()) {
			textVergleicher.vergleicheUeberGanzesDokument();
		} else {
			textVergleicher.vergleicheZeilenweise();
		}
		matrix = textVergleicher.getMatrix();
		new MatrixView((IMatrixImpl) matrix, anzahlDateien, nameDateien);
		}
		// ----------------------------------------------------------------

		this.setLocationRelativeTo(null);
	}

	public void actionPerformed(ActionEvent e) {

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

}
