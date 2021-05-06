package de.thkoeln.syp.mtc.datenhaltung.api;

import java.util.Comparator;
import java.util.List;

import org.jdom2.Element;

public class IXMLElementComparator implements Comparator<Element> {

	@Override
	public int compare(Element e1, Element e2) {
		// Tagnamen gleich
		if (e1.getName().compareTo(e2.getName()) == 0) {
			// Values ungleich
			if (e1.getText().compareTo(e2.getText()) != 0) {
				// return Value-Vergleich
				return e1.getText().compareTo(e2.getText());
			} else {

				// Values gleich
				int returnCompareAttributes;
				if ((returnCompareAttributes = compareAttributes(e1, e2)) != 0) {
					return returnCompareAttributes;
				} else {
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
			return e1.getAttributes().get(0).getValue()
					.compareTo(e2.getAttributes().get(0).getValue());
		} else if (e1.hasAttributes() && !e2.hasAttributes()) {
			return e1.getAttributes().get(0).getValue().compareTo("");
		} else if (!e1.hasAttributes() && e2.hasAttributes()) {
			return "".compareTo(e2.getAttributes().get(0).getValue());
		} else {
			return 0;
		}
	}

	public int compareElementsForChildren(Element e1, Element e2) {
		List<Element> childrenE1 = e1.getChildren();
		List<Element> childrenE2 = e2.getChildren();
		if (!hasChildren(e1) || !hasChildren(e2)) {
			return 0;
		}
		if (childrenE1.size() == childrenE2.size()) {
			Element left = childrenE1.get(0);
			Element right = childrenE2.get(0);
			if (hasChildren(left) || hasChildren(right)) {
				compareElementsForChildren(left, right);
			} else {
				Element leftParent = (Element) left.getParent();
				Element rightParent = (Element) right.getParent();
				// if elements do not have children, compare parent names
				if (leftParent.getName().compareTo(rightParent.getName()) == 0) {
						// if parents are equal compare current element values
						if (left.getText().compareTo(right.getText()) != 0) {
							return left.getText().compareTo(right.getText());
						} else {
	
							int returnCompareAttributes;
							if ((returnCompareAttributes = compareAttributes(left,
									right)) != 0) {
								return returnCompareAttributes;
							} else {
								return compareElementsForChildren(left, right);
							}
						}
					}
					else {
						System.out.println("hallo");
						return left.getName().compareTo(right.getName());
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
