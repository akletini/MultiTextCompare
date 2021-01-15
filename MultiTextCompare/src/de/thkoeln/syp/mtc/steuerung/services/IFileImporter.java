package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public interface IFileImporter {
	final String PROP_LEERZEICHEN = "beachteLeerzeichen";
	final String PROP_SATZZEICHEN = "beachteSatzzeichen";
	final String PROP_GROSSSCHREIBUNG = "beachteGrossschreibung";
	final String PROP_ROOT = "rootDirectory";
	final String PROP_LINEMATCH = "lineMatch";
	final String PROP_DATEINAME = "dateiname";
	final File DEFAULT_CONFIG = new File(System.getProperty("user.dir")
			+ File.separator + "config.properties");

	IConfig getConfig();

	List<File> getTextdateien();

	Map<File, File> getTempFilesMap();

	boolean importConfigdatei(File config);

	boolean exportConfigdatei();

	boolean setConfigPath(String path);

	boolean setRootDir(File rootDir);

	boolean importTextdateien(List<File> textdateien);

	boolean importTextRoot(String fileName);

	void loescheImports();

	boolean createTempFiles();

	boolean deleteTempFiles();
}
