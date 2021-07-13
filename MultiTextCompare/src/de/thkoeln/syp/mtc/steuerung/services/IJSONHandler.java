package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;

/**
 * Bereitet JSON-Dateien f�r den Vergleich vor. Verantwortlich f�r das Parsen,
 * Sortieren und Manipulieren.
 * 
 * @author Matthias Pooth
 *
 */
public interface IJSONHandler {
  
  boolean parseFile(File file);
  
  String jsonFileToString(File file) throws IOException;
  
  List<IParseError> getErrorList();
  
  void clearErrorList();
  
  Map<File, File> jsonPrepare(Map<File, File> tempFiles) throws IOException;
  
  String sortKeysAlphabetical(String jsonString);
  
  String deleteValues(String jsonString);
}