package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLElementComparator;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IXMLvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;


public class IXMLVergleicherImplTest {
	
	static File testdatei;
	static File erwarteteErgebnisse;
	static File testMap1;
	static File testMap2;
	static File testMap3;
	static File testMap4;
	
	static Document testdateiDoc;
	static Document erwartetesErgebnissDoc;
	
	static IXMLvergleicher iXML;
	static IConfig config;
	static IFileImporter fileImporter;
	
	static SAXBuilder builder;
	static Document doc;
	
	static XMLOutputter xout;
	static Map<File, File> tempFiles;
	
	@BeforeClass
	public static void beforeAllTests() {
		builder = new SAXBuilder();
		
		testdatei = null;
		
		testdateiDoc = new Document();
		erwartetesErgebnissDoc = new Document();
		
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setXmlDeleteAttributes(false);
		fileImporter.getConfig().setXmlDeleteComments(false);
		fileImporter.getConfig().setXmlOnlyTags(false);
		fileImporter.getConfig().setXmlSortElements(true);
		fileImporter.getConfig().setXmlSortAttributes(true);
		fileImporter.getConfig().setXmlValidation(0);
		
		iXML = new IXMLvergleicherImpl(fileImporter);
		xout = new XMLOutputter(Format.getRawFormat());
		
	}
	
	
	@Test
	public void test_sortAttributes() {
		iXML.clearErrorList();
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
		
		assertEquals(soll, ist);
	
	}
	
	
	@Test
	public void test_sortElements() {
		iXML.clearErrorList();
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

		String ist = xout.outputString(iXML.sortElements(testdateiDoc, new IXMLElementComparator()));
		String soll = xout.outputString(erwartetesErgebnissDoc);
		
		assertEquals(soll, ist);
	
	}
	
	
	@Test
	public void test_deleteComments() {
		iXML.clearErrorList();
		
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
		
		assertEquals(soll, ist);
	
	}
	
	
	@Test
	public void test_deleteAttributes() {
		iXML.clearErrorList();
		
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
		
		assertEquals(soll, ist);
	
	}
	
	@Test
	public void test_tagsOnly() {
		iXML.clearErrorList();
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
		String soll = iXML.xmlFileToString(erwartetesErgebniss);
		assertEquals(soll, ist);
	
	}
	
	
	
	@Test
	public void test_parseFile_NoValidation_Successful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileSortAttributes.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertFalse( iXML.parseFile(testdatei, 0) );
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(0, errorListe.size());	
	}
	
	@Test
	public void test_parseFile_NoValidation_unsuccessful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileFaultyXML.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertTrue(iXML.parseFile(testdatei, 0));
		
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(1, errorListe.size());
		
	}
	
	/*
	 * Für diesen Testfall muss die Referenz im XML File an das Benutzer Verzeichniss angepasst werden
	 * **/
	@Test
	public void test_parseFile_Validation_XSDinternal_successful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileInternalXSD.xml");

		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertFalse(iXML.parseFile(testdatei, 1));
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(0, errorListe.size());	
	}
	
	/*
	 * Für diesen Testfall muss die Referenz im XML File an das Benutzer Verzeichniss angepasst werden
	 * **/
	@Test
	public void test_parseFile_Validation_XSDinternal_unsuccessful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileFaultyXML.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertTrue(iXML.parseFile(testdatei, 1));
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(1, errorListe.size());
		
	}	
	
	
	
	@Test
	public void test_parseFile_Validation_dtd_successful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileDTD.xml");
		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertFalse(iXML.parseFile(testdatei, 2));
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(0, errorListe.size());	
	}
	
	@Test
	public void test_parseFile_Validation_dtd_unsuccessful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileDTDFaulty.xml");
						
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertTrue(iXML.parseFile(testdatei, 2));
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertTrue(errorListe.size() >= 1);
		
	}
	
	@Test
	public void test_parseFile_ExternalXSD_Successful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileExternalXSD.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertFalse( iXML.parseFile(testdatei, 3) );
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(0, errorListe.size());	
	}
	
	@Test
	public void test_parseFile_ExternalXSD_Unsuccessful() {
		iXML.clearErrorList();
		
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileFaultyXML.xml");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		assertTrue(iXML.parseFile(testdatei, 3));
		
		List<IParseError> errorListe = iXML.getErrorList();
		
		assertNotNull(errorListe);
		assertEquals(1, errorListe.size());
	}
	
	@Test
	public void test_xmlPrepare_Correct() {
		iXML.clearErrorList();
		
		FileInputStream instream = null;
		FileOutputStream outstream = null;
		
		try{
			testMap1 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileMap1.xml");
			
			testMap2 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileMap2.xml");
			
			
			/*
			 * In testMap2 soll zu Anfang das gleiche stehen wie in testMap1
			 * und muss zu jedem neustart der Testfälle erneut kopiert werden
			 * */
			BufferedWriter writer;
			byte[] buffer = new byte[1024];
				
			instream = new FileInputStream(testMap1);
			outstream = new FileOutputStream(testMap2);
			int length;
			while ((length = instream.read(buffer)) > 0){
    	    	outstream.write(buffer, 0, length);
    	    }   
    	    instream.close();
    	    outstream.close();
						
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		Map<File,File> testMap = new HashMap<File, File>();
		
		testMap.put(testMap1, testMap2);//correct
		
		Map<File,File> ergebnisMap;
		
		ergebnisMap = iXML.xmlPrepare(testMap);
		
		assertTrue(iXML.getErrorList().size() == 0);
		
		
	}
	
	
	@Test
	public void test_xmlPrepare_Incorrect() {
		iXML.clearErrorList();
		
		try{
			testMap3 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileMap3.xml");
			
			testMap4 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/XMLTestFiles/TestFileMap4.xml");
						
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		Map<File,File> testMap = new HashMap<File, File>();
		
		testMap.put(testMap3, testMap4);//faulty
		
		Map<File,File> ergebnisMap;
		
		ergebnisMap = iXML.xmlPrepare(testMap);
		
		assertTrue(iXML.getErrorList().size() > 0);
		
	}
	
	public static void readFileToConsole(File file) throws IOException{
		Scanner myReader = new Scanner(file);
		 String content = "";
	      while (myReader.hasNextLine()) {
	    	  content = myReader.nextLine();
	    	  System.out.println(content);	
	      }	     	   
	      myReader.close();
	     
	}
	
	
	
}
