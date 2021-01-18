package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public class IConfigImpl implements IConfig {

	private boolean beachteLeerzeichen, beachteSatzzeichen,
			beachteGrossschreibung, lineMatch, beachteLeerzeilen;
	private String rootDir, path, dateiname;

	@Override
	public void setLineMatch(boolean lineMatch) {
		this.lineMatch = lineMatch;
	}

	@Override
	public void setBeachteLeerzeichen(boolean beachteLeerzeichen) {
		this.beachteLeerzeichen = beachteLeerzeichen;
	}

	@Override
	public void setBeachteSatzzeichen(boolean beachteSatzzeichen) {
		this.beachteSatzzeichen = beachteSatzzeichen;
	}

	@Override
	public void setBeachteGrossschreibung(boolean beachteGrossschreibung) {
		this.beachteGrossschreibung = beachteGrossschreibung;
	}

	@Override
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void setDateiname(String dateiname) {
		this.dateiname = dateiname;
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

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean getLineMatch() {
		return lineMatch;
	}

	@Override
	public String getDateiname() {
		return dateiname;
	}

	@Override
	public void setBeachteLeerzeilen(boolean beachteLeerzeilen) {
		this.beachteLeerzeilen = beachteLeerzeilen;
	}

	@Override
	public boolean getBeachteLeerzeilen() {
		return beachteLeerzeilen;
	}
}
