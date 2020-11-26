package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public class IConfigImpl implements IConfig {

	private boolean beachteLeerzeichen, beachteSatzzeichen, beachteGrossschreibung;
	private String rootDir;
	
	@Override
	public boolean setBeachteLeerzeichen(boolean beachteLeerzeichen) {
		this.beachteLeerzeichen = beachteLeerzeichen;
		return true;
	}

	@Override
	public boolean setBeachteSatzzeichen(boolean beachteSatzzeichen) {
		this.beachteSatzzeichen = beachteSatzzeichen;
		return true;
	}

	@Override
	public boolean setBeachteGrossschreibung(boolean beachteGrossschreibung) {
		this.beachteGrossschreibung = beachteGrossschreibung;
		return false;
	}

	@Override
	public boolean setRootDir(String rootDir) {
		this.rootDir = rootDir;
		return false;
	}

	@Override
	public boolean getBeachteLeerzeichen() {
		return beachteLeerzeichen;
	}

	@Override
	public boolean getBeachteSatzzeichen() {
		return beachteSatzzeichen;
	}

	@Override
	public boolean getBeachteGrossschreibung() {
		return beachteGrossschreibung;
	}

	@Override
	public String getRootDir() {
		return rootDir;
	}
}
