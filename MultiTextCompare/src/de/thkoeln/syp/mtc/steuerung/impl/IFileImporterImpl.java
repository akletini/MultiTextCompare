package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IFileImporterImpl implements IFileImporter {

	private IConfigImpl iConfigImpl;
	private List<File> textdateien;

	@Override
	public IConfigImpl getConfig() {
		return iConfigImpl;
	}

	@Override
	public List<File> getTextdateien() {
		return textdateien;
	}

	@Override
	public boolean importConfigdatei() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean importTextdateien() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean importTextRoot() {
		// TODO Auto-generated method stub
		return false;
	}
}
