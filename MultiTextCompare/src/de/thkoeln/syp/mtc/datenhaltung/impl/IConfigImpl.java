package de.thkoeln.syp.mtc.datenhaltung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;

public class IConfigImpl implements IConfig {

	private boolean keepWhitespaces, keepPunctuation, keepCapitalizazion,
			compareLines, keepBlankLines;

	private String rootDir, path, filename, filetype, pathCurrent;
	
	//Log parameters
	private boolean showInfos, showWarnings, showErrors;
	
	// Matching parameter
	private boolean lineMatch, bestMatch;
	private int matchingLookahead;
	private double matchAt;
	
	// XML spezifische Parameter
	private boolean xmlSortElements, xmlSortAttributes, xmlDeleteAttributes,
			xmlDeleteComments, xmlOnlyTags;
	private int xmlValidation, xmlPrint;

	// JSON spezifische Parameter
	private boolean jsonSortKeys, jsonDeleteValues;

	@Override
	public void setCompareLines(boolean compareLines) {
		this.compareLines = compareLines;
	}

	@Override
	public void setKeepWhitespaces(boolean keepWhitespaces) {
		this.keepWhitespaces = keepWhitespaces;
	}

	@Override
	public void setKeepPuctuation(boolean keepPunctuation) {
		this.keepPunctuation = keepPunctuation;
	}

	@Override
	public void setKeepCapitalization(boolean keepCapitalizazion) {
		this.keepCapitalizazion = keepCapitalizazion;
	}

	@Override
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	@Override
	public boolean getKeepWhitespaces() {
		return keepWhitespaces;
	}

	@Override
	public boolean getKeepPuctuation() {
		return keepPunctuation;
	}

	@Override
	public boolean getKeepCapitalization() {
		return keepCapitalizazion;
	}

	@Override
	public String getRootDir() {
		return rootDir;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean getCompareLines() {
		return compareLines;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getFiletype() {
		return filetype;
	}

	@Override
	public void setKeepBlankLines(boolean keepBlankLines) {
		this.keepBlankLines = keepBlankLines;
	}

	@Override
	public boolean getKeepBlankLines() {
		return keepBlankLines;
	}

	@Override
	public void setXmlSortElements(boolean xmlSortElements) {
		this.xmlSortElements = xmlSortElements;
	}

	@Override
	public boolean getXmlSortElements() {
		return this.xmlSortElements;
	}

	@Override
	public void setXmlSortAttributes(boolean xmlSortAttributes) {
		this.xmlSortAttributes = xmlSortAttributes;
	}

	@Override
	public boolean getXmlSortAttributes() {
		return this.xmlSortAttributes;
	}

	@Override
	public void setXmlDeleteAttributes(boolean xmlDeleteAttributes) {
		this.xmlDeleteAttributes = xmlDeleteAttributes;
	}

	@Override
	public boolean getXmlDeleteAttributes() {
		return this.xmlDeleteAttributes;
	}

	@Override
	public void setXmlDeleteComments(boolean xmlDeleteComments) {
		this.xmlDeleteComments = xmlDeleteComments;
	}

	@Override
	public boolean getXmlDeleteComments() {
		return this.xmlDeleteComments;
	}

	@Override
	public void setXmlOnlyTags(boolean xmlOnlyTags) {
		this.xmlOnlyTags = xmlOnlyTags;
	}

	@Override
	public boolean getXmlOnlyTags() {
		return this.xmlOnlyTags;
	}

	@Override
	public void setXmlValidation(int xmlValidation) {
		this.xmlValidation = xmlValidation;
	}

	@Override
	public int getXmlValidation() {
		return this.xmlValidation;
	}

	@Override
	public void setJsonSortKeys(boolean jsonSortKeys) {
		this.jsonSortKeys = jsonSortKeys;
	}

	@Override
	public void setJsonDeleteValues(boolean jsonDeleteValues) {
		this.jsonDeleteValues = jsonDeleteValues;
	}

	@Override
	public boolean getJsonSortKeys() {
		return jsonSortKeys;
	}

	@Override
	public boolean getJsonDeleteValues() {
		return jsonDeleteValues;
	}

	@Override
	public void setLineMatch(boolean lineMatch) {
		this.lineMatch = lineMatch;
	}

	@Override
	public boolean getLineMatch() {
		return lineMatch;
	}
	
	@Override
	public boolean getBestMatch() {
		return bestMatch;
	}
	@Override
	public void setBestMatch(boolean bestMatch) {
		this.bestMatch = bestMatch;
	}

	@Override
	public void setXmlPrint(int xmlPrint) {
		this.xmlPrint = xmlPrint;
	}

	@Override
	public int getXmlPrint() {
		return xmlPrint;
	}
	@Override
	public int getMatchingLookahead() {
		return matchingLookahead;
	}
	@Override
	public double getMatchAt() {
		return matchAt;
	}
	@Override
	public void setMatchingLookahead(int matchingLookahead) {
		this.matchingLookahead = matchingLookahead;
	}
	@Override
	public void setMatchAt(double matchAt) {
		this.matchAt = matchAt;
	}
	@Override
	public boolean getShowInfos() {
		return showInfos;
	}
	@Override
	public boolean getShowWarnings() {
		return showWarnings;
	}
	@Override
	public boolean getShowErrors() {
		return showErrors;
	}
	@Override
	public void setShowInfos(boolean showInfos) {
		this.showInfos = showInfos;
	}
	@Override
	public void setShowWarnings(boolean showWarnings) {
		this.showWarnings = showWarnings;
	}
	@Override
	public void setShowErrors(boolean showErrors) {
		this.showErrors = showErrors;
	}
	@Override
	public String getPathCurrent() {
		return pathCurrent;
	}
	@Override
	public void setPathCurrent(String pathCurrent) {
		this.pathCurrent = pathCurrent;
	}
	
	
	
}