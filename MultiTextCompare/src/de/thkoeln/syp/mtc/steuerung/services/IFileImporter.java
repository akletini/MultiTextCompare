package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;

public interface IFileImporter {
	IConfigImpl getConfig();

	List<File> getTextdateien();
	
	boolean importConfigdatei();

	boolean importTextdateien();
	
	boolean importTextRoot();
}
