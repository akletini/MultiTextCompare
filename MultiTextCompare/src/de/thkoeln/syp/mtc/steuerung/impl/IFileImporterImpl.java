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

import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IFileImporterImpl implements IFileImporter {

	private IConfigImpl iConfigImpl;
	private List<File> textdateien;
	private Map<File, File> tempFiles;
	private Properties prop;

	/**
	 * Klassen Konstruktor initialisiert die Klassen-Attribute und lädt die
	 * Default-Config fuer den Textvergleich
	 */
	public IFileImporterImpl() {
		textdateien = new ArrayList<>();
		tempFiles = new HashMap<>();
		prop = new Properties();

		prop.setProperty(PROP_LEERZEICHEN, "false");
		prop.setProperty(PROP_SATZZEICHEN, "false");
		prop.setProperty(PROP_GROSSSCHREIBUNG, "false");
		prop.setProperty(PROP_ROOT, System.getProperty("user.dir"));
		importConfigdatei(DEFAULT_CONFIG);
	}

	@Override
	public IConfigImpl getConfig() {
		return iConfigImpl;
	}

	@Override
	public List<File> getTextdateien() {
		return textdateien;
	}

	@Override
	public Map<File, File> getTempFilesMap() {
		return tempFiles;
	}

	/**
	 * Versucht die uebergebene Config zu importieren, wenn nicht moeglich dann
	 * wird die Default-Config geladen
	 * 
	 * @param config
	 *            die Config, welche importiert und uebernommen werden soll
	 * 
	 * @return true: bei erfolgreichem Laden der uebergebenen oder
	 *         Default-Config
	 * 
	 *         false: bei Fehlschlag des Imports
	 */
	@Override
	public boolean importConfigdatei(File config) {
		iConfigImpl = new IConfigImpl();
		OutputStream outputStream;
		InputStream inputStream;

		if (!config.exists()) {
			if (!DEFAULT_CONFIG.exists()) {
				try {
					outputStream = new FileOutputStream(
							System.getProperty("user.dir") + File.separator
									+ "config.properties");

					prop.store(outputStream, null);

					iConfigImpl.setPath(System.getProperty("user.dir")
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

					iConfigImpl.setPath(DEFAULT_CONFIG.getAbsolutePath());

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

				iConfigImpl.setPath(config.getAbsolutePath());

				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		iConfigImpl.setBeachteLeerzeichen(Boolean.parseBoolean(prop
				.getProperty(PROP_LEERZEICHEN)));
		iConfigImpl.setBeachteSatzzeichen(Boolean.parseBoolean(prop
				.getProperty(PROP_SATZZEICHEN)));
		iConfigImpl.setBeachteGrossschreibung(Boolean.parseBoolean(prop
				.getProperty(PROP_GROSSSCHREIBUNG)));
		iConfigImpl.setRootDir(prop.getProperty(PROP_ROOT));

		return true;
	}

	/**
	 * Aktuelle Config kann abgespeichert werden um die abgespeicherten Werte
	 * spaeter wieder importieren zu koennen
	 * 
	 * @param iConfigImpl
	 *            die Config, welche abgespeichert werden soll
	 * 
	 * @return true: bei erfolgreichem Export
	 * 
	 *         false: bei Fehlschlag des Exports
	 */
	@Override
	public boolean exportConfigdatei(IConfigImpl iConfigImpl) {
		Properties prop = new Properties();
		OutputStream outputStream;

		try {
			outputStream = new FileOutputStream(iConfigImpl.getPath());

			prop.setProperty(PROP_LEERZEICHEN,
					Boolean.toString(iConfigImpl.getBeachteLeerzeichen()));
			prop.setProperty(PROP_SATZZEICHEN,
					Boolean.toString(iConfigImpl.getBeachteSatzzeichen()));
			prop.setProperty(PROP_GROSSSCHREIBUNG,
					Boolean.toString(iConfigImpl.getBeachteGrossschreibung()));
			prop.setProperty(PROP_ROOT, iConfigImpl.getRootDir());

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
	 *            Pfade der zu importierenden Textdateien werden als Liste von
	 *            Strings uebergeben
	 * 
	 * @return true: bei erfolgreichem Import
	 * 
	 *         false: falls keine Textdatei importiert wurde
	 */
	@Override
	public boolean importTextdateien(List<String> textdateien) {
		for (String str : textdateien) {
			File file = new File(str);

			if (file.exists())
				this.textdateien.add(file);
		}

		if (this.textdateien.isEmpty())
			return false;

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
		File rootDir = new File(iConfigImpl.getRootDir());

		if (!rootDir.isDirectory())
			return false;

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
		for (File f : file.listFiles()) {
			if (f.isDirectory())
				searchInDir(f, fileName);
			else if (f.getName().equals(fileName))
				this.textdateien.add(f);
		}
	}

	/**
	 * Alles importierten Textdateien werden aus der lokalen Variablen geloescht
	 */
	@Override
	public void loescheImports() {
		textdateien.clear();
	}

	/**
	 * Erstellt aus den bereits importierten Textdateien temporaere Dateien im
	 * Ordner 'TempFiles', welche fuer den anschliessenden Textvergleich
	 * formatiert werden. Die temporaeren Dateien werden in einer HashMap
	 * gespeichert um von diesen auch wieder auf die originalen Dateien
	 * schliessen zu koennen
	 * 
	 * @return true: bei erfolgreichem Erstellen der temporaeren Dateien
	 * 
	 *         false: falls beim Lesen/Erstellen der temporaeren Dateien ein
	 *         Fehler auftritt
	 */
	@Override
	public boolean createTempFiles() {
		BufferedReader reader;
		BufferedWriter writer;

		new File(System.getProperty("user.dir") + File.separator + "TempFiles")
				.mkdirs();

		for (File f : this.textdateien) {
			File temp = new File(System.getProperty("user.dir")
					+ File.separator + "TempFiles" + File.separator
					+ f.hashCode());

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
				while ((line = reader.readLine()) != null)
					writer.write(line.replaceAll("\\s", "\n") + "\n");

				tempFiles.put(f, temp);

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
	 * Loescht die zuvor erstellten temporaeren Dateien samt Ordner aus dem
	 * Dateisystem
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
			tempFiles.delete();

			return true;
		}

		return false;
	}
}
