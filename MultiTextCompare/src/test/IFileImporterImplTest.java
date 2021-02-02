package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IFileImporterImplTest {
	static IFileImporter fileImporter;
	static List<File> textdateien;

	@BeforeClass
	public static void beforeAllTests() {
		fileImporter = new IFileImporterImpl();
		textdateien = new ArrayList<File>();

		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator + "FileA.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator + "FileB.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator + "FileC.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator + "FileD.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator + "FileE.txt"));
	}

	@Before
	public void before() {
		fileImporter.importTextdateien(textdateien);
	}

	@After
	public void After() {
		fileImporter.deleteImports();
	}

	@Test
	public void test_defaultConfig() {
		assertNotNull(fileImporter.getConfig());
	}

	@Test
	public void test_configPath() {
		IConfig config = fileImporter.getConfig();
		File fileConfig = new File(config.getPath());
		assertEquals(IFileImporter.DEFAULT_CONFIG.getAbsolutePath(),
				config.getPath());
		assertTrue(fileConfig.exists());

		assertTrue(fileImporter.setConfigPath(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator
				+ "config.properties"));

		assertEquals(System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "test" + File.separator + "testFiles"
				+ File.separator + "config.properties", config.getPath());

		assertFalse(fileConfig.exists());
		assertTrue(new File(config.getPath()).exists());
	}

	@Test
	public void test_configParameter() {
		IConfig config = fileImporter.getConfig();

		assertTrue(config.getBeachteLeerzeichen());
		assertTrue(config.getBeachteSatzzeichen());
		assertTrue(config.getBeachteGrossschreibung());
		assertTrue(config.getBeachteLeerzeilen());
		assertEquals(System.getProperty("user.dir"), config.getRootDir());
		assertTrue(config.getLineMatch());
		assertEquals("", config.getDateiname());

		assertTrue(config.getSortiereElemente());
		assertTrue(config.getSortiereAttribute());
		assertFalse(config.getLoescheAttribute());
		assertFalse(config.getLoescheKommentare());
		assertFalse(config.getNurTags());
		assertEquals(0, config.getValidation());

		config.setBeachteLeerzeichen(false);
		config.setBeachteSatzzeichen(false);
		config.setBeachteGrossschreibung(false);
		config.setBeachteLeerzeilen(false);
		config.setRootDir(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles");
		config.setLineMatch(false);
		config.setDateiname("testName.txt");

		config.setSortiereElemente(false);
		config.setSortiereAttribute(false);
		config.setLoescheAttribute(true);
		config.setLoescheKommentare(true);
		config.setNurTags(true);
		config.setValidation(1);

		assertTrue(fileImporter.exportConfigdatei());
		assertTrue(fileImporter.importConfigdatei(new File(config.getPath())));

		config = fileImporter.getConfig();

		assertFalse(config.getBeachteLeerzeichen());
		assertFalse(config.getBeachteSatzzeichen());
		assertFalse(config.getBeachteGrossschreibung());
		assertFalse(config.getBeachteLeerzeilen());
		assertEquals(System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "test" + File.separator + "testFiles",
				config.getRootDir());
		assertFalse(config.getLineMatch());
		assertEquals("testName.txt", config.getDateiname());

		assertFalse(config.getSortiereElemente());
		assertFalse(config.getSortiereAttribute());
		assertTrue(config.getLoescheAttribute());
		assertTrue(config.getLoescheKommentare());
		assertTrue(config.getNurTags());
		assertEquals(1, config.getValidation());
	}

	@Test
	public void test_importTextdateien() {
		assertEquals(textdateien, fileImporter.getTextdateien());
	}

	@Test
	public void test_deleteImports() {
		assertTrue(fileImporter.importTextdateien(textdateien));
		fileImporter.deleteImports();

		assertEquals(Collections.EMPTY_LIST, fileImporter.getTextdateien());
	}

	@Test
	public void test_deleteImport() {
		File fileA = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "FileA.txt");

		assertTrue(fileImporter.importTextdateien(textdateien));
		assertTrue(fileImporter.getTextdateien().contains(fileA));

		assertTrue(fileImporter.deleteImport(fileA));
		assertFalse(fileImporter.getTextdateien().contains(fileA));
	}

	@Test
	public void test_createTempFiles() {
		assertTrue(fileImporter.createTempFiles());

		assertEquals(fileImporter.getTextdateien().size(), fileImporter
				.getTempFilesMap().size());
		for (File f : fileImporter.getTempFilesMap().keySet())
			assertNotNull(fileImporter.getTempFilesMap().get(f));
	}

	@Test
	public void test_normTempFiles() throws IOException {
		assertTrue(fileImporter.createTempFiles());
		assertTrue(fileImporter.normTempFiles());

		for (File f : fileImporter.getTempFilesMap().keySet()) {
			File temp = fileImporter.getTempFilesMap().get(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(temp)));

			String line;
			while ((line = reader.readLine()) != null) {
				if (!fileImporter.getConfig().getBeachteLeerzeilen())
					assertFalse(line.isEmpty());
				if (!fileImporter.getConfig().getBeachteGrossschreibung())
					for (char c : line.toCharArray())
						assertFalse(Character.isUpperCase(c));
				if (!fileImporter.getConfig().getBeachteSatzzeichen())
					assertFalse(line.contains("\\p{Punct}"));
				if (!fileImporter.getConfig().getBeachteLeerzeichen())
					assertFalse(line.contains(" "));
			}

			reader.close();
		}
	}

	@Test
	public void test_createXmlTempFiles() {
		assertTrue(fileImporter.createTempFiles());
		assertTrue(fileImporter.createXmlTempFiles(fileImporter
				.getTempFilesMap()));

		assertEquals(fileImporter.getTempFilesMap().size(), fileImporter
				.getXmlTempFilesMap().size());
		for (File f : fileImporter.getXmlTempFilesMap().keySet())
			assertNotNull(fileImporter.getXmlTempFilesMap().get(f));
	}

	@Test
	public void test_deleteTempFiles() {
		assertTrue(fileImporter.createTempFiles());
		assertNotEquals(Collections.EMPTY_MAP, fileImporter.getTempFilesMap());

		List<File> tempFiles = new ArrayList<>();
		for (File f : fileImporter.getTempFilesMap().keySet())
			tempFiles.add(fileImporter.getTempFilesMap().get(f));

		for (File f : tempFiles)
			assertTrue(f.exists());

		assertTrue(fileImporter.deleteTempFiles());
		assertEquals(Collections.EMPTY_MAP, fileImporter.getTempFilesMap());

		for (File f : tempFiles)
			assertFalse(f.exists());
	}

	@Test
	public void test_importRoot() throws InterruptedException {
		fileImporter.deleteImports();
		assertTrue(fileImporter.importTextRoot("File?.txt"));
		fileImporter.getRootImporter().start();
		fileImporter.getRootImporter().join();

		assertTrue(fileImporter.getTextdateien().containsAll(textdateien));
	}
}
