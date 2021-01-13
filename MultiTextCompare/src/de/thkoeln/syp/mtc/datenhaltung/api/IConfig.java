package de.thkoeln.syp.mtc.datenhaltung.api;

public interface IConfig {
	void setBeachteLeerzeichen(boolean beachteLeerzeichen);

	void setBeachteSatzzeichen(boolean beachteSatzzeichen);

	void setBeachteGrossschreibung(boolean beachteGrossschreibung);

	void setRootDir(String rootDir);

	void setPath(String path);

	void setLineMatch(boolean lineMatch);

	void setDateiname(String dateiname);

	boolean getBeachteLeerzeichen();

	boolean getBeachteSatzzeichen();

	boolean getBeachteGrossschreibung();

	String getRootDir();

	String getPath();

	boolean getLineMatch();

	String getDateiname();
}
