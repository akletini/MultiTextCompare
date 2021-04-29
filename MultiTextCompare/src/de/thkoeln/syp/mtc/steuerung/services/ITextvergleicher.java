package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;

public interface ITextvergleicher {

	List<IAehnlichkeitImpl> getVergleiche(List<File> files);

	void vergleicheUeberGanzesDokument(List<IAehnlichkeitImpl> list);
	
	void vergleicheZeilenweise(List<IAehnlichkeitImpl> batch);

	List<IAehnlichkeitImpl> getPaarungen();

	List<File> getTempFiles();

	IMatrixImpl getMatrix();

	void getTempfilesFromHashMap(Map<File, File> map);

	void createBatches();

	void mergeBatches();

	List<IMatrixImpl> getBatches();

	void fillMatrix();

	void setFileImporter(IFileImporter fileImporter);

	void compareJSON(List<IAehnlichkeitImpl> batch);

	int calculateLevenshteinDist(String ref, String comp, Integer threshold);

	
	
	
}
