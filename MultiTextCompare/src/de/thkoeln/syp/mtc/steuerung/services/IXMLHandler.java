package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;

public interface IXMLHandler {
  
  Document sortAttributes(Document doc);
  
  Document sortElements(Document doc, Comparator<Element> comparator);
  
  boolean parseFile(File file, int mode);
  
  String deleteAttributes(String xmlFile);
  
  String deleteComments(String xmlFile);
  
  String tagsOnly(String xmlFile);
  
  String xmlFileToString(File file);
  
  List<IParseError> getErrorList();
  
  void clearErrorList();
  
  Map<File, File> xmlPrepare(Map<File, File> tempFiles);
}