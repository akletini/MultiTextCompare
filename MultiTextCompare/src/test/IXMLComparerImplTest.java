package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IXMLCompareImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IXMLComparerImplTest {
	
	static IXMLCompareImpl xmlCompare;
	static File attrTest1, attrTest2,attrTest3, attrTest4, attrTest5, attrTest6,attrTest7, attrTest8;
	static File recTest1, recTest2;
	static File comTest1, comTest2, comTest3;
	
	static File attrTestNS1, attrTestNS2,attrTestNS3, attrTestNS4, attrTestNS5, attrTestNS6,attrTestNS7, attrTestNS8, attrTestNS1DiffNS;
	static File recTestNS1, recTestNS2;
	static File comTestNS1, comTestNS2, comTestNS3;
	
	@BeforeClass
	public static void beforeAllTests(){
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setMaxLineLength(0);
		xmlCompare = new IXMLCompareImpl(fileImporter);
		attrTest1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes1.xml");
		attrTest2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes2.xml");
		attrTest3 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes3.xml");
		attrTest4 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes4.xml");
		attrTest5 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes5.xml");
		attrTest6 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes6.xml");
		attrTest7 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes7.xml");
		attrTest8 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributes8.xml");
		
		recTest1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestRecursionList1.xml");
		recTest2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestRecursionList2.xml");
		
		comTest1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestComments1.xml");
		comTest2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestComments2.xml");
		comTest3 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestComments3.xml");
		
		attrTestNS1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS1.xml");
		attrTestNS2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS2.xml");
		attrTestNS3 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS3.xml");
		attrTestNS4 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS4.xml");
		attrTestNS5 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS5.xml");
		attrTestNS6 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS6.xml");
		attrTestNS7 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS7.xml");
		attrTestNS8 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS8.xml");
		attrTestNS1DiffNS = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestAttributesNS1DiffNS.xml");
		
		recTestNS1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestRecursionListNS1.xml");
		recTestNS2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestRecursionListNS2.xml");
		
		comTestNS1 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestCommentsNS1.xml");
		comTestNS2 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestCommentsNS2.xml");
		comTestNS3 = new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/IXMLCompareTestFiles/TestCommentsNS3.xml");
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
		xmlCompare.compare(attrTest7, attrTest8);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.75,similarities.get(0).doubleValue(), 0.0001);
		xmlCompare.compare(attrTest8, attrTest7);
		similarities = xmlCompare.getSimilarities();
		assertEquals(0.75,similarities.get(0).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_equalElementValuesOneWithoutAttributesIsSymmetrical() throws IOException, JDOMException{
		xmlCompare.compare(attrTest3, attrTest4);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 ,similarities.get(0).doubleValue(), 0.0001);
		xmlCompare.compare(attrTest4, attrTest3);
		similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 ,similarities.get(0).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_differentElementValuesOneWithoutAttributesIsSymmetrical() throws IOException, JDOMException{
		xmlCompare.compare(attrTest5, attrTest6);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.25 ,similarities.get(0).doubleValue(), 0.0001);
		xmlCompare.compare(attrTest6, attrTest5);
		similarities = xmlCompare.getSimilarities();
		assertEquals(0.25 ,similarities.get(0).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_equalElementValuesWithoutAttributes() throws IOException, JDOMException{
		xmlCompare.compare(attrTest1, attrTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(1.0 * 1.0/8.0 ,similarities.get(2).doubleValue(), 0.0001);
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
	
	/*
	 * Comment test
	 */
	@Test
	public void test_disableCommentCompare() throws IOException, JDOMException{
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setMaxLineLength(0);
		fileImporter.getConfig().setXmlCompareComments(false);
		IXMLCompareImpl xmlCompare = new IXMLCompareImpl(fileImporter);
		xmlCompare.compare(comTest1, comTest2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5, similarities.get(0).doubleValue(), 0.001);
	}
	
	@Test
	public void test_enableCommentCompare() throws IOException, JDOMException{
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setMaxLineLength(0);
		fileImporter.getConfig().setXmlCompareComments(true);
		IXMLCompareImpl xmlCompare = new IXMLCompareImpl(fileImporter);
		double val = xmlCompare.compare(comTest1, comTest3);
		assertEquals(0.75, val, 0.001);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////Namespace-Tests////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void test_equalElementValuesBothHaveAttributesNS() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS1, attrTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals( 1.0 ,similarities.get(0).doubleValue() * 8, 0.0001);
	}
	
	@Test
	public void test_differentElementValuesBothHaveAttributesNS() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS7, attrTestNS8);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.75,similarities.get(0).doubleValue(), 0.0001);
		xmlCompare.compare(attrTest8, attrTest7);
		similarities = xmlCompare.getSimilarities();
		assertEquals(0.75,similarities.get(0).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_equalElementValuesOneWithoutAttributesIsSymmetricalNS() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS3, attrTestNS4);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 ,similarities.get(0).doubleValue(), 0.0001);
		xmlCompare.compare(attrTest4, attrTest3);
		similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 ,similarities.get(0).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_differentElementValuesOneWithoutAttributesIsSymmetricalNS() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS5, attrTestNS6);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.25 ,similarities.get(0).doubleValue(), 0.0001);
		xmlCompare.compare(attrTest6, attrTest5);
		similarities = xmlCompare.getSimilarities();
		assertEquals(0.25 ,similarities.get(0).doubleValue(), 0.0001);
	}
	
	@Test
	public void test_equalElementValuesWithoutAttributesNS() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS1, attrTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(1.0 * 1.0/8.0 ,similarities.get(2).doubleValue(), 0.0001);
	}
	@Test
	public void test_differentElementValuesWithoutAttributesNS() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS1, attrTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5 * 1/8 ,similarities.get(7).doubleValue(), 0.0001);
	}
	
	/*
	 * Element tests
	 */
	@Test
	public void test_recursiveElementListEqualNS() throws IOException, JDOMException{
		xmlCompare.compare(recTestNS1, recTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(1.0 * 1/3 ,similarities.get(0).doubleValue(), 0.0001);
	}
	@Test
	public void test_recursiveElementListDifferentNS() throws IOException, JDOMException{
		xmlCompare.compare(recTestNS1, recTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.75 * 1/3 ,similarities.get(1).doubleValue(), 0.0001);
	}
	@Test
	public void test_recursiveElementListDifferentDeepNS() throws IOException, JDOMException{
		xmlCompare.compare(recTestNS1, recTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		//left branch + right branch
		assertEquals(((1.0/3.0 * 1.0/2.0) + (1.0/3.0 *  1.0/2.0 * 1.0/2.0)) ,similarities.get(2).doubleValue(), 0.0001);
	}
	
	/*
	 * Comment test
	 */
	@Test
	public void test_disableCommentCompareNS() throws IOException, JDOMException{
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setMaxLineLength(0);
		fileImporter.getConfig().setXmlCompareComments(false);
		IXMLCompareImpl xmlCompare = new IXMLCompareImpl(fileImporter);
		xmlCompare.compare(comTestNS1, comTestNS2);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals(0.5, similarities.get(0).doubleValue(), 0.001);
	}
	
	@Test
	public void test_enableCommentCompareNS() throws IOException, JDOMException{
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setMaxLineLength(0);
		fileImporter.getConfig().setXmlCompareComments(true);
		IXMLCompareImpl xmlCompare = new IXMLCompareImpl(fileImporter);
		double val = xmlCompare.compare(comTestNS1, comTestNS3);
		assertEquals(0.75, val, 0.001);
	}
	
	@Test
	public void test_differentNamespaceNoMatches() throws IOException, JDOMException{
		xmlCompare.compare(attrTestNS1, attrTestNS1DiffNS);
		List<Double> similarities = xmlCompare.getSimilarities();
		assertEquals( 0 ,similarities.size());
	}
	
}
