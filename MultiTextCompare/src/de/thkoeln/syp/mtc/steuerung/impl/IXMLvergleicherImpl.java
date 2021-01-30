package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.thkoeln.syp.mtc.datenhaltung.api.IXMLAttributeComparator;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLElementComparator;
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
	
	private IXMLParseError parseError = null;
	
	
	/**
	 * Methode fuegt festgestellte Parse-Error zur
	 * Liste der festgestellten Fehler hinzu
	 * 
	 * @param error
	 *            Objekt der Klasse IXMLParseError
	 */
	public void addErrorToErrorList(IXMLParseError error){
		errorListe.add(error);
	}
	
	/**
	 * set Methode zum setzen des XSD Files
	 * 
	 * @param xsdFile
	 *           Java.io File welches die XSD zur XML beinhaltet
	 */
	public void setXSDFile(File xsdFile){
		this.xsdFile = xsdFile;
	}
	
	
	/**
	 * get Methode ermoeglicht Zugriff auf die Errorliste
	 * 
	 * @return List<IXMLParseError>
	 *           Liste mit moeglichen Elementen der Klasse IXMLParseError,
	 *           die bei auftretenden Fehlern waehrend des Parsens festegestellt wurden
	 */
	public List<IXMLParseError> getErrorList(){
		return errorListe;
	}

	/**
	 * Parst mittels JDOM gegebenes File auf Validitaet
	 * und Wohlgeformtheit
	 * 
	 * @param mode
	 *            Aus den Einstellungen zu importierender Wert
	 *            0: None | 1: internal XSD | 2: external XSD | 3: DTD
	 *            
	 * @return Liste an moeglichen Fehlern die festgestellt wurden
	 */
	public List<IXMLParseError> parseFile(File file, int mode){
		errorListe = new ArrayList();
		
		//int mode = 3; // 0: None | 1: internal XSD | 2: DTD
		switch(mode){
		case 0:
			 builder = new SAXBuilder();
			 try{
				 
				 doc = builder.build(file);
				 
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");
			 } catch (JDOMParseException jde){			

				 parseError = new IXMLParseErrorImpl(file, jde.getMessage(), jde.getColumnNumber(), jde.getLineNumber());
				 errorListe.add(parseError);
				 
				 builder = new SAXBuilder();	 
			 } catch(IOException ioe) {
				 ioe.printStackTrace();
			 }catch(JDOMException jde) {	 
				 jde.printStackTrace(); 
			 }
			 break;
			 
		case 1:
			
			// internal XSD reference
			 try{
				 
				builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
				doc = builder.build(file);
				
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");	 
			 } catch (JDOMParseException jde){			

				 parseError = new IXMLParseErrorImpl(file, jde.getMessage(), jde.getColumnNumber(), jde.getLineNumber());
				 errorListe.add(parseError);
				 
				 builder = new SAXBuilder();
				 
			 }
			 catch(IOException ioe) {
				 ioe.printStackTrace();
			 }catch(JDOMException jde) {	 
				 jde.printStackTrace(); 
			 }
			 break;
			 
		case 2:
			// DTD
			 try{
				 builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
				 doc = builder.build(file);
			 } catch (FileNotFoundException e) {
				 System.out.println("File < " + file.getName() + " > not found.");	 
			 } catch (JDOMParseException jde){			

				 parseError = new IXMLParseErrorImpl(file, jde.getMessage(), jde.getColumnNumber(), jde.getLineNumber());
				 errorListe.add(parseError);
				 
				 builder = new SAXBuilder();
				 
			 } catch(IOException ioe) {
				 ioe.printStackTrace();
			 }catch(JDOMException jde) {	 
				 jde.printStackTrace(); 
			 }
			 break;
		}
		return errorListe;
	
	}
	
	
	
	
	
	/**
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param doc
	 * 		  repraesentiert ein JDOM Dokument dessen Attribute alphabetisch sortiert werden sollen.
	 * 
	 * @return representiert das urspruengliche Dokument mit alphabeitsch geordneten Attributen.
	 * 		
	 * */
	public Document sortAttributes(Document doc){
		
		Element root = doc.getRootElement();
		
		this.iterateAndSortAttributes(root);
		
		return doc;
		
	}
	
	/**
	 * Hilfsmethode
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde.
	 * Iteratiever Aufruf um durch den DOM-Baum zu navigieren und Attribute alphabetisch zu ordnen.
	 * 
	 * @param current
	 * 		  repraesentiert ein JDOM Element, dessen 
	 * 
	 * */
	private void iterateAndSortAttributes(Element current) {    
		
		Comparator<Attribute> attributeComperator = new IXMLAttributeComparator();
		
	    List children = current.getChildren();
	      
	    for(int i=0; i<children.size(); i++){
	    	
	    	Element e = (Element)children.get(i);
	    	
	    	if(e.hasAttributes() && e.getAttributes().size() > 1){
	    		
	    		e.sortAttributes(attributeComperator);
	    		
	    	}
	    	
	    	iterateAndSortAttributes(e);
	    }
	          
	}
	
	
	/**
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param doc
	 * 		  repraesentiert ein JDOM Dokument dessen Attribute alphabetisch sortiert werden sollen.
	 * 
	 * @return representiert das urspruengliche Dokument mit alphabeitsch geordneten Attributen.
	 * 		
	 * */
	public Document sortElements(Document doc){
		Comparator<Element> elementsComperator = new IXMLElementComparator();
		
		Element root = doc.getRootElement();
		
		root.sortChildren(elementsComperator);
	
		iterateAndSortElements(root);
		
		return doc;		
		
	}
	
	
	/**
	 * Hilfsmethode
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde.
	 * Iteratiever Aufruf um durch den DOM-Baum zu navigieren und Kindknoten alphabetisch ordnet.
	 * 
	 * @param current
	 * 		  repraesentiert ein JDOM Element, dessen Kindelemente alphabetisch geordnet werden sollen.
	 * 
	 * */
	private void iterateAndSortElements(Element current) {    
		
		Comparator<Element> elementsComperator = new IXMLElementComparator();
		
	    List children = current.getChildren();
	      
	    for(int i=0; i<children.size(); i++){
	    	
	    	Element e = (Element)children.get(i);
	    	
	    	e.sortChildren(elementsComperator);
	    	
	    	iterateAndSortElements(e);
	    }
	          
	}

	
	/**
	 * Ersetzt aus gegebenem String, welcher ein vollst‰ndiges XMLDokument darstellt,
	 * alle Attribute innerhalb der einzelnen XML-Tags
	 * 
	 * @param xmlFile
	 *            XML-Dokument als String
	 *            
	 * @return String
	 * 			  XML-Dokument als String ohne Attribute
	 */
	public String deleteAttributes(String xmlFile){
			
			String[] xmlFileArray = xmlFile.split("");
		
			String content = "";
			
			boolean openingTagIndicatorFound = false;
			boolean closingTagIndicatorFound = false;
			
			int openingTagIndicatorAtIndex = -1;
			int closingTagIndicatorAtIndex = -1;
			
			for(int index=0; index<xmlFileArray.length; index++){
				
				if(xmlFileArray[index].equals("<")){
					
					openingTagIndicatorAtIndex = index;
					openingTagIndicatorFound = true;
					closingTagIndicatorFound = false;
					content="";
					
				}
				
				if(openingTagIndicatorFound = true && closingTagIndicatorFound == false){
					
					content = content + xmlFileArray[index];
					
				}
							
				if(xmlFileArray[index].equals(">")){
					
					closingTagIndicatorAtIndex = index;
					closingTagIndicatorFound = true;
									
					if(this.isTag(content) && !this.isClosingTag(content)){
							
						if(content.contains("=")){
							
							boolean firstSpaceSeperator = false;
												
							if(this.isShortNotation(content)){
								
								for(int i = openingTagIndicatorAtIndex; i<closingTagIndicatorAtIndex-2;i++){
									
									if(xmlFileArray[i].equals(" ")){
										firstSpaceSeperator = true;
									}
									
									if(firstSpaceSeperator == true){
										xmlFileArray[i] = "";
									}								
								}
														
							}else{
									
								for(int i = openingTagIndicatorAtIndex;i<xmlFileArray.length;i++ ){
									
									if(xmlFileArray[i].equals(" ")){
										firstSpaceSeperator = true;
									}
									
									if(xmlFileArray[i].equals(">")){
										openingTagIndicatorFound = false;
										closingTagIndicatorFound = false;
										firstSpaceSeperator = false;
										content = "";	
										break;
									}
									
									if(firstSpaceSeperator == true){
										xmlFileArray[i] = "";
									}
									
								}
								
							}
											
						}
						
					} 
						
					openingTagIndicatorFound = false;
					closingTagIndicatorFound = false;
					content = "";	
								
				}
						
			}
			
			String returnString = "";
			for(String x : xmlFileArray){
				returnString = returnString + x;
			}
			return returnString;
			
		}
	
	
	/**
	 * Ersetzt aus gegebenem String, welcher ein vollst‰ndiges XMLDokument darstellt,
	 * alle Kommentare
	 * 
	 * @param xmlFile
	 *            XML-Dokument als String
	 *            
	 * @return XML-Dokument als String ohne Kommentare
	 */
	public String deleteComments(String xmlFile){
			
		String[] xmlFileArray = xmlFile.split("");
		String content = "";
		boolean openingTagFound = false;
		boolean closingTagFound = false;
		int openingTagAtIndex = -1;
		int closingTagAtIndex = -1;
		
		for(int index=0; index<xmlFileArray.length; index++){
			
			if(xmlFileArray[index].equals("<")){
				openingTagAtIndex = index;
				openingTagFound = true;
			}
			
			if(openingTagFound == true && closingTagFound == false){
				
				content = content + xmlFileArray[index];
				
			}
					
			if(xmlFileArray[index].equals(">")){
				closingTagAtIndex = index;
				closingTagFound = true;
				content = content + xmlFileArray[index];
				
				if(this.isComment(content)){
					
					for(int i = openingTagAtIndex; i<=closingTagAtIndex; i++){
						
						xmlFileArray[i] = "";
						
					}
					
					openingTagAtIndex = -1;
					closingTagAtIndex = -1;
					openingTagFound = false;
					closingTagFound = false;
					content = "";
					
				} else {
					
					openingTagAtIndex = -1;
					closingTagAtIndex = -1;
					openingTagFound = false;
					closingTagFound = false;
					content = "";
				}
				
			}
						
		}
		
		String returnString = "";
		for(String x : xmlFileArray){
			returnString = returnString + x;
		}
		
		return returnString;	
	}
	
	/**
	 * Ersetzt aus gegebenem String, welcher ein XML Dokument beinhaltet,
	 * alle Kommentare, Attribute und Werte
	 * 
	 * @param xmlFile
	 *            XML-Dokument als java.io.File
	 *            
	 * @return XML-Datei als String ohne Kommentare, Attribute und Werte
	 */
	public String tagsOnly(String xmlFile){
		
		String manipulated = this.deleteComments(xmlFile);
		
		manipulated = this.deleteAttributes(manipulated);
		
		String[] xmlFileArray = manipulated.split("");
		
		String priorContent = "";
		String content = "";
		
		boolean openingTagIndicatorFound = false;
		boolean closingTagIndicatorFound = false;
		
		int priorOpeningTagIndicatorAtIndex = -1;
		int priorClosingTagIndicatorAtIndex = -1;
		
		int openingTagIndicatorAtIndex = -1;
		int closingTagIndicatorAtIndex = -1;
		
		for(int index=0; index<xmlFileArray.length; index++){
			
			if(xmlFileArray[index].equals("<")){
				content = "";
				priorOpeningTagIndicatorAtIndex = openingTagIndicatorAtIndex;
				openingTagIndicatorAtIndex = index;
				openingTagIndicatorFound = true;
				closingTagIndicatorFound = false;
			}
	
			if(openingTagIndicatorFound = true && closingTagIndicatorFound == false){
				
				content = content + xmlFileArray[index];
				
			}
			
			if(xmlFileArray[index].equals(">")){
				priorClosingTagIndicatorAtIndex = closingTagIndicatorAtIndex;
				closingTagIndicatorAtIndex = index;
				closingTagIndicatorFound = true;
	
	
				if(this.isClosingTag(content) && this.isTag(priorContent) && this.isMatchingEndTag(priorContent, content) && priorClosingTagIndicatorAtIndex != -1){
					
					for(int i = priorClosingTagIndicatorAtIndex+1; i<openingTagIndicatorAtIndex; i++){
						
						xmlFileArray[i] = "";
						
					}
					
					
					openingTagIndicatorFound = false;
					closingTagIndicatorFound = false;
					priorContent = content;
					content = "";
					
				}else{
					
					
					openingTagIndicatorFound = false;
					closingTagIndicatorFound = false;
					priorContent = content;
					content = "";
				}				
				
			}		
			
		}
		
		String returnString = "";
		for(String x : xmlFileArray){
			returnString = returnString + x;
		}
		
		return returnString;
	}


	/**
	 * Hilfsmethode um festzustellen, ob der uebergebene String formal
	 * ein Element-Tag innerhalb des XML-Dokuments wiederspiegelt
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param xmlTag
	 * 		   zu pruefender String
	 *            
	 * @return boolean true/false
	 */
	public boolean isTag(String xmlTag){
		if(xmlTag.startsWith("<") && xmlTag.endsWith(">") && !xmlTag.contains("<!") && !xmlTag.contains("<?") && !xmlTag.contains("<!--") ){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 *  Hilfsmethode um festzustellen, ob der uebergebene String formal
	 *  ein Kommentar innerhalb des XML-Dokuments wiederspiegelt
	 *  Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param xmlComment
	 * 		   zu pruefender String
	 *            
	 * @return boolean true/false
	 */
	public boolean isComment(String xmlComment){
		if(xmlComment.contains("<!--") && xmlComment.contains("-->")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Hilfsmethode um festzustellen, ob der uebergebene String formal
	 * ein schliessendes Element-Tag innerhalb des XML-Dokuments wiederspiegelt.
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param xmlTag
	 * 		   zu pruefender String
	 *            
	 * @return boolean true/false
	 */
	public boolean isClosingTag(String xmlTag) {
		if(xmlTag.startsWith("</") && xmlTag.endsWith(">") && !xmlTag.contains("<!") && !xmlTag.contains("<?") && !xmlTag.contains("<!--") ){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Hilfsmethode um festzustellen, ob der uebergebene String formal
	 * ein Element-Tag in der "Short Notation" innerhalb des XML-Dokuments wiederspiegelt.
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param xmlTag
	 * 		   zu pruefender String
	 *            
	 * @return boolean true/false
	 */
	public boolean isShortNotation(String xmlTag){
		if(xmlTag.startsWith("<") && xmlTag.endsWith("/>") && xmlTag.contains(" ")){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Hilfsmethode um festzustellen, ob der uebergebene String formal
	 * ein Element-Tag und das dazugehoerige schlieﬂende Tag wiederspiegelt.
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param  startTag
	 * 		   repraesentiert das oeffnende Tag
	 *            
	 *	@param endTag
	 * 		   repraesentiert das schliessende Tag       
	 *  
	 * @return boolean true/false
	 */
	public boolean isMatchingEndTag(String startTag, String endTag){
		String[] startTagArray = startTag.split("");
		
		for(int i=0;i<startTagArray.length;i++){
			if(startTagArray[i].equals("<")){
				startTagArray[i] = "</";
			}
		}
		
		String tempString = "";
		for(String s : startTagArray){
			tempString = tempString + s;
		}
		
		
		if(endTag.contains(tempString)){
			return true;
		}
		
		
		return false;
	}
	
	/**
	 * Hilfsmethode 
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param  file
	 * 		   repraesentiert eine XML-Datei die in einen String umgeformt werden soll.
	 *  
	 * @return content
	 * 		   repraesentiert uebergebene XML-Datei als String.
	 */
	public String xmlFileToString(File file){
		String content = "";
		try{
			content = new String(FileUtils.readFileToByteArray(file));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return content;
	}


}
	


