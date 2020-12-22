package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public class IConfigImpl implements IConfig {

	private boolean beachteLeerzeichen, beachteSatzzeichen,
			beachteGrossschreibung, lineMatch;
	private String rootDir;
	private String path;
	
	@Override
	public void setLineMatch(boolean lineMatch){
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
	public boolean getLineMatch(){
		return lineMatch;
	}
}
