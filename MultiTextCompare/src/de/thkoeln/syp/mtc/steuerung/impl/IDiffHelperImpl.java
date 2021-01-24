package de.thkoeln.syp.mtc.steuerung.impl;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;
import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;
import de.thkoeln.syp.mtc.datenhaltung.impl.IDiffCharImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IDiffLineImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;

public class IDiffHelperImpl implements IDiffHelper {

	private List<IDiffLine> leftLines = new ArrayList<IDiffLine>();
	private List<IDiffLine> rightLines = new ArrayList<IDiffLine>();
	private List<IDiffLine> middleLines = new ArrayList<IDiffLine>();

	/**
	 * Die Methode ermittelt die Differenzen zwischen den �bergebenen Dateien
	 * und markiert diese durch die Klasse FileCommandsVisitor
	 * 
	 * @param files Die Dateien, deren Diff gebildet werden soll
	 * 
	 */
	@Override
	public void computeDisplayDiff(File[] files) throws IOException {
		// Read both files with line iterator.
		if (files.length == 2) {
			LineIterator file1 = FileUtils.lineIterator(files[0]);
			LineIterator file2 = FileUtils.lineIterator(files[1]);

			// Initialize visitor.
			FileCommandVisitor fileCommandsVisitor = new FileCommandVisitor();
			int lineNum = 0;

			// Read file line by line so that comparison can be done line by
			// line.
			while (file1.hasNext() || file2.hasNext()) {
				/*
				 * In case both files have different number of lines, fill in
				 * with empty strings. Also append newline char at end so next
				 * line comparison moves to next line.
				 */
				lineNum++;

				String left = lineNum + "  "
						+ (file1.hasNext() ? file1.nextLine() : "") + "\n";
				String right = lineNum + "  "
						+ (file2.hasNext() ? file2.nextLine() : "") + "\n";

				// Prepare diff comparator with lines from both files.
				StringsComparator comparator = new StringsComparator(left,
						right);

				if (comparator.getScript().getLCSLength() > (Math.max(
						left.length(), right.length()) * 0.4)) {
					/*
					 * If both lines have atleast 40% commonality then only
					 * compare with each other so that they are aligned with
					 * each other in final diff.
					 */
					comparator.getScript().visit(fileCommandsVisitor);
				} else {
					/*
					 * If both lines do not have 40% commanlity then compare
					 * each with empty line so that they are not aligned to each
					 * other in final diff instead they show up on separate
					 * lines.
					 */
					StringsComparator leftComparator = new StringsComparator(
							left, "");
					leftComparator.getScript().visit(fileCommandsVisitor);
					StringsComparator rightComparator = new StringsComparator(
							"", right);
					rightComparator.getScript().visit(fileCommandsVisitor);
				}
			}
			fileCommandsVisitor.generateDiff(2);
			leftLines = fileCommandsVisitor.getLeftLines();
			rightLines = fileCommandsVisitor.getRightLines();
		} else {
			final LineIterator file1 = FileUtils.lineIterator(files[0]);
			final LineIterator file2 = FileUtils.lineIterator(files[1]);
			final LineIterator file3 = FileUtils.lineIterator(files[2]);

			// Initialize visitor.
			FileCommandVisitor fileCommandsVisitor = new FileCommandVisitor();
			int lineNum = 0;

			// Read file line by line so that comparison can be done line by
			// line.
			while (file1.hasNext() || file2.hasNext() || file3.hasNext()) {
				/*
				 * In case both files have different number of lines, fill in
				 * with empty strings. Also append newline char at end so next
				 * line comparison moves to next line.
				 */
				lineNum++;

				String left = lineNum + "  "
						+ (file1.hasNext() ? file1.nextLine() : "") + "\n";
				String middle = lineNum + "  "
						+ (file2.hasNext() ? file2.nextLine() : "") + "\n";
				String right = lineNum + "  "
						+ (file3.hasNext() ? file3.nextLine() : "") + "\n";

				// Prepare diff comparator with lines from both files.
				StringsComparator comparator1 = new StringsComparator(left,
						middle);
				StringsComparator comparator2 = new StringsComparator(left,
						right);
				StringsComparator comparator3 = new StringsComparator(middle,
						right);

				if (comparator1.getScript().getLCSLength() > (Math.max(
						left.length(), middle.length()) * 0.4)) {
					/*
					 * If both lines have atleast 40% commonality then only
					 * compare with each other so that they are aligned with
					 * each other in final diff.
					 */
					comparator1.getScript().visit(fileCommandsVisitor);

				} else {
					/*
					 * If both lines do not have 40% commanlity then compare
					 * each with empty line so that they are not aligned to each
					 * other in final diff instead they show up on separate
					 * lines.
					 */
					StringsComparator leftComparator = new StringsComparator(
							left, "");
					leftComparator.getScript().visit(fileCommandsVisitor);
					StringsComparator middleComparator = new StringsComparator(
							"", middle);
					middleComparator.getScript().visit(fileCommandsVisitor);

				}

				if (comparator2.getScript().getLCSLength() > (Math.max(
						left.length(), right.length()) * 0.4)) {
					/*
					 * If both lines have atleast 40% commonality then only
					 * compare with each other so that they are aligned with
					 * each other in final diff.
					 */
					comparator2.getScript().visit(fileCommandsVisitor);
				} else {
					/*
					 * If both lines do not have 40% commanlity then compare
					 * each with empty line so that they are not aligned to each
					 * other in final diff instead they show up on separate
					 * lines.
					 */
					StringsComparator leftComparator = new StringsComparator(
							left, "");
					leftComparator.getScript().visit(fileCommandsVisitor);
					StringsComparator rightComparator = new StringsComparator(
							"", right);
					rightComparator.getScript().visit(fileCommandsVisitor);
				}

				// if (comparator3.getScript().getLCSLength() > (Math.max(
				// middle.length(), right.length()) * 0.4)) {
				// /*
				// * If both lines have atleast 40% commonality then only
				// * compare with each other so that they are aligned with
				// * each other in final diff HTML.
				// */
				// comparator3.getScript().visit(fileCommandsVisitor);
				// } else {
				// /*
				// * If both lines do not have 40% commanlity then compare
				// * each with empty line so that they are not aligned to each
				// * other in final diff instead they show up on separate
				// * lines.
				// */
				// StringsComparator middleComparator = new StringsComparator(
				// middle, "");
				// middleComparator.getScript().visit(fileCommandsVisitor);
				// StringsComparator rightComparator = new StringsComparator(
				// "", right);
				// rightComparator.getScript().visit(fileCommandsVisitor);
				// }
			}

			fileCommandsVisitor.generateDiff(3);
			leftLines = fileCommandsVisitor.getLeftLines();
			middleLines = fileCommandsVisitor.getMiddleLines();
			rightLines = fileCommandsVisitor.getRightLines();
		}
	}

