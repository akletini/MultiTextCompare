package de.thkoeln.syp.mtc.datenhaltung.api;


public interface IMatch {

	int getLeftRow();

	void setLeftRow(int leftRow);

	int getRightRow();

	void setRightRow(int rightRow);

	String getValueLeft();

	void setValueLeft(String value);

	String getValueRight();

	void setValueRight(String valueRight);

	int getMatchLCS();

	void setMatchLCS(int matchLCS);

}
