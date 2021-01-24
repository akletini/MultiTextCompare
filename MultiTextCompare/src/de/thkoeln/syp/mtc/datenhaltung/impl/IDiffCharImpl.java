package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;

public class IDiffCharImpl implements IDiffChar{
	private Character currentChar;
	private String charColor;
	
	public IDiffCharImpl(){
		this.charColor = "WHITE";
	}
	
	public IDiffCharImpl(Character currentChar, String charColor){
		this.currentChar = currentChar;
		this.charColor = charColor;
	}
	
	@Override
	public Character getCurrentChar() {
		return currentChar;
	}
	@Override
	public void setCurrentChar(Character currentChar) {
		this.currentChar = currentChar;
	}
	@Override
	public String getCharColor() {
		return charColor;
	}
	@Override
	public void setCharColor(String charColor) {
		this.charColor = charColor;
	}
	
	
}