	@Override
	public List<IDiffLine> getLeftLines() {
		return leftLines;
	}

	@Override
	public List<IDiffLine> getRightLines() {
		return rightLines;
	}

	@Override
	public List<IDiffLine> getMiddleLines() {
		return middleLines;
	}
}

/*
 * Custom visitor for file comparison which stores comparison.
 */
class FileCommandVisitor implements CommandVisitor<Character> {

	// Spans with red & green highlights to put highlighted characters in HTML
	private static final String DELETION = "RED";
	private static final String INSERTION = "GREEN";
	private static final String UNCHANGED = "WHITE";

	private List<IDiffChar> leftFile = new ArrayList<IDiffChar>();
	private List<IDiffChar> rightFile = new ArrayList<IDiffChar>();
	private List<IDiffChar> middleFile = new ArrayList<IDiffChar>();

	private List<IDiffLine> leftLines = new ArrayList<IDiffLine>();
	private List<IDiffLine> rightLines = new ArrayList<IDiffLine>();
	private List<IDiffLine> middleLines = new ArrayList<IDiffLine>();

	/**
	 * der betroffene Buchstabe Wird aufgerufen wenn der Buchstabe c in beiden
	 * Dateien an der gleichen Stelle vorhanden ist
	 * 
	 * @param c
	 */
	@Override
	public void visitKeepCommand(Character c) {

		IDiffChar toAppend = new IDiffCharImpl();
		toAppend.setCharColor(UNCHANGED);
		toAppend.setCurrentChar(c);
		leftFile.add(toAppend);
		middleFile.add(toAppend);
		rightFile.add(toAppend);

	}

	/**
	 * der betroffene Buchstabe Wird aufgerufen wenn der Buchstabe c in einer
	 * Vergleichsdatei pr�sent ist aber nicht in der Referenz
	 * 
	 * @param c
	 */
	@Override
	public void visitInsertCommand(Character c) {
		IDiffChar toAppend = new IDiffCharImpl();
		toAppend.setCharColor(INSERTION);
		toAppend.setCurrentChar(c);
		middleFile.add(toAppend);
		rightFile.add(toAppend);

	}

	/**
	 * der betroffene Buchstabe Wird aufgerufen wenn der Buchstabe c in einer
	 * Referenzdatei pr�sent ist aber nicht in der Vergleichsdatei
	 * 
	 * @param c
	 * 
	 */
	@Override
	public void visitDeleteCommand(Character c) {
		IDiffChar toAppend = new IDiffCharImpl();
		toAppend.setCharColor(DELETION);
		toAppend.setCurrentChar(c);
		leftFile.add(toAppend);
	}

