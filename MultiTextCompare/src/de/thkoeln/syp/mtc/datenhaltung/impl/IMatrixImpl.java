package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;

public class IMatrixImpl implements IMatrix {

	private List<IAehnlichkeitImpl> inhalt;

	@Override
	public List<IAehnlichkeitImpl> getInhalt() {
		return inhalt;
	}
}
