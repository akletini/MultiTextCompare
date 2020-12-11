package de.thkoeln.syp.mtc.gui.view;

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
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class DateiauswahlView extends JFrame {
	JPanel panel;
	JFileChooser dateiSystem;
	private File[] auswahl;
	
	IFileImporter fileImport;
	ITextvergleicher textVergleicher;
	private IMatrix matrix;

	public DateiauswahlView() {
		panel = new JPanel();
		dateiSystem = new JFileChooser();
		fileImport = new IFileImporterImpl();
		textVergleicher = new ITextvergleicherImpl();
		matrix = new IMatrixImpl();

		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 1));
		
		//----------------------------------------------------------------
		//Code der so in etwa in den Controller ausgelagert werden müsste
		auswahl = dateiSystem.getSelectedFiles();
		fileImport.importTextdateien(fileArrayToList(getAuswahl()));
		fileImport.createTempFiles();
		textVergleicher.getTempfilesFromHashMap(fileImport.getTempFilesMap());
		textVergleicher.getVergleiche(textVergleicher.getTempFiles());
		textVergleicher.vergleicheUeberGanzesDokument();
		matrix = textVergleicher.getMatrix();
		//----------------------------------------------------------------
		
		Action details = dateiSystem.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		dateiSystem.setMultiSelectionEnabled(true);
		dateiSystem.setCurrentDirectory(new File("F:\\"));
		dateiSystem.showOpenDialog(panel);
		

		this.setLocationRelativeTo(null);
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
	
	public File[] getAuswahl(){
		return auswahl;
	}
	
	private List<File> fileArrayToList(File[] array){
		List<File> fileListe = new ArrayList<>();
		for(int i = 0; i < array.length; i++){
			fileListe.add(array[i]);
		}
		return fileListe;
	}
	
	public IMatrix getMatrix(){
		return matrix;
	}

}
