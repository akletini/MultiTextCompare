package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IComparison;

import java.io.File;
import java.io.Serializable;

public class IComparisonImpl implements IComparison, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8018312879168148656L;
	private File from, to;
	private int weight, id;
	private double value;

	public IComparisonImpl() {

	}

	public IComparisonImpl(File from, File to, int weight, int id, double value) {
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.id = id;
		this.value = value;
	}

	@Override
	public void setFrom(File from) {
		if (from != null) {
			this.from = from;
		}
	}

	@Override
	public void setTo(File to) {
		if (to != null) {
			this.to = to;
		}
	}

	@Override
	public void setValue(double wert) {
		this.value = wert;
	}

	@Override
	public File getFrom() {
		return from;
	}

	@Override
	public File getTo() {
		return to;
	}

	@Override
	public double getValue() {
		return value;
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
		return "" + id;
	}
}