	/**
	 * Erstellt aus dem ganzheitlichen Diff eines jeweiligen Files eine
	 * zugeh�rige zeilenweise Speicherm�glichkeit
	 * 
	 * @param file
	 *            Die Datei dessen Diff verarbeitet werden soll
	 * @param lines
	 *            Die Liste deren Zeilen bef�llt werden sollen
	 */
	private void writeToDiffLines(List<IDiffChar> file, List<IDiffLine> lines) {
		String diffedFile = "";
		for (IDiffChar c : file) {
			diffedFile += c.getCurrentChar().toString();
		}
		String[] getLinesFromDiffedFile = diffedFile.split("\n");
		String[] linesAsStrings = new String[getLinesFromDiffedFile.length];
		for (int i = 0; i < linesAsStrings.length; i++) {
			linesAsStrings[i] = getLinesFromDiffedFile[i] + "\n";

		}

		int stringIndex = 0;
		for (int i = 0; i < linesAsStrings.length; i++) {
			IDiffLine diffLine = new IDiffLineImpl();
			List<IDiffChar> temp = file.subList(stringIndex, stringIndex
					+ linesAsStrings[i].length());
			diffLine.setDiffedLine(temp);
			stringIndex = stringIndex + linesAsStrings[i].length();
			lines.add(diffLine);
		}

	}

	/**
	 * Fertigt die Funktionsaufrufe des FileCommandVisitors in der richtigen
	 * Reihenfolge ab
	 * 
	 * @param numberOfFiles
	 *            die Anzahl der �bergebenen Dateien f�r die Diff-Bildung
	 */
	public void generateDiff(int numberOfFiles) {

		if (numberOfFiles == 2) {
			writeToDiffLines(leftFile, leftLines);
			writeToDiffLines(rightFile, rightLines);
			removeLastLineBreak(leftLines);
			removeLastLineBreak(rightLines);
		} else if (numberOfFiles == 3) {
			writeToDiffLines(leftFile, leftLines);
			writeToDiffLines(middleFile, middleLines);
			writeToDiffLines(rightFile, rightLines);
			correctMiddleAndRight();
			mergeDiffedLines();
			removeLastLineBreak(leftLines);
			removeLastLineBreak(middleLines);
			removeLastLineBreak(rightLines);
		}

	}

	/**
	 * Entfernt doppelte Zeilen aus der mittleren und rechten Datei
	 */
	private void correctMiddleAndRight() {
		Iterator<IDiffLine> itr = middleLines.iterator();
		int i = 0;
		while (itr.hasNext()) {
			itr.next();
			if (i % 2 != 0) {
				itr.remove();
			}
			i++;
		}

		itr = rightLines.iterator();
		i = 0;
		while (itr.hasNext()) {
			itr.next();
			if (i % 2 == 0) {
				itr.remove();
			}
			i++;
		}

	}

	/*
	 * Analysiert die Zeilen der Referenzdatei. Da die Unterschiede zur
	 * mittleren und zur rechten Datei unterschiedlich sein koennen, muessen sie
	 * hier gemerged werden.
	 */
	private void mergeDiffedLines() {
		List<IDiffChar> mergedLine;
		List<IDiffLine> mergedFile = new ArrayList<IDiffLine>();

		for (int i = 1; i < leftLines.size(); i += 2) {
			List<IDiffChar> upper = leftLines.get(i - 1).getDiffedLine();
			List<IDiffChar> lower = leftLines.get(i).getDiffedLine();
			mergedLine = new ArrayList<IDiffChar>();

			for (int j = 0; j < upper.size(); j++) {
				if (upper.get(j).getCharColor()
						.equals(lower.get(j).getCharColor())) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), upper.get(j).getCharColor()));
				}
				if (upper.get(j).getCharColor().equals("RED")
						&& lower.get(j).getCharColor().equals("WHITE")) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), "PINK"));
				}
				if (upper.get(j).getCharColor().equals("WHITE")
						&& lower.get(j).getCharColor().equals("RED")) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), "PINK"));
				}
			}
			IDiffLine line = new IDiffLineImpl();
			line.setDiffedLine(mergedLine);
			mergedFile.add(line);
		}
		leftLines = mergedFile;
	}

	/**
	 * Der letzten Zeile jeder Datei wird in writeToDiffLines ein \n zuviel
	 * hinzugefuegt. Dieses wird hier entfernt
	 * 
	 * @param lines
	 */
	private void removeLastLineBreak(List<IDiffLine> lines) {
		IDiffLine line = new IDiffLineImpl();
		List<IDiffChar> lineAsChars = new ArrayList<IDiffChar>();
		lineAsChars = lines.get(lines.size() - 1).getDiffedLine();
		line.setDiffedLine(lineAsChars.subList(0, lineAsChars.size() - 1));
		lines.remove(lines.size() - 1);
		lines.add(line);
	}

	public List<IDiffLine> getLeftLines() {
		return leftLines;
	}

	public List<IDiffLine> getRightLines() {
		return rightLines;
	}

	public List<IDiffLine> getMiddleLines() {
		return middleLines;
	}

}
