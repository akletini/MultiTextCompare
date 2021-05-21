package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.Comparator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class IXMLElementComparator implements Comparator<Element> {

	@Override
	public int compare(Element e1, Element e2) {
		// Tagnamen gleich
		if (e1.getName().compareTo(e2.getName()) == 0) {
			// Prüfe Attribute
			int returnCompareAttributes;
			if ((returnCompareAttributes = compareAttributes(e1, e2)) != 0) {
				return returnCompareAttributes;
			} else {
				// prüfe Text-Inhalt
				if (e1.getTextTrim().compareTo(e2.getTextTrim()) != 0) {
					return e1.getTextTrim().compareTo(e2.getTextTrim());
				} else {
					// sonst betrachte Kindknoten
					int returnRecursiveSearch = compareElementsForChildren(e1,
							e2);
					return returnRecursiveSearch;
				}
			}
		}
		// return Tag-Vergleich
		return e1.getName().compareTo(e2.getName());
	}

	public int compareAttributes(Element e1, Element e2) {
		if (e1.hasAttributes() && e2.hasAttributes()) {
			// return Vergleich des ersten Attribut-Values
			List<Attribute> attr1, attr2;
			attr1 = e1.getAttributes();
			attr2 = e2.getAttributes();
			int minSize = Math.min(e1.getAttributes().size(), e2
					.getAttributes().size());
			for (int i = 0; i < minSize; i++) {
				Attribute a1 = attr1.get(i);
				Attribute a2 = attr2.get(i);

				if (a1.getValue().compareTo(a2.getValue()) == 0) {
					continue;
				}
				return a1.getValue().compareTo(a2.getValue());
			}
			if (attr1.size() > attr2.size()) {
				return 1;
			}
			if (attr1.size() < attr2.size()) {
				return -1;
			}
		}
		return 0;
	}

	public int compareElementsForChildren(Element e1, Element e2) {
		List<Element> childrenE1 = e1.getChildren();
		List<Element> childrenE2 = e2.getChildren();
		if (!hasChildren(e1) || !hasChildren(e2)) {
			return 0;
		}
		int minSize = Math.min(childrenE1.size(), childrenE2.size());
		for (int i = 0; i < minSize; i++) { // rework this
			Element left = childrenE1.get(i);
			Element right = childrenE2.get(i);

			Element leftParent = (Element) left.getParent();
			Element rightParent = (Element) right.getParent();
			// if elements do not have children, compare parent names
			if (leftParent.getName().compareTo(rightParent.getName()) == 0) {
				// if parents are equal compare current element attributes
				int returnCompareAttributes;
				if ((returnCompareAttributes = compareAttributes(left, right)) != 0) {
					return returnCompareAttributes;
				} else {
					// for equal attributes, check text content
					if (left.getTextTrim().compareTo(right.getTextTrim()) != 0) {
						return left.getTextTrim().compareTo(right.getTextTrim());
					}
					else if (hasChildren(left) || hasChildren(right)) {
						int returnRecursive;
						returnRecursive = compareElementsForChildren(left, right);
						if(returnRecursive != 0){
							return returnRecursive;
						}
					}
				}
			}

		}
		return 0;
	}

	public boolean hasChildren(Element e) {
		if (e.getChildren().size() == 0) {
			return false;
		} else {
			return true;
		}
	}

}
