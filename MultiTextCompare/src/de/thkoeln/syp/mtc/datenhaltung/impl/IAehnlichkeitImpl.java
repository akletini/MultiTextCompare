package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.io.File;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;

public class IAehnlichkeitImpl implements IAehnlichkeit {

	private File von, zu;
	private double wert;


	@Override
	public boolean setVon(File von) {
		this.von = von;
		return true;
	}

	@Override
	public boolean setZu(File zu) {
		this.zu = zu;
		return true;
	}

	@Override
	public boolean setWert(double wert) {
		this.wert = wert;
		return true;
	}


	@Override
	public File getVon() {
		return von;
	}

	@Override
	public File getZu() {
		return zu;
	}

	@Override
	public double getWert() {
		return wert;
	}
}


