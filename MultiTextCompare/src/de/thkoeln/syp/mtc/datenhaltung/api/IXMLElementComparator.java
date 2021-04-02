package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.Comparator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class IXMLElementComparator implements Comparator<Element> {

	@Override
	public int compare(Element e1, Element e2) {
		//Tagnamen gleich
		if (e1.getName().compareTo(e2.getName()) == 0) {
			//Values ungleich
			if (e1.getValue().compareTo(e2.getValue()) != 0) {
				//return Value-Vergleich
				return e1.getValue().compareTo(e2.getValue());
			}
			else {
				//Values gleich
				compareAttributes(e1, e2);
			}
		}
		//return Tag-Vergleich
		return e1.getName().compareTo(e2.getName());
	}
	
	public int compareAttributes(Element e1, Element e2){
		if(e1.hasAttributes() || e2.hasAttributes()) {
			//return Vergleich des ersten Attribut-Values
			return e1.getAttributes().get(0).getValue().compareTo(e2.getAttributes().get(0).getValue());
		}
		else if(e1.hasAttributes() && !e2.hasAttributes()) {
			return e1.getAttributes().get(0).getValue().compareTo("");
		}
		else if(!e1.hasAttributes() && e2.hasAttributes()) {
			return "".compareTo(e2.getAttributes().get(0).getValue());
		}
		else {
			return 0;
		}
	}

}
