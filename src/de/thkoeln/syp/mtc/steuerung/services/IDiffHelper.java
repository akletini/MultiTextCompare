package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;

/**
 * Berechnet die Diff (Difference) zwischen zwei oder drei Dateien und speichert
 * diese in jeweiligen Listen mit den farbannotierten Zeichen der einzelnen
 * Zeilen
 * 
 * @author Allen Kletinitch
 *
 */
public interface IDiffHelper {

	List<IDiffLine> getLeftLines();

	List<IDiffLine> getRightLines();

	List<IDiffLine> getMiddleLines();

	void computeDisplayDiff(File[] files) throws IOException;
	
}
