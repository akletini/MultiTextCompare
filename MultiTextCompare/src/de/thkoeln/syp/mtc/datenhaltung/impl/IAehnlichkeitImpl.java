package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.io.File;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;

public class IAehnlichkeitImpl implements IAehnlichkeit {

	private File von, zu;
	private double wert;

	@Override
	public void setVon(File von) {
		if (von != null) {
			this.von = von;
		}
	}

	@Override
	public void setZu(File zu) {
		if (zu != null) {
			this.zu = zu;
		}
	}

	@Override
	public void setWert(double wert) {
		if (wert >= 0 && wert <= 1) {
			this.wert = wert;
		}
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
