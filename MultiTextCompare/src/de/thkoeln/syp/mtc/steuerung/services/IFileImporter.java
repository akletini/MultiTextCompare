package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;

public interface IFileImporter {
	final String PROP_LEERZEICHEN = "beachteLeerzeichen";
	final String PROP_SATZZEICHEN = "beachteSatzzeichen";
	final String PROP_GROSSSCHREIBUNG = "beachteGrossschreibung";
	final String PROP_ROOT = "rootDirectory";
	final File DEFAULT_CONFIG = new File(System.getProperty("user.dir")
			+ "/config.properties");

	IConfigImpl getConfig();

	List<File> getTextdateien();

	Map<File, File> getTempFilesMap();

	boolean importConfigdatei(File config);

	boolean exportConfigdatei();

	boolean importTextdateien(List<String> textdateien);

	boolean importTextRoot(String fileName);

	void loescheImports();

	boolean createTempFiles();

	boolean deleteTempFiles();
}
