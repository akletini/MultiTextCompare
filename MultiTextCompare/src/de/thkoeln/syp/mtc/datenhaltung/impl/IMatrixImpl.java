package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.util.ArrayList;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;

public class IMatrixImpl implements IMatrix {

	private List<IAehnlichkeitImpl> inhalt;
	
	public IMatrixImpl() {
		inhalt = new ArrayList<IAehnlichkeitImpl>();
	}

	@Override
	public List<IAehnlichkeitImpl> getInhalt() {
		return inhalt;
	}
	
	
	@Override
	public void setInhalt(List<IAehnlichkeitImpl> inhalt) {
		if(inhalt != null){
			this.inhalt = new ArrayList<IAehnlichkeitImpl>();
			for(int i = 0; i < inhalt.size(); i++){
				this.inhalt.add(new IAehnlichkeitImpl(
						inhalt.get(i).getVon(),
						inhalt.get(i).getZu(),
						inhalt.get(i).getWeight(),
						inhalt.get(i).getId(),
						inhalt.get(i).getWert()
						));
			}
		}
	}
	
}
