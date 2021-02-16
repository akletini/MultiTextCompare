package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IJSONParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IJSONParseErrorImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IJSONSortNodeFactoryImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IJSONvergleicher;

public class IJSONvergleicherImpl extends JsonNodeFactory implements IJSONvergleicher {
	
	private List<IJSONParseError> errorListe;
	
	private IJSONParseError parseError;
	
	private IFileImporter iFileImporter;
	
	private IConfig iConfig;

	public IJSONvergleicherImpl(IFileImporter fileImporter){
		this.errorListe = new ArrayList<>();
		this.parseError = null;
		this.iFileImporter = fileImporter;
		this.iConfig = fileImporter.getConfig();
	}
		
	/**
	 * Parst mittels Jackson gegebenes File auf Wohlgeformtheit
	 * 
	 * @param Map<File, File>
	 *            Map mit zu manipulierender JSON-Dateiein
	 *            
	 * @return Map<File, File>
	 * 			  Map welche nach den in der Config getroffenen 
	 * 			  Einstellungen manipuliert wurde
	 */
	@Override
	public Map<File, File> jsonPrepare(Map<File, File> tempFiles) {
		this.clearErrorList();
		
		boolean deleteValues = false; //iConfig.getValue
		boolean sortKeys = false; //iConfig.getValue
			
		for(Map.Entry<File, File> entry : tempFiles.entrySet()) {

			if(!this.parseFile(entry.getKey())){
				
				String jsonString = jsonFileToString(entry.getKey());
				
				if(sortKeys){
					
					jsonString = this.sortKeysAlphabetical(jsonString);
					
				}
				
				if(deleteValues){
					
					jsonString = this.deleteValues(jsonString);
					
				}
				
				BufferedWriter writer;
				try{
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.getValue()), "UTF-8"));
					writer.write(jsonString);
					writer.close();
				}catch(Exception e){
					
					e.printStackTrace();
					
				}
			
					
			}else{
				//inkorrekt
			}
			
		}
		
		return tempFiles;
	}
	
	
	/**
	 * Ersetzt aus gegebenem String, welcher ein vollständiges JSON Dokument darstellt,
	 * alle Values
	 * 
	 * @param String
	 *            JSON-Dokument als String
	 *            
	 * @return String
	 * 			  JSON-Dokument als String ohne Values
	 */
	public String deleteValues(String jsonString){
		
		//TODO
		
		return jsonString;
		
	}
	
	
	
	/**
	 * Diese Methode setzt vorraus, dass das JSON-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param jsonString
	 * 		  repraesentiert ein JSON Dokument dessen Values alphabetisch sortiert werden sollen.
	 * 
	 * @return representiert das urspruengliche Dokument mit alphabeitsch geordneten Values.
	 * 		
	 * */
	public String sortKeysAlphabetical(String jsonString){
			
		ObjectMapper mapper = JsonMapper.builder()
			    .nodeFactory(new IJSONSortNodeFactoryImpl())
			    .build();
		
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		JsonNode root = null;
		String sortedJSONString = "";
		
		try{
			
			root = mapper.readTree(jsonString);
			sortedJSONString = mapper.writeValueAsString(root);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sortedJSONString;
	}
	
	/**
	 * Parst mittels Jackson gegebenes File auf Wohlgeformtheit
	 * 
	 * @param file
	 *            File welches JSON-Objekte beinhaltet
	 *            
	 * @return boolean
	 * 			  Wenn ein Parse-Error aufgetreten ist true, sonst false.
	 */
	@Override
	public boolean parseFile(File file) {
		
		boolean parseErrorOccured = false;
		
	    FileInputStream fileInputStream = null;
	      
	    byte[] fileInBytes = new byte[(int) file.length()];
	      
	    try {
	    	
	    	fileInputStream = new FileInputStream(file);
	        fileInputStream.read(fileInBytes);
	        fileInputStream.close();
 	 
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   String jsonString = new String(fileInBytes);
	   
	   JsonNode root = null;
	   
	   try{
		   ObjectMapper mapper = JsonMapper.builder().build();

	       root = mapper.readTree(jsonString);
        
    	}catch (JsonParseException jpe) {
    	
	    	parseError = new IJSONParseErrorImpl(file, jpe.getMessage(), jpe.getLocation().getColumnNr(), jpe.getLocation().getLineNr());
	    	addErrorToErrorList(parseError);
	    	parseErrorOccured = true;
	 
    	}catch(JsonMappingException jme){
    		parseError = new IJSONParseErrorImpl(file, jme.getMessage(), jme.getLocation().getColumnNr(), jme.getLocation().getLineNr());
	    	addErrorToErrorList(parseError);
	    	parseErrorOccured = true;
    
    	}catch(JsonProcessingException jpe){
    		parseError = new IJSONParseErrorImpl(file, jpe.getMessage(), jpe.getLocation().getColumnNr(), jpe.getLocation().getLineNr());
	    	addErrorToErrorList(parseError);
	    	parseErrorOccured = true;
    
    	}
	    
	    
	   return parseErrorOccured;
	}
	
	

	/**
	 * Hilfsmethode 
	 * Diese Methode setzt vorraus, dass das XML-Dokument bereits erfolgreich geparst wurde!
	 * 
	 * @param  file
	 * 		   repraesentiert eine JSON-Datei die in einen String umgeformt werden soll.
	 *  
	 * @return content
	 * 		   repraesentiert uebergebene JSON-Datei als String.
	 */
	@Override
	public String jsonFileToString(File file) {
		boolean parseErrorOccured = false;
		
	    FileInputStream fileInputStream = null;
	      
	    byte[] fileInBytes = new byte[(int) file.length()];
	      
	    try {
	    	
	    	fileInputStream = new FileInputStream(file);
	        fileInputStream.read(fileInBytes);
	        fileInputStream.close();
	        
	    }catch (Exception e) { 	 
	    	e.printStackTrace();
	    }
	    return new String(fileInBytes);
	}
	
	/**
	 * get Methode ermoeglicht Zugriff auf die Errorliste
	 * 
	 * @return List<IXMLParseError>
	 *           Liste mit moeglichen Elementen der Klasse IXMLParseError,
	 *           die bei auftretenden Fehlern waehrend des Parsens festegestellt wurden
	 */
	@Override
	public List<IJSONParseError> getErrorList() {
		return this.errorListe;
	}

	/**
	 * Hilfsmethode
	 * Methode zum neu Initialisieren der Error Liste
	 * 
	 */
	@Override
	public void clearErrorList() {
		this.errorListe = new ArrayList<>();
	}

	/**
	 * Methode fuegt festgestellte Parse-Error zur
	 * Liste der festgestellten Fehler hinzu
	 * 
	 * @param error
	 *            Objekt der Klasse IJSONParseErrorImpl
	 */
	public void addErrorToErrorList(IJSONParseError error){
		errorListe.add(error);
	}
	
	
}
