package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;

public interface ITextvergleicher {

	List<IAehnlichkeitImpl> getVergleiche(List<File> files);

	void vergleicheZeilenweise();

	void setRef(File von);

	void setVgl(File vgl);

	List<IAehnlichkeitImpl> getPaarungen();

	void vergleicheUeberGanzesDokument();

	void getTempfilesFromHashMap(Map<File, File> map);

	List<File> getTempFiles();

	IMatrixImpl getMatrix();

	
}
