package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatch;

public class IMatchImpl implements IMatch{
	private int leftRow;
	private int rightRow;
	private String valueLeft;
	private String valueRight;
	
	public IMatchImpl(){
		
	}
	public IMatchImpl(int leftRow, int rightRow, String valueLeft, String valueRight){
		this.leftRow = leftRow;
		this.rightRow = rightRow;
		this.valueLeft = valueLeft;
		this.valueRight = valueRight;
	}

	@Override
	public String getValueLeft() {
		return valueLeft;
	}
	@Override
	public void setValueLeft(String value) {
		this.valueLeft = value;
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
	@Override
	public String getValueRight() {
		return valueRight;
	}
	@Override
	public void setValueRight(String valueRight) {
		this.valueRight = valueRight;
	}
	
	@Override
	public String toString(){
		return "Match: \"" + this.valueLeft + "\"\t mit: \t\"" + this.valueRight + "\"\t Zeilen: (" + this.leftRow + " | " + this.rightRow + ")";
	}
	
	
}
