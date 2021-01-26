package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
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
		fileImporter.loescheImports();
	}

	@Test
	public void test_defaultConfig() {
		assertNotNull(fileImporter.getConfig());
	}

	@Test
	public void test_configPath() {
		IConfig config = fileImporter.getConfig();

		assertEquals(IFileImporter.DEFAULT_CONFIG.getAbsolutePath(),
				config.getPath());

		fileImporter.setConfigPath(System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "test"
				+ File.separator + "testFiles" + File.separator
				+ "config.properties");

		assertEquals(System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "test" + File.separator + "testFiles"
				+ File.separator + "config.properties", config.getPath());
	}

	@Test
	public void test_configParameter() {
		IConfig config = fileImporter.getConfig();

		assertTrue(config.getBeachteLeerzeichen());
		assertTrue(config.getBeachteSatzzeichen());
		assertTrue(config.getBeachteGrossschreibung());
		assertTrue(config.getBeachteLeerzeilen());
		assertTrue(config.getLineMatch());
		assertEquals("dateiname", config.getDateiname());
		assertEquals(System.getProperty("user.dir"), config.getRootDir());

		config.setBeachteLeerzeichen(false);
		config.setBeachteSatzzeichen(false);
		config.setBeachteGrossschreibung(false);
		config.setBeachteLeerzeilen(false);
		config.setLineMatch(false);
		config.setDateiname("testName.txt");
		config.setRootDir(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles");

		fileImporter.exportConfigdatei();
		fileImporter.importConfigdatei(new File(config.getPath()));
		config = fileImporter.getConfig();

		assertFalse(config.getBeachteLeerzeichen());
		assertFalse(config.getBeachteSatzzeichen());
		assertFalse(config.getBeachteGrossschreibung());
		assertFalse(config.getBeachteLeerzeilen());
		assertFalse(config.getLineMatch());
		assertEquals("testName.txt", config.getDateiname());
		assertEquals(System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + "test" + File.separator + "testFiles",
				config.getRootDir());
	}

	@Test
	public void test_importTextdateien() {
		assertEquals(textdateien, fileImporter.getTextdateien());
	}

	@Test
	public void test_loescheTextdateien() {
		fileImporter.importTextdateien(textdateien);
		fileImporter.loescheImports();

		assertEquals(Collections.EMPTY_LIST, fileImporter.getTextdateien());
	}

	@Test
	public void test_createTempFiles() {
		fileImporter.createTempFiles();

		assertEquals(fileImporter.getTextdateien().size(), fileImporter
				.getTempFilesMap().size());
		for (File f : fileImporter.getTempFilesMap().keySet())
			assertNotNull(fileImporter.getTempFilesMap().get(f));
	}

	@Test
	public void test_normTempFiles() {
		fileImporter.createTempFiles();
		fileImporter.normTempFiles();
	}

	@Test
	public void test_loescheTempFiles() {
		fileImporter.createTempFiles();
		assertNotEquals(Collections.EMPTY_MAP, fileImporter.getTempFilesMap());

		fileImporter.deleteTempFiles();
		assertEquals(Collections.EMPTY_MAP, fileImporter.getTempFilesMap());
	}

	@Test
	public void test_importRoot() throws InterruptedException {
		fileImporter.loescheImports();
		fileImporter.importTextRoot("File?.txt");
		fileImporter.getRootImporter().start();
		fileImporter.getRootImporter().join();

		for (File f : fileImporter.getTextdateien())
			System.out.println(f.getAbsolutePath());
		assertTrue(fileImporter.getTextdateien().containsAll(textdateien));
	}
}
