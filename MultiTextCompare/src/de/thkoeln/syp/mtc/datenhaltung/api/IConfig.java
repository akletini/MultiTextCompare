package de.thkoeln.syp.mtc.datenhaltung.api;

public interface IConfig {
	void setBeachteLeerzeichen(boolean beachteLeerzeichen);

	void setBeachteSatzzeichen(boolean beachteSatzzeichen);

	void setBeachteGrossschreibung(boolean beachteGrossschreibung);

	void setRootDir(String rootDir);

	void setPath(String path);

	void setLineMatch(boolean lineMatch);

	void setDateiname(String dateiname);
	
	void setDateityp(String dateityp);

	void setBeachteLeerzeilen(boolean beachteLeerzeilen);

	boolean getBeachteLeerzeichen();

	boolean getBeachteSatzzeichen();

	boolean getBeachteGrossschreibung();

	String getRootDir();

	String getPath();

	boolean getLineMatch();

	String getDateiname();
	
	String getDateityp();

	boolean getBeachteLeerzeilen();
	
	
	//XML spezifische Parameter
	void setSortiereElemente(boolean sortiereElemente);
	
	void setSortiereAttribute(boolean sortiereAttribute);
	
	void setLoescheAttribute(boolean loescheAttribute);
	
	void setLoescheKommentare(boolean loescheKommentare);
	
	void setNurTags(boolean nurTags);
	
	void setValidation(int validation);
	
	boolean getSortiereElemente();
	
	boolean getSortiereAttribute();
	
	boolean getLoescheAttribute();
	
	boolean getLoescheKommentare();
	
	boolean getNurTags();
	
	int getValidation();
	
	
}
