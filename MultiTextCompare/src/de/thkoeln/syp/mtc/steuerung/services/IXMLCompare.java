package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

public interface IXMLCompare {

	double compare(File ref, File comp) throws IOException, JDOMException;

}
