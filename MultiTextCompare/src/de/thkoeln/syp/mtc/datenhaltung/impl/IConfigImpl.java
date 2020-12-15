package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public class IConfigImpl implements IConfig {

	private boolean beachteLeerzeichen, beachteSatzzeichen,
			beachteGrossschreibung, lineMatch;
	private String rootDir;
	private String path;
	
	@Override
	public boolean setLineMatch(boolean lineMatch){
		this.lineMatch = lineMatch;
		return true;
	}
	
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
	public boolean setPath(String path) {
		this.path = path;
		return true;
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
