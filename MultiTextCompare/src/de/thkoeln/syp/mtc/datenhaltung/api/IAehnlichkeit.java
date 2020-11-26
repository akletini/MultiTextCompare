package de.thkoeln.syp.mtc.datenhaltung.api;

import java.io.File;

public interface IAehnlichkeit {
	boolean setVon(File von);

	boolean setZu(File zu);

	boolean setWert(double wert);
	
	boolean setFarbe(String farbe);

	File getVon();

	File getZu();

	double getWert();
	
	String getFarbe();
}