package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

/**
 * Verantwortlich für den strukturellen Vergleich von XML-Dateien
 * 
 * @author Allen Kletinitch
 *
 */
public class IXMLComparerImpl {

	private int maxLineLength;
	private ITextvergleicher textvergleicher;
	private IFileImporter fileImporter;
	private List<Double> similarities;
	private boolean commentFound, rootHasNamespaces;

	public IXMLComparerImpl(IFileImporter fileImporter) {
		textvergleicher = new ITextvergleicherImpl();
		this.fileImporter = fileImporter;
		this.maxLineLength = fileImporter.getConfig().getMaxLineLength();
	}

	/**
	 * Hauptmethode des XML-Vergleichs. Liest die beiden Dateibäume der
	 * uebergebenen Dateien ein und ruft Methoden zur Berechnung der
	 * Aehnlichkeit auf
	 * 
	 * @param ref
	 *            Referenzdatei
	 * @param comp
	 *            Vergleichsdatei
	 * @return Aehnlichkeit der beiden Dateien
	 * @throws IOException
	 */
	public double compare(File ref, File comp) throws IOException,
			JDOMException {
		Document docRef, docComp;
		SAXBuilder builder = new SAXBuilder();
		Element rootRef, rootComp;
		docRef = builder.build(ref);
		docComp = builder.build(comp);

		rootRef = docRef.getRootElement();
		rootComp = docComp.getRootElement();
		
		rootHasNamespaces = rootHasNameSpace(rootRef, rootComp);

		double similarity = compareElementsRecursively(rootRef, rootComp, 1.0);
		return similarity;
	}

	/**
	 * Berechnet die Aehnlichkeit von Elementen
	 * 
	 * @param ref
	 *            Referenzknoten
	 * @param comp
	 *            Vergleichsknoten
	 * @param currentLevelWeight
	 *            Gewicht fuer Knoten der aktuellen Ebene
	 * @return Aehnlichkeit der beiden Knoten
	 */
	private double compareElements(Element ref, Element comp,
			double currentLevelWeight) {
		boolean compareComments = fileImporter.getConfig()
				.getXmlCompareComments();
		boolean attributesPresent = hasAttributes(ref) || hasAttributes(comp);
		boolean textPresent = hasText(ref) || hasText(comp);
		double contentWeight = 0.5;
		double textWeight = 0.5;
		double attributeWeight = 0.5;
		double totalSimilarity = 0, textSimilarity, attributeSimilarity;
		commentFound = false;

		List<Content> refContent = ref.getContent();
		List<Content> compContent = comp.getContent();
		double contentSim = currentLevelWeight
				* compareContent(refContent, compContent);
		if (textPresent && commentFound && attributesPresent) {
			textWeight = 1.0 / 3.0;
			attributeWeight = textWeight;
			contentWeight = attributeWeight;
		}
		String refNorm = ref.getTextNormalize();
		String compNorm = comp.getTextNormalize();
		double stringSimilarity = compareStrings(refNorm, compNorm);

		textSimilarity = currentLevelWeight * textWeight * stringSimilarity;
		attributeSimilarity = currentLevelWeight
				* attributeWeight
				* calcAttributeSimilarity(ref.getAttributes(),
						comp.getAttributes());
		contentSim = contentWeight * contentSim;

		if (!compareComments) {
			if (attributesPresent && textPresent) {
				totalSimilarity = textSimilarity + attributeSimilarity;
			} else if (attributesPresent && !textPresent) {
				totalSimilarity = 2 * attributeSimilarity;
			} else if (!attributesPresent && !textPresent) {
				totalSimilarity = -1;
			} else {
				totalSimilarity = 2 * textSimilarity;
			}
		} else {
			// attribute + text + kein Kommentar
			if (attributesPresent && textPresent && !commentFound) {
				totalSimilarity = textSimilarity + attributeSimilarity;
			}
			// attribute + text + kommentar
			else if (attributesPresent && textPresent && commentFound) {
				totalSimilarity = textSimilarity + attributeSimilarity
						+ contentSim;
			}
			// keine attribute + text + kein kommentar
			else if (!attributesPresent && textPresent && !commentFound) {
				totalSimilarity = 2 * textSimilarity;
			}
			// keine attribute + text + kommentar
			else if (!attributesPresent && textPresent && commentFound) {
				totalSimilarity = textSimilarity + contentSim;
			}
			// attribute + kein text + kein Kommentar
			else if (attributesPresent && !textPresent && !commentFound) {
				totalSimilarity = 2 * attributeSimilarity;
			}
			// attribute + kein text + kommentar
			else if (attributesPresent && !textPresent && commentFound) {
				totalSimilarity = attributeSimilarity + contentSim;
			}
			// keine attribute + kein text + kein kommentar
			else if (!attributesPresent && !textPresent && !commentFound) {
				totalSimilarity = -1;
			}
			// keine attribute + kein text + kommentar
			else if (!attributesPresent && !textPresent && commentFound) {
				totalSimilarity = 2 * contentSim;
			}
		}

		return totalSimilarity;
	}

