package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.steuerung.impl.IXMLComparerImpl;

public class IXMLComparerImplTest {
	
	static IXMLComparerImpl xmlCompare;
	static File attrTest1, attrTest2;
	static File recTest1, recTest2;
	
	@BeforeClass
	public static void beforeAllTests(){
		xmlCompare = new IXMLComparerImpl(0);
		attrTest1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes1.xml");
		attrTest2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes2.xml");
		recTest1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestRecursionList1.xml");
		recTest2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestRecursionList2.xml");
	}
	
	/*
	 * Attribute tests
	 */
	@Test
	public void test_equalElementValuesBothHaveAttributes() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals( 1.0 ,similarities.get(0).doubleValue() * 8, 0.0001);
	}
	
	@Test
	public void test_differentElementValuesBothHaveAttributes() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.75 * 1/8 ,similarities.get(1).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_equalElementValuesOneWithoutAttributesIsSymmetrical() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 * 1/8 ,similarities.get(2).doubleValue(), 0.0001);
		assertEquals(0.5 * 1/8 ,similarities.get(3).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_differentElementValuesOneWithoutAttributesIsSymmetrical() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.25 * 1/8 ,similarities.get(4).doubleValue(), 0.0001);
		assertEquals(0.25 * 1/8 ,similarities.get(5).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_equalElementValuesWithoutAttributes() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(1.0 * 1/8 ,similarities.get(6).doubleValue(), 0.0001);
	}
	@Test
	public void test_differentElementValuesWithoutAttributes() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 * 1/8 ,similarities.get(7).doubleValue(), 0.0001);
	}
	
	/*
	 * Element tests
	 */
	@Test
	public void test_recursiveElementListEqual() throws IOException, JDOMException{
		xmlCompare.compare(recTest1, recTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(1.0 * 1/3 ,similarities.get(0).doubleValue(), 0.0001);
	}
	@Test
	public void test_recursiveElementListDifferent() throws IOException, JDOMException{
		xmlCompare.compare(recTest1, recTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.75 * 1/3 ,similarities.get(1).doubleValue(), 0.0001);
	}
	@Test
	public void test_recursiveElementListDifferentDeep() throws IOException, JDOMException{
		xmlCompare.compare(recTest1, recTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		//left branch + right branch
		assertEquals(((1.0/3.0 * 1.0/2.0) + (1.0/3.0 *  1.0/2.0 * 1.0/2.0)) ,similarities.get(2).doubleValue(), 0.0001);
	}

}
