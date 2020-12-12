package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;

public interface IXMLvergleicher {
	
	List<IXMLParseError> parseFile(File file, int mode);
	
	List<IXMLParseError> getErrorList();
	
	void addErrorToErrorList(IXMLParseError error);
}
