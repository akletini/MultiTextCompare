package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public interface IFileImporter {
	final String PROP_LEERZEICHEN = "beachteLeerzeichen";
	final String PROP_SATZZEICHEN = "beachteSatzzeichen";
	final String PROP_GROSSSCHREIBUNG = "beachteGrossschreibung";
	final String PROP_LEERZEILEN = "beachteLeerzeilen";
	final String PROP_ROOT = "rootDirectory";
	final String PROP_LINEMATCH = "lineMatch";
	final String PROP_DATEINAME = "dateiname";

	final String PROP_SORTIEREELEMENTE = "sortiereElemente";
	final String PROP_SORTIEREATTRIBUTE = "sortiereAttribute";
	final String PROP_LOESCHEATTRIBUTE = "loescheAttribute";
	final String PROP_LOESCHEKOMMENTARE = "loescheKommentare";
	final String PROP_NURTAGS = "nurTags";
	final String PROP_VALIDATION = "validation";

	final File DEFAULT_CONFIG = new File(System.getProperty("user.dir")
			+ File.separator + "config.properties");

	IConfig getConfig();

	List<File> getTextdateien();

	Map<File, File> getTempFilesMap();
	
	Map<File, File> getXmlTempFilesMap();

	Thread getRootImporter();

	boolean importConfigdatei(File config);

	boolean exportConfigdatei();

	boolean setConfigPath(String path);

	boolean setRootDir(File rootDir);

	boolean importTextdateien(List<File> textdateien);

	boolean importTextRoot(String fileName);

	void deleteImports();

	boolean deleteImport(File f);

	boolean createTempFiles();

	boolean normTempFiles();
	
	boolean createXmlTempFiles(Map<File, File> xmlFileMap);

	boolean deleteTempFiles();
}
