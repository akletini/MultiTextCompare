package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;

public interface IMatrix {
	List<IAehnlichkeitImpl> getInhalt();
}
