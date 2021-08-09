package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

/**
 * Verwaltet die zu vergleichenden Textdateien und die Konfiguration der
 * Anwendung.
 * 
 * @author Luca Uckermann
 *
 */
public class IFileImporterImpl implements IFileImporter {

	private IConfig iConfig;
	private List<File> textdateien;
	private Map<File, File> tempFiles, diffTempFiles;

	private Properties prop;
	private Thread rootImporter;

	/**
	 * Klassen Konstruktor initialisiert die Klassen-Attribute und laedt die
	 * Default-Config fuer den Textvergleich
	 */
	public IFileImporterImpl() {
		textdateien = new ArrayList<>();
		tempFiles = new LinkedHashMap<>();
		diffTempFiles = new LinkedHashMap<>();
		prop = new Properties();

		prop.setProperty(PROP_ROOT, System.getProperty("user.dir"));
		prop.setProperty(PROP_FILENAME, "");
		prop.setProperty(PROP_FILETYPE, ".txt");

		prop.setProperty(PROP_WHITESPACES, "true");
		prop.setProperty(PROP_BLANKLINES, "true");
		prop.setProperty(PROP_PUNCTUATION, "true");
		prop.setProperty(PROP_CAPITALIZATION, "true");
		prop.setProperty(PROP_COMPARELINES, "true");
		prop.setProperty(PROP_MAXLINELENGTH, "0");
		prop.setProperty(PROP_OPENLASTCOMPARISON, "false");
		prop.setProperty(PROP_LASTCOMPARISONPATH, "");

		prop.setProperty(PROP_LINEMATCH, "true");
		prop.setProperty(PROP_MATCHAT, "0.85");
		prop.setProperty(PROP_MATCHINGLOOKAHEAD, "50");
		prop.setProperty(PROP_BESTMATCH, "false");

		prop.setProperty(PROP_XMLVALIDATION, "0");
		prop.setProperty(PROP_XMLPRINT, "0");
		prop.setProperty(PROP_XMLSORTELEMNTS, "true");
		prop.setProperty(PROP_XMLSORTATTRIBUTES, "true");
		prop.setProperty(PROP_XMLDELETEATTRIBUTES, "false");
		prop.setProperty(PROP_XMLDELETECOMMENTS, "false");
		prop.setProperty(PROP_XMLONLYTAGS, "false");
		prop.setProperty(PROP_XMLUSESEMANTICCOMPARE, "true");
		prop.setProperty(PROP_XMLCOMPARECOMMENTS, "true");

		prop.setProperty(PROP_JSONSORTKEYS, "true");
		prop.setProperty(PROP_JSONDELETEVALUES, "false");
		prop.setProperty(PROP_JSONUSESEMANTICCOMPARE, "true");
		prop.setProperty(PROP_JSONKEEPARRAYORDER, "false");

		prop.setProperty(PROP_SHOWINFOS, "true");
		prop.setProperty(PROP_SHOWWARNINGS, "true");
		prop.setProperty(PROP_SHOWERRORS, "true");
		prop.setProperty(PATH_CURRENT_CONFIG, DEFAULT_CONFIG.getAbsolutePath());

		importConfigdatei(DEFAULT_CONFIG);
		File importedConfig = new File(prop.getProperty(PATH_CURRENT_CONFIG));
		if (!importedConfig.getAbsolutePath().equals(
				DEFAULT_CONFIG.getAbsolutePath())
				&& importedConfig.exists()) {
			importConfigdatei(importedConfig);
		} else {
			iConfig.setPathCurrent(DEFAULT_CONFIG.getAbsolutePath());
			exportConfigdatei();
		}
	}

	@Override
	public IConfig getConfig() {
		return iConfig;
	}

	@Override
	public List<File> getTextdateien() {
		return textdateien;
	}

	@Override
	public void setTextdateien(List<File> textDateien) {
		this.textdateien = textDateien;
	}

	@Override
	public Map<File, File> getTempFilesMap() {
		return tempFiles;
	}

	@Override
	public void setTempFiles(Map<File, File> tempFiles) {
		this.tempFiles = tempFiles;
	}

