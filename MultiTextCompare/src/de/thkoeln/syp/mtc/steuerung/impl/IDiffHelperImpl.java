package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import extern.diff_match_patch;
import extern.diff_match_patch.Diff;
import extern.diff_match_patch.Patch;

public class IDiffHelperImpl {

	private File ref, vgl;
	private LinkedList<Diff> diff;
	private static String oldText = "<xsd:enumeration type=\"value\">";

	private static String newText = "<xsd:enumeration type=\"values\">";

	public IDiffHelperImpl() {

	}
	
	public static void main(String[] args) throws IOException{
		diff_match_patch dmp = new diff_match_patch();

	    String v1 = "My Json Object";            
	    String v2 = "My Mutated Json Object";

	    LinkedList<Patch> v2ToV1Patch = dmp.patch_make(newText, oldText);
	    String v2ToV1PatchText = dmp.patch_toText(v2ToV1Patch);
	    System.out.println(v2ToV1PatchText);
	}

	public String fillDiffList(String refString, String vglString) throws IOException {
		diff_match_patch dmp = new diff_match_patch();
//		String refString = readFile(ref);
//		String vglString = readFile(vgl);

		diff = dmp.diff_main(refString, vglString);
		dmp.diff_cleanupSemantic(diff);
		System.out.println(diff);
		String html = dmp.diff_prettyHtml(diff);

		return html;
	}

	private static String readFile(File file) throws IOException {
		// Read a file from disk and return the text contents.
		String filename = file.getAbsolutePath();
		StringBuilder sb = new StringBuilder();
		FileReader input = new FileReader(filename);
		BufferedReader bufRead = new BufferedReader(input);
		try {
			String line = bufRead.readLine();
			while (line != null) {
				sb.append(line).append('\n');
				line = bufRead.readLine();
			}
		} finally {
			bufRead.close();
			input.close();
		}
		return sb.toString();
	}
//
//	private static diff_match_patch diffMatchPatch;
//
//	public static void main(String[] args) {
//
//		diffMatchPatch = new diff_match_patch();
//
//		// Split text into List of strings
//		List<String> oldTextList = Arrays.asList(oldText.split("(\\ |\\n)"));
//		List<String> newTextList = Arrays.asList(newText.split("(\\ |\\n)"));
//
//		// If we have different length
//		int counter = Math.max(oldTextList.size(), newTextList.size());
//		StringBuilder sb = new StringBuilder();
//
//		for (int current = 0; current < counter; current++) {
//			String oldString = null;
//			String newString = null;
//
//			if (oldTextList.size() <= current) {
//				oldString = "";
//				newString = newTextList.get(current);
//
//			} else if (newTextList.size() <= current) {
//				oldString = oldTextList.get(current);
//				newString = "";
//			} else {
//				if (isLineDifferent(oldTextList.get(current),
//						newTextList.get(current))) {
//					oldString = oldTextList.get(current);
//					newString = newTextList.get(current);
//				}
//			}
//			if (oldString != null && newString != null) {
//				// ---- Insert into database here -----
//				sb.append("Changes for Line: " + (current + 1) + "\n");
//				sb.append("Old: " + oldString + "; New: " + newString + ";\n");
//			}
//		}
//
//		System.out.println(sb.toString());
//	}
//
//	private static boolean isLineDifferent(String oldString, String newString) {
//		LinkedList<diff_match_patch.Diff> deltas = diffMatchPatch.diff_main(
//				oldString, newString);
//		diffMatchPatch.diff_cleanupSemantic(deltas);
//		for (diff_match_patch.Diff d : deltas) {
//			if (d.operation == diff_match_patch.Operation.EQUAL) {
//				System.out.println(d.text);
//				continue;
//			}
//			return true;
//		}
//		return false;
//	}
//
//	private static boolean isLineEqual(String oldString, String newString) {
//		LinkedList<diff_match_patch.Diff> deltas = diffMatchPatch.diff_main(
//				oldString, newString);
//		diffMatchPatch.diff_cleanupSemantic(deltas);
//		for (diff_match_patch.Diff d : deltas) {
//			if (d.operation == diff_match_patch.Operation.EQUAL) {
//				return true;
//			} else {
//				continue;
//			}
//		}
//		return false;
//
//	}
}
