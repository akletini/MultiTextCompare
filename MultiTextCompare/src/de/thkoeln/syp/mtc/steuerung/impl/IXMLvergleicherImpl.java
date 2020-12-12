package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.input.sax.XMLReaders;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IXMLParseErrorImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;

public class IXMLvergleicherImpl implements IXMLvergleicher {
	private File xsdFile;
	private File ref, vgl;
	
	private Document doc;
	
	private SAXBuilder builder = null;
	
	private List<IXMLParseError> errorListe;
	
	private ITextvergleicher itv = new ITextvergleicherImpl();
	
	private IXMLParseError parseError = null;
	
	
	public void addErrorToErrorList(IXMLParseError error){
		this.errorListe.add(error);
	}
	
	public List<IXMLParseError> getErrorList(){
		return this.errorListe;
	}

	
	public List<IXMLParseError> parseFile(File file, int mode){
		errorListe = new ArrayList();
		
		//int mode = 3; // 0: None | 1: internal XSD | 2: external XSD | 3: DTD
		switch(mode){
		case 0:
			 builder = new SAXBuilder();
			 try{
				 doc = builder.build(file);
			 } catch (JDOMParseException jde){			

				 parseError = new IXMLParseErrorImpl(file, jde.getMessage(), jde.getColumnNumber(), jde.getLineNumber());
				 errorListe.add(parseError);
				 
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");	 
			 } catch(IOException e) {
				 e.printStackTrace();
			 }catch(JDOMException e) {
				 e.printStackTrace();
			 } finally {
				 parseError = null;
			 }
			 break;
		case 1:
			// internal XSD reference
			 try{
				builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
				doc = builder.build(file);
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");	 
			 } catch(IOException e) {
				 e.printStackTrace();
			 }catch(JDOMException e) {
				 e.printStackTrace();
			 } finally {
				 parseError = null;
			 }
			 break;
		case 2:
			// external XSD reference
			 try{
				XMLReaderJDOMFactory factory = new XMLReaderXSDFactory(xsdFile);
				builder = new SAXBuilder(factory);
				doc = builder.build(file);
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");	 
			 } catch(IOException e) {
				 e.printStackTrace();
			 }catch(JDOMException e) {
				 e.printStackTrace();
			 } finally {
				 parseError = null;
			 }
			 break;
		case 3:
			// DTD
			 try{
				 builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
				 doc = builder.build(file);
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");	 
			 } catch(IOException e) {
				 e.printStackTrace();
			 }catch(JDOMException e) {
				 e.printStackTrace();
			 } finally {
				 parseError = null;
			 }
			 break;
		}
		return errorListe;
	
	}
	
}
	


