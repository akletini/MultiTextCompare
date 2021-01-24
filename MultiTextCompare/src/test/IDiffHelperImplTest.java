package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

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
				+ "testFiles" + File.separator + "DiffHelperA.txt");
		File file2 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "DiffHelperB.txt");
		File file3 = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "DiffHelperC.txt");
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
}
