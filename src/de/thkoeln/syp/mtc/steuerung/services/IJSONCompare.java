package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

/**
 * Verantwortlich für den strukturellen Vergleich von JSON-Dateien
 * 
 * @author Allen Kletinitch
 *
 */
public interface IJSONCompare {

	double compare(File ref, File comp) throws IOException;

}
