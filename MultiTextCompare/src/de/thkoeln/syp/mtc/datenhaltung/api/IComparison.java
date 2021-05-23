package de.thkoeln.syp.mtc.datenhaltung.api;

import java.io.File;

public interface IComparison {
	void setFrom(File from);

	void setTo(File to);

	void setValue(double value);

	File getFrom();

	File getTo();

	double getValue();
	
	public int getWeight();

	void setWeight(int weight);

	int getId();

	void setId(int id);
	
}