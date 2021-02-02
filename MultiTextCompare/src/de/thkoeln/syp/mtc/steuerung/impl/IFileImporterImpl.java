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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IFileImporterImpl implements IFileImporter {

	private IConfig iConfig;
	private List<File> textdateien;
	private Map<File, File> tempFiles, xmlTempFiles;
	private Properties prop;
	private Thread rootImporter;

	/**
	 * Klassen Konstruktor initialisiert die Klassen-Attribute und laedt die
	 * Default-Config fuer den Textvergleich
	 */
	public IFileImporterImpl() {
		textdateien = new ArrayList<>();
		tempFiles = new HashMap<>();
		xmlTempFiles = new HashMap<>();
		prop = new Properties();

		prop.setProperty(PROP_LEERZEICHEN, "true");
		prop.setProperty(PROP_SATZZEICHEN, "true");
		prop.setProperty(PROP_GROSSSCHREIBUNG, "true");
		prop.setProperty(PROP_LEERZEILEN, "true");
		prop.setProperty(PROP_ROOT, System.getProperty("user.dir"));
		prop.setProperty(PROP_LINEMATCH, "true");
		prop.setProperty(PROP_DATEINAME, "");
		prop.setProperty(PROP_DATEITYP, ".txt");

		prop.setProperty(PROP_SORTIEREELEMENTE, "true");
		prop.setProperty(PROP_SORTIEREATTRIBUTE, "true");
		prop.setProperty(PROP_LOESCHEATTRIBUTE, "false");
		prop.setProperty(PROP_LOESCHEKOMMENTARE, "false");
		prop.setProperty(PROP_NURTAGS, "false");
		prop.setProperty(PROP_VALIDATION, "0");

		importConfigdatei(DEFAULT_CONFIG);
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
	public Map<File, File> getTempFilesMap() {
		return tempFiles;
	}

	@Override
	public Map<File, File> getXmlTempFilesMap() {
		return xmlTempFiles;
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
			if (!DEFAULT_CONFIG.exists()) {
				try {
					outputStream = new FileOutputStream(
							System.getProperty("user.dir") + File.separator
									+ "config.properties");

					prop.store(outputStream, null);

					iConfig.setPath(System.getProperty("user.dir")
							+ File.separator + "config.properties");

					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else {
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
		} else {
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

		iConfig.setBeachteLeerzeichen(Boolean.parseBoolean(prop
				.getProperty(PROP_LEERZEICHEN)));
		iConfig.setBeachteSatzzeichen(Boolean.parseBoolean(prop
				.getProperty(PROP_SATZZEICHEN)));
		iConfig.setBeachteGrossschreibung(Boolean.parseBoolean(prop
				.getProperty(PROP_GROSSSCHREIBUNG)));
		iConfig.setBeachteLeerzeilen(Boolean.parseBoolean(prop
				.getProperty(PROP_LEERZEILEN)));
		iConfig.setRootDir(prop.getProperty(PROP_ROOT));
		iConfig.setLineMatch(Boolean.parseBoolean(prop
				.getProperty(PROP_LINEMATCH)));
		iConfig.setDateiname(prop.getProperty(PROP_DATEINAME));
		iConfig.setDateityp(prop.getProperty(PROP_DATEITYP));

		iConfig.setSortiereElemente(Boolean.parseBoolean(prop
				.getProperty(PROP_SORTIEREELEMENTE)));
		iConfig.setSortiereAttribute(Boolean.parseBoolean(prop
				.getProperty(PROP_SORTIEREATTRIBUTE)));
		iConfig.setLoescheAttribute(Boolean.parseBoolean(prop
				.getProperty(PROP_LOESCHEATTRIBUTE)));
		iConfig.setLoescheKommentare(Boolean.parseBoolean(prop
				.getProperty(PROP_LOESCHEKOMMENTARE)));
		iConfig.setNurTags(Boolean.parseBoolean(prop.getProperty(PROP_NURTAGS)));
		iConfig.setValidation(Integer.parseInt(prop
				.getProperty(PROP_VALIDATION)));

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

			prop.setProperty(PROP_LEERZEICHEN,
					Boolean.toString(iConfig.getBeachteLeerzeichen()));
			prop.setProperty(PROP_SATZZEICHEN,
					Boolean.toString(iConfig.getBeachteSatzzeichen()));
			prop.setProperty(PROP_GROSSSCHREIBUNG,
					Boolean.toString(iConfig.getBeachteGrossschreibung()));
			prop.setProperty(PROP_LEERZEILEN,
					Boolean.toString(iConfig.getBeachteLeerzeilen()));
			prop.setProperty(PROP_ROOT, iConfig.getRootDir());
			prop.setProperty(PROP_LINEMATCH,
					Boolean.toString(iConfig.getLineMatch()));
			prop.setProperty(PROP_DATEINAME, iConfig.getDateiname());
			prop.setProperty(PROP_DATEITYP, iConfig.getDateityp());

			prop.setProperty(PROP_SORTIEREELEMENTE,
					Boolean.toString(iConfig.getSortiereElemente()));
			prop.setProperty(PROP_SORTIEREATTRIBUTE,
					Boolean.toString(iConfig.getSortiereAttribute()));
			prop.setProperty(PROP_LOESCHEATTRIBUTE,
					Boolean.toString(iConfig.getLoescheAttribute()));
			prop.setProperty(PROP_LOESCHEKOMMENTARE,
					Boolean.toString(iConfig.getLoescheKommentare()));
			prop.setProperty(PROP_NURTAGS,
					Boolean.toString(iConfig.getNurTags()));
			prop.setProperty(PROP_VALIDATION,
					Integer.toString(iConfig.getValidation()));

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

		if (!rootDir.isDirectory())
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
	 */
	@Override
	public boolean createTempFiles() {
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

			try {
				if (temp.exists()) {
					temp.delete();
				}
				temp.createNewFile();

				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(temp)));

				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line + "\n");
				}

				tempFiles.put(f, temp);
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
						new FileInputStream(temp)));

				String line;
				while ((line = reader.readLine()) != null) {
					if (!iConfig.getBeachteLeerzeilen())
						if (line.isEmpty())
							continue;
					if (!iConfig.getBeachteGrossschreibung())
						line = line.toLowerCase();
					if (!iConfig.getBeachteSatzzeichen())
						line = line.replaceAll("\\p{Punct}", "");
					if (!iConfig.getBeachteLeerzeichen())
						line = line.replaceAll("\\s", "\n");
					else
						line = line.replaceAll(" ", " \n");

					text += line + "\n";
				}
				reader.close();

				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(temp)));

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
	 * Erstellt aus den manipulierten XML-Dateien temporaere Dateien im Ordner
	 * 'TempFiles'. Die temporaeren Dateien werden in einer HashMap gespeichert
	 * um von diesen auch wieder auf die originalen Dateien schliessen zu
	 * koennen
	 * 
	 * @param xmlFileMap
	 *            manipulierte tempFileMap
	 * 
	 * @return true: bei erfolgreichem Erstellen der temporaeren Dateien
	 * 
	 *         false: falls beim Erstellen der temporaeren Dateien ein Fehler
	 *         auftritt
	 */
	@Override
	public boolean createXmlTempFiles(Map<File, File> xmlFileMap) {
		BufferedReader reader;
		BufferedWriter writer;
		int index = 1;

		new File(System.getProperty("user.dir") + File.separator + "TempFiles")
				.mkdirs();

		for (File f : xmlFileMap.keySet()) {
			File temp = xmlFileMap.get(f);
			String path = System.getProperty("user.dir") + File.separator
					+ "TempFiles" + File.separator + "temp_"
					+ Integer.toString(index) + "xml";
			File tempXml = new File(path);

			try {
				if (tempXml.exists()) {
					tempXml.delete();
				}
				tempXml.createNewFile();

				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(temp)));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tempXml)));

				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line + "\n");
				}

				xmlTempFiles.put(f, tempXml);
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
		for (File f : this.tempFiles.keySet()) {
			File temp = tempFiles.get(f);

			temp.delete();
		}
		tempFiles.clear();

		return true;
	}
}
