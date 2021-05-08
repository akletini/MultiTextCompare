package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;

public interface IDiffHelper {

	List<IDiffLine> getLeftLines();

	List<IDiffLine> getRightLines();

	List<IDiffLine> getMiddleLines();

	void computeDisplayDiff(File[] files) throws IOException;
	
	void setFileImporter(IFileImporter fileImporter);
}
