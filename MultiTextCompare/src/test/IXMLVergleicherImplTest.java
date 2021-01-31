package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;
import de.thkoeln.syp.mtc.steuerung.impl.IXMLvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;


public class IXMLVergleicherImplTest {
	
	static File testdatei;
	static File erwarteteErgebnisse;
	static File xsdTestDatei;
	
	static Document testdateiDoc;
	static Document erwartetesErgebnissDoc;
	
	static IXMLvergleicher iXML;
	
	static SAXBuilder builder;
	static Document doc;
	
	static XMLOutputter xout;
	
	@BeforeClass
	public static void beforeAllTests() {
		builder = new SAXBuilder();
		
		xsdTestDatei = null;
		testdatei = null;
		
		testdateiDoc = new Document();
		erwartetesErgebnissDoc = new Document();
		
		iXML = new IXMLvergleicherImpl();
		xout = new XMLOutputter(Format.getRawFormat());
		
		

	}
	
	@Test
	public void test_sortAttributes() {
		
		try{
			testdateiDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortAttributes.xml"));		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		try{
			erwartetesErgebnissDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/ExpectedOutcome/ExpectedOutcomeSortAttributes.xml"));			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}

		String ist = xout.outputString(iXML.sortAttributes(testdateiDoc));;
		String soll = xout.outputString(erwartetesErgebnissDoc);
		
		assertEquals(ist, soll);
	
	}
	
	
	@Test
	public void test_sortElements() {
		
		try{
			testdateiDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortElements.xml"));		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		try{
			erwartetesErgebnissDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/ExpectedOutcome/ExpectedOutcomeSortElements.xml"));			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Kontrolldateien beschaedigt!");
			e.printStackTrace();
		}

		String ist = xout.outputString(iXML.sortElements(testdateiDoc));;
		String soll = xout.outputString(erwartetesErgebnissDoc);
		
		assertEquals(ist, soll);
	
	}
	
	
	@Test
	public void test_deleteComments() {
		
		try{
			testdateiDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortAttributes.xml"));		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		try{
			erwartetesErgebnissDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/ExpectedOutcome/ExpectedOutcomeDeleteComments.xml"));			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Kontrolldateien beschaedigt!");
			e.printStackTrace();
		}

		
		
		String ist = iXML.deleteComments(xout.outputString(testdateiDoc));
		String soll = xout.outputString(erwartetesErgebnissDoc);
		
		assertEquals(ist, soll);
	
	}
	
	
	@Test
	public void test_deleteAttributes() {
		
		try{
			testdateiDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortAttributes.xml"));		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		try{
			erwartetesErgebnissDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/ExpectedOutcome/ExpectedOutcomeDeleteAttributes.xml"));			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Kontrolldateien beschaedigt!");
			e.printStackTrace();
		}

		
		
		String ist = iXML.deleteAttributes(xout.outputString(testdateiDoc));
		String soll = xout.outputString(erwartetesErgebnissDoc);
		
		assertEquals(ist, soll);
	
	}
	
	@Test
	public void test_tagsOnly() {
		File erwartetesErgebniss = null;
		try{
			testdateiDoc = builder.build(new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortAttributes.xml"));
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		try{
			erwartetesErgebniss = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/ExpectedOutcome/ExpectedOutcomeTagsOnly.xml");

		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Kontrolldateien beschaedigt!");
			e.printStackTrace();
		}

		
		
		String ist = iXML.tagsOnly(xout.outputString(testdateiDoc));
		System.out.println(ist);
		String soll = iXML.xmlFileToString(erwartetesErgebniss);
		System.out.println(soll);
		assertEquals(ist, soll);
	
	}
	
	
	
	@Test
	public void test_parseFile_NoValidation_Successful() {
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortAttributes.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		List<IXMLParseError> errorListe = iXML.parseFile(testdatei, 0);
		
		assertNotNull(errorListe);
		assertEquals(errorListe.size(), 0);	
	}
	
	@Test
	public void test_parseFile_NoValidation_unsuccessful() {
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileFaultyXML.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		List<IXMLParseError> errorListe = iXML.parseFile(testdatei, 0);
		
		assertNotNull(errorListe);
		assertEquals(errorListe.size(), 1);
		
	}
	
	/*
	 * Für diesen Testfall muss die Referenz im XML File an das Benutzer Verzeichniss angepasst werden
	 * **/
	@Test
	public void test_parseFile_Validation_XSDinternal_successful() {
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileInternalXSD.xml");

		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		List<IXMLParseError> errorListe = iXML.parseFile(testdatei, 1);
		
		assertNotNull(errorListe);
		assertEquals(errorListe.size(), 0);	
	}
	
	/*
	 * Für diesen Testfall muss die Referenz im XML File an das Benutzer Verzeichniss angepasst werden
	 * **/
	@Test
	public void test_parseFile_Validation_XSDinternal_unsuccessful() {
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileFaultyXML.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		List<IXMLParseError> errorListe = iXML.parseFile(testdatei, 1);
		
		assertNotNull(errorListe);
		assertEquals(errorListe.size(), 1);
		
	}	
	
	
	
	@Test
	public void test_parseFile_Validation_dtd_successful() {
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileDTD.xml");
		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		iXML.setXSDFile(xsdTestDatei);
		List<IXMLParseError> errorListe = iXML.parseFile(testdatei, 2);
		
		assertNotNull(errorListe);
		assertEquals(errorListe.size(), 0);	
	}
	
	@Test
	public void test_parseFile_Validation_dtd_unsuccessful() {
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileDTDFaulty.xml");
						
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		iXML.setXSDFile(xsdTestDatei);
		List<IXMLParseError> errorListe = iXML.parseFile(testdatei, 2);
		
		assertNotNull(errorListe);
		assertTrue(errorListe.size() >= 1);
		
	}
	
	
	
	
	
}
