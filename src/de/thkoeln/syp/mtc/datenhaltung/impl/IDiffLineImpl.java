package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;
import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;

import java.util.ArrayList;
import java.util.List;

public class IDiffLineImpl implements IDiffLine{
	private List<IDiffChar> diffedLine;
	
	@Override
	public List<IDiffChar> getDiffedLine() {
		return diffedLine;
	}
	
	@Override
	public void setDiffedLine(List<IDiffChar> diffedLine) {
		this.diffedLine = diffedLine;
	}

	public IDiffLineImpl(){
		diffedLine = new ArrayList<IDiffChar>();
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		for(IDiffChar c : diffedLine){
			s.append(c.getCurrentChar().toString());
		}
		return s.toString();
	}
}