	/**
	 * Berechnet die Aehnlichkeit von zwei Attributlisten
	 * 
	 * @param refAttr
	 *            Referenzattribute
	 * @param compAttr
	 *            Vergleichsattribute
	 * @return Aehnlichkeit der Attributslisten
	 */
	private double calcAttributeSimilarity(List<Attribute> refAttr,
			List<Attribute> compAttr) {
		List<Attribute> matchingRef = new ArrayList<Attribute>();
		List<Attribute> matchingComp = new ArrayList<Attribute>();
		double similarity = 0;
		int refSize = refAttr.size();
		int compSize = compAttr.size();
		double maxSize = (double) Math.max(refSize, compSize);

		// if either list is empty
		if (refSize == 0 && compSize == 0) {
			return 1.0;
		} else if (refSize == 0 || compSize == 0) {
			return 0.0;
		}

		// Get machting attribute names
		for (Attribute refAttribute : refAttr) {
			for (Attribute compAttribute : compAttr) {
				if (refAttribute.getName().equals(compAttribute.getName())) {
					
					matchingRef.add(refAttribute);
					matchingComp.add(compAttribute);
				}
			}
		}

		for (int i = 0; i < matchingRef.size(); i++) {
			similarity += (1.0 / maxSize)
					* compareStrings(matchingRef.get(i).getValue(),
							matchingComp.get(i).getValue());
		}

		return similarity;
	}