	@Override
	public Map<File, File> getDiffTempFilesMap() {
		return diffTempFiles;
	}

	@Override
	public Thread getRootImporter() {
		return rootImporter;
	}

	/**
	 * Versucht die uebergebene Config zu importieren, wenn nicht moeglich dann
	 * wird die Default-Config geladen
	 * 
	 * @param configPfad
	 *            die Config, welche importiert und uebernommen werden soll als
	 *            Pfad
	 * 
	 * @return true: bei erfolgreichem Laden der uebergebenen oder
	 *         Default-Config
	 * 
	 *         false: bei Fehlschlag des Imports
	 */
	@Override
	public boolean importConfigdatei(File config) {
		iConfig = new IConfigImpl();
		OutputStream outputStream;
		InputStream inputStream;

		if (config == null)
			return false;

		if (!config.exists()) {
			// übergebene Datei existiert nicht im Dateisystem
			// Default config existiert nicht
			if (!DEFAULT_CONFIG.exists()) {
				try {
					new File(System.getProperty("user.dir") + File.separator
							+ "configs").mkdir();
					outputStream = new FileOutputStream(
							System.getProperty("user.dir") + File.separator
									+ "configs" + File.separator
									+ "config.properties");

					prop.store(outputStream, null);

					iConfig.setPath(System.getProperty("user.dir")
							+ File.separator + "configs" + File.separator
							+ "config.properties");

					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			// übergebene Datei existiert nicht und Default existiert
			else {
				try {
					inputStream = new FileInputStream(DEFAULT_CONFIG);

					prop.load(inputStream);

					iConfig.setPath(DEFAULT_CONFIG.getAbsolutePath());

					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		// Datei existiert bereits
		else {
			try {
				inputStream = new FileInputStream(config);

				prop.load(inputStream);

				iConfig.setPath(config.getAbsolutePath());

				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		iConfig.setRootDir(prop.getProperty(PROP_ROOT));
		iConfig.setFilename(prop.getProperty(PROP_FILENAME));
		iConfig.setFiletype(prop.getProperty(PROP_FILETYPE));
		iConfig.setPathCurrent(prop.getProperty(PATH_CURRENT_CONFIG));

		iConfig.setKeepWhitespaces(Boolean.parseBoolean(prop
				.getProperty(PROP_WHITESPACES)));
		iConfig.setKeepBlankLines(Boolean.parseBoolean(prop
				.getProperty(PROP_BLANKLINES)));
		iConfig.setKeepPuctuation(Boolean.parseBoolean(prop
				.getProperty(PROP_PUNCTUATION)));
		iConfig.setKeepCapitalization(Boolean.parseBoolean(prop
				.getProperty(PROP_CAPITALIZATION)));
		iConfig.setCompareLines(Boolean.parseBoolean(prop
				.getProperty(PROP_COMPARELINES)));
		iConfig.setMaxLineLength(Integer.parseInt(prop
				.getProperty(PROP_MAXLINELENGTH)));
		iConfig.setOpenLastComparison(Boolean.parseBoolean(prop
				.getProperty(PROP_OPENLASTCOMPARISON)));
		iConfig.setLastComparisonPath(prop.getProperty(PROP_LASTCOMPARISONPATH));

		iConfig.setLineMatch(Boolean.parseBoolean(prop
				.getProperty(PROP_LINEMATCH)));
		iConfig.setMatchAt(Double.parseDouble(prop.getProperty(PROP_MATCHAT)));
		iConfig.setMatchingLookahead(Integer.parseInt(prop
				.getProperty(PROP_MATCHINGLOOKAHEAD)));
		iConfig.setBestMatch(Boolean.parseBoolean(prop
				.getProperty(PROP_BESTMATCH)));

		iConfig.setXmlValidation(Integer.parseInt(prop
				.getProperty(PROP_XMLVALIDATION)));
		iConfig.setXmlPrint(Integer.parseInt(prop.getProperty(PROP_XMLPRINT)));
		iConfig.setXmlSortElements(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLSORTELEMNTS)));
		iConfig.setXmlSortAttributes(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLSORTATTRIBUTES)));
		iConfig.setXmlDeleteAttributes(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLDELETEATTRIBUTES)));
		iConfig.setXmlDeleteComments(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLDELETECOMMENTS)));
		iConfig.setXmlOnlyTags(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLONLYTAGS)));
		iConfig.setXmlUseSemanticComparison(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLUSESEMANTICCOMPARE)));
		iConfig.setXmlCompareComments(Boolean.parseBoolean(prop
				.getProperty(PROP_XMLCOMPARECOMMENTS)));

		iConfig.setJsonSortKeys(Boolean.parseBoolean(prop
				.getProperty(PROP_JSONSORTKEYS)));
		iConfig.setJsonDeleteValues(Boolean.parseBoolean(prop
				.getProperty(PROP_JSONDELETEVALUES)));
		iConfig.setJsonUseSemanticComparison(Boolean.parseBoolean(prop
				.getProperty(PROP_JSONUSESEMANTICCOMPARE)));
		iConfig.setJsonKeepArrayOrder(Boolean.parseBoolean(prop
				.getProperty(PROP_JSONKEEPARRAYORDER)));

		iConfig.setShowInfos(Boolean.parseBoolean(prop
				.getProperty(PROP_SHOWINFOS)));
		iConfig.setShowWarnings(Boolean.parseBoolean(prop
				.getProperty(PROP_SHOWWARNINGS)));
		iConfig.setShowErrors(Boolean.parseBoolean(prop
				.getProperty(PROP_SHOWERRORS)));

		return true;
	}

	/**
	 * Eine aktuelle Config kann an einem anderen Speicherort abgelegt werden
	 * 
	 * @param path
	 *            Neuer Speicherort des Config-Files
	 * @return true: bei erfolgreichem Setzen des neuen Pfades & Speichern der
	 *         Config
	 * 
	 *         false: bei Fehlschlag (Pfad bleibt dann unveraendert)
	 */
	@Override
	public boolean setConfigPath(String path) {
		File oldConfig = new File(iConfig.getPath());

		if (path == null)
			return false;

		iConfig.setPath(path);
		if (!exportConfigdatei()) {
			iConfig.setPath(oldConfig.getAbsolutePath());
			return false;
		}

		oldConfig.delete();
		return true;
	}

	/**
	 * Das Root-Directory kann neu gesetzt werden
	 * 
	 * @param rootDir
	 *            Das neue Root-Directory in welchem fortan gesucht werden soll
	 * 
	 * @return true: bei erfolgreichem Setzen des neuen Root-Directories &
	 *         Speichern der Config
	 * 
	 *         false: bei Fehlschlag (Root-Directory bleibt dann unveraendert)
	 */
	@Override
	public boolean setRootDir(File rootDir) {
		String oldPath = iConfig.getRootDir();

		if (rootDir == null)
			return false;

		if (!rootDir.isDirectory())
			return false;

		iConfig.setRootDir(rootDir.getAbsolutePath());
		if (!exportConfigdatei()) {
			iConfig.setRootDir(oldPath);
			return false;
		}

		return true;
	}

	/**
	 * Aktuelle Config kann abgespeichert werden um die abgespeicherten Werte
	 * spaeter wieder importieren zu koennen
	 * 
	 * @return true: bei erfolgreichem Export
	 * 
	 *         false: bei Fehlschlag des Exports
	 */
	@Override
	public boolean exportConfigdatei() {
		Properties prop = new Properties();
		OutputStream outputStream;

		try {
			outputStream = new FileOutputStream(iConfig.getPath());

			prop.setProperty(PROP_ROOT, iConfig.getRootDir());
			prop.setProperty(PROP_FILENAME, iConfig.getFilename());
			prop.setProperty(PROP_FILETYPE, iConfig.getFiletype());
			prop.setProperty(PATH_CURRENT_CONFIG, iConfig.getPathCurrent());

			prop.setProperty(PROP_WHITESPACES,
					Boolean.toString(iConfig.getKeepWhitespaces()));
			prop.setProperty(PROP_BLANKLINES,
					Boolean.toString(iConfig.getKeepBlankLines()));
			prop.setProperty(PROP_PUNCTUATION,
					Boolean.toString(iConfig.getKeepPuctuation()));
			prop.setProperty(PROP_CAPITALIZATION,
					Boolean.toString(iConfig.getKeepCapitalization()));
			prop.setProperty(PROP_COMPARELINES,
					Boolean.toString(iConfig.getCompareLines()));
			prop.setProperty(PROP_MAXLINELENGTH,
					Integer.toString(iConfig.getMaxLineLength()));
			prop.setProperty(PROP_OPENLASTCOMPARISON,
					Boolean.toString(iConfig.getOpenLastComparison()));
			prop.setProperty(PROP_LASTCOMPARISONPATH,
					iConfig.getLastComparisonPath());

			prop.setProperty(PROP_LINEMATCH,
					Boolean.toString(iConfig.getLineMatch()));
			prop.setProperty(PROP_MATCHAT,
					Double.toString(iConfig.getMatchAt()));
			prop.setProperty(PROP_MATCHINGLOOKAHEAD,
					Integer.toString(iConfig.getMatchingLookahead()));
			prop.setProperty(PROP_BESTMATCH,
					Boolean.toString(iConfig.getBestMatch()));

			prop.setProperty(PROP_XMLVALIDATION,
					Integer.toString(iConfig.getXmlValidation()));
			prop.setProperty(PROP_XMLPRINT,
					Integer.toString(iConfig.getXmlPrint()));
			prop.setProperty(PROP_XMLSORTELEMNTS,
					Boolean.toString(iConfig.getXmlSortElements()));
			prop.setProperty(PROP_XMLSORTATTRIBUTES,
					Boolean.toString(iConfig.getXmlSortAttributes()));
			prop.setProperty(PROP_XMLDELETEATTRIBUTES,
					Boolean.toString(iConfig.getXmlDeleteAttributes()));
			prop.setProperty(PROP_XMLDELETECOMMENTS,
					Boolean.toString(iConfig.getXmlDeleteComments()));
			prop.setProperty(PROP_XMLONLYTAGS,
					Boolean.toString(iConfig.getXmlOnlyTags()));
			prop.setProperty(PROP_XMLUSESEMANTICCOMPARE,
					Boolean.toString(iConfig.isXmlUseSemanticComparison()));
			prop.setProperty(PROP_XMLCOMPARECOMMENTS,
					Boolean.toString(iConfig.getXmlCompareComments()));

			prop.setProperty(PROP_JSONSORTKEYS,
					Boolean.toString(iConfig.getJsonSortKeys()));
			prop.setProperty(PROP_JSONDELETEVALUES,
					Boolean.toString(iConfig.getJsonDeleteValues()));
			prop.setProperty(PROP_JSONUSESEMANTICCOMPARE,
					Boolean.toString(iConfig.isJsonUseSemanticComparison()));
			prop.setProperty(PROP_JSONKEEPARRAYORDER,
					Boolean.toString(iConfig.getJsonKeepArrayOrder()));

			prop.setProperty(PROP_SHOWINFOS,
					Boolean.toString(iConfig.getShowInfos()));
			prop.setProperty(PROP_SHOWWARNINGS,
					Boolean.toString(iConfig.getShowWarnings()));
			prop.setProperty(PROP_SHOWERRORS,
					Boolean.toString(iConfig.getShowErrors()));

			prop.store(outputStream, null);

			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Uebergebene Textdateien werden von der Festplatte importiert in lokaler
	 * Variable 'textdateien' abgespeichert
	 * 
	 * @param textdateien
	 *            Die zu importierenden Textdateien
	 * 
	 * @return true: bei erfolgreichem Import
	 * 
	 *         false: falls keine Textdatei importiert wurde
	 */
	@Override
	public boolean importTextdateien(List<File> textdateien) {
		if (textdateien == null)
			return false;

		if (textdateien.isEmpty())
			return false;

		for (File file : textdateien) {
			if (file.exists() && !this.textdateien.contains(file))
				this.textdateien.add(file);
		}

		return true;
	}

	/**
	 * Es wird das gesamte Root-Directory rekursiv nach Textdateien durchsucht
	 * 
	 * @param fileName
	 *            Name der zu importierende(n) Dateie(n)
	 * 
	 * @return true: bei erfolgreichem Import
	 * 
	 *         false: falls in der aktiven Config ein unzulaessiges rootDir
	 *         festgelegt wurde
	 */
	@Override
	public boolean importTextRoot(String fileName) {
		File rootDir = new File(iConfig.getRootDir());

		if (fileName == null || fileName.equals(""))
			return false;

		if (!rootDir.exists() || !rootDir.isDirectory())
			return false;

		fileName = fileName.toLowerCase();
		fileName = fileName.replaceAll("\\.", "\\\\.");
		fileName = fileName.replaceAll("\\?", "(.)");
		fileName = fileName.replaceAll("\\*", "(.*)");

		searchInDir(rootDir, fileName);
		return true;
	}

	/**
	 * Methode hilt beim Suchen nach Textdateien im Root-Directory
	 * 
	 * @param file
	 *            Ort an dem nach 'fileName' gesucht werden soll
	 * 
	 * @param fileName
	 *            Name des Files nach dem gesucht werden soll
	 * 
	 * @see {@link #importTextRoot(String)}
	 */
	private void searchInDir(File file, String fileName) {
		class RootImporter implements Runnable {
			File file;
			String fileName;
			List<File> textdateien;

			public RootImporter(File file, String fileName,
					List<File> textdateien) {
				this.file = file;
				this.fileName = fileName;
				this.textdateien = textdateien;
			}

			@Override
			public void run() {
				searchInDir(file, fileName);
			}

			private void searchInDir(File file, String fileName) {
				try {
					for (File f : file.listFiles()) {
						if (f.isDirectory()) {
							searchInDir(f, fileName);
						} else if (f.getName().toLowerCase().matches(fileName)
								&& !textdateien.contains(f)) {
							this.textdateien.add(f);
						}
					}
				} catch (Exception e) {
				}
			}
		}

		this.rootImporter = new Thread(new RootImporter(file, fileName,
				textdateien));
	}

	/**
	 * Alle importierten Textdateien werden aus dem MTC geloescht
	 */
	@Override
	public void deleteImports() {
		textdateien.clear();
		tempFiles.clear();
	}

	/**
	 * Eine einzelne Textdatei wird aus dem MTC geloescht
	 * 
	 * @return true: bei erfolgreichem Loeschen
	 * 
	 *         false: falls die zu loeschende Datei nicht existiert
	 */
	@Override
	public boolean deleteImport(File f) {
		if (f == null || !textdateien.contains(f))
			return false;

		if (tempFiles.containsKey(f))
			tempFiles.remove(f);
		textdateien.remove(f);

		return true;
	}

	/**
	 * Erstellt aus den bereits importierten Textdateien temporaere Dateien im
	 * Ordner 'TempFiles'. Die temporaeren Dateien werden in einer HashMap
	 * gespeichert um von diesen auch wieder auf die originalen Dateien
	 * schliessen zu koennen
	 * 
	 * @return true: bei erfolgreichem Erstellen der temporaeren Dateien
	 * 
	 *         false: falls beim Erstellen der temporaeren Dateien ein Fehler
	 *         auftritt
	 * @throws IOException 
	 */
	@Override
	public boolean createTempFiles() throws IOException {
		BufferedReader reader;
		BufferedWriter writer;
		int index = 1;

		new File(System.getProperty("user.dir") + File.separator + "TempFiles")
				.mkdirs();

		for (File f : this.textdateien) {
			String path = System.getProperty("user.dir") + File.separator
					+ "TempFiles" + File.separator + "temp_"
					+ Integer.toString(index);
			File temp = new File(path);

	
				if (temp.exists()) {
					temp.delete();
				}
				temp.createNewFile();

				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(f), "UTF-8"));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(temp), "UTF-8"));

				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line + "\n");
				}

				tempFiles.put(f, temp);
				index++;

				reader.close();
				writer.close();
			
		}

		return true;
	}

	/**
	 * Zuvor erstellte TempFiles werden unter Beruecksichtigung der aktuellen
	 * Config-Parameter normiert
	 * 
	 * @return true: bei erfolgreichem Normieren der temporaeren Dateien
	 * 
	 *         false: falls beim Normieren der temporaeren Dateien ein Fehler
	 *         auftritt
	 * 
	 * @see {@link #createTempFiles()}
	 */
	@Override
	public boolean normTempFiles() {
		BufferedReader reader;
		BufferedWriter writer;

		if (this.tempFiles.isEmpty())
			return false;

		for (File f : this.tempFiles.keySet()) {
			File temp = tempFiles.get(f);
			String text = "";

			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(temp), "UTF-8"));

				String line;
				while ((line = reader.readLine()) != null) {
					if (!iConfig.getKeepBlankLines())
						if (line.replaceAll("\\s", "").isEmpty())
							continue;
					if (!iConfig.getKeepCapitalization())
						line = line.toLowerCase();
					if (!iConfig.getKeepPuctuation())
						line = line.replaceAll("\\p{Punct}", "");
					if (!iConfig.getKeepWhitespaces())
						line = line.replaceAll("\\s", "\n");

					text += line + "\n";
				}
				reader.close();

				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(temp), "UTF-8"));

				writer.write(text);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	/**
	 * @deprecated Erstellt aus den manipulierten Dateien temporaere Dateien im
	 *             Ordner 'TempFiles'. Die temporaeren Dateien werden in einer
	 *             HashMap gespeichert um von diesen auch wieder auf die
	 *             originalen Dateien schliessen zu koennen
	 * 
	 * @param fileMap
	 *            manipulierte tempFileMap
	 * 
	 * @return true: bei erfolgreichem Erstellen der temporaeren Dateien
	 * 
	 *         false: falls beim Erstellen der temporaeren Dateien ein Fehler
	 *         auftritt
	 */
	@Override
	public boolean createDiffTempFiles(Map<File, File> fileMap) {
		BufferedReader reader;
		BufferedWriter writer;
		int index = 1;

		new File(System.getProperty("user.dir") + File.separator + "TempFiles")
				.mkdirs();

		for (File f : fileMap.keySet()) {
			File temp = fileMap.get(f);
			String path = System.getProperty("user.dir") + File.separator
					+ "TempFiles" + File.separator + "temp_"
					+ Integer.toString(index) + "diff";
			File tempDiff = new File(path);

			try {
				if (tempDiff.exists()) {
					tempDiff.delete();
				}
				tempDiff.createNewFile();

				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(temp), "UTF-8"));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tempDiff), "UTF-8"));

				String line;
				while ((line = reader.readLine()) != null) {
					if (!iConfig.getKeepBlankLines())
						if (line.replaceAll("\\s", "").isEmpty())
							continue;
					if (!iConfig.getKeepCapitalization())
						line = line.toLowerCase();
					if (!iConfig.getKeepPuctuation())
						line = line.replaceAll("\\p{Punct}", "");
					if (!iConfig.getKeepWhitespaces())
						line = line.replaceAll("\\s", "");

					writer.write(line + "\n");
				}

				diffTempFiles.put(f, tempDiff);
				index++;

				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	/**
	 * Loescht die zuvor erstellten temporaeren Dateien aus dem Dateisystem
	 * 
	 * @return true: bei erfolgreichem Loeschen der temporaeren Dateien
	 * 
	 *         false: falls der Ordner 'TempFiles' gar nicht existiert
	 * 
	 * @see {@link #createTempFiles()}
	 */
	@Override
	public boolean deleteTempFiles() {
		File tempFiles = new File(System.getProperty("user.dir")
				+ File.separator + "TempFiles");

		if (tempFiles.exists() && tempFiles.isDirectory()) {
			for (File f : tempFiles.listFiles())
				f.delete();

			this.tempFiles.clear();
			this.diffTempFiles.clear();
		}

		return true;
	}
}
