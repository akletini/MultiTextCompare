package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

public interface IMatchHelper {

	void matchEqualLines(File a, File b) throws IOException;

}
