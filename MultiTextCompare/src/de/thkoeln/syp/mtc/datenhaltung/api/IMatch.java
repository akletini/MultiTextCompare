package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IMatchImpl;

public interface IMatch {

	int getLeftRow();

	void setLeftRow(int leftRow);

	int getRightRow();

	void setRightRow(int rightRow);

	String getValue();

	void setValue(String value);

}
