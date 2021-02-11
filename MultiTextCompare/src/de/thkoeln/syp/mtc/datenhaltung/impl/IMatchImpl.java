package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.util.ArrayList;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatch;

public class IMatchImpl implements IMatch{
	private int leftRow;
	private int rightRow;
	private String value;
	
	public IMatchImpl(){
		
	}
	public IMatchImpl(int leftRow, int rightRow, String value){
		this.leftRow = leftRow;
		this.rightRow = rightRow;
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public int getLeftRow() {
		return leftRow;
	}
	@Override
	public void setLeftRow(int leftRow) {
		this.leftRow = leftRow;
	}
	@Override
	public int getRightRow() {
		return rightRow;
	}
	@Override
	public void setRightRow(int rightRow) {
		this.rightRow = rightRow;
	}
	
	
}
