package de.thkoeln.syp.mtc.datenhaltung.api;

import org.jdom2.Attribute;

import java.util.Comparator;

public class IXMLAttributeComparator implements Comparator<Attribute> {
	
	@Override
	public int compare(Attribute a1, Attribute a2) {
		return a1.getName().compareTo(a2.getName());
	}
	
}

