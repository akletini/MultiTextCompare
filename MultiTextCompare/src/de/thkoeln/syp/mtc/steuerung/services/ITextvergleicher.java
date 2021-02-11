package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;

public interface ITextvergleicher {

	List<IAehnlichkeitImpl> getVergleiche(List<File> files);

	void vergleicheUeberGanzesDokument();
	
	void vergleicheZeilenweise();

	List<File> getTempfilesFromHashMap(Map<File, File> map, List<File> tempFiles);

	void setRef(File von);

	void setVgl(File vgl);

	List<IAehnlichkeitImpl> getPaarungen();

	List<File> getTempFiles();

	IMatrixImpl getMatrix();
	
}
