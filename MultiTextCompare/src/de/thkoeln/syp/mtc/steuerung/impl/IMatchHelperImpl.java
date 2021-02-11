package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatch;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatchImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IMatchHelper;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class IMatchHelperImpl implements IMatchHelper {
	private List<IAehnlichkeitImpl> paarungen;
	private ITextvergleicher textvergleicher;
	private IFileImporter fileimporter;
	private List<File> tempfiles;
	public List<IMatch> matches;
	List<IMatch> oldIndeces;
	List<String> leftFile = new ArrayList<String>();
	List<String> rightFile = new ArrayList<String>();

	public String[] leftFileLines, rightFileLines;

	// Anzahl der Zeilen, fuer die nach einer identischen Zeile gesucht wird
	private final int LOOKAHEAD = 5;
	private int leftSize = 0, rightSize = 0;

	public IMatchHelperImpl() {
		if (textvergleicher != null) {
			tempfiles = textvergleicher.getTempfilesFromHashMap(
					fileimporter.getTempFilesMap(), tempfiles);
			textvergleicher.getVergleiche(tempfiles);
			paarungen = textvergleicher.getPaarungen();
		}
	}

	/**
	 * Sucht in a und b gleiche Zeilen und ruft Methoden auf, die diese in einer
	 * sinnvolle Darstellung schreiben.
	 * 
	 * @param a
	 *            Referenzdatei
	 * @param b
	 *            Vergleichsdatei
	 */
	@Override
	public void matchEqualLines(File a, File b) throws IOException {

		String reference = "", comp = "";
		int lineCountLeft = 0, lineCountRight = 0;
		oldIndeces = new ArrayList<IMatch>();

		matches = new ArrayList<IMatch>();

		lineCountLeft = getLineCounts(a, leftFile);
		lineCountRight = getLineCounts(b, rightFile);

		if (lineCountLeft == Math.min(lineCountLeft, lineCountRight)) {
			reference = leftFile.get(0);
		} else {
			reference = rightFile.get(0);
		}

		// Schaue für jede Zeile der linken Datei
		for (int i = 0; i < lineCountLeft; i++) {
			reference = leftFile.get(i);
			// ob es in der rechten Datei ein Match gibt
			for (int j = 0; j < lineCountRight; j++) {
				if (reference.equals(rightFile.get(j)) && j >= i
						&& notMatchedYet(i, j)) {
					IMatch match1 = new IMatchImpl(i, j, reference);
					IMatch match2 = new IMatchImpl(i, j, reference);
					matches.add(match1);
					oldIndeces.add(match2);
					break;
				}

			}
		}

		leftSize = lineCountLeft + getMaxDistance(matches);
		rightSize = lineCountRight + getMaxDistance(matches);

		alignMatches(matches);
		fillInMatches();
		fillInBetweenMatches(oldIndeces);

	}

	/**
	 * Prueft ob es bereits ein Match für eine der uebergebenen Zeilen gibt
	 * 
	 * @param left
	 *            Zeile der linken Datei
	 * @param right
	 *            Zeile der rechten Datei
	 * @return true wenn noch kein Match existiert, sonst false
	 */
	private boolean notMatchedYet(Integer left, Integer right) {
		//
		List<Integer> leftIndeces = new LinkedList<Integer>(), rightIndeces = new LinkedList<Integer>();
		for (IMatch match : matches) {
			leftIndeces.add(match.getLeftRow());
			rightIndeces.add(match.getRightRow());
		}
		if (leftIndeces.contains(left) || rightIndeces.contains(right))
			return false;
		else
			return true;
	}

	/**
	 * Zaehlt die Anzahl der Zeilen von file und schreibt diese Zeilen in list
	 * 
	 * @param file
	 *            Die betrachtete Datei
	 * @param list
	 *            Die Liste die befuellt werden soll
	 * @return die Anzahl der Zeilen innerhalb von file
	 * @throws IOException
	 */
	private int getLineCounts(File file, List<String> list) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int lines = 0;
		String line = "";
		while ((line = reader.readLine()) != null) {
			lines++;
			list.add(line);
		}
		reader.close();
		return lines;
	}

	/**
	 * Berechnet auf welche Zeile Matches geschrieben werden muessen um sinnvoll
	 * in der gleichen Zeile zu stehen
	 * 
	 * @param matches
	 *            die Matches die eingereiht werden sollen
	 */
	private void alignMatches(List<IMatch> matches) {
		for (int i = 0; i < matches.size(); i++) {
			IMatch match = matches.get(i);
			boolean matchAligned = isMatchAligned(match);
			if (!matchAligned) {
				int dist = Math.abs(match.getLeftRow() - match.getRightRow());
				if (match.getLeftRow() < match.getRightRow()) {
					for (int j = i; j < matches.size(); j++) {
						matches.get(j).setLeftRow(
								matches.get(j).getLeftRow() + dist);
					}
				} else {
					for (int j = i; j < matches.size(); j++) {
						matches.get(j).setRightRow(
								matches.get(j).getRightRow() + dist);
					}
				}
			}
		}

	}

	/**
	 * Prueft ob gematchte Zeilen innerhalb der gleichen Zeile stehen
	 * 
	 * @param match
	 *            das zu ueberpruefende Match
	 * @return true wenn die Zeilen auf einer Hoehe stehen, sonst false
	 */
	private boolean isMatchAligned(IMatch match) {
		if (match.getLeftRow() == match.getRightRow()) {
			return true;
		}
		return false;
	}

	/**
	 * Schreibt gematchte Zeilen in ihre berechnete Position
	 */
	private void fillInMatches() {
		calculateLineArraySize();
		leftFileLines = new String[leftSize];
		rightFileLines = new String[rightSize];
		for (int i = 0; i < matches.size(); i++) {
			IMatch match = matches.get(i);
			int row = match.getLeftRow();
			leftFileLines[row] = match.getValue();

			row = match.getRightRow();
			rightFileLines[row] = match.getValue();
		}

	}

	/**
	 * Berechnet wie groß die finalen Arrays mit den gematchten Ergebnissen sein
	 * muessen
	 */
	private void calculateLineArraySize() {

		List<String> chunk = leftFile.subList(
				oldIndeces.get(oldIndeces.size() - 1).getLeftRow() + 1,
				leftFile.size());
		int lastMatchIndex = matches.get(matches.size() - 1).getLeftRow() + 1;
		leftSize = chunk.size() + lastMatchIndex;

		chunk = rightFile.subList(oldIndeces.get(oldIndeces.size() - 1)
				.getRightRow() + 1, rightFile.size());
		lastMatchIndex = matches.get(matches.size() - 1).getRightRow() + 1;
		rightSize = chunk.size() + lastMatchIndex;
	}

	/**
	 * Befuellt die Zeilen zwischen Matches mit Zeilen aus der Ursprungsdatei
	 * auf eine sinnvolle Hoehe und fuellt Luecken falls welche durch das
	 * alignen (auf eine Linie bringen) der Matches entstanden sind
	 * 
	 * @param oldIndeces
	 */
	private void fillInBetweenMatches(List<IMatch> oldIndeces) {
		List<String> chunk = new ArrayList<String>();
		int differenceOld = 0, differenceNew = 0, firstIndexOld = 0, secondIndexOld = 0, firstIndexNew = 0, secondIndexNew = 0;
		fillLinesBeforeMatch();
		for (int i = 1; i < oldIndeces.size(); i++) {

			// --------------------------------------------------------------------------------
			// --------------------------leftFile----------------------------------------------
			// --------------------------------------------------------------------------------
			firstIndexOld = oldIndeces.get(i - 1).getLeftRow() + 1;
			firstIndexNew = matches.get(i - 1).getLeftRow() + 1;

			secondIndexOld = oldIndeces.get(i).getLeftRow() + 1;
			// Anzahl der Zeilen die einzufügen sind
			differenceOld = secondIndexOld - firstIndexOld - 1;

			secondIndexNew = matches.get(i).getLeftRow() + 1;

			chunk = leftFile.subList(firstIndexOld, secondIndexOld - 1);

			int index = 0;

			// Teil zwischen den Matches füllen
			for (int m = firstIndexNew; m < secondIndexNew; m++) {
				if (index < differenceOld) {
					leftFileLines[firstIndexNew] = chunk.get(index);
					firstIndexNew++;
					index++;
				}
			}

			// --------------------------------------------------------------------------------
			// --------------------------rightFile---------------------------------------------
			// --------------------------------------------------------------------------------

			firstIndexOld = oldIndeces.get(i - 1).getRightRow() + 1;
			firstIndexNew = matches.get(i - 1).getRightRow() + 1;

			secondIndexOld = oldIndeces.get(i).getRightRow() + 1;
			// Anzahl der Zeilen die einzufügen sind
			differenceOld = secondIndexOld - firstIndexOld - 1;

			secondIndexNew = matches.get(i).getRightRow() + 1;
			// Anzahl der freien Zeilen zwischen matches
			differenceNew = secondIndexNew - firstIndexNew - 1;

			chunk = rightFile.subList(firstIndexOld, secondIndexOld - 1);

			index = 0;
			for (int m = firstIndexNew; m < secondIndexNew; m++) {
				if (index < differenceOld) {
					rightFileLines[firstIndexNew] = chunk.get(index);
					firstIndexNew++;
					index++;
				}
			}

		}
		fillLinesAfterMatches();

		// Unbefuellte Zeilen zur Leerzeile umschreiben
		for (int m = 0; m < leftFileLines.length; m++) {
			if (leftFileLines[m] == null) {
				leftFileLines[m] = "";
			}
		}
		// Ungesetzte Werte auf Leerzeile setzen
		for (int m = 0; m < rightFileLines.length; m++) {
			if (rightFileLines[m] == null) {
				rightFileLines[m] = "";
			}
		}

	}

	/**
	 * Berechnet die Anzahl der Zeilen die durch das Angleichen gematchter
	 * Zeilen zusaetzlich im Ergebnisarray allokiert werden muessen
	 * 
	 * @param matches
	 *            Liste der gematchten Zeilen
	 * @return Die Anzahl zusaetzlicher zeilen
	 */
	private int getMaxDistance(List<IMatch> matches) {
		int dist = 0;
		for (int i = 0; i < matches.size(); i++) {
			dist += Math.abs(matches.get(i).getLeftRow()
					- matches.get(i).getRightRow());
		}
		return dist;
	}

	/**
	 * Falls das erste Match nicht in der ersten Zeile steht, werden hier vorher
	 * die alten Zeilen vor das erste Match eingefuegt
	 */
	private void fillLinesBeforeMatch() {
		if (oldIndeces != null) {
			IMatch firstMatch = oldIndeces.get(0);
			List<String> chunk = leftFile.subList(0, firstMatch.getLeftRow());

			for (int i = 0; i < chunk.size(); i++) {
				leftFileLines[i] = chunk.get(i);
			}

			chunk = rightFile.subList(0, firstMatch.getRightRow());

			for (int i = 0; i < chunk.size(); i++) {
				rightFileLines[i] = chunk.get(i);
			}
		}
	}

	/**
	 * Falls nach dem letzten Match weitere Zeilen existieren, werden diese hier
	 * eingefuegt
	 */
	private void fillLinesAfterMatches() {
		if (oldIndeces != null) {
			List<String> chunk = leftFile.subList(
					oldIndeces.get(oldIndeces.size() - 1).getLeftRow() + 1,
					leftFile.size());

			int index = 0;

			for (int j = matches.get(matches.size() - 1).getLeftRow() + 1; j < leftFileLines.length; j++) {
				leftFileLines[j] = chunk.get(index);
				index++;
			}

			chunk = rightFile.subList(oldIndeces.get(oldIndeces.size() - 1)
					.getRightRow() + 1, rightFile.size());

			index = 0;
			for (int j = matches.get(matches.size() - 1).getRightRow() + 1; j < rightFileLines.length; j++) {
				rightFileLines[j] = chunk.get(index);
				index++;
			}
		}
	}

}
