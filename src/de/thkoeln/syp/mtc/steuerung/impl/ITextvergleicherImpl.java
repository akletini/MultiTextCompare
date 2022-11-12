package de.thkoeln.syp.mtc.steuerung.impl;

import de.thkoeln.syp.mtc.datenhaltung.api.IComparison;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.control.FileSelectionController.CompareListener.CompareThread;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;
import de.thkoeln.syp.mtc.steuerung.services.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jdom2.JDOMException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Verwaltet alle Vergleichsalgorithmen und das Setup von Vergleichen. Dabei
 * werden die Dateien von IFileImporter nach Vergleichen aufgeteilt und nach
 * Gewicht sortiert auf verf�gbare CPU-Threads verteilt.
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
	 * Sucht zun�chst alle gleichen Zeilen. Falls es nur inserts und deletes
	 * gibt, ergibt sich die Aehnlichkeit aus: Anzahl gleicher Zahlen * Gewicht
	 * Ansonsten werden ge�nderte Zeilen einzeln ausgewertet und gewichtet.
	 */
	@Override
	public void vergleicheZeilenweise(List<IComparisonImpl> batch) {
		compareThread = Management.getInstance().getCompareThread();
		i = 0;
		List<String> referenceLines;
		List<String> comparisonLines;
		Object[] matchedFiles;
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

					referenceLines = new ArrayList<>(
							Arrays.asList(refArray));
					comparisonLines = new ArrayList<>(
							Arrays.asList(compArray));
				} else {
					referenceLines = fileToLines(ref);
					comparisonLines = fileToLines(comp);
				}

				double maxFileSize = calculateLineWeight(referenceLines.size(),
						comparisonLines.size());
				double weightPerLine = 1 / maxFileSize;
				double similarity;

				similarity = calculateSimilarityMetric(weightPerLine,
						referenceLines, comparisonLines);

				a.setValue(similarity);
				compareThread.publishData(i);
			} catch (IOException e) {
				Logger logger = Management.getInstance().getLogger();
				logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
			}
		}

	}

	/**
	 * Verkettet sowohl Referenz- als auch Vergleichsdatei zu je einem String
	 * und vergleicht jeweils die Zeichenmengen miteinander. Anwendung: Wenn die
	 * Reihenfolge der Woerter keine gro�e Rolle spielt.
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

				StringBuilder referenzString = new StringBuilder();
				StringBuilder vergleichsString = new StringBuilder();
				for (String s : refList) {
					referenzString.append(s);
				}
				for (String s : vglList) {
					vergleichsString.append(s);
				}
				char[] referenzArray = referenzString.toString().toCharArray();
				char[] vergleichsArray = vergleichsString.toString().toCharArray();

				Arrays.sort(referenzArray);
				Arrays.sort(vergleichsArray);

				referenzString = new StringBuilder(new String(referenzArray));
				vergleichsString = new StringBuilder(new String(vergleichsArray));

				double maxSize = Math.max(referenzArray.length,
						vergleichsArray.length);

				double levenshtein = calculateLevenshteinDist(
						referenzString.toString(), vergleichsString.toString(), MAXLINELENGTH);

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
	 * Ruft den strukturellen Vergleich f�r JSON-Dateien auf und speichert das
	 * Ergebnis
	 */
	@Override
	public void compareJSON(List<IComparisonImpl> batch) {
		i = 0;
		// this line will cause unit tests to fail
		compareThread = Management.getInstance().getCompareThread();
		Logger logger = Management.getInstance().getLogger();
		final int MAXLINELENGTH = fileImporter.getConfig().getMaxLineLength();
		for (IComparisonImpl a : batch) {
			try {
				i++;
				IJSONCompare jsonComparer = new IJSONCompareImpl(
						MAXLINELENGTH);
				double similarity = jsonComparer
						.compare(a.getFrom(), a.getTo());
				a.setValue(similarity);
				compareThread.publishData(i);
			} catch (IOException e) {
				logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
			}
		}

	}

	/**
	 * Ruft den strukturellen Vergleich f�r JSON-Dateien auf und speichert das
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
				IXMLCompare xmlComparer = new IXMLCompareImpl(
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
		tempFiles = new ArrayList<>();
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
	 *            enth�lt das Mapping der importierten Dateien auf die
	 *            temporaeren Dateien
	 * @return eine sortierte Hashmap
	 */
	private HashMap<File, File> sortHashMapByValue(Map<File, File> map) {
		List<Map.Entry<File, File>> list = new ArrayList<>(
				map.entrySet());

		// Sort the list
		list.sort((o1, o2) -> {
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

		});

		// put data from sorted list to hashmap
		HashMap<File, File> temp = new LinkedHashMap<>();
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
		final List<String> lines = new ArrayList<>();
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
		if (norm == 1) {
			max = compList.size();
		}

		double[] metricPerLine = new double[max];
		for (int i = 0; i < max; i++) {
			String ref = refList.get(i);
			String comp = compList.get(i);

			double lengthOfLongestString = Math.max(ref.length(), comp.length());
			double levenshteinDist;

			levenshteinDist = calculateLevenshteinDist(ref, comp, MAXLINELENGTH);

			if (lengthOfLongestString != 0) {
				metricPerLine[i] = weight
						* ((lengthOfLongestString - levenshteinDist) / lengthOfLongestString);
			} else {
				metricPerLine[i] = weight;
			}

		}
		double similarity = 0;
		for (double v : metricPerLine) {
			similarity += v;
		}
		return similarity;
	}

	/**
	 * 
	 * @param refList
	 *            Liste mit Zeilen der Referenzdatei
	 * @param compList
	 *            Liste mit Zahlen der Vergleichsdatei
	 * @return 0 wenn refList groe�er ist, 1 wenn vglList groe�er ist und 2
	 *         wenn beide gleich gro� sind
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
		paarungen = new ArrayList<>();
		int id = 0;
		IComparisonImpl vergleich;
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
				paarungen.add(vergleich);
			}
		}

		return paarungen;
	}

	/**
	 * Errechnet f�r einen Vergleich das Gewicht. Das Gewicht ist die Anzahl
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
		String line;
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
	 * Erstellt Batches je nach Anzahl der verf�gbaren CPU-Threads und teils
	 * Vergleiche gleichm��ig auf diesen auf.
	 */
	@Override
	public void createBatches() {
		batches = new ArrayList<>();
		int numThreads = Runtime.getRuntime().availableProcessors();
		paarungen.sort(new SortIAehnlichkeitByWeight());
		batches = distributeBatches(paarungen, numThreads);

	}

	/**
	 * F�gt Batches nach Vergleich in eine einzige Liste zusammen und sortiert
	 * diese nach ihrer ID
	 */
	@Override
	public void mergeBatches() {
		paarungen.clear();
		for (IMatrixImpl batch : batches) {
			List<IComparisonImpl> currentBatch = batch.getInhalt();
			for (IComparison currentComparison : currentBatch) {
				paarungen.add(new IComparisonImpl(currentComparison.getFrom(),
						currentComparison.getTo(), currentComparison
						.getWeight(), currentComparison.getId(),
						currentComparison.getValue()));
			}
		}
		paarungen.sort(new SortIAehnlichkeitByID());
	}

	/**
	 * Verteilt Vergleiche nach dem Round Robin Prinzip auf die verf�gbaren
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
					.add(iterator.next());

		return batches;
	}

	/*
	 * END FOR NORMAL TEXT COMPARISON
	 */

	/**
	 * Berechnet die Levenshtein Distanz zwischen s1 und s2
	 * 
	 * @param ref
	 *            Referenz-String
	 * @param comp
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
	static class SortIAehnlichkeitByID implements Comparator<IComparison> {

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
	static class SortIAehnlichkeitByWeight implements Comparator<IComparison> {

		@Override
		public int compare(IComparison o1, IComparison o2) {
			return o2.getWeight() - o1.getWeight();
		}

	}

}