	/**
	 * Ruft wo noetig die compareElements-Methode auf und matcht wo moeglich
	 * Elementeintraege in Elementlisten
	 * 
	 * @param ref
	 *            Referenzknoten
	 * @param comp
	 *            Vergleichsknoten
	 * @param currentLevelWeight
	 *            Gewicht der Knoten in aktueller Ebene
	 * @return Aehnlichkeit der beiden Knoten
	 */
	private double compareElementsRecursively(Element ref, Element comp,
			double currentLevelWeight) {
		List<Double> similarities = new ArrayList<Double>();
		List<Element> matchingRef = new ArrayList<Element>();
		List<Element> matchingComp = new ArrayList<Element>();
		boolean adjustNodeSimilarity = false;

		double currentWeight = calcLevelWeight(ref, comp);

		// get all elements with equal names which exist in both files
		List<String> matchedElementNames = new ArrayList<String>();
		for (int i = 0; i < ref.getChildren().size(); i++) {
			Element currentRef = ref.getChildren().get(i);
			String currentRefName = currentRef.getName();
			matchedElementNames.add(currentRefName);
			if (getElementCount(matchedElementNames, currentRefName) == 1
					&& comp.getChildren(currentRefName,
							currentRef.getNamespace()).size() != 0) {
				matchingRef.addAll(ref.getChildren(currentRefName,
						currentRef.getNamespace()));
				matchingComp.addAll(comp.getChildren(currentRefName,
						currentRef.getNamespace()));
			}
		}

		// look for equal elements and remove them from the matched pool
		for (int i = 0; i < matchingRef.size(); i++) {
			Element currentRef = matchingRef.get(i);
			for (int j = 0; j < matchingComp.size(); j++) {
				Element currentComp = matchingComp.get(j);
				if (currentComp == null) {
					continue;
				}
				XMLOutputter xmlOut = new XMLOutputter();
				String refString = xmlOut.outputString(currentRef);
				String compString = xmlOut.outputString(currentComp);
				boolean equals = refString.equals(compString);
				if (equals) {
					similarities.add(currentLevelWeight * currentWeight);
					matchingRef.set(i, null);
					matchingComp.set(j, null);
					break;
				}
			}
		}

		double currentNodeSim = compareElements(ref, comp, currentWeight);
		if (currentNodeSim != -1) {
			currentNodeSim *= currentLevelWeight;
			adjustNodeSimilarity = true;

		}
		matchingRef = clearNullValues(matchingRef);
		matchingComp = clearNullValues(matchingComp);

		int minSize = Math.min(matchingRef.size(), matchingComp.size());

		// compare all leftover elements by index
		for (int i = 0; i < minSize; i++) {
			Element currentRef = matchingRef.get(i);
			Element currentComp = matchingComp.get(i);

			if (hasChildren(currentRef) && hasChildren(currentComp)) {
				double similarity = currentLevelWeight
						* compareElementsRecursively(currentRef, currentComp,
								currentWeight);

				similarities.add(similarity);
			} else if (!hasChildren(currentRef) && !hasChildren(currentComp)) {
				double similarity = currentLevelWeight
						* compareElements(currentRef, currentComp,
								currentWeight);
				similarities.add(similarity);
			}
		}
		double sim = 0.0;
		for (Double s : similarities) {
			sim += s;
		}
		if (adjustNodeSimilarity && !rootHasNamespaces && !(ref.isRootElement() && comp.isRootElement())) {
			sim = sim * similarities.size() / (similarities.size() + 1);
			sim += currentNodeSim / (similarities.size() + 1);
		}
		this.similarities = similarities;
		return sim;
	}

	/**
	 * Vergleicht den textuellen Inhalt von Kommentaren
	 * 
	 * @param refContent
	 *            Contentliste des Referenzknotens
	 * @param compContent
	 *            Contentliste des Vergleichsknotens
	 * @return Aehnlichkeit der Kommentare
	 */
	private double compareContent(List<Content> refContent,
			List<Content> compContent) {
		boolean useComments = fileImporter.getConfig()
				.getXmlCompareComments();

		double totalCommentsLeft = getCommentCount(refContent);
		double totalCommentsRight = getCommentCount(compContent);
		double maxSize = Math.max(totalCommentsLeft, totalCommentsRight);
		List<Double> similaritiesComment = new ArrayList<Double>();
		for (int i = 0; i < refContent.size(); i++) {
			Object ref = refContent.get(i);
			for (int j = 0; j < compContent.size(); j++) {
				Object comp = compContent.get(j);
				if (ref instanceof Comment && comp instanceof Comment) {
					double sim = compareComments((Comment) ref, (Comment) comp);
					similaritiesComment.add(sim);
					break;
				}
			}
		}
		double sim = 0;
		if (useComments) {
			if (maxSize != 0) {
				sim += sumSimilarities(similaritiesComment) / maxSize;
				commentFound = true;
			}
		}
		return sim;
	}

	/**
	 * Gibt die Anzahl der Kommentare in einer Contentliste zurueck
	 * 
	 * @param content
	 * @return
	 */
	private double getCommentCount(List<Content> content) {
		double count = 0.0;
		for (Object o : content) {
			if (o instanceof Comment) {
				count++;
			}
		}
		return (double) count;
	}

