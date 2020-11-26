package de.thkoeln.syp.mtc.steuerung.impl;

import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class ITextvergleicherImpl implements ITextvergleicher {

	private IMatrixImpl iMatrixImpl;

	@Override
	public boolean vergleichen() {

		return true;
	}
}
