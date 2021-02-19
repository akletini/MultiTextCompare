package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IJSONParseError;

public interface IJSONvergleicher {
  
  boolean parseFile(File file);
  
  String jsonFileToString(File file);
  
  List<IJSONParseError> getErrorList();
  
  void clearErrorList();
  
  Map<File, File> jsonPrepare(Map<File, File> tempFiles);
  
  String sortKeysAlphabetical(String jsonString);
  
  String deleteValues(String jsonString);
}