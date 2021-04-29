package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IMatchHelper;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class ITextvergleicherImpl implements ITextvergleicher {

	private IFileImporter fileImporter;

	private IMatrix iMatrixImpl;
	private List<IMatrixImpl> batches;

	private List<IAehnlichkeitImpl> paarungen;
	private List<File> tempFiles;

	public ITextvergleicherImpl() {

	}

	/**
	 * Sucht zun‰chst alle gleichen Zeilen. Falls es nur inserts und deletes
	 * gibt, ergibt sich die Aehnlichkeit aus: Anzahl gleicher Zahlen * Gewicht
	 * Ansonsten werden ge‰nderte Zeilen einzeln ausgewertet und gewichtet.
	 */
	@Override
	public void vergleicheZeilenweise(List<IAehnlichkeitImpl> batch) {
		List<String> referenceLines = null;
		List<String> comparisonLines = null;
		final int MATCHING_LOOKAHEAD = fileImporter.getConfig()
				.getMatchingLookahead();
		final double MATCH_AT_VALUE = fileImporter.getConfig().getMatchAt();
		final boolean SEARCH_BEST_MATCH = fileImporter.getConfig()
				.getBestMatch();
		final boolean MATCH_LINES = fileImporter.getConfig().getLineMatch();
		for (IAehnlichkeitImpl a : batch) {
			File ref, comp;
			ref = a.getVon();
			comp = a.getZu();

			try {
				IMatchHelper matchHelper = new IMatchHelperImpl();
				File[] original = new File[] { ref, comp };
				File[] matchedFiles = createCompareMatchFiles(original);
				ref = matchedFiles[0];
				comp = matchedFiles[1];
				matchHelper.setMATCH_AT(MATCH_AT_VALUE);
				matchHelper.setLOOKAHEAD(MATCHING_LOOKAHEAD);
				matchHelper.setSearchBestMatch(SEARCH_BEST_MATCH);
				if(MATCH_LINES){
					matchHelper.matchLines(ref, comp);
				}

				referenceLines = fileToLines(ref);
				comparisonLines = fileToLines(comp);

				double maxFileSize = calculateLineWeight(referenceLines.size(),
						comparisonLines.size());
				double weightPerLine = 1 / maxFileSize;
				double similarity = 0;

				similarity = calculateSimilarityMetric(weightPerLine,
						referenceLines, comparisonLines);
				ref.delete();
				comp.delete();
				a.setWert(similarity);

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	/**
	 * Verkettet sowohl Referenz- als auch Vergleichsdatei zu je einem String
	 * und vergleicht jeweils die Zeichenmengen miteinander. Anwendung: Wenn die
	 * Reihenfolge der Woerter keine groﬂe Rolle spielt.
	 */
	@Override
	public void vergleicheUeberGanzesDokument(List<IAehnlichkeitImpl> batch) {
		for (IAehnlichkeitImpl a : batch) {

			try {
				List<String> refList = fileToLines(a.getVon());
				List<String> vglList = fileToLines(a.getZu());

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

				double maxSize = (double) Math.max(referenzArray.length, vergleichsArray.length);
				
				double levenshtein = (double) calculateLevenshteinDist(
						referenzString, vergleichsString, new Integer(5));

				double metrik = (maxSize - levenshtein) / maxSize;
				a.setWert(metrik);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void compareJSON(List<IAehnlichkeitImpl> batch) {
		for (IAehnlichkeitImpl a : batch) {
			try {
				IJSONcomparer jsonComparer = new IJSONcomparer();
				double similarity = jsonComparer.compare(a.getVon(), a.getZu());
				a.setWert(similarity);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private File[] createCompareMatchFiles(File[] files) throws IOException {
		BufferedReader reader;
		BufferedWriter writer;
		List<File> matchFiles = new ArrayList<File>();
		for (File f : files) {
			String path = System.getProperty("user.dir") + File.separator
					+ "TempFiles" + File.separator + "temp_match_"
					+ UUID.randomUUID().toString();
			File temp = new File(path);

			if (temp.exists()) {
				temp.delete();
			}
			temp.createNewFile();

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(temp), "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line + "\n");
			}
			matchFiles.add(temp);

			reader.close();
			writer.close();

		}
		return matchFiles.toArray(new File[files.length]);

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
	 *            enth‰lt das Mapping der importierten Dateien auf die
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
		final BufferedReader in = new BufferedReader(new FileReader(file));
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
			if(lengthOfLongestString <= 500) {
				levenshteinDist = (double) new LevenshteinDistance().apply(ref, comp);
			}
			else {
				LevenshteinDistance levenshtein = new LevenshteinDistance(500);
				levenshteinDist = (double) levenshtein.apply(ref, comp);
			}
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
	 * @return 0 wenn refList groeﬂer ist, 1 wenn vglList groeﬂer ist und 2 wenn
	 *         beide gleich groﬂ sind
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
	public List<IAehnlichkeitImpl> getVergleiche(List<File> files) {
		paarungen = new ArrayList<IAehnlichkeitImpl>();
		int id = 0;
		IAehnlichkeit vergleich = new IAehnlichkeitImpl();
		for (int i = 0; i < files.size(); i++) {
			for (int j = i + 1; j < files.size(); j++) {
				id++;
				vergleich = new IAehnlichkeitImpl();
				vergleich.setVon(files.get(i));
				vergleich.setZu(files.get(j));
				vergleich.setId(id);
				try {
					vergleich.setWeight(getWeightFromComparison(files.get(i),
							files.get(j)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				paarungen.add((IAehnlichkeitImpl) vergleich);
			}
		}

		return paarungen;
	}

	private int getWeightFromComparison(File ref, File comp) throws IOException {
		String line = "";
		int weight = 0;
		BufferedReader in = new BufferedReader(new FileReader(ref));
		while ((line = in.readLine()) != null) {
			weight += line.length();
		}
		in.close();
		in = new BufferedReader(new FileReader(comp));
		while ((line = in.readLine()) != null) {
			weight += line.length();
		}
		in.close();
		return weight;
	}

	@Override
	public void createBatches() {
		batches = new ArrayList<IMatrixImpl>();
		int numThreads = Runtime.getRuntime().availableProcessors();
		Collections.sort(paarungen, new SortIAehnlichkeitByID());
		batches = distributeBatches(paarungen, numThreads);

	}

	@Override
	public void mergeBatches() {
		paarungen.clear();
		for (int i = 0; i < batches.size(); i++) {
			List<IAehnlichkeitImpl> currentBatch = batches.get(i).getInhalt();
			for (int j = 0; j < currentBatch.size(); j++) {
				IAehnlichkeit currentComparison = currentBatch.get(j);
				paarungen.add(new IAehnlichkeitImpl(currentComparison.getVon(),
						currentComparison.getZu(), currentComparison
								.getWeight(), currentComparison.getId(),
						currentComparison.getWert()));
			}
		}
		Collections.sort(paarungen, new SortIAehnlichkeitByID());
	}

	public List<IMatrixImpl> distributeBatches(
			List<IAehnlichkeitImpl> iterable, int partitions) {
		batches = new ArrayList<>(partitions);
		for (int i = 0; i < partitions; i++)
			batches.add(new IMatrixImpl());

		Iterator<IAehnlichkeitImpl> iterator = iterable.iterator();
		for (int i = 0; iterator.hasNext(); i++)
			batches.get(i % partitions).getInhalt()
					.add((IAehnlichkeitImpl) iterator.next());

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
	public int calculateLevenshteinDist(String ref, String comp, Integer threshold) {
		LevenshteinDistance levenshtein = new LevenshteinDistance(threshold);
		return levenshtein.apply(ref, comp);
	}

	@Override
	public List<IAehnlichkeitImpl> getPaarungen() {
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

	class SortIAehnlichkeitByID implements Comparator<IAehnlichkeit> {

		@Override
		public int compare(IAehnlichkeit o1, IAehnlichkeit o2) {
			return o1.getId() - o2.getId();
		}

	}

	class SortIAehnlichkeitByWeight implements Comparator<IAehnlichkeit> {

		@Override
		public int compare(IAehnlichkeit o1, IAehnlichkeit o2) {
			return o2.getWeight() - o1.getWeight();
		}

	}

}
