package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;

import org.jdom2.Document;

import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;

public interface IXMLvergleicher {
  
  Document sortAttributes(Document doc);
  
  Document sortElements(Document doc);
  
  List<IXMLParseError> parseFile(File file, int mode);
  
  String deleteAttributes(String xmlFile);
  
  String deleteComments(String xmlFile);
  
  String tagsOnly(String xmlFile);
  
  String xmlFileToString(File file);
  
  void setXSDFile(File xsdFile);
}