	/**
	 * Gibt die Äehnlichkeit zweier Kommentare zurueck
	 * 
	 * @param ref
	 *            Referenzkommentar
	 * @param comp
	 *            Vergleichskommentar
	 * @return
	 */
	private double compareComments(Comment ref, Comment comp) {
		return compareStrings(ref.getText(), comp.getText());
	}

	/**
	 * Vergleicht zwei Strings und gibt deren Aehnlichkeit zurueck
	 * 
	 * @param ref
	 *            Referenzstring
	 * @param comp
	 *            Vergleichsstring
	 * @return Aehnlichkeit der Strings
	 */
	private double compareStrings(String ref, String comp) {
		double similarity = 1.0;
		double maxLength = Math.max(ref.length(), comp.length());
		if (maxLength > 0.0) {
			similarity = 1 - (textvergleicher.calculateLevenshteinDist(ref,
					comp, maxLineLength) / maxLength);
		}
		return similarity;
	}

	/**
	 * Berechnet das Gewicht für die Knoten der aktuellen Ebene
	 * 
	 * @param rootRef
	 *            Referenzknoten
	 * @param rootComp
	 *            Vergleichsknoten
	 * @return Das aktuelle Gewicht fuer Knoten der Ebene
	 */
	private double calcLevelWeight(Element ref, Element comp) {
		double levelWeight = 0.0;
		int nodeCountLeft = ref.getChildren().size();
		int nodeCountRight = comp.getChildren().size();
		int maxNodeCount = Math.max(nodeCountLeft, nodeCountRight);
		levelWeight = 1 / (double) maxNodeCount;
		return levelWeight;
	}

	/**
	 * Prueft ob ein Element Kinder hat
	 * 
	 * @param e
	 * @return
	 */
	private boolean hasChildren(Element e) {
		if (e.getChildren().size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Prueft ob ein Element Attribute hat
	 * 
	 * @param e
	 * @return
	 */
	private boolean hasAttributes(Element e) {
		if (e.getAttributes().size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Prueft ob ein Element Text hat
	 * 
	 * @param e
	 * @return
	 */
	private boolean hasText(Element e) {
		if (e.getTextNormalize().equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * Summiert alle Teilaehnlichkeiten einer Liste auf
	 * 
	 * @param similarities
	 * @return Gesamtaehnlichkeit
	 */
	private double sumSimilarities(List<Double> similarities) {
		double similarity = 0.0;
		for (Double d : similarities) {
			similarity += d;
		}
		return similarity;
	}

	/**
	 * Kopiert Elementliste und lässt null-Knoten aus
	 * 
	 * @param original
	 * @return
	 */
	private List<Element> clearNullValues(List<Element> original) {
		List<Element> returnList = new ArrayList<Element>();
		for (Element e : original) {
			if (e != null) {
				returnList.add(e);
			}
		}
		return returnList;
	}

	/**
	 * Zaehlt wie oft ein Element mit einem bestimmten Namen in einer Liste
	 * auftaucht
	 * 
	 * @param original
	 *            Liste die durchsucht werden soll
	 * @param name
	 * @return Die Anzahl der Elemente mit dem Nanem
	 */
	private int getElementCount(List<String> original, String name) {
		int count = 0;
		for (String s : original) {
			if (s.equals(name)) {
				count++;
			}
		}
		return count;
	}
	
	private boolean rootHasNameSpace(Element rootRef, Element rootComp){
		boolean hasNS = false;
		
	    if(!rootRef.getAttributes().isEmpty() && !rootComp.getAttributes().isEmpty()){
			Attribute refAttr = rootRef.getAttributes().get(0);
			Attribute compAttr = rootComp.getAttributes().get(0);
			
			if(!refAttr.getNamespacesInScope().isEmpty() && !compAttr.getNamespacesInScope().isEmpty()){
				return true;
			}
		}
		
		return hasNS;
	}

	public List<Double> getSimilarities() {
		return similarities;
	}

}
