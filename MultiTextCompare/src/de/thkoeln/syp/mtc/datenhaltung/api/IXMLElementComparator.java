package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.Comparator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class IXMLElementComparator implements Comparator<Element> {

	@Override
	public int compare(Element e1, Element e2) {
		if (e1.getName().compareTo(e2.getName()) == 0) {
			return e1.getValue().compareTo(e2.getValue());
		}
		return e1.getName().compareTo(e2.getName());
	}

}
