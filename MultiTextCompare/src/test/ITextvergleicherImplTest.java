package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class ITextvergleicherImplTest {
	
	static List<File> textdateien;
	static ITextvergleicher iText;
	
	@BeforeClass
	public static void beforeAllTests(){
		textdateien = new ArrayList<File>();
		textdateien.add(new File("a"));
		textdateien.add(new File("b"));
		textdateien.add(new File("c"));
		textdateien.add(new File("d"));
		textdateien.add(new File("e"));
		
		iText = new ITextvergleicherImpl();
	}
	
	@Test
	public void testVergleichsalorithmus() {
		List<IAehnlichkeitImpl> paarungen = iText.getVergleiche(textdateien);
		assertEquals(10, paarungen.size());
		
		for(IAehnlichkeitImpl s : paarungen){
			System.out.println(s.getVon() + "," + s.getZu());
		}
	}

}
