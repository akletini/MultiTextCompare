package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

/**
 * Verantwortlich für den strukturellen Vergleich von XML-Dateien
 * 
 * @author Allen Kletinitch
 *
 */
public interface IXMLCompare {

	double compare(File ref, File comp) throws IOException, JDOMException;

}
