package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.Comparator;
import org.jdom2.Element;

public class IXMLElementComparator implements Comparator<Element>{
	
		@Override
		public int compare(Element e1, Element e2) {
			return e1.getName().compareTo(e2.getName());
		}
		
}
