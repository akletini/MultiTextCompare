package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.io.File;

import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;

public class IXMLParseErrorImpl implements IXMLParseError {
	private File file;
	private String message;
	private int col;
	private int line;
	
	public IXMLParseErrorImpl(File eFile, String eMessage, int eCol, int eLine){
		this.file = eFile;
		this.message = eMessage;
		this.col = eCol;
		this.line = eLine;
	}

	public IXMLParseErrorImpl(){};
	
	
	@Override
	public void setFile(File eFile){
		this.file = eFile;
	}
	
	@Override
	public File getFile(){
		return this.file;
	}
	
	@Override
	public void setMessage(String eMessage) {
		this.message = eMessage;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public void setCol(int eCol) {
		this.col = eCol;
	}

	@Override
	public int getCol() {
		return this.col;
	}

	@Override
	public void setLine(int eLine) {
		this.line = eLine;
	}

	@Override
	public int getLine() {
		return this.line;
	}
	
	
	
}
