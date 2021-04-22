package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;

public interface ITextvergleicher {

	List<IAehnlichkeitImpl> getVergleiche(List<File> files);

	void vergleicheUeberGanzesDokument(List<IAehnlichkeitImpl> list);
	
	void vergleicheZeilenweise(List<IAehnlichkeitImpl> batch);

	void setRef(File von);

	void setVgl(File vgl);

	List<IAehnlichkeitImpl> getPaarungen();

	List<File> getTempFiles();

	IMatrixImpl getMatrix();

	void getTempfilesFromHashMap(Map<File, File> map);

	void createBatches();

	void mergeBatches();

	List<IMatrixImpl> getBatches();

	void fillMatrix();

	
	
	
}
