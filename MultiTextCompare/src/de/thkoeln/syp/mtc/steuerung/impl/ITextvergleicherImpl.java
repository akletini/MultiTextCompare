package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.api.IAehnlichkeit;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;

public class ITextvergleicherImpl implements ITextvergleicher {

	private IMatrixImpl iMatrixImpl;
	private List<IAehnlichkeitImpl> paarungen;
	private List<String> referenzZeilen;
	private List<String> vergleichsZeilen;
	private File ref,vgl;

	public ITextvergleicherImpl(File ref, File vgl) {
		this.ref = ref;
		this.vgl = vgl;
	}
	
	public ITextvergleicherImpl() {
		
	}

	
	private List<Chunk> getChangesFromOriginal() throws IOException {
		return getChunksByType(Delta.TYPE.CHANGE);
	}

	
	private List<Chunk> getInsertsFromOriginal() throws IOException {
		return getChunksByType(Delta.TYPE.INSERT);
	}

	
	private List<Chunk> getDeletesFromOriginal() throws IOException {
		return getChunksByType(Delta.TYPE.DELETE);
	}

	
	private List<Chunk> getChangesInReference() throws IOException {
		return getUnchangedChunksByType(Delta.TYPE.CHANGE);
	}

	
	private List<Chunk> getInsertsInReference() throws IOException {
		return getUnchangedChunksByType(Delta.TYPE.INSERT);
	}

	
	private List<Chunk> getDeletesInReference() throws IOException {
		return getUnchangedChunksByType(Delta.TYPE.DELETE);
	}

	/**
	 * @param type
	 *            entscheidet ob Unterschied in die Liste für INSERT,DELETE oder
	 *            CHANGE geschrieben wird
	 * @return listOfChanges je nach Typ eine Liste mit neuen,gelöschten oder
	 *         geänderten Zeilen
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
	 * @return patch.getDeltas Gibt deltas der beiden verglichenen Files zurück
	 */
	private List<Delta> getDeltas() throws IOException {

		final List<String> originalFileLines = fileToLines(ref);
		final List<String> revisedFileLines = fileToLines(vgl);

		final Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);

		return patch.getDeltas();
	}

	/**
	 * Liest Datei zeilenweise aus und speichert die Zeilen in lines
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
	public void vergleiche() {
		for (IAehnlichkeit a : paarungen) {
			this.ref = a.getVon();
			this.vgl = a.getZu();
			double gewicht = 0, aehnlichkeit = 0;
			if (a.getVon() == a.getZu()) {
				a.setWert(1);
			} else {
				ITextvergleicherImpl comp = new ITextvergleicherImpl(a.getVon(), a.getZu());
				try {
					List<String> refList = fileToLines(a.getVon());
					List<String> vglList = fileToLines(a.getZu());

					gewicht = 1 / ermittleGewicht(refList.size(), vglList.size());

					List<Chunk> changedChunks = comp
							.getChangesFromOriginal();

					List<Chunk> deletedChunks = comp
							.getDeletesInReference();

					List<Chunk> unchangedChunks = comp
							.getChangesInReference();

					int anzahlGleicherZeilen = 0, anzahlGelöschterZeilen = 0, anzahlGeänderterZeilen = 0;
					for (int i = 0; i < deletedChunks.size(); i++) {
						anzahlGelöschterZeilen += deletedChunks.get(i)
								.getLines().size();
					}

					for (int i = 0; i < unchangedChunks.size(); i++) {
						anzahlGeänderterZeilen += unchangedChunks.get(i)
								.getLines().size();
					}
					
					anzahlGleicherZeilen = refList.size() - anzahlGelöschterZeilen
							- anzahlGeänderterZeilen;

					if (changedChunks.size() == 0) {
						aehnlichkeit = anzahlGleicherZeilen * gewicht;
						a.setWert(aehnlichkeit);
						
					}
					else {

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
	}

	private double ermittleGewicht(int refSize, int vglSize) {
		double gewicht = 0;
		if (refSize > vglSize) {
			gewicht = refSize;
		} else if (refSize <= vglSize) {
			gewicht = vglSize;
		}
		return gewicht;
	}

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
	 * Berechnet die Aehnlichkeitsmetrix für geänderte Zeilen
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
		int max = 2;
		if(norm == 0){
			max = refList.size();
		}
		else if(norm == 1){
			max = vglList.size();
		}
		
			
		double[] metrikProZeile = new double[vglList.size()];
		for (int i = 0; i < max; i++) {
			String ref = refList.get(i);
			String vlg = vglList.get(i);
			String laengsterString;

			if (ref.length() > vlg.length()) {
				laengsterString = ref;
			} else if (ref.length() < vlg.length()) {
				laengsterString = vlg;
			} else {
				laengsterString = ref;
			}

			double laengeDesLaengsten = (double) laengsterString.length();
			double levenshteinDist = (double) berechneLevenshteinDistanz(
					ref.toCharArray(), vlg.toCharArray());
			metrikProZeile[i] = gewicht
					* (double) ((laengeDesLaengsten - levenshteinDist) / laengeDesLaengsten);

		}
		return metrikProZeile;
	}
	
	/**
	 * 
	 * @param refList Liste mit Zeilen der Referenzdatei
	 * @param vglList Liste mit Zahlen der Vergleichsdatei
	 * @return 0 wenn refList größer ist, 1 wenn vglList größer ist und 2 wenn beide gleich groß sind
	 */
	private int normalizeStringLists(List<String> refList, List<String> vglList){
		int refListSize = refList.size();
		int vglListSize = vglList.size();
		int diff = Math.abs(refListSize - vglListSize);
		if(refListSize > vglListSize){
			for(int i = 0; i < diff; i++){
				vglList.add("");
			}
			return 0;
		}
		else if (refListSize < vglListSize){
			for(int i = 0; i < diff; i++){
				refList.add("");
			}
			return 1;
		}
		return 2;
		
	}

	/**
	 * @param files die Liste der Textdateien die für den Vergleich
	 * 				ausgewählt wurden           
	 * @return paarungen die Einträge der Ähnlichkeitsmatrix ohne den
	 *         	  		 Ähnlichkeitswert
	 */
	public List<IAehnlichkeitImpl> getVergleiche(List<File> files) {
		paarungen = new ArrayList<IAehnlichkeitImpl>();
		IAehnlichkeit vergleich = new IAehnlichkeitImpl();
		for (int i = 0; i < files.size(); i++) {
			for (int j = i + 1; j < files.size(); j++) {
				vergleich = new IAehnlichkeitImpl();
				vergleich.setVon(files.get(i));
				vergleich.setZu(files.get(j));
				paarungen.add((IAehnlichkeitImpl) vergleich);
			}
		}

		return paarungen;
	}

	/**
	 * Berechnet die Levenshtein Distanz zwischen s1 und s2
	 * @param s1 Referenz-String          
	 * @param s2 Vergleichs-String           
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
	
}
