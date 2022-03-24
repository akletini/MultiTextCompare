package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.util.ArrayList;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;

public class IMatrixImpl implements IMatrix {

	private List<IComparisonImpl> inhalt;
	
	public IMatrixImpl() {
		inhalt = new ArrayList<IComparisonImpl>();
	}

	@Override
	public List<IComparisonImpl> getInhalt() {
		return inhalt;
	}
	
	
	@Override
	public void setInhalt(List<IComparisonImpl> inhalt) {
		if(inhalt != null){
			this.inhalt = new ArrayList<IComparisonImpl>();
			for(int i = 0; i < inhalt.size(); i++){
				this.inhalt.add(new IComparisonImpl(
						inhalt.get(i).getFrom(),
						inhalt.get(i).getTo(),
						inhalt.get(i).getWeight(),
						inhalt.get(i).getId(),
						inhalt.get(i).getValue()
						));
			}
		}
	}
	
}
