package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jdom2.JDOMException;

import de.thkoeln.syp.mtc.datenhaltung.api.IComparison;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.control.FileSelectionController.CompareListener.CompareThread;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IMatchHelper;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

/**
 * Verwaltet alle Vergleichsalgorithmen und das Setup von Vergleichen. Dabei
 * werden die Dateien von IFileImporter nach Vergleichen aufgeteilt und nach
 * Gewicht sortiert auf verfügbare CPU-Threads verteilt.
 * 
 * @author Allen Kletinitch
 *
 */
public class ITextvergleicherImpl implements ITextvergleicher {

	private IFileImporter fileImporter;

	private IMatrix iMatrixImpl;
	private List<IMatrixImpl> batches;

	private List<IComparisonImpl> paarungen;
	private List<File> tempFiles;

	private CompareThread compareThread;
	private volatile int i;

	public ITextvergleicherImpl() {

	}

	/**
	 * Sucht zunï¿½chst alle gleichen Zeilen. Falls es nur inserts und deletes
	 * gibt, ergibt sich die Aehnlichkeit aus: Anzahl gleicher Zahlen * Gewicht
	 * Ansonsten werden geï¿½nderte Zeilen einzeln ausgewertet und gewichtet.
	 */
	@Override
	public void vergleicheZeilenweise(List<IComparisonImpl> batch) {
		compareThread = Management.getInstance().getCompareThread();
		i = 0;
		List<String> referenceLines = null;
		List<String> comparisonLines = null;
		Object[] matchedFiles = null;
		final int MATCHING_LOOKAHEAD = fileImporter.getConfig()
				.getMatchingLookahead();
		final double MATCH_AT_VALUE = fileImporter.getConfig().getMatchAt();
		final boolean SEARCH_BEST_MATCH = fileImporter.getConfig()
				.getBestMatch();
		final boolean MATCH_LINES = fileImporter.getConfig().getLineMatch();
		for (IComparisonImpl a : batch) {
			File ref, comp;
			ref = a.getFrom();
			comp = a.getTo();
			try {
				i++;
				IMatchHelper matchHelper = new IMatchHelperImpl();
				matchHelper.setMATCH_AT(MATCH_AT_VALUE);
				matchHelper.setLOOKAHEAD(MATCHING_LOOKAHEAD);
				matchHelper.setSearchBestMatch(SEARCH_BEST_MATCH);
				matchHelper.isLineCompare(true);
				if (MATCH_LINES) {
					matchedFiles = matchHelper.matchLines(ref, comp);
					String[] refArray = (String[]) matchedFiles[0];
					String[] compArray = (String[]) matchedFiles[1];

					referenceLines = new ArrayList<String>(
							Arrays.asList(refArray));
					comparisonLines = new ArrayList<String>(
							Arrays.asList(compArray));
				} else {
					referenceLines = fileToLines(ref);
					comparisonLines = fileToLines(comp);
				}

				double maxFileSize = calculateLineWeight(referenceLines.size(),
						comparisonLines.size());
				double weightPerLine = 1 / maxFileSize;
				double similarity = 0;

				similarity = calculateSimilarityMetric(weightPerLine,
						referenceLines, comparisonLines);

				a.setValue(similarity);
				compareThread.publishData(i);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	/**
	 * Verkettet sowohl Referenz- als auch Vergleichsdatei zu je einem String
	 * und vergleicht jeweils die Zeichenmengen miteinander. Anwendung: Wenn die
	 * Reihenfolge der Woerter keine groï¿½e Rolle spielt.
	 */
	@Override
	public void vergleicheUeberGanzesDokument(List<IComparisonImpl> batch) {
		final int MAXLINELENGTH = fileImporter.getConfig().getMaxLineLength();
		compareThread = Management.getInstance().getCompareThread();
		i = 0;
		for (IComparisonImpl a : batch) {
			try {
				i++;
				List<String> refList = fileToLines(a.getFrom());
				List<String> vglList = fileToLines(a.getTo());

				String referenzString = "", vergleichsString = "";
				for (String s : refList) {
					referenzString += s;
				}
				for (String s : vglList) {
					vergleichsString += s;
				}
				char[] referenzArray = referenzString.toCharArray();
				char[] vergleichsArray = vergleichsString.toCharArray();

				Arrays.sort(referenzArray);
				Arrays.sort(vergleichsArray);

				referenzString = new String(referenzArray);
				vergleichsString = new String(vergleichsArray);

				double maxSize = (double) Math.max(referenzArray.length,
						vergleichsArray.length);

				double levenshtein = (double) calculateLevenshteinDist(
						referenzString, vergleichsString, new Integer(
								MAXLINELENGTH));

				double metrik = (maxSize - levenshtein) / maxSize;
				a.setValue(metrik);
				compareThread.publishData(i);
			} catch (IOException e) {
				Logger logger = Management.getInstance().getLogger();
				logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
			}

		}

	}

	/**
	 * Ruft den strukturellen Vergleich für JSON-Dateien auf und speichert das
	 * Ergebnis
	 */
	@Override
	public void compareJSON(List<IComparisonImpl> batch) {
		i = 0;
		// this line will cause unit tests to fail
		compareThread = Management.getInstance().getCompareThread();
		final int MAXLINELENGTH = fileImporter.getConfig().getMaxLineLength();
		for (IComparisonImpl a : batch) {
			try {
				i++;
				IJSONComparerImpl jsonComparer = new IJSONComparerImpl(
						MAXLINELENGTH);
				double similarity = jsonComparer
						.compare(a.getFrom(), a.getTo());
				a.setValue(similarity);
				compareThread.publishData(i);
			} catch (IOException e) {
				Logger logger = Management.getInstance().getLogger();
				logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
			}
		}

	}

	/**
	 * Ruft den strukturellen Vergleich für JSON-Dateien auf und speichert das
	 * Ergebnis
	 */
	@Override
	public void compareXML(List<IComparisonImpl> batch) {
		i = 0;
		// this line will cause unit tests to fail
		compareThread = Management.getInstance().getCompareThread();
		for (IComparisonImpl a : batch) {
			try {
				i++;
				IXMLComparerImpl xmlComparer = new IXMLComparerImpl(
						fileImporter);
				double similarity = xmlComparer.compare(a.getFrom(), a.getTo());
				a.setValue(similarity);
				compareThread.publishData(i);
			} catch (JDOMException e) {
				// occurs due to xml files with errors being compared
			} catch (IOException e) {
				Logger logger = Management.getInstance().getLogger();
				logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
			}
		}
	}

	/**
	 * Extrahiert die temporaeren Dateien die im IFileImporterImpl erstellt
	 * werden
	 */
	@Override
	public void getTempfilesFromHashMap(Map<File, File> map) {
		tempFiles = new ArrayList<File>();
		Map<File, File> sorted = sortHashMapByValue(map);
		for (Map.Entry<File, File> entry : sorted.entrySet()) {
			tempFiles.add(entry.getValue());
		}

	}

	/**
	 * Sortiert die Hashmap nach Nummerierung der temporaeren Dateien. Diese
	 * sind mit temp_1...temp_n benannt
	 * 
	 * @param map
	 *            enthï¿½lt das Mapping der importierten Dateien auf die
	 *            temporaeren Dateien
	 * @return eine sortierte Hashmap
	 */
	private HashMap<File, File> sortHashMapByValue(Map<File, File> map) {
		List<Map.Entry<File, File>> list = new ArrayList<Map.Entry<File, File>>(
				map.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<File, File>>() {
			public int compare(Map.Entry<File, File> o1,
					Map.Entry<File, File> o2) {
				try {
					Integer von = Integer.parseInt(o1.getValue().getName()
							.replace("temp_", ""));
					Integer zu = Integer.parseInt(o2.getValue().getName()
							.replace("temp_", ""));
					return von.compareTo(zu);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return -1;
				}

			}
		});

		// put data from sorted list to hashmap
		HashMap<File, File> temp = new LinkedHashMap<File, File>();
		for (Map.Entry<File, File> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;

	}

	/**
	 * Liest Datei zeilenweise aus und speichert die Zeilen in lines
	 * 
	 * @param file
	 *            Datei deren Zeilen in eine Liste konvertiert werden sollen
	 * @return lines Liste mit Zeilen von file
	 */
	private List<String> fileToLines(File file) throws IOException {
		final List<String> lines = new ArrayList<String>();
		String line;
		final BufferedReader in = Files.newBufferedReader(file.toPath(),
				StandardCharsets.UTF_8);
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();
		return lines;
	}

	@Override
	public void fillMatrix() {
		iMatrixImpl = new IMatrixImpl();
		iMatrixImpl.setInhalt(paarungen);
	}

	/**
	 * Zwischenschritt fuer die Berechnung der Zeilengewichte. Vergleicht die
	 * groessen der beiden Zeilenlisten
	 * 
	 * @param refSize
	 *            Liste mit einzelnen Zeilen der Referenzdatei
	 * @param vglSize
	 *            Liste mit einzelnen Zeilen der Vergleichsdatei
	 * @return die groesste Zahl zwischen den beiden Parametern
	 */
	private int calculateLineWeight(int refSize, int vglSize) {
		return Math.max(refSize, vglSize);
	}

	/**
	 * Berechnet die Aehnlichkeitsmetrix fuer geaenderte Zeilen
	 * 
	 * @param weight
	 *            die Gewichtung einer einzigen Zeile := 1/max. Anzahl der
	 *            Zeilen innerhalb der verglichenen Dateien
	 * @param refList
	 *            eine Liste mit allen Zeilen aus der Referenzdatei
	 * @param compList
	 *            eine Liste mit allen Zeilen aus der Vergleichsdatei
	 * @return Array mit den berechneten Metriken
	 */
	private double calculateSimilarityMetric(double weight,
			List<String> refList, List<String> compList) {
		final int MAXLINELENGTH = fileImporter.getConfig().getMaxLineLength();
		int norm = normalizeStringLists(refList, compList);
		int max = refList.size();
		if (norm == 0) {
			max = refList.size();
		} else if (norm == 1) {
			max = compList.size();
		}

		double[] metricPerLine = new double[max];
		for (int i = 0; i < max; i++) {
			String ref = refList.get(i);
			String comp = compList.get(i);
			int longestString;

			if (ref.length() >= comp.length()) {
				longestString = ref.length();
			} else {
				longestString = comp.length();
			}

			double lengthOfLongestString = (double) longestString;
			double levenshteinDist;

			levenshteinDist = calculateLevenshteinDist(ref, comp, MAXLINELENGTH);

			if (lengthOfLongestString != 0) {
				metricPerLine[i] = weight
						* (double) ((lengthOfLongestString - levenshteinDist) / lengthOfLongestString);
			} else {
				metricPerLine[i] = weight * 1.0;
			}

		}
		double similarity = 0;
		for (int i = 0; i < metricPerLine.length; i++) {
			similarity += metricPerLine[i];
		}
		return similarity;
	}

	/**
	 * 
	 * @param refList
	 *            Liste mit Zeilen der Referenzdatei
	 * @param compList
	 *            Liste mit Zahlen der Vergleichsdatei
	 * @return 0 wenn refList groeï¿½er ist, 1 wenn vglList groeï¿½er ist und 2
	 *         wenn beide gleich groï¿½ sind
	 */
	private int normalizeStringLists(List<String> refList, List<String> compList) {
		int refListSize = refList.size();
		int compListSize = compList.size();
		int diff = Math.abs(refListSize - compListSize);
		if (refListSize > compListSize) {
			for (int i = 0; i < diff; i++) {
				compList.add("");
			}
			return 0;
		} else if (refListSize < compListSize) {
			for (int i = 0; i < diff; i++) {
				refList.add("");
			}
			return 1;
		}
		return 2;

	}

	/**
	 * @param files
	 *            die Liste der Textdateien die fuer den Vergleich ausgewaehlt
	 *            wurden
	 * @return paarungen die Eintraege der Aehnlichkeitsmatrix ohne den
	 *         Aehnlichkeitswert
	 */
	public List<IComparisonImpl> getVergleiche(List<File> files) {
		paarungen = new ArrayList<IComparisonImpl>();
		int id = 0;
		IComparison vergleich = new IComparisonImpl();
		for (int i = 0; i < files.size(); i++) {
			for (int j = i + 1; j < files.size(); j++) {
				id++;
				vergleich = new IComparisonImpl();
				vergleich.setFrom(files.get(i));
				vergleich.setTo(files.get(j));
				vergleich.setId(id);
				try {
					vergleich.setWeight(getWeightFromComparison(files.get(i),
							files.get(j)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				paarungen.add((IComparisonImpl) vergleich);
			}
		}

		return paarungen;
	}

	/**
	 * Errechnet für einen Vergleich das Gewicht. Das Gewicht ist die Anzahl
	 * aller Zeichen in beiden zu vergleichenden Dateien
	 * 
	 * @param ref
	 *            Referenzdatei
	 * @param comp
	 *            Vergleichsdatei
	 * @return Das Gewicht des Vergleichs
	 * @throws IOException
	 */
	private int getWeightFromComparison(File ref, File comp) throws IOException {
		String line = "";
		int weight = 0;
		BufferedReader in = Files.newBufferedReader(ref.toPath(),
				StandardCharsets.UTF_8);
		while ((line = in.readLine()) != null) {
			weight += line.length();
		}
		in.close();
		in = Files.newBufferedReader(comp.toPath(), StandardCharsets.UTF_8);
		while ((line = in.readLine()) != null) {
			weight += line.length();
		}
		in.close();
		return weight;
	}

	/**
	 * Erstellt Batches je nach Anzahl der verfügbaren CPU-Threads und teils
	 * Vergleiche gleichmäßig auf diesen auf.
	 */
	@Override
	public void createBatches() {
		batches = new ArrayList<IMatrixImpl>();
		int numThreads = Runtime.getRuntime().availableProcessors();
		Collections.sort(paarungen, new SortIAehnlichkeitByWeight());
		batches = distributeBatches(paarungen, numThreads);

	}

	/**
	 * Fügt Batches nach Vergleich in eine einzige Liste zusammen und sortiert
	 * diese nach ihrer ID
	 */
	@Override
	public void mergeBatches() {
		paarungen.clear();
		for (int i = 0; i < batches.size(); i++) {
			List<IComparisonImpl> currentBatch = batches.get(i).getInhalt();
			for (int j = 0; j < currentBatch.size(); j++) {
				IComparison currentComparison = currentBatch.get(j);
				paarungen.add(new IComparisonImpl(currentComparison.getFrom(),
						currentComparison.getTo(), currentComparison
								.getWeight(), currentComparison.getId(),
						currentComparison.getValue()));
			}
		}
		Collections.sort(paarungen, new SortIAehnlichkeitByID());
	}

	/**
	 * Verteilt Vergleiche nach dem Round Robin Prinzip auf die verfügbaren
	 * Batches in Abhaengigkeit der Vergleichsgewichte
	 * 
	 * @param iterable
	 *            Listen mit den zu verteilenden Vergleichen
	 * @param partitions
	 *            Anzahl der Batches
	 * @return
	 */
	public List<IMatrixImpl> distributeBatches(List<IComparisonImpl> iterable,
			int partitions) {
		batches = new ArrayList<>(partitions);
		for (int i = 0; i < partitions; i++)
			batches.add(new IMatrixImpl());

		Iterator<IComparisonImpl> iterator = iterable.iterator();
		for (int i = 0; iterator.hasNext(); i++)
			batches.get(i % partitions).getInhalt()
					.add((IComparisonImpl) iterator.next());

		return batches;
	}

	/*
	 * END FOR NORMAL TEXT COMPARISON
	 */

	/**
	 * Berechnet die Levenshtein Distanz zwischen s1 und s2
	 * 
	 * @param s1
	 *            Referenz-String
	 * @param s2
	 *            Vergleichs-String
	 * @return die LevenShtein Distanz zwischen s1 und s2
	 */
	@Override
	public int calculateLevenshteinDist(String ref, String comp,
			Integer threshold) {
		LevenshteinDistance levenshtein = new LevenshteinDistance(threshold);
		if (threshold == 0) {
			levenshtein = new LevenshteinDistance(null);
		}
		int dist = levenshtein.apply(ref, comp);
		if (dist == -1) {
			return Math.max(ref.length(), comp.length());
		}

		return dist;
	}

	@Override
	public List<IComparisonImpl> getPaarungen() {
		return paarungen;
	}

	@Override
	public List<File> getTempFiles() {
		return tempFiles;
	}

	@Override
	public IMatrixImpl getMatrix() {
		return (IMatrixImpl) iMatrixImpl;
	}

	@Override
	public List<IMatrixImpl> getBatches() {
		return batches;
	}

	@Override
	public void setFileImporter(IFileImporter fileImporter) {
		this.fileImporter = fileImporter;
	}

	/**
	 * Sortiert Vergleiche nach ihrer ID
	 * 
	 * @author Allen Kletinitch
	 *
	 */
	class SortIAehnlichkeitByID implements Comparator<IComparison> {

		@Override
		public int compare(IComparison o1, IComparison o2) {
			return o1.getId() - o2.getId();
		}

	}

	/**
	 * Sortiert Vergleiche nach ihrem Gewicht
	 * 
	 * @author Allen Kletinitch
	 *
	 */
	class SortIAehnlichkeitByWeight implements Comparator<IComparison> {

		@Override
		public int compare(IComparison o1, IComparison o2) {
			return o2.getWeight() - o1.getWeight();
		}

	}

}
