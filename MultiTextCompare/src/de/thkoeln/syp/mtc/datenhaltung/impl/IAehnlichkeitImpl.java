package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.io.File;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;

public class IAehnlichkeitImpl implements IAehnlichkeit {

	private File von, zu;
	private int weight, id;
	private double wert;

	public IAehnlichkeitImpl() {

	}

	public IAehnlichkeitImpl(File von, File zu, int weight, int id, double wert) {
		this.von = von;
		this.zu = zu;
		this.weight = weight;
		this.id = id;
		this.wert = wert;
	}

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
		this.wert = wert;
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

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		// return "Compare " + von.getName() + " with " + zu.getName() + " ID: "
		// + id + ", weight: " + weight;
		return "" + id;
	}
}
