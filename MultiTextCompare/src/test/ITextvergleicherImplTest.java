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
		textdateien.add(new File(System.getProperty("user.dir") + File.separator +  "/src/test/testFiles/FileA.txt"));
		textdateien.add(new File(System.getProperty("user.dir") + File.separator +  "/src/test/testFiles/FileB.txt"));
		textdateien.add(new File(System.getProperty("user.dir") + File.separator +  "/src/test/testFiles/FileC.txt"));
		textdateien.add(new File(System.getProperty("user.dir") + File.separator +  "/src/test/testFiles/FileD.txt"));
		iText = new ITextvergleicherImpl();
	}

	@Test
	public void testVergleichsalorithmus() {
		paarungen = iText.getVergleiche(textdateien);
		assertEquals(6, paarungen.size());

		for (IAehnlichkeitImpl s : paarungen) {
			System.out
					.println(s.getVon().getName().replaceFirst("[.][^.]+$", "") + "," + s.getZu().getName().replaceFirst("[.][^.]+$", ""));
		}
	}

	@Test
	public void test_vergleich() {
		double[] ähnlichkeit = new double[6];
		paarungen = iText.getVergleiche(textdateien);
		iText.vergleiche();
		
		for(int i = 0; i < paarungen.size();i++){
			ähnlichkeit[i] = paarungen.get(i).getWert();
			System.out.println(ähnlichkeit[i]);
		}
		assertEquals(0.5 , ähnlichkeit[3], 0.0000001);
	}

}
