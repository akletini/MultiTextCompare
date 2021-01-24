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

	@Override
	public void computeDisplayDiff(File[] files) throws IOException {
		// Read both files with line iterator.
		if (files.length == 2) {
			LineIterator file1 = FileUtils.lineIterator(files[0]);
			LineIterator file2 = FileUtils.lineIterator(files[1]);

			// Initialize visitor.
			FileCommandsVisitor fileCommandsVisitor = new FileCommandsVisitor();
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
					 * each other in final diff HTML.
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
			fileCommandsVisitor.printLists(2);
			leftLines = fileCommandsVisitor.getLeftLines();
			rightLines = fileCommandsVisitor.getRightLines();
			// fileCommandsVisitor.generateHTML(files, mode);
		} else {
			final LineIterator file1 = FileUtils.lineIterator(files[0]);
			final LineIterator file2 = FileUtils.lineIterator(files[1]);
			final LineIterator file3 = FileUtils.lineIterator(files[2]);

			// Initialize visitor.
			FileCommandsVisitor fileCommandsVisitor = new FileCommandsVisitor();
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
					 * each other in final diff HTML.
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
					 * each other in final diff HTML.
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

			fileCommandsVisitor.printLists(3);
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
 * Custom visitor for file comparison which stores comparison & also generates
 * HTML in the end.
 */
class FileCommandsVisitor implements CommandVisitor<Character> {

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
	 * Modes: BOTH: Verdoppelt die Zeilen und zeigt abwechselnd die Diffs
	 * Links-Mitte, Links-Rechts an MID: Zeigt nur die Diffs zum mittleren Text
	 * an RIGHT: Zeigt nur die Diffs zum rechten Text an
	 */

	@Override
	public void visitKeepCommand(Character c) {

		// For new line use <br/> so that in HTML also it shows on next line.
		IDiffChar toAppend = new IDiffCharImpl();
		toAppend.setCharColor(UNCHANGED);
		toAppend.setCurrentChar(c);

		// KeepCommand means c present in both left & right. So add this to both
		// without
		// any
		// highlight.

		leftFile.add(toAppend);
		middleFile.add(toAppend);
		rightFile.add(toAppend);

	}

	@Override
	public void visitInsertCommand(Character c) {
		IDiffChar toAppend = new IDiffCharImpl();
		toAppend.setCharColor(INSERTION);
		toAppend.setCurrentChar(c);

		// InsertCommand means character is present in right file but not in
		// left. Show
		// with green highlight on right.
		middleFile.add(toAppend);
		rightFile.add(toAppend);

	}

	@Override
	public void visitDeleteCommand(Character c) {
		IDiffChar toAppend = new IDiffCharImpl();
		toAppend.setCharColor(DELETION);
		toAppend.setCurrentChar(c);
		// DeleteCommand means character is present in left file but not in
		// right. Show
		// with red highlight on left.
		leftFile.add(toAppend);
	}

	public void writeToDiffLines(List<IDiffChar> file, List<IDiffLine> lines) {
		String diffedFile = "";
		for (IDiffChar c : file) {
			diffedFile += c.getCurrentChar().toString();
		}
		String[] getLinesFromDiffedFile = diffedFile.split("\n");
		String[] linesAsStrings = new String[getLinesFromDiffedFile.length];
		for (int i = 0; i < linesAsStrings.length; i++) {
			// if (i < linesAsStrings.length - 1)
			linesAsStrings[i] = getLinesFromDiffedFile[i] + "\n";
			// else {
			// linesAsStrings[i] = getLinesFromDiffedFile[i];
			// }
		}

		int stringIndex = 0;
		for (int i = 0; i < linesAsStrings.length; i++) {
			IDiffLine diffLine = new IDiffLineImpl();
			diffLine.setDiffedLine(file.subList(stringIndex, stringIndex
					+ linesAsStrings[i].length()));
			stringIndex = stringIndex + linesAsStrings[i].length();
			lines.add(diffLine);
		}

	}

	public void printLists(int numberOfFiles) {

		if (numberOfFiles == 2) {
			writeToDiffLines(leftFile, leftLines);
			writeToDiffLines(rightFile, rightLines);
		} else if (numberOfFiles == 3) {
			writeToDiffLines(leftFile, leftLines);
			writeToDiffLines(middleFile, middleLines);
			writeToDiffLines(rightFile, rightLines);
			correctMiddleAndRight();
			mergeDiffedLines();
		}

	}

	public void correctMiddleAndRight() {
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

	public void mergeDiffedLines() {
		Iterator<IDiffLine> itr = leftLines.iterator();
		List<IDiffChar> mergedLine;
		List<IDiffLine> mergedFile = new ArrayList<IDiffLine>();
		
		for (int i = 1; i < leftLines.size(); i += 2) {
			List<IDiffChar> upper = leftLines.get(i - 1).getDiffedLine();
			List<IDiffChar> lower = leftLines.get(i).getDiffedLine();
			mergedLine = new ArrayList<IDiffChar>();

			for (int j = 0; j < upper.size(); j++) {
				if (upper.get(j).getCharColor()
						.equals(lower.get(j).getCharColor())) {
					mergedLine.add(new IDiffCharImpl(upper.get(j).getCurrentChar(),
							upper.get(j).getCharColor()));
				}
				if(upper.get(j).getCharColor().equals("RED") && lower.get(j).getCharColor().equals("WHITE")){
					mergedLine.add(new IDiffCharImpl(upper.get(j).getCurrentChar(),
							"PINK"));
				}
				if(upper.get(j).getCharColor().equals("WHITE") && lower.get(j).getCharColor().equals("RED")){
					mergedLine.add(new IDiffCharImpl(upper.get(j).getCurrentChar(),
							"PINK"));
				}
			}
			IDiffLine line = new IDiffLineImpl();
			line.setDiffedLine(mergedLine);
			mergedFile.add(line);
		}
		leftLines = mergedFile;
	}

	public void printDiffLineList() {
		for (IDiffLine c : leftLines) {

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

	// public void generateHTML(File[] files, String mode) throws IOException {
	//
	// // Get template & replace placeholders with left & right variables with
	// // actual
	// // comparison
	// String template = null;
	// if (files.length == 2) {
	// template = FileUtils.readFileToString(
	// new File(System.getProperty("user.dir") + File.separator
	// + "/htmlTemplates/difftemplateTwoFiles.html"),
	// "utf-8");
	// String file1 = template.replace("${fileName1}",
	// files[0].getAbsolutePath());
	// String file2 = file1.replace("${fileName2}",
	// files[1].getAbsolutePath());
	// String out1 = file2.replace("${left}", left);
	// String output = out1.replace("${right}", right);
	// // Write file to disk.
	// FileUtils.write(new File(System.getProperty("user.dir")
	// + File.separator + "/out/displayDiff.html"), output,
	// "utf-8");
	//
	// } else {
	// template = FileUtils.readFileToString(
	// new File(System.getProperty("user.dir") + File.separator
	// + "/htmlTemplates/difftemplateThreeFiles.html"),
	// "utf-8");
	// String[] leftLines = left.split("<br/>");
	// String[] midLines = middle.split("<br/>");
	// String[] rightLines = middle.split("<br/>");
	// String outputLeft = "", outputMiddle = "", outputRight = "";
	// for (int i = 0; i < midLines.length; i++) {
	// if (i % 2 == 0) {
	// outputMiddle += midLines[i] + "<br/>";
	// }
	// }
	// for (int i = 0; i < rightLines.length; i++) {
	// if (i % 2 != 0) {
	// outputRight += rightLines[i] + "<br/>";
	// }
	// }
	//
	// if (mode.equals("MID")) {
	// for (int i = 0; i < leftLines.length; i++) {
	// if (i % 2 == 0) {
	// outputLeft += leftLines[i] + "<br/>";
	// }
	// }
	// } else if (mode.equals("RIGHT")) {
	// for (int i = 0; i < leftLines.length; i++) {
	// if (i % 2 != 0) {
	// outputLeft += leftLines[i] + "<br/>";
	// }
	// }
	// } else {
	// outputLeft = left;
	// }
	//
	// String file1 = template.replace("${fileName1}",
	// files[0].getAbsolutePath());
	// String file2 = file1.replace("${fileName2}",
	// files[1].getAbsolutePath());
	// String file3 = file2.replace("${fileName3}",
	// files[2].getAbsolutePath());
	// String out1 = file3.replace("${left}", outputLeft);
	// String out2 = out1.replace("${middle}", outputMiddle);
	// String output = out2.replace("${right}", outputRight);
	// // Write file to disk.
	// Document doc = Jsoup.parse(output, "utf-8");
	// checkIfEqual(doc);
	// String cleanHTML = doc.html();
	// FileUtils.write(new File(System.getProperty("user.dir")
	// + File.separator + "/out/displayDiff.html"), cleanHTML,
	// "utf-8");
	//
	// }
	//
	// System.out.println("HTML diff generated.");
	// }

}
