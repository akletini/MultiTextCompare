package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ITextvergleicherImpl implements ITextvergleicher {

	private IMatrix iMatrixImpl;
	private List<IMatrixImpl> batches;

	private List<IAehnlichkeitImpl> paarungen;
	private List<File> tempFiles;
	private List<String> referenzZeilen;
	private List<String> vergleichsZeilen;
	private File ref, vgl;

	public ITextvergleicherImpl(File ref, File vgl) {
		this.ref = ref;
		this.vgl = vgl;

	}

	public ITextvergleicherImpl() {
	}

	/**
	 * Sucht zun�chst alle gleichen Zeilen. Falls es nur inserts und deletes
	 * gibt, ergibt sich die Aehnlichkeit aus: Anzahl gleicher Zahlen * Gewicht
	 * Ansonsten werden ge�nderte Zeilen einzeln ausgewertet und gewichtet.
	 */
	@Override
	public void vergleicheZeilenweise(List<IAehnlichkeitImpl> batch) {
		for (IAehnlichkeit a : batch) {
			ref = a.getVon();
			vgl = a.getZu();
			double gewicht = 0, aehnlichkeit = 0;
			ITextvergleicherImpl comp = new ITextvergleicherImpl(a.getVon(),
					a.getZu());
			try {
				List<String> refList = fileToLines(a.getVon());
				List<String> vglList = fileToLines(a.getZu());

				gewicht = 1 / ermittleGewicht(refList.size(), vglList.size());

				List<Chunk> changedChunks = comp.getChangesFromOriginal();

				List<Chunk> deletedChunks = comp.getDeletesInReference();

				List<Chunk> unchangedChunks = comp.getChangesInReference();

				int anzahlGleicherZeilen = 0, anzahlGeloeschterZeilen = 0, anzahlGeaenderterZeilen = 0;
				for (int i = 0; i < deletedChunks.size(); i++) {
					anzahlGeloeschterZeilen += deletedChunks.get(i).getLines()
							.size();

				}

				for (int i = 0; i < unchangedChunks.size(); i++) {
					anzahlGeaenderterZeilen += unchangedChunks.get(i)
							.getLines().size();
				}

				anzahlGleicherZeilen = refList.size() - anzahlGeloeschterZeilen
						- anzahlGeaenderterZeilen;

				if (changedChunks.size() == 0) {
					aehnlichkeit = anzahlGleicherZeilen * gewicht;
					a.setWert(aehnlichkeit);

				} else {

					referenzZeilen = new ArrayList<String>();
					vergleichsZeilen = new ArrayList<String>();

					changedChunkListToStringList(unchangedChunks,
							referenzZeilen);
					changedChunkListToStringList(changedChunks,
							vergleichsZeilen);

					double[] metrikProZeile = berechneMetrik(gewicht,
							referenzZeilen, vergleichsZeilen);
					for (int i = 0; i < metrikProZeile.length; i++) {
						aehnlichkeit += metrikProZeile[i];
					}
					aehnlichkeit += anzahlGleicherZeilen * gewicht;
					a.setWert(aehnlichkeit);

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Verkettet sowohl Referenz- als auch Vergleichsdatei zu je einem String
	 * und vergleicht jeweils die Zeichenmengen miteinander. Anwendung: Wenn die
	 * Reihenfolge der Woerter keine gro�e Rolle spielt.
	 */
	@Override
	public void vergleicheUeberGanzesDokument(List<IAehnlichkeitImpl> batch) {
		for (IAehnlichkeitImpl a : batch) {
			ref = a.getVon();
			vgl = a.getZu();
			System.out.println(a.getId() + " in Thread " + Thread.currentThread().getName() + " mit Prio "+ Thread.currentThread().getPriority() + " | Aktiv: " + Thread.activeCount());

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

				double levenshtein = (double) berechneLevenshteinDistanz(
						referenzArray, vergleichsArray);

				double maxSize;
				if (referenzString.length() > vergleichsString.length()) {
					maxSize = referenzString.length();
				} else {
					maxSize = vergleichsString.length();
				}

				double metrik = (maxSize - levenshtein) / maxSize;
				a.setWert(metrik);

			} catch (IOException e) {
				e.printStackTrace();
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
	 *            enth�lt das Mapping der importierten Dateien auf die
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
	 * @param type
	 *            entscheidet ob Unterschied in die Liste fuer INSERT,DELETE
	 *            oder CHANGE geschrieben wird
	 * @return listOfChanges je nach Typ eine Liste mit neuen,geloeschten oder
	 *         geaenderten Zeilen
	 */
	private List<Chunk> getChunksByType(Delta.TYPE type) throws IOException {
		final List<Chunk> listOfChanges = new ArrayList<Chunk>();
		final List<Delta> deltas = getDeltas();
		for (Delta delta : deltas) {
			if (delta.getType() == type) {
				listOfChanges.add(delta.getRevised());
			}
		}
		return listOfChanges;
	}

	/**
	 * @return patch.getDeltas Gibt deltas der beiden verglichenen Files zurueck
	 */
	private List<Delta> getDeltas() throws IOException {

		final List<String> originalFileLines = fileToLines(ref);
		final List<String> revisedFileLines = fileToLines(vgl);

		final Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);

		return patch.getDeltas();
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

	/**
	 * Gibt Chunks die aus der Referenzdatei geaendert wurden als Liste zurueck
	 * 
	 * @param type
	 *            INSERT, DELETION, CHANGE
	 * @return listOfChanges eine Liste der ge�nderten Chunks
	 * @throws IOException
	 */
	private List<Chunk> getUnchangedChunksByType(Delta.TYPE type)
			throws IOException {
		final List<Chunk> listOfChanges = new ArrayList<Chunk>();
		final List<Delta> deltas = getDeltas();
		for (Delta delta : deltas) {
			if (delta.getType() == type) {
				listOfChanges.add(delta.getOriginal());
			}
		}
		return listOfChanges;
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
	private double ermittleGewicht(int refSize, int vglSize) {
		double gewicht = 0;
		if (refSize > vglSize) {
			gewicht = refSize;
		} else if (refSize <= vglSize) {
			gewicht = vglSize;
		}
		return gewicht;
	}

	/**
	 * Konvertiert Listen des Typs Chunk zu String Listen
	 * 
	 * @param chunkList
	 *            Zu konvertierende Liste
	 * @param stringList
	 *            Liste der die Eintraege angehaengt werden sollen
	 */
	private void changedChunkListToStringList(List<Chunk> chunkList,
			List<String> stringList) {
		for (Chunk c : chunkList) {
			for (int i = 0; i < c.getLines().size(); i++) {
				stringList.add((String) c.getLines().get(i));
			}
			if (c.getLines().size() == 0)
				stringList.add("");
		}
	}

	/**
	 * Berechnet die Aehnlichkeitsmetrix fuer geaenderte Zeilen
	 * 
	 * @param gewicht
	 *            die Gewichtung einer einzigen Zeile := 1/max. Anzahl der
	 *            Zeilen innerhalb der verglichenen Dateien
	 * @param refList
	 *            eine Liste mit allen Zeilen aus der Referenzdatei
	 * @param vglList
	 *            eine Liste mit allen Zeilen aus der Vergleichsdatei
	 * @return Array mit den berechneten Metriken
	 */
	private double[] berechneMetrik(double gewicht, List<String> refList,
			List<String> vglList) {
		int norm = normalizeStringLists(refList, vglList);
		int max = refList.size();
		if (norm == 0) {
			max = refList.size();
		} else if (norm == 1) {
			max = vglList.size();
		}

		double[] metrikProZeile = new double[max];
		for (int i = 0; i < max; i++) {
			char[] ref = refList.get(i).toCharArray();
			char[] vgl = vglList.get(i).toCharArray();
			int laengsterString;

			if (ref.length >= vgl.length) {
				laengsterString = ref.length;
			} else {
				laengsterString = vgl.length;
			}

			Arrays.sort(ref);
			Arrays.sort(vgl);

			double laengeDesLaengsten = (double) laengsterString;
			double levenshteinDist = (double) berechneLevenshteinDistanz(ref,
					vgl);
			if (laengeDesLaengsten != 0) {
				metrikProZeile[i] = gewicht
						* (double) ((laengeDesLaengsten - levenshteinDist) / laengeDesLaengsten);
			} else {
				metrikProZeile[i] = gewicht * 1.0;
			}

		}
		return metrikProZeile;
	}

	/**
	 * 
	 * @param refList
	 *            Liste mit Zeilen der Referenzdatei
	 * @param vglList
	 *            Liste mit Zahlen der Vergleichsdatei
	 * @return 0 wenn refList groe�er ist, 1 wenn vglList groe�er ist und 2 wenn
	 *         beide gleich gro� sind
	 */
	private int normalizeStringLists(List<String> refList, List<String> vglList) {
		int refListSize = refList.size();
		int vglListSize = vglList.size();
		int diff = Math.abs(refListSize - vglListSize);
		if (refListSize > vglListSize) {
			for (int i = 0; i < diff; i++) {
				vglList.add("");
			}
			return 0;
		} else if (refListSize < vglListSize) {
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
		batches = partition(paarungen, numThreads);


	}

	@Override
	public void mergeBatches(){
		paarungen.clear();
		for(int i = 0; i < batches.size(); i++){
			List<IAehnlichkeitImpl> currentBatch = batches.get(i).getInhalt();
			for(int j = 0; j < currentBatch.size(); j++){
				IAehnlichkeit currentComparison = currentBatch.get(j);
				paarungen.add(new IAehnlichkeitImpl(currentComparison.getVon(),
						currentComparison.getZu(), currentComparison.getWeight(), currentComparison.getId(), currentComparison.getWert()));
			}
		}
		Collections.sort(paarungen, new SortIAehnlichkeitByID());
	}

	public List<IMatrixImpl> partition(List<IAehnlichkeitImpl> iterable,
			int partitions) {
		batches = new ArrayList<>(partitions);
		for (int i = 0; i < partitions; i++)
			batches.add(new IMatrixImpl());

		Iterator<IAehnlichkeitImpl> iterator = iterable.iterator();
		for (int i = 0; iterator.hasNext(); i++)
			batches.get(i % partitions).getInhalt()
					.add((IAehnlichkeitImpl) iterator.next());

		return batches;
	}

	/**
	 * Berechnet die Levenshtein Distanz zwischen s1 und s2
	 * 
	 * @param s1
	 *            Referenz-String
	 * @param s2
	 *            Vergleichs-String
	 * @return die LevenShtein Distanz zwischen s1 und s2
	 */
	private int berechneLevenshteinDistanz(char[] s1, char[] s2) {

		// memoize only previous line of distance matrix
		int[] prev = new int[s2.length + 1];

		for (int j = 0; j < s2.length + 1; j++) {
			prev[j] = j;
		}

		for (int i = 1; i < s1.length + 1; i++) {

			// calculate current line of distance matrix
			int[] curr = new int[s2.length + 1];
			curr[0] = i;

			for (int j = 1; j < s2.length + 1; j++) {
				int d1 = prev[j] + 1;
				int d2 = curr[j - 1] + 1;
				int d3 = prev[j - 1];
				if (s1[i - 1] != s2[j - 1]) {
					d3 += 1;
				}
				curr[j] = Math.min(Math.min(d1, d2), d3);
			}

			// define current line of distance matrix as previous
			prev = curr;
		}
		return prev[s2.length];
	}

	private List<Chunk> getChangesFromOriginal() throws IOException {
		return getChunksByType(Delta.TYPE.CHANGE);
	}

	private List<Chunk> getChangesInReference() throws IOException {
		return getUnchangedChunksByType(Delta.TYPE.CHANGE);
	}

	private List<Chunk> getDeletesInReference() throws IOException {
		return getUnchangedChunksByType(Delta.TYPE.DELETE);
	}

	@Override
	public void setRef(File ref) {
		this.ref = ref;
	}

	@Override
	public void setVgl(File vgl) {
		this.vgl = vgl;
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
