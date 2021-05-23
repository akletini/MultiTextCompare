package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;

public interface IMatrix {
	List<IComparisonImpl> getInhalt();
	void setInhalt(List<IComparisonImpl> inhalt);
}
