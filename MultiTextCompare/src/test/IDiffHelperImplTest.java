package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;

public class IDiffHelperImplTest {
	static IDiffHelper iDiff;
	static File[] twoFiles;
	static File[] threeFiles;

	@BeforeClass
	public static void beforeClass() {
		iDiff = new IDiffHelperImpl();
		File file1 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IDiffHelperTestFiles"
				+ File.separator + "DiffHelperA.txt");
		File file2 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IDiffHelperTestFiles"
				+ File.separator + "DiffHelperB.txt");
		File file3 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IDiffHelperTestFiles"
				+ File.separator + "DiffHelperC.txt");
		twoFiles = new File[] { file1, file2 };
		threeFiles = new File[] { file1, file2, file3 };
	}

	@Test
	public void test_diffListNotEmptyTwoFiles() throws IOException {
		iDiff.computeDisplayDiff(twoFiles);
		assertNotNull(iDiff.getLeftLines());
		assertNotNull(iDiff.getRightLines());
	}

	@Test
	public void test_diffListNotEmptyThreeFiles() throws IOException {
		iDiff.computeDisplayDiff(threeFiles);
		assertNotNull(iDiff.getLeftLines());
		assertNotNull(iDiff.getMiddleLines());
		assertNotNull(iDiff.getRightLines());
	}
	
	/**
	 * Prueft in der ersten Zeile ob unit1..3 als "anders in allen Dateien" gewertet wird
	 * @throws IOException
	 */
	@Test
	public void test_detectChangeInAllFiles() throws IOException {
		iDiff.computeDisplayDiff(threeFiles);
		List<IDiffLine> links = iDiff.getLeftLines();
		List<IDiffLine> mitte = iDiff.getMiddleLines();
		List<IDiffLine> rechts = iDiff.getRightLines();
		assertEquals(links.get(0).getDiffedLine().get(40).getCharColor(), "RED");
		assertEquals(mitte.get(0).getDiffedLine().get(39).getCharColor(), "RED");
		assertEquals(rechts.get(0).getDiffedLine().get(40).getCharColor(),
				"RED");
	}
	
	/**
	 * Prueft ob unveraenderte Zeichen korrekt erkannt werden
	 * @throws IOException
	 */
	@Test
	public void test_detectUnchangedChar() throws IOException {
		iDiff.computeDisplayDiff(threeFiles);
		List<IDiffLine> links = iDiff.getLeftLines();
		List<IDiffLine> mitte = iDiff.getMiddleLines();
		List<IDiffLine> rechts = iDiff.getRightLines();
		assertEquals(links.get(0).getDiffedLine().get(39).getCharColor(),
				"WHITE");
		assertEquals(mitte.get(0).getDiffedLine().get(38).getCharColor(),
				"WHITE");
		assertEquals(rechts.get(0).getDiffedLine().get(39).getCharColor(),
				"WHITE");
	}

	/**
	 * Prueft ob in der zweiten Zeile die Änderung von "value" (links) auf "values"
	 * (mitte, rechts) erkannt wird
	 * 
	 * @throws IOException
	 */
	@Test
	public void test_detectChangeInLeftFile() throws IOException {
		iDiff.computeDisplayDiff(threeFiles);
		List<IDiffLine> mitte = iDiff.getMiddleLines();
		List<IDiffLine> rechts = iDiff.getRightLines();
		assertEquals(mitte.get(1).getDiffedLine().get(25).getCharColor(),
				"CYAN");
		assertEquals(rechts.get(1).getDiffedLine().get(24).getCharColor(),
				"CYAN");
	}
	
	/**
	 * Prueft ob in der ersten Zeile die Änderung von "value"(mitte) auf "values"
	 * (links, rechts) erkannt wird
	 * 
	 * @throws IOException
	 */
	@Test
	public void test_detectChangeInMiddleFile() throws IOException {
		iDiff.computeDisplayDiff(threeFiles);
		List<IDiffLine> links = iDiff.getLeftLines();
		List<IDiffLine> rechts = iDiff.getRightLines();
		assertEquals(links.get(0).getDiffedLine().get(25).getCharColor(),
				"ORANGE");
		assertEquals(rechts.get(0).getDiffedLine().get(25).getCharColor(),
				"ORANGE");
	}
	
	/**
	 * Prueft ob in der zweiten Zeile die Änderung von "xsd:" (rechts) auf "&lt;xsd:"
	 * (links, mitte) erkannt wird
	 * 
	 * @throws IOException
	 */
	@Test
	public void test_detectChangeInRightFile() throws IOException {
		iDiff.computeDisplayDiff(threeFiles);
		List<IDiffLine> links = iDiff.getLeftLines();
		List<IDiffLine> mitte = iDiff.getMiddleLines();
		assertEquals(links.get(1).getDiffedLine().get(3).getCharColor(),
				"GREEN");
		assertEquals(mitte.get(1).getDiffedLine().get(3).getCharColor(),
				"GREEN");
	}
}
