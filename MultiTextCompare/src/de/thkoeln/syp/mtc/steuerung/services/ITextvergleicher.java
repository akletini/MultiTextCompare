package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;

/**
 * Verwaltet alle Vergleichsalgorithmen und das Setup von Vergleichen. Dabei
 * werden die Dateien von IFileImporter nach Vergleichen aufgeteilt und nach
 * Gewicht sortiert auf verfügbare CPU-Threads verteilt.
 * 
 * @author Allen Kletinitch
 *
 */
public interface ITextvergleicher {

	List<IComparisonImpl> getVergleiche(List<File> files);

	void vergleicheUeberGanzesDokument(List<IComparisonImpl> list);
	
	void vergleicheZeilenweise(List<IComparisonImpl> batch);

	List<IComparisonImpl> getPaarungen();

	List<File> getTempFiles();

	IMatrixImpl getMatrix();

	void getTempfilesFromHashMap(Map<File, File> map);

	void createBatches();

	void mergeBatches();

	List<IMatrixImpl> getBatches();

	void fillMatrix();

	void setFileImporter(IFileImporter fileImporter);

	void compareJSON(List<IComparisonImpl> batch);

	int calculateLevenshteinDist(String ref, String comp, Integer threshold);

	void compareXML(List<IComparisonImpl> batch);

	
	
	
}
