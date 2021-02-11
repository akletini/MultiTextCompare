package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IMatchImpl;

public interface IMatch {

	int getLeftRow();

	void setLeftRow(int leftRow);

	int getRightRow();

	void setRightRow(int rightRow);

	String getValueLeft();

	void setValueLeft(String value);

	String getValueRight();

	void setValueRight(String valueRight);

}
