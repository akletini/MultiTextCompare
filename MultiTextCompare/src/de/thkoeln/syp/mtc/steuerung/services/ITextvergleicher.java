package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;

public interface ITextvergleicher {

	List<IAehnlichkeitImpl> getVergleiche(List<File> files);

	void vergleicheZeilenweise();

	void setRef(File von);

	void setVgl(File vgl);

	List<IAehnlichkeitImpl> getPaarungen();

	void vergleicheUeberGanzesDokument();
	
}
