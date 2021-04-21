package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatch;
import de.thkoeln.syp.mtc.steuerung.impl.IMatchHelperImpl;

public class IMatchHelperTest {
	static IMatchHelperImpl iMatch;
	static File equalA, equalB, similarA, similarB;
	static File temp_equal_A, temp_equal_B, temp_similar_A, temp_similar_B;

	@BeforeClass
	public static void beforeClass() throws IOException {

		iMatch = new IMatchHelperImpl();
		iMatch.setMATCH_AT(0.6);
		iMatch.setLOOKAHEAD(0);

		equalA = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IMatchHelperTestFiles"
				+ File.separator + "matchEqualA.txt");
		equalB = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IMatchHelperTestFiles"
				+ File.separator + "matchEqualB.txt");
		similarA = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IMatchHelperTestFiles"
				+ File.separator + "matchSimilarA.txt");
		similarB = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "test" + File.separator
				+ "testFiles" + File.separator + "IMatchHelperTestFiles"
				+ File.separator + "matchSimilarB.txt");

		temp_equal_A = File.createTempFile("temp_equal_A", ".txt");
		temp_equal_B = File.createTempFile("temp_equal_B", ".txt");
		temp_similar_A = File.createTempFile("temp_similar_A", ".txt");
		temp_similar_B = File.createTempFile("temp_similar_B", ".txt");

		createTempFiles(equalA, temp_equal_A);
		createTempFiles(equalB, temp_equal_B);
		createTempFiles(similarA, temp_similar_A);
		createTempFiles(similarB, temp_similar_B);

	}

	public static void createTempFiles(File ref, File temp) throws IOException {
		FileInputStream fileIn = new FileInputStream(ref);
		FileOutputStream fileOut = new FileOutputStream(temp);
		FileChannel src = fileIn.getChannel();
		FileChannel dest = fileOut.getChannel();
		dest.transferFrom(src, 0, src.size());
		fileIn.close();
		fileOut.close();
	}

	@AfterClass
	public static void afterClass() throws IOException {
		temp_equal_A.deleteOnExit();
		temp_equal_B.deleteOnExit();
		temp_similar_A.deleteOnExit();
		temp_similar_B.deleteOnExit();
	}
	
	@Test
	public void test_MatchCountEqual() throws IOException {
		iMatch.matchLines(temp_equal_A, temp_equal_B);
		assertEquals(19, iMatch.getMatches().size());
	}
	
	@Test
	public void test_MatchEqualLines() throws IOException {
		iMatch.matchLines(temp_equal_A, temp_equal_B);
		for (IMatch match : iMatch.getMatches()) {
			assertEquals(match.getValueLeft(), match.getValueRight());
		}
	}
	
	@Test
	public void test_MatchCountSimilar() throws IOException {
		iMatch.matchLines(temp_similar_A, temp_similar_B);
		assertEquals(5, iMatch.getMatches().size());
	}

	@Test
	public void test_MatchSimilarLines() throws IOException {
		iMatch.matchLines(temp_similar_A, temp_similar_B);
		List<IMatch> matches = iMatch.getMatches();

		for (int i = 0; i < matches.size(); i++) {
			if (i == 3) {
				assertEquals(matches.get(i).getValueLeft(), matches.get(i)
						.getValueRight());
				continue;
			}
			assertNotEquals(matches.get(i).getValueLeft(), matches.get(i)
					.getValueRight());
		}
	}
}
