package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.List;

public interface IDiffLine {

	void setDiffedLine(List<IDiffChar> diffedLine);

	List<IDiffChar> getDiffedLine();

}
