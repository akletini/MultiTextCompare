package de.thkoeln.syp.mtc.datenhaltung.api;

public interface IConfig {
	boolean setBeachteLeerzeichen(boolean beachteLeerzeichen);

	boolean setBeachteSatzzeichen(boolean beachteSatzzeichen);

	boolean setBeachteGrossschreibung(boolean beachteGrossschreibung);

	boolean setRootDir(String rootDir);

	boolean getBeachteLeerzeichen();

	boolean getBeachteSatzzeichen();

	boolean getBeachteGrossschreibung();

	String getRootDir();
}
