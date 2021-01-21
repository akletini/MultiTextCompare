package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		fileImporter.importTextdateien(textdateien);

		assertEquals(textdateien, fileImporter.getTextdateien());
	}

	@Test
	public void test_loescheTextdateien() {
		fileImporter.loescheImports();

		assertEquals(Collections.EMPTY_LIST, fileImporter.getTextdateien());
	}

	@Test
	public void test_createTempFiles() {
		fileImporter.importTextdateien(textdateien);
		fileImporter.createTempFiles();

		assertNotNull(fileImporter.getTempFilesMap());
	}

	@Test
	public void test_loescheTempFiles() {
		File tempFiles = new File(System.getProperty("user.dir")
				+ File.separator + "TempFiles");
		fileImporter.deleteTempFiles();

		assertEquals(0, tempFiles.listFiles().length);
		assertEquals(Collections.EMPTY_MAP, fileImporter.getTempFilesMap());
	}
}
