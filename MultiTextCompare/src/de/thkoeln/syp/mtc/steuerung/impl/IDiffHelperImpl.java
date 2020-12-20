package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;


public class IDiffHelperImpl {
	

	public static void main(String[] args) throws IOException {
		// Read both files with line iterator.
		LineIterator file1 = FileUtils.lineIterator(new File("F:\\a.txt"));
		LineIterator file2 = FileUtils.lineIterator(new File("F:\\b.txt"));
 
		// Initialize visitor.
		FileCommandsVisitor fileCommandsVisitor = new FileCommandsVisitor();
		int lineNum = 0;
 
		// Read file line by line so that comparison can be done line by line.
		while (file1.hasNext() || file2.hasNext()) {
			/*
			 * In case both files have different number of lines, fill in with empty
			 * strings. Also append newline char at end so next line comparison moves to
			 * next line.
			 */
			lineNum++;
			String left = lineNum + "  " + (file1.hasNext() ? file1.nextLine() : "") + "\n";
			String right = lineNum + "  " + (file2.hasNext() ? file2.nextLine() : "") + "\n";
 
			// Prepare diff comparator with lines from both files.
			StringsComparator comparator = new StringsComparator(left, right);
 
			if (comparator.getScript().getLCSLength() > (Math.max(left.length(), right.length()) * 0.4)) {
				/*
				 * If both lines have atleast 40% commonality then only compare with each other
				 * so that they are aligned with each other in final diff HTML.
				 */
				comparator.getScript().visit(fileCommandsVisitor);
			} else {
				/*
				 * If both lines do not have 40% commanlity then compare each with empty line so
				 * that they are not aligned to each other in final diff instead they show up on
				 * separate lines.
				 */
				StringsComparator leftComparator = new StringsComparator(left, "\n");
				leftComparator.getScript().visit(fileCommandsVisitor);
				StringsComparator rightComparator = new StringsComparator("\n", right);
				rightComparator.getScript().visit(fileCommandsVisitor);
			}
		}
 
		fileCommandsVisitor.generateHTML();
	}
}

class MyCommandsVisitor implements CommandVisitor<Character> {
	 
	String left = "";
	String right = "";
 
	@Override
	public void visitKeepCommand(Character c) {
		// Character is present in both files.	
		left = left + c;
		right = right + c;
	}
 
	@Override
	public void visitInsertCommand(Character c) {
		/*
		 * Character is present in right file but not in left. Method name
		 * 'InsertCommand' means, c need to insert it into left to match right.
		 */
		right = right + "(" + c + ")";
	}
 
	@Override
	public void visitDeleteCommand(Character c) {
		/*
		 * Character is present in left file but not right. Method name 'DeleteCommand'
		 * means, c need to be deleted from left to match right.
		 */
		left = left + "{" + c + "}";
	}
}

/*
 * Custom visitor for file comparison which stores comparison & also generates
 * HTML in the end.
 */
class FileCommandsVisitor implements CommandVisitor<Character> {

 
	// Spans with red & green highlights to put highlighted characters in HTML
	private static final String DELETION = "<span style=\"background-color: #FB504B\">${text}</span>";
	private static final String INSERTION = "<span style=\"background-color: #45EA85\">${text}</span>";
	private static final String UNCHANGED = "<span style=\"background-color: #FFFFFF\">${text}</span>";
 
	private String left = "";
	private String right = "";
 
	@Override
	public void visitKeepCommand(Character c) {
		// For new line use <br/> so that in HTML also it shows on next line.
		String toAppend = "\n".equals("" + c) ? "<br/>" : "" + c;
		// KeepCommand means c present in both left & right. So add this to both without
		// any
		// highlight.
		left = left + UNCHANGED.replace("${text}", "" + toAppend);
		right = right + UNCHANGED.replace("${text}", "" + toAppend);
	}
 
	@Override
	public void visitInsertCommand(Character c) {
		// For new line use <br/> so that in HTML also it shows on next line.
		String toAppend = "\n".equals("" + c) ? "<br/>" : "" + c;
		// InsertCommand means character is present in right file but not in left. Show
		// with green highlight on right.
		right = right + INSERTION.replace("${text}", "" + toAppend);
	}
 
	@Override
	public void visitDeleteCommand(Character c) {
		// For new line use <br/> so that in HTML also it shows on next line.
		String toAppend = "\n".equals("" + c) ? "<br/>" : "" + c;
		// DeleteCommand means character is present in left file but not in right. Show
		// with red highlight on left.
		left = left + DELETION.replace("${text}", "" + toAppend);
	}
	
	public void generateHTML() throws IOException {
 
		// Get template & replace placeholders with left & right variables with actual
		// comparison
		String template = FileUtils.readFileToString(new File(System.getProperty("user.dir")
				+ File.separator + "/htmlTemplates/difftemplateTwoFiles.html"), "utf-8");
		
		
		String out1 = template.replace("${left}", left);
		String output = out1.replace("${right}", right);
		// Write file to disk.
		FileUtils.write(new File(System.getProperty("user.dir") + File.separator + "/out/displayDiff.html"), output, "utf-8");
		System.out.println("HTML diff generated.");
	}
	
}

