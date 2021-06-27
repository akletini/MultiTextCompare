package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.steuerung.impl.IJSONComparerImpl;

public class IJSONCompareImplTest {

	static IJSONComparerImpl jsonCompare;
	static File testRef, test2, test3, test4, test5, test6, test7, test8,
			test9;
	static File recTest1, recTest2;

	@BeforeClass
	public static void beforeAllTests() {
		jsonCompare = new IJSONComparerImpl(0);
		testRef = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test1.json");
		test2 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test2.json");
		test3 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test3.json");
		test4 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test4.json");
		test5 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test5.json");
		test6 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test6.json");
		test7 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test7.json");
		test8 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test8.json");
		test9 = new File(System.getProperty("user.dir") + File.separator
				+ "/src/test/testFiles/IJSONCompareTestFiles/test9.json");

	}

	@Test
	public void differentValues() throws IOException {
		jsonCompare.compare(testRef, test2);
		assertEquals(0.25, jsonCompare.getSimiList().get(0), 0.001);
	}
	
	/**
	 * 1/3 * (1/3* 3/4 + 2/3)
	 * @throws IOException
	 */
	@Test
	public void differentValuesInArray() throws IOException {
		jsonCompare.compare(testRef, test3);
		assertEquals(0.305, jsonCompare.getSimiList().get(1), 0.001);
	}
	
	/**
	 * 1/3 * (1/3* 2/3 + 2/3)
	 * @throws IOException
	 */
	@Test
	public void differentArrayEntryInArray() throws IOException {
		jsonCompare.compare(testRef, test4);
		assertEquals(0.296, jsonCompare.getSimiList().get(1), 0.001);
	}
	
	/**
	 * 1/3 * (1/3* 2/3 + 1/3* 3/4 + 1/3)
	 * @throws IOException
	 */
	@Test
	public void differentObjectValueInArray() throws IOException {
		jsonCompare.compare(testRef, test5);
		assertEquals(0.26851, jsonCompare.getSimiList().get(1), 0.001);
	}
	
	/**
	 * 1/3 * (1/3* 3/4 + 2/3)
	 * @throws IOException
	 */
	@Test
	public void differentValueInObject() throws IOException {
		jsonCompare.compare(testRef, test6);
		assertEquals(0.305, jsonCompare.getSimiList().get(2), 0.001);
	}
	
	/**
	 * 1/3 * (1/3* 2/3 + 2/3)
	 * @throws IOException
	 */
	@Test
	public void differentArrayEntryInObject() throws IOException {
		jsonCompare.compare(testRef, test7);
		assertEquals(0.296, jsonCompare.getSimiList().get(2), 0.001);
	}
	
	/**
	 * 1/3 * (2/3 + 1/3 * (1/2 * 3/4 + 1/2))
	 * @throws IOException
	 */
	@Test
	public void differentObjectValueInObject() throws IOException {
		jsonCompare.compare(testRef, test8);
		assertEquals(0.31944, jsonCompare.getSimiList().get(2), 0.001);
	}
	
	/**
	 * 1/3 * (2/3 + 1/3 * (1/2 * 2/3 + 1/2))
	 * @throws IOException
	 */
	@Test
	public void differentObjectArrayEntryInObject() throws IOException {
		jsonCompare.compare(testRef, test9);
		assertEquals(0.3148, jsonCompare.getSimiList().get(2), 0.001);
	}
}
