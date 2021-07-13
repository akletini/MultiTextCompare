package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

public interface IJSONCompare {

	double compare(File ref, File comp) throws IOException;

}
