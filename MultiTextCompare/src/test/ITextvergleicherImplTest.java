package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class ITextvergleicherImplTest {

	static List<File> textdateien;
	static ITextvergleicher iText;
	List<IAehnlichkeitImpl> paarungen;

	@BeforeClass
	public static void beforeAllTests() {
		textdateien = new ArrayList<File>();
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileA.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileB.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileC.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileD.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileE.txt"));

		iText = new ITextvergleicherImpl();
	}

	@Test
	public void testVergleichsalorithmus() {
		System.out.println("Vergleiche für Matrixaufbau");
		paarungen = iText.getVergleiche(textdateien);
		assertEquals(10, paarungen.size());

		for (IAehnlichkeitImpl s : paarungen) {
			System.out.println(s.getVon().getName()
					.replaceFirst("[.][^.]+$", "")
					+ "," + s.getZu().getName().replaceFirst("[.][^.]+$", ""));
		}
		System.out.println("");
	}

	@Test
	public void test_vergleichWortFuerWort() {
		System.out.println("Vergleiche WortFuerWort");
		paarungen = iText.getVergleiche(textdateien);
		double[] aehnlichkeit = new double[paarungen.size()];
		iText.vergleicheZeilenweise();

		for (int i = 0; i < paarungen.size(); i++) {
			aehnlichkeit[i] = paarungen.get(i).getWert();
			System.out.println("Von "
					+ paarungen.get(i).getVon().getName()
							.replaceFirst("[.][^.]+$", "")
					+ " Zu "
					+ paarungen.get(i).getZu().getName()
							.replaceFirst("[.][^.]+$", "")
					+ " mit Ähnlichkeit " + aehnlichkeit[i]);
		}
		System.out.println("");
		assertEquals(0.5, aehnlichkeit[4], 0.0000001);
	}

	@Test
	public void test_vergleichZeichenmenge() {
		System.out.println("Vergleiche Zeichenmenge");
		paarungen = iText.getVergleiche(textdateien);
		double[] aehnlichkeit = new double[paarungen.size()];

		iText.vergleicheUeberGanzesDokument();

		for (int i = 0; i < paarungen.size(); i++) {
			aehnlichkeit[i] = paarungen.get(i).getWert();
			System.out.println("Von "
					+ paarungen.get(i).getVon().getName()
							.replaceFirst("[.][^.]+$", "")
					+ " Zu "
					+ paarungen.get(i).getZu().getName()
							.replaceFirst("[.][^.]+$", "")
					+ " mit Ähnlichkeit " + aehnlichkeit[i]);
		}
		System.out.println("");
		assertEquals(0.5, aehnlichkeit[4], 0.0000001);
	}

}
