package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class IDiffHelperImpl implements IDiffHelper {

	private List<IDiffLine> leftLines;
	private List<IDiffLine> middleLines;
	private List<IDiffLine> rightLines;

	private final double minCommonality = 0.4;
	
	private IFileImporter fileImporter;

	public IDiffHelperImpl() {
		leftLines = new ArrayList<IDiffLine>();
		middleLines = new ArrayList<IDiffLine>();
		rightLines = new ArrayList<IDiffLine>();
	}

	/**
	 * Die Methode ermittelt die Differenzen zwischen den �bergebenen Dateien
	 * und markiert diese durch die Klasse FileCommandsVisitor
	 * 
	 * @param files
	 *            Die Dateien, deren Diff gebildet werden soll
	 * 
	 */
	@Override
	public void computeDisplayDiff(File[] files) throws IOException {
		int maxLength = getMaxLengthOfFiles(files);
		int maxLineSize = fileImporter.getConfig().getMaxLineLength();
		// Read both files with line iterator.
		if (files.length == 2) {
			LineIterator file1 = FileUtils.lineIterator(files[0], "UTF-8");
			LineIterator file2 = FileUtils.lineIterator(files[1], "UTF-8");

			// Initialize visitor.
			FileCommandVisitor fileCommandVisitor = new FileCommandVisitor();
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
				int numofSpaces = numberOfSpaces(lineNum, maxLength);
				String spaces = "";
				for(int i = 0; i < numofSpaces + 1; i++){
					spaces += " ";
				}

				String left = (file1.hasNext() ? file1.nextLine() : "") + "\n";
				String right = (file2.hasNext() ? file2.nextLine() : "") + "\n";
				
				int lineSize = left.length() + right.length();

				// Prepare diff comparator with lines from both files.
				StringsComparator comparator = new StringsComparator(left,
						right);

				if ( lineSize < maxLineSize && (comparator.getScript().getLCSLength() > (Math.max(
						left.length(), right.length()) * minCommonality))) {
					/*
					 * If both lines have atleast 40% commonality then only
					 * compare with each other so that they are aligned with
					 * each other in final diff.
					 */
					left = lineNum + spaces + left;
					right = lineNum + spaces + right;
					comparator = new StringsComparator(left, right);
					comparator.getScript().visit(fileCommandVisitor);
				} else {
					/*
					 * If both lines do not have 40% commanlity then compare
					 * each with empty line so that they are not aligned to each
					 * other in final diff instead they show up on separate
					 * lines.
					 */
					left = lineNum + spaces + left;
					right = lineNum + spaces + right;
					StringsComparator leftComparator = new StringsComparator(
							left, "");
					leftComparator.getScript().visit(fileCommandVisitor);
					StringsComparator rightComparator = new StringsComparator(
							"", right);
					rightComparator.getScript().visit(fileCommandVisitor);
				}
			}
			fileCommandVisitor.generatePrimaryDiff(2);
			leftLines = fileCommandVisitor.getLeftLines();
			rightLines = fileCommandVisitor.getRightLines();
		} else {
			final LineIterator file1 = FileUtils
					.lineIterator(files[0], "UTF-8");
			final LineIterator file2 = FileUtils
					.lineIterator(files[1], "UTF-8");
			final LineIterator file3 = FileUtils
					.lineIterator(files[2], "UTF-8");

			// Initialize visitor.
			FileCommandVisitor fileCommandVisitor = new FileCommandVisitor();
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
				int numofSpaces = numberOfSpaces(lineNum, maxLength);
				String spaces = "";
				for(int i = 0; i < numofSpaces + 1; i++){
					spaces += " ";
				}

				String left = (file1.hasNext() ? file1.nextLine() : "") + "\n";
				String middle = (file2.hasNext() ? file2.nextLine() : "")
						+ "\n";
				String right = (file3.hasNext() ? file3.nextLine() : "") + "\n";
				
				int lineSizeLeftMid = left.length() + middle.length();
				int lineSizeLeftRight = left.length() + right.length();
				// Prepare diff comparator with lines from both files.
				StringsComparator comparator1 = new StringsComparator(left,
						middle);
				StringsComparator comparator2 = new StringsComparator(left,
						right);

				if (lineSizeLeftMid < maxLineSize && (comparator1.getScript().getLCSLength() > (Math.max(
						left.length(), middle.length()) * minCommonality))) {
					/*
					 * If both lines have atleast 40% commonality then only
					 * compare with each other so that they are aligned with
					 * each other in final diff.
					 */
					left = lineNum + spaces + left;
					middle = lineNum + spaces + middle;

					comparator1 = new StringsComparator(left, middle);
					comparator1.getScript().visit(fileCommandVisitor);

				} else {
					/*
					 * If both lines do not have 40% commanlity then compare
					 * each with empty line so that they are not aligned to each
					 * other in final diff instead they show up on separate
					 * lines.
					 */
					left = lineNum + spaces + left;
					middle = lineNum + spaces + middle;

					StringsComparator leftComparator = new StringsComparator(
							left, "");
					leftComparator.getScript().visit(fileCommandVisitor);
					StringsComparator middleComparator = new StringsComparator(
							"", middle);
					middleComparator.getScript().visit(fileCommandVisitor);

				}

				if (lineSizeLeftRight < maxLineSize && (comparator2.getScript().getLCSLength() > (Math.max(
						left.length(), right.length()) * minCommonality))) {
					/*
					 * If both lines have atleast 40% commonality then only
					 * compare with each other so that they are aligned with
					 * each other in final diff.
					 */
					right = lineNum + spaces + right;

					comparator2 = new StringsComparator(left, right);
					comparator2.getScript().visit(fileCommandVisitor);
				} else {
					/*
					 * If both lines do not have 40% commanlity then compare
					 * each with empty line so that they are not aligned to each
					 * other in final diff instead they show up on separate
					 * lines.
					 */
					right = lineNum + spaces + right;

					StringsComparator leftComparator = new StringsComparator(
							left, "");
					leftComparator.getScript().visit(fileCommandVisitor);
					StringsComparator rightComparator = new StringsComparator(
							"", right);
					rightComparator.getScript().visit(fileCommandVisitor);
				}

			}

			fileCommandVisitor.generatePrimaryDiff(3);
			fileCommandVisitor.setDurchgang(2);
			generateOuterDiff(files, maxLength ,fileCommandVisitor);
			fileCommandVisitor.generateFinalDiff();

			leftLines = fileCommandVisitor.getLeftLines();
			middleLines = fileCommandVisitor.getMiddleLines();
			rightLines = fileCommandVisitor.getRightLines();
		}
	}

	/**
	 * Erstellt Diff fuer mittleres und rechtes File
	 * 
	 * @param files
	 *            ausgewaehlte Dateien
	 * @param fileCommandVisitor
	 *            aktuelle Instanz des fileCommandVisitors auf dem die primaere
	 *            Diff gebildet wurde
	 * @throws IOException
	 */
	private void generateOuterDiff(File[] files, int maxLength,
			FileCommandVisitor fileCommandVisitor) throws IOException {
		LineIterator file1 = FileUtils.lineIterator(files[1], "UTF-8");
		LineIterator file2 = FileUtils.lineIterator(files[2], "UTF-8");

		// Initialize visitor.
		int lineNum = 0;
		int maxLineSize = fileImporter.getConfig().getMaxLineLength();
		// Read file line by line so that comparison can be done line by
		// line.
		while (file1.hasNext() || file2.hasNext()
				|| lineNum < fileCommandVisitor.getLeftLines().size()) {
			/*
			 * In case both files have different number of lines, fill in with
			 * empty strings. Also append newline char at end so next line
			 * comparison moves to next line.
			 */
			lineNum++;
			int numofSpaces = numberOfSpaces(lineNum, maxLength);
			String spaces = "";
			for(int i = 0; i < numofSpaces + 1; i++){
				spaces += " ";
			}
			String middle = (file1.hasNext() ? file1.nextLine() : "") + "\n";
			String right = (file2.hasNext() ? file2.nextLine() : "") + "\n";
			int lineSizeMidRight = middle.length() + right.length();
			// Prepare diff comparator with lines from both files.
			StringsComparator comparator = new StringsComparator(middle, right);

			if (lineSizeMidRight < maxLineSize && (comparator.getScript().getLCSLength() > (Math.max(
					middle.length(), right.length()) * minCommonality))) {
				/*
				 * If both lines have atleast 40% commonality then only compare
				 * with each other so that they are aligned with each other in
				 * final diff.
				 */
				right = lineNum + spaces + right;
				middle = lineNum + spaces + middle;
				comparator = new StringsComparator(middle, right);
				comparator.getScript().visit(fileCommandVisitor);
			} else {
				/*
				 * If both lines do not have 40% commanlity then compare each
				 * with empty line so that they are not aligned to each other in
				 * final diff instead they show up on separate lines.
				 */
				right = lineNum + spaces + right;
				middle = lineNum + spaces + middle;
				StringsComparator leftComparator = new StringsComparator(
						middle, "");
				leftComparator.getScript().visit(fileCommandVisitor);
				StringsComparator rightComparator = new StringsComparator("",
						right);
				rightComparator.getScript().visit(fileCommandVisitor);
			}
		}

	}
	
	private int getMaxLengthOfFiles(File[] files){
		Integer[] fileSizes = new Integer[files.length];
		
		for(int i = 0; i < files.length; i++){
			 List<String> fileStream;
			try {
				fileStream = Files.readAllLines(Paths.get(files[i].getAbsolutePath()), StandardCharsets.UTF_8);
				fileSizes[i] = fileStream.size();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			    
		}
		return Collections.max(Arrays.asList(fileSizes));
	}
	
	private int numberOfSpaces(int current, int max){
		int numOfDigitsCurrent = String.valueOf(current).length();
		int numOfDigitsMax = String.valueOf(max).length();
		
		return numOfDigitsMax - numOfDigitsCurrent;
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
	
	@Override
	public void setFileImporter(IFileImporter fileImporter){
		this.fileImporter = fileImporter;
	}
}

/*
 * Custom visitor for file comparison which stores comparison.
 */
class FileCommandVisitor implements CommandVisitor<Character> {

	private static final String DELETION = "RED";
	private static final String INSERTION = "GREEN";
	private static final String UNCHANGED = "WHITE";

	private static final String DIFFTOLEFT = "CYAN";
	private static final String DIFFTOMIDDLE = "ORANGE";
	private static final String DIFFTORIGHT = "GREEN";
	private static final String DIFFTOALL = "RED";

	private List<IDiffChar> leftFile;
	private List<IDiffChar> rightFile;
	private List<IDiffChar> middleFile;

	private List<IDiffChar> middleFile2;
	private List<IDiffChar> rightFile2;

	private List<IDiffLine> leftLines;
	private List<IDiffLine> rightLines;
	private List<IDiffLine> middleLines;

	private List<IDiffLine> rightLines2;
	private List<IDiffLine> middleLines2;

	private int durchgang;

	public FileCommandVisitor() {
		leftFile = new ArrayList<IDiffChar>();
		rightFile = new ArrayList<IDiffChar>();
		middleFile = new ArrayList<IDiffChar>();

		leftLines = new ArrayList<IDiffLine>();
		rightLines = new ArrayList<IDiffLine>();
		middleLines = new ArrayList<IDiffLine>();

		// fuer den Vergleich vom mittleren und rechten File
		middleFile2 = new ArrayList<IDiffChar>();
		rightFile2 = new ArrayList<IDiffChar>();

		rightLines2 = new ArrayList<IDiffLine>();
		middleLines2 = new ArrayList<IDiffLine>();

		durchgang = 1;
	}

	/**
	 * der betroffene Buchstabe Wird aufgerufen wenn der Buchstabe c in beiden
	 * Dateien an der gleichen Stelle vorhanden ist
	 * 
	 * @param c
	 *            der "gediffte" Buchstabe
	 */
	@Override
	public void visitKeepCommand(Character c) {

		IDiffChar toAppendLeft = new IDiffCharImpl(c, UNCHANGED);
		IDiffChar toAppendMiddle = new IDiffCharImpl(c, UNCHANGED);
		IDiffChar toAppendRight = new IDiffCharImpl(c, UNCHANGED);
		IDiffChar toAppendMiddle2 = new IDiffCharImpl(c, UNCHANGED);
		IDiffChar toAppendRight2 = new IDiffCharImpl(c, UNCHANGED);

		if (durchgang == 1) {
			leftFile.add(toAppendLeft);
			middleFile.add(toAppendMiddle);
			rightFile.add(toAppendRight);
		} else {
			middleFile2.add(toAppendMiddle2);
			rightFile2.add(toAppendRight2);
		}

	}

	/**
	 * der betroffene Buchstabe Wird aufgerufen wenn der Buchstabe c in einer
	 * Vergleichsdatei pr�sent ist aber nicht in der Referenz
	 * 
	 * @param c
	 *            der "gediffte" Buchstabe
	 */
	@Override
	public void visitInsertCommand(Character c) {
		IDiffChar toAppendMiddle = new IDiffCharImpl(c, INSERTION);
		IDiffChar toAppendRight = new IDiffCharImpl(c, INSERTION);
		IDiffChar toAppendRight2 = new IDiffCharImpl(c, INSERTION);

		if (durchgang == 1) {
			middleFile.add(toAppendMiddle);
			rightFile.add(toAppendRight);
		} else {
			rightFile2.add(toAppendRight2);
		}

	}

	/**
	 * der betroffene Buchstabe Wird aufgerufen wenn der Buchstabe c in einer
	 * Referenzdatei pr�sent ist aber nicht in der Vergleichsdatei
	 * 
	 * @param c
	 *            der "gediffte" Buchstabe
	 * 
	 */
	@Override
	public void visitDeleteCommand(Character c) {
		IDiffChar toAppendLeft = new IDiffCharImpl(c, DELETION);
		IDiffChar toAppendMiddle2 = new IDiffCharImpl(c, DELETION);

		if (durchgang == 1) {
			leftFile.add(toAppendLeft);
		} else {
			middleFile2.add(toAppendMiddle2);

		}
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
	protected void generatePrimaryDiff(int numberOfFiles) {

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
			normalizeEmptyLines();
			removeLastLineBreak(leftLines);
			removeLastLineBreak(middleLines);
			removeLastLineBreak(rightLines);
		}

	}

	/**
	 * F�hrt Operationen aus damit der Vergleich der mittleren und rechten Datei
	 * dargestellt werden k�nnen
	 */
	protected void generateFinalDiff() {
		writeToDiffLines(middleFile2, middleLines2);
		writeToDiffLines(rightFile2, rightLines2);
		mergeOuterDiff();
		removeWrongTriggersOnShortLines();
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
	 * Analysiert die Zeilen der linken Datei. Da die Unterschiede zur mittleren
	 * und zur rechten Datei unterschiedlich sein koennen, muessen sie hier
	 * gemerged werden.
	 */
	private void mergeDiffedLines() {
		List<IDiffChar> mergedLine;
		List<IDiffLine> mergedFile = new ArrayList<IDiffLine>();

		for (int i = 1; i < leftLines.size(); i += 2) {
			List<IDiffChar> upper = leftLines.get(i - 1).getDiffedLine();
			List<IDiffChar> lower = leftLines.get(i).getDiffedLine();
			mergedLine = new ArrayList<IDiffChar>();

			for (int j = 0; j < upper.size(); j++) {
				// unver�ndert
				if (upper.get(j).getCharColor().equals("WHITE")
						&& lower.get(j).getCharColor().equals("WHITE")) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), UNCHANGED));
				}
				// diff zur Mitte
				if (upper.get(j).getCharColor().equals("RED")
						&& lower.get(j).getCharColor().equals("WHITE")) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), DIFFTOMIDDLE));
				}
				// diff zu rechts
				if (upper.get(j).getCharColor().equals("WHITE")
						&& lower.get(j).getCharColor().equals("RED")) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), DIFFTORIGHT));
				}
				// diff zu beiden
				if (upper.get(j).getCharColor().equals("RED")
						&& lower.get(j).getCharColor().equals("RED")) {
					mergedLine.add(new IDiffCharImpl(upper.get(j)
							.getCurrentChar(), DIFFTOALL));
				}
			}
			IDiffLine line = new IDiffLineImpl();
			line.setDiffedLine(mergedLine);
			mergedFile.add(line);
		}
		leftLines = mergedFile;
	}

	/**
	 * Abgleich der mittleren und rechten Datei
	 */
	private void mergeOuterDiff() {
		List<IDiffChar> mergedLine;
		List<IDiffLine> mergedFileMiddle = new ArrayList<IDiffLine>();
		List<IDiffLine> mergedFileRight = new ArrayList<IDiffLine>();

		for (int i = 0; i < middleLines.size(); i++) {
			mergedLine = new ArrayList<IDiffChar>();
			List<IDiffChar> left = middleLines.get(i).getDiffedLine();
			List<IDiffChar> right = middleLines2.get(i).getDiffedLine();

			for (int j = 0; j < left.size(); j++) {
				if (left.get(j).getCharColor()
						.equals(right.get(j).getCharColor())) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), UNCHANGED));
				}
				// Diff zu beiden
				if (left.get(j).getCharColor().equals("GREEN")
						&& right.get(j).getCharColor().equals("RED")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), DIFFTOALL));
				}
				// Diff zu links
				if (left.get(j).getCharColor().equals("GREEN")
						&& right.get(j).getCharColor().equals("WHITE")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), DIFFTOLEFT));
				}
				// Diff zu rechts
				if (left.get(j).getCharColor().equals("WHITE")
						&& right.get(j).getCharColor().equals("RED")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), DIFFTORIGHT));
				}

			}
			IDiffLine line = new IDiffLineImpl();
			line.setDiffedLine(mergedLine);
			mergedFileMiddle.add(line);
		}
		middleLines = mergedFileMiddle;

		for (int i = 0; i < rightLines.size(); i++) {
			mergedLine = new ArrayList<IDiffChar>();
			List<IDiffChar> left = rightLines.get(i).getDiffedLine();
			List<IDiffChar> right = rightLines2.get(i).getDiffedLine();

			for (int j = 0; j < left.size(); j++) {
				if (left.get(j).getCharColor().equals("WHITE")
						&& right.get(j).getCharColor().equals("WHITE")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), UNCHANGED));
				}
				// Diff zur mitte
				if (left.get(j).getCharColor().equals("WHITE")
						&& right.get(j).getCharColor().equals("GREEN")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), DIFFTOMIDDLE));
				}
				// Diff zu links
				if (left.get(j).getCharColor().equals("GREEN")
						&& right.get(j).getCharColor().equals("WHITE")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), DIFFTOLEFT));
				}
				// Diff zu beiden
				if (left.get(j).getCharColor().equals("GREEN")
						&& right.get(j).getCharColor().equals("GREEN")) {
					mergedLine.add(new IDiffCharImpl(left.get(j)
							.getCurrentChar(), DIFFTOALL));
				}

			}
			IDiffLine line = new IDiffLineImpl();
			line.setDiffedLine(mergedLine);
			mergedFileRight.add(line);
		}

		rightLines = mergedFileRight;
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

	/**
	 * Behebt Verhalten, bei dem Leerzeilen falsch vom Algorithmus markiert
	 * werden. Prueft manuell ob in allen drei Dateien an der gleichen Zeile
	 * eine Leerzeile steht und faerbt diese korrekt ein
	 */
	private void normalizeEmptyLines() {
		List<Boolean> emptyLinesLeft = checkWholeLineEmpty(leftLines);
		List<Boolean> emptyLinesMiddle = checkWholeLineEmpty(middleLines);
		List<Boolean> emptyLinesRight = checkWholeLineEmpty(rightLines);
		for (int i = 0; i < leftLines.size(); i++) {
			if (emptyLinesLeft.get(i).booleanValue() == true
					&& emptyLinesMiddle.get(i).booleanValue() == true
					&& emptyLinesRight.get(i).booleanValue() == true) {
				for (int j = 0; j < leftLines.get(i).getDiffedLine().size(); j++) {
					leftLines.get(i).getDiffedLine().get(j)
							.setCharColor("WHITE");
					middleLines.get(i).getDiffedLine().get(j)
							.setCharColor("WHITE");
					rightLines.get(i).getDiffedLine().get(j)
							.setCharColor("WHITE");
				}
			}
		}
	}

	/**
	 * Enfernt die Zeilennummerierung und prueft ob der restliche String dem
	 * Rest des Praefixes gleich ist. Falls ja wird der Ergebnisliste true
	 * hinzugefuegt, sonst false
	 * 
	 * @param lines
	 *            Die zu untersuchenden Zeilen einer Datei
	 * @return Liste mit Eintraegen ob Zeilen leer sind oder nicht
	 */
	private List<Boolean> checkWholeLineEmpty(List<IDiffLine> lines) {
		// Check all lines
		String line = "";
		List<Boolean> isEmptyLine = new ArrayList<Boolean>();
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i).toString();
			if (line.replaceAll("[0-9]", "").equals("  \n")) {
				isEmptyLine.add(true);
			} else {
				isEmptyLine.add(false);
			}
		}
		return isEmptyLine;
	}

	/**
	 * Bei kurzen Zeilen werden die Indices falsch markiert. Diese Methode
	 * behebt den Fehler
	 */
	private void removeWrongTriggersOnShortLines() {
		String colorLeftIndex, colorMiddleIndex, colorRightIndex;

		for (int i = 0; i < leftLines.size(); i++) {
			int numberOfDigits = (int) (Math.log10(i + 1) + 1);
			// Check index coloration
			colorLeftIndex = leftLines.get(i).getDiffedLine().get(0)
					.getCharColor();
			colorMiddleIndex = middleLines.get(i).getDiffedLine().get(0)
					.getCharColor();
			colorRightIndex = rightLines.get(i).getDiffedLine().get(0)
					.getCharColor();

			if (colorLeftIndex.equals(DIFFTORIGHT)
					&& colorMiddleIndex.equals(UNCHANGED)
					&& colorRightIndex.equals(DIFFTOLEFT)) {

				for (int j = 0; j < numberOfDigits; j++) {
					leftLines.get(i).getDiffedLine().get(j)
							.setCharColor(UNCHANGED);
					middleLines.get(i).getDiffedLine().get(j)
							.setCharColor(UNCHANGED);
					rightLines.get(i).getDiffedLine().get(j)
							.setCharColor(UNCHANGED);
				}
			}

		}
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

	public void setDurchgang(int durchgang) {
		this.durchgang = durchgang;
	}

	public int getDurchgang() {
		return durchgang;
	}

}
