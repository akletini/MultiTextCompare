package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;

/**
 * Bereitet XML-Dateien für den Vergleich vor. Verantwortlich für das Parsen,
 * Sortieren und Manipulieren.
 * 
 * @author Matthias Pooth
 *
 */
public interface IXMLHandler {
  
  Document sortAttributes(Document doc);
  
  Document sortElements(Document doc, Comparator<Element> comparator);
  
  boolean parseFile(File file, int mode, File externalXSD);
  
  String deleteAttributes(String xmlFile);
  
  String deleteComments(String xmlFile);
  
  String tagsOnly(String xmlFile);
  
  String xmlFileToString(File file);
  
  List<IParseError> getErrorList();
  
  void clearErrorList();
  
  Map<File, File> xmlPrepare(Map<File, File> tempFiles);
  
  void setExternalXSD(File externalXSD);
}