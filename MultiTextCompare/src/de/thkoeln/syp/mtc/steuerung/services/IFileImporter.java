package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public interface IFileImporter {
	final String PROP_ROOT = "rootDirectory";
	final String PROP_FILENAME = "filename";
	final String PROP_FILETYPE = "filetype";

	final String PROP_WHITESPACES = "whitespaces";
	final String PROP_BLANKLINES = "blanklines";
	final String PROP_PUNCTUATION = "punctuation";
	final String PROP_CAPITALIZATION = "capitalization";
	final String PROP_COMPARELINES = "compareLines";
	final String PROP_LINEMATCH = "lineMatch";

	final String PROP_XMLVALIDATION = "xmlValidation";
	final String PROP_XMLPRINT = "xmlPrint";
	final String PROP_XMLSORTELEMNTS = "xmlSortElements";
	final String PROP_XMLSORTATTRIBUTES = "xmlSortAttributes";
	final String PROP_XMLDELETEATTRIBUTES = "xmlDeleteAttributes";
	final String PROP_XMLDELETECOMMENTS = "xmlDeleteComments";
	final String PROP_XMLONLYTAGS = "xmlOnlyTags";
	
	final String PROP_JSONSORTKEYS = "jsonSortKeys";
	final String PROP_JSONDELETEVALUES = "jsonDeleteValues";

	final File DEFAULT_CONFIG = new File(System.getProperty("user.dir")
			+ File.separator + "config.properties");

	IConfig getConfig();

	List<File> getTextdateien();

	Map<File, File> getTempFilesMap();

	Map<File, File> getDiffTempFilesMap();

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

	boolean createDiffTempFiles(Map<File, File> xmlFileMap);

	boolean deleteTempFiles();
}
