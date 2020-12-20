package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;

public interface IDiffHelper {
	public void computeDisplayDiff(File[] files) throws IOException;
}
