package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

public interface IDiffHelper {
	void computeDisplayDiff(File[] files, String mode) throws IOException;
}
