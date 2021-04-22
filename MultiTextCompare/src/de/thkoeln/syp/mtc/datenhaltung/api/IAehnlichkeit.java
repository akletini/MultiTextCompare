package de.thkoeln.syp.mtc.datenhaltung.api;

import java.io.File;

public interface IAehnlichkeit {
	void setVon(File von);

	void setZu(File zu);

	void setWert(double wert);

	File getVon();

	File getZu();

	double getWert();
	
	public int getWeight();

	void setWeight(int weight);

	int getId();

	void setId(int id);
	
}