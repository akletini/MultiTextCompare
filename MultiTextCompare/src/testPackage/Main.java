package testPackage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import difflib.Chunk;

public class Main {

	private static final File a = new File("F:\\FileA.txt");
	private static final File b = new File("F:\\FileB.txt");
	private static final File c = new File("F:\\FileC.txt");
	private static final File d = new File("F:\\FileD.txt");

	public static void main(String[] args) {
//		new Main().machWat();
		 ITextvergleicherImpl i = new ITextvergleicherImpl();
//		 List<File> files = new ArrayList<File>();
//		 files.add(a);
//		 files.add(b);
//		 files.add(c);
//		 files.add(d);
//		 List<IAehnlichkeitImpl> paarungen = i.getVergleiche(files);
//		 i.vergleicheUeberGanzesDokument();
//		 i.vergleicheZeilenweise();
//		 
//		 for(IAehnlichkeitImpl s : paarungen){
//			 	i.vergleiche();
//				System.out.println(s.getVon().getName() + "," + s.getZu().getName() + "Wert: " + s.getWert());
//			}
//		 char[] c1 = "Ich habe ein groﬂes Problem".toCharArray();
//		 char[] c2 = "Ich habe Problem".toCharArray();
//		 System.out.println(dist(c1,c2));
	}

//	public void machWat() {
//		final ITextvergleicherImpl comp = new ITextvergleicherImpl(a, b);
//
//		try {
//			final List<Chunk> changesFromOriginal = comp
//					.getChangesFromOriginal();
//			final List<Chunk> insertsFromOriginal = comp
//					.getInsertsFromOriginal();
//			final List<Chunk> deletesFromOriginal = comp
//					.getDeletesFromOriginal();
//
//			for (int i = 0; i < changesFromOriginal.size(); i++) {
//				System.out.println("Changes" + changesFromOriginal.get(i));
//			}
//			for (int i = 0; i < insertsFromOriginal.size(); i++) {
//				System.out.println("Inserts" + insertsFromOriginal.get(i));
//			}
//			for (int i = 0; i < deletesFromOriginal.size(); i++) {
//				System.out.println("Deletes" + deletesFromOriginal.get(i));
//			}
//			
//			final List<Chunk> unchangedChunks = comp.getChangesInReference();
//			final List<Chunk> insertedChunks = comp.getInsertsInReference();
//			final List<Chunk> deletedChunks = comp.getDeletesInReference();
//			
//			for (int i = 0; i < unchangedChunks.size(); i++) {
//				System.out.println("Changes" + unchangedChunks.get(i));
//				System.out.println("size of changes " + unchangedChunks.get(i).getLines().size());
//			}
//			for (int i = 0; i < insertedChunks.size(); i++) {
//				System.out.println("Inserts" + insertedChunks.get(i));
//			}
//			for (int i = 0; i < deletedChunks.size(); i++) {
//				System.out.println("Deletes" + deletedChunks.get(i));
//			}
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/**
	 * Berechnet die Levenshtein Distanz zwischen s1 und s2
	 * 
	 * @param s1
	 *            Referenz-String
	 * @param s2
	 *            Vergleichs-String
	 * @return die LevenShtein Distanz zwischen s1 und s2
	 */
	public static int dist(char[] s1, char[] s2) {

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
}
