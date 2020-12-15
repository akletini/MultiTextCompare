package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.control.HomeController.HilfeListener;
import de.thkoeln.syp.mtc.gui.view.Dateiauswahl2View;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.MatrixView;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class Dateiauswahl2Controller {
	private Dateiauswahl2View dateiauswahl2View;
	private File[] auswahl;
	private IFileImporter fileImport;
	private ITextvergleicher textVergleicher;
	private IMatrix matrix;
	private FileDialog fd;
	private JFileChooser fc;
	
	public Dateiauswahl2Controller(Dateiauswahl2View dateiauswahl2View){
		this.dateiauswahl2View = dateiauswahl2View;
		this.dateiauswahl2View.addVergleichenListener(new VergleichenListener());
		this.dateiauswahl2View.addWurzelverzeichnisListener(new WurzelverzeichnisListener());
		fileImport = this.dateiauswahl2View.getFileImport();
		textVergleicher = this.dateiauswahl2View.getTextvergleicher();
		matrix = new IMatrixImpl();	
	}
	
	class WurzelverzeichnisListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(fileImport.getConfig().getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(dateiauswahl2View);
			fileImport.setRootDir(fc.getSelectedFile());
			dateiauswahl2View.updateWurzelpfad();
			dateiauswahl2View.pack();
		}
	}
	
	class VergleichenListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			dateiauswahl2View.getFileImport().importTextRoot(dateiauswahl2View.getTextFieldName().getText());
			fileImport.createTempFiles();
			textVergleicher.getTempfilesFromHashMap(fileImport.getTempFilesMap());
			textVergleicher.getVergleiche(textVergleicher.getTempFiles());
			textVergleicher.vergleicheUeberGanzesDokument();
			matrix = dateiauswahl2View.getTextvergleicher().getMatrix();
			new MatrixView(matrix, textVergleicher.getTempFiles().size());
		}
	}
}
