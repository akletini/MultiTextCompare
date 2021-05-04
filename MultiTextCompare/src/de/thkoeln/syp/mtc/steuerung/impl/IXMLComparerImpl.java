package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class IXMLComparerImpl {

	private int maxLineLength;
	private ITextvergleicher textvergleicher;

	public IXMLComparerImpl(int maxLineLength) {
		textvergleicher = new ITextvergleicherImpl();
		this.maxLineLength = maxLineLength;
	}

	public double compare(File ref, File comp) throws IOException,
			JDOMException {
		Document docRef, docComp;
		SAXBuilder builder = new SAXBuilder();
		Element rootRef, rootComp;
		docRef = builder.build(ref);
		docComp = builder.build(comp);

		rootRef = docRef.getRootElement();
		rootComp = docComp.getRootElement();

		double similarity = traverseGraph(rootRef, rootComp);
		return similarity;
	}

	private double traverseGraph(Element rootRef, Element rootComp) {
		double similarity = 0.0;
		double currentLevelWeight = calcLevelWeight(rootRef, rootComp);
		List<Element> refFirstLevelChildren = rootRef.getChildren();
		List<Element> compFirstLevelChildren = rootComp.getChildren();
		for (Element currentRef : refFirstLevelChildren) {
			String currentRefName = currentRef.getName();
			Element currentComp = rootComp.getChild(currentRefName);
			if (currentComp != null) {
				// Liste von Knoten
				if (hasChildren(currentRef) && hasChildren(currentComp)) {
					similarity += compareElementsRecursively(currentRef, currentComp,
							currentLevelWeight);
				}
				// einzelnes Feld
				else if (!hasChildren(currentRef) && !hasChildren(currentComp)) {
					similarity += compareElements(currentRef, currentComp,
							currentLevelWeight);
				}
			}
		}
		return similarity;
	}

	private double compareElements(Element ref, Element comp,
			double currentLevelWeight) {
		final double contentWeight = 0.5;
		final double attributeWeight = 0.5;
		double totalSimilarity = 0, contentSimilarity, attributeSimilarity;
		double stringSimilarity = compareStrings(ref.getValue(), comp.getValue());
		contentSimilarity = currentLevelWeight * contentWeight
				* stringSimilarity;
		attributeSimilarity = currentLevelWeight
				* attributeWeight
				* calcAttributeSimilarity(ref.getAttributes(),
						comp.getAttributes());
		totalSimilarity = contentSimilarity + attributeSimilarity;
		return totalSimilarity;
	}

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

	private double compareElementsRecursively(Element ref, Element comp,
			double currentLevelWeight) {
		List<Double> similarities = new ArrayList<Double>();
		List<Element> matchingRef = new ArrayList<Element>();
		List<Element> matchingComp = new ArrayList<Element>();

		double currentWeight = calcLevelWeight(ref, comp);

		for (int i = 0; i < ref.getChildren().size(); i++) {
			Element currentRef = ref.getChildren().get(i);
			for (int j = 0; j < comp.getChildren().size(); j++) {
				Element currentComp = comp.getChildren().get(j);
				if (currentRef.getName().equals(currentComp.getName())) {
					matchingRef.add(currentRef);
					matchingComp.add(currentComp);
				}
			}
		}

		for (int i = 0; i < matchingRef.size(); i++) {
			Element currentRef = matchingRef.get(i);
			Element currentComp = matchingComp.get(i);
			if (hasChildren(currentRef) && hasChildren(currentComp)) {
				double similarity = currentLevelWeight * compareElementsRecursively(currentRef, currentComp,
						currentWeight);
				similarities.add(similarity);
			} else if (hasChildren(currentRef) && !hasChildren(currentComp)) {

			} else if (hasChildren(currentRef) && !hasChildren(currentComp)) {

			} else if (!hasChildren(currentRef) && !hasChildren(currentComp)) {
				double similarity = currentLevelWeight * compareElements(currentRef, currentComp, currentWeight);
				similarities.add(similarity);
			}
		}
		double sim = 0.0;
		for (Double s : similarities) {
			sim += s;
		}
		return sim;
	}

	private double compareStrings(String ref, String comp) {
		double similarity = 1.0;
		double maxLength = Math.max(ref.length(), comp.length());
		if(maxLength > 0.0){
		similarity = 1 - (textvergleicher.calculateLevenshteinDist(ref,
				comp, maxLineLength) / maxLength);
		}
		return similarity;
	}

	private double calcLevelWeight(Element ref, Element comp) {
		double levelWeight = 0.0;
		int nodeCountLeft = ref.getChildren().size();
		int nodeCountRight = comp.getChildren().size();
		int maxNodeCount = Math.max(nodeCountLeft, nodeCountRight);
		levelWeight = 1 / (double) maxNodeCount;
		return levelWeight;
	}

	private boolean hasChildren(Element e) {
		if (e.getChildren().size() == 0) {
			return false;
		}
		return true;
	}
}